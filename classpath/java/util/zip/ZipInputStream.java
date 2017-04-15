package java.util.zip;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

public class ZipInputStream extends InputStream {
  private InputStream in, current;
  private File temp;
  private ZipFile zip;
  private Enumeration<? extends ZipEntry> e;

  public ZipInputStream(InputStream in) {
    this.in = in;
  }

  public ZipEntry getNextEntry() throws IOException {
    if (e == null) {
      if (in == null) {
        throw new IOException("No input stream");
      }
      temp = File.createTempFile("input-stream.", ".zip");
      FileOutputStream out = new FileOutputStream(temp);
      byte[] buffer = new byte[16384];
      for (;;) {
        int count = in.read(buffer);
        if (count < 0) {
          break;
        }
        out.write(buffer, 0, count);
      }
      in.close();
      in = null;
      out.close();
      zip = new ZipFile(temp);
      e = zip.entries();
    }
    if (!e.hasMoreElements()) {
      return null;
    }
    ZipEntry result = e.nextElement();
    current = zip.getInputStream(result);
    return result;
  }

  public void closeEntry() throws IOException {
    if (current != null) {
      current.close();
      current = null;
    }
  }

  public int available() throws IOException {
    return current == null ? 0 : 1;
  }

  public int read() throws IOException {
    if (current == null) {
      return -1;
    }
    return current.read();
  }

  public int read(byte[] buffer, int offset, int length) throws IOException {
    if (current == null) {
      return -1;
    }
    int count = current.read(buffer, offset, length);
    if (count < 0) {
      closeEntry();
    }
    return count;
  }

  public long skip(long count) throws IOException {
    if (current == null) {
      throw new IOException("No current entry");
    }
    return current.skip(count);
  }

  public void close() throws IOException {
    closeEntry();
    if (in != null) {
      in.close();
      in = null;
    }
    if (zip != null) {
      zip.close();
      zip = null;
    }
    if (temp != null) {
      temp.delete();
      temp = null;
    }
  }
}
