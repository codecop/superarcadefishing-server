package io.letsplay.saf.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class ParserHandlerTest {

    public static final String JSON_INPUT = "{\"timeStamp\":1378129744128,\"type\":\"miss\",\"level\":2}";
    public static final String TIME_STAMP = "timeStamp";
    public static final String TYPE = "type";
    public static final String LEVEL = "level";

    public static final String INVALID_JSON = "{";

    private ChannelHandlerContext context = mock(ChannelHandlerContext.class);
    private Channel callbackChannel = mock(Channel.class);
    private TextWebSocketFrame message = mock(TextWebSocketFrame.class);
    private Controller controller = mock(Controller.class);

    @Before
    public void setUpMocks() {
        when(context.channel()).thenReturn(callbackChannel);
    }

    @Test
    public void shouldParseJson() throws IOException {
        when(message.text()).thenReturn(JSON_INPUT);

        final boolean[] nextWasCalled = new boolean[1];
        controller = new Controller() {
            @Override
            public void process(Map<String, Object> any) {
                assertEquals(1378129744128L, any.get(TIME_STAMP));
                assertEquals("miss", any.get(TYPE));
                assertEquals(2L, any.get(LEVEL));

                nextWasCalled[0] = true;
            }
        };

        handleRead();

        assertTrue("did not call next", nextWasCalled[0]);
    }

    private void handleRead() {
        ParserHandler handler = new ParserHandler(controller, new JsonMapper());
        handler.channelRead0(context, message);
    }

    @Test
    public void shouldAnswerOnInvalidJson() throws IOException {
        when(message.text()).thenReturn(INVALID_JSON);

        handleRead();

        verify(controller, never()).process(Matchers.<Map<String, Object>>any());
        verify(callbackChannel).write(any((TextWebSocketFrame.class)));
    }

    @Test
    public void shouldAnswerOnEmpty() throws IOException {
        when(message.text()).thenReturn("");

        handleRead();

        verify(callbackChannel).write(any((TextWebSocketFrame.class)));
    }

    // TODO return error codes as JSON nicely

}
