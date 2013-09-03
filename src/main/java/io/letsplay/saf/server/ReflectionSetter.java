package io.letsplay.saf.server;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.Map;

public class ReflectionSetter {

    public void set(Object bean, Map<String, Object> valuesToSetFields) {
        try {
            for (Field fieldToSet : bean.getClass().getDeclaredFields()) {

                if (valuesToSetFields.containsKey(fieldToSet.getName())) {

                    convertValueIfNeededAndSetBeanField(bean, fieldToSet, valuesToSetFields.get(fieldToSet.getName()));
                }
            }

        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("error during reflection", e);
        }
    }

    private void convertValueIfNeededAndSetBeanField(Object bean, Field fieldToSet, Object valueToSetField)
            throws IllegalAccessException {
        fieldToSet.setAccessible(true);

        fieldToSet.set(bean,
                convertLongToIntIfNeeded(
                        fieldToSet.getType(),
                        convertLongToDateIfNeeded(fieldToSet.getType(), valueToSetField)
                )
        );
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
