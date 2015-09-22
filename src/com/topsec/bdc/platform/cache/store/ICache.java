package com.topsec.bdc.platform.cache.store;

import com.topsec.bdc.platform.core.services.IService;


/**
 * @author baiyanwei interface for the platform cache pool
 */
public interface ICache extends IService {

    /**
     * @param key
     *            remove one item in cache
     */
    public void remove(String key);

    /**
     * @param key
     * @param value
     *            add one item into cache ,if key is exist ,update it.
     */
    public void set(String key, Object value);

    /**
     * @param key
     * @return query a item for a key in cache.
     */
    public Object get(String key);
}
