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
package org.sonatype.gshell.util.converter;

import org.sonatype.gshell.util.converter.basic.ArrayConverter;
import org.sonatype.gshell.util.converter.basic.BigDecimalConverter;
import org.sonatype.gshell.util.converter.basic.BigIntegerConverter;
import org.sonatype.gshell.util.converter.basic.ClassConverter;
import org.sonatype.gshell.util.converter.basic.DateConverter;
import org.sonatype.gshell.util.converter.basic.EnumConverter;
import org.sonatype.gshell.util.converter.basic.FileConverter;
import org.sonatype.gshell.util.converter.basic.Inet4AddressConverter;
import org.sonatype.gshell.util.converter.basic.Inet6AddressConverter;
import org.sonatype.gshell.util.converter.basic.InetAddressConverter;
import org.sonatype.gshell.util.converter.basic.ObjectNameConverter;
import org.sonatype.gshell.util.converter.basic.PatternConverter;
import org.sonatype.gshell.util.converter.basic.StringConverter;
import org.sonatype.gshell.util.converter.basic.UriConverter;
import org.sonatype.gshell.util.converter.basic.UrlConverter;
import org.sonatype.gshell.util.converter.collections.ArrayListConverter;
import org.sonatype.gshell.util.converter.collections.GenericCollectionConverter;
import org.sonatype.gshell.util.converter.collections.GenericMapConverter;
import org.sonatype.gshell.util.converter.collections.HashMapConverter;
import org.sonatype.gshell.util.converter.collections.IdentityHashMapConverter;
import org.sonatype.gshell.util.converter.collections.LinkedHashMapConverter;
import org.sonatype.gshell.util.converter.collections.LinkedHashSetConverter;
import org.sonatype.gshell.util.converter.collections.LinkedListConverter;
import org.sonatype.gshell.util.converter.collections.ListConverter;
import org.sonatype.gshell.util.converter.collections.MapConverter;
import org.sonatype.gshell.util.converter.collections.PropertiesConverter;
import org.sonatype.gshell.util.converter.collections.SetConverter;
import org.sonatype.gshell.util.converter.collections.SortedMapConverter;
import org.sonatype.gshell.util.converter.collections.SortedSetConverter;
import org.sonatype.gshell.util.converter.collections.TreeMapConverter;
import org.sonatype.gshell.util.converter.collections.TreeSetConverter;
import org.sonatype.gshell.util.converter.collections.WeakHashMapConverter;
import org.sonatype.gshell.util.converter.primitive.BooleanConverter;
import org.sonatype.gshell.util.converter.primitive.ByteConverter;
import org.sonatype.gshell.util.converter.primitive.CharacterConverter;
import org.sonatype.gshell.util.converter.primitive.DoubleConverter;
import org.sonatype.gshell.util.converter.primitive.FloatConverter;
import org.sonatype.gshell.util.converter.primitive.IntegerConverter;
import org.sonatype.gshell.util.converter.primitive.LongConverter;
import org.sonatype.gshell.util.converter.primitive.ShortConverter;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.sonatype.gshell.util.converter.ConverterHelper.*;

/**
 * Provides access to conversion.
 *
 * @since 2.0
 */
public class Converters
{
    private static final Map<Class, Converter> REGISTRY = Collections.synchronizedMap(new HashMap<Class,Converter>());

    private static final Map<Class, Class> PRIMITIVE_TO_WRAPPER;

    private static final Map<Class, Class> WRAPPER_TO_PRIMITIVE;

    private static boolean registerWithVM;

    /**
     * Register all of the built in converters
     */
    static {
        Map<Class, Class> map = new HashMap<Class, Class>();
        map.put(boolean.class, Boolean.class);
        map.put(char.class, Character.class);
        map.put(byte.class, Byte.class);
        map.put(short.class, Short.class);
        map.put(int.class, Integer.class);
        map.put(long.class, Long.class);
        map.put(float.class, Float.class);
        map.put(double.class, Double.class);
        PRIMITIVE_TO_WRAPPER = Collections.unmodifiableMap(map);


        map = new HashMap<Class, Class>();
        map.put(Boolean.class, boolean.class);
        map.put(Character.class, char.class);
        map.put(Byte.class, byte.class);
        map.put(Short.class, short.class);
        map.put(Integer.class, int.class);
        map.put(Long.class, long.class);
        map.put(Float.class, float.class);
        map.put(Double.class, double.class);
        WRAPPER_TO_PRIMITIVE = Collections.unmodifiableMap(map);

        // Explicitly register the types
        registerConverter(new ArrayListConverter());
        registerConverter(new BigDecimalConverter());
        registerConverter(new BigIntegerConverter());
        registerConverter(new BooleanConverter());
        registerConverter(new ByteConverter());
        registerConverter(new CharacterConverter());
        registerConverter(new ClassConverter());
        registerConverter(new DateConverter());
        registerConverter(new DoubleConverter());
        registerConverter(new FileConverter());
        registerConverter(new FloatConverter());
        registerConverter(new HashMapConverter());
        registerConverter(new IdentityHashMapConverter());
        registerConverter(new Inet4AddressConverter());
        registerConverter(new Inet6AddressConverter());
        registerConverter(new InetAddressConverter());
        registerConverter(new IntegerConverter());
        registerConverter(new LinkedHashMapConverter());
        registerConverter(new LinkedHashSetConverter());
        registerConverter(new LinkedListConverter());
        registerConverter(new ListConverter());
        registerConverter(new LongConverter());
        registerConverter(new MapConverter());
        registerConverter(new ObjectNameConverter());
        registerConverter(new PropertiesConverter());
        registerConverter(new SetConverter());
        registerConverter(new ShortConverter());
        registerConverter(new SortedMapConverter());
        registerConverter(new SortedSetConverter());
        registerConverter(new StringConverter());
        registerConverter(new TreeMapConverter());
        registerConverter(new TreeSetConverter());
        registerConverter(new UriConverter());
        registerConverter(new UrlConverter());
        registerConverter(new PatternConverter());
        registerConverter(new WeakHashMapConverter());
    }

    /**
     * Are converters registered with the VM PropertyEditorManager.  By default
     * converters are not registered with the VM as this creates problems for
     * IDE and Spring because they rely in their specific converters being
     * registered to function properly.
     */
    public static boolean isRegisterWithVM() {
        return registerWithVM;
    }

    /**
     * Sets if converters registered with the VM PropertyEditorManager.
     * If the new value is true, all currently registered converters are
     * immediately registered with the VM.
     */
    public static void setRegisterWithVM(boolean registerWithVM) {
        if (Converters.registerWithVM != registerWithVM) {
            Converters.registerWithVM = registerWithVM;

            // register all converters with the VM
            if (registerWithVM) {
                for (Entry<Class, Converter> entry : REGISTRY.entrySet()) {
                    Class type = entry.getKey();
                    Converter converter = entry.getValue();
                    PropertyEditorManager.registerEditor(type, converter.getClass());
                }
            }
        }
    }

    public static void registerConverter(final Converter converter) {
        assert converter != null;

        Class type = converter.getType();
        REGISTRY.put(type, converter);
        if (registerWithVM) {
            PropertyEditorManager.registerEditor(type, converter.getClass());
        }

        if (PRIMITIVE_TO_WRAPPER.containsKey(type)) {
            Class wrapperType = PRIMITIVE_TO_WRAPPER.get(type);
            REGISTRY.put(wrapperType, converter);
            if (registerWithVM) {
                PropertyEditorManager.registerEditor(wrapperType, converter.getClass());
            }
        }
        else if (WRAPPER_TO_PRIMITIVE.containsKey(type)) {
            Class primitiveType = WRAPPER_TO_PRIMITIVE.get(type);
            REGISTRY.put(primitiveType, converter);
            if (registerWithVM) {
                PropertyEditorManager.registerEditor(primitiveType, converter.getClass());
            }
        }
    }

    public static boolean isConvertible(final String type, final ClassLoader classLoader) {
        assert type != null;
        assert classLoader != null;

        // load using the ClassLoading utility, which also manages arrays and primitive classes.
        Class typeClass;
        try {
            typeClass = Class.forName(type, true, classLoader);
        }
        catch (ClassNotFoundException e) {
            throw new ConversionException("Type class could not be found: " + type);
        }

        return isConvertible(typeClass);

    }

    public static boolean isConvertible(final Class type) {
        return findConverterOrEditor(type) != null;
    }

    private static PropertyEditor findConverterOrEditor(final Type type) {
        Converter converter = findConverter(type);
        if (converter != null) {
            return converter;
        }

        // fall back to a property editor
        PropertyEditor editor = findEditor(type);
        if (editor != null) {
            return editor;
        }

        converter = findBuiltinConverter(type);
        if (converter != null) {
            return converter;
        }

        return null;
    }

    public static String toString(final Object value) throws ConversionException {
        assert value != null;

        // get an editor for this type
        Class type = value.getClass();

        PropertyEditor editor = findConverterOrEditor(type);

        if (editor instanceof Converter) {
            Converter converter = (Converter) editor;
            return converter.toString(value);
        }

        if (editor == null) {
            throw new ConversionException("Unable to find converter for " + type.getSimpleName());
        }

        // create the string value
        editor.setValue(value);
        String textValue;
        try {
            textValue = editor.getAsText();
        }
        catch (Exception e) {
            throw new ConversionException("Error while converting a \"" + type.getSimpleName() + "\" to text " +
                " using the converter " + editor.getClass().getSimpleName(), e);
        }
        return textValue;
    }

    public static Object getValue(final String type, final String value, final ClassLoader classLoader) throws ConversionException {
        assert type != null;
        assert value != null;
        assert classLoader != null;

        // load using the ClassLoading utility, which also manages arrays and primitive classes.
        Class typeClass;
        try {
            typeClass = Class.forName(type, true, classLoader);
        }
        catch (ClassNotFoundException e) {
            throw new ConversionException("Type class could not be found: " + type);
        }

        return getValue(typeClass, value);
    }

    public static Object getValue(final Type type, final String value) throws ConversionException {
        assert type != null;
        assert value != null;

        PropertyEditor editor = findConverterOrEditor(type);

        if (editor instanceof Converter) {
            Converter converter = (Converter) editor;
            return converter.toObject(value);
        }

        Class clazz = toClass(type);

        if (editor == null) {
            throw new ConversionException("Unable to find converter for " + clazz.getSimpleName());
        }

        editor.setAsText(value);
        Object objectValue;
        try {
            objectValue = editor.getValue();
        }
        catch (Exception e) {
            throw new ConversionException("Error while converting \"" + value + "\" to a " + clazz.getSimpleName() +
                " using the converter " + editor.getClass().getSimpleName(), e);
        }
        return objectValue;
    }

    private static Converter findBuiltinConverter(final Type type) {
        assert type != null;
        Class clazz = toClass(type);

        if (Enum.class.isAssignableFrom(clazz)) {
            return new EnumConverter(clazz);
        }

        return null;
    }

    private static Converter findConverter(final Type type) {
        assert type != null;
        Class clazz = toClass(type);


        // it's possible this was a request for an array class.  We might not
        // recognize the array type directly, but the component type might be
        // resolvable
        if (clazz.isArray() && !clazz.getComponentType().isArray()) {
            // do a recursive lookup on the base type
            PropertyEditor editor = findConverterOrEditor(clazz.getComponentType());
            // if we found a suitable editor for the base component type,
            // wrapper this in an array adaptor for real use
            if (editor != null) {
                return new ArrayConverter(clazz, editor);
            }
            else {
                return null;
            }
        }

        if (Collection.class.isAssignableFrom(clazz)) {
            Type[] types = getTypeParameters(Collection.class, type);

            Type componentType = String.class;
            if (types != null && types.length == 1 && types[0] instanceof Class) {
                componentType = types[0];
            }

            PropertyEditor editor = findConverterOrEditor(componentType);

            if (editor != null) {
                if (ConverterHelper.hasDefaultConstructor(clazz)) {
                    return new GenericCollectionConverter(clazz, editor);
                }
                else if (SortedSet.class.isAssignableFrom(clazz)) {
                    return new GenericCollectionConverter(TreeSet.class, editor);
                }
                else if (Set.class.isAssignableFrom(clazz)) {
                    return new GenericCollectionConverter(LinkedHashSet.class, editor);
                }
                else {
                    return new GenericCollectionConverter(ArrayList.class, editor);
                }
            }

            return null;
        }

        if (Map.class.isAssignableFrom(clazz)) {
            Type[] types = getTypeParameters(Map.class, type);

            Type keyType = String.class;
            Type valueType = String.class;
            if (types != null && types.length == 2 && types[0] instanceof Class && types[1] instanceof Class) {
                keyType = types[0];
                valueType = types[1];
            }

            PropertyEditor keyConverter = findConverterOrEditor(keyType);
            PropertyEditor valueConverter = findConverterOrEditor(valueType);

            if (keyConverter != null && valueConverter != null) {
                if (ConverterHelper.hasDefaultConstructor(clazz)) {
                    return new GenericMapConverter(clazz, keyConverter, valueConverter);
                }
                else if (SortedMap.class.isAssignableFrom(clazz)) {
                    return new GenericMapConverter(TreeMap.class, keyConverter, valueConverter);
                }
                else if (ConcurrentMap.class.isAssignableFrom(clazz)) {
                    return new GenericMapConverter(ConcurrentHashMap.class, keyConverter, valueConverter);
                }
                else {
                    return new GenericMapConverter(LinkedHashMap.class, keyConverter, valueConverter);
                }
            }

            return null;
        }

        Converter converter = REGISTRY.get(clazz);

        // we're outta here if we got one.
        if (converter != null) {
            return converter;
        }

        Class[] declaredClasses = clazz.getDeclaredClasses();
        for (Class declaredClass : declaredClasses) {
            if (Converter.class.isAssignableFrom(declaredClass)) {
                try {
                    converter = (Converter) declaredClass.newInstance();
                    registerConverter(converter);

                    // try to get the converter from the registry... the converter
                    // created above may have been for another class
                    converter = REGISTRY.get(clazz);
                    if (converter != null) {
                        return converter;
                    }
                }
                catch (Exception e) {
                    // ignore
                }

            }
        }

        // nothing found
        return null;
    }

    /**
     * Locate a property editor for given class of object.
     *
     * @param type The target object class of the property.
     * @return The resolved editor, if any.  Returns null if a suitable editor could not be located.
     */
    private static PropertyEditor findEditor(final Type type) {
        assert type != null;
        Class clazz = toClass(type);

        // try to locate this directly from the editor manager first.
        PropertyEditor editor = PropertyEditorManager.findEditor(clazz);

        // we're outta here if we got one.
        if (editor != null) {
            return editor;
        }


        // it's possible this was a request for an array class.  We might not
        // recognize the array type directly, but the component type might be
        // resolvable
        if (clazz.isArray() && !clazz.getComponentType().isArray()) {
            // do a recursive lookup on the base type
            editor = findEditor(clazz.getComponentType());
            // if we found a suitable editor for the base component type,
            // wrapper this in an array adaptor for real use
            if (editor != null) {
                return new ArrayConverter(clazz, editor);
            }
        }

        // nothing found
        return null;
    }
}
