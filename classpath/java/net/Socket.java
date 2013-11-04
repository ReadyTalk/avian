/* Copyright (c) 2008-2013, Avian Contributors

   Permission to use, copy, modify, and/or distribute this software
   for any purpose with or without fee is hereby granted, provided
   that the above copyright notice and this permission notice appear
   in all copies.

   There is NO WARRANTY for this software.  See license.txt for
   details. */

package java.net;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Socket implements Closeable, AutoCloseable {

	private static final int SD_RECEIVE = 0x00;
	private static final int SD_SEND = 0x01;
	private static final int SD_BOTH = 0x02;
	
	private static final int BUFFER_SIZE = 65535;
	
	/**
	 * This method is called from all routines that depend on winsock in windows,
	 * so it has public visibility
	 * @throws IOException
	 */
	public static native void init() throws IOException;
	
	/**
	 * Creates the native socket object
	 * @return Handle to the native object
	 * @throws IOException
	 */
	private static native /* SOCKET */long create() throws IOException;
	
	/**
	 * Connects the native socket object to an address:port
	 * @param sock Native socket handler
	 * @param addr Address to connect to
	 * @param port Port to connect to
	 * @throws IOException
	 */
	private static native void connect(/* SOCKET */long sock, long addr, short port) throws IOException;
	private static native void bind(/* SOCKET */long sock, long addr, short port) throws IOException;

	private static native void send(/* SOCKET */long sock, /* char* */long buff_ptr, int buff_size) throws IOException;
	private static native int recv(/* SOCKET */long sock, /* char* */long buff_ptr, int buff_size) throws IOException;
	
	private static native void closeAbortive(/* SOCKET */long sock);
	private static native void closeGraceful(/* SOCKET */long sock, int how);
	
	private static native /* char* */long allocateBuffer(int buf_size);
	private static native void freeBuffer(/* char* */long buf_ptr);
	private static native void copyBufferFromNative(long source_ptr, int source_size, byte[] target, int start_pos);
	private static native void copyBufferToNative(byte[] source, int start_pos, int target_size, long target);
	
	private class SocketInputStream extends InputStream {

		private long buf_ptr;
		
		public SocketInputStream() {
			buf_ptr = allocateBuffer(BUFFER_SIZE);
		}
		
		@Override
		public void close() throws IOException {
			if (buf_ptr != 0) {
				closeGraceful(sock, SD_RECEIVE);
				freeBuffer(buf_ptr);
				buf_ptr = 0;
			}
			super.close();
		}
		
		@Override
		protected void finalize() throws Throwable {
			close();
			super.finalize();
		}
		
		@Override
		public int read() throws IOException {
			int size = recv(sock, buf_ptr, 1);
			if (size == 0) {
				return -1;
			}
			byte[] res = new byte[1];
			copyBufferFromNative(buf_ptr, 1, res, 0);
			return res[0];
		}
		
		@Override
		public int read(byte[] buffer) throws IOException {
			int fullSize = buffer.length;
			int index = 0;
			int size;
			do {
				size = recv(sock, buf_ptr, Math.min(fullSize, Socket.BUFFER_SIZE));
				fullSize -= size;
				copyBufferFromNative(buf_ptr, size, buffer, index);
				index += size;
			} while (fullSize != 0 && size != 0);
			return index;
		}

		
	}
	
	private class SocketOutputStream extends OutputStream {

		private long buf_ptr;
		
		public SocketOutputStream() {
			buf_ptr = allocateBuffer(BUFFER_SIZE);
		}
		
		@Override
		public void close() throws IOException {
			if (buf_ptr != 0) {
				closeGraceful(sock, SD_SEND);
				freeBuffer(buf_ptr);
				buf_ptr = 0;
			}
			super.close();
		}
		
		@Override
		protected void finalize() throws Throwable {
			close();
			super.finalize();
		}
		
		@Override
		public void write(int c) throws IOException {
			byte[] res = new byte[1];
			res[0] = (byte)c;
			copyBufferToNative(res, 0, 1, buf_ptr);
			send(sock, buf_ptr, 1);
		}
		
		@Override
		public void write(byte[] buffer) throws IOException {
			int fullSize = buffer.length;
			int index = 0;
			int size;
			do {
				size = Math.min(fullSize, Socket.BUFFER_SIZE);
				copyBufferToNative(buffer, index, size, buf_ptr);
				send(sock, buf_ptr, size);
				fullSize -= size;
				index += size;
			} while (fullSize != 0 && size != 0);
		}

	}

	private long sock;
	private SocketInputStream inputStream;
	private SocketOutputStream outputStream;
	
	public Socket() throws IOException {
		Socket.init();
		sock = create();
		inputStream = new SocketInputStream();
		outputStream = new SocketOutputStream();
	}
	
	public SocketInputStream getInputStream() {
		return inputStream;
	}
	
	public SocketOutputStream getOutputStream() {
		return outputStream;
	}
	
	public Socket(InetAddress address, int port) throws IOException {
		this();
		connect(sock, address.getRawAddress(), (short)port);
	}
	
	public Socket(String host, int port) throws UnknownHostException, IOException {
		this(InetAddress.getByName(host), port);
	}
	
	public void bind(SocketAddress bindpoint) throws IOException {
		if (bindpoint instanceof InetSocketAddress) {
			InetSocketAddress inetBindpoint = (InetSocketAddress)bindpoint;
			bind(sock, inetBindpoint.getAddress().getRawAddress(), (short) inetBindpoint.getPort());
		}
	}
	
	public void setTcpNoDelay(boolean on) throws SocketException {}

	@Override
	public void close() throws IOException {
		closeGraceful(sock, SD_BOTH);
	}
	
	public void shutdownInput() throws IOException {
		inputStream.close();
	}
	
	public void shutdownOutput() throws IOException {
		outputStream.close();
	}
	
	
	@Override
	protected void finalize() throws Throwable {
		close();
		super.finalize();
	}
}
