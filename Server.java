/**
 * The Server class sets up a ServerSocket and waits for clients to connect.
 * When a client connects, it creates a new Clientmanager thread to handle communication with that client.
 * @authors	Migael Van Wyk 24716812@sun.ac.za - Project Manager
 *		Jurianne Schreuder 24695351@sun.ac.za - Testing
 *		Stefan Russouw 24715034@sun.ac.za - Client
 *		Hardus Viviers 24917540@sun.ac.za - GUI design
 *		Louis De Jager 24908134@sun.ac.za - Server
 * @version 1.0
 * @since 2023-02-28
 */

import java.io.IOException;
import java.net.SocketException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.util.HashMap;

/**
 * The main method of the Server class.
 * Sets up a ServerSocket and waits for clients to connect.
 * When a client connects, it creates a new Clientmanager thread to handle communication with that client.
 *
 * @param args the command line arguments
 */
public class Server {
    public static void main(String[] args) {
        System.out.println("Waiting connection...");

        // Making of server and accepting connection from the client
        try {
            ServerSocket ss = new ServerSocket(2560);
            
			// Create a list of users
			HashMap<String, ObjectOutputStream> userList = new HashMap<String, ObjectOutputStream>();

			// Loop to accept multiple clients
			while(!ss.isClosed()) {

				Socket s = ss.accept();
            	
				// Create a clientmanager on a new thread
				Clientmanager client = new Clientmanager(s, userList, ss); 
				new Thread(client).start();

			}
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
