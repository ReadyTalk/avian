/* Copyright (c) 2008-2009, Avian Contributors

   Permission to use, copy, modify, and/or distribute this software
   for any purpose with or without fee is hereby granted, provided
   that the above copyright notice and this permission notice appear
   in all copies.

   There is NO WARRANTY for this software.  See license.txt for
   details. */

package java.lang;

public class ArrayIndexOutOfBoundsException extends IndexOutOfBoundsException {
  public ArrayIndexOutOfBoundsException(String message, Throwable cause) {
    super(message, cause);
  }

  public ArrayIndexOutOfBoundsException(String message) {
    this(message, null);
  }

  public ArrayIndexOutOfBoundsException(int index) {
    this("index=" + index);
  }

  public ArrayIndexOutOfBoundsException(Throwable cause) {
    this(null, cause);
  }

  public ArrayIndexOutOfBoundsException() {
    this(null, null);
  }
}
