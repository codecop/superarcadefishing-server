package io.letsplay.saf.server;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.Map;

public class JsonMapper {

    private final JSONParser parser;

    public JsonMapper() {
        parser = new JSONParser();
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> map(String json) throws IOException {
        try {
            return (JSONObject) parser.parse(json);
        } catch (ParseException e) {
            throw new IOException(e);
        }
    }
}
