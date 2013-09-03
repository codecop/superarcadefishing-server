package io.letsplay.saf.server;

import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ReflectionSetterTest {

    @SuppressWarnings("unused") // fields get assigned via reflection
    private static class Bean {
        private String someField;
        private int someInt;
        private Date someDate;
    }

    @Test
    public void shouldSetString() {
        Bean bean = new Bean();

        Map<String, Object> values = new HashMap<>();
        final String value = "abc";
        values.put("someField", value);
        new ReflectionSetter().set(bean, values);

        assertEquals(value, bean.someField);
    }

    @Test
    public void shouldSetLongAsInt() {
        Bean bean = new Bean();

        Map<String, Object> values = new HashMap<>();
        values.put("someInt", 10L);
        new ReflectionSetter().set(bean, values);

        assertEquals(10, bean.someInt);
    }

    @Test
    public void shouldSetLongAsDate() {
        Bean bean = new Bean();

        Map<String, Object> values = new HashMap<>();
        final long timestamp = 1234567890L;
        values.put("someDate", timestamp);
        new ReflectionSetter().set(bean, values);

        assertEquals(new Date(timestamp), bean.someDate);
    }
}
