/* Copyright (c) 2008-2010, Avian Contributors

   Permission to use, copy, modify, and/or distribute this software
   for any purpose with or without fee is hereby granted, provided
   that the above copyright notice and this permission notice appear
   in all copies.

   There is NO WARRANTY for this software.  See license.txt for
   details. */

package java.util;

public class LinkedHashMap<K, V> extends HashMap<K, V> {
  

  public LinkedHashMap(int capacity) {
    super(capacity);
  }

  public LinkedHashMap() {
    super(0);
  }

  public LinkedHashMap(Map<K, V> map) {
    super(map);
    
  }

}
