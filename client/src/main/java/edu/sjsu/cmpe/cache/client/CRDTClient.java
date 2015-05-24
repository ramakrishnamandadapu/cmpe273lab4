package edu.sjsu.cmpe.cache.client;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class CRDTClient
{
	ArrayList<String> caches;
	public CRDTClient(ArrayList<String> instances)
	{
		 caches =  new ArrayList<String>(instances);
	}

	public List<String> putAsync(long key, String value) {
	
        List<Future<HttpResponse<JsonNode>>> futures = new ArrayList<Future<HttpResponse<JsonNode>>>();
        List<String> putError = new ArrayList<String>();
		HttpResponse<JsonNode> response = null;

        for (String cacheUrl : this.caches) {
            Future<HttpResponse<JsonNode>> request = Unirest.put(cacheUrl + "/cache/{key}/{value}").
            		header("accept", "application/json").routeParam("key", Long.toString(key)).routeParam("value", value).asJsonAsync();
			try {
                response = request.get();
            } 
			catch (InterruptedException e) 
			{
               putError.add(e.getMessage());
            } 
			catch (ExecutionException e) 
			{
                putError.add(e.getMessage());
            }
        }
		
        return putError;
    }
	
   public String getValues(long key) {
   
        Map<String, Integer> returnValue = new HashMap<String, Integer>();
		Map<String, Integer> mapValueToCount = new HashMap<String, Integer>();
		String final_value="";
        int value_count=0, max=0;
		
        for (String cacheUrl : this.caches) {
			
            Future<HttpResponse<JsonNode>> request = Unirest.get(cacheUrl + "/cache/{key}")
                    .header("accept", "application/json")
                    .routeParam("key", Long.toString(key)).asJsonAsync();
					
					 HttpResponse<JsonNode> response = future.get();
                try {
                    String value = request.getBody().getObject().getString("value");
                    int temp_count = mapValueToCount.getOrDefault(value, 0);
                    temp_count++;
                    mapValueToCount.put(value, temp_count);
                } catch(NullPointerException e) 
				{
					return e.getMessage();
				}	    
        }

        for (String strkey : mapValueToCount.keySet()) {
			value_count = mapValueToCount.get(strkey);
           if (value_count > max) {
			   final_value = strkey;
               max = value_count;
           }
        }
		
        if (max >= 2) 
		{
            put(key,  final_value);
            return  final_value;
        } 
		else 
		{
            return "Majority not found";
        }
    }

   public Boolean deleteVal(long key) {
        if (this.caches.length > 0) {
            for (String url : caches) {
                try {
                    Unirest.delete(url + "/cache/{key}").header("accept", "application/json").routeParam("key", Long.toString(key)).asJson();
					return true;
                } 
				catch (UnirestException e) 
				{
                    return false;
                }
            }
        }
    }	
}