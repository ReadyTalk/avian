/* Copyright (c) 2011, Avian Contributors

   Permission to use, copy, modify, and/or distribute this software
   for any purpose with or within fee is hereby granted, provided
   that the above copyright notice and this permission notice appear
   in all copies.

   There is NO WARRANTY for this software.  See license.txt for
   details. */

package java.io;

public class FilterInputStream extends InputStream {
  protected volatile InputStream in;

  public FilterInputStream(InputStream in) {
    this.in = in;
  }

  public void close() throws IOException {
    in.close();
  }

  public int read(byte[] buffer) throws IOException {
    return in.read(buffer);
  }

  public int read(byte[] buffer, int offset, int length) throws IOException {
    return in.read(buffer, offset, length);
  }

  public int read() throws IOException {
    return in.read();
  }

  public void reset() throws IOException {
    in.reset();
  }

  public int available() throws IOException {
    return in.available();
  }

}
