/* Copyright (c) 2008-2015, Avian Contributors

   Permission to use, copy, modify, and/or distribute this software
   for any purpose with or without fee is hereby granted, provided
   that the above copyright notice and this permission notice appear
   in all copies.

   There is NO WARRANTY for this software.  See license.txt for
   details. */

package java.lang;

import java.io.Serializable;

public abstract class Number implements Serializable {
  public abstract byte byteValue();
  public abstract short shortValue();
  public abstract int intValue();
  public abstract long longValue();
  public abstract float floatValue();
  public abstract double doubleValue();
}
