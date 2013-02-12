/* Copyright (c) 2008, Avian Contributors

   Permission to use, copy, modify, and/or distribute this software
   for any purpose with or without fee is hereby granted, provided
   that the above copyright notice and this permission notice appear
   in all copies.

   There is NO WARRANTY for this software.  See license.txt for
   details. */

#ifndef JNIENV_H
#define JNIENV_H

#include "machine.h"

#define BOOTSTRAP_PROPERTY "avian.bootstrap"
#define CRASHDIR_PROPERTY "avian.crash.dir"
#define EMBED_PREFIX_PROPERTY "avian.embed.prefix"
#define CLASSPATH_PROPERTY "java.class.path"
#define JAVA_HOME_PROPERTY "java.home"
#define BOOTCLASSPATH_PREPEND_OPTION "bootclasspath/p"
#define BOOTCLASSPATH_OPTION "bootclasspath"
#define BOOTCLASSPATH_APPEND_OPTION "bootclasspath/a"
#define BOOTCLASSPATH_APPEND_OPTION "bootclasspath/a"

namespace vm {

void
populateJNITables(JavaVMVTable* vmTable, JNIEnvVTable* envTable);

} // namespace vm

#endif//JNIENV_H
