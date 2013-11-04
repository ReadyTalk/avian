/* Copyright (c) 2008-2013, Avian Contributors

   Permission to use, copy, modify, and/or distribute this software
   for any purpose with or without fee is hereby granted, provided
   that the above copyright notice and this permission notice appear
   in all copies.

   There is NO WARRANTY for this software.  See license.txt for
   details. */


#include "jni.h"
#include "jni-util.h"

#ifdef PLATFORM_WINDOWS
#  include <winsock2.h>

#  define ONLY_ON_WINDOWS(x)	x
#else
#  include <netdb.h>
#  include <sys/socket.h>
#  include <netinet/in.h>
#  include <unistd.h>

#  define ONLY_ON_WINDOWS(x)
#  define SOCKET				int
#  define INVALID_SOCKET		-1
#  define SOCKET_ERROR			-1
#  define closesocket(x)		close(x)
#endif

int last_socket_error() {
#ifdef PLATFORM_WINDOWS
		int error = WSAGetLastError();
#else
		int error = errno;
#endif
		return error;
}

extern "C" JNIEXPORT void JNICALL
Java_java_net_Socket_init(JNIEnv* ONLY_ON_WINDOWS(e), jclass)
{
#ifdef PLATFORM_WINDOWS
  static bool wsaInitialized = false;
  if (not wsaInitialized) {
    WSADATA data;
    int r = WSAStartup(MAKEWORD(2, 2), &data);
    if (r or LOBYTE(data.wVersion) != 2 or HIBYTE(data.wVersion) != 2) {
      throwNew(e, "java/io/IOException", "WSAStartup failed");
    } else {
      wsaInitialized = true;
    }
  }
#endif
}

extern "C" JNIEXPORT SOCKET JNICALL
Java_java_net_Socket_create(JNIEnv* e, jclass) {
	SOCKET sock;
	if (INVALID_SOCKET == (sock = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP))) {
		char buf[255];
		sprintf(buf, "Can't create a socket. System error: %d", last_socket_error());
		throwNew(e, "java/io/IOException", buf);
		return 0;	// This doesn't matter cause we have risen an exception
	}
	return sock;
}

extern "C" JNIEXPORT void JNICALL
Java_java_net_Socket_connect(JNIEnv* e, jclass, SOCKET sock, long addr, short port) {
	sockaddr_in adr;
	adr.sin_family = AF_INET;
#ifdef PLATFORM_WINDOWS
	adr.sin_addr.S_un.S_addr = htonl(addr);
#else
	adr.sin_addr.s_addr = htonl(addr);
#endif
	adr.sin_port = htons (port);

	if (SOCKET_ERROR == connect(sock, (sockaddr* )&adr, sizeof(adr)))
	{
		char buf[255];
		sprintf(buf, "Can't connect a socket. System error: %d", last_socket_error());
		throwNew(e, "java/io/IOException", buf);
		return;
	}
}

extern "C" JNIEXPORT void JNICALL
Java_java_net_Socket_bind(JNIEnv* e, jclass, SOCKET sock, long addr, short port) {
	sockaddr_in adr;
	adr.sin_family = AF_INET;
#ifdef PLATFORM_WINDOWS
	adr.sin_addr.S_un.S_addr = htonl(addr);
#else
	adr.sin_addr.s_addr = htonl(addr);
#endif
	adr.sin_port = htons (port);

	if (SOCKET_ERROR == bind(sock, (sockaddr* )&adr, sizeof(adr)))
	{
		char buf[255];
		sprintf(buf, "Can't bind a socket. System error: %d", last_socket_error());
		throwNew(e, "java/io/IOException", buf);
		return;
	}
}


extern "C" JNIEXPORT void JNICALL
Java_java_net_Socket_send(JNIEnv* e, jclass, SOCKET sock, const char* buff_ptr, int buff_size) {
	if (SOCKET_ERROR == send(sock, buff_ptr, buff_size, 0)) {
		char buf[255];
		sprintf(buf, "Can't send data through the socket. System error: %d", last_socket_error());
		throwNew(e, "java/io/IOException", buf);
		return;
	}
}

extern "C" JNIEXPORT int JNICALL
Java_java_net_Socket_recv(JNIEnv* e, jclass, SOCKET sock, char* buff_ptr, int buff_size) {
	int length = recv(sock, buff_ptr, buff_size, 0);
	if (SOCKET_ERROR == length) {
		char buf[255];
		sprintf(buf, "Can't receive data through the socket. System error: %d", last_socket_error());
		throwNew(e, "java/io/IOException", buf);
		return 0;	// This doesn't matter cause we have risen an exception
	}
	return length;
}

extern "C" JNIEXPORT void JNICALL
Java_java_net_Socket_closeAbortive(JNIEnv* e, jclass, SOCKET sock) {
	if (SOCKET_ERROR == closesocket(sock)) {
		char buf[255];
		sprintf(buf, "Can't close the socket. System error: %d", last_socket_error());
		throwNew(e, "java/io/IOException", buf);
	}
}

extern "C" JNIEXPORT void JNICALL
Java_java_net_Socket_closeGraceful(JNIEnv* e, jclass, SOCKET sock, int how) {
	if (SOCKET_ERROR == shutdown(sock, how)) {
		char buf[255];
		sprintf(buf, "Can't shutdown the socket. System error: %d", last_socket_error());
		throwNew(e, "java/io/IOException", buf);
	}
}

extern "C" JNIEXPORT char* JNICALL
Java_java_net_Socket_allocateBuffer(JNIEnv*, jclass, int buf_size) {
	return (char*)malloc(buf_size);
}

extern "C" JNIEXPORT void JNICALL
Java_java_net_Socket_freeBuffer(JNIEnv*, jclass, char* buf_ptr) {
	free(buf_ptr);
}

extern "C" JNIEXPORT void JNICALL
Java_java_net_Socket_copyBufferFromNative(JNIEnv* e, jclass, char* source_ptr, int source_size, jbyteArray target, int start_pos) {
	if (source_size > e->GetArrayLength(target) - start_pos) {
		throwNew(e, "java/lang/RuntimeException", "The source buffer is greater than the target array");
		return;
	}
	jbyte* targetElements = e->GetByteArrayElements(target, NULL);
	memcpy(targetElements + start_pos, source_ptr, source_size);
	e->ReleaseByteArrayElements(target, targetElements, (int)0);
}

extern "C" JNIEXPORT void JNICALL
Java_java_net_Socket_copyBufferToNative(JNIEnv* e, jclass, jbyteArray source, int start_pos, int target_size, char* target) {
	int source_size = e->GetArrayLength(source);
	if (source_size - start_pos > target_size) {
		throwNew(e, "java/lang/RuntimeException", "The source array is greater than the target buffer");
		return;
	}
	jbyte* sourceElements = e->GetByteArrayElements(source, NULL);
	memcpy(target, sourceElements + start_pos, target_size);
	e->ReleaseByteArrayElements(source, sourceElements, JNI_ABORT);
}


extern "C" JNIEXPORT jint JNICALL
Java_java_net_InetAddress_ipv4AddressForName(JNIEnv* e,
                                             jclass,
                                             jstring name)
{
  const char* chars = e->GetStringUTFChars(name, 0);
  if (chars) {
#ifdef PLATFORM_WINDOWS
    hostent* host = gethostbyname(chars);
    e->ReleaseStringUTFChars(name, chars);
    if (host) {
      return ntohl(reinterpret_cast<in_addr*>(host->h_addr_list[0])->s_addr);
    } else {
      fprintf(stderr, "trouble %d\n", WSAGetLastError());
    }
#else
    addrinfo hints;
    memset(&hints, 0, sizeof(addrinfo));
    hints.ai_family = AF_INET;
    hints.ai_socktype = SOCK_STREAM;

    addrinfo* result;
    int r = getaddrinfo(chars, 0, &hints, &result);
    e->ReleaseStringUTFChars(name, chars);

    jint address;
    if (r != 0) {
      address = 0;
    } else {
      address = ntohl
        (reinterpret_cast<sockaddr_in*>(result->ai_addr)->sin_addr.s_addr);

      freeaddrinfo(result);
    }

    return address;
#endif
  }
  return 0;
}

