package edu.sjsu.cmpe.cache.client;

/**
 * Cache Service Interface
 * 
 */
public interface CacheServiceInterface 
{
	public void delete(long key);  // adding delete method signature
	public String get(long key);
    public void put(long key, String value);
}
