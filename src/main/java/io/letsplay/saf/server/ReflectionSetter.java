package io.letsplay.saf.server;

import java.beans.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;

public class ReflectionSetter {

    private final Object bean;

    public ReflectionSetter(Object bean) {
        this.bean = bean;
    }

    public void set(Map<String, Object> values) {
        try {

            setByReflection(values);

        } catch (IntrospectionException | InvocationTargetException | IllegalAccessException e) {
            throw new IllegalArgumentException("error during reflection", e);
        }
    }

    private void setByReflection(Map<String, Object> values) throws IntrospectionException, IllegalAccessException, InvocationTargetException {
        BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
        for (PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors()) {
            Object value = values.get(descriptor.getName());

            boolean hasMatchingValueInMap = value != null;
            if (hasMatchingValueInMap) {
                Method writeMethod = descriptor.getWriteMethod();
                Class<?> argumentType = writeMethod.getParameterTypes()[0];

                value = convertLongToIntIfNeeded(argumentType, value);

                value = convertLongToDateIfNeeded(argumentType, value);

                writeMethod.invoke(bean, value);
            }

        }
    }

    private Object convertLongToIntIfNeeded(Class<?> argumentType, Object value) {
        boolean needsInt = argumentType == Integer.class || argumentType == int.class;
        boolean isLong = value.getClass() == Long.class;

        if (needsInt && isLong) {
            Long longValue = (Long) value;
            return longValue.intValue();
        }

        return value;
    }

    private Object convertLongToDateIfNeeded(Class<?> argumentType, Object value) {
        boolean needsDate = argumentType == Date.class;
        boolean isLong = value.getClass() == Long.class;

        if (needsDate && isLong) {
            return new Date((Long) value);
        }

        return value;
    }
}
