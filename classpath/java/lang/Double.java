/* Copyright (c) 2008, Avian Contributors

   Permission to use, copy, modify, and/or distribute this software
   for any purpose with or without fee is hereby granted, provided
   that the above copyright notice and this permission notice appear
   in all copies.

   There is NO WARRANTY for this software.  See license.txt for
   details. */

package java.lang;

public final class Double extends Number {
  public static final Class TYPE = Class.forCanonicalName("D");

  public static final double NEGATIVE_INFINITY = -1.0 / 0.0;
  public static final double POSITIVE_INFINITY =  1.0 / 0.0;
  public static final double NaN =  0.0 / 0.0;

  private final double value;

  public Double(String value) {
    this.value = parseDouble(value);
  }

  public Double(double value) {
    this.value = value;
  }

  public static Double valueOf(double value) {
    return new Double(value);
  }

  public static Double valueOf(String s) {
    return new Double(s);
  }

  public boolean equals(Object o) {
    return o instanceof Double && ((Double) o).value == value;
  }

  public int hashCode() {
    long v = doubleToRawLongBits(value);
    return (int) ((v >> 32) ^ (v & 0xFF));
  }

  public String toString() {
    return toString(value);
  }

  public static String toString(double v) {
    byte[] buffer = new byte[20];
    int numChars = fillBufferWithDouble(v, buffer, 20);
    return new String(buffer, 0, numChars, false);
  }

  public byte byteValue() {
    return (byte) value;
  }

  public short shortValue() {
    return (short) value;
  }

  public int intValue() {
    return (int) value;
  }

  public long longValue() {
    return (long) value;
  }

  public float floatValue() {
    return (float) value;
  }

  public double doubleValue() {
    return value;
  }

  public boolean isInfinite() {
    return isInfinite(value);
  }

  public boolean isNaN() {
    return isNaN(value);
  }

  public static double parseDouble(String s) {
    int[] numRead = new int[1];
    double d = doubleFromString(s, numRead);
    if (numRead[0] == 1) {
      return d;
    } else {
      throw new NumberFormatException(s);
    }
  }

  public static int compare(double double1, double double2) {
        // Non-zero, non-NaN checking.
        if (double1 > double2) {
            return 1;
        }
        if (double2 > double1) {
            return -1;
        }
        if (double1 == double2 && 0.0d != double1) {
            return 0;
        }

        // NaNs are equal to other NaNs and larger than any other double
        if (isNaN(double1)) {
            if (isNaN(double2)) {
                return 0;
            }
            return 1;
        } else if (isNaN(double2)) {
            return -1;
        }

        // Deal with +0.0 and -0.0
        long d1 = doubleToRawLongBits(double1);
        long d2 = doubleToRawLongBits(double2);
        // The below expression is equivalent to:
        // (d1 == d2) ? 0 : (d1 < d2) ? -1 : 1
        return (int) ((d1 >> 63) - (d2 >> 63));
    }

  public static native int fillBufferWithDouble(double value, byte[] buffer,
                                                int charCount);

  public static native long doubleToRawLongBits(double value);

  public static native double longBitsToDouble(long bits);

  public static native boolean isInfinite(double value);

  public static native boolean isNaN(double value);

  public static native double doubleFromString(String s, int[] numRead);
}
