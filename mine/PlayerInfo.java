package mine;


import java.util.*; 
import java.io.*; 
import java.net.*;



class PlayerInfo {
	public int playerID;
	public boolean isBot;
	public boolean online;
	public Socket connection;
	public BufferedReader inFromClient;
	public DataOutputStream outToClient;
	public ArrayList<String> hand;
	public ArrayList<String> greenApples = new ArrayList<String>();
	public PlayerInfo(int playerID, ArrayList<String> hand, boolean isBot) {
		this.playerID = playerID; this.hand = hand; this.isBot = isBot; this.online = false;
	}
	public PlayerInfo(int playerID, boolean isBot, Socket connection, BufferedReader inFromClient, DataOutputStream outToClient) {
		this.playerID = playerID; this.isBot = isBot; this.online = true;
		this.connection = connection; this.inFromClient = inFromClient; this.outToClient = outToClient;
	}
}