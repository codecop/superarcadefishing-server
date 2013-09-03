package io.letsplay.saf.server;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class JsonMapperTest {

    @Test
    public void shouldCreateSimpleMessage() {
        assertEquals("{\"key\":\"value\"}", new JsonMapper().build("key", "value").toJson());
    }

    @Test
    public void shouldCreateComplexMessage() {
        String createdJson = new JsonMapper().build("key", "value").with("some", 123).toJson();
        Map<String, Object> messageAttributes = new JsonMapper().map(createdJson);
        assertEquals(123L, messageAttributes.get("some"));
        assertEquals("value", messageAttributes.get("key"));
    }
}
