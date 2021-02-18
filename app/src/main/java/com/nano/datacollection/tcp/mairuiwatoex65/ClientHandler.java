package com.nano.datacollection.tcp.mairuiwatoex65;

import com.nano.common.logger.Logger;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;


/**
 * Netty Handler
 * @author cz
 */
@Deprecated
public class ClientHandler extends SimpleChannelInboundHandler {

    /**
     * Logger
     */
    private Logger logger = new Logger("ClientHandler");

    @Override
    public void channelActive(ChannelHandlerContext channelHandlerContext){
        logger.debug("channelActive");
        channelHandlerContext.writeAndFlush(Unpooled.copiedBuffer("Netty Rocks!", CharsetUtil.UTF_8));
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause){
        logger.debug("exceptionCaught");
        cause.printStackTrace();
        channelHandlerContext.close();
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.debug("Client received: " + msg.toString());
    }
}
