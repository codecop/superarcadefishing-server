package io.letsplay.saf.server;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JsonMapperTest {

    @Test
    public void shouldCreateSimpleMessage() {
        assertEquals("{\"key\":\"value\"}", new JsonMapper().build("key", "value").toJson());
    }

    @Test
    public void shouldCreateComplexMessage() {
        assertEquals("{\"some\":123,\"key\":\"value\"}", new JsonMapper().build("key", "value").with("some", 123).toJson());
    }
}
