package io.letsplay.saf.server.connector;

import io.letsplay.saf.server.Controller;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.Map;

public class ParserHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private final JsonMapper mapper;
    private final Controller controller;

    public ParserHandler(Controller controller1, JsonMapper mapper) {
        this.controller = controller1;
        this.mapper = mapper;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) {
        try {
            Map<String, Object> objectMap = mapper.map(msg.text());
            controller.process(objectMap);
        } catch (Exception ex) {
            // TODO maybe put in another place because this is different responsibility
            String jsonString = mapper.build("error", true).
                                       with("type", ex.getClass()).
                                       with("message", ex.getMessage()).
                                       with("stacktrace", ex).
                                       toJson();
            ctx.channel().write(new TextWebSocketFrame(jsonString));
        }
    }
}
