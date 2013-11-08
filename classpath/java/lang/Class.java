/* Copyright (c) 2008-2013, Avian Contributors

   Permission to use, copy, modify, and/or distribute this software
   for any purpose with or without fee is hereby granted, provided
   that the above copyright notice and this permission notice appear
   in all copies.

   There is NO WARRANTY for this software.  See license.txt for
   details. */

package java.lang;

import avian.VMClass;
import avian.ClassAddendum;
import avian.AnnotationInvocationHandler;
import avian.SystemClassLoader;
import avian.Classes;
import avian.InnerClassReference;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.AnnotatedElement;
import java.lang.annotation.Annotation;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.security.ProtectionDomain;
import java.security.Permissions;
import java.security.AllPermission;

public final class Class <T> implements Type, AnnotatedElement {
  private static final int PrimitiveFlag = 1 <<  5;
  private static final int EnumFlag      = 1 << 14;

  public final VMClass vmClass;

  public Class(VMClass vmClass) {
    this.vmClass = vmClass;
  }

  public String toString() {
    return getName();
  }

  private static byte[] replace(int a, int b, byte[] s, int offset,
                                int length)
  {
    byte[] array = new byte[length];
    for (int i = 0; i < length; ++i) {
      byte c = s[i];
      array[i] = (byte) (c == a ? b : c);
    }
    return array;
  }

  public String getName() {
    return getName(vmClass);
  }

  public static String getName(VMClass c) {
    if (c.name == null) {
      if ((c.vmFlags & PrimitiveFlag) != 0) {
        if (c == Classes.primitiveClass('V')) {
          c.name = "void\0".getBytes();
        } else if (c == Classes.primitiveClass('Z')) {
          c.name = "boolean\0".getBytes();
        } else if (c == Classes.primitiveClass('B')) {
          c.name = "byte\0".getBytes();
        } else if (c == Classes.primitiveClass('C')) {
          c.name = "char\0".getBytes();
        } else if (c == Classes.primitiveClass('S')) {
          c.name = "short\0".getBytes();
        } else if (c == Classes.primitiveClass('I')) {
          c.name = "int\0".getBytes();
        } else if (c == Classes.primitiveClass('F')) {
          c.name = "float\0".getBytes();
        } else if (c == Classes.primitiveClass('J')) {
          c.name = "long\0".getBytes();
        } else if (c == Classes.primitiveClass('D')) {
          c.name = "double\0".getBytes();
        } else {
          throw new AssertionError();
        }
      } else {
        throw new AssertionError();
      }
    }

    return new String
      (replace('/', '.', c.name, 0, c.name.length - 1), 0, c.name.length - 1,
       false);
  }

  public String getCanonicalName() {
    if ((vmClass.vmFlags & PrimitiveFlag) != 0) {
      return getName();
    } else if (isArray()) {
      return getComponentType().getCanonicalName() + "[]";
    } else {
      return getName().replace('$', '.');
    }
  }

  public String getSimpleName() {
    if ((vmClass.vmFlags & PrimitiveFlag) != 0) {
      return getName();
    } else if (isArray()) {
      return getComponentType().getSimpleName() + "[]";
    } else {
      String name = getCanonicalName();
      int index = name.lastIndexOf('.');
      if (index >= 0) {
        return name.substring(index + 1);
      } else {
        return name;
      }
    }
  }

  public T newInstance()
    throws IllegalAccessException, InstantiationException
  {
    try {
      return (T) getConstructor().newInstance();
    } catch (NoSuchMethodException e) {
      throw new RuntimeException(e);
    } catch (InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  public static Class forName(String name) throws ClassNotFoundException {
    return forName(name, true, Method.getCaller().class_.loader);
  }

  public static Class forName(String name, boolean initialize,
                              ClassLoader loader)
    throws ClassNotFoundException
  {
    return Classes.forName(name, initialize, loader);
  }

  public Class getComponentType() {
    if (isArray()) {
      String n = getName();
      if ("[Z".equals(n)) {
        return SystemClassLoader.getClass(Classes.primitiveClass('Z'));
      } else if ("[B".equals(n)) {
        return SystemClassLoader.getClass(Classes.primitiveClass('B'));
      } else if ("[S".equals(n)) {
        return SystemClassLoader.getClass(Classes.primitiveClass('S'));
      } else if ("[C".equals(n)) {
        return SystemClassLoader.getClass(Classes.primitiveClass('C'));
      } else if ("[I".equals(n)) {
        return SystemClassLoader.getClass(Classes.primitiveClass('I'));
      } else if ("[F".equals(n)) {
        return SystemClassLoader.getClass(Classes.primitiveClass('F'));
      } else if ("[J".equals(n)) {
        return SystemClassLoader.getClass(Classes.primitiveClass('J'));
      } else if ("[D".equals(n)) {
        return SystemClassLoader.getClass(Classes.primitiveClass('D'));
      }

      if (vmClass.staticTable == null) throw new AssertionError();
      return SystemClassLoader.getClass((VMClass) vmClass.staticTable);
    } else {
      return null;
    }
  }

  public boolean isAssignableFrom(Class c) {
    return Classes.isAssignableFrom(vmClass, c.vmClass);
  }

  public Field getDeclaredField(String name) throws NoSuchFieldException {
    int index = Classes.findField(vmClass, name);
    if (index < 0) {
      throw new NoSuchFieldException(name);
    } else {
      return new Field(vmClass.fieldTable[index]);
    }
  }

  public Field getField(String name) throws NoSuchFieldException {
    for (VMClass c = vmClass; c != null; c = c.super_) {
      int index = Classes.findField(c, name);
      if (index >= 0) {
        return new Field(vmClass.fieldTable[index]);
      }
    }
    throw new NoSuchFieldException(name);
  }

  public Method getDeclaredMethod(String name, Class ... parameterTypes)
    throws NoSuchMethodException
  {
    if (name.startsWith("<")) {
      throw new NoSuchMethodException(name);
    }
    int index = Classes.findMethod(vmClass, name, parameterTypes);
    if (index < 0) {
      throw new NoSuchMethodException(name);
    } else {
      return new Method(vmClass.methodTable[index]);
    }
  }

  public Method getMethod(String name, Class ... parameterTypes)
    throws NoSuchMethodException
  {
    if (name.startsWith("<")) {
      throw new NoSuchMethodException(name);
    }
    for (VMClass c = vmClass; c != null; c = c.super_) {
      int index = Classes.findMethod(c, name, parameterTypes);
      if (index >= 0) {
        return new Method(vmClass.methodTable[index]);
      }
    }
    throw new NoSuchMethodException(name);
  }

  public Constructor getConstructor(Class ... parameterTypes)
    throws NoSuchMethodException
  {
    int index = Classes.findMethod(vmClass, "<init>", parameterTypes);
    if (index < 0) {
      throw new NoSuchMethodException();
    } else {
      return new Constructor(new Method(vmClass.methodTable[index]));
    }
  }

  public Constructor getDeclaredConstructor(Class ... parameterTypes)
    throws NoSuchMethodException
  {
    Constructor c = null;
    Constructor[] constructors = getDeclaredConstructors();

    for (int i = 0; i < constructors.length; ++i) {
      if (Classes.match(parameterTypes, constructors[i].getParameterTypes())) {
        c = constructors[i];
      }
    }

    if (c == null) {
      throw new NoSuchMethodException();
    } else {
      return c;
    }
  }

  private int countConstructors(boolean publicOnly) {
    int count = 0;
    if (vmClass.methodTable != null) {
      for (int i = 0; i < vmClass.methodTable.length; ++i) {
        if (((! publicOnly)
             || ((vmClass.methodTable[i].flags & Modifier.PUBLIC))
             != 0)
            && Method.getName(vmClass.methodTable[i]).equals("<init>"))
        {
          ++ count;
        }
      }
    }
    return count;
  }

  public Constructor[] getDeclaredConstructors() {
    Constructor[] array = new Constructor[countConstructors(false)];
    if (vmClass.methodTable != null) {
      Classes.link(vmClass);

      int index = 0;
      for (int i = 0; i < vmClass.methodTable.length; ++i) {
        if (Method.getName(vmClass.methodTable[i]).equals("<init>")) {
          array[index++] = new Constructor(new Method(vmClass.methodTable[i]));
        }
      }
    }

    return array;
  }

  public Constructor[] getConstructors() {
    Constructor[] array = new Constructor[countConstructors(true)];
    if (vmClass.methodTable != null) {
      Classes.link(vmClass);

      int index = 0;
      for (int i = 0; i < vmClass.methodTable.length; ++i) {
        if (((vmClass.methodTable[i].flags & Modifier.PUBLIC) != 0)
            && Method.getName(vmClass.methodTable[i]).equals("<init>"))
        {
          array[index++] = new Constructor(new Method(vmClass.methodTable[i]));
        }
      }
    }

    return array;
  }

  public Field[] getDeclaredFields() {
    if (vmClass.fieldTable != null) {
      Field[] array = new Field[vmClass.fieldTable.length];
      for (int i = 0; i < vmClass.fieldTable.length; ++i) {
        array[i] = new Field(vmClass.fieldTable[i]);
      }
      return array;
    } else {
      return new Field[0];
    }
  }

  private int countPublicFields() {
    int count = 0;
    if (vmClass.fieldTable != null) {
      for (int i = 0; i < vmClass.fieldTable.length; ++i) {
        if (((vmClass.fieldTable[i].flags & Modifier.PUBLIC)) != 0) {
          ++ count;
        }
      }
    }
    return count;
  }

  public Field[] getFields() {
    Field[] array = new Field[countPublicFields()];
    if (vmClass.fieldTable != null) {
      Classes.link(vmClass);

      int ai = 0;
      for (int i = 0; i < vmClass.fieldTable.length; ++i) {
        if (((vmClass.fieldTable[i].flags & Modifier.PUBLIC)) != 0) {
          array[ai++] = new Field(vmClass.fieldTable[i]);
        }
      }
    }
    return array;
  }

  private static void getAllFields(VMClass vmClass, ArrayList<Field> fields) {
    if (vmClass.super_ != null) {
      getAllFields(vmClass.super_, fields);
    }
    if (vmClass.fieldTable != null) {
      Classes.link(vmClass);

      for (int i = 0; i < vmClass.fieldTable.length; ++i) {
        fields.add(new Field(vmClass.fieldTable[i]));
      }
    }
  }

  public Field[] getAllFields() {
    ArrayList<Field> fields = new ArrayList<Field>();
    getAllFields(vmClass, fields);
    return fields.toArray(new Field[fields.size()]);
  }

  public Method[] getDeclaredMethods() {
    return Classes.getMethods(vmClass, false);
  }

  public Method[] getMethods() {
    return Classes.getMethods(vmClass, true);
  }

  public Class[] getInterfaces() {
    if (vmClass.interfaceTable != null) {
      Classes.link(vmClass);

      int stride = (isInterface() ? 1 : 2);
      Class[] array = new Class[vmClass.interfaceTable.length / stride];
      for (int i = 0; i < array.length; ++i) {
        array[i] = SystemClassLoader.getClass
          ((VMClass) vmClass.interfaceTable[i * stride]);
      }
      return array;
    } else {
      return new Class[0];
    }
  }

  public T[] getEnumConstants() {
    if (Enum.class.isAssignableFrom(this)) {
      try {
        return (T[]) getMethod("values").invoke(null);
      } catch (Exception e) {
        throw new Error();
      }
    } else {
      return null;
    }
  }

  public Class[] getDeclaredClasses() {
    if (vmClass.addendum == null || vmClass.addendum.innerClassTable == null) {
      return new Class[0];
    }
    InnerClassReference[] table = vmClass.addendum.innerClassTable;
    Class[] result = new Class[table.length];
    int counter = 0;
    String prefix = getName().replace('.', '/') + "$";
    for (int i = 0; i < table.length; ++i) try {
      byte[] inner = table[i].inner;
      if (inner != null && inner.length > 1) {
        String name = new String(inner, 0, inner.length - 1);
        if (name.startsWith(prefix)) {
          Class innerClass = getClassLoader().loadClass(name);
          result[counter++] = innerClass;
        }
      }
    } catch (ClassNotFoundException e) {
      throw new Error(e);
    }
    if (counter == result.length) {
      return result;
    }
    if (counter == 0) {
      return new Class[0];
    }
    Class[] result2 = new Class[counter];
    System.arraycopy(result, 0, result2, 0, counter);
    return result2;
  }

  public ClassLoader getClassLoader() {
    return vmClass.loader;
  }

  public int getModifiers() {
    return vmClass.flags;
  }

  public boolean isInterface() {
    return (vmClass.flags & Modifier.INTERFACE) != 0;
  }

  public Class getSuperclass() {
    return (vmClass.super_ == null ? null : SystemClassLoader.getClass(vmClass.super_));
  }

  public boolean isArray() {
    return vmClass.arrayDimensions != 0;
  }

  public static boolean isInstance(VMClass c, Object o) {
    return o != null && Classes.isAssignableFrom
      (c, Classes.getVMClass(o));
  }

  public boolean isInstance(Object o) {
    return isInstance(vmClass, o);
  }

  public boolean isPrimitive() {
    return (vmClass.vmFlags & PrimitiveFlag) != 0;
  }

  public boolean isEnum() {
    return getSuperclass() == Enum.class && (vmClass.flags & EnumFlag) != 0;
  }

  public URL getResource(String path) {
    if (! path.startsWith("/")) {
      String name = new String
        (vmClass.name, 0, vmClass.name.length - 1, false);
      int index = name.lastIndexOf('/');
      if (index >= 0) {
        path = name.substring(0, index) + "/" + path;
      }
    }
    return getClassLoader().getResource(path);
  }

  public InputStream getResourceAsStream(String path) {
    URL url = getResource(path);
    try {
      return (url == null ? null : url.openStream());
    } catch (IOException e) {
      return null;
    }
  }

  public boolean desiredAssertionStatus() {
    return false;
  }

  public <T> Class<? extends T> asSubclass(Class<T> c) {
    if (! c.isAssignableFrom(this)) {
      throw new ClassCastException();
    }

    return (Class<? extends T>) this;
  }

  public T cast(Object o) {
    return (T) o;
  }

  public Package getPackage() {
    if ((vmClass.vmFlags & PrimitiveFlag) != 0 || isArray()) {
      return null;
    } else {
      String name = getCanonicalName();
      int index = name.lastIndexOf('.');
      if (index >= 0) {
        return new Package(name.substring(0, index),
                           null, null, null, null, null, null, null, null);
      } else {
        return null;
      }
    }
  }

  public boolean isAnnotationPresent
    (Class<? extends Annotation> class_)
  {
    return getAnnotation(class_) != null;
  }

  private static Annotation getAnnotation(VMClass c, Object[] a) {
    if (a[0] == null) {
      a[0] = Proxy.newProxyInstance
        (c.loader, new Class[] { (Class) a[1] },
         new AnnotationInvocationHandler(a));
    }
    return (Annotation) a[0];
  }

  public <T extends Annotation> T getAnnotation(Class<T> class_) {
    for (VMClass c = vmClass; c != null; c = c.super_) {
      if (c.addendum != null && c.addendum.annotationTable != null) {
        Classes.link(c, c.loader);

        Object[] table = (Object[]) c.addendum.annotationTable;
        for (int i = 0; i < table.length; ++i) {
          Object[] a = (Object[]) table[i];
          if (a[1] == class_) {
            return (T) getAnnotation(c, a);
          }
        }
      }
    }
    return null;
  }

  public Annotation[] getDeclaredAnnotations() {
    if (vmClass.addendum.annotationTable != null) {
      Classes.link(vmClass);

      Object[] table = (Object[]) vmClass.addendum.annotationTable;
      Annotation[] array = new Annotation[table.length];
      for (int i = 0; i < table.length; ++i) {
        array[i] = getAnnotation(vmClass, (Object[]) table[i]);
      }
      return array;
    } else {
      return new Annotation[0];
    }
  }

  private int countAnnotations() {
    int count = 0;
    for (VMClass c = vmClass; c != null; c = c.super_) {
      if (c.addendum != null && c.addendum.annotationTable != null) {
        count += ((Object[]) c.addendum.annotationTable).length;
      }
    }
    return count;
  }

  public Annotation[] getAnnotations() {
    Annotation[] array = new Annotation[countAnnotations()];
    int i = 0;
    for (VMClass c = vmClass; c != null; c = c.super_) {
      if (c.addendum != null && c.addendum.annotationTable != null) {
        Object[] table = (Object[]) c.addendum.annotationTable;
        for (int j = 0; j < table.length; ++j) {
          array[i++] = getAnnotation(vmClass, (Object[]) table[j]);
        }
      }
    }

    return array;
  }

  public ProtectionDomain getProtectionDomain() {
    Permissions p = new Permissions();
    p.add(new AllPermission());
    return new ProtectionDomain(null, p);
  }
}
