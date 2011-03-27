package com.handinfo.redis4j.impl.classification;

import com.handinfo.redis4j.api.IConnector;
import com.handinfo.redis4j.api.RedisCommand;
import com.handinfo.redis4j.api.RedisResponseMessage;
import com.handinfo.redis4j.api.classification.IStrings;
import com.handinfo.redis4j.impl.CommandExecutor;

public class Strings extends CommandExecutor implements IStrings
{

	public Strings(IConnector connector)
	{
		super(connector);
	}

	/* (non-Javadoc)
	 * @see com.handinfo.redis4j.impl.IStrings#append(java.lang.String, java.lang.String)
	 */
	public int append(String key, String value)
	{
		return integerReply(RedisCommand.APPEND, key);
	}
	
	/* (non-Javadoc)
	 * @see com.handinfo.redis4j.impl.IStrings#getrange(java.lang.String, int, int)
	 */
	public String getrange(String key, int start, int end)
	{
		return (String) bulkReply(RedisCommand.GETRANGE, false, start, end);
	}
	
	/* (non-Javadoc)
	 * @see com.handinfo.redis4j.impl.IStrings#mset(java.lang.String, java.lang.String)
	 */
	public Boolean mset(String key, String value)
	{
		return singleLineReplyForBoolean(RedisCommand.MSET, RedisResponseMessage.OK, value);
	}
	
	/* (non-Javadoc)
	 * @see com.handinfo.redis4j.impl.IStrings#setnx(java.lang.String, java.lang.String)
	 */
	public Boolean setnx(String key, String value)
	{
		return integerReply(RedisCommand.SETNX, key)==1 ? true : false;
	}
	
	/* (non-Javadoc)
	 * @see com.handinfo.redis4j.impl.IStrings#decr(java.lang.String)
	 */
	public int decr(String key)
	{
		return integerReply(RedisCommand.DECR, key);
	}
	
	/* (non-Javadoc)
	 * @see com.handinfo.redis4j.impl.IStrings#getset(java.lang.String, java.lang.String)
	 */
	public String getset(String key, String value)
	{
		return (String) bulkReply(RedisCommand.GETSET, false, value);
	}
	
	/* (non-Javadoc)
	 * @see com.handinfo.redis4j.impl.IStrings#msetnx(java.lang.String, java.lang.String)
	 */
	public Boolean msetnx(String key, String value)
	{
		return integerReply(RedisCommand.MSETNX, key)==1 ? true : false;
	}
	
	/* (non-Javadoc)
	 * @see com.handinfo.redis4j.impl.IStrings#setrange(java.lang.String, int, java.lang.String)
	 */
	public int setrange(String key, int offset, String value)
	{
		return integerReply(RedisCommand.SETRANGE, key, offset, value);
	}
	
	/* (non-Javadoc)
	 * @see com.handinfo.redis4j.impl.IStrings#decrby(java.lang.String, int)
	 */
	public int decrby(String key, int decrement)
	{
		return integerReply(RedisCommand.DECRBY, key, decrement);
	}
	
	/* (non-Javadoc)
	 * @see com.handinfo.redis4j.impl.IStrings#incr(java.lang.String)
	 */
	public int incr(String key)
	{
		return integerReply(RedisCommand.INCR, key);
	}
	
	/* (non-Javadoc)
	 * @see com.handinfo.redis4j.impl.IStrings#set(java.lang.String, java.lang.String)
	 */
	public boolean set(String key, String value)
	{
		return singleLineReplyForBoolean(RedisCommand.SET, RedisResponseMessage.OK, key, value);
	}
	
	/* (non-Javadoc)
	 * @see com.handinfo.redis4j.impl.IStrings#strlen(java.lang.String)
	 */
	public int strlen(String key)
	{
		return integerReply(RedisCommand.STRLEN, key);
	}
	
	/* (non-Javadoc)
	 * @see com.handinfo.redis4j.impl.IStrings#get(java.lang.String)
	 */
	public String get(String key)
	{
		return (String) bulkReply(RedisCommand.GET, false, key);
	}
	
	/* (non-Javadoc)
	 * @see com.handinfo.redis4j.impl.IStrings#incrby(java.lang.String, int)
	 */
	public int incrby(String key, int increment)
	{
		return integerReply(RedisCommand.INCRBY, key, increment);
	}
	
	/* (non-Javadoc)
	 * @see com.handinfo.redis4j.impl.IStrings#setbit(java.lang.String, int, int)
	 */
	public int setbit(String key, int offset, int value)
	{
		return integerReply(RedisCommand.SETBIT, key, offset, value);
	}
	
	/* (non-Javadoc)
	 * @see com.handinfo.redis4j.impl.IStrings#getbit(java.lang.String, int)
	 */
	public int getbit(String key, int offset)
	{
		return integerReply(RedisCommand.GETBIT, key, offset);
	}
	
	/* (non-Javadoc)
	 * @see com.handinfo.redis4j.impl.IStrings#mget(java.lang.String)
	 */
	public String[] mget(String...keys)
	{
		return (String[]) multiBulkReply(RedisCommand.MGET, false, keys);
	}
	
	/* (non-Javadoc)
	 * @see com.handinfo.redis4j.impl.IStrings#setex(java.lang.String, int, java.lang.String)
	 */
	public boolean setex(String key, int seconds, String value)
	{
		return singleLineReplyForBoolean(RedisCommand.SETEX, RedisResponseMessage.OK, key, seconds, value);
	}
}
