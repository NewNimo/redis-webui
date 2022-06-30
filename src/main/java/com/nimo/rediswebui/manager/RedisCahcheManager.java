package com.nimo.rediswebui.manager;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.nimo.rediswebui.config.RedisConncet;
import com.nimo.rediswebui.config.RedisServerConfig;
import com.nimo.rediswebui.entity.ServerInfo;
import com.nimo.rediswebui.entity.redis.KeyMember;
import com.nimo.rediswebui.entity.redis.RedisKeys;
import com.nimo.rediswebui.entity.redis.RedisResult;
import com.nimo.rediswebui.exception.BusinessException;
import com.nimo.rediswebui.exception.Code;
import com.nimo.rediswebui.rsp.ConnectInfo;
import com.nimo.rediswebui.rsp.InfoRsp;
import com.nimo.rediswebui.rsp.MemberRsp;
import com.nimo.rediswebui.rsp.keyRsp;
import com.nimo.rediswebui.utils.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.resps.Tuple;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**  
* @author nimo
* @date 2017年10月28日 
* @version V1.0  
* @Description: RedisCahcheManager
*/
@Slf4j
public  class RedisCahcheManager {


	private static volatile RedisCahcheManager inst;
	private static final Object lock = new Object();
	private static volatile ConcurrentHashMap<String,RedisConncet> servers=new ConcurrentHashMap<String,RedisConncet>();
	public ServerInfo entity;

	private static final int EXP_TIME = 3600;//缓存过期时间

	private static final int MAX_COUNT = 500; //最大数量

	public static final int SIZE = 50; //数量
	public static final int HOMEPAGE = 1; //第一页
	public static final String SCAN_POINTER_END="-1";
	public static final String SCAN_POINTER_START="0";

	public static final String TypeString = "string";
	public static final String TypeSet = "set";
	public static final String TypeZset = "zset";
	public static final String TypeHash = "hash";
	public static final String TypeList = "list";


	public static final String MEMORY = "memory";//内存
	public static final String STATS = "stats";//统计
	public static final String SERVER = "server";
	public static final String CLIENTS = "clients";
	public static final String COMMANDSTATS  = "commandstats";






	
	   
	public static RedisCahcheManager inst() {
	    if (inst == null) {
		    synchronized (lock) {
			    if (inst == null)
				    inst = new RedisCahcheManager();
		    }
	    }
	    return inst;
	 }

	private synchronized void add( RedisServerConfig config) {
		if (!servers.containsKey(config.getServerName())) {
			RedisConncet redisConncet=new RedisConncet(config);
			servers.put(config.getServerName(),redisConncet);
		}
	}

	public boolean existConnect(String name){
		return servers.get(name)!=null;
	}

	public void addServer(String name, String host, Integer port,String user, String password){
		 RedisServerConfig config=new RedisServerConfig();
		 config.setServerName(name);
		 config.setHost(host);
		 config.setPort(port);
		 config.setUser(user);
		 config.setPassword(password);
		 add(config);
	}

	public void removeServer(String name){
		RedisConncet conncet=getServer(name.toLowerCase());
		if(conncet!=null){
			conncet.closePool();
			servers.remove(name);
		}
	}
	public RedisConncet getServer(String name){
		RedisConncet conncet= servers.get(name.toLowerCase());
		if(conncet==null){
			throw  new BusinessException("实例连接异常");
		}
		return conncet;
	}

	public List<String> getServerList(){
		List<String> list=new ArrayList<>();
		servers.forEach((k,v)-> {list.add(k);});
		return list;
	}
	public List<ConnectInfo> getConnectList(){
		List<ConnectInfo> list=new ArrayList<>();
		servers.forEach((k,v)-> {
			ConnectInfo info=new ConnectInfo();
			info.setServerName(k);
			info.setHost(v.getRedisServerConfig().getHost());
			list.add(info);
		});
		return list;
	}


	public keyRsp searchKey(String key, String server, String cursor, boolean accurate, int page, int size){
		keyRsp result= new keyRsp();
		result.setSize(size);
		result.setPage(page);
		List<RedisKeys> list= new ArrayList<>();
		if(size>MAX_COUNT) {
			size=MAX_COUNT;
		}
		if(key==null&&page==HOMEPAGE) {
			throw new BusinessException(Code.DATA_NULL);
		}
		RedisConncet redis=getServer(server);
		log.info("redis:{} server:{}", redis,server);
		if(page==HOMEPAGE&& !key.contains("*") &&accurate) {
			key="*"+key.trim()+"*";
		}
		if(page>HOMEPAGE) {
			if(cursor==null) {
				throw new BusinessException(Code.DATA_NULL);
			}else if(SCAN_POINTER_START.equals(cursor)) {
				throw new BusinessException(Code.DATA_END);
			}
		}else if(page==HOMEPAGE) {
			cursor=SCAN_POINTER_START;
			result.setCount(redis.dbsize());
		}
		RedisResult<String> data=redis.Scan(key, size,cursor);
		List<String> keylist=data.getKeys();
		cursor=data.getCursor();
		log.info("key：{} list:{}, cur:{}", key,keylist.size(),cursor);
		if(keylist.size()>0) {
			try {
				Pipeline p=getServer(server).getPipeline();
				List<Response<String>> tempType=new ArrayList<>(keylist.size());
				List<Response<Long>> tempTtl=new ArrayList<>(keylist.size());
				for (String k : keylist) {
					tempType.add(p.type(k));
					tempTtl.add(p.ttl(k));
				}
				p.sync();
				p.close();
				for (int i = 0; i < keylist.size(); i++) {
					RedisKeys redisKeys=new RedisKeys(keylist.get(i),tempType.get(i).get(),tempTtl.get(i).get());
					list.add(redisKeys);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(page==HOMEPAGE&&list.size()<size) {
			cursor=SCAN_POINTER_END;
		}
		result.setCursor(cursor);
		result.setList(list);
		return result;
	}


	public MemberRsp scanKeyMember(String server, String key, String match, String cursor, boolean accurate, int page, int size){
		RedisConncet redis=getServer(server.toLowerCase());
		if(!redis.exists(key)) {
			throw new BusinessException(Code.VALUE_NOT_EXIST);
		}
		if(size>MAX_COUNT) {
			size=MAX_COUNT;
		}
		MemberRsp rsp=new MemberRsp();
		rsp.setPage(page);
		rsp.setSize(size);
		log.info("redis:{} server:{}", redis,server);
		if(page==HOMEPAGE&& !key.contains("*") &&accurate) {
			key="*"+key.trim()+"*";
		}
		if(page>HOMEPAGE) {
			if(cursor==null) {
				throw new BusinessException(Code.DATA_NULL);
			}else if(SCAN_POINTER_START.equals(cursor)) {
				throw new BusinessException(Code.DATA_END);
			}
		}else if(page==HOMEPAGE) {
			cursor=SCAN_POINTER_START;
		}
		String type=redis.type(key);
		List<KeyMember> list=new ArrayList<>();
		switch (type) {
			case TypeSet:
				RedisResult<String> setResult=redis.sscan(key, match, size, cursor);
				for (String member : setResult.getKeys()) {
					list.add(new KeyMember(member));
				}
				rsp.setCursor(setResult.getCursor());
				rsp.setList(list);
				rsp.setCount(redis.scard(key));
				break;
			case TypeZset:
				RedisResult<Tuple> zsetResult=redis.Zscan(key, match, size, cursor);
				for (Tuple tuple : zsetResult.getKeys()) {
					list.add(new KeyMember(tuple.getElement(), tuple.getScore()));
				}
				rsp.setCursor(zsetResult.getCursor());
				rsp.setList(list);
				rsp.setCount(redis.zcard(key));
				break;
			case TypeHash:
				RedisResult<Entry<String, String>> hashResult=redis.hscan(key, match, size, cursor);
				for (Entry<String, String> kv : hashResult.getKeys()) {
					list.add(new KeyMember(kv.getKey(), kv.getValue()));
				}
				rsp.setCursor(hashResult.getCursor());
				rsp.setList(list);
				rsp.setCount(redis.hlen(key));
				break;

		}
		log.info("key：{} match :{} list:{}, cur:{}", key,match,list.size(),cursor);
		if(page==HOMEPAGE&&list.size()<size) {
			rsp.setCursor(SCAN_POINTER_END);
		}
		return rsp;
	}

	public  MemberRsp listKeyMember(String key,String server,int page,int sort,int size) {
		RedisConncet redis=getServer(server);
		if(!redis.exists(key)) {
			throw new BusinessException("键不存在");
		}
		if(size>MAX_COUNT) {
			size=MAX_COUNT;
		}
		MemberRsp rsp=new MemberRsp();
		String type=redis.type(key);
		rsp.setPage(page);
		rsp.setSize(size);
		if(type.equals(TypeZset)) {
			List<Tuple> set=null;
			int start=(page-1)*size;
			int end=start+size-1;
			if(sort==1) {//0降序 1升序
				set=redis.zrangeWithScores(key, start, end);
			}else {
				set=redis.zrevrangeWithScores(key, start, end);
			}
			List<KeyMember> list=new ArrayList<>();
			if(set!=null) {
				for (Tuple tuple : set) {
					list.add(new KeyMember(tuple.getElement(), tuple.getScore()));
				}
				rsp.setList(list);
			}
			rsp.setCount(redis.zcard(key));
		}else if(type.equals(TypeList)){
			long count=redis.llen(key);
			rsp.setCount(count);
			if(count==0) {
				rsp.setList(Collections.emptyList());
				return rsp;
			}
			int start=(page-1)*size;
			int end=start+size-1;
			if(sort==0) {//0降序 1升序
				int s=(int)count-(page-1)*size;
				int e=s-size+1;
				start=s*-1;
				end=e*-1;
				if(end>0) {
					end=0;
				}
			}
			List<String> list=redis.lrange(key, start, end);
			if(CollUtil.isNotEmpty(list)){
				List<KeyMember> result=list.stream().map(KeyMember::new).collect(Collectors.toList());
				rsp.setList(result);
			}else{
				rsp.setList(Collections.emptyList());
			}

		}
		return rsp;
	}


	public  void newkey(String server,String key,String type,String value,String member,int ttl) {
		RedisConncet redis=getServer(server);
		log.info("redis:{} server:{}", redis,server);
		if(ttl<0) {
			throw new BusinessException("过期时间不可小于0");
		}
		if(StrUtil.isBlank(key)) {
			throw new BusinessException("key不可以为空");
		}
		if(redis.exists(key)) {
			throw new BusinessException("已经存在的key");
		}
		switch (type) {
			case TypeString:
				if(StrUtil.isBlank(value)) {
					throw new BusinessException("value不可以为空");
				}
				redis.setString(key, value, ttl);
				break;
			case TypeSet:
				if(StrUtil.isBlank(member)) {
					throw new BusinessException("member不可以为空");
				}
				redis.sadd(key, member, ttl);
				break;
			case TypeZset:
				if(StrUtil.isBlank(value)) {
					throw new BusinessException("value不可以为空");
				}
				if(StrUtil.isBlank(member)) {
					throw new BusinessException("member不可以为空");
				}
				long score;
				try {
					score=Long.parseLong(value);
				} catch (NumberFormatException e) {
					throw new BusinessException("score为数值格式");
				}
				redis.zadd(key, score, member, ttl);
				break;
			case TypeHash:
				if(StrUtil.isBlank(value)) {
					throw new BusinessException("value不可以为空");
				}
				if(StrUtil.isBlank(member)) {
					throw new BusinessException("member不可以为空");
				}
				redis.hset(key, member, value, ttl);
				break;
			case TypeList:
				if(StrUtil.isBlank(value)) {
					throw new BusinessException("value不可以为空");
				}
				redis.lpush(key, member, ttl);
				break;
			default:
				throw new BusinessException("未知类型");
		}
	}





	public  void deleteKey(String key,String server) {
		RedisConncet redis=getServer(server);
		log.info("redis:{} server:{}", redis,server);
		redis.del(key);
	}


	public String getKeyType(String server,String key){
		RedisConncet conncet=getServer(server.toLowerCase());
		return conncet.type(key);
	}

	public long getKeyTtl(String server,String key){
		RedisConncet conncet=getServer(server.toLowerCase());
		return conncet.ttl(key);
	}

	public String getKeyValue(String server,String key){
		RedisConncet conncet=getServer(server.toLowerCase());
		return conncet.get(key);
	}
	public long updateKeyExptime(String server,String key,long second){
		RedisConncet conncet=getServer(server.toLowerCase());
		if(second>0){
			return conncet.expire(key,second);
		}else{
			return conncet.persist(key);
		}
	}

	public void addValueByKey(String key,String member,String value,String server,long ttl,boolean left)  {
//		if(StringUtils.isBlank(key)) {
//			throw new YzException(CODE.PARAMS_NULL);
//		}
//		if(StringUtils.isBlank(member)&&StringUtils.isBlank(value)) {
//			throw new YzException(CODE.PARAMS_NULL);
//		}
		RedisConncet conncet=getServer(server.toLowerCase());
		String type=conncet.type(key);
		if(!type.equals(TypeString)&&!conncet.exists(key)) {
			throw new BusinessException("键不存在");
		}
		switch (type) {
			case TypeString:
				if(StrUtil.isBlank(value)) {
					throw new BusinessException("value为空");
				}
				conncet.setString(key, value, ttl);
				break;
			case TypeSet:
				conncet.sadd(key, member, ttl);
				break;
			case TypeZset:
				long score;
				try {
					score=Long.parseLong(value);
				} catch (NumberFormatException e) {
					throw new BusinessException("数据错误");
				}
				conncet.zadd(key, score, member,ttl);
				break;
			case TypeHash:
				conncet.hset(key, member, value,ttl);
				break;
			case TypeList:
				if(left) {
					conncet.lpush(key, member,ttl);
				}else {
					conncet.rpush(key, member,ttl);
				}
				break;
		}
	}


	public void deleteValueByKey(String key,String member,String server,Integer count){
//		if(StringUtils.isBlank(member)) {
//			throw new YzException(CODE.PARAMS_NULL);
//		}
		RedisConncet redis=getServer(server.toLowerCase());
		if(!redis.exists(key)) {
			throw new BusinessException("键不存在");
		}
		String type=redis.type(key);
		switch (type) {
			case TypeSet:
				redis.srem(key, member);
				break;
			case TypeZset:
				redis.zrem(key, member);
				break;
			case TypeHash:
				redis.hdel(key, member);
				break;
			case TypeList:
				count=count==null?0: count;
				redis.lrem(key, member, count);
				break;
		}
	}

	public  String listSetMember(String key,String member,String server,int index) {
		RedisConncet redis=getServer(server.toLowerCase());
		if(!redis.exists(key)) {
			throw new BusinessException("键不存在");
		}
		return redis.lset(key, member, index);
	}


	public InfoRsp info(String server) {
		InfoRsp rsp=new InfoRsp();
		RedisConncet redis=getServer(server.toLowerCase());
		Map<String,String> info=parsing(redis.info(SERVER));
		info.putAll(parsing(redis.info(CLIENTS)));
		info.putAll(parsing(redis.info(MEMORY)));
		info.putAll(parsing(redis.info(STATS)));
		info.putAll(parsing(redis.info(COMMANDSTATS)));
		log.info(JSONObject.toJSONString(info));
		rsp.setVersion(info.get("redis_version"));
		rsp.setMode(info.get("redis_mode"));
		rsp.setOs(info.get("os"));
		String t=TimeUtils.getTimeDes(Long.parseLong(info.get("uptime_in_seconds")));
		rsp.setUptime(t);
		rsp.setClient(Integer.parseInt(info.get("connected_clients")));
		rsp.setMaxClient(Integer.parseInt(info.get("maxclients")==null?"0":info.get("maxclients")));
		rsp.setUserdMemory(info.get("used_memory_human"));
		rsp.setUserdMemoryRss(info.get("used_memory_rss_human"));
		rsp.setUserdMemoryPeak(info.get("used_memory_peak_human"));
		rsp.setSystemMemory(info.get("total_system_memory_human"));
		if(Integer.parseInt(info.get("maxmemory"))>0){
			rsp.setMaxMemory(info.get("maxmemory_human"));
		}else{
			rsp.setMaxMemory("未设置");
		}
		rsp.setKeyspaceHits(Long.parseLong(info.get("keyspace_hits")));
		rsp.setKeyspaceMisses(Long.parseLong(info.get("keyspace_misses")));
		rsp.setHitRate("100%");
		if(rsp.getKeyspaceMisses()>0){
			double total=rsp.getKeyspaceHits() + rsp.getKeyspaceMisses();
			double hitrate=  rsp.getKeyspaceHits()/total;
			rsp.setHitRate(String.format("%.2f",hitrate*100)+"%");
		}
		return rsp;
	}

	private Map<String,String> parsing(String info){
		Map<String,String> map=new HashMap<>();
		if(StrUtil.isBlank(info)){
			return map;
		}
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(info.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8));
			String line;
			while ( (line = br.readLine()) != null ) {
				if(StrUtil.isNotBlank(line)&& line.contains(":")){
					String[] kv=line.split(":");
					if(kv.length>1){
						map.put(kv[0],kv[1]);
					}else{
						map.put(kv[0],"");
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return map;
	}



}
