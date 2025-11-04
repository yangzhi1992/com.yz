package com.commons.fuyoo.netty;

import com.commons.common.utils.JsonTool;
import com.commons.fuyoo.dto.HttpRequestEntity;
import com.commons.fuyoo.dto.SocketMessage;
import com.commons.fuyoo.service.FuyooApiRequestService;
import com.google.common.collect.Lists;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyWebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private final Logger logger = LoggerFactory.getLogger(NettyWebSocketHandler.class);

    private FuyooApiRequestService fuyooApiRequestService;
    /**
     * 存储用户id和用户的channelId绑定
     */
    public static ConcurrentHashMap<ChannelId, Channel> userMap = new ConcurrentHashMap<>();

    public NettyWebSocketHandler(FuyooApiRequestService fuyooApiRequestService) {
        this.fuyooApiRequestService = fuyooApiRequestService;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("与客户端建立连接，通道开启！");
        userMap.put(ctx.channel().id(), ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("与客户端断开连接，通道关闭！");
        userMap.remove(ctx.channel().id());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
        } else if (msg instanceof TextWebSocketFrame) {
            //正常的TEXT消息类型
            TextWebSocketFrame frame = (TextWebSocketFrame) msg;
            SocketMessage socketMessage = JsonTool.parseObject(frame.text(), SocketMessage.class);
            Map<String, String> message = socketMessage.getMessage();

            switch (socketMessage.getMessageType()) {
                case "schedule":
                    scheduleTask(message, ctx.channel());
                    break;
                case "cancel":
                    cancelTask(message);
                    break;
                case "close":
                    ctx.channel().close();
                default:
            }
        }
        super.channelRead(ctx, msg);
    }


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame) {

    }

    private void scheduleTask(Map<String, String> message, Channel channel) {
        HttpRequestEntity requestEntity = HttpRequestEntity.builder()
                                                           .ips(Lists.newArrayList(message.get("ip")))
                                                           .method(message.get("method"))
                                                           .path(message.get("path"))
                                                           .paramsStr(message.get("param"))
                                                           .build();
        if (message.get("host") != null) {
            requestEntity.setHeaders(Collections.singletonMap("Host", message.get("host")));
        }

        fuyooApiRequestService.scheduleTask(requestEntity, channel, "api",
                Integer.parseInt(message.getOrDefault("delay", "1000")));
    }

    private void cancelTask(Map<String, String> message) {
        fuyooApiRequestService.cancelTask(message.get("path"), message.get("ip"), message.get("method"));
    }
}
