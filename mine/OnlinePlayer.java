package mine;

import java.util.*; 
import java.nio.charset.StandardCharsets; 
import java.nio.file.*; 
import java.io.*; 
import java.net.*;
import java.util.concurrent.*;



public class OnlinePlayer {
    public static void main(String argv[]) {
        OnlinePlayer game;
        System.out.println("Starting game");
        try {        game = new OnlinePlayer(argv[0]);
        }
        catch (Exception e) {
            System.out.println("Something went wrong starting the online player");

        }
	}
    public OnlinePlayer(String ipAddress) throws Exception {
		//Connect to server
		Socket aSocket = new Socket(ipAddress, 2048);
		DataOutputStream outToServer = new DataOutputStream(aSocket.getOutputStream());
		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(aSocket.getInputStream()));
		//Get the hand of apples from server
		String[] applesString = (inFromServer.readLine()).split(";");
		ArrayList<String> hand = new ArrayList<String>(Arrays.asList(applesString));

    }
}