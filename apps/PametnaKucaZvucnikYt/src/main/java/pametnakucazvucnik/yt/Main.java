/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pametnakucazvucnik.yt;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author adinc
 */
public class Main {
    public static void main(String[] args){
        
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(5656);
            while(true){
                Socket clientSocket = serverSocket.accept();
                new RequestHandler(clientSocket).start();
            }
        }catch(IOException e){
            System.out.println("Greska pri povezivanju sa klijentu");
        }
        finally{
            try {
                if(serverSocket !=null)
                    serverSocket.close();
            }
            catch (IOException ex) {
                System.out.println("Greska pri zatvaranju server socketa");
            }
        }
    }
}
