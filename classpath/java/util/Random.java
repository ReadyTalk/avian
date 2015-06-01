/* Copyright (c) 2008-2015, Avian Contributors

   Permission to use, copy, modify, and/or distribute this software
   for any purpose with or without fee is hereby granted, provided
   that the above copyright notice and this permission notice appear
   in all copies.

   There is NO WARRANTY for this software.  See license.txt for
   details. */

package java.util;

public class Random {
  private static final long Mask = 0x5DEECE66DL;

  private static final long InitialSeed = 123456789987654321L;

  private static long nextSeed = InitialSeed;

  private long seed;

  public Random(long seed) {
    setSeed(seed);
  }

  public Random() {
    synchronized (Random.class) {
      setSeed(nextSeed ^ System.currentTimeMillis());
      nextSeed *= 123456789987654321L;
      if (nextSeed == 0) {
        nextSeed = InitialSeed;
      }
    }
  }

  public void setSeed(long seed) {
    this.seed = (seed ^ Mask) & ((1L << 48) - 1);
  }

  protected int next(int bits) {
    seed = ((seed * Mask) + 0xBL) & ((1L << 48) - 1);
    return (int) (seed >>> (48 - bits));
  }

  public int nextInt(int limit) {
    if (limit <= 0) {
      throw new IllegalArgumentException();
    }
    
    if ((limit & -limit) == limit) {
      // limit is a power of two
      return (int) ((limit * (long) next(31)) >> 31);
    }
    
    int bits;
    int value;
    do {
      bits = next(31);
      value = bits % limit;
    } while (bits - value + (limit - 1) < 0);

    return value;
  }

  public int nextInt() {
    return next(32);
  }

  public void nextBytes(byte[] bytes) {
    final int length = bytes.length;
    for (int i = 0; i < length;) {
      int r = nextInt();
      for (int j = Math.min(length - i, 4); j > 0; --j) {
        bytes[i++] = (byte) r;
        r >>= 8;
      }
    }
  }

  public long nextLong() {
    return ((long) next(32) << 32) + next(32);
  }

  public double nextDouble() {
    return (((long) next(26) << 27) + next(27)) / (double) (1L << 53);
  }
}
