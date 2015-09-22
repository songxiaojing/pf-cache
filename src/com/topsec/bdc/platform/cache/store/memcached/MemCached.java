package com.topsec.bdc.platform.cache.store.memcached;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;
import com.topsec.bdc.platform.cache.store.ICache;
import com.topsec.bdc.platform.core.services.ServiceInfo;


/**
 * @author baiyanwei the MemCached.
 */
@ServiceInfo(description = "Memcached for Platform", configurationPath = "application/services/cacheService/memCached/")
public class MemCached implements ICache {

    // the cache server list.
    @XmlElement(name = "serverList", type = List.class)
    private List<String> _serverList = new ArrayList<String>();
    //
    private SockIOPool _pool = null;
    private MemCachedClient _mc = null;

    @Override
    public void remove(String key) {

        if (key == null) {
            return;
        }
        _mc.delete(key);
    }

    @Override
    public void set(String key, Object value) {

        if (key == null) {
            return;
        }
        _mc.set(key, value);
    }

    @Override
    public Object get(String key) {

        if (key == null) {
            return null;
        }
        return _mc.get(key);
    }

    @Override
    public void start() throws Exception {

        if (this._serverList == null || this._serverList.isEmpty()) {
            throw new Exception();
        }
        // read the Memcached server list from configuration.
        String[] cacheServers = this._serverList.toArray(new String[this._serverList.size()]);
        _pool = SockIOPool.getInstance();
        _pool.setServers(cacheServers);
        //
        _pool.setInitConn(5);
        _pool.setMinConn(5);
        _pool.setMaxConn(50);
        _pool.setMaintSleep(30);
        //
        _pool.setNagle(false);
        _pool.initialize();
        //
        _mc = new MemCachedClient();
    }

    @Override
    public void stop() throws Exception {

        _mc = null;
        _pool.shutDown();
    }
}
