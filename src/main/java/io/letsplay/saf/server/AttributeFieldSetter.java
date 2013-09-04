package io.letsplay.saf.server;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class AttributeFieldSetter {

    private final Object dto;
    private final Field field;

    public AttributeFieldSetter(Object dto, Field field) {
        this.dto = dto;
        this.field = field;
    }

    public void set(Object attribute) throws IllegalAccessException, InstantiationException {
        Object value = convertLongToIntIfNeeded(attribute);
        value = convertLongToDateIfNeeded(value);
        value = convertNestedIfNeeded(field.getType(), value);
        value = convertListIfNeeded(value);

        setField(value);
    }

    private Object convertLongToIntIfNeeded(Object attribute) {
        final Class<?> argumentType = field.getType();
        boolean needsInt = argumentType == Integer.class || argumentType == int.class;
        boolean isLong = attribute instanceof Long;

        if (needsInt && isLong) {
            Long longValue = (Long) attribute;
            return longValue.intValue();
        }

        return attribute;
    }

    private Object convertLongToDateIfNeeded(Object attribute) {
        final Class<?> argumentType = field.getType();
        boolean needsDate = argumentType == Date.class;
        boolean isLong = attribute instanceof Long;

        if (needsDate && isLong) {
            return new Date((Long) attribute);
        }

        return attribute;
    }

    @SuppressWarnings("unchecked")
    private Object convertNestedIfNeeded(Class<?> argumentType, Object attribute) throws IllegalAccessException, InstantiationException {
        if (attribute instanceof Map) {
            Object nestedDto = argumentType.newInstance();
            new AttributesClassSetter(nestedDto).set((Map<String, Object>) attribute);
            return nestedDto;
        }

        return attribute;
    }

    private Object convertListIfNeeded(Object attribute) throws IllegalAccessException, InstantiationException {
        if (attribute instanceof List<?>) {
            ListType annotation = field.getAnnotation(ListType.class);
            if (annotation != null) {
                return convertTypedList(annotation.value(), (List<?>) attribute);
            }
        }
        return attribute;
    }

    private Object convertTypedList(Class<?> listType, List<?> listOfAttributes) throws IllegalAccessException, InstantiationException {
        List<Object> nestedDto = new ArrayList<>();
        for (Object attribute : listOfAttributes) {
            nestedDto.add(convertNestedIfNeeded(listType, attribute));
        }
        return nestedDto;
    }

    private void setField(Object value) throws IllegalAccessException {
        field.set(dto, value);
    }

}
