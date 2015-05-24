package edu.sjsu.cmpe.cache.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.json.JSONException;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

/**
 * Distributed cache service
 * 
 */
public class CRDTController {

	List<String> cacheServerUrlList;
	CRDTController crdtController;

	public CRDTController(List<String> cacheServerUrlList) {
		this.cacheServerUrlList = cacheServerUrlList;
	}

	/**
	 * @see edu.sjsu.cmpe.cache.client.CacheServiceInterface#get(long)
	 */
	public String get(long key) {
		List<Future<HttpResponse<JsonNode>>> futureList = asyncGet(key);
		
		Map<String,Integer> valeCountMap=new HashMap<String, Integer>();
		
		int count = 0;
		for(;count<3;){
		  count = 0;
		for (Future<HttpResponse<JsonNode>> future : futureList) {
			if(future.isDone())
				count++;
			if(count==3)
				break;
		}
		}
		
		for (Future<HttpResponse<JsonNode>> future : futureList) {
			try {
				HttpResponse<JsonNode> httpResponse = future.get();
				if(httpResponse.getCode()==200){
				String value = httpResponse.getBody().getObject().getString("value");
				if(valeCountMap.containsKey(value)){
					int tempCount=valeCountMap.get(value);
					tempCount++;
					valeCountMap.put(value, tempCount);
				}
				else {
					valeCountMap.put(value, 1);
				}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
		
		int max_count=0;
		String final_value = "";
		for (String value : valeCountMap.keySet()) {
			int value_count = valeCountMap.get(value);
           if (value_count > max_count) {
			   final_value = value;
               max_count = value_count;
           }
        }
		
        if (max_count < cacheServerUrlList.size()) 
            put(key,  final_value);
            
        return  final_value;

	}

	private List<Future<HttpResponse<JsonNode>>> asyncGet(long key){
		List<Future<HttpResponse<JsonNode>>> futureList = new ArrayList<Future<HttpResponse<JsonNode>>>();
		for (String cacheServerUrl : cacheServerUrlList) {
			Future<HttpResponse<JsonNode>> future = Unirest.get(cacheServerUrl + "/cache/{key}")
					.header("accept", "application/json")
					.routeParam("key", Long.toString(key)).asJsonAsync();
			futureList.add(future);
		}
		return futureList;
	}
	
	
	
	private List<Future<HttpResponse<JsonNode>>> asyncPut(long key, String value){
		List<Future<HttpResponse<JsonNode>>> futureList = new ArrayList<Future<HttpResponse<JsonNode>>>();
		for (String cacheServerUrl : cacheServerUrlList) {
			Future<HttpResponse<JsonNode>> future = Unirest
					.put(cacheServerUrl + "/cache/{key}/{value}")
					.header("accept", "application/json")
					.routeParam("key", Long.toString(key))
					.routeParam("value", value)
					.asJsonAsync();
			futureList.add(future);
		}
		return futureList;
	}
	/**
	 * @see edu.sjsu.cmpe.cache.client.CacheServiceInterface#put(long,
	 *      java.lang.String)
	 */
	public void put(long key, String value) {
		List<Future<HttpResponse<JsonNode>>> futureList = asyncPut(key, value);
		
		int count = 0;
		for(;count<3;){
		  count = 0;
		for (Future<HttpResponse<JsonNode>> future : futureList) {
			if(future.isDone())
				count++;
			if(count==3)
				break;
		}
		}
		
		
		int successCount = 0;
		for (Future<HttpResponse<JsonNode>> future : futureList) {
			try {
				if (future.get().getCode() == 200)
					successCount++;
			} catch (InterruptedException e1) {
				System.out.println("Can not establish connection with one of the server");
			} catch (ExecutionException e1) {
				System.out.println("Can not establish connection with one of the server");
			}
		}

		if (successCount >= cacheServerUrlList.size() / 2)
			return;
		else
			delete(key);

	}

	public void delete(long key) {
		for (String url : cacheServerUrlList) {
            
                try {
					Unirest.delete(url + "/cache/{key}").header("accept", "application/json").routeParam("key", Long.toString(key)).asJson();
				} catch (UnirestException e) {
					e.printStackTrace();
				}
        }
	}
}
