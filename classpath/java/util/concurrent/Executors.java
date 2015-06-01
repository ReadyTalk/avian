/* Copyright (c) 2008-2015, Avian Contributors

   Permission to use, copy, modify, and/or distribute this software
   for any purpose with or without fee is hereby granted, provided
   that the above copyright notice and this permission notice appear
   in all copies.

   There is NO WARRANTY for this software.  See license.txt for
   details. */

package java.util.concurrent;

public class Executors {
  public static <T> Callable<T> callable(final Runnable task, final T result) {
    return new Callable<T>() {
      @Override
      public T call() throws Exception {
        task.run();
        
        return result;
      }
    };
  }
}
