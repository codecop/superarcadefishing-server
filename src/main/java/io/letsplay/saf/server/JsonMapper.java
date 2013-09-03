package io.letsplay.saf.server;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

public class JsonMapper {

    private final JSONParser parser;

    public JsonMapper() {
        parser = new JSONParser();
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> map(String json) {
        try {
            return (JSONObject) parser.parse(json);
        } catch (ParseException e) {
            throw new IllegalArgumentException("invalid JSON received from client", e);
        }
    }

    public JsonBuilder build(String key, Object value) {
        return new JsonBuilder().with(key, value);
    }

    public static class JsonBuilder {
        private final JSONObject root = new JSONObject();

        private JsonBuilder() {
        }

        @SuppressWarnings("unchecked")
        public JsonBuilder with(String key, Object value) {
            root.put(key, value);
            return this;
        }

        public JsonBuilder with(String key, Class<?> type) {
            return with(key, type.getName());
        }

        public JsonBuilder with(String key, Throwable ex) {
            StringWriter out = new StringWriter();
            ex.printStackTrace(new PrintWriter(out));
            String stackTrace = out.toString();
            return with(key, stackTrace);
        }

        public String toJson() {
            return root.toJSONString();
        }
    }
}
