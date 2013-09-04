package io.letsplay.saf.server;

import java.lang.reflect.Field;
import java.util.Map;

public class AttributesClassSetter {

    private final Object dto;

    public AttributesClassSetter(Object dto) {
        this.dto = dto;
    }

    public void set(Map<String, Object> attributes) {
        try {

            setAllFields(attributes);

        } catch (IllegalAccessException | InstantiationException e) {
            throw new IllegalArgumentException("error during reflection", e);
        }
    }

    private void setAllFields(Map<String, Object> attributes) throws IllegalAccessException, InstantiationException {
        final Field[] allFields = dto.getClass().getDeclaredFields();
        for (Field field : allFields) {
            setField(field, attributes);
        }
    }

    private void setField(Field field, Map<String, Object> attributes) throws IllegalAccessException, InstantiationException {
        final String fieldName = field.getName();
        if (attributes.containsKey(fieldName)) {
            final Object attribute = attributes.get(fieldName);
            new AttributeFieldSetter(dto, field).set(attribute);
        }
    }

}
