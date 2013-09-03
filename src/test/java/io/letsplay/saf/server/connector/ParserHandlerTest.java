package io.letsplay.saf.server.connector;

import io.letsplay.saf.server.Controller;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
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

    @Test
    public void shouldParseJson() throws IOException {
        when(message.text()).thenReturn(JSON_INPUT);
        when(context.channel()).thenReturn(callbackChannel);

        final boolean[] nextWasCalled = new boolean[1];
        controller = new Controller() {
            @Override
            public void process(Map<String, Object> attributes) {
                assertEquals(1378129744128L, attributes.get(TIME_STAMP));
                assertEquals("miss", attributes.get(TYPE));
                assertEquals(2L, attributes.get(LEVEL));

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
        when(context.channel()).thenReturn(callbackChannel);

        handleRead();

        verify(controller, never()).process(Matchers.<Map<String, Object>>any());
        verify(callbackChannel).write(any((TextWebSocketFrame.class)));
    }

    @Test
    public void shouldAnswerExactErrorMessage() throws IOException {
        when(message.text()).thenReturn("");

        final boolean[] writeWasCalled = new boolean[1];
        callbackChannel = new NullChannel() {

            @Override
            public ChannelFuture write(Object msg) {
                String returnedJson = ((TextWebSocketFrame) msg).text();
                Map<String, Object> messageAttributes = new JsonMapper().map(returnedJson);
                assertEquals(true, messageAttributes.get("error"));
                assertEquals("java.lang.IllegalArgumentException", messageAttributes.get("type"));
                assertEquals("invalid JSON received from client", messageAttributes.get("message"));
                assertTrue("no stacktrace", messageAttributes.containsKey("stacktrace"));

                writeWasCalled[0] = true;
                return null;
            }

        };
        when(context.channel()).thenReturn(callbackChannel);

        handleRead();

        assertTrue("did not call write", writeWasCalled[0]);
    }

}
