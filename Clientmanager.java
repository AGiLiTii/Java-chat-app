/**
 * The Clientmanager class is responsible for managing each client connection to the server.
 * It receives and sends packets to/from clients, adds clients to a list, and removes them when they disconnect.
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
import java.util.Set;

/**
 * Constructs a new Clientmanager object.
 * 
 * @param socket The Socket object representing the client connection
 * @param userList A HashMap representing the list of users connected to the server
 * @param ss The ServerSocket object representing the server's listening socket
 */
public class Clientmanager implements Runnable {

	Socket socket;
	String username;
	HashMap<String, ObjectOutputStream> userList;
	ServerSocket ss;
	Packet packet;

	// Constructor
	public Clientmanager(Socket socket, HashMap<String, ObjectOutputStream> userList, ServerSocket ss) {
		this.socket = socket;
		this.userList = userList;
		this.ss = ss;
	}

/**
 * Sends a packet to all clients in the user list.
 * 
 * @param toSelf A boolean indicating whether to send the packet to the sender as well
 * @param packet The Packet object containing the message to send
 */
	public void broadcast(Boolean toSelf, Packet packet) {
		for (String i : userList.keySet()) {

			if (i == username && !toSelf) {
				continue;
			}

			ObjectOutputStream currentOut = userList.get(i);

			try {
				currentOut.writeObject(packet);
			} catch (IOException e) {
				terminate();
			}

		}

	}

/**
 * The run() method is executed when the Clientmanager thread starts.
 * It receives messages from clients and handles them accordingly.
 */
	public void run() {

		try {

			// Create object input and output streams
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

			// Get username from client and add to user list
			while (socket.isConnected()) {

				try {
					packet = (Packet) in.readObject();
				} catch (Exception e) {
					terminate();
				}

				username = packet.getUsername();

				// Check for duplicates
				if (!userList.containsKey(username)) {
					userList.put(username, out);
					System.out.println(username + " has connected");
					out.writeObject("success");

					String[] users = new String[userList.keySet().size()];
					userList.keySet().toArray(users);
					packet = new Packet(users, true, username, "");
					broadcast(true, packet);

					break;
				} else {
					System.out.println("duplicate user detected!");
					out.writeObject("err");
				}
			}

			System.out.println("The current user list = " + userList);

			// Waiting for new messages from client and to stop the server
			new Thread(() -> {

				try {
					while (socket.isConnected()) {
						try {
							packet = (Packet) in.readObject();
						} catch (Exception e) {
							terminate();
							return;
						}

						// Check for disconnect
						if (packet.getMessage().equals("Bye")) {
							terminate();

							// Send userlist to all other users
							String[] users = new String[userList.keySet().size()];
							userList.keySet().toArray(users);
							packet = new Packet(users, true, "", packet.username);
							broadcast(false, packet);

							System.out.println("The current user list = " + userList);

							// Close server if last user disconnected
							if (userList.isEmpty()) {
								ss.close();
								System.exit(0);
							}
							return;
						}

						// Whisper and other commands
						if (packet.getMessage().charAt(0) == '/') {

							if (packet.getMessage().length() < 2) {
								continue;
							}

							if (packet.getMessage().charAt(1) == 'w') {
							
								// Send whisper message
								String toUser = "";

								if (packet.getMessage().length() < 4) {
									continue;
								}

								if (packet.getMessage().charAt(3) == ' ') {
									continue;
								}

								// Find username
								int i = 3;
								while (packet.getMessage().charAt(i) != ' ') {
									toUser += packet.getMessage().charAt(i);
									i++;

									if (packet.getMessage().length() == i) {
										break;
									}
								}

								// Check if user exists
								if (!userList.keySet().contains(toUser)) {
									continue;
								}

								// Check if a message was typed
								if (packet.getMessage().length() == i || packet.getMessage().length() == i + 1) {
									continue;
								}

								i++;
								String msg = packet.getMessage().substring(i, packet.getMessage().length());

								// Create a new packet
								packet = new Packet(username, msg, false, false, true);

								// Send the packet
								ObjectOutputStream currentOut = userList.get(toUser);
								currentOut.writeObject(packet);

								continue;
							}

							if (packet.getMessage().charAt(1) == 'a') {

								String msg = "\n" +
										" ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⣤⣤⣤⣤⣤⣶⣦⣤⣄⡀⠀⠀⠀⠀⠀⠀⠀ \n" +
										"⠀⠀⠀⠀⠀⠀⠀⠀⢀⣴⣿⡿⠛⠉⠙⠛⠛⠛⠛⠻⢿⣿⣷⣤⡀⠀⠀⠀⠀⠀\n" +
										"⠀⠀⠀⠀⠀⠀⠀⠀⣼⣿⠋⠀⠀⠀⠀⠀⠀⠀⢀⣀⣀⠈⢻⣿⣿⡄⠀⠀⠀⠀\n" +
										"⠀⠀⠀⠀⠀⠀⠀⣸⣿⡏⠀⠀⠀⣠⣶⣾⣿⣿⣿⠿⠿⠿⢿⣿⣿⣿⣄⠀⠀⠀\n" +
										"⠀⠀⠀⠀⠀⠀⠀⣿⣿⠁⠀⠀⢰⣿⣿⣯⠁⠀⠀⠀⠀⠀⠀⠀⠈⠙⢿⣷⡄⠀\n" +
										"⠀⠀⣀⣤⣴⣶⣶⣿⡟⠀⠀⠀⢸⣿⣿⣿⣆⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣿⣷⠀\n" +
										"⠀⢰⣿⡟⠋⠉⣹⣿⡇⠀⠀⠀⠘⣿⣿⣿⣿⣷⣦⣤⣤⣤⣶⣶⣶⣶⣿⣿⣿⠀\n" +
										"⠀⢸⣿⡇⠀⠀⣿⣿⡇⠀⠀⠀⠀⠹⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡿⠃⠀\n" +
										"⠀⣸⣿⡇⠀⠀⣿⣿⡇⠀⠀⠀⠀⠀⠉⠻⠿⣿⣿⣿⣿⡿⠿⠿⠛⢻⣿⡇⠀⠀\n" +
										"⠀⣿⣿⠁⠀⠀⣿⣿⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢸⣿⣧⠀⠀\n" +
										"⠀⣿⣿⠀⠀⠀⣿⣿⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢸⣿⣿⠀⠀\n" +
										"⠀⣿⣿⠀⠀⠀⣿⣿⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢸⣿⣿⠀⠀\n" +
										"⠀⢿⣿⡆⠀⠀⣿⣿⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢸⣿⡇⠀⠀\n" +
										"⠀⠸⣿⣧⡀⠀⣿⣿⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣿⣿⠃⠀⠀\n" +
										"⠀⠀⠛⢿⣿⣿⣿⣿⣇⠀⠀⠀⠀⠀⣰⣿⣿⣷⣶⣶⣶⣶⠶⠀⢠⣿⣿⠀⠀⠀\n" +
										"⠀⠀⠀⠀⠀⠀⠀⣿⣿⠀⠀⠀⠀⠀⣿⣿⡇⠀⣽⣿⡏⠁⠀⠀⢸⣿⡇⠀⠀⠀\n" +
										"⠀⠀⠀⠀⠀⠀⠀⣿⣿⠀⠀⠀⠀⠀⣿⣿⡇⠀⢹⣿⡆⠀⠀⠀⣸⣿⠇⠀⠀⠀\n" +
										"⠀⠀⠀⠀⠀⠀⠀⢿⣿⣦⣄⣀⣠⣴⣿⣿⠁⠀⠈⠻⣿⣿⣿⣿⡿⠏⠀⠀⠀⠀\n" +
										"⠀⠀⠀⠀⠀⠀⠀⠈⠛⠻⠿⠿⠿⠿⠋⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀\n";
								packet = new Packet(username, msg, false, false, false);
								broadcast(true, packet);
								continue;

							}

						}

						// Send packet to all clients (except self)
						broadcast(false, packet);
					}
					terminate();
				} catch (IOException e) {
					terminate();
				}
			}).start();

		} catch (IOException e) {
			terminate();
		}
	}

/**
 * Terminates the user's session by removing their username from the user list, closing their socket,
 * and broadcasting the updated user list to all other users. If an exception occurs, it will be printed
 * to the console.
 */
	public void terminate() {
		try {
			System.out.println(username + " has disconnected");
			userList.remove(username);
			socket.close();

			// Send userlist to all other users
			String[] users = new String[userList.keySet().size()];
			userList.keySet().toArray(users);
			packet = new Packet(users, true, "", username);
			broadcast(false, packet);

			System.out.println("The current user list = " + userList);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
