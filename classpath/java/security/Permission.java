/* Copyright (c) 2008-2015, Avian Contributors

   Permission to use, copy, modify, and/or distribute this software
   for any purpose with or without fee is hereby granted, provided
   that the above copyright notice and this permission notice appear
   in all copies.

   There is NO WARRANTY for this software.  See license.txt for
   details. */

package java.security;

public abstract class Permission {
  
  protected String name;
  
  public Permission(String name) {
    this.name = name;
  }
  
  public String getName() {
    return name;
  }
  
  @Override
  public String toString() {
    return this.getClass().getSimpleName() + '['+name+']';
  }
  
  public PermissionCollection newPermissionCollection() {
    return null;
  }
}
