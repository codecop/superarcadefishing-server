package io.letsplay.saf.server;

import org.junit.Ignore;
import org.junit.Test;

import java.util.*;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;

public class AttributesDtoSetterTest {

    public static class DTO {
        public String text;
        public int number;
        public Date time;
        public NestedDto nested;
        @ListType(NestedDto.class)
        public List<NestedDto> many;
    }
    public static class NestedDto {
        public String name;
    }

    @Test
    public void shouldSetString() {
        DTO dto = new DTO();

        Map<String, Object> attributes = new HashMap<>();
        final String value = "abc";
        attributes.put("text", value);
        new AttributesDtoSetter().set(dto, attributes);

        assertEquals(value, dto.text);
    }

    @Test
    public void shouldSetLongAsInt() {
        DTO dto = new DTO();

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("number", 10L);
        new AttributesDtoSetter().set(dto, attributes);

        assertEquals(10, dto.number);
    }

    @Test
    public void shouldSetLongAsDate() {
        DTO dto = new DTO();

        Map<String, Object> attributes = new HashMap<>();
        final long timestamp = 1234567890L;
        attributes.put("time", timestamp);
        new AttributesDtoSetter().set(dto, attributes);

        assertEquals(new Date(timestamp), dto.time);
    }

    @Test
    public void shouldNotSetNestedWhenNotGiven() {
        DTO dto = new DTO();

        new AttributesDtoSetter().set(dto, new HashMap<String, Object>());

        assertNull(dto.nested);
    }

    @Test
    public void shouldSetNested() {
        DTO dto = new DTO();

        Map<String, Object> nestedAttributes = new HashMap<>();
        nestedAttributes.put("name", "Peter");
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("nested", nestedAttributes);
        new AttributesDtoSetter().set(dto, attributes);

        assertNotNull(dto.nested);
        assertEquals("Peter", dto.nested.name);
    }

    @Test
    public void shouldSetList() {
        DTO dto = new DTO();

        Map<String, Object> nestedAttributes = new HashMap<>();
        nestedAttributes.put("name", "Peter");

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("many", Arrays.asList(nestedAttributes));
        new AttributesDtoSetter().set(dto, attributes);

        assertNotNull(dto.many);
        assertNotNull(dto.many.get(0));
        assertEquals("Peter", dto.many.get(0).name);
    }

}
