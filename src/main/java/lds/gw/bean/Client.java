package lds.gw.bean;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lds.gw.handler.ClientHandler;
import lds.gw.handler.ClientInitializer;

public class Client {

	private String ip;
	private int port;
	private EventLoopGroup group;
	private ClientHandler clientHandler;
	
	public Channel start() {
		if(null != group && !group.isShutdown()) group.shutdownGracefully();
		group = new NioEventLoopGroup();
		Bootstrap b = new Bootstrap();
		b.group(group);
		b.channel(NioSocketChannel.class);
		b.option(ChannelOption.TCP_NODELAY, true);
		
		clientHandler = new ClientHandler();
		b.handler(new ClientInitializer(clientHandler));
		
		try {
			ChannelFuture f = b.connect(ip, port).sync();
			return f.channel();
		} catch (InterruptedException e) {
			e.printStackTrace();
			group.shutdownGracefully();
		}
		return null;
	}
	
	
	public void stop() {
		if(null != group && !group.isShutdown()) group.shutdownGracefully();
	}
	

	public void setPort(int port) {
		this.port = port;
	}
	
	public void setIp(String ip) {
		this.ip = ip;
	}
	
	public void send(byte[] bytes) {
		clientHandler.getCtx().writeAndFlush(Unpooled.wrappedBuffer(bytes));
	}

}
