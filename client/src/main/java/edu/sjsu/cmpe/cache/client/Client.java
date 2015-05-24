package edu.sjsu.cmpe.cache.client;
import java.util.ArrayList;

public class Client {

    public static void main(String[] args) throws Exception {
	
        System.out.println("Starting Cache Client...");
		
		ArrayList<String> instances = new ArrayList<String>();
        instances.add("http://localhost:3000");
		instances.add("http://localhost:3001");
		instances.add("http://localhost:3002");
		
        DistributedCacheService cache = new DistributedCacheService(instances);

        cache.put(1,"a");
        System.out.println("putting a to key 1");

        Thread.sleep(30000);

        cache.put(1,"b");
        System.out.println("putting b to key 1" );

        Thread.sleep(30000); 

        System.out.println("get(1) => " + cache.get(1)); 

        //System.out.println("deleting value" + cache.delete(1);

    }

}
