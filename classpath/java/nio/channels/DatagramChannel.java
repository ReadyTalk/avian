/* Copyright (c) 2012, Avian Contributors

   Permission to use, copy, modify, and/or distribute this software
   for any purpose with or without fee is hereby granted, provided
   that the above copyright notice and this permission notice appear
   in all copies.

   There is NO WARRANTY for this software.  See license.txt for
   details. */

package java.nio.channels;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.net.ProtocolFamily;
import java.net.Socket;
import java.net.SocketException;
import java.net.DatagramSocket;
import java.net.StandardProtocolFamily;

// TODO: This class is both divergent from the Java standard and incomplete.
public class DatagramChannel extends SelectableChannel
  implements ReadableByteChannel, WritableByteChannel
{
  public static final int InvalidSocket = -1;

  private int socket = InvalidSocket;
  private boolean blocking = true;
  private boolean connected = false;

  public SelectableChannel configureBlocking(boolean v) throws IOException {
    blocking = v;
    configureBlocking();
    return this;
  }

  private void configureBlocking() throws IOException {
    if (socket != InvalidSocket) {
      configureBlocking(socket, blocking);
    }
  }

  int socketFD() {
    return socket;
  }

  void handleReadyOps(int ops) {
    // ignore
  }

  public static DatagramChannel open(ProtocolFamily family)
    throws IOException
  {
    if (family.equals(StandardProtocolFamily.INET)) {
      Socket.init();
    
      return new DatagramChannel();
    } else {
      throw new UnsupportedOperationException();
    }
  }

  public static DatagramChannel open()
    throws IOException
  {
    return open(StandardProtocolFamily.INET);
  }

  public DatagramSocket socket() {
    return new Handle();
  }

  public DatagramChannel bind(SocketAddress address) throws IOException {
    InetSocketAddress inetAddress;
    try {
      inetAddress = (InetSocketAddress) address;
    } catch (ClassCastException e) {
      throw new UnsupportedAddressTypeException();
    }

    socket = bind(inetAddress.getHostName(), inetAddress.getPort());
    configureBlocking();

    return this;
  }

  public DatagramChannel connect(SocketAddress address) throws IOException {
    InetSocketAddress inetAddress;
    try {
      inetAddress = (InetSocketAddress) address;
    } catch (ClassCastException e) {
      throw new UnsupportedAddressTypeException();
    }

    socket = connect(inetAddress.getHostName(), inetAddress.getPort());
    configureBlocking();

    if (socket != 0) connected = true;

    return this;
  }

  public int write(ByteBuffer b) throws IOException {
    if (b.remaining() == 0) return 0;

    byte[] array = b.array();
    if (array == null) throw new NullPointerException();

    int c = write
      (socket, array, b.arrayOffset() + b.position(), b.remaining(), blocking);

    if (c > 0) {
      b.position(b.position() + c);
    }

    return c;
  }

  public int read(ByteBuffer b) throws IOException {
    int p = b.position();
    receive(b);
    return b.position() - p;
  }

  public SocketAddress receive(ByteBuffer b) throws IOException {
    if (b.remaining() == 0) return null;

    byte[] array = b.array();
    if (array == null) throw new NullPointerException();

    int[] address = new int[2];

    int c = receive
      (socket, array, b.arrayOffset() + b.position(), b.remaining(), blocking,
       address);

    if (c > 0) {
      b.position(b.position() + c);

      return new InetSocketAddress(ipv4ToString(address[0]), address[1]);
    } else {
      return null;
    }
  }

  private static String ipv4ToString(int address) {
    StringBuilder sb = new StringBuilder();

    sb.append( address >> 24        ).append('.')
      .append((address >> 16) & 0xFF).append('.')
      .append((address >>  8) & 0xFF).append('.')
      .append( address        & 0xFF);

    return sb.toString();
  }

  public class Handle extends DatagramSocket {
    public SocketAddress getRemoteSocketAddress() {
      throw new UnsupportedOperationException();
    }

    public void bind(SocketAddress address) throws SocketException {
      try {
        DatagramChannel.this.bind(address);
      } catch (SocketException e) {
        throw e;
      } catch (IOException e) {
        SocketException se = new SocketException();
        se.initCause(e);
        throw se;
      }
    }
  }

  public boolean isConnected() { return connected; }

  /** TODO: This is probably incomplete. */
  public DatagramChannel disconnect() throws IOException {
    close();
    connected = false;
    return this;
  }

  private static native void configureBlocking(int socket, boolean blocking)
    throws IOException;
  private static native int bind(String hostname, int port)
    throws IOException;
  private static native int connect(String hostname, int port)
    throws IOException;
  private static native int write(int socket, byte[] array, int offset,
                                  int length, boolean blocking)
    throws IOException;
  private static native int receive(int socket, byte[] array, int offset,
                                    int length, boolean blocking,
                                    int[] address)
    throws IOException;
}
