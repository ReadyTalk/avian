/* Copyright (c) 2009-2011, Avian Contributors

   Permission to use, copy, modify, and/or distribute this software
   for any purpose with or without fee is hereby granted, provided
   that the above copyright notice and this permission notice appear
   in all copies.

   There is NO WARRANTY for this software.  See license.txt for
   details. */

package java.util;

public abstract class AbstractList<T> extends AbstractCollection<T>
  implements List<T>
{
  protected int modCount;

  public boolean add(T o) {
    add(size(), o);
    return true;
  }

  public Iterator<T> iterator() {
    return listIterator();
  }

  public ListIterator<T> listIterator() {
    return new Collections.ArrayListIterator(this);
  }

  public ListIterator<T> listIterator(int index) {
    return new Collections.ArrayListIterator(this, index);
  }

  public int lastIndexOf(Object object) {
        ListIterator<?> it = listIterator(size());
        if (object != null) {
            while (it.hasPrevious()) {
                if (object.equals(it.previous())) {
                    return it.nextIndex();
                }
            }
        } else {
            while (it.hasPrevious()) {
                if (it.previous() == null) {
                    return it.nextIndex();
                }
            }
        }
        return -1;
    }

  public int indexOf(Object o) {
    int i = 0;
    for (T v: this) {
      if (o == null) {
        if (v == null) {
          return i;
        }
      } else if (o.equals(v)) {
        return i;
      }

      ++ i;
    }
    return -1;
  }
}
