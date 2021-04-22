/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pametnakucauredjaj;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.commons.codec.binary.Base64;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.client5.http.fluent.Response;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.util.Timeout;

/**
 *
 * @author adinc
 */
public class HttpClient {
    
    private static String encodeCredentials(String username, String password){
        String credentials = username + ":" + password;
        byte[] encodedBytes = Base64.encodeBase64(credentials.getBytes());
        String fullEncodedCredentials = "Basic " + new String(encodedBytes);
        return fullEncodedCredentials;
    }
    
    private static String getResponse(Response r) throws IOException{
        return r.handleResponse(new HttpClientResponseHandler<String>(){
            public String handleResponse(ClassicHttpResponse chr) throws HttpException, IOException{
                BufferedReader reader = new BufferedReader(new InputStreamReader(chr.getEntity().getContent()));
                StringBuilder sb = new StringBuilder();
                for(String s = reader.readLine(); s != null;){
                    sb.append(s);
                    s = reader.readLine();
                    if(s != null)
                        sb.append("\r\n");
                }
                return sb.toString();
            }
        });
    }
    
    public static String handleGetRequest(String uri, String username, String password){
        try {
            return getResponse(Request.get(uri).responseTimeout(Timeout.ofSeconds(20)).addHeader(HttpHeaders.AUTHORIZATION,encodeCredentials(username, password)).execute());
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return "Nepoznata greska";
        }
    }
    
    public static String handlePostRequest(String uri, String username, String password){
        try {
            return getResponse(Request.post(uri).responseTimeout(Timeout.ofSeconds(20)).addHeader(HttpHeaders.AUTHORIZATION,encodeCredentials(username, password)).execute());
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return "Nepoznata greska";
        }
    }
    
    public static String handlePutRequest(String uri, String username, String password){
        try {
            return getResponse(Request.put(uri).responseTimeout(Timeout.ofSeconds(20)).addHeader(HttpHeaders.AUTHORIZATION,encodeCredentials(username, password)).execute());
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return "Nepoznata greska";
        }
    }
    
    public static String handleDeleteRequest(String uri, String username, String password){
        try {
            return getResponse(Request.delete(uri).responseTimeout(Timeout.ofSeconds(20)).addHeader(HttpHeaders.AUTHORIZATION,encodeCredentials(username, password)).execute());
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return "Nepoznata greska";
        }
    }
}
