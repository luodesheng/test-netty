package lds.gw.handler;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientHandler extends ChannelHandlerAdapter {

	private static final Logger LOG = LoggerFactory
			.getLogger(ClientHandler.class);
	
	private ChannelHandlerContext channelHandlerContext;
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		ctx.close();
		LOG.error("连接关闭....");
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
			throws Exception {
		if (!(evt instanceof IdleStateEvent)) {
			return;
		}
		IdleStateEvent e = (IdleStateEvent) evt;
		if (e.state() == IdleState.READER_IDLE) {
			ctx.writeAndFlush(Unpooled.wrappedBuffer("0000".getBytes()));
		}
	}
	
	

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		this.channelHandlerContext = ctx;
	}

	public ChannelHandlerContext getCtx() {
		return this.channelHandlerContext;
	}
}