/* Copyright (c) 2013, Avian Contributors

   Permission to use, copy, modify, and/or distribute this software
   for any purpose with or without fee is hereby granted, provided
   that the above copyright notice and this permission notice appear
   in all copies.

   There is NO WARRANTY for this software.  See license.txt for
   details. */

package java.util.jar;

public class Attributes {
  public static class Name {
    private final String name;

    private static final int MAX_NAME_LENGTH = 70;

    public Name(String s) {
      int len = s.length();
      if (len == 0 || len > MAX_NAME_LENGTH)
        throw new IllegalArgumentException();

      name = s;
    }
  }
}
