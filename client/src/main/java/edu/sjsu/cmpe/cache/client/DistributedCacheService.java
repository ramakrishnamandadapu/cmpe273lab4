package edu.sjsu.cmpe.cache.client;

import java.util.List;

/**
 * Distributed cache service
 * 
 */
public class DistributedCacheService implements CacheServiceInterface {
	
	List<String> cacheServerUrlList ;
	CRDTController crdtController;

    public DistributedCacheService(List<String> cacheServerUrlList) {
        this.cacheServerUrlList = cacheServerUrlList;
        this.crdtController=new CRDTController(cacheServerUrlList);
    }

    /**
     * @see edu.sjsu.cmpe.cache.client.CacheServiceInterface#get(long)
     */
    @Override
    public String get(long key) {
        return crdtController.get(key);
    }

    /**
     * @see edu.sjsu.cmpe.cache.client.CacheServiceInterface#put(long,
     *      java.lang.String)
     */
    @Override
    public void put(long key, String value) {
         crdtController.put(key, value);
    }

}
