package com.topsec.bdc.platform.cache.store.redis;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import redis.clients.jedis.JedisCommands;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.util.Pool;

import com.topsec.bdc.platform.cache.store.ICache;
import com.topsec.bdc.platform.core.services.ServiceInfo;
import com.topsec.bdc.platform.core.utils.Assert;
import com.topsec.bdc.platform.log.PlatformLogger;


/**
 * @author ctc the redis cache.
 */
@ServiceInfo(description = "Redsi cached for Platform", configurationPath = "application/services/cacheService/RedisCached/")
public class RedisCached implements ICache {
	
	private static PlatformLogger _logger = PlatformLogger.getLogger(RedisCached.class);
	
    // the cache server list.
    @XmlElement(name = "serverList", type = List.class)
    private List<String> _serverList = new ArrayList<String>();
    //
	private Pool _pool = null;
    
    public void close(JedisCommands jedis) {
		if (jedis != null) {
			_pool.returnResource(jedis);
		}
	}
    
    private JedisCommands getJedis() {
		return (JedisCommands) _pool.getResource();
	}

    @Override
    public void remove(String key) {

        if (key == null) {
            return;
        }
        JedisCommands jedis = null;
        try {
        	jedis = this.getJedis();
        	jedis.del(key);
        }catch(Exception e) {
        	e.printStackTrace();
        }finally {
        	close(jedis);
        }
    }

    @Override
    public void set(String key, Object value) {

        if (key == null) {
            return;
        }
        JedisCommands jedis = null;
        try {
        	jedis = this.getJedis();
        	jedis.set(key, value.toString());
        }catch(Exception e) {
        	e.printStackTrace();
        }finally {
        	close(jedis);
        }
    }
    @Override
    public Object get(String key) {

        if (key == null) {
            return null;
        }
        JedisCommands jedis = null;
        try {
        	jedis = this.getJedis();
        	return jedis.get(key);
        }catch(Exception e) {
        	e.printStackTrace();
        }finally {
        	close(jedis);
        }
        return null;
    }
    
    /**
     * 构建切片Jedis.
     * @return
     */
    private List<JedisShardInfo> bulidJedisShardInfoList() {
    	
    	List<JedisShardInfo> redisLists = new ArrayList<JedisShardInfo>();
    	
    	if(_serverList.size() <= 0){
    		return redisLists;
    	}
    	
		for(int i=0; i<_serverList.size(); i++) {
			String[] hostAndPort = _serverList.get(i).split(":");
			if(null == hostAndPort || 2 != hostAndPort.length) {
				continue;
			}
			if(Assert.isEmptyString(hostAndPort[0])) {
            	continue;
            }
            int port = Protocol.DEFAULT_PORT;
            try {
              port = Integer.parseInt(hostAndPort[1]);
            } catch (Exception e) {
            	_logger.exception("解析redis端口错误：" + _serverList.get(i), e);
            }
            redisLists.add(new JedisShardInfo(hostAndPort[0],port));
		}
		
		return redisLists;
	}
    
    @Override
    public void start() throws Exception {

        if (this._serverList == null || this._serverList.isEmpty()) {
            throw new Exception();
        }
        
        // read the redis server list from configuration.
        JedisPoolConfig conf = new JedisPoolConfig();
        //最大连接数, 默认8个
        conf.setMaxTotal(50);
        //最大空闲连接数, 默认8个
        conf.setMaxIdle(8);
        //在获取连接的时候检查有效性, 默认false
        conf.setTestOnBorrow(true);
        //获取连接时的最大等待毫秒数(如果设置为阻塞时BlockWhenExhausted),如果超时就抛异常, 小于零:阻塞不确定的时间,  默认-1
        conf.setMaxWaitMillis(6000);
        
        //_pool = new JedisPool(poolConfig, "192.168.56.8");
        _pool = new ShardedJedisPool(conf, bulidJedisShardInfoList());
    }

    @Override
    public void stop() throws Exception {

        _pool.destroy();
    }
}
