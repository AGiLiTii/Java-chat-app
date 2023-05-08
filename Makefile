server:
	javac Packet.java
	javac Clientmanager.java
	javac Server.java
	java Server

client:
	javac Packet.java
	javac Clientmanager.java
	javac LoginFrame.java
	java LoginFrame

client2:
	javac Packet.java
	javac Clientmanager.java
	javac LoginFrame.java
	java LoginFrame

client3:
	javac Packet.java
	javac Clientmanager.java
	javac LoginFrame.java
	java LoginFrame

clean:
	rm *.class
