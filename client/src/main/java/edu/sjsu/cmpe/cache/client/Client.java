package edu.sjsu.cmpe.cache.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) throws Exception {
        System.out.println("Starting Cache Client...");
        List<String> instances = new ArrayList<String>();
        instances.add("http://localhost:3000");
		instances.add("http://localhost:3001");
		instances.add("http://localhost:3002");
		
        CacheServiceInterface cache = new DistributedCacheService(instances);

        Scanner scanner=new Scanner(System.in);
        
        cache.put(1,"a");
        System.out.println("put(1 => a)");

        //Thread.sleep(30000);
        System.out.println("Successfully published values to Servers..");
        System.out.println("Please Shutdown one server and Press any key to continue..");
        scanner.nextLine();

        cache.put(1,"b");
        System.out.println("put(1 => b)");

        //Thread.sleep(30000);
        System.out.println("Please Bring back server and Press any key to continue..");
        scanner.nextLine();
        System.out.println("Trying to get value and repairing inconsistent data");
        System.out.println("get(1) => " + cache.get(1)); 
        
        scanner.close();
        System.out.println("Existing Cache Client...");
    }

}
