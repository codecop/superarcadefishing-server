package io.letsplay.saf.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.io.IOException;
import java.util.Map;

public class ParserHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private final JsonMapper mapper;
    private final Controller controller;

    public ParserHandler(Controller controller1, JsonMapper mapper) {
        this.controller = controller1;
        this.mapper = mapper;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws IOException {
        Map<String,Object> objectMap = mapper.map(msg.text());
        // TODO if sth technical is missing
        controller.process(objectMap);
    }
}
