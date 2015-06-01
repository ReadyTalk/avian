/* Copyright (c) 2008-2015, Avian Contributors

   Permission to use, copy, modify, and/or distribute this software
   for any purpose with or without fee is hereby granted, provided
   that the above copyright notice and this permission notice appear
   in all copies.

   There is NO WARRANTY for this software.  See license.txt for
   details. */

package java.lang.ref;

public class PhantomReference<T> extends Reference<T> {
  public PhantomReference(T target, ReferenceQueue<? super T> queue) {
    super(target, queue);    
  }

  public PhantomReference(T target) {
    this(target, null);
  }

  public T get() {
    return null;
  }
}
