/**
 * created by: fox0lr2 created at: 23.10.2008, 09:50:29
 */
package net.ulrice.databinding.reflect;


import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.ulrice.databinding.modelaccess.impl.ReflectionMVAException;

/**
 * Utils for reflection.
 * 
 * @author fox0lr2
 */
public final class ReflectionUtils {

    public static final String THIS = "this";

    private static final String NORMAL_GETTER_PREFIX = "get";
    private static final String BOOLEAN_GETTER_PREFIX = "is";
    private static final String STANDARD_SETTER_PREFIX = "set";

    private static final String SEP_FIELD_DOT = "\\.";

    private static final String MAP_KEY_START = "(";
    private static final String MAP_KEY_END = ")";

    private static final Pattern MAP_SETTER_REGEX = Pattern.compile("\\w+\\" + MAP_KEY_START + "[\\w-]*" + "\\" + MAP_KEY_END);
    private static final Pattern MAP_REGEX_INT = Pattern.compile("\\w+\\[[\\w-]*\\]");


    private final Map<Class< ?>, Class< ?>[]> interfacesByClass = new HashMap<Class< ?>, Class< ?>[]>();
    private Map<Class< ?>, Class< ?>[]> superclassesByClass = new HashMap<Class< ?>, Class< ?>[]>();
    private final Map<Class< ? extends Enum< ?>>, Enum< ?>[]> enumConstantsByClass = new HashMap<Class< ? extends Enum< ?>>, Enum< ?>[]>();
    private final Map<Class< ?>, Field[]> fieldsByClass = new HashMap<Class< ?>, Field[]>();
    private final Map<Class< ?>, Map<String, Method[]>> methodsByClass = new HashMap<Class< ?>, Map<String, Method[]>>();
    private final Map<Class< ?>, Map<String, Class< ?>>> dotSeparatedFieldTypes = new HashMap<Class< ?>, Map<String, Class< ?>>>();
    private final Map<String, Boolean> getterNameWithAttributes = new HashMap<String, Boolean>();
    private final Map<String, Boolean> setterRegExMatch = new HashMap<String, Boolean>();

    private Set<Class<?>> immutableClassSet;

    private static final ReflectionUtils INSTANCE = new ReflectionUtils();

    private ReflectionUtils() {
    }

    /**
     * Get all fiels of the given class.
     * 
     * @param clazz of the fields
     * @return List with the filds
     */
    public Field[] findAllFieldsByClass(final Class< ?> clazz) {
        Field[] fields = fieldsByClass.get(clazz);
        
        if (fields == null) {
            fields = clazz.getDeclaredFields();
            Class< ?> superclass = clazz.getSuperclass();
            
            while (superclass != null) {
                final Field[] superClassFields = superclass.getDeclaredFields();
                final Field[] allFields = new Field[superClassFields.length + fields.length];
                System.arraycopy(fields, 0, allFields, 0, fields.length);
                System.arraycopy(superClassFields, 0, allFields, fields.length, superClassFields.length);
                superclass = superclass.getSuperclass();
                fields = allFields;
            }
            
            fieldsByClass.put(clazz, fields);
        }
        
        return fields;
    }

    /**
     * Returns a list with fields from the <code>class</code>, which are from type <code>type</code>.
     * 
     * @param clazz the class
     * @param typeClass the type of the fields
     * @return List with fields
     */
    public Field[] findAllFieldsByClassAndType(Class< ?> clazz, Class< ?> typeClass) {
        return findFieldsByClassAndFilter(clazz, new FieldTypeFieldFilter(typeClass));
    }

    /**
     * checks if a a class implementing a interface.
     * 
     * @param baseClass the class to check
     * @param interfaceToImplement the interface check for
     * @return true if the class implements the interface, false if not
     */
    public boolean isImplementingInterface(Class< ?> baseClass, Class< ?> interfaceToImplement) {
        Class< ?>[] interfaces = interfacesByClass.get(baseClass);
        if (interfaces == null) {
            interfaces = getInterfaces(baseClass);
        }
        for (final Class< ?> iface : interfaces) {
            if (iface == interfaceToImplement) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the interfaces.
     * 
     * @param baseClass the base class
     * @return the interfaces
     */
    private Class< ?>[] getInterfaces(Class< ?> baseClass) {
        Class< ?>[] interfaces = baseClass.getInterfaces();
        for (final Class< ?> aIface : interfaces) {
            final Class< ?>[] interfaces2 = getInterfaces(aIface);
            if (interfaces2.length > 0) {
                final Class< ?>[] tmpInterfaces = new Class[interfaces.length + interfaces2.length];
                System.arraycopy(interfaces, 0, tmpInterfaces, 0, interfaces.length);
                System.arraycopy(interfaces2, 0, tmpInterfaces, interfaces.length, interfaces2.length);
                interfaces = tmpInterfaces;
            }
        }
        interfacesByClass.put(baseClass, interfaces);
        return interfaces;
    }

    /**
     * checks if a a class is of a given type (itself or one of its superclasses).
     * 
     * @param baseClass the class to check
     * @param typeToCheck the type to check for
     * @return true if the class or one of its superclasses is typeToCheck, false if not
     */
    public boolean isType(Class< ?> baseClass, Class< ?> typeToCheck) {
        if (baseClass == null) {
            throw new IllegalArgumentException("baseClass is null");
        }
        if (baseClass.equals(typeToCheck)) {
            return true;
        }

        Class< ?>[] superclasses = superclassesByClass.get(baseClass);
        if (superclasses == null) {
            final List<Class< ?>> superclassList = new ArrayList<Class< ?>>();
            Class< ?> superclass = baseClass.getSuperclass();
            while (superclass != null) {
                superclassList.add(superclass);
                superclass = superclass.getSuperclass();
            }

            superclasses = superclassList.toArray(new Class[superclassList.size()]);
            superclassesByClass.put(baseClass, superclasses);
        }

        for (final Class< ?> type : superclasses) {
            if (type.equals(typeToCheck)) {
                return true;
            }
        }
        return false;
    }

    /**
     * find a Enum value by name.
     * 
     * @param <T> -
     * @param aEnumClass the enum class
     * @param name the name of the enum constant
     * @return the enum constant if exists
     */
    public <T extends Enum< ?>> T findEnumValue(Class<T> aEnumClass, String name) {
        final T[] enumConstants = findEnumConstants(aEnumClass);
        for (final T aEnum : enumConstants) {
            if (aEnum.name().equals(name)) {
                return aEnum;
            }
        }
        return null;
    }

    /**
     * find all enum constats for the enum.
     * 
     * @param <T> -
     * @param aEnumClass a enum find the enum constants for
     * @return the enum
     */
    @SuppressWarnings("unchecked")
    public <T extends Enum< ?>> T[] findEnumConstants(Class<T> aEnumClass) {
        T[] enumConstants = (T[]) enumConstantsByClass.get(aEnumClass);
        if (enumConstants == null) {
            enumConstants = aEnumClass.getEnumConstants();
            enumConstantsByClass.put(aEnumClass, enumConstants);
        }
        return enumConstants;
    }

    /**
     * Get the instance.
     * 
     * @return ReflectionUtils instance
     */
    public static ReflectionUtils getInstance() {
        return INSTANCE;
    }

    /**
     * find all fields with the given annotation.
     * 
     * @param baseClass the class searching for fields
     * @param annotationClass the annotation class
     * @return a array with the fields
     */
    public Field[] findAllFieldsByClassAndAnnotation(Class< ?> baseClass, Class< ? extends Annotation> annotationClass) {
        return findFieldsByClassAndFilter(baseClass, new AnnotationFieldFilter(annotationClass));
    }
    
    public static final List<String> findAllFieldNamesFromClassWithAnnotation(Class<?> cl, Class< ? extends Annotation> annotationClass) {
        Field[] fields = ReflectionUtils.getInstance().findAllFieldsByClassAndAnnotation(cl, annotationClass);
        
        List<String> result = new ArrayList<String>();
        for (Field field : fields) {
            result.add(field.getName());
        }
        
        return result;
    }

    /**
     * Returning the field of class clazz with the name fieldName.
     * 
     * @param clazz the class searching for the field
     * @param fieldName the fieldname
     * @return the field or null if not exists
     */
    public Field findFieldByClassAndName(Class< ?> clazz, String fieldName) {
        final Field[] fieldsByClassAndName = findFieldsByClassAndFilter(clazz, new NameFieldFilter(fieldName));

        if (fieldsByClassAndName.length != 1) {
            if (clazz.getSuperclass() == null) {
                return null;
            }
            return findFieldByClassAndName(clazz.getSuperclass(), fieldName);
        }
        return fieldsByClassAndName[0];
    }

    /**
     * Returning the field of class clazz with the name fieldName with support for nested elements by .-notation
     * 
     * @param aObject the object getting the value from
     * @param aFieldName the fieldname, nested path with .-notation
     * @param isNested use nested support
     * @return the field or null if not exists
     * @throws InvocationTargetException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     */
    public Object getPropertyValue(Object aObject, String aFieldName, boolean isNested) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        return getPropertyValue(aObject, aFieldName, isNested, true);
    }

    /**
     * Returning the field of class clazz with the name fieldName with support for nested elements by .-notation
     * 
     * @param aObject the object getting the value from
     * @param aFieldName the fieldname, nested path with .-notation
     * @param isNested use nested support
     * @param ignoreNull Ignores null-values somewhere in the path, if false an exception is thrown.
     * @return the field or null if not exists
     * @throws InvocationTargetException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     */
    public Object getPropertyValue(Object aObject, String aFieldName, boolean isNested, boolean ignoreNull) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        if (aFieldName == null || aFieldName.isEmpty()) {
            return aObject;
        }

        if (isNested) {
            final StringTokenizer st = new StringTokenizer(aFieldName, ".");
            Object object = aObject;
            while(st.hasMoreTokens()) {
                object = getPropertyValue(object, st.nextToken());

                if (object == null && ignoreNull) {
                    return null;
                }
            }
            return object;
        }
        return getPropertyValue(aObject, aFieldName);
    }
    
    private Boolean isGetterNameWithAttribute(final String key, final String fieldName) {
        if (getterNameWithAttributes.containsKey(key)) {
            return getterNameWithAttributes.get(key);
        }
        final boolean isGetterNameWithAttribute = matches(fieldName, MAP_SETTER_REGEX) || matches(fieldName, MAP_REGEX_INT);
        getterNameWithAttributes.put(key, isGetterNameWithAttribute);
        return isGetterNameWithAttribute;
    }
    
    private Boolean isSetterRegExMatch(final String key, final String fieldName) {
        if (setterRegExMatch.containsKey(key)) {
            return setterRegExMatch.get(key);
        }
        final boolean isSetterRegExMatch = matches(fieldName, MAP_SETTER_REGEX);
        setterRegExMatch.put(key, isSetterRegExMatch);
        return isSetterRegExMatch;
    }
    
    /**
     * return the value of the field fieldName from the object object.
     * 
     * @param object the object to find the value from
     * @param fieldName the fieldname
     * @return the value
     * @throws InvocationTargetException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     */
    public Object getPropertyValue(Object object, String fieldName) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        if (object == null) {
            throw new IllegalArgumentException("object is null");
        }

        if (THIS.equals(fieldName)) {
            return object;
        }

        String getterNameForField = NORMAL_GETTER_PREFIX + ReflectionUtils.capitalizeFirstLetter(fieldName);
        final Class< ? extends Object> classOfObject = object.getClass();
        Method[] methods = getMethodsByClassAndName(getterNameForField, classOfObject);

        if (methods == null || methods.length == 0) {
            getterNameForField = BOOLEAN_GETTER_PREFIX + ReflectionUtils.capitalizeFirstLetter(fieldName);
            methods = getMethodsByClassAndName(getterNameForField, classOfObject);
        }

        if (methods == null) {
            final Field field = findFieldByClassAndName(classOfObject, fieldName);
            
            if (field != null) {
                return getValueFromField(object, field);
            }
            
            final int keyBeginIndex = getKeyBeginIndex(fieldName);
            final String key = getFieldKey(classOfObject, fieldName, keyBeginIndex);

            if (isGetterNameWithAttribute(key, fieldName)) {

                final int keyEndIndex;
                final Object keyParam;

                if (isSetterRegExMatch(key, fieldName)) {
                    keyEndIndex = fieldName.lastIndexOf(MAP_KEY_END);
                    keyParam = fieldName.substring(keyBeginIndex + 1, keyEndIndex);
                }
                else {
                    keyEndIndex = fieldName.lastIndexOf(']');
                    keyParam = Integer.parseInt(fieldName.substring(keyBeginIndex + 1, keyEndIndex));
                }
                final String methodName = fieldName.substring(0, keyBeginIndex);

                final Method[] mapGetMethods = getMethodsByClassAndName(NORMAL_GETTER_PREFIX + ReflectionUtils.capitalizeFirstLetter(methodName), classOfObject);
                if (mapGetMethods != null) {
                    for (final Method method : mapGetMethods) {
                        if (method.getParameterTypes().length == 1) {
                            return method.invoke(object, new Object[] { keyParam });
                        }
                    }
                }
            }
        }
        else {
            for (final Method method : methods) {
                if (method.getParameterTypes().length == 0) {                    
                    return method.invoke(object);
                }
            }
        }

        throw new IllegalArgumentException(fieldName);
    }

    private int getKeyBeginIndex(String fieldName) {
        final int getterBeginIndex = fieldName.lastIndexOf('[');
        if (getterBeginIndex >= 0) {
            return getterBeginIndex;
        }
        
        final int setterKeyBeginIndex = fieldName.lastIndexOf(MAP_KEY_START);
        if (setterKeyBeginIndex >= 0) {
            return setterKeyBeginIndex;
        }
        
        return -1;
    }

    private String getFieldKey(final Class< ? extends Object> classOfObject, String fieldName, final int beginIndex) {
        if (beginIndex >= 0) {
            return classOfObject.getName() + fieldName.substring(0, beginIndex);
        }
        
        return classOfObject.getName() + fieldName;
    }

    private Object getValueFromField(final Object object, final Field field) throws IllegalArgumentException, IllegalAccessException {
        field.setAccessible(true);
        return field.get(object);
    }

    /**
     * Gets the methods by class and name.
     * 
     * @param methodName the method name
     * @param clazz the clazz
     * @return the methods by class and name
     */
    private Method[] getMethodsByClassAndName(String methodName, Class< ? extends Object> clazz) {
        return getMethodsByClass(clazz).get(methodName);
    }

    /**
     * Gets the methods by class.
     * 
     * @param clazz the clazz
     * @return the methods by class
     */
    private Map<String, Method[]> getMethodsByClass(final Class< ? extends Object> clazz) {
        Map<String, Method[]> methodsForClass = this.methodsByClass.get(clazz);
        if (methodsForClass == null) {
            methodsForClass = new HashMap<String, Method[]>();
            this.methodsByClass.put(clazz, methodsForClass);
            final Method[] declaredMethods = clazz.getDeclaredMethods();
            for (final Method method : declaredMethods) {
                final String localMethodName = method.getName();
                final Method[] methods = methodsForClass.get(localMethodName);
                if (methods == null) {
                    methodsForClass.put(localMethodName, new Method[] { method });
                }
                else {
                    final Method[] newMethods = new Method[methods.length + 1];
                    System.arraycopy(methods, 0, newMethods, 0, methods.length);
                    newMethods[methods.length] = method;
                    methodsForClass.put(localMethodName, newMethods);
                }
            }
            if (clazz.getSuperclass() != null) {
                final Class< ? extends Object> superclass = clazz.getSuperclass();
                final Map<String, Method[]> superclassMethods = getMethodsByClass(superclass);
                for (final Entry<String, Method[]> entry : superclassMethods.entrySet()) {
                    final String methodName = entry.getKey();
                    Method[] methods = methodsForClass.get(methodName);
                    Method[] superclassMethodsWithName = entry.getValue();

                    methods = methods == null ? new Method[0] : methods;
                    superclassMethodsWithName = superclassMethodsWithName == null ? new Method[0] : superclassMethodsWithName;

                    final Method[] newMethods = new Method[methods.length + superclassMethodsWithName.length];
                    System.arraycopy(methods, 0, newMethods, 0, methods.length);
                    System.arraycopy(superclassMethodsWithName, 0, newMethods, methods.length, superclassMethodsWithName.length);
                    methodsForClass.put(methodName, newMethods);
                }
            }
        }
        return methodsForClass;
    }

    public Class< ?> getDotSeparatedFieldType(Class< ?> rootClass, String dotSeparatedFieldString) {
        if (THIS.equals(dotSeparatedFieldString)) {
            return rootClass;
        }

        Map<String, Class< ?>> fieldMap = dotSeparatedFieldTypes.get(rootClass);

        if (fieldMap == null) {
            dotSeparatedFieldTypes.put(rootClass, new HashMap<String, Class< ?>>());
            fieldMap = dotSeparatedFieldTypes.get(rootClass);
        }

        Class< ?> type = fieldMap.get(dotSeparatedFieldString);

        if (type == null) {
            final String[] fieldNames = dotSeparatedFieldString.split(SEP_FIELD_DOT);
            Class< ?> currentClass = rootClass;
            for (int i = 0; i < fieldNames.length; i++) {
                currentClass = findPropertyTypeByReflection(currentClass, fieldNames[i]);
            }
            type = currentClass;
            fieldMap.put(dotSeparatedFieldString, type);
        }

        return type;
    }
    
    public Field getFieldByReflection(Class<?> rootClass, String path) {
        
        String[] pathArr = path.split(SEP_FIELD_DOT);
        Class<?> currentClass = rootClass;
        Field field = null;
        try {
            for (String pathElement : pathArr) {
                field = getFieldInHierarchy(currentClass, pathElement);
                currentClass = field.getType();
            }               
        } catch (IllegalArgumentException e) {
            throw new ReflectionMVAException("Could not read object from path: " + path, e);
        } catch (SecurityException e) {
            throw new ReflectionMVAException("Could not read object from path: " + path, e);
        }
        return field;
    }   
    
    public Field getFieldInHierarchy (Class<?> cls, String fieldName) {
        try {
            return getFieldInHierarchy (cls, cls, fieldName);
        }
        catch (NoSuchFieldException e) {
            throw new ReflectionMVAException("There is no field " + fieldName + " in " + cls.getName() + " or its subclasses", e);
        }
    }
    
    private Field getFieldInHierarchy (Class<?> cls, Class<?> originalCls, String fieldName) throws NoSuchFieldException {
        try {
            Field result = cls.getDeclaredField(fieldName);
            setAccessible(result);
            return result;
        } catch (SecurityException e) {
            throw new ReflectionMVAException("Security exception while accessing field " + cls.getName() + "." + fieldName + ".", e);
        } catch (NoSuchFieldException e) {
            if (cls.getSuperclass() == null) {
                throw e;
            }
            return getFieldInHierarchy(cls.getSuperclass(), originalCls, fieldName);
        }
    }

    public Class< ?> getParentClassForDotSeparatedFieldType(final Class< ?> rootClass,
        final String dotSeparatedFieldString) {
        Class< ?> currentClass = rootClass;
        final String[] fieldNames = dotSeparatedFieldString.split(SEP_FIELD_DOT);

        for (int i = 0; i < fieldNames.length - 1; i++) {
            currentClass = findPropertyTypeByReflection(currentClass, fieldNames[i]);
        }
        return currentClass;
    }

    /**
     * Determines the type of a given Field, returning the type paramter for collections.
     */
    public Class< ?> findPropertyTypeByReflection(final Class< ?> baseClass, final String fieldName) {
        if (THIS.equals(fieldName)) {
            return baseClass;
        }

        final Field field = findFieldByClassAndName(baseClass, fieldName);
        if (field == null) {
            if (matches(fieldName, MAP_SETTER_REGEX)) {
                final int keyBeginIndex = fieldName.indexOf(MAP_KEY_START);
                final String methodName = fieldName.substring(0, keyBeginIndex);

                final Method[] mapGetMethods = getMethodsByClassAndName(NORMAL_GETTER_PREFIX + ReflectionUtils.capitalizeFirstLetter(methodName), baseClass);
                if (mapGetMethods != null) {
                    for (final Method method : mapGetMethods) {
                        if (method.getParameterTypes().length == 1) {
                            return method.getReturnType();
                        }
                    }
                }
            }

            // If the field was not found. Try to find the getter.
            final String getterNameForField = NORMAL_GETTER_PREFIX + ReflectionUtils.capitalizeFirstLetter(fieldName);
            final Method[] methods = getMethodsByClassAndName(getterNameForField, baseClass);

            if (methods == null || methods.length == 0) {
                throw new IllegalStateException("Can not find property " + fieldName + " in class " + baseClass + " by reflection");
            }

            for (final Method method : methods) {
                // Search for a method with 0 parameters.
                if (method.getParameterTypes() != null && method.getParameterTypes().length == 0) {
                    return method.getReturnType();
                }
            }
        }
        if (field == null) {
            throw new IllegalStateException("Field '" + fieldName + "' not found in class '" + baseClass + "'.");
        }

        // use generic type parameter for collections if available
        if (Collection.class.isAssignableFrom(field.getType())) {
            if (field.getGenericType() instanceof ParameterizedType && ((ParameterizedType) field.getGenericType()).getActualTypeArguments().length == 1) {
                final Type typeParam = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];

                if (typeParam instanceof Class) {
                    return (Class< ?>) typeParam;
                }
                if (typeParam instanceof ParameterizedType && ((ParameterizedType) typeParam).getRawType() instanceof Class) {
                    return (Class< ?>) ((ParameterizedType) typeParam).getRawType();
                }
            }
            else {
                throw new RuntimeException("could not determine element type of collection " + baseClass.getName() + "." + field + ".");
            }
        }

        return field.getType();
    }

    public Type findGenericTypeByReflection(final Class< ?> baseClass, final String fieldName) {
        final Field field = findFieldByClassAndName(baseClass, fieldName);

        if (field != null) {
            return field.getGenericType();
        }

        if (matches(fieldName, MAP_SETTER_REGEX)) {
            final int keyBeginIndex = fieldName.indexOf(MAP_KEY_START);
            final String methodName = fieldName.substring(0, keyBeginIndex);

            final Method[] mapGetMethods = getMethodsByClassAndName(NORMAL_GETTER_PREFIX + ReflectionUtils.capitalizeFirstLetter(methodName), baseClass);
            if (mapGetMethods != null) {
                for (final Method method : mapGetMethods) {
                    if (method.getParameterTypes().length == 1) {
                        return method.getGenericReturnType();
                    }
                }
            }
        }

        // If the field was not found. Try to find the getter.
        final String getterNameForField = NORMAL_GETTER_PREFIX + ReflectionUtils.capitalizeFirstLetter(fieldName);
        final Method[] methods = getMethodsByClassAndName(getterNameForField, baseClass);

        if (methods == null || methods.length == 0) {
            throw new IllegalArgumentException("Can not find property " + fieldName + " in class " + baseClass + " by reflection");
        }
        for (final Method method : methods) {
            // Search for a method with 0 parameters.
            if (method.getParameterTypes() != null && method.getParameterTypes().length == 0) {
                return method.getGenericReturnType();
            }
        }

        throw new IllegalArgumentException("Can not find property " + fieldName + " in class " + baseClass + " by reflection");
    }

    /**
     * find all fields of the class by a filter.
     * 
     * @param aClass Class to use
     * @param filter a filter
     * @return a array of fields
     */
    public Field[] findFieldsByClassAndFilter(Class< ?> aClass, FieldFilter filter) {
        final List<Field> result = new ArrayList<Field>();
        final Field[] allFieldsByClass = findAllFieldsByClass(aClass);
        for (final Field aField : allFieldsByClass) {
            if (filter.accept(aField)) {
                result.add(aField);
            }
        }
        return result.toArray(new Field[result.size()]);
    }

    /**
     * Creates a simple copy of the Object using {@link BeanUtils}. For collections annotated with {@link CascadeCopyAndCompare} 
     * this function makes a deep copy for this collection.
     * The object must stick to the beans convention - It must have a default
     * constructor and getter/setter methods for the properties which should be copied.
     * 
     * @param <T> -
     * @param aObject the object to copy
     * @return the copy
     * @throws InstantiationException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     */
    @SuppressWarnings("unchecked")
    public <T> T createCopy(final T aObject) throws IllegalArgumentException, IllegalAccessException, InstantiationException {
        if (aObject == null) {
            return null;
        }
        final Class< ? extends Object> classOfObject = aObject.getClass();
        
        
        
        if(isKnownAsImmutable(classOfObject)) {
            return aObject;
        }
        
        if (isImplementingInterface(classOfObject, List.class)) {
            final List<Object> listToCopy = (List<Object>) aObject;
            final List<Object> copyOfList = new ArrayList<Object>(listToCopy.size());
            copyCollectionElements(listToCopy, copyOfList);
            return (T) copyOfList;
        }
        if (isImplementingInterface(classOfObject, Set.class)) {
            final Set<Object> setToCopy = (Set<Object>) aObject;
            final Set<Object> copyOfSet = new HashSet<Object>(setToCopy.size());
            copyCollectionElements(setToCopy, copyOfSet);
            return (T) copyOfSet;
        }
        if (isImplementingInterface(classOfObject, Collection.class)) {
            final Collection<Object> collectionToCopy = (Collection<Object>) aObject;
            final Collection<Object> copyOfCollection = new ArrayList<Object>(collectionToCopy.size());
            copyCollectionElements(collectionToCopy, copyOfCollection);
            return (T) copyOfCollection;
        }
        if (classOfObject.isArray()) {
            int length = Array.getLength(aObject);
            T clone = (T) Array.newInstance(classOfObject, length);
            for( int i = 0; i < length; i++) {
                Array.set(clone, i, createCopy(Array.get(aObject, i)));                
            }
        }
        if (isImplementingInterface(classOfObject, Cloneable.class)) {
            Method[] cloneMethod = getMethodsByClassAndName("clone", classOfObject);
            try {
                cloneMethod[0].setAccessible(true);
                return ((T) cloneMethod[0].invoke(aObject));
            }
            catch (InvocationTargetException e) {
                throw new IllegalAccessException("Error invoking clone method on object of class " + classOfObject); 
            }
        }
        if (isImplementingInterface(classOfObject, Map.class)) {
            final Map<Object, Object> mapToCopy = (Map<Object, Object>) aObject;
            final Map<Object, Object> copyOfMap = new HashMap<Object, Object>(mapToCopy.size());
            for (final Entry<Object, Object> obj : mapToCopy.entrySet()) {
                copyOfMap.put(obj.getKey(), obj.getValue());
            }
            return (T) copyOfMap;
        }
        
        return copySimpleObject(aObject);
    }

    private boolean isKnownAsImmutable(Class< ? extends Object> classOfObject) {
        if(immutableClassSet == null) {
            immutableClassSet = new HashSet<Class<?>>();
            immutableClassSet.add(String.class);
            immutableClassSet.add(Integer.class);
            immutableClassSet.add(Long.class);
            immutableClassSet.add(Boolean.class);
            immutableClassSet.add(Class.class);
            immutableClassSet.add(Float.class);
            immutableClassSet.add(Double.class);
            immutableClassSet.add(Character.class);
            immutableClassSet.add(Byte.class);
            immutableClassSet.add(Short.class);
            immutableClassSet.add(Void.class);

            immutableClassSet.add(BigDecimal.class);
            immutableClassSet.add(BigInteger.class);
            immutableClassSet.add(URI.class);
            immutableClassSet.add(URL.class);
            immutableClassSet.add(UUID.class);
            immutableClassSet.add(Pattern.class);            
        }
        
        return (immutableClassSet.contains(classOfObject));
    }

    private void copyCollectionElements(Collection< ?> listToCopy, Collection<Object> copyOfList) {
        for (Object obj : listToCopy) {
            copyOfList.add(obj);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T copySimpleObject(final T aObject) throws IllegalArgumentException, IllegalAccessException, InstantiationException {
        T newInstance = null;
        final Class< ? extends Object> classOfObject = aObject.getClass();
        
        if (classOfObject.isEnum()) {
        	return aObject;
        }

        newInstance = (T) classOfObject.newInstance();

        //BeanUtils.copyProperties(aObject, newInstance);
        final Field[] fieldsToDeepCopy = findAllFieldsByClass(classOfObject);
        for (final Field field : fieldsToDeepCopy) {
            field.setAccessible(true);
            field.set(newInstance, createCopy(field.get(aObject)));
        }
        return newInstance;
    }
    
    public void setAccessible(final Field field) {
        if (!field.isAccessible()) {
            AccessController.doPrivileged(new PrivilegedAction<Object>() {
                @Override
                public Object run() {
                    field.setAccessible(true);
                    return null;
                }
            });
        }
    }

    public Annotation[] getAllAnnotationsOnPath(final Class< ?> rootClass, final String dotSeparatedPath) {
        Class< ?> currentClass = rootClass;
        final String[] fieldNames = dotSeparatedPath.split(SEP_FIELD_DOT);

        final List<Annotation> result = new ArrayList<Annotation>();

        for (int i = 0; i < fieldNames.length; i++) {
            result.addAll(Arrays.asList(getAnnotationsForAttribute(currentClass, fieldNames[i])));
            currentClass = findPropertyTypeByReflection(currentClass, fieldNames[i]);
        }
        return result.toArray(new Annotation[0]);
    }

    /**
     * Return the annotations of a field.
     * 
     * @param containingClass The class in which the attribute is contained
     * @param attributeName The name of the attribute
     * @return An array of annotations.
     */
    public Annotation[] getAnnotationsForAttribute(Class< ?> containingClass, String attributeName) {

        final Field field = findFieldByClassAndName(containingClass, attributeName);
        
        if (field != null) {
            return field.getAnnotations();
        }
        
        final String getterNameForField = NORMAL_GETTER_PREFIX + ReflectionUtils.capitalizeFirstLetter(attributeName);
        final Method[] methods = getMethodsByClassAndName(getterNameForField, containingClass);
        
        if (methods != null) {
            Annotation[] annotations = new Annotation[0];
            for (final Method method : methods) {
                // get first method which name matches and get annotations
                annotations = method.getAnnotations();
                break;
            }
            return annotations;
        }

        return new Annotation[0];
    }

    /**
     * Returns the deepest instance of an annotation from a path.
     */
    public <T extends Annotation> T getAnnotationFromPath(Class< ?> containingClass, String dotSeparatedPath,
        Class<T> annotationClass) {
        Class< ?> currentClass = containingClass;
        final String[] fieldNames = dotSeparatedPath.split(SEP_FIELD_DOT);

        T result = null;
        for (int i = 0; i < fieldNames.length; i++) {
            result = getAnnotationForAttribute(currentClass, fieldNames[i], annotationClass);
            currentClass = findPropertyTypeByReflection(currentClass, fieldNames[i]);
        }
        return result;
    }

    /**
     * Returns a specific annotation from a field.
     */
    public <T extends Annotation> T getAnnotationForAttribute(Class< ?> containingClass, String attributeName, Class<T> annotationClass) {

        final Field field = findFieldByClassAndName(containingClass, attributeName);
        if (field != null) {
            return field.getAnnotation(annotationClass);
        }
        final String getterNameForField = NORMAL_GETTER_PREFIX + ReflectionUtils.capitalizeFirstLetter(attributeName);
        final Method[] methods = getMethodsByClassAndName(getterNameForField, containingClass);
        if (methods != null) {
            for (final Method method : methods) {
                // get first method which name matches and get annotations
                return method.getAnnotation(annotationClass);
            }
            return null;
        }

        return null;
    }

    /**
     * Sets the property value.
     * 
     * @param aObject the a object
     * @param aFieldName the a field name
     * @param value the value
     * @param isNested the is nested
     * @param createAttributes the create attributes
     * @throws InvocationTargetException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws InstantiationException 
     */
    public void setPropertyValue(final Object aObject, final String aFieldName, final Object value,
        final boolean isNested, final boolean createAttributes) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if (isNested) {
            String[] fieldNames = aFieldName.split("\\.");
            Object curObject = aObject;
            for (int i = 0; i < fieldNames.length - 1; i++) {
                Object oldObject = curObject;
                curObject = getPropertyValue(curObject, fieldNames[i]);
                if (curObject == null && createAttributes) {
                    if (value != null) {
                        curObject = createInstance(oldObject, fieldNames[i]);
                    }
                    else {
                        return;
                    }
                }
            }
            setPropertyValue(curObject, fieldNames[fieldNames.length - 1], value);
        }
        else {
            setPropertyValue(aObject, aFieldName, value);
        }
    }

    /**
     * Creates the instance.
     * 
     * @param object the object
     * @param fieldName the field name
     * @return the object
     * @throws IllegalAccessException 
     * @throws InstantiationException 
     * @throws InvocationTargetException 
     * @throws IllegalArgumentException 
     */
    private Object createInstance(final Object object, final String fieldName) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        final Class< ?> objectClass = object.getClass();
        final Class< ?> propertyType = findPropertyTypeByReflection(objectClass, fieldName);
        final Object tmpInstance = propertyType.newInstance();
        setPropertyValue(object, fieldName, tmpInstance);
        return tmpInstance;
    }

    /**
     * Sets the property value.
     * 
     * @param aObject the a object
     * @param aFieldName the a field name
     * @param value the value
     * @param isNested the is nested
     * @throws InstantiationException 
     * @throws InvocationTargetException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     */
    public void setPropertyValue(Object aObject, String aFieldName, Object value, boolean isNested) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException {
        setPropertyValue(aObject, aFieldName, value, isNested, false);
    }

    public void setPropertyValue(final Object object, final String fieldName, final Object value) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        boolean isCalledByMappingClassesAndObjectAndValueAreSame = fieldName.equals("") && object == value;
        if (isCalledByMappingClassesAndObjectAndValueAreSame) {
            return;
        }

        final String setterNameForField = STANDARD_SETTER_PREFIX + ReflectionUtils.capitalizeFirstLetter(fieldName);
        final Method[] methods = getMethodsByClassAndName(setterNameForField, object.getClass());
        final boolean standardSetterForTheObjectClassMayExist = methods != null;
        boolean mapSetterForTheObjectClassExists = matches(fieldName, MAP_SETTER_REGEX);

        if (standardSetterForTheObjectClassMayExist) {
            setFieldValueToObjectViaSetter(fieldName, value, object, methods);
        }

        else if (mapSetterForTheObjectClassExists) {
            setFieldValueToObjectViaMapSetter(fieldName, value, object);
        }

        else {
            setFieldValueToObjectDirectly(fieldName, value, object);
        }
    }

    private void setFieldValueToObjectViaMapSetter(final String fieldName, final Object value, final Object object) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        final int keyBeginIndex = fieldName.indexOf(MAP_KEY_START);
        final int keyEndIndex = fieldName.lastIndexOf(MAP_KEY_END);

        final String methodNameWithoutParameters = fieldName.substring(0, keyBeginIndex);
        final String keyParam = fieldName.substring(keyBeginIndex + 1, keyEndIndex);

        final String setterNameWithoutParameters =
                STANDARD_SETTER_PREFIX + ReflectionUtils.capitalizeFirstLetter(methodNameWithoutParameters);
        Method mapSetterMethod = findMapSetterMethod(setterNameWithoutParameters, object.getClass(), fieldName);
        invokeMethod(fieldName, object, new Object[] { keyParam, value }, mapSetterMethod);
    }

    private Method findMapSetterMethod(String setterNameWithoutParameters, Class< ? extends Object> objectClass,
        String fieldName) {
        final Method[] specialMethods = getMethodsByClassAndName(setterNameWithoutParameters, objectClass);
        if (specialMethods == null) {
            throw new IllegalStateException("Map setter method for " + fieldName + " does not exist.");
        }

        for (final Method method : specialMethods) {
            boolean methodHasTwoParameters = method.getParameterTypes().length == 2;
            if (methodHasTwoParameters) {
                return method;
            }
        }

        throw new IllegalStateException("Map setter method for " + fieldName
            + " must have key and value arguments.");
    }

    private Field findField(final String fieldName, final Class< ? extends Object> objectClass) {
        final Field field = findFieldByClassAndName(objectClass, fieldName);
        if (field == null) {
            throw new IllegalArgumentException("Field '" + fieldName + "' does not exist.");
        }
        return field;
    }

    private Method findStandardSetterMethod(Method[] methods, String fieldName) {
        for (final Method method : methods) {
            boolean methodHasOneParameter = method.getParameterTypes().length == 1;
            if (methodHasOneParameter) {
                return method;
            }
        }

        throw new IllegalStateException("Standard setter method for " + fieldName + " does not exist.");
    }

    private void setFieldValueToObjectDirectly(String fieldName, Object value, Object objectToSetValue) throws IllegalArgumentException, IllegalAccessException {
        final Field field = findField(fieldName, objectToSetValue.getClass());
        field.setAccessible(true);
        field.set(objectToSetValue, value);
    }

    private void setFieldValueToObjectViaSetter(String fieldName, Object value, Object object,
        Method[] methodsThatMayContainStandardSetterMethods) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {

        Method standardSetterMethod = findStandardSetterMethod(methodsThatMayContainStandardSetterMethods, fieldName);
        invokeMethod(fieldName, object, new Object[] { value }, standardSetterMethod);
    }

    private void invokeMethod(String fieldName, Object object, Object[] arguments, Method standardSetterMethod) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        standardSetterMethod.invoke(object, arguments);
    }

    private static boolean matches(final CharSequence input, final Pattern pattern) {
        final Matcher m = pattern.matcher(input);
        return m.matches();
    }
    
    
    private static String capitalizeFirstLetter(String text) {
        return "" + Character.toUpperCase(text.charAt(0)) + text.substring(1);
    }
}
