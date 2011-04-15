/**
 * 
 */
package com.handinfo.redis4j.impl.cache;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.handinfo.redis4j.api.RedisCommand;
import com.handinfo.redis4j.api.RedisResponse;
import com.handinfo.redis4j.api.RedisResponseMessage;
import com.handinfo.redis4j.api.RedisResponseType;
import com.handinfo.redis4j.api.Sharding;
import com.handinfo.redis4j.api.cache.ICacheConnector;
import com.handinfo.redis4j.api.cache.IRedisCacheClient;
import com.handinfo.redis4j.api.exception.CleanLockedThreadException;
import com.handinfo.redis4j.api.exception.ErrorCommandException;
import com.handinfo.redis4j.impl.util.ObjectWrapper;

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
	 * @see com.handinfo.redis4j.api.ICache#append(java.lang.String, java.lang.String)
	 */
	@Override
	public int append(String key, String value)
	{
		return this.sendRequest(Integer.class, null, RedisCommand.APPEND, key, value);
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#decrement(java.lang.String)
	 */
	@Override
	public int decrement(String key)
	{
		return this.sendRequest(Integer.class, null, RedisCommand.DECRBY, key);
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#decrementByValue(java.lang.String, int)
	 */
	@Override
	public int decrementByValue(String key, int decrement)
	{
		return this.sendRequest(Integer.class, null, RedisCommand.DECRBY, key, decrement);
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#del(java.lang.String[])
	 */
	@Override
	public int del(String... keys)
	{
		List<Integer> resultList = this.sendMultipleKeysNoArgsAndSingleReplay(Integer.class, null, RedisCommand.DEL, keys);
		int result=0;
		for(Integer i : resultList)
		{
			result += i;
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#exists(java.lang.String)
	 */
	@Override
	public boolean exists(String key)
	{
		return this.sendRequest(Boolean.class, RedisResponseMessage.INTEGER_1, RedisCommand.EXISTS, key);
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#expire(java.lang.String, int)
	 */
	@Override
	public boolean expire(String key, int seconds)
	{
		return this.sendRequest(Boolean.class, RedisResponseMessage.INTEGER_1, RedisCommand.EXPIRE, key, seconds);
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#expireAsTimestamp(java.lang.String, long)
	 */
	@Override
	public boolean expireAsTimestamp(String key, long timestamp)
	{
		return this.sendRequest(Boolean.class, RedisResponseMessage.INTEGER_1, RedisCommand.EXPIREAT, key, timestamp);
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#get(java.lang.String)
	 */
	@Override
	public <T> T get(String key)
	{
		byte[] objectbyte = this.sendRequest(byte[].class, null, RedisCommand.GET, key);

		ObjectWrapper<T> obj = new ObjectWrapper<T>(objectbyte);

		return obj.getOriginal();
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#getBit(java.lang.String, int)
	 */
	@Override
	public int getBit(String key, int offset)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#getRange(java.lang.String, int, int)
	 */
	@Override
	public String getRange(String key, int start, int end)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#getSet(java.lang.String, java.lang.Object)
	 */
	@Override
	public <T> T getSet(String key, T value)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#hashesDel(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean hashesDel(String key, String field)
	{
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#hashesExists(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean hashesExists(String key, String field)
	{
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#hashesGet(java.lang.String, java.lang.String)
	 */
	@Override
	public <T> T hashesGet(String key, String field)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#hashesGetAll(java.lang.String)
	 */
	@Override
	public <T> HashMap<String, T> hashesGetAll(String key)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#hashesGetAllField(java.lang.String)
	 */
	@Override
	public String[] hashesGetAllField(String key)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#hashesGetAllValue(java.lang.String)
	 */
	@Override
	public <T> T[] hashesGetAllValue(String key)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#hashesIncrementByValue(java.lang.String, java.lang.String, int)
	 */
	@Override
	public int hashesIncrementByValue(String key, String field, int increment)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#hashesLength(java.lang.String)
	 */
	@Override
	public int hashesLength(String key)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#hashesMultipleFieldGet(java.lang.String, java.lang.String[])
	 */
	@Override
	public <T> T[] hashesMultipleFieldGet(String key, String... fields)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#hashesMultipleSet(java.lang.String, java.util.HashMap)
	 */
	@Override
	public <T> boolean hashesMultipleSet(String key, HashMap<String, T> fieldAndValue)
	{
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#hashesSet(java.lang.String, java.lang.String, java.lang.Object)
	 */
	@Override
	public <T> boolean hashesSet(String key, String field, T value)
	{
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#hashesSetNotExistField(java.lang.String, java.lang.String, java.lang.Object)
	 */
	@Override
	public <T> boolean hashesSetNotExistField(String key, String field, T value)
	{
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#increment(java.lang.String)
	 */
	@Override
	public int increment(String key)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#incrementByValue(java.lang.String, int)
	 */
	@Override
	public int incrementByValue(String key, int increment)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#keys(java.lang.String)
	 */
	@Override
	public String[] keys(String pattern)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#listBlockLeftPop(int, java.lang.String)
	 */
	@Override
	public <T> T listBlockLeftPop(int timeout, String key)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#listBlockRightPop(int, java.lang.String)
	 */
	@Override
	public <T> T listBlockRightPop(int timeout, String key)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#listBlockRightPopLeftPush(java.lang.String, java.lang.String, int)
	 */
	@Override
	public <T> T listBlockRightPopLeftPush(String source, String destination, int timeout)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#listIndex(java.lang.String, int)
	 */
	@Override
	public <T> T listIndex(String key, int index)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#listLeftInsert(java.lang.String, java.lang.String, java.lang.String, java.lang.Object)
	 */
	@Override
	public <T> int listLeftInsert(String key, String BEFORE_AFTER, String pivot, T value)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#listLeftPop(java.lang.String)
	 */
	@Override
	public <T> T listLeftPop(String key)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#listLeftPush(java.lang.String, java.lang.Object)
	 */
	@Override
	public <T> int listLeftPush(String key, T value)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#listLeftPushOnExist(java.lang.String, java.lang.Object)
	 */
	@Override
	public <T> int listLeftPushOnExist(String key, T value)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#listLength(java.lang.String)
	 */
	@Override
	public int listLength(String key)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#listRange(java.lang.String, int, int)
	 */
	@Override
	public <T> T[] listRange(String key, int start, int stop)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#listRemove(java.lang.String, int, java.lang.Object)
	 */
	@Override
	public <T> int listRemove(String key, int count, T value)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#listRightPop(java.lang.String)
	 */
	@Override
	public <T> T listRightPop(String key)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#listRightPopLeftPush(java.lang.String, java.lang.String)
	 */
	@Override
	public <T> T listRightPopLeftPush(String source, String destination)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#listRightPush(java.lang.String, java.lang.Object)
	 */
	@Override
	public <T> int listRightPush(String key, T value)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#listRightPushOnExist(java.lang.String, java.lang.Object)
	 */
	@Override
	public <T> int listRightPushOnExist(String key, T value)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#listSet(java.lang.String, int, java.lang.Object)
	 */
	@Override
	public <T> boolean listSet(String key, int index, T value)
	{
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#listTrim(java.lang.String, int, int)
	 */
	@Override
	public boolean listTrim(String key, int start, int stop)
	{
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#move(java.lang.String, int)
	 */
	@Override
	public boolean move(String key, int indexDB)
	{
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#multipleGet(java.lang.String[])
	 */
	@Override
	public <T> List<T> multipleGet(String... keys)
	{
		byte[][] objectbytes = this.sendMultipleKeysNoArgsAndMultiReplay(byte[][].class, null, RedisCommand.MGET, keys);

		List<T> result = new ArrayList<T>(objectbytes.length);;
		for(byte[] object : objectbytes)
		{
			ObjectWrapper<T> obj = new ObjectWrapper<T>(object);
			result.add(obj.getOriginal());
		}
		

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#multipleSet(java.util.HashMap)
	 */
	@Override
	public <T> Boolean multipleSet(HashMap<String, T> keyAndValue)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#multipleSetOnNotExist(java.util.HashMap)
	 */
	@Override
	public Boolean multipleSetOnNotExist(HashMap<String, String> keyAndValue)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#persist(java.lang.String)
	 */
	@Override
	public boolean persist(String key)
	{
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#randomKey(java.net.InetSocketAddress)
	 */
	@Override
	public String randomKey(InetSocketAddress server)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#rename(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean rename(String key, String newKey)
	{
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#renameOnNotExistNewKey(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean renameOnNotExistNewKey(String key, String newKey)
	{
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#set(java.lang.String, java.lang.Object)
	 */
	@Override
	public <T> boolean set(String key, T value)
	{
		ObjectWrapper<T> obj = new ObjectWrapper<T>(value);
		return this.sendRequest(Boolean.class, RedisResponseMessage.OK, RedisCommand.SET, key, obj);
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#setAndExpire(java.lang.String, int, java.lang.Object)
	 */
	@Override
	public <T> boolean setAndExpire(String key, int seconds, T value)
	{
		ObjectWrapper<T> obj = new ObjectWrapper<T>(value);
		return this.sendRequest(Boolean.class, RedisResponseMessage.OK, RedisCommand.SETEX, key, seconds, obj);
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#setBit(java.lang.String, int, int)
	 */
	@Override
	public int setBit(String key, int offset, int value)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#setOnNotExist(java.lang.String, java.lang.Object)
	 */
	@Override
	public <T> Boolean setOnNotExist(String key, T value)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#setRange(java.lang.String, int, java.lang.String)
	 */
	@Override
	public int setRange(String key, int offset, String value)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#setsAdd(java.lang.String, java.lang.Object)
	 */
	@Override
	public <T> Boolean setsAdd(String key, T member)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#setsCard(java.lang.String)
	 */
	@Override
	public int setsCard(String key)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#setsDiff(java.lang.String[])
	 */
	@Override
	public <T> T[] setsDiff(String... keys)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#setsDiffStore(java.lang.String, java.lang.String[])
	 */
	@Override
	public int setsDiffStore(String destination, String... keys)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#setsInter(java.lang.String[])
	 */
	@Override
	public <T> T[] setsInter(String... keys)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#setsInterStore(java.lang.String, java.lang.String[])
	 */
	@Override
	public int setsInterStore(String destination, String... keys)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#setsIsMember(java.lang.String, java.lang.Object)
	 */
	@Override
	public <T> Boolean setsIsMember(String key, T member)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#setsMembers(java.lang.String)
	 */
	@Override
	public <T> T[] setsMembers(String key)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#setsMove(java.lang.String, java.lang.String, java.lang.Object)
	 */
	@Override
	public <T> Boolean setsMove(String source, String destination, T member)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#setsPop(java.lang.String)
	 */
	@Override
	public <T> T setsPop(String key)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#setsRandMember(java.lang.String)
	 */
	@Override
	public <T> T setsRandMember(String key)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#setsRemove(java.lang.String, java.lang.Object)
	 */
	@Override
	public <T> Boolean setsRemove(String key, T member)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#setsUnion(java.lang.String[])
	 */
	@Override
	public <T> T[] setsUnion(String... keys)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#setsUnionStore(java.lang.String, java.lang.String[])
	 */
	@Override
	public int setsUnionStore(String destination, String... keys)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#sortedSetsAdd(java.lang.String, int, java.lang.Object)
	 */
	@Override
	public <T> Boolean sortedSetsAdd(String key, int score, T member)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#sortedSetsCard(java.lang.String)
	 */
	@Override
	public int sortedSetsCard(String key)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#sortedSetsCount(java.lang.String, int, int)
	 */
	@Override
	public int sortedSetsCount(String key, int min, int max)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#sortedSetsIncrementByValue(java.lang.String, int, java.lang.Object)
	 */
	@Override
	public <T> String sortedSetsIncrementByValue(String key, int increment, T member)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#sortedSetsInterStore(java.lang.String[])
	 */
	@Override
	public int sortedSetsInterStore(String... args)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#sortedSetsRange(java.lang.String, int, int)
	 */
	@Override
	public <T> T[] sortedSetsRange(String key, int start, int stop)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#sortedSetsRangeByScore(java.lang.String[])
	 */
	@Override
	public <T> T[] sortedSetsRangeByScore(String... args)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#sortedSetsRank(java.lang.String, java.lang.Object)
	 */
	@Override
	public <T> int sortedSetsRank(String key, T member)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#sortedSetsRem(java.lang.String, java.lang.Object)
	 */
	@Override
	public <T> Boolean sortedSetsRem(String key, T member)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#sortedSetsRemoveRangeByRank(java.lang.String, int, int)
	 */
	@Override
	public int sortedSetsRemoveRangeByRank(String key, int start, int stop)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#sortedSetsRemoveRangeByScore(java.lang.String, int, int)
	 */
	@Override
	public int sortedSetsRemoveRangeByScore(String key, int min, int max)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#sortedSetsRevRange(java.lang.String, int, int)
	 */
	@Override
	public <T> T[] sortedSetsRevRange(String key, int start, int stop)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#sortedSetsRevRangeByScore(java.lang.String[])
	 */
	@Override
	public <T> T[] sortedSetsRevRangeByScore(String... args)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#sortedSetsRevRank(java.lang.String, java.lang.Object)
	 */
	@Override
	public <T> int sortedSetsRevRank(String key, T member)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#sortedSetsScore(java.lang.String, java.lang.Object)
	 */
	@Override
	public <T> String sortedSetsScore(String key, T member)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#strLength(java.lang.String)
	 */
	@Override
	public int strLength(String key)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#timeToLive(java.lang.String)
	 */
	@Override
	public int timeToLive(String key)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see com.handinfo.redis4j.api.ICache#type(java.lang.String)
	 */
	@Override
	public String type(String key)
	{
		// TODO Auto-generated method stub
		return null;
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
						if (response.getMultiBulkValue().size() == 0)
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
			return classType.cast(Integer.valueOf((String) arg));
		else if (classType == Boolean.class)
		{
			if (compareValue != null)
				return classType.cast(Boolean.valueOf(((String) arg).equalsIgnoreCase(compareValue.getValue())));
			else
				return classType.cast(Boolean.FALSE);
		} else if (classType == byte[][].class)
			return classType.cast(arg);
		else
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
	
	public <T> List<T> sendMultipleKeysNoArgsAndSingleReplay(Class<T> classType, RedisResponseMessage compareValue, RedisCommand command, String... keys)
	{
		List<RedisResponse> responseList = connector.executeMultiKeysNoArgsAndSingleReplay(command, keys);
		List<T> result = new ArrayList<T>(responseList.size());
		for(RedisResponse response : responseList)
		{
			result.add(handleResponse(response, classType, compareValue, command));
		}

		return result;
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
		for(Sharding sharding : serverList)
		{
			shardGroup.add(sharding.clone());
		}
		return shardGroup;
	}

	@Override
	public Boolean flushAllDB()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean flushCurrentDB()
	{
		// TODO Auto-generated method stub
		return null;
	}
}
