package com.topsec.bdc.platform.cache.services;

import javax.xml.bind.annotation.XmlElement;

import com.topsec.bdc.platform.cache.Activator;
import com.topsec.bdc.platform.core.metrics.AbstractMetricMBean;
import com.topsec.bdc.platform.core.metrics.MetricUtils;
import com.topsec.bdc.platform.core.services.IService;
import com.topsec.bdc.platform.core.services.PropertyLoaderService;
import com.topsec.bdc.platform.core.services.ServiceHelper;
import com.topsec.bdc.platform.core.services.ServiceInfo;
import com.topsec.bdc.platform.log.PlatformLogger;


/**
 * @author baiyanwei platform cache pool management
 */
@ServiceInfo(description = "Platform Cache Service", configurationPath = "application/services/cacheService/")
public class CacheService extends AbstractMetricMBean implements IService {

    private static PlatformLogger _logger = PlatformLogger.getLogger(CacheService.class);
    private ICache _cache = null;

    @XmlElement(name = "currentCache", type = String.class, defaultValue = "com.byw.platform.cache.services.MemCached")
    private String _currentCache = "com.byw.platform.cache.services.MemCached";

    @Override
    public void start() throws Exception {

        this._cache = buildCacheInstance();
        this._cache.start();
        MetricUtils.registerMBean(this);
        _logger.info("CacheService is stared!");
    }

    @Override
    public void stop() throws Exception {

        // TODO Auto-generated method stub
        this._cache.stop();
        this._cache = null;
        _logger.info("CacheService is stop!");
    }

    /**
     * build a cache instance with class name.
     * 
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    private ICache buildCacheInstance() throws InstantiationException, IllegalAccessException, ClassNotFoundException {

        Class<?> cacheCalzz = Activator.getContext().getBundle().loadClass(_currentCache);
        if (isICacheImplementClass(cacheCalzz) == false) {
            throw new InstantiationException("The cache need to implement the " + ICache.class);
        }
        PropertyLoaderService propertyLoaderService = ServiceHelper.findService(PropertyLoaderService.class);
        if (propertyLoaderService == null) {
            throw new InstantiationException("Can't find the PropertyLoaderService, Server can't start without PropertyLoaderService.");
        }
        //
        Object cacheObject = cacheCalzz.newInstance();
        //
        propertyLoaderService.injectServiceProperties((ICache) cacheObject);
        //
        return (ICache) cacheObject;
    }

    /**
     * @return get the Platform Cache.
     */
    public ICache getCache() {

        return this._cache;
    }

    /**
     * @param clazz
     * @return is a implement of the ICache interface
     */
    public boolean isICacheImplementClass(Class<?> clazz) {

        if (clazz == null) {
            return false;
        }
        Class<?>[] interfaces = clazz.getInterfaces();
        if (interfaces == null) {
            return false;
        }
        for (int i = 0; i < interfaces.length; i++) {
            if (interfaces[i].getName().equals(ICache.class.getName())) {
                return true;
            }
        }
        return false;
    }
}
