package io.letsplay.saf.server;

import java.util.HashMap;
import java.util.Map;

/**
 * Invoke service for given route value.
 *
 * Client must specify a route value. A default (null) route can be defined.
 */
public class Dispatcher implements Controller {

    public static final String ROUTE = "route";

    private final Map<String, Controller> controllerByName = new HashMap<>();

    public void register(String name, Controller controller) {
        controllerByName.put(name, controller);
    }

    public void registerDefault(Controller controller) {
        register(null, controller);
    }

    @Override
    public void process(Map<String, Object> attributes) {
        String route = (String) attributes.get(ROUTE);
        Controller controller = controllerByName.get(route);

        if (controller != null) {
            controller.process(attributes);
        } else {
            throw new IllegalArgumentException("unknown route: " + route);
        }
    }
}
