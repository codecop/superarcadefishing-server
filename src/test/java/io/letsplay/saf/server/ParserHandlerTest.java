package io.letsplay.saf.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ParserHandlerTest {

    public static final String JSON_INPUT = "{\"timeStamp\":1378129744128,\"type\":\"miss\",\"level\":2}";
    public static final String TIME_STAMP = "timeStamp";
    public static final String TYPE = "type";
    public static final String LEVEL = "level";

    @Test
    public void shouldParseJson() throws IOException {
        ChannelHandlerContext context = mock(ChannelHandlerContext.class);

        TextWebSocketFrame message = mock(TextWebSocketFrame.class);
        when(message.text()).thenReturn(JSON_INPUT);

        final boolean[] nextWasCalled = new boolean[1];
        Controller next = new Controller() {
            @Override
            public void process(Map<String, Object> any) {
                assertEquals(1378129744128L, any.get(TIME_STAMP));
                assertEquals("miss", any.get(TYPE));
                assertEquals(2L, any.get(LEVEL));

                nextWasCalled[0] = true;
            }
        };

        ParserHandler handler = new ParserHandler(next, new JsonMapper());
        handler.channelRead0(context, message);

        assertTrue("did not call next", nextWasCalled[0]);
    }

}
