package com.handinfo.redis4j.impl.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class LogUtil
{
	private class LogFormatter extends Formatter
	{
		@Override
		public String format(LogRecord record)
		{
			StringBuffer sb = new StringBuffer();
			Date date = new Date(record.getMillis());
			// 日期
			sb.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));
			sb.append(" ");

			// 日志本身
			sb.append("LogInfo=【");
			sb.append(formatMessage(record));
			sb.append("】");

			// 日志级别
			sb.append(record.getLevel().getName());
			sb.append(" ");

			// 线程名
			sb.append("ThreadName=【");
			sb.append(Thread.currentThread().getName());
			sb.append("】");
			sb.append(" ");

			// 调用函数所在的类名
			sb.append("SourceClassName=【");
			sb.append(record.getSourceClassName());
			sb.append("】");
			sb.append(" ");

			// 函数名
			sb.append("SourceMethodName=【");
			sb.append(record.getSourceMethodName());
			sb.append("】");

			sb.append("\n");

			return sb.toString();
		}
	}
	
	//private final static StreamHandler streamHandler = new StreamHandler(System.out, new LogUtil().new LogFormatter());
	
	public static Logger getLogger(String loggerName)
	{
		Logger logger = Logger.getLogger(loggerName);
		//streamHandler.setLevel(Level.INFO);
		logger.setUseParentHandlers(false);
		//logger.addHandler(streamHandler);
		ConsoleHandler ch = new ConsoleHandler();
		ch.setLevel(Level.ALL);
		ch.setFormatter(new LogUtil().new LogFormatter());
		logger.addHandler(ch);
		return logger;
	}

	public static void main(String[] args)
	{
		Logger logger = LogUtil.getLogger("test");
		logger.log(Level.INFO, "xxxxxxxxxxx");
		
		Logger logger1 =  LogUtil.getLogger("test1");
		logger1.log(Level.INFO, "xxxxxxxxxxx111");
		//int a = 1/0;
	}
}