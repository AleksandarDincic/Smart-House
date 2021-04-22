/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pametnakucazvucnik.yt;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

/**
 *
 * @author adinc
 */
public class RequestHandler extends Thread {
    
    private final Socket clientSocket;
    
    private static YouTube youtube;
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();
    
    public RequestHandler(Socket clientSocket){
        this.clientSocket = clientSocket;
    }
    
    @Override
    public void run(){
        
        if(youtube == null)
            youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, (HttpRequest request) -> {
            }).setApplicationName("pametnaKuca").build();
        
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(),true);
            String searchTerm = reader.readLine();
            
            YouTube.Search.List search = youtube.search().list("id");

            search.setKey("AIzaSyDlkZCgyXm_aLU0EfIZXKM00YisJ3K7FxU");
            search.setQ(searchTerm);
            search.setType("video");
            search.setFields("items(id/kind,id/videoId)");
            search.setMaxResults(1L);

            SearchListResponse searchResponse = search.execute();
            List<SearchResult> searchResultList = searchResponse.getItems();
            if (searchResultList != null && searchResultList.size() > 0) {
                SearchResult retVal = searchResultList.get(0);
                writer.println(retVal.getId().getVideoId());
            }
            else writer.println("");
        } catch (IOException ex) {
            System.out.println("Greska pri povezivanju sa YT");
        } 
        finally {
            try {
                clientSocket.close();
            } catch (IOException ex) {
                System.out.println("Greska pri zatvaranju socketa");
            }
        }
    }
}
