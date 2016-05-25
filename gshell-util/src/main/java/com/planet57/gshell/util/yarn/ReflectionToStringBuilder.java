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
package com.planet57.gshell.util.yarn;

//
// NOTE: Copied and massaged from commons-lang 2.3
//

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Porting/adapting commons-land muck to GShell Yarn.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.3
 */
class ReflectionToStringBuilder
    extends ToStringBuilder
{
  public static String toString(Object object) {
    return toString(object, null, false, false, null);
  }

  public static String toString(Object object, ToStringStyle style) {
    return toString(object, style, false, false, null);
  }

  public static String toString(Object object, ToStringStyle style, boolean outputTransients) {
    return toString(object, style, outputTransients, false, null);
  }

  public static String toString(Object object, ToStringStyle style, boolean outputTransients, boolean outputStatics) {
    return toString(object, style, outputTransients, outputStatics, null);
  }

  public static String toString(Object object, ToStringStyle style, boolean outputTransients, boolean outputStatics,
                                Class reflectUpToClass)
  {
    return new ReflectionToStringBuilder(object, style, null, reflectUpToClass, outputTransients, outputStatics)
        .toString();
  }

  public static String toStringExclude(Object object, final String excludeFieldName) {
    return toStringExclude(object, new String[]{excludeFieldName});
  }

  public static String toStringExclude(Object object, Collection /*String*/ excludeFieldNames) {
    return toStringExclude(object, toNoNullStringArray(excludeFieldNames));
  }

  static String[] toNoNullStringArray(Collection collection) {
    if (collection == null) {
      return ArrayUtils_EMPTY_STRING_ARRAY;
    }
    return toNoNullStringArray(collection.toArray());
  }

  static String[] toNoNullStringArray(Object[] array) {
    ArrayList list = new ArrayList(array.length);
    for (Object obj : array) {
      if (obj != null) {
        list.add(obj.toString());
      }
    }
    return (String[]) list.toArray(ArrayUtils_EMPTY_STRING_ARRAY);
  }

  public static String toStringExclude(Object object, String[] excludeFieldNames) {
    return new ReflectionToStringBuilder(object).setExcludeFieldNames(excludeFieldNames).toString();
  }

  /**
   * Whether or not to append static fields.
   */
  private boolean appendStatics = false;

  /**
   * Whether or not to append transient fields.
   */
  private boolean appendTransients = false;

  /**
   * Which field names to exclude from output. Intended for fields like <code>"password"</code>.
   */
  private String[] excludeFieldNames;

  /**
   * The last super class to stop appending fields for.
   */
  private Class upToClass = null;

  public ReflectionToStringBuilder(Object object) {
    super(object);
  }

  public ReflectionToStringBuilder(Object object, ToStringStyle style) {
    super(object, style);
  }

  public ReflectionToStringBuilder(Object object, ToStringStyle style, StringBuilder buffer) {
    super(object, style, buffer);
  }

  public ReflectionToStringBuilder(Object object, ToStringStyle style, StringBuilder buffer, Class reflectUpToClass,
                                   boolean outputTransients, boolean outputStatics)
  {
    super(object, style, buffer);
    this.setUpToClass(reflectUpToClass);
    this.setAppendTransients(outputTransients);
    this.setAppendStatics(outputStatics);
  }

  protected boolean accept(Field field) {
    if (field.getName().indexOf(ClassUtils_INNER_CLASS_SEPARATOR_CHAR) != -1) {
      // Reject field from inner class.
      return false;
    }
    if (Modifier.isTransient(field.getModifiers()) && !this.isAppendTransients()) {
      // Reject transient fields.
      return false;
    }
    if (Modifier.isStatic(field.getModifiers()) && !this.isAppendStatics()) {
      // Rject static fields.
      return false;
    }

    return !(this.getExcludeFieldNames() != null
        && Arrays.binarySearch(this.getExcludeFieldNames(), field.getName()) >= 0);
  }

  protected void appendFieldsIn(Class clazz) {
    if (clazz.isArray()) {
      this.reflectionAppendArray(this.getObject());
      return;
    }
    Field[] fields = clazz.getDeclaredFields();
    AccessibleObject.setAccessible(fields, true);

    int i = 0;
    while (i < fields.length) {
      Field field = fields[i];
      String fieldName = field.getName();
      if (this.accept(field)) {
        try {
          // Warning: Field.get(Object) creates wrappers objects
          // for primitive types.
          Object fieldValue = this.getValue(field);
          this.append(fieldName, fieldValue);
        }
        catch (IllegalAccessException ex) {
          //this can't happen. Would get a Security exception
          // instead
          //throw a runtime exception in case the impossible
          // happens.
          throw new InternalError("Unexpected IllegalAccessException: " + ex.getMessage());
        }
      }
      i++;
    }
  }

  /**
   * @return Returns the excludeFieldNames.
   */
  public String[] getExcludeFieldNames() {
    return this.excludeFieldNames;
  }

  public Class getUpToClass() {
    return this.upToClass;
  }

  protected Object getValue(Field field) throws IllegalArgumentException, IllegalAccessException {
    return field.get(this.getObject());
  }

  public boolean isAppendStatics() {
    return this.appendStatics;
  }

  public boolean isAppendTransients() {
    return this.appendTransients;
  }

  public ToStringBuilder reflectionAppendArray(Object array) {
    this.getStyle().reflectionAppendArrayDetail(this.getStringBuilder(), null, array);
    return this;
  }

  public void setAppendStatics(boolean appendStatics) {
    this.appendStatics = appendStatics;
  }

  public void setAppendTransients(boolean appendTransients) {
    this.appendTransients = appendTransients;
  }

  public ReflectionToStringBuilder setExcludeFieldNames(String[] excludeFieldNamesParam) {
    if (excludeFieldNamesParam == null) {
      this.excludeFieldNames = null;
    }
    else {
      this.excludeFieldNames = toNoNullStringArray(excludeFieldNamesParam);
      Arrays.sort(this.excludeFieldNames);
    }
    return this;
  }

  public void setUpToClass(Class clazz) {
    this.upToClass = clazz;
  }

  public String toString() {
    if (this.getObject() == null) {
      return this.getStyle().getNullText();
    }
    Class clazz = this.getObject().getClass();
    this.appendFieldsIn(clazz);
    while (clazz.getSuperclass() != null && clazz != this.getUpToClass()) {
      clazz = clazz.getSuperclass();
      this.appendFieldsIn(clazz);
    }
    return super.toString();
  }

  //
  // NOTE: Unrolled from commons-lang bits
  //

  private static final String[] ArrayUtils_EMPTY_STRING_ARRAY = {};

  private static final char ClassUtils_INNER_CLASS_SEPARATOR_CHAR = '$';
}