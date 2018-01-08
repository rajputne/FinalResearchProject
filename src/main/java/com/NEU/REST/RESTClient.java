/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.NEU.REST;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.RequestLine;
import org.apache.http.util.EntityUtils;

import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;

/**
 *
 * @author User
 */
public class RESTClient {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {

        // TODO code application logic here
        //Low Level REST API
        HttpHost hp = new HttpHost("localhost", 9200, "http");
        RestClient restClient = RestClient.builder(
                hp).build();
        Response response = restClient.performRequest("GET", "/");
        RequestLine requestLine = response.getRequestLine();
        HttpHost host = response.getHost();
        int statusCode = response.getStatusLine().getStatusCode();
        Header[] headers = response.getHeaders();
        String responseBody = EntityUtils.toString(response.getEntity());
        System.out.println(responseBody);
        restClient.close();

        
    }

}
