package com.topsec.bdc.platform.cache;

import org.osgi.framework.BundleContext;

import com.topsec.bdc.platform.cache.services.CacheService;
import com.topsec.bdc.platform.core.activator.PlatformActivator;
import com.topsec.bdc.platform.log.PlatformLogger;

public class Activator extends PlatformActivator {

	 /**
     * logger.
     */
    final private static PlatformLogger _logger = PlatformLogger.getLogger(Activator.class);
	
    private static BundleContext context;

    public static BundleContext getContext() {

        return context;
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext bundleContext) throws Exception {

        Activator.context = bundleContext;
        
        // register message formatter service
        this.registerService(new CacheService());

        //
        _logger.info("Platform cacheService is started.");
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext bundleContext) throws Exception {

        Activator.context = null;
        
        _logger.info("Platform cacheService is stopped.");
    }

}
