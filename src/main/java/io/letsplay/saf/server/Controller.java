package io.letsplay.saf.server;

import java.util.Map;

public interface Controller {

    void process(Map<String, Object> attributes);

}
