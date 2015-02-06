/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Andoni del Olmo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.ado.github.minesync.github;

import org.junit.Before;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ClassTestCase<T> extends MockitoTestCase {

    protected T unitUnderTest;
    private List<Object> mockImplementations = new ArrayList<Object>();

    @Before
    public void createUnitUnderTest() {
        try {
            Class<?> aClass = Class.forName(getClassCanonicalName());
            Object o = aClass.newInstance();
            unitUnderTest = (T) o;
            applyMockImplementation();
        } catch (Exception e) {
            // ignore
        }
    }

    protected void createUnitUnderTest(T unitUnderTest) {
        this.unitUnderTest = unitUnderTest;
        applyMockImplementation();
    }

    protected void addMock(Object objectImplementation) {
        this.mockImplementations.add(objectImplementation);
    }

    private void applyMockImplementation() {
        for (Object mockImplementation : this.mockImplementations) {
            addMock(unitUnderTest, mockImplementation);
        }
    }

    protected void addMock(Object targetInstance, Object objectImplementation) {
        injectImplementation(targetInstance, objectImplementation);
    }

    protected void injectImplementation(Object objectImplementation) {
        injectImplementation(unitUnderTest, objectImplementation);
    }
    protected void injectImplementation(Object targetInstance, Object objectImplementation) {
        Field[] fields = findFields(targetInstance.getClass(), objectImplementation.getClass());
        if (fields.length == 0)
            throw new IllegalStateException(objectImplementation.getClass().getSimpleName()
                    + " cannot be applied to any field in " + targetInstance.getClass().getSimpleName() + ".");
        if (fields.length > 1)
            throw new IllegalStateException("Multiple fields targetInstance of "
                    + objectImplementation.getClass().getSimpleName()
                    + " found for "
                    + targetInstance.getClass().getSimpleName() + ".");
        setFieldValue(targetInstance, fields[0], objectImplementation);
    }

    private void setFieldValue(Object instance, Field field, Object value) {
        field.setAccessible(true);
        try {
            field.set(instance, value);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    private String getClassCanonicalName() {
        return getTypeForClassUnderTest().toString().replace("class ", "");
    }

    private Type getTypeForClassUnderTest() {
        return ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    private Field[] findFields(Class<?> targetClazz, Class<?> attributeType) {
        List<Field> fields = new ArrayList<Field>();
        for (Field field : targetClazz.getDeclaredFields()) {
            if (field.getType().isAssignableFrom(attributeType)) {
                fields.add(field);
            }
        }
        if (fields.size() == 0) {
            Class<?> superClass = targetClazz.getSuperclass();
            if (superClass != null && superClass != Object.class) {
                return findFields(superClass, attributeType);
            }
        }
        return fields.toArray(new Field[fields.size()]);
    }

    protected void invokeMethod(String methodName) {
        invokeMethod(methodName, null);
    }

    protected void invokeMethod(String methodName, Object[] parameters) {
        try {
            Class<?> c = unitUnderTest.getClass();
            for (Method method : c.getDeclaredMethods()) {
                if (methodName.equals(method.getName())) {
                    method.setAccessible(true);
                    method.invoke(unitUnderTest, parameters);
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException("Cannot invoke method "
                    + methodName, e);
        }
    }
}
