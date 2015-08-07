package lds.gw.handler;


import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

public class ClientInitializer extends ChannelInitializer<SocketChannel> {

	private ChannelHandler clientHandler;
	
	public ClientInitializer(ChannelHandler clientHandler) {
		this.clientHandler = clientHandler;
	}
	
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ch.pipeline().addLast(new LoggingHandler("debug"));
		ch.pipeline().addLast(new IdleStateHandler(30, 0, 0));
		ch.pipeline().addLast(clientHandler);
	}

}
