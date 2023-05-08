/**
 * The Packet class represents a packet of data that can be sent between the server and clients.
 * It contains information about the message, sender, recipient, and whether the message is a private message or not.
 * @authors	Migael Van Wyk 24716812@sun.ac.za - Project Manager
 *		Jurianne Schreuder 24695351@sun.ac.za - Testing
 *		Stefan Russouw 24715034@sun.ac.za - Client
 *		Hardus Viviers 24917540@sun.ac.za - GUI design
 *		Louis De Jager 24908134@sun.ac.za - Server
 * @version 1.0
 * @since 2023-02-28
 */

import java.time.LocalTime;
import java.lang.*;
import java.io.Serializable;
import java.util.HashSet;

public class Packet implements Serializable {

	String username;
	String message;
	String sentAt;
	String to;
	Boolean createUser;
	String[] list;
	Boolean isList;
	Boolean isWhisper;
	String userCon;
	String userDis;

/**
 * Constructor for creating a Packet object.
 * @param username the username of the sender
 * @param message the message to be sent
 * @param createUser whether the packet represents a new user joining the chat
 * @param isList whether the packet represents a list of users
 * @param isWhisper whether the message is a private message
 */
	public Packet(String username, String message, Boolean createUser, Boolean isList, Boolean isWhisper){
		this.username = username;
		this.message = message;
		this.sentAt = LocalTime.now().toString().substring(0,8);
		this.createUser = createUser;
		this.isList = isList;
		this.isWhisper = isWhisper;
	}

/**
 * Constructor for creating a Packet object representing a list of users.
 * @param list the list of users
 * @param isList whether the packet represents a list of users
 * @param userCon the username of the user who just joined the chat
 * @param userDis the username of the user who just left the chat
 */
	public Packet(String[] list, Boolean isList, String userCon, String userDis) {
		this.list = list;
		this.isList = isList;
		this.userCon = userCon;
		this.userDis = userDis;
	}

/**
 * Getter method for the username.
 * @return the username of the sender
 */
	public String getUsername() {
		return username;
	}

/**
 * Getter method for the message.
 * @return the message to be sent
 */
	public String getMessage() {
		return message;
	}

/**
 * Getter method for the time the message was sent.
 * @return the time the message was sent
 */
	public String getSentAt() {
		return sentAt;
	}

/**
 * Getter method for the recipient.
 * @return the recipient of the message
 */
	public String getTo() {
		return to;
	}

/**
 * Getter method for whether the packet represents a new user joining the chat.
 * @return whether the packet represents a new user joining the chat
 */
	public Boolean getCreateUser() {
		return createUser;
	}

/**
 * Getter method for the list of users.
 * @return the list of users
 */
	public String[] getList() {
		return list;
	}

/**
 * Getter method for whether the message is a private message.
 * @return whether the message is a private message
 */	
	public Boolean getisWhisper() {
		return isWhisper;
	}


}
