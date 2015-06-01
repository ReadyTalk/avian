/* Copyright (c) 2008-2015, Avian Contributors

   Permission to use, copy, modify, and/or distribute this software
   for any purpose with or without fee is hereby granted, provided
   that the above copyright notice and this permission notice appear
   in all copies.

   There is NO WARRANTY for this software.  See license.txt for
   details. */

package java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ArrayList<T> extends AbstractList<T> implements java.io.Serializable, RandomAccess {
  private static final int MinimumCapacity = 16;

  private Object[] array;
  private int size;

  public ArrayList(int capacity) {
    resize(capacity);
  }

  public ArrayList() {
    this(0);
  }

  public ArrayList(Collection<? extends T> source) {
    this(source.size());
    addAll(source);
  }

  private void grow(int newSize) {
    if (array == null || newSize > array.length) {
      resize(Math.max(newSize, array == null
                      ? MinimumCapacity : array.length * 2));
    }
  }

  private void shrink(int newSize) {
    if (array.length / 2 >= MinimumCapacity && newSize <= array.length / 3) {
      resize(array.length / 2);
    }
  }

  private void resize(int capacity) {
    Object[] newArray = null;
    if (capacity != 0) {
      if (array != null && array.length == capacity) {
        return;
      }

      newArray = new Object[capacity];
      if (array != null) {
        System.arraycopy(array, 0, newArray, 0, size);
      }
    }
    array = newArray;
  }

  private static boolean equal(Object a, Object b) {
    return (a == null && b == null) || (a != null && a.equals(b));
  }

  public int size() {
    return size;
  }

  public void ensureCapacity(int capacity) {
    grow(capacity);
  }

  public boolean contains(Object element) {
    for (int i = 0; i < size; ++i) {
      if (equal(element, array[i])) {
        return true;
      }
    }
    return false;
  }

  public void add(int index, T element) {
    int newSize = Math.max(size+1, index+1);
    grow(newSize);
    size = newSize;
    System.arraycopy(array, index, array, index+1, size-index-1);
    array[index] = element;
  }

  public boolean add(T element) {
    add(size, element);
    return true;
  }

  public boolean addAll(Collection<? extends T> collection) {
    for (T t: collection) add(t);
    return true;
  }

  public int indexOf(Object element) {
    for (int i = 0; i < size; ++i) {
      if (equal(element, array[i])) {
        return i;
      }
    }
    return -1;
  }

  public int lastIndexOf(Object element) {
    for (int i = size - 1; i >= 0; --i) {
      if (equal(element, array[i])) {
        return i;
      }
    }
    return -1;
  }

  public T get(int index) {
    if (index >= 0 && index < size) {
      return (T) array[index];
    } else {
      throw new IndexOutOfBoundsException(index + " not in [0, " + size + ")");
    }    
  }

  public T set(int index, T element) {
    if (index >= 0 && index < size) {
      Object oldValue = array[index];
      array[index] = element;
      return (T) oldValue;
    } else {
      throw new IndexOutOfBoundsException(index + " not in [0, " + size + ")");
    }
  }

  public T remove(int index) {
    T v = get(index);

    int newSize = size - 1;

    if (index == newSize) {
      array[index] = null;
    } else {
      System.arraycopy(array, index + 1, array, index, newSize - index);
    }

    shrink(newSize);
    size = newSize;

    return v;
  }

  public boolean remove(Object element) {
    for (int i = 0; i < size; ++i) {
      if (equal(element, array[i])) {
        remove(i);
        return true;
      }
    }
    return false;
  }

  public boolean isEmpty() {
    return size() == 0;
  }

  public void clear() {
    array = null;
    size = 0;
  }

  public Iterator<T> iterator() {
    return listIterator();
  }

  public ListIterator<T> listIterator(int index) {
    return new Collections.ArrayListIterator(this, index);
  }

  public ListIterator<T> listIterator() {
    return listIterator(0);
  }

  public String toString() {
    return avian.Data.toString(this);
  }

  private void writeObject(ObjectOutputStream out) throws IOException {
    out.defaultWriteObject();
    out.writeInt(array.length);
    for (T o : this) {
      out.writeObject(o);
    }
  }

  private void readObject(ObjectInputStream in)
      throws ClassNotFoundException, IOException
  {
    in.defaultReadObject();
    int capacity = in.readInt();
    grow(capacity);
    for (int i = 0; i < size; i++) {
      array[i] = in.readObject();
    }
  }
}
