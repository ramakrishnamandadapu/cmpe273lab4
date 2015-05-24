package edu.sjsu.cmpe.cache.client;

import java.util.ArrayList;
import java.util.List;


public class DistributedCacheService implements CacheServiceInterface {
    
	ArrayList<String> cacheServerUrl =  new ArrayList<String>();
	CRDTClient cc;
	
    public DistributedCacheService( ArrayList<String> urlList) {
        this.cacheServerUrl = urlList;
		cc = new CRDTClient(urlList);
    }

    @Override
    public String get(long key) {
		return cc.getValues(key);
    }

    @Override
    public void put(long key, String value) {
       List<String> response = cc.putAsync(key, value);
		   int min = response.size()/2 ;
           if (response.size() >= min) {
               delete(key); // deleting inserted values
           }
       }

    @Override
    public void delete(long key) {
        if(cc.deleteVal(key))
		{
			 System.out.println("deleted");
		}
		else
		{
			 System.out.println("error in deleting");
		}
    }

}
