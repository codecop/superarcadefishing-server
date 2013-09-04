package io.letsplay.saf.server;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class AttributesDtoSetter {

    public void set(Object dto, Map<String, Object> attributes) {
        try {
            for (Field fieldToSet : dto.getClass().getDeclaredFields()) {

                if (attributes.containsKey(fieldToSet.getName())) {

                    convertValueIfNeededAndSetBeanField(dto, fieldToSet, attributes.get(fieldToSet.getName()));
                }
            }

        } catch (IllegalAccessException | InstantiationException e) {
            throw new IllegalArgumentException("error during reflection", e);
        }
    }

    private void convertValueIfNeededAndSetBeanField(Object dto, Field fieldToSet, Object attribute)
            throws IllegalAccessException, InstantiationException {
        fieldToSet.set(dto,
                convertListIfNeeded(fieldToSet,
                convertNestedIfNeeded(fieldToSet.getType(),
                        convertLongToIntIfNeeded(
                                fieldToSet.getType(),
                                convertLongToDateIfNeeded(fieldToSet.getType(), attribute)
                        )
                )));
    }

    private Object convertLongToIntIfNeeded(Class<?> argumentType, Object attribute) {
        boolean needsInt = argumentType == Integer.class || argumentType == int.class;
        boolean isLong = attribute instanceof Long;

        if (needsInt && isLong) {
            Long longValue = (Long) attribute;
            return longValue.intValue();
        }

        return attribute;
    }

    private Object convertLongToDateIfNeeded(Class<?> argumentType, Object attribute) {
        boolean needsDate = argumentType == Date.class;
        boolean isLong = attribute instanceof Long;

        if (needsDate && isLong) {
            return new Date((Long) attribute);
        }

        return attribute;
    }

    private Object convertNestedIfNeeded(Class<?> argumentType, Object attribute) throws IllegalAccessException, InstantiationException {
        if (attribute instanceof Map) {
            Object nestedDto = argumentType.newInstance();
            set(nestedDto, (Map<String, Object>) attribute);
            return nestedDto;
        }

        return attribute;
    }

    private Object convertListIfNeeded(Field field, Object attribute) throws IllegalAccessException, InstantiationException {
        if (attribute instanceof Iterable<?>) {
            List<Object> nestedDto = new ArrayList<>();
            for (Object o : (Iterable<?>) attribute) {
                ListType annotation = field.getAnnotation(ListType.class);
                if (annotation != null) {

                    nestedDto.add(convertNestedIfNeeded(annotation.value(), o));
                }
            }
            return nestedDto;
        }

        return attribute;
    }

}
