/* Copyright (c) 2008-2014, Avian Contributors

   Permission to use, copy, modify, and/or distribute this software
   for any purpose with or without fee is hereby granted, provided
   that the above copyright notice and this permission notice appear
   in all copies.

   There is NO WARRANTY for this software.  See license.txt for
   details. */

package java.lang.reflect;

public interface Member {
  public static final int PUBLIC = 0;
  public static final int DECLARED = 1;

  public Class getDeclaringClass();

  public int getModifiers();

  public String getName();

  public boolean isSynthetic();
}
