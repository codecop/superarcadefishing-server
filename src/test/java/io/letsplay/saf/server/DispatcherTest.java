package io.letsplay.saf.server;

import org.junit.Test;
import org.mockito.Matchers;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class DispatcherTest {

    private Dispatcher dispatcher = new Dispatcher();

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailOnWrongDispatchCode() {
        dispatcher.process(new HashMap<String, Object>());
    }

    @Test
    public void shouldDispatchOnCode() {
        Controller shouldCall = mock(Controller.class);
        dispatcher.register("abc", shouldCall);

        Map<String, Object> map = new HashMap<>();
        map.put(Dispatcher.ROUTE, "abc");
        dispatcher.process(map);

        verify(shouldCall).process(Matchers.<Map<String, Object>>any());
    }

    @Test
    public void shouldDispatchOnDefault() {
        Controller shouldCall = mock(Controller.class);
        dispatcher.registerDefault(shouldCall);

        dispatcher.process(new HashMap<String, Object>());

        verify(shouldCall).process(Matchers.<Map<String, Object>>any());
    }

}
