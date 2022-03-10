package com.nimo.rediswebui.config;


import cn.hutool.core.util.StrUtil;
import com.nimo.rediswebui.entity.redis.RedisResult;
import com.nimo.rediswebui.utils.SerializeUtils;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.resps.ScanResult;
import redis.clients.jedis.resps.Tuple;

import java.net.SocketTimeoutException;
import java.time.Duration;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

/**  
* @author nimo
* @date 2017年10月28日 
* @version V1.0  
* @Description: Redis基础类
*/
@Slf4j
public class RedisConncet {
	protected JedisPool pool = null;
	protected JedisPoolConfig poolConfig=null;
	protected RedisServerConfig redisServerConfig =null;
	protected String clientName = "webUI";


	public RedisConncet(RedisServerConfig config){
		this(config,null);
	}

	public RedisConncet(RedisServerConfig config,JedisPoolConfig poolConfig){
		this.redisServerConfig=config;
		this.poolConfig=poolConfig;
		initPool();
	}


	public synchronized void initPool() {
		if (pool == null) {
			if(redisServerConfig==null){
					throw new NullPointerException("RedisServerConfig is null");
			}
			if(redisServerConfig.getMaxRetryCount()<=0){
				redisServerConfig.setMaxRetryCount(0);
			}
			if(poolConfig==null){
				poolConfig = new JedisPoolConfig();
				poolConfig.setMaxTotal(-1);
				poolConfig.setMaxIdle(5000);
				poolConfig.setMaxWait(Duration.ofMillis(30000L));
				poolConfig.setTestOnBorrow(false);
				poolConfig.setTestOnReturn(false);
				poolConfig.setTestWhileIdle(false);
			}
			if(StrUtil.isNotBlank(redisServerConfig.getUser())&&StrUtil.isNotBlank(redisServerConfig.getPassword())){
				pool = new JedisPool(poolConfig, redisServerConfig.getHost(), redisServerConfig.getPort(), 0, redisServerConfig.getUser()+":"+redisServerConfig.getPassword());
			}else{
				pool = new JedisPool(poolConfig, redisServerConfig.getHost(), redisServerConfig.getPort());
			}
		}
	}
	public synchronized void closePool() {
		if (pool != null) {
			pool.close();
		}
	}

	public RedisServerConfig getRedisServerConfig() {
		return this.redisServerConfig;
	}
	/**
	 * 获取redis连接
	 * @return
	 */
	private Jedis getJedis(){
		int retryCount = 0;
		while (true){
			try (Jedis jedis = pool.getResource()) {
				jedis.clientSetname(clientName);
				return jedis;
			}catch (Exception e){
				if (e instanceof JedisConnectionException || e instanceof SocketTimeoutException){
					retryCount++;
					if (retryCount > redisServerConfig.getMaxRetryCount()){
						break;
					}
				}else{
					break;
				}
			}
		}
		return null;
	}
	/**
	 * 获取redis连接
	 * @return
	 */
	public Pipeline getPipeline(){
		Jedis jedis = getJedis();
		return jedis.pipelined();
	}

	/***
	 * 数量
	 *@param key
	 *@return
	 */
	public long dbsize() {
		try {
			Jedis jedis = getJedis();
			return jedis.dbSize();
		} catch (Exception e) {
			//log.log(e);
		}
		return 0;
	}
	/***
	 * 是否存在
	 *@param key
	 *@return
	 */
	public boolean exists(String key) {
		try {
			return getJedis().exists(key);
		} catch (Exception e) {
			//log.log(e);
		}
		return false;
	}

	/***
	 * key扫描
	 *@param key
	 *@param pattern 模糊匹配
	 *@param count
	 *@return
	 */
	public List<String> Scan(String pattern, int count) {
		List<String> list=new ArrayList<String>();
		if(StrUtil.isBlank(pattern)) {
			pattern="*";
		}
		try {
			Jedis jedis = getJedis();
			String cursor= ScanParams.SCAN_POINTER_START;
			ScanParams scanParams = new ScanParams().count(count).match(pattern);
			while (true) {
				ScanResult<String> result=jedis.scan(cursor, scanParams);
				cursor=result.getCursor();
				list.addAll(result.getResult());
				if(ScanParams.SCAN_POINTER_START.equals(cursor)){
					break;
				}
			}
			return list;
		} catch (Exception e) {
			//log.log(e);
		}
		return list;
	}


	/***
	 * key扫描
	 *@param key
	 *@param pattern 模糊匹配
	 *@param size
	 *@return
	 */
	public RedisResult<String> Scan(String pattern, int size, String cursor) {
		RedisResult<String> data=new RedisResult();
		if(StrUtil.isBlank(pattern)) {
			pattern="*";
		}
		List<String> keys=new ArrayList<>(size);
		try {
			List<byte[]> templist=new ArrayList<>(size);
			Jedis jedis = getJedis();
			if(StrUtil.isBlank(cursor)) {
				cursor=ScanParams.SCAN_POINTER_START;
			}
			int count=size;
			while (true) {
				count=size-templist.size();
				ScanParams scanParams = new ScanParams().count(count).match(pattern);
				ScanResult<byte[]> result=jedis.scan(cursor.getBytes(), scanParams);
				cursor=result.getCursor();
				templist.addAll(result.getResult());
				if(templist.size()>=size||ScanParams.SCAN_POINTER_START.equals(cursor)){
					break;
				}
			}
			log.info("templist {}",templist.size());
			if(templist.size()>0){
				for (byte[] s : templist) {
					keys.add(SerializeUtils.unserialize(s).toString());
				}
			}
			data.setKeys(keys);
			data.setCursor(cursor);
			return data;
		} catch (Exception e) {
			e.printStackTrace();
		}
		data.setKeys(keys);
		data.setCursor(ScanParams.SCAN_POINTER_START);
		return data;
	}

	/***
	 * set扫描
	 *@param key
	 *@param pattern 模糊匹配
	 *@param size
	 *@return
	 */
	public RedisResult<String> sscan(String key,String pattern,int size,String cursor) {
		RedisResult<String> data=new RedisResult();
		if(StrUtil.isBlank(pattern)) {
			pattern="*";
		}
		List<String> member=new ArrayList<>(size);
		try {
			Jedis jedis = getJedis();
			if(StrUtil.isBlank(cursor)) {
				cursor=ScanParams.SCAN_POINTER_START;
			}
			ScanParams scanParams = new ScanParams().count(size).match(pattern);
			while (true) {
				ScanResult<String> result=jedis.sscan(key, cursor,scanParams);
				cursor=result.getCursor();
				member.addAll(result.getResult());
				if(ScanParams.SCAN_POINTER_START.equals(cursor)){
					break;
				}
			}
			data.setKeys(member);
			data.setCursor(cursor);
			return data;
		} catch (Exception e) {
			e.printStackTrace();
		}
		data.setKeys(member);
		data.setCursor(ScanParams.SCAN_POINTER_START);
		return data;
	}



	/***
	 * key扫描
	 *@param key
	 *@param pattern 模糊匹配
	 *@param size
	 *@return
	 */
	public RedisResult<Tuple> Zscan(String key,String pattern,int size,String cursor) {
		RedisResult<Tuple> data=new RedisResult();
		if(StrUtil.isBlank(pattern)) {
			pattern="*";
		}
		List<Tuple> templist=new ArrayList<>(size);
		try {
			Jedis jedis = getJedis();
			if(StrUtil.isBlank(cursor)) {
				cursor=ScanParams.SCAN_POINTER_START;
			}
			int count=size;
			while (true) {
				count=size-templist.size();
				ScanParams scanParams = new ScanParams().count(count).match(pattern);
				log.info("size:{} count:{},pattern:{} cursor:{}",templist.size(), count,pattern,cursor);
				ScanResult<Tuple> result=jedis.zscan(key, cursor, scanParams);
				cursor=result.getCursor();
				templist.addAll(result.getResult());
				if(templist.size()>=size||ScanParams.SCAN_POINTER_START.equals(cursor)){
					break;
				}
			}
			data.setKeys(templist);
			data.setCursor(cursor);
			return data;
		} catch (Exception e) {
			e.printStackTrace();
		}
		data.setKeys(templist);
		data.setCursor(ScanParams.SCAN_POINTER_START);
		return data;
	}

	/***
	 * key扫描
	 *@param key
	 *@param pattern 模糊匹配
	 *@param size
	 *@return
	 */
	public  RedisResult<Entry<String, String>> hscan(String key, String pattern, int size, String cursor) {
		RedisResult<Entry<String, String>> data=new RedisResult();
		if(StrUtil.isBlank(pattern)) {
			pattern="*";
		}
		List<Entry<String, String>> templist=new ArrayList<>(size);
		try {
			Jedis jedis = getJedis();
			if(StrUtil.isBlank(cursor)) {
				cursor=ScanParams.SCAN_POINTER_START;
			}
			int count=size;
			while (true) {
				count=size-templist.size();
				ScanParams scanParams = new ScanParams().count(count).match(pattern);
				log.info("size:{} count:{},pattern:{} cursor:{}",templist.size(), count,pattern,cursor);
				ScanResult<Entry<String, String>> result=jedis.hscan(key, cursor, scanParams);
				templist.addAll(result.getResult());
				cursor=result.getCursor();
				if(templist.size()>=size||ScanParams.SCAN_POINTER_START.equals(cursor)){
					break;
				}
			}
			data.setKeys(templist);
			data.setCursor(cursor);
			return data;
		} catch (Exception e) {
			//log.log(e);
		}
		data.setKeys(templist);
		data.setCursor(ScanParams.SCAN_POINTER_START);
		return data;
	}


	/***
	 * 删除
	 *@param key
	 */
	public void del(String key) {
		getJedis().del(key);
	}


	/**
	 * 设置字符串缓存
	 * @param key
	 * @param value
	 * @param expire
	 */
	public void setString(String key, String value, long expire) {
		Jedis	jedis = getJedis();
		jedis.set(key, value);
		if (expire > 0) jedis.expire(key, expire);
	}

	/**
	 * 无序集合 添加
	 * @param key
	 * @param val
	 * @param expire
	 */
	public void sadd(String key, String val, long expire) {
		Jedis	jedis = getJedis();
		jedis.sadd(key, val);
		if (expire > 0) {
			jedis.expire(key, expire);
		}
	}
	public Long zadd(String key, long score, String member, long expire) {
		Jedis	jedis = getJedis();
		Long result= null;
		try {
			jedis = getJedis();
			result= jedis.zadd(key, score, member);
			if (expire > 0) {
				jedis.expire(key, expire);
			}
			return result;
		} catch (Exception e) {
			//log.log(e);
		}
		return result;
	}


	/**
	 * hash操作 set
	 *
	 * @param key
	 * @param field
	 * @param value
	 */
	public void hset(String key, String field, String value,long expire) {
		Jedis	jedis = getJedis();
		try {
			jedis = getJedis();
			jedis.hset(key, field, value);
			if (expire > 0)
				jedis.expire(key, expire);
		} catch (Exception e) {
			//log.log(e);
		}
	}


	public void lpush(String key, String value, long expire) {
		Jedis	jedis = getJedis();
		try {
			jedis.lpush(key, value);
			if (expire > 0)
				jedis.expire(key, expire);
		} catch (Exception e) {
			//log.log(e);
		}
	}

	public void rpush(String key, String value, long expire) {
		Jedis	jedis = getJedis();
		try {
			jedis.rpush(key, value);
			if (expire > 0)
				jedis.expire(key, expire);
		} catch (Exception e) {
			//log.log(e);
		}
	}


	/**
	 * 用于返回 key 所储存的值的类型。
	 *@param key
	 *@return
	 *  none (key不存在) string (字符串)list (列表)set (集合) zset (有序集) hash (哈希表)
	 */
	public String type(String key) {
		return getJedis().type(key);
	}


	/**
	 * 以秒为单位返回 key 的剩余过期时间。
	 *@param key
	 *@return -1 永久/或者key不存在 -2不存在
	 */
	public long ttl(String key) {
		return getJedis().ttl(key);
	}

	public String get(String key) {
		return getJedis().get(key);
	}

	/***
	 * 修改过期时间
	 *@param key
	 *@param expTime
	 */
	public long expire(String key, long seconds) {
		return getJedis().expire(key,seconds);

	}
	/***
	 * 设置为永久
	 *@param key
	 *@return
	 */
	public long persist(String key) {
		return getJedis().persist(key);
	}


	public long zrem(String key, String member) {
		return getJedis().zrem(key, member);
	}

	public long zrem(String key, String... member) {
		return getJedis().zrem(key, member);
	}

	/**
	 * 删除无序集合的成员
	 * @param key
	 * @param member
	 */
	public long srem(String key,String member) {
		return getJedis().srem(key,member);
	}
	public Long hdel(String key, String field) {
		return getJedis().hdel(key, field);
	}

	public Long hdel(String key, String... field) {
		return getJedis().hdel(key, field);
	}

	/**
	 * 移除列表中的元素 从存于 key 的列表里移除前 count 次出现的值为 value 的元素
	 *
	 * @param key
	 * @param value
	 * @param count
	 * count > 0: 从头往尾移除值为 value 的元素。
	 * count < 0: 从尾往头移除值为 value 的元素。
	 *  count = 0: 移除所有值为 value 的元素。
	 */
	public Long lrem(String key, String value, int count) {
		return getJedis().lrem(key, count, value);
	}

	/**
	 * 无序集合key的大小
	 * @param key
	 * @return
	 */
	public long scard(String key) {
		return getJedis().scard(key);
	}

	public Long zcard(String key) {
		return getJedis().zcard(key);
	}

	/**
	 * 获取哈希字段数
	 *
	 * @param key
	 * @return
	 */
	public long hlen(String key) {
		return getJedis().hlen(key);
	}


	public List<Tuple>  zrangeWithScores(String key, int start, int end) {
		return getJedis().zrangeWithScores(key, start, end);
	}


	/**
	 * 命令返回有序集中，指定区间内的成员。其中成员的位置按分数值递减(从大到小)来排列。
	 *@param key
	 *@param start
	 *@param end
	 *@return
	 */
	public List<Tuple>  zrevrangeWithScores(String key, long start, long end) {
		return getJedis().zrevrangeWithScores(key, start, end);
	}

	/**
	 * 获取list长度
	 *
	 * @param key
	 * @return
	 */
	public long llen(String key) {
		return getJedis().llen(key);
	}

	public List<String> lrange(String key, int start, int end) {
		return getJedis().lrange(key, start, end);
	}

	public String lset(String key, String value, int index) {
		return getJedis().lset(key, index, value);
	}


}
