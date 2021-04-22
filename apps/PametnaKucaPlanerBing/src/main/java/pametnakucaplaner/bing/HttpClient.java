/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pametnakucaplaner.bing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.client5.http.fluent.Response;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.util.Timeout;

/**
 *
 * @author adinc
 */
public class HttpClient {
    
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
    
    public static String handleGetRequest(String uri){
        try {
            return getResponse(Request.get(uri).responseTimeout(Timeout.ofSeconds(30)).execute());
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return "Nepoznata greska";
        }
    }
}
