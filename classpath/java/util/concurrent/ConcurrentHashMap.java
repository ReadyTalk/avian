/* Copyright (c) 2008-2014, Avian Contributors

   Permission to use, copy, modify, and/or distribute this software
   for any purpose with or without fee is hereby granted, provided
   that the above copyright notice and this permission notice appear
   in all copies.

   There is NO WARRANTY for this software.  See license.txt for
   details. */

package java.util.concurrent;

import static avian.Data.nextPowerOfTwo;

import avian.Data;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class ConcurrentHashMap<K,V>
  extends AbstractMap<K,V>
  implements ConcurrentMap<K,V>
{
  private static final int MinimumLength = 16;

  private final AtomicReference<Content> content;

  public ConcurrentHashMap() {
    this(MinimumLength);
  }

  public ConcurrentHashMap(int initialCapacity) {
    content = new AtomicReference
      (new Content
       (new AtomicReferenceArray
        (Math.max(MinimumLength, nextPowerOfTwo(initialCapacity))), 0));
  }

  public boolean isEmpty() {
    return size() == 0;
  }

  public int size() {
    // Note that Content.size might briefly go negative if e.g. one
    // thread adds an entry and another removes it before the first
    // thread is able to update the size, so we clamp negative values
    // to zero:
    return Math.max(content.get().size, 0);
  }

  public boolean containsKey(Object key) {
    return find(key) != null;
  }

  public boolean containsValue(Object value) {
    for (V v: values()) {
      if (value.equals(v)) {
        return true;
      }
    }
    return false;
  }

  public V get(Object key) {
    Cell<K,V> cell = find(key);
    return cell == null ? null : cell.value;
  }

  private Cell<K,V> find(Object key) {
    AtomicReferenceArray<Cell<K,V>> array = content.get().array;
    for (Cell<K,V> cell = array.get(key.hashCode() & (array.length() - 1));
         cell != null;
         cell = cell.next)
    {
      if (key.equals(cell.key)) {
        return cell;
      }
    }
    return null;
  }

  public V putIfAbsent(K key, V value) {
    Cell<K,V> cell = put(key, value, PutCondition.IfAbsent, null);
    return cell == null ? null : cell.value;
  }

  public boolean remove(K key, V value) {
    Cell<K,V> cell = remove(key, RemoveCondition.IfEqual, value);
    return cell != null && cell.value.equals(value);
  }

  public V replace(K key, V value) {
    Cell<K,V> cell = put(key, value, PutCondition.IfPresent, null);
    return cell == null ? null : cell.value;
  }

  public boolean replace(K key, V oldValue, V newValue) {
    Cell<K,V> cell = put(key, newValue, PutCondition.IfEqual, oldValue);
    return cell != null && cell.value.equals(oldValue);    
  }

  public V put(K key, V value) {
    Cell<K,V> cell = put(key, value, PutCondition.Always, null);
    return cell == null ? null : cell.value;
  }

  public V remove(Object key) {
    Cell<K,V> cell = remove(key, RemoveCondition.Always, null);
    return cell == null ? null : cell.value;
  }

  private enum PutCondition {
    Always() {
      public boolean addIfAbsent() { return true; }
      public <V> boolean addIfPresent(V a, V b) { return true; }
    }, IfAbsent() {
      public boolean addIfAbsent() { return true; }
      public <V> boolean addIfPresent(V a, V b) { return false; }      
    }, IfPresent() {
      public boolean addIfAbsent() { return false; }
      public <V> boolean addIfPresent(V a, V b) { return true; }
    }, IfEqual() {
      public boolean addIfAbsent() { return false; }
      public <V> boolean addIfPresent(V a, V b) { return a.equals(b); }
    };

    public boolean addIfAbsent() { throw new AssertionError(); }
    public <V> boolean addIfPresent(V a, V b) { throw new AssertionError(); }
  }

  private enum RemoveCondition {
    Always() {
      public <V> boolean remove(V a, V b) { return true; }
    }, IfEqual() {
      public <V> boolean remove(V a, V b) { return a.equals(b); }
    };

    public <V> boolean remove(V a, V b) { throw new AssertionError(); }
  }

  private Cell<K,V> put(K key, V value, PutCondition condition, V oldValue) {
    int hash = key.hashCode();
    boolean arrayUpdated = false;
    Cell<K,V> oldCell = null;

    loop: while (true) {
      Content originalContent = content.get();
      AtomicReferenceArray<Cell<K,V>> array = originalContent.array;
      int index = hash & (array.length() - 1);
      Cell<K,V> original = array.get(index);

      for (Cell<K,V> cell = original; cell != null; cell = cell.next) {
        if (key.equals(cell.key)) {
          if (arrayUpdated && value == cell.value) {
            // This means we've already looped at least once, with the
            // result that we were able to update the array and make
            // it stick, but the content.compareAndSet failed.  Since
            // our update survived intact, we have nothing left to do
            // except increment the size if it's a new entry.
            if (oldCell == null) {
              if (content.compareAndSet
                  (originalContent, new Content
                   (array, originalContent.size + 1)))
              {
                maybeGrow();
                return null;
              } else {
                continue loop;
              }
            } else {
              return oldCell;
            }
          }

          if (! condition.addIfPresent(cell.value, oldValue)) {
            return cell;
          }

          Cell<K,V> start = null;
          Cell<K,V> last = null;
          for (Cell<K,V> cell2 = original; true; cell2 = cell2.next) {
            Cell<K,V> c;
            c = cell2.clone();

            if (last == null) {
              last = start = c;
            } else {
              last.next = c;
              last = c;
            }

            if (cell2 == cell) {
              c.value = value;
              break;
            }
          }

          if (array.compareAndSet(index, original, start)) {
            arrayUpdated = true;
            oldCell = cell;

            if (content.compareAndSet
                (originalContent, new Content(array, originalContent.size)))
            {
              return cell;
            } else {
              continue loop;
            }
          } else {
            continue loop;
          }
        }
      }

      // no mapping found -- add a new one if appropriate
      if (! condition.addIfAbsent()) {
        return null;
      }

      if (array.compareAndSet
          (index, original, new Cell<K,V>(key, value, original)))
      {
        arrayUpdated = true;

        if (content.compareAndSet
            (originalContent, new Content(array, originalContent.size + 1)))
        {
          maybeGrow();
          return null;
        }
      }
    }
  }

  public void putAll(Map<? extends K, ? extends V> map) {
    for (Map.Entry<? extends K, ? extends V> e: map.entrySet()) {
      put(e.getKey(), e.getValue());
    }
  }

  private Cell<K,V> remove(Object key, RemoveCondition condition,
                           V oldValue)
  {
    int hash = key.hashCode();
    Cell<K,V> oldCell = null;

    loop: while (true) {
      Content<K,V> originalContent = content.get();
      AtomicReferenceArray<Cell<K,V>> array = originalContent.array;
      int index = hash & (array.length() - 1);
      Cell<K,V> original = array.get(index);

      for (Cell<K,V> cell = original; cell != null; cell = cell.next) {
        if (key.equals(cell.key)) {
          if (! condition.remove(cell.value, oldValue)) {
            return cell;
          }

          Cell<K,V> start = null;
          Cell<K,V> last = null;
          for (Cell<K,V> cell2 = original;
               cell2 != cell;
               cell2 = cell2.next)
          {
            Cell<K,V> c = cell2.clone();
            if (last == null) {
              last = start = c;
            } else {
              last.next = c;
              last = c;
            }
          }

          if (last == null) {
            start = last = cell.next;
          } else {
            last.next = cell.next;
          }

          if (array.compareAndSet(index, original, start)) {
            oldCell = cell;

            if (content.compareAndSet
                (originalContent, new Content
                 (array, originalContent.size - 1)))
            {
              maybeShrink();
              return cell;
            } else {
              continue loop;
            }
          } else {
            continue loop;
          }
        }
      }

      if (oldCell != null) {
        // This means we've already looped at least once and
        // successfully updated the array, but content.compareAndSet
        // failed, so there's nothing left to do but update the size.
        if (content.compareAndSet
            (originalContent, new Content
             (array, originalContent.size - 1)))
        {
          maybeShrink();
          return oldCell;
        } else {
          continue loop;
        }
      } else {
        return null;
      }
    }
  }

  private void maybeGrow() {
    while (true) {
      Content<K,V> originalContent = content.get();
      if (originalContent.size >= originalContent.array.length() * 2) {
        if (resize(originalContent, originalContent.array.length() * 2)) {
          return;
        }
      } else {
        return;
      }
    }
  }

  private void maybeShrink() {
    while (true) {
      Content<K,V> originalContent = content.get();
      if (originalContent.size <= originalContent.array.length() / 3
          && originalContent.array.length() / 2 > MinimumLength)
      {
        if (resize(originalContent, originalContent.array.length() / 2)) {
          return;
        }
      } else {
        return;
      }
    }
  }

  private boolean resize(Content<K,V> originalContent, int length) {
    AtomicReferenceArray<Cell<K,V>> array = new AtomicReferenceArray(length);
    AtomicReferenceArray<Cell<K,V>> original = originalContent.array;
    int count = 0;
    for (int i = 0; i < original.length(); ++i) {
      for (Cell<K,V> cell = original.get(i); cell != null; cell = cell.next) {
        int index = cell.key.hashCode() & (array.length() - 1);
        array.set(index, new Cell(cell.key, cell.value, array.get(index)));
        ++ count;
      }
    }
    return content.compareAndSet(originalContent, new Content(array, count));
  }

  public void clear() {
    content.set(new Content(new AtomicReferenceArray(MinimumLength), 0));
  }

  public Set<Map.Entry<K, V>> entrySet() {
    return new Data.EntrySet(new MyEntryMap());
  }

  public Set<K> keySet() {
    return new Data.KeySet(new MyEntryMap());
  }

  public Collection<V> values() {
    return new Data.Values(new MyEntryMap());
  }

  private class MyEntryMap implements Data.EntryMap<K, V> {
    public int size() {
      return ConcurrentHashMap.this.size();
    }

    public Map.Entry<K,V> find(Object key) {
      return new MyEntry(ConcurrentHashMap.this.find(key));
    }

    public Map.Entry<K,V> remove(Object key) {
      return new MyEntry
        (ConcurrentHashMap.this.remove(key, RemoveCondition.Always, null));
    }

    public void clear() {
      ConcurrentHashMap.this.clear();
    }

    public Iterator<Map.Entry<K,V>> iterator() {
      return new MyIterator(content.get());
    }
  }

  private static class Content<K,V> {
    private final AtomicReferenceArray<Cell<K,V>> array;
    private final int size;

    public Content(AtomicReferenceArray<Cell<K,V>> array,
                   int size)
    {
      this.array = array;
      this.size = size;
    }
  }

  private static class Cell<K,V> implements Cloneable {
    public final K key;
    public V value;
    public Cell<K,V> next;

    public Cell(K key, V value, Cell<K,V> next) {
      this.key = key;
      this.value = value;
      this.next = next;
    }

    public Cell<K,V> clone() {
      try {
        return (Cell<K,V>) super.clone();
      } catch (CloneNotSupportedException e) {
        throw new AssertionError();
      }
    }
  }

  private class MyEntry implements Map.Entry<K,V> {
    private final K key;
    private V value;
    
    public MyEntry(Cell<K,V> cell) {
      key = cell.key;
      value = cell.value;
    }

    public K getKey() {
      return key;
    }

    public V getValue() {
      return value;
    }

    public V setValue(V value) {
      V v = value;
      this.value = value;
      put(key, value);
      return v;
    }
  }

  private class MyIterator implements Iterator<Map.Entry<K, V>> {
    private final Content<K,V> content;
    private int currentIndex = -1;
    private int nextIndex = -1;
    private Cell<K, V> currentCell;
    private Cell<K, V> nextCell;

    public MyIterator(Content<K,V> content) {
      this.content = content;
      hasNext();
    }

    public Map.Entry<K, V> next() {
      if (hasNext()) {
        currentCell = nextCell;
        currentIndex = nextIndex;

        nextCell = nextCell.next;

        return new MyEntry(currentCell);
      } else {
        throw new NoSuchElementException();
      }
    }

    public boolean hasNext() {
      AtomicReferenceArray<Cell<K,V>> array = content.array;
      while (nextCell == null && ++ nextIndex < array.length()) {
        Cell<K,V> c = array.get(nextIndex);
        if (c != null) {
          nextCell = c;
          return true;
        }
      }
      return nextCell != null;
    }

    public void remove() {
      if (currentCell != null) {
        ConcurrentHashMap.this.remove
          (currentCell.key, RemoveCondition.Always, null);
        currentCell = null;
      } else {
        throw new IllegalStateException();
      }
    }
  }
}
