/* Copyright (c) 2008-2010, Avian Contributors

   Permission to use, copy, modify, and/or distribute this software
   for any purpose with or without fee is hereby granted, provided
   that the above copyright notice and this permission notice appear
   in all copies.

   There is NO WARRANTY for this software.  See license.txt for
   details. */

package java.util;

public class Arrays {
  private Arrays() { }

  private static boolean equal(Object a, Object b) {
    return (a == null && b == null) || (a != null && a.equals(b));
  }

  public static void sort(Object[] a) {
    ComparableTimSort.sort(a);
  }

  public static void sort(Object[] a, int fromIndex, int toIndex) {
    ComparableTimSort.sort(a, fromIndex, toIndex);
  }
  
  public static <T> void sort(T[] array, Comparator<? super T> comparator) {
      TimSort.sort(array, comparator);
  }

  public static <T> void sort(T[] array, int fromIndex, int toIndex, Comparator<? super T> comparator) {
    TimSort.sort(array, fromIndex, toIndex, comparator);
  }

  public static int hashCode(Object[] array) {
    if(array == null) {
      return 9023;
    }

    int hc = 823347;
    for(Object o : array) {
      hc += o != null ? o.hashCode() : 54267;
      hc *= 3;
    }
    return hc;
  }

  public static boolean equals(Object[] a, Object[] b) {
    if(a == b) {
      return true;
    }
    if(a == null || b == null) {
      return false;
    }
    if(a.length != b.length) {
      return false;
    }
    for(int i = 0; i < a.length; i++) {
      if(!equal(a[i], b[i])) {
        return false;
      }
    }
    return true;
  }

  public static <T> List<T> asList(final T ... array) {
    return new List<T>() {
      public String toString() {
        return Collections.toString(this);
      }

      public int size() {
        return array.length;
      }

      public boolean add(T element) {
        throw new UnsupportedOperationException();
      }

      public boolean addAll(Collection<? extends T> collection) {
        throw new UnsupportedOperationException();      
      }

      public void add(int index, T element) {
        throw new UnsupportedOperationException();
      }

      public boolean contains(Object element) {
        for (int i = 0; i < array.length; ++i) {
          if (equal(element, array[i])) {
            return true;
          }
        }
        return false;
      }

      public int indexOf(Object element) {
        for (int i = 0; i < array.length; ++i) {
          if (equal(element, array[i])) {
            return i;
          }
        }
        return -1;
      }

      public int lastIndexOf(Object element) {
        for (int i = array.length - 1; i >= 0; --i) {
          if (equal(element, array[i])) {
            return i;
          }
        }
        return -1;
      }

      public T get(int index) {
        return array[index];
      }

      public T set(int index, T value) {
        throw new UnsupportedOperationException();
      }

      public Object[] toArray() {
        return toArray(new Object[size()]);      
      }

      public <S> S[] toArray(S[] a) {
        return (S[])array;
      }

      public boolean isEmpty() {
        return size() == 0;
      }

      public T remove(int index) {
        throw new UnsupportedOperationException();        
      }

      public boolean remove(Object element) {
        throw new UnsupportedOperationException();
      }

      public void clear() {
        throw new UnsupportedOperationException();
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
    };
  }

  public static void fill(int[] array, int value) {
    for (int i=0;i<array.length;i++) {
      array[i] = value;
    }
  }

  public static void fill(int[] array, int start, int end, int value) {
    for (int i = start; i < end; i++) {
      array[i] = value;
    }
  }

  public static void fill(boolean[] array, boolean value) {
    for (int i = 0; i < array.length; i++) {
       array[i] = value;
    }
  }

  public static void fill(boolean[] array, int start, int end, boolean value) {
    for (int i = start; i < end; i++) {
      array[i] = value;
    }
  }

  public static void fill(byte[] array, byte value) {
    for (int i = 0; i < array.length; i++) {
       array[i] = value;
    }
  }

  public static void fill(byte[] array, int start, int end, byte value) {
    for (int i = start; i < end; i++) {
      array[i] = value;
    }
  }

  public static void fill(float[] array, float value) {
    for (int i = 0; i < array.length; i++) {
       array[i] = value;
    }
  }

  public static void fill(float[] array, int start, int end, float value) {
    for (int i = start; i < end; i++) {
      array[i] = value;
    }
  }  

  public static void fill(long[] array, long value) {
    for (int i = 0; i < array.length; i++) {
       array[i] = value;
    }
  }

  public static void fill(long[] array, int start, int end, long value) {
    for (int i = start; i < end; i++) {
      array[i] = value;
    }
  }

  public static void fill(char[] array, char value) {
    for (int i=0;i<array.length;i++) {
      array[i] = value;
    }
  }
  
  public static <T> void fill(T[] array, T value) {
    for (int i=0;i<array.length;i++) {
      array[i] = value;
    }
  }

  public static <T> void fill(T[] array, int start, int end, T value) {
    for (int i = start; i < end; i++) {
      array[i] = value;
    }
  }

  public static void sort(int[] a) {
      DualPivotQuicksort.sort(a);
  }

  public static void sort(int[] a, int fromIndex, int toIndex) {
      DualPivotQuicksort.sort(a, fromIndex, toIndex - 1);
  }

  public static void sort(short[] a) {
      DualPivotQuicksort.sort(a);
  }

  public static void sort(short[] a, int fromIndex, int toIndex) {
      DualPivotQuicksort.sort(a, fromIndex, toIndex - 1);
  }

  public static void sort(byte[] a) {
      DualPivotQuicksort.sort(a);
  }

  public static void sort(byte[] a, int fromIndex, int toIndex) {
      DualPivotQuicksort.sort(a, fromIndex, toIndex - 1);
  }
  
  public static void sort(long[] a) {
      DualPivotQuicksort.sort(a);
  }

  public static void sort(long[] a, int fromIndex, int toIndex) {
      DualPivotQuicksort.sort(a, fromIndex, toIndex - 1);
  }

  public static void sort(float[] a) {
      DualPivotQuicksort.sort(a);
  }

  public static void sort(float[] a, int fromIndex, int toIndex) {
      DualPivotQuicksort.sort(a, fromIndex, toIndex - 1);
  }

  private static void checkBinarySearchBounds(int startIndex, int endIndex, int length) {
        if (startIndex > endIndex) {
            throw new IllegalArgumentException();
        }
        if (startIndex < 0 || endIndex > length) {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

  /**
   * Performs a binary search for {@code value} in the ascending sorted array {@code array}.
   * Searching in an unsorted array has an undefined result. It's also undefined which element
   * is found if there are multiple occurrences of the same element.
   *
   * @param array the sorted array to search.
   * @param value the element to find.
   * @return the non-negative index of the element, or a negative index which
   *         is {@code -index - 1} where the element would be inserted.
   */
  public static int binarySearch(int[] array, int value) {
      return binarySearch(array, 0, array.length, value);
  }

  /**
     * Performs a binary search for {@code value} in the ascending sorted array {@code array},
     * in the range specified by fromIndex (inclusive) and toIndex (exclusive).
     * Searching in an unsorted array has an undefined result. It's also undefined which element
     * is found if there are multiple occurrences of the same element.
     *
     * @param array the sorted array to search.
     * @param startIndex the inclusive start index.
     * @param endIndex the exclusive start index.
     * @param value the element to find.
     * @return the non-negative index of the element, or a negative index which
     *         is {@code -index - 1} where the element would be inserted.
     * @throws IllegalArgumentException if {@code startIndex > endIndex}
     * @throws ArrayIndexOutOfBoundsException if {@code startIndex < 0 || endIndex > array.length}
     * @since 1.6
     */
    public static int binarySearch(int[] array, int startIndex, int endIndex, int value) {
        checkBinarySearchBounds(startIndex, endIndex, array.length);
        int lo = startIndex;
        int hi = endIndex - 1;

        while (lo <= hi) {
            int mid = (lo + hi) >>> 1;
            int midVal = array[mid];

            if (midVal < value) {
                lo = mid + 1;
            } else if (midVal > value) {
                hi = mid - 1;
            } else {
                return mid;  // value found
            }
        }
        return ~lo;  // value not present
    }

  /**
   * Performs a binary search for {@code value} in the ascending sorted array {@code array}.
   * Searching in an unsorted array has an undefined result. It's also undefined which element
   * is found if there are multiple occurrences of the same element.
   *
   * @param array the sorted array to search.
   * @param value the element to find.
   * @return the non-negative index of the element, or a negative index which
   *         is {@code -index - 1} where the element would be inserted.
   */
  public static int binarySearch(byte[] array, byte value) {
      return binarySearch(array, 0, array.length, value);
  }

  /**
     * Performs a binary search for {@code value} in the ascending sorted array {@code array},
     * in the range specified by fromIndex (inclusive) and toIndex (exclusive).
     * Searching in an unsorted array has an undefined result. It's also undefined which element
     * is found if there are multiple occurrences of the same element.
     *
     * @param array the sorted array to search.
     * @param startIndex the inclusive start index.
     * @param endIndex the exclusive start index.
     * @param value the element to find.
     * @return the non-negative index of the element, or a negative index which
     *         is {@code -index - 1} where the element would be inserted.
     * @throws IllegalArgumentException if {@code startIndex > endIndex}
     * @throws ArrayIndexOutOfBoundsException if {@code startIndex < 0 || endIndex > array.length}
     * @since 1.6
     */
    public static int binarySearch(byte[] array, int startIndex, int endIndex, byte value) {
        checkBinarySearchBounds(startIndex, endIndex, array.length);
        int lo = startIndex;
        int hi = endIndex - 1;

        while (lo <= hi) {
            int mid = (lo + hi) >>> 1;
            byte midVal = array[mid];

            if (midVal < value) {
                lo = mid + 1;
            } else if (midVal > value) {
                hi = mid - 1;
            } else {
                return mid;  // value found
            }
        }
        return ~lo;  // value not present
    }


  /**
   * Performs a binary search for {@code value} in the ascending sorted array {@code array}.
   * Searching in an unsorted array has an undefined result. It's also undefined which element
   * is found if there are multiple occurrences of the same element.
   *
   * @param array the sorted array to search.
   * @param value the element to find.
   * @return the non-negative index of the element, or a negative index which
   *         is {@code -index - 1} where the element would be inserted.
   */
  public static int binarySearch(long[] array, long value) {
      return binarySearch(array, 0, array.length, value);
  }

  /**
     * Performs a binary search for {@code value} in the ascending sorted array {@code array},
     * in the range specified by fromIndex (inclusive) and toIndex (exclusive).
     * Searching in an unsorted array has an undefined result. It's also undefined which element
     * is found if there are multiple occurrences of the same element.
     *
     * @param array the sorted array to search.
     * @param startIndex the inclusive start index.
     * @param endIndex the exclusive start index.
     * @param value the element to find.
     * @return the non-negative index of the element, or a negative index which
     *         is {@code -index - 1} where the element would be inserted.
     * @throws IllegalArgumentException if {@code startIndex > endIndex}
     * @throws ArrayIndexOutOfBoundsException if {@code startIndex < 0 || endIndex > array.length}
     * @since 1.6
     */
    public static int binarySearch(long[] array, int startIndex, int endIndex, long value) {
        checkBinarySearchBounds(startIndex, endIndex, array.length);
        int lo = startIndex;
        int hi = endIndex - 1;

        while (lo <= hi) {
            int mid = (lo + hi) >>> 1;
            long midVal = array[mid];

            if (midVal < value) {
                lo = mid + 1;
            } else if (midVal > value) {
                hi = mid - 1;
            } else {
                return mid;  // value found
            }
        }
        return ~lo;  // value not present
    }

    /**
   * Performs a binary search for {@code value} in the ascending sorted array {@code array}.
   * Searching in an unsorted array has an undefined result. It's also undefined which element
   * is found if there are multiple occurrences of the same element.
   *
   * @param array the sorted array to search.
   * @param value the element to find.
   * @return the non-negative index of the element, or a negative index which
   *         is {@code -index - 1} where the element would be inserted.
   */
  public static int binarySearch(float[] array, float value) {
      return binarySearch(array, 0, array.length, value);
  }

  /**
     * Performs a binary search for {@code value} in the ascending sorted array {@code array},
     * in the range specified by fromIndex (inclusive) and toIndex (exclusive).
     * Searching in an unsorted array has an undefined result. It's also undefined which element
     * is found if there are multiple occurrences of the same element.
     *
     * @param array the sorted array to search.
     * @param startIndex the inclusive start index.
     * @param endIndex the exclusive start index.
     * @param value the element to find.
     * @return the non-negative index of the element, or a negative index which
     *         is {@code -index - 1} where the element would be inserted.
     * @throws IllegalArgumentException if {@code startIndex > endIndex}
     * @throws ArrayIndexOutOfBoundsException if {@code startIndex < 0 || endIndex > array.length}
     * @since 1.6
     */
    public static int binarySearch(float[] array, int startIndex, int endIndex, float value) {
        checkBinarySearchBounds(startIndex, endIndex, array.length);
        int lo = startIndex;
        int hi = endIndex - 1;

        while (lo <= hi) {
            int mid = (lo + hi) >>> 1;
            float midVal = array[mid];

            if (midVal < value) {
                lo = mid + 1;
            } else if (midVal > value) {
                hi = mid - 1;
            } else {
                int midValBits = Float.floatToIntBits(midVal);
                int valueBits  = Float.floatToIntBits(value);
                if (midValBits < valueBits) {
                    lo = mid + 1; // (-0.0, 0.0) or (not NaN, NaN); midVal < val
                } else if (midValBits > valueBits) {
                    hi = mid - 1; // (0.0, -0.0) or (NaN, not NaN); midVal > val
                } else {
                    return mid; // bit patterns are equal; value found
                }
            }
        }
        return ~lo;  // value not present
    }


    /**
     * Creates a {@code String} representation of the {@code boolean[]} passed.
     * The result is surrounded by brackets ({@code "[]"}), each
     * element is converted to a {@code String} via the
     * {@link String#valueOf(boolean)} and separated by {@code ", "}.
     * If the array is {@code null}, then {@code "null"} is returned.
     *
     * @param array
     *            the {@code boolean} array to convert.
     * @return the {@code String} representation of {@code array}.
     * @since 1.5
     */
    public static String toString(boolean[] array) {
        if (array == null) {
            return "null";
        }
        if (array.length == 0) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder(array.length * 7);
        sb.append('[');
        sb.append(array[0]);
        for (int i = 1; i < array.length; i++) {
            sb.append(", ");
            sb.append(array[i]);
        }
        sb.append(']');
        return sb.toString();
    }

    /**
     * Creates a {@code String} representation of the {@code byte[]} passed. The
     * result is surrounded by brackets ({@code "[]"}), each element
     * is converted to a {@code String} via the {@link String#valueOf(int)} and
     * separated by {@code ", "}. If the array is {@code null}, then
     * {@code "null"} is returned.
     *
     * @param array
     *            the {@code byte} array to convert.
     * @return the {@code String} representation of {@code array}.
     * @since 1.5
     */
    public static String toString(byte[] array) {
        if (array == null) {
            return "null";
        }
        if (array.length == 0) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder(array.length * 6);
        sb.append('[');
        sb.append(array[0]);
        for (int i = 1; i < array.length; i++) {
            sb.append(", ");
            sb.append(array[i]);
        }
        sb.append(']');
        return sb.toString();
    }

    /**
     * Creates a {@code String} representation of the {@code char[]} passed. The
     * result is surrounded by brackets ({@code "[]"}), each element
     * is converted to a {@code String} via the {@link String#valueOf(char)} and
     * separated by {@code ", "}. If the array is {@code null}, then
     * {@code "null"} is returned.
     *
     * @param array
     *            the {@code char} array to convert.
     * @return the {@code String} representation of {@code array}.
     * @since 1.5
     */
    public static String toString(char[] array) {
        if (array == null) {
            return "null";
        }
        if (array.length == 0) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder(array.length * 3);
        sb.append('[');
        sb.append(array[0]);
        for (int i = 1; i < array.length; i++) {
            sb.append(", ");
            sb.append(array[i]);
        }
        sb.append(']');
        return sb.toString();
    }

    /**
     * Creates a {@code String} representation of the {@code double[]} passed.
     * The result is surrounded by brackets ({@code "[]"}), each
     * element is converted to a {@code String} via the
     * {@link String#valueOf(double)} and separated by {@code ", "}.
     * If the array is {@code null}, then {@code "null"} is returned.
     *
     * @param array
     *            the {@code double} array to convert.
     * @return the {@code String} representation of {@code array}.
     * @since 1.5
     */
    public static String toString(double[] array) {
        if (array == null) {
            return "null";
        }
        if (array.length == 0) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder(array.length * 7);
        sb.append('[');
        sb.append(array[0]);
        for (int i = 1; i < array.length; i++) {
            sb.append(", ");
            sb.append(array[i]);
        }
        sb.append(']');
        return sb.toString();
    }

    /**
     * Creates a {@code String} representation of the {@code float[]} passed.
     * The result is surrounded by brackets ({@code "[]"}), each
     * element is converted to a {@code String} via the
     * {@link String#valueOf(float)} and separated by {@code ", "}.
     * If the array is {@code null}, then {@code "null"} is returned.
     *
     * @param array
     *            the {@code float} array to convert.
     * @return the {@code String} representation of {@code array}.
     * @since 1.5
     */
    public static String toString(float[] array) {
        if (array == null) {
            return "null";
        }
        if (array.length == 0) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder(array.length * 7);
        sb.append('[');
        sb.append(array[0]);
        for (int i = 1; i < array.length; i++) {
            sb.append(", ");
            sb.append(array[i]);
        }
        sb.append(']');
        return sb.toString();
    }

    /**
     * Creates a {@code String} representation of the {@code int[]} passed. The
     * result is surrounded by brackets ({@code "[]"}), each element
     * is converted to a {@code String} via the {@link String#valueOf(int)} and
     * separated by {@code ", "}. If the array is {@code null}, then
     * {@code "null"} is returned.
     *
     * @param array
     *            the {@code int} array to convert.
     * @return the {@code String} representation of {@code array}.
     * @since 1.5
     */
    public static String toString(int[] array) {
        if (array == null) {
            return "null";
        }
        if (array.length == 0) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder(array.length * 6);
        sb.append('[');
        sb.append(array[0]);
        for (int i = 1; i < array.length; i++) {
            sb.append(", ");
            sb.append(array[i]);
        }
        sb.append(']');
        return sb.toString();
    }

    /**
     * Creates a {@code String} representation of the {@code long[]} passed. The
     * result is surrounded by brackets ({@code "[]"}), each element
     * is converted to a {@code String} via the {@link String#valueOf(long)} and
     * separated by {@code ", "}. If the array is {@code null}, then
     * {@code "null"} is returned.
     *
     * @param array
     *            the {@code long} array to convert.
     * @return the {@code String} representation of {@code array}.
     * @since 1.5
     */
    public static String toString(long[] array) {
        if (array == null) {
            return "null";
        }
        if (array.length == 0) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder(array.length * 6);
        sb.append('[');
        sb.append(array[0]);
        for (int i = 1; i < array.length; i++) {
            sb.append(", ");
            sb.append(array[i]);
        }
        sb.append(']');
        return sb.toString();
    }

    /**
     * Creates a {@code String} representation of the {@code short[]} passed.
     * The result is surrounded by brackets ({@code "[]"}), each
     * element is converted to a {@code String} via the
     * {@link String#valueOf(int)} and separated by {@code ", "}. If
     * the array is {@code null}, then {@code "null"} is returned.
     *
     * @param array
     *            the {@code short} array to convert.
     * @return the {@code String} representation of {@code array}.
     * @since 1.5
     */
    public static String toString(short[] array) {
        if (array == null) {
            return "null";
        }
        if (array.length == 0) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder(array.length * 6);
        sb.append('[');
        sb.append(array[0]);
        for (int i = 1; i < array.length; i++) {
            sb.append(", ");
            sb.append(array[i]);
        }
        sb.append(']');
        return sb.toString();
    }

    /**
     * Creates a {@code String} representation of the {@code Object[]} passed.
     * The result is surrounded by brackets ({@code "[]"}), each
     * element is converted to a {@code String} via the
     * {@link String#valueOf(Object)} and separated by {@code ", "}.
     * If the array is {@code null}, then {@code "null"} is returned.
     *
     * @param array
     *            the {@code Object} array to convert.
     * @return the {@code String} representation of {@code array}.
     * @since 1.5
     */
    public static String toString(Object[] array) {
        if (array == null) {
            return "null";
        }
        if (array.length == 0) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder(array.length * 7);
        sb.append('[');
        sb.append(array[0]);
        for (int i = 1; i < array.length; i++) {
            sb.append(", ");
            sb.append(array[i]);
        }
        sb.append(']');
        return sb.toString();
    }

}
