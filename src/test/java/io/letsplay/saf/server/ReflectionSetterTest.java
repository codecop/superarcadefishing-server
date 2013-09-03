package io.letsplay.saf.server;

import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ReflectionSetterTest {

    public static class Bean {
        private String someField;
        private int someInt;
        private Date someDate;

        public String getSomeField() {
            return someField;
        }

        @SuppressWarnings("unused") // used with reflection
        public void setSomeField(String someField) {
            this.someField = someField;
        }

        public int getSomeInt() {
            return someInt;
        }

        @SuppressWarnings("unused") // used with reflection
        public void setSomeInt(int someInt) {
            this.someInt = someInt;
        }

        public Date getSomeDate() {
            return someDate;
        }

        @SuppressWarnings("unused") // used with reflection
        public void setSomeDate(Date someDate) {
            this.someDate = someDate;
        }
    }

    @Test
    public void shouldSetString() {
        Bean bean = new Bean();

        Map<String, Object> values = new HashMap<>();
        final String value = "abc";
        values.put("someField", value);
        new ReflectionSetter(bean).set(values);

        assertEquals(value, bean.getSomeField());
    }

    @Test
    public void shouldSetLongAsInt() {
        Bean bean = new Bean();

        Map<String, Object> values = new HashMap<>();
        values.put("someInt", 10L);
        new ReflectionSetter(bean).set(values);

        assertEquals(10, bean.getSomeInt());
    }

    @Test
    public void shouldSetLongAsDate() {
        Bean bean = new Bean();

        Map<String, Object> values = new HashMap<>();
        final long timestamp = 1234567890L;
        values.put("someDate", timestamp);
        new ReflectionSetter(bean).set(values);

        assertEquals(new Date(timestamp), bean.getSomeDate());
    }
}
