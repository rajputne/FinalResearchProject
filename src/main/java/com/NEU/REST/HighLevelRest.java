/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.NEU.REST;

import java.io.IOException;
import org.apache.http.HttpHost;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.get.GetField;

/**
 *
 * @author User
 */
public class HighLevelRest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
      RestHighLevelClient client = new RestHighLevelClient(
        RestClient.builder(
                new HttpHost("localhost", 9200, "http")));
      GetRequest getRequest = new GetRequest(
        "mycontracts", 
        "contract",  
        "1"); 
      GetResponse getResponse = client.get(getRequest);
      
      
      getRequest.storedFields("tableOfContent"); 
GetResponse getResponse1 = client.get(getRequest);
Object message = getResponse1.getField("tableOfContent").getValue();
      System.out.println(message.toString());
      client.close();
    }
    
}
