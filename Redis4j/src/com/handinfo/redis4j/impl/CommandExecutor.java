package com.handinfo.redis4j.impl;

import com.handinfo.redis4j.api.ICommandExecutor;
import com.handinfo.redis4j.api.IConnector;
import com.handinfo.redis4j.api.RedisCommand;
import com.handinfo.redis4j.api.RedisResponse;
import com.handinfo.redis4j.api.RedisResponseMessage;
import com.handinfo.redis4j.api.RedisResponseType;
import com.handinfo.redis4j.api.exception.CleanLockedThreadException;
import com.handinfo.redis4j.api.exception.ErrorCommandException;
import com.handinfo.redis4j.impl.util.ObjectWrapper;

public abstract class CommandExecutor implements ICommandExecutor
{
	private IConnector connector;

	/**
	 * @param connector
	 */
	public CommandExecutor(IConnector connector)
	{
		this.connector = connector;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.handinfo.redis4j.impl.ICommandExecutor#singleLineReplyForBoolean(
	 * java.lang.String, java.lang.String, java.lang.Object)
	 */
	public boolean singleLineReplyForBoolean(RedisCommand command, RedisResponseMessage RedisResultInfo, Object... args) throws IllegalStateException, CleanLockedThreadException, ErrorCommandException
	{
		RedisResponse response = connector.executeCommand(command, args);

		if (response != null)
		{
			if (response.getType() == RedisResponseType.SingleLineReply)
			{
				if (response.getTextValue().equalsIgnoreCase(RedisResultInfo.getValue()))
				{
					return true;
				}
			}
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.handinfo.redis4j.impl.ICommandExecutor#singleLineReplyForString(java
	 * .lang.String, java.lang.Object)
	 */
	public String singleLineReplyForString(RedisCommand command, Object... args) throws IllegalStateException, CleanLockedThreadException, ErrorCommandException
	{
		RedisResponse response = connector.executeCommand(command, args);

		if (response != null)
		{
			if (response.getType() == RedisResponseType.SingleLineReply)
			{
				return response.getTextValue();
			}
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.handinfo.redis4j.impl.ICommandExecutor#integerReply(java.lang.String,
	 * java.lang.Object)
	 */
	public int integerReply(RedisCommand command, Object... args) throws IllegalStateException, CleanLockedThreadException, ErrorCommandException
	{
		RedisResponse response = connector.executeCommand(command, args);

		if (response != null)
		{
			if (response.getType() == RedisResponseType.IntegerReply)
			{
				return Integer.valueOf(response.getTextValue());
			} else if (response.getType() == RedisResponseType.SingleLineReply)
			{
				// System.out.println(response.getTextValue());
				return -2;
			} else if (response.getType() == RedisResponseType.BulkReplies)
			{
				return -3;
			}
		}

		return -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.handinfo.redis4j.impl.ICommandExecutor#bulkReply(java.lang.String,
	 * boolean, java.lang.Object)
	 */
	public Object bulkReply(RedisCommand command, boolean isUseObjectDecoder, Object... args) throws IllegalStateException, CleanLockedThreadException, ErrorCommandException
	{
		RedisResponse response = connector.executeCommand(command, args);

		if (response != null)
		{
			if (response.getType() == RedisResponseType.BulkReplies)
			{
				if (response.getBulkValue() != null)
				{
					if (isUseObjectDecoder)
					{
						ObjectWrapper<?> obj = new ObjectWrapper(response.getBulkValue());
						return obj.getOriginal();
					} else
						return new String(response.getBulkValue());
				}
			}
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.handinfo.redis4j.impl.ICommandExecutor#multiBulkReply(java.lang.String
	 * , boolean, java.lang.Object)
	 */
	public Object[] multiBulkReply(RedisCommand command, boolean isUseObjectDecoder, Object... args) throws IllegalStateException, CleanLockedThreadException, ErrorCommandException
	{
		RedisResponse response = connector.executeCommand(command, args);

		if (response != null)
		{
			if (response.getType() == RedisResponseType.MultiBulkReplies)
			{
				if (response.getMultiBulkValue() != null)
				{
					int returnValueLength = response.getMultiBulkValue().size();

					if (isUseObjectDecoder)
					{
						Object[] returnValue = new Object[returnValueLength];

						for (int i = 0; i < returnValueLength; i++)
						{
							returnValue[i] = new ObjectWrapper(response.getMultiBulkValue().get(i)).getOriginal();
						}
						return returnValue;
					} else
					{
						String[] returnValue = new String[returnValueLength];

						for (int i = 0; i < returnValueLength; i++)
						{
							// TODO getMultiBulkValue很复杂,需要再考虑
							//returnValue[i] = new String(response.getMultiBulkValue().get(i).getBulkValue());
							RedisResponse sonResponse = response.getMultiBulkValue().get(i);
							switch (sonResponse.getType())
							{
							case SingleLineReply:
							{
								returnValue[i] = sonResponse.getTextValue();
								break;
							}
							case ErrorReply:
							{
								returnValue[i] = sonResponse.getTextValue();
								break;
							}
							case IntegerReply:
							{
								returnValue[i] = sonResponse.getTextValue();
								break;
							}
							case BulkReplies:
							{
								returnValue[i] =  new String(sonResponse.getBulkValue());
								break;
							}
							case MultiBulkReplies:
							{
								returnValue[i] = "";
								for(RedisResponse res : sonResponse.getMultiBulkValue())
								{
									returnValue[i] += new String(res.getBulkValue()) + " ";
								}
								returnValue[i] = returnValue[i].trim();
								break;
							}
							default:
								break;
							}
						}
						return returnValue;
					}
				}
			}
		}

		return null;
	}

	public <T> T reply(RedisCommand command, Object... args) throws IllegalStateException, CleanLockedThreadException, ErrorCommandException
	{
		RedisResponse response = connector.executeCommand(command, args);

		if (response != null)
		{
			switch (response.getType())
			{
			case SingleLineReply:
			{
				return null;
			}
			case IntegerReply:
			{
				return null;
			}
			case BulkReplies:
			{
				return null;
			}
			case MultiBulkReplies:
			{
				return null;
			}
			case ErrorReply:
			{
				return null;
			}
			default:
				return null;
			}
		}

		return null;
	}
}
