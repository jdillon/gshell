/*
 * Copyright (c) 2009-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.planet57.gshell.util.converter;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.List;

/**
 * Helpers for {@link Converter} implementations.
 *
 * @since 2.0
 */
public class ConverterHelper
{
  public static boolean hasDefaultConstructor(final Class type) {
    if (!Modifier.isPublic(type.getModifiers())) {
      return false;
    }
    if (Modifier.isAbstract(type.getModifiers())) {
      return false;
    }

    for (Constructor constructor : type.getConstructors()) {
      if (Modifier.isPublic(constructor.getModifiers()) &&
          constructor.getParameterTypes().length == 0) {
        return true;
      }
    }

    return false;
  }

  public static boolean isAssignableFrom(final Class expected, final Class actual) {
    if (expected == null) {
      return true;
    }

    if (expected.isPrimitive()) {
      // verify actual is the correct wrapper type
      if (expected.equals(boolean.class)) {
        return actual.equals(Boolean.class);
      }
      else if (expected.equals(char.class)) {
        return actual.equals(Character.class);
      }
      else if (expected.equals(byte.class)) {
        return actual.equals(Byte.class);
      }
      else if (expected.equals(short.class)) {
        return actual.equals(Short.class);
      }
      else if (expected.equals(int.class)) {
        return actual.equals(Integer.class);
      }
      else if (expected.equals(long.class)) {
        return actual.equals(Long.class);
      }
      else if (expected.equals(float.class)) {
        return actual.equals(Float.class);
      }
      else if (expected.equals(double.class)) {
        return actual.equals(Double.class);
      }
      else {
        throw new AssertionError("Invalid primitive type: " + expected);
      }
    }

    return expected.isAssignableFrom(actual);
  }

  public static boolean isAssignableFrom(final List<? extends Class<?>> expectedTypes,
                                         final List<? extends Class<?>> actualTypes)
  {
    if (expectedTypes.size() != actualTypes.size()) {
      return false;
    }

    for (int i = 0; i < expectedTypes.size(); i++) {
      Class expectedType = expectedTypes.get(i);
      Class actualType = actualTypes.get(i);
      if (expectedType != actualType && !isAssignableFrom(expectedType, actualType)) {
        return false;
      }
    }

    return true;
  }

  public static Class toClass(final Type type) {
    // GenericArrayType, ParameterizedType, TypeVariable<D>, WildcardType
    if (type instanceof Class) {
      Class clazz = (Class) type;
      return clazz;
    }
    else if (type instanceof GenericArrayType) {
      GenericArrayType arrayType = (GenericArrayType) type;
      Class componentType = toClass(arrayType.getGenericComponentType());
      return Array.newInstance(componentType, 0).getClass();
    }
    else if (type instanceof ParameterizedType) {
      ParameterizedType parameterizedType = (ParameterizedType) type;
      return toClass(parameterizedType.getRawType());
    }
    else {
      return Object.class;
    }
  }

  public static Type[] getTypeParameters(final Class desiredType, final Type type) {
    if (type instanceof Class) {
      Class rawClass = (Class) type;

      // if this is the collection class we're done
      if (desiredType.equals(type)) {
        return null;
      }

      for (Type intf : rawClass.getGenericInterfaces()) {
        Type[] collectionType = getTypeParameters(desiredType, intf);
        if (collectionType != null) {
          return collectionType;
        }
      }

      return getTypeParameters(desiredType, rawClass.getGenericSuperclass());
    }
    else if (type instanceof ParameterizedType) {
      ParameterizedType parameterizedType = (ParameterizedType) type;

      Type rawType = parameterizedType.getRawType();
      if (desiredType.equals(rawType)) {
        Type[] argument = parameterizedType.getActualTypeArguments();
        return argument;
      }

      Type[] collectionTypes = getTypeParameters(desiredType, rawType);
      if (collectionTypes != null) {
        for (int i = 0; i < collectionTypes.length; i++) {
          if (collectionTypes[i] instanceof TypeVariable) {
            TypeVariable typeVariable = (TypeVariable) collectionTypes[i];
            TypeVariable[] rawTypeParams = ((Class) rawType).getTypeParameters();
            for (int j = 0; j < rawTypeParams.length; j++) {
              if (typeVariable.getName().equals(rawTypeParams[j].getName())) {
                collectionTypes[i] = parameterizedType.getActualTypeArguments()[j];
              }
            }
          }
        }
      }

      return collectionTypes;
    }

    return null;
  }
}