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
        try {        
            game = new OnlinePlayer(argv[0]);
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
        
        
        
        
        while(true) {
			//receive info about being the judge or not
			String judgeString = inFromServer.readLine();
			boolean judge = (judgeString.compareTo("JUDGE")==0);
			//If someone wins the game the FINISHED string is written, and it just happens to be caught here
			if(judgeString.startsWith("FINISHED")) {
				System.out.println("\n"+judgeString);
				break;
			}
//test
			System.out.println("*****************************************************");
			if(judge) {
				System.out.println("**                 NEW ROUND - JUDGE               **");				
			} else {
				System.out.println("**                    NEW ROUND                    **");

			}
			System.out.println("*****************************************************");

			//receive and print the green apple that has been played
			String greenApple = inFromServer.readLine();
			System.out.println(greenApple + "\n");

			if(!judge) {
				//Play your red apple
				System.out.println("Choose a red apple to play");
				for(int i=0; i<hand.size(); i++) {
					System.out.println("["+i+"]   " + hand.get(i));
				}
				System.out.println("");
				int choice;
				BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				String input=br.readLine();
				choice = Integer.parseInt(input);
				outToServer.writeBytes(hand.get(choice)+"\n");
				hand.remove(choice);
				System.out.println("Waiting for other players\n");				
			}

			//Receive the played apples from server
			String playedApples = (inFromServer.readLine()).replaceAll("#", "\n");
			System.out.println("\nThe following apples were played:\n"+playedApples);

			if(judge) {
				//choose which red apple should win
				System.out.println("Choose which red apple wins\n");
				int choice;
				BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				String input=br.readLine();
				outToServer.writeBytes(input+"\n");
			}

			String winningRedApple = inFromServer.readLine();
			System.out.println(winningRedApple + "\n");

			//Non judges get a new red apple to replace the one that was played
			if(!judge) {
				String newRedApple = inFromServer.readLine();
				hand.add(newRedApple);
			}
		}
	
    }
}