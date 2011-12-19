/**
 * 
 */
package org.elk.redis4j.impl.cache;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.elk.redis4j.api.ListPosition;
import org.elk.redis4j.api.RedisCommand;
import org.elk.redis4j.api.RedisResponse;
import org.elk.redis4j.api.RedisResponseMessage;
import org.elk.redis4j.api.RedisResponseType;
import org.elk.redis4j.api.Sharding;
import org.elk.redis4j.api.cache.ICacheConnector;
import org.elk.redis4j.api.cache.IRedisCacheClient;
import org.elk.redis4j.api.cache.TypeReference;
import org.elk.redis4j.api.exception.CleanLockedThreadException;
import org.elk.redis4j.api.exception.ErrorCommandException;
import org.elk.redis4j.impl.util.ObjectWrapper;
import org.elk.redis4j.impl.util.ParameterConvert;


/**
 * @author Administrator
 */
public class RedisCacheClient implements IRedisCacheClient
{
	private Set<Sharding> serverList;
	private ICacheConnector connector;

	/**
	 * 
	 */
	public RedisCacheClient(Set<Sharding> serverList)
	{
		this.serverList = serverList;
		connector = new CacheConnector(serverList);
		connector.initSession();
	}

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#append(java.lang.String, java.lang.String)
	 */
	@Override
	public Integer append(String key, String value)
	{
		return this.sendRequest(Integer.class, null, RedisCommand.APPEND, key, value);
	}

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#decrement(java.lang.String)
	 */
	@Override
	public Integer decrement(String key)
	{
		return this.sendRequest(Integer.class, null, RedisCommand.DECR, key);
	}

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#decrementByValue(java.lang.String, int)
	 */
	@Override
	public Integer decrementByValue(String key, int decrement)
	{
		return this.sendRequest(Integer.class, null, RedisCommand.DECRBY, key, decrement);
	}

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#del(java.lang.String[])
	 */
	@Override
	public Integer del(String... keys)
	{
		List<Integer> resultList = this.sendMultipleKeysWithSameArgsAndSingleReplay(Integer.class, null, RedisCommand.DEL, null, keys);
		int result = 0;
		for (Integer i : resultList)
		{
			result += i;
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#exists(java.lang.String)
	 */
	@Override
	public Boolean exists(String key)
	{
		return this.sendRequest(Boolean.class, RedisResponseMessage.INTEGER_1, RedisCommand.EXISTS, key);
	}

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#expire(java.lang.String, int)
	 */
	@Override
	public Boolean expire(String key, int seconds)
	{
		return this.sendRequest(Boolean.class, RedisResponseMessage.INTEGER_1, RedisCommand.EXPIRE, key, seconds);
	}

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#expireAsTimestamp(java.lang.String, long)
	 */
	@Override
	public Boolean expireAsTimestamp(String key, long timestamp)
	{
		return this.sendRequest(Boolean.class, RedisResponseMessage.INTEGER_1, RedisCommand.EXPIREAT, key, timestamp);
	}

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#get(java.lang.String)
	 */
	@Override
	public <T> T get(Class<T> clazz, String key)
	{
		byte[] objectbyte = this.sendRequest(byte[].class, null, RedisCommand.GET, key);

		return new ObjectWrapper<T>(objectbyte, clazz).getOriginal();
	}
	
	@Override
	public <T> T get(TypeReference<T> clazz, String key)
	{
		byte[] objectbyte = this.sendRequest(byte[].class, null, RedisCommand.GET, key);

		return new ObjectWrapper<T>(objectbyte, clazz).getOriginal();
	}

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#getBit(java.lang.String, int)
	 */
	@Override
	public Boolean getBit(String key, int offset)
	{
		return this.sendRequest(Boolean.class, RedisResponseMessage.INTEGER_1, RedisCommand.GETBIT, key, offset);
	}

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#getRange(java.lang.String, int, int)
	 */
	@Override
	public String getRange(String key, int start, int end)
	{
		byte[] objectbyts = this.sendRequest(byte[].class, null, RedisCommand.GETRANGE, key, start, end);
		if(objectbyts == null)
			return null;
		return new String(objectbyts, Charset.forName("UTF-8"));
	}

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#getSet(java.lang.String, java.lang.Object)
	 */
	@Override
	public <T> T getSet(Class<T> clazz, String key, T value)
	{
		ObjectWrapper<T> obj = new ObjectWrapper<T>(value);
		byte[] objectbyte = this.sendRequest(byte[].class, null, RedisCommand.GETSET, key, obj);

		return new ObjectWrapper<T>(objectbyte, clazz).getOriginal();
	}

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#hashesDel(java.lang.String, java.lang.String)
	 */
	@Override
	public Boolean hashesDel(String key, String field)
	{
		return this.sendRequest(Boolean.class, RedisResponseMessage.INTEGER_1, RedisCommand.HDEL, key, field);
	}

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#hashesExists(java.lang.String, java.lang.String)
	 */
	@Override
	public Boolean hashesExists(String key, String field)
	{
		return this.sendRequest(Boolean.class, RedisResponseMessage.INTEGER_1, RedisCommand.HEXISTS, key, field);
	}

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#hashesGet(java.lang.String, java.lang.String)
	 */
	@Override
	public <T> T hashesGet(Class<T> clazz, String key, String field)
	{
		byte[] objectbyte = this.sendRequest(byte[].class, null, RedisCommand.HGET, key, field);

		return new ObjectWrapper<T>(objectbyte, clazz).getOriginal();
	}

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#hashesGetAll(java.lang.String)
	 */
	@Override
	public <T> Map<String, T> hashesGetAll(Class<T> clazz, String key)
	{
		byte[][] objectbytes = this.sendRequest(byte[][].class, null, RedisCommand.HGETALL, key);

		return ParameterConvert.objectArrayToMap(clazz, objectbytes);
	}

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#hashesGetAllField(java.lang.String)
	 */
	@Override
	public List<String> hashesGetAllField(String key)
	{
		byte[][] objectbytes = this.sendRequest(byte[][].class, null, RedisCommand.HKEYS, key);

		return ParameterConvert.objectArrayToStringList(objectbytes);
	}

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#hashesGetAllValue(java.lang.String)
	 */
	@Override
	public <T> List<T> hashesGetAllValue(Class<T> clazz, String key)
	{
		byte[][] objectbytes = this.sendRequest(byte[][].class, null, RedisCommand.HVALS, key);

		return ParameterConvert.objectArrayToObjectList(clazz, objectbytes);
	}

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#hashesIncrementByValue(java.lang.String, java.lang.String, int)
	 */
	@Override
	public Integer hashesIncrementByValue(String key, String field, int increment)
	{
		return this.sendRequest(Integer.class, null, RedisCommand.HINCRBY, key, field, increment);
	}

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#hashesLength(java.lang.String)
	 */
	@Override
	public Integer hashesLength(String key)
	{
		return this.sendRequest(Integer.class, null, RedisCommand.HLEN, key);
	}

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#hashesMultipleFieldGet(java.lang.String, java.lang.String[])
	 */
	@Override
	public <T> List<T> hashesMultipleFieldGet(Class<T> clazz, String key, String... fields)
	{
		byte[][] objectbytes = this.sendRequest(byte[][].class, null, RedisCommand.HMGET, key, (Object[]) fields);
		return ParameterConvert.objectArrayToObjectList(clazz, objectbytes);
	}

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#hashesMultipleSet(java.lang.String, java.util.HashMap)
	 */
	@Override
	public <T> Boolean hashesMultipleSet(String key, Map<String, T> fieldAndValue)
	{
		List<Object> allKeys = ParameterConvert.mapToObjectArray(fieldAndValue);

		return this.sendRequest(Boolean.class, RedisResponseMessage.OK, RedisCommand.HMSET, key, allKeys.toArray());
	}

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#hashesSet(java.lang.String, java.lang.String, java.lang.Object)
	 */
	@Override
	public <T> Boolean hashesSet(String key, String field, T value)
	{
		return this.sendRequest(Boolean.class, RedisResponseMessage.INTEGER_1, RedisCommand.HSET, key, field, new ObjectWrapper<T>(value));
	}

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#hashesSetNotExistField(java.lang.String, java.lang.String, java.lang.Object)
	 */
	@Override
	public <T> Boolean hashesSetNotExistField(String key, String field, T value)
	{
		return this.sendRequest(Boolean.class, RedisResponseMessage.INTEGER_1, RedisCommand.HSETNX, key, field, new ObjectWrapper<T>(value));
	}

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#increment(java.lang.String)
	 */
	@Override
	public Long increment(String key)
	{
		return this.sendRequest(Long.class, null, RedisCommand.INCR, key);
	}

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#incrementByValue(java.lang.String, int)
	 */
	@Override
	public Long incrementByValue(String key, int increment)
	{
		return this.sendRequest(Long.class, null, RedisCommand.INCRBY, key, increment);
	}

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#keys(java.lang.String)
	 */
	@Override
	public List<String> keys(String pattern)
	{
		byte[][] objectBytes = this.sendRequestOnAllShardingAndMultiReplay(byte[][].class, null, RedisCommand.KEYS, pattern);
		return ParameterConvert.objectArrayToStringList(objectBytes);
	}

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#listBlockLeftPop(int, java.lang.String)
	 */
	@Override
	public <T> Entry<String, T> listBlockLeftPop(Class<T> clazz, String key, int timeout)
	{
		byte[][] objectbyte = this.sendRequest(byte[][].class, null, RedisCommand.BLPOP, key, timeout);

		if (objectbyte == null)
			return null;

		Map<String, T> map = new HashMap<String, T>();
		map.put(new String(objectbyte[0], Charset.forName("UTF-8")), new ObjectWrapper<T>(objectbyte[1], clazz).getOriginal());

		return map.entrySet().iterator().next();
	}

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#listBlockRightPop(int, java.lang.String)
	 */
	@Override
	public <T> Entry<String, T> listBlockRightPop(Class<T> clazz, String key, int timeout)
	{
		byte[][] objectbyte = this.sendRequest(byte[][].class, null, RedisCommand.BRPOP, key, timeout);

		if (objectbyte == null)
			return null;

		Map<String, T> map = new HashMap<String, T>();
		map.put(new String(objectbyte[0], Charset.forName("UTF-8")), new ObjectWrapper<T>(objectbyte[1], clazz).getOriginal());

		return map.entrySet().iterator().next();
	}

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#listBlockRightPopLeftPush(java.lang.String, java.lang.String, int)
	 */
	// @Override
	// public <T> T listBlockRightPopLeftPush(String source, String destination, int timeout)
	// {
	// byte[] objectbyte = this.sendRequest(byte[].class, null, RedisCommand.BRPOPLPUSH, source, destination, timeout);
	// return new ObjectWrapper<T>(objectbyte).getOriginal();
	// }

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#listIndex(java.lang.String, int)
	 */
	@Override
	public <T> T listIndex(Class<T> clazz, String key, int index)
	{
		byte[] objectbyte = this.sendRequest(byte[].class, null, RedisCommand.LINDEX, key, index);
		return new ObjectWrapper<T>(objectbyte, clazz).getOriginal();
	}

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#listLeftInsert(java.lang.String, java.lang.String, java.lang.String, java.lang.Object)
	 */
	@Override
	public <T> Integer listInsert(String key, ListPosition beforeOrAfter, T pivot, T value)
	{
		return this.sendRequest(Integer.class, null, RedisCommand.LINSERT, key, beforeOrAfter.toString(), new ObjectWrapper<T>(pivot), new ObjectWrapper<T>(value));
	}

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#listLeftPop(java.lang.String)
	 */
	@Override
	public <T> T listLeftPop(Class<T> clazz, String key)
	{
		byte[] objectbyte = this.sendRequest(byte[].class, null, RedisCommand.LPOP, key);
		return new ObjectWrapper<T>(objectbyte, clazz).getOriginal();
	}

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#listLeftPush(java.lang.String, java.lang.Object)
	 */
	@Override
	public <T> Integer listLeftPush(String key, T value)
	{
		return this.sendRequest(Integer.class, null, RedisCommand.LPUSH, key, new ObjectWrapper<T>(value));
	}

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#listLeftPushOnExist(java.lang.String, java.lang.Object)
	 */
	@Override
	public <T> Integer listLeftPushOnExist(String key, T value)
	{
		return this.sendRequest(Integer.class, null, RedisCommand.LPUSHX, key, new ObjectWrapper<T>(value));
	}

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#listLength(java.lang.String)
	 */
	@Override
	public Integer listLength(String key)
	{
		return this.sendRequest(Integer.class, null, RedisCommand.LLEN, key);
	}

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#listRange(java.lang.String, int, int)
	 */
	@Override
	public <T> List<T> listRange(Class<T> clazz, String key, int start, int stop)
	{
		byte[][] objectbytes = this.sendRequest(byte[][].class, null, RedisCommand.LRANGE, key, start, stop);
		return ParameterConvert.objectArrayToObjectList(clazz, objectbytes);
	}

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#listRemove(java.lang.String, int, java.lang.Object)
	 */
	@Override
	public <T> Integer listRemove(String key, int count, T value)
	{
		return this.sendRequest(Integer.class, null, RedisCommand.LREM, key, count, new ObjectWrapper<T>(value));
	}

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#listRightPop(java.lang.String)
	 */
	@Override
	public <T> T listRightPop(Class<T> clazz, String key)
	{
		byte[] objectbyte = this.sendRequest(byte[].class, null, RedisCommand.RPOP, key);
		return new ObjectWrapper<T>(objectbyte, clazz).getOriginal();
	}

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#listRightPopLeftPush(java.lang.String, java.lang.String)
	 */
	// @Override
	// public <T> T listRightPopLeftPush(String source, String destination)
	// {
	// byte[] objectbyte = this.sendRequest(byte[].class, null, RedisCommand.RPOPLPUSH, source, destination);
	// return new ObjectWrapper<T>(objectbyte).getOriginal();
	// }

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#listRightPush(java.lang.String, java.lang.Object)
	 */
	@Override
	public <T> Integer listRightPush(String key, T value)
	{
		return this.sendRequest(Integer.class, null, RedisCommand.RPUSH, key, new ObjectWrapper<T>(value));
	}

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#listRightPushOnExist(java.lang.String, java.lang.Object)
	 */
	@Override
	public <T> Integer listRightPushOnExist(String key, T value)
	{
		return this.sendRequest(Integer.class, null, RedisCommand.RPUSHX, key, new ObjectWrapper<T>(value));
	}

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#listSet(java.lang.String, int, java.lang.Object)
	 */
	@Override
	public <T> Boolean listSet(String key, int index, T value)
	{
		return this.sendRequest(Boolean.class, RedisResponseMessage.OK, RedisCommand.LSET, key, index, new ObjectWrapper<T>(value));
	}

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#listTrim(java.lang.String, int, int)
	 */
	@Override
	public Boolean listTrim(String key, int start, int stop)
	{
		return this.sendRequest(Boolean.class, RedisResponseMessage.OK, RedisCommand.LTRIM, key, start, stop);
	}

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#move(java.lang.String, int)
	 */
	@Override
	public Boolean move(String key, int indexDB)
	{
		return this.sendRequest(Boolean.class, RedisResponseMessage.INTEGER_1, RedisCommand.MOVE, key, indexDB);
	}

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#multipleGet(java.lang.String[])
	 */
	@Override
	public <T> List<T> multipleGet(Class<T> clazz, String... keys)
	{
		byte[][] objectbytes = this.sendMultipleKeysNoArgsAndMultiReplay(byte[][].class, null, RedisCommand.MGET, keys);
		return ParameterConvert.objectArrayToObjectList(clazz, objectbytes);
	}

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#multipleSet(java.util.HashMap)
	 */
	@Override
	public <T> Boolean multipleSet(Map<String, T> keyAndValue)
	{
		List<Boolean> resultList = this.sendMultiKeyValueAndSingleReplay(Boolean.class, RedisResponseMessage.OK, RedisCommand.MSET, ParameterConvert.normalMapToWrapperMap(keyAndValue));
		for (boolean boo : resultList)
		{
			if (boo == false)
				return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#multipleSetOnNotExist(java.util.HashMap)
	 */
//	@Override
//	public Boolean multipleSetOnNotExist(Map<String, String> keyAndValue)
//	{
//		List<Boolean> resultList = this.sendMultiKeyValueAndSingleReplay(Boolean.class, RedisResponseMessage.INTEGER_1, RedisCommand.MSETNX, ParameterConvert.normalMapToWrapperMap(keyAndValue));
//		for (boolean boo : resultList)
//		{
//			if (boo == false)
//				return false;
//		}
//		return true;
//	}

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#persist(java.lang.String)
	 */
	@Override
	public Boolean persist(String key)
	{
		return this.sendRequest(Boolean.class, RedisResponseMessage.INTEGER_1, RedisCommand.PERSIST, key);
	}

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#rename(java.lang.String, java.lang.String)
	 */
	// @Override
	// public Boolean rename(String key, String newKey)
	// {
	// return this.sendRequest(Boolean.class, RedisResponseMessage.OK, RedisCommand.RENAME, key, newKey);
	// }

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#renameOnNotExistNewKey(java.lang.String, java.lang.String)
	 */
	// @Override
	// public Boolean renameOnNotExistNewKey(String key, String newKey)
	// {
	// return this.sendRequest(Boolean.class, RedisResponseMessage.INTEGER_1, RedisCommand.RENAMENX, key, newKey);
	// }

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#set(java.lang.String, java.lang.Object)
	 */
	@Override
	public <T> Boolean set(String key, T value)
	{
		return this.sendRequest(Boolean.class, RedisResponseMessage.OK, RedisCommand.SET, key, new ObjectWrapper<T>(value));
	}

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#setAndExpire(java.lang.String, int, java.lang.Object)
	 */
	@Override
	public <T> Boolean setAndExpire(String key, int seconds, T value)
	{
		return this.sendRequest(Boolean.class, RedisResponseMessage.OK, RedisCommand.SETEX, key, seconds, new ObjectWrapper<T>(value));
	}

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#setBit(java.lang.String, int, int)
	 */
	@Override
	public Boolean setBit(String key, int offset, boolean value)
	{
		return this.sendRequest(Boolean.class, RedisResponseMessage.INTEGER_1, RedisCommand.SETBIT, key, offset, value ? 1 : 0);
	}

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#setOnNotExist(java.lang.String, java.lang.Object)
	 */
	@Override
	public <T> Boolean setOnNotExist(String key, T value)
	{
		return this.sendRequest(Boolean.class, RedisResponseMessage.INTEGER_1, RedisCommand.SETNX, key, new ObjectWrapper<T>(value));
	}

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#setRange(java.lang.String, int, java.lang.String)
	 */
	@Override
	public Integer setRange(String key, int offset, String value)
	{
		return this.sendRequest(Integer.class, null, RedisCommand.SETRANGE, key, offset, value);
	}

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#setsAdd(java.lang.String, java.lang.Object)
	 */
	@Override
	public <T> Boolean setsAdd(String key, T member)
	{
		return this.sendRequest(Boolean.class, RedisResponseMessage.INTEGER_1, RedisCommand.SADD, key, new ObjectWrapper<T>(member));
	}

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#setsCard(java.lang.String)
	 */
	@Override
	public Integer setsCard(String key)
	{
		return this.sendRequest(Integer.class, null, RedisCommand.SCARD, key);
	}

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#setsDiff(java.lang.String[])
	 */
	// @Override
	// public <T> List<T> setsDiff(String... keys)
	// {
	// byte[][] objectbytes = this.sendMultipleKeysNoArgsAndMultiReplay(byte[][].class, null, RedisCommand.SDIFF, keys);
	// return ParameterConvert.objectArrayToObjectList(objectbytes);
	// }

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#setsDiffStore(java.lang.String, java.lang.String[])
	 */
	// @Override
	// public Integer setsDiffStore(String destination, String... keys)
	// {
	// return 0;
	// }

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#setsInter(java.lang.String[])
	 */
	// @Override
	// public <T> List<T> setsInter(String... keys)
	// {
	// byte[][] objectbytes = this.sendMultipleKeysNoArgsAndMultiReplay(byte[][].class, null, RedisCommand.SINTER, keys);
	// return ParameterConvert.objectArrayToObjectList(objectbytes);
	// }

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#setsInterStore(java.lang.String, java.lang.String[])
	 */
	// @Override
	// public Integer setsInterStore(String destination, String... keys)
	// {
	// return 0;
	// }

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#setsIsMember(java.lang.String, java.lang.Object)
	 */
	@Override
	public <T> Boolean setsIsMember(String key, T member)
	{
		return this.sendRequest(Boolean.class, RedisResponseMessage.INTEGER_1, RedisCommand.SISMEMBER, key, new ObjectWrapper<T>(member));
	}

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#setsMembers(java.lang.String)
	 */
	@Override
	public <T> List<T> setsMembers(Class<T> clazz, String key)
	{
		byte[][] objectbytes = this.sendRequest(byte[][].class, null, RedisCommand.SMEMBERS, key);
		return ParameterConvert.objectArrayToObjectList(clazz, objectbytes);
	}

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#setsMove(java.lang.String, java.lang.String, java.lang.Object)
	 */
	// @Override
	// public <T> Boolean setsMove(String source, String destination, T member)
	// {
	// return this.sendRequest(Boolean.class, RedisResponseMessage.INTEGER_1, RedisCommand.SMOVE, source, destination, new ObjectWrapper<T>(member));
	// }

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#setsPop(java.lang.String)
	 */
	@Override
	public <T> T setsPop(Class<T> clazz, String key)
	{
		return new ObjectWrapper<T>(this.sendRequest(byte[].class, null, RedisCommand.SPOP, key), clazz).getOriginal();
	}

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#setsRandMember(java.lang.String)
	 */
	@Override
	public <T> T setsRandMember(Class<T> clazz, String key)
	{
		return new ObjectWrapper<T>(this.sendRequest(byte[].class, null, RedisCommand.SRANDMEMBER, key), clazz).getOriginal();
	}

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#setsRemove(java.lang.String, java.lang.Object)
	 */
	@Override
	public <T> Boolean setsRemove(String key, T member)
	{
		return this.sendRequest(Boolean.class, RedisResponseMessage.INTEGER_1, RedisCommand.SREM, key, new ObjectWrapper<T>(member));
	}

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#setsUnion(java.lang.String[])
	 */
	// @Override
	// public <T> List<T> setsUnion(String... keys)
	// {
	// byte[][] objectbytes = this.sendMultipleKeysNoArgsAndMultiReplay(byte[][].class, null, RedisCommand.SUNION, keys);
	// return ParameterConvert.objectArrayToObjectList(objectbytes);
	// }

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#setsUnionStore(java.lang.String, java.lang.String[])
	 */
	// @Override
	// public Integer setsUnionStore(String destination, String... keys)
	// {
	// return 0;
	// }

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#sortedSetsAdd(java.lang.String, int, java.lang.Object)
	 */
	@Override
	public <T> Boolean sortedSetsAdd(String key, int score, T member)
	{
		return this.sendRequest(Boolean.class, RedisResponseMessage.INTEGER_1, RedisCommand.ZADD, key, score, new ObjectWrapper<T>(member));
	}

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#sortedSetsCard(java.lang.String)
	 */
	@Override
	public Integer sortedSetsCard(String key)
	{
		return this.sendRequest(Integer.class, null, RedisCommand.ZCARD, key);
	}

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#sortedSetsCount(java.lang.String, int, int)
	 */
	@Override
	public Integer sortedSetsCount(String key, int min, int max)
	{
		return this.sendRequest(Integer.class, null, RedisCommand.ZCOUNT, key, min, max);
	}

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#sortedSetsIncrementByValue(java.lang.String, int, java.lang.Object)
	 */
	@Override
	public <T> Double sortedSetsIncrementByValue(String key, int increment, T member)
	{
		return this.sendRequest(Double.class, null, RedisCommand.ZINCRBY, key, increment, new ObjectWrapper<T>(member));
	}

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#sortedSetsInterStore(java.lang.String[])
	 */
	// @Override
	// public Integer sortedSetsInterStore(String destination, String... keys)
	// {
	// return 0;
	// }

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#sortedSetsRange(java.lang.String, int, int)
	 */
	@Override
	public <T> List<T> sortedSetsRange(Class<T> clazz, String key, int start, int stop)
	{
		byte[][] objectbytes = this.sendRequest(byte[][].class, null, RedisCommand.ZRANGE, key, start, stop);
		return ParameterConvert.objectArrayToObjectList(clazz, objectbytes);
	}

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#sortedSetsRangeByScore(java.lang.String[])
	 */
	@Override
	public <T> List<T> sortedSetsRangeByScore(Class<T> clazz, String key, int min, int max)
	{
		byte[][] objectbytes = this.sendRequest(byte[][].class, null, RedisCommand.ZRANGEBYSCORE, key, min, max);
		return ParameterConvert.objectArrayToObjectList(clazz, objectbytes);
	}

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#sortedSetsRank(java.lang.String, java.lang.Object)
	 */
	@Override
	public <T> Integer sortedSetsRank(String key, T member)
	{
		return this.sendRequest(Integer.class, null, RedisCommand.ZRANK, key, new ObjectWrapper<T>(member));
	}

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#sortedSetsRem(java.lang.String, java.lang.Object)
	 */
	@Override
	public <T> Boolean sortedSetsRemove(String key, T member)
	{
		return this.sendRequest(Boolean.class, RedisResponseMessage.INTEGER_1, RedisCommand.ZREM, key, new ObjectWrapper<T>(member));
	}

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#sortedSetsRemoveRangeByRank(java.lang.String, int, int)
	 */
	@Override
	public Integer sortedSetsRemoveRangeByRank(String key, int start, int stop)
	{
		return this.sendRequest(Integer.class, null, RedisCommand.ZREMRANGEBYRANK, key, start, stop);
	}

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#sortedSetsRemoveRangeByScore(java.lang.String, int, int)
	 */
	@Override
	public Integer sortedSetsRemoveRangeByScore(String key, int min, int max)
	{
		return this.sendRequest(Integer.class, null, RedisCommand.ZREMRANGEBYSCORE, key, min, max);
	}

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#sortedSetsRevRange(java.lang.String, int, int)
	 */
	@Override
	public <T> List<T> sortedSetsRevRange(Class<T> clazz, String key, int start, int stop)
	{
		byte[][] objectbytes = this.sendRequest(byte[][].class, null, RedisCommand.ZREVRANGE, key, start, stop);
		return ParameterConvert.objectArrayToObjectList(clazz, objectbytes);
	}

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#sortedSetsRevRangeByScore(java.lang.String[])
	 */
	@Override
	public <T> List<T> sortedSetsRevRangeByScore(Class<T> clazz, String key, int max, int min)
	{
		byte[][] objectbytes = this.sendRequest(byte[][].class, null, RedisCommand.ZREVRANGEBYSCORE, key, max, min);
		return ParameterConvert.objectArrayToObjectList(clazz, objectbytes);
	}

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#sortedSetsRevRank(java.lang.String, java.lang.Object)
	 */
	@Override
	public <T> Integer sortedSetsRevRank(String key, T member)
	{
		return this.sendRequest(Integer.class, null, RedisCommand.ZREVRANK, key, new ObjectWrapper<T>(member));
	}

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#sortedSetsScore(java.lang.String, java.lang.Object)
	 */
	@Override
	public <T> Integer sortedSetsScore(String key, T member)
	{
		return this.sendRequest(Integer.class, null, RedisCommand.ZSCORE, key, new ObjectWrapper<T>(member));
	}

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#strLength(java.lang.String)
	 */
	@Override
	public Integer strLength(String key)
	{
		return this.sendRequest(Integer.class, null, RedisCommand.STRLEN, key);
	}

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#timeToLive(java.lang.String)
	 */
	@Override
	public Integer timeToLive(String key)
	{
		return this.sendRequest(Integer.class, null, RedisCommand.TTL, key);
	}

	/*
	 * (non-Javadoc)
	 * @see org.elk.redis4j.api.ICache#type(java.lang.String)
	 */
	@Override
	public String type(String key)
	{
		return this.sendRequest(String.class, null, RedisCommand.TYPE, key);
	}

	@Override
	public Boolean flushAllDB()
	{
		List<Boolean> resultList = this.sendRequestOnAllShardingAndSingleReplay(Boolean.class, RedisResponseMessage.OK, RedisCommand.FLUSHALL);
		for (boolean boo : resultList)
		{
			if (boo == false)
				return false;
		}
		return true;
	}

	@Override
	public Boolean flushCurrentDB()
	{
		List<Boolean> resultList = this.sendRequestOnAllShardingAndSingleReplay(Boolean.class, RedisResponseMessage.OK, RedisCommand.FLUSHDB);
		for (boolean boo : resultList)
		{
			if (boo == false)
				return false;
		}
		return true;
	}

	private <T> T handleResponse(RedisResponse response, Class<T> classType, RedisResponseMessage compareValue, RedisCommand command)
	{
		if (response != null)
		{
			// 判断返回类型是否为预期的结果类型
			if (command.getResponseType() == response.getType())
			{
				// 返回正确结果
				switch (response.getType())
				{
				case SingleLineReply:
					{
						return castResult(classType, response.getTextValue(), compareValue);
					}
				case IntegerReply:
					{
						return castResult(classType, response.getTextValue(), compareValue);
					}
				case BulkReplies:
					{
						if (response.getBulkValue() == null)
							return null;
						else
						{
							return castResult(classType, response.getBulkValue(), compareValue);
						}
					}
				case MultiBulkReplies:
					{
						if (response.getMultiBulkValue() == null)
							return null;

						byte[][] result = new byte[response.getMultiBulkValue().size()][];
						int i = 0;
						for (RedisResponse res : response.getMultiBulkValue())
						{
							result[i] = res.getBulkValue();
							i++;
						}
						return castResult(classType, result, null);
					}
				default:
					return null;
				}
			} else
			{
				// 返回的值有问题
				if (response.getType() == RedisResponseType.ErrorReply)
					throw new ErrorCommandException(response.getTextValue());
			}
		}

		return null;
	}

	private <T, V> T castResult(Class<T> classType, V arg, RedisResponseMessage compareValue)
	{
		if (arg == null)
			return null;
		if (classType == byte[].class)
		{
			return classType.cast(arg);
		} else if (classType == String.class)
		{
			return classType.cast(arg);
		} else if (classType == Integer.class)
		{
			if (arg instanceof byte[])
				return classType.cast(Integer.valueOf(new String((byte[]) arg, Charset.forName("UTF-8"))));
			return classType.cast(Integer.valueOf((String) arg));
		}
		else if (classType == Boolean.class)
		{
			if (compareValue != null)
			{
				if (arg instanceof byte[])
					return classType.cast(Boolean.valueOf(new String((byte[]) arg, Charset.forName("UTF-8")).equalsIgnoreCase(compareValue.getValue())));
				return classType.cast(Boolean.valueOf(((String) arg).equalsIgnoreCase(compareValue.getValue())));
			}
			else
				return classType.cast(Boolean.FALSE);
		} else if (classType == byte[][].class)
			return classType.cast(arg);
		else if (classType == Double.class)
		{
			if (arg instanceof byte[])
				return classType.cast(Double.valueOf(new String((byte[]) arg, Charset.forName("UTF-8"))));
			return classType.cast(Double.valueOf((String) arg));
		}
		else if (classType == Long.class)
		{
			if (arg instanceof byte[])
				return classType.cast(Long.valueOf(new String((byte[]) arg, Charset.forName("UTF-8"))));
			return classType.cast(Long.valueOf((String) arg));
		} else
			return null;
	}

	/**
	 * 发送请求
	 * 
	 * @param <T>
	 *            classType 返回数据类型
	 * @param compareValue
	 *            与返回结果做对比用的数据,以生成boolean型返回值
	 * @param command
	 *            发送的命令
	 * @param args
	 *            命令参数
	 * @return
	 * @throws IllegalStateException
	 * @throws CleanLockedThreadException
	 * @throws ErrorCommandException
	 */
	public <T> T sendRequest(Class<T> classType, RedisResponseMessage compareValue, RedisCommand command, String key, Object... args)
	{
		RedisResponse response = connector.executeCommand(command, key, args);

		return handleResponse(response, classType, compareValue, command);
	}

	public <T> T sendMultipleKeysNoArgsAndMultiReplay(Class<T> classType, RedisResponseMessage compareValue, RedisCommand command, String... keys)
	{
		RedisResponse response = connector.executeMultiKeysNoArgsAndMultiReplay(command, keys);

		return handleResponse(response, classType, compareValue, command);
	}

	public <T> List<T> sendMultipleKeysWithSameArgsAndSingleReplay(Class<T> classType, RedisResponseMessage compareValue, RedisCommand command, Object arg, String... keys)
	{
		List<RedisResponse> responseList = connector.executeMultiKeysWithSameArgAndSingleReplay(command, arg, keys);
		List<T> result = new ArrayList<T>(responseList.size());
		for (RedisResponse response : responseList)
		{
			result.add(handleResponse(response, classType, compareValue, command));
		}

		return result;
	}

	public <T> List<T> sendMultiKeyValueAndSingleReplay(Class<T> classType, RedisResponseMessage compareValue, RedisCommand command, Map<String, Object> map)
	{
		List<RedisResponse> responseList = connector.executeMultiKeyValueAndSingleReplay(command, map);
		List<T> result = new ArrayList<T>(responseList.size());
		for (RedisResponse response : responseList)
		{
			result.add(handleResponse(response, classType, compareValue, command));
		}

		return result;
	}

	public <T> List<T> sendRequestOnAllShardingAndSingleReplay(Class<T> classType, RedisResponseMessage compareValue, RedisCommand command, Object... args)
	{
		List<RedisResponse> responseList = connector.executeCommandOnAllShardingAndSingleReplay(command, args);
		List<T> result = new ArrayList<T>(responseList.size());
		for (RedisResponse response : responseList)
		{
			result.add(handleResponse(response, classType, compareValue, command));
		}

		return result;
	}

	public <T> T sendRequestOnAllShardingAndMultiReplay(Class<T> classType, RedisResponseMessage compareValue, RedisCommand command, Object... args)
	{
		RedisResponse response = connector.executeCommandOnAllShardingAndMultiReplay(command, args);
		return handleResponse(response, classType, compareValue, command);
	}

	@Override
	public boolean quit()
	{
		connector.disConnect();
		return true;
	}

	@Override
	public int totalOfConnected()
	{
		return connector.getNumberOfConnected();
	}

	@Override
	public Set<Sharding> getShardGroupInfo()
	{
		Set<Sharding> shardGroup = new HashSet<Sharding>();
		for (Sharding sharding : serverList)
		{
			shardGroup.add(sharding.clone());
		}
		return shardGroup;
	}
}
