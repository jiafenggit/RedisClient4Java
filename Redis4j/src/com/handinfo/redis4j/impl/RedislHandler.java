package com.handinfo.redis4j.impl;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

public class RedislHandler extends SimpleChannelUpstreamHandler
{

	private static final Logger logger = Logger.getLogger(RedislHandler.class.getName());

	private volatile Channel channel;
	private final BlockingQueue<String> answer = new LinkedBlockingQueue<String>();

	public String excuteCmd(String[] cmd)
	{
		//buffer.writeBytes(("*" + cmd.length + "\r\n").getBytes());
		StringBuffer sb = new StringBuffer();
		sb.append("*" + cmd.length + "\r\n");
		for (int i = 0; i < cmd.length; i++)
		{
			sb.append("$" + cmd[i].length() + "\r\n");
			sb.append(cmd[i] + "\r\n");
		}
		
		//System.out.println(sb.toString());
		ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
		buffer.writeBytes(sb.toString().getBytes());
		//buffer.writeBytes("*2\r\n$4\r\necho\r\n$7\r\ntestsrz\r\n".getBytes());
		
		sb.delete(0, sb.length()-1);

		channel.write(buffer).awaitUninterruptibly();

		String result = null;
		boolean interrupted = false;
		try
		{
			result = answer.take();
			// break;
		}
		catch (InterruptedException e)
		{
			interrupted = true;
		}

		if (interrupted)
		{
			Thread.currentThread().interrupt();
		}

		//System.err.println("accept");
		//System.err.println(result);
		//System.err.println("accept - over");
		return result;
	}

	@Override
	public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception
	{
		if (e instanceof ChannelStateEvent)
		{
			// logger.info(e.toString());
		}
		super.handleUpstream(ctx, e);
	}

	@Override
	public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception
	{
		channel = e.getChannel();
		super.channelOpen(ctx, e);
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
	{
		answer.offer((String) e.getMessage());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
	{
		logger.log(Level.WARNING, "Unexpected exception from downstream.", e.getCause());
		e.getChannel().close();
	}
}