// import java.io.BufferedReader;
// import java.io.InputStreamReader;
// import java.io.PrintWriter;
// import java.net.ServerSocket;
// import java.net.Socket;
// import java.util.ArrayList;
// import java.util.List;
// import java.nio.file.*; 
// import java.nio.charset.StandardCharsets; 
// import java.util.Collections;
// import java.util.Random;

// class PlayedApple {
// 	public int playerID;
// 	public String redApple;
// 	public PlayedApple(int playerID, String redApple) {
// 		this.playerID = playerID;
// 		this.redApple = redApple;
// 	}
// }
// public class GameServer {

//     private final List<Socket> clients = new ArrayList<>();
//     private final List<PrintWriter> clientWriters = new ArrayList<>();
//     private final List<BufferedReader> clientReaders = new ArrayList<>();
//     private final int port;
//     private List<PlayerInfo> playerInfos = new ArrayList<>();  // List of PlayerInfo objects
//     public ArrayList<String> redApples;
// 	public ArrayList<String> greenApples;
//     private String chosenGreenApple;
//     // private ArrayList<String> submittedAnswers = new ArrayList<>();  // List to store answers from players
// 	private static ArrayList<PlayedApple> playedApples = new ArrayList<PlayedApple>();

//     public GameServer(int port) {
//         this.port = port;
        
//     }
//     public void start(int AmountOfPlayers){
        
        
//         setupConnections(AmountOfPlayers);

//         //this is the setup phase of the game, reading the decks, shuffling them, dealing cards, and assigning a random judge.
//         setupGame();

//         gameLoop();
//         closeAllConnections();
//     }
//     private void setupConnections(int AmountOfPlayers){
//         try (ServerSocket serverSocket = new ServerSocket(port)) {
//             System.out.println("Sender waiting for " + AmountOfPlayers + " receivers to connect on port " + port);
            
//             int playerId = 1;  // Start player IDs from 1
//             while (clients.size() < AmountOfPlayers) {
//                 Socket clientSocket = serverSocket.accept();
//                 clients.add(clientSocket);
//                 clientWriters.add(new PrintWriter(clientSocket.getOutputStream(), true));
//                 clientReaders.add(new BufferedReader(new InputStreamReader(clientSocket.getInputStream())));

//                 // Create a PlayerInfo instance for each player
//                 // Here, we're assuming every new player connected initially has judge = false.
//                 // Set judge = true for one of them later, as per your game rules
//                 playerInfos.add(new PlayerInfo(playerId, false));
//                 System.out.println("Receiver connected: " + clientSocket.getInetAddress() + " as Player " + playerId);
//                 playerId++;
//             } 
//         }catch (Exception e) {
//             System.out.println("Server error: " + e.getMessage());
//         }
//     }
//     private void setupGame(){
//         try {
//             readCardDecks();
//         } catch (Exception e) {
//             System.out.println("error reading the card decks");
//         }
//         redApples=shuffleCardDeck(redApples);
//         greenApples=shuffleCardDeck(greenApples);
//         dealCards();

//         Random random = new Random();
//         int randomIndex = random.nextInt(playerInfos.size());  // Generate a random index within the bounds of playerInfos
//         playerInfos.get(randomIndex).setJudge(true);

//     }



//     private void readCardDecks() throws Exception{
//         redApples = new ArrayList<String>(Files.readAllLines(Paths.get("./", "redApples.txt"), StandardCharsets.ISO_8859_1)); 
//         greenApples = new ArrayList<String>(Files.readAllLines(Paths.get("./", "greenApples.txt"), StandardCharsets.ISO_8859_1));

//     }
//     private static ArrayList<String> shuffleCardDeck(ArrayList<String> deck) {
//         Collections.shuffle(deck);  // Shuffle the elements in the list
//         return deck;
//     }
    
//     private void dealCards() {
//         for (PlayerInfo player : playerInfos) {
//             int cardsNeeded = 7 - player.getHand().size();  // Calculate how many cards are needed to reach 7
//             for (int i = 0; i < cardsNeeded; i++) {
//                 if (!redApples.isEmpty()) {
//                     String card = redApples.remove(0);       // Remove the first card from redApples
//                     player.addCardToHand(card);              // Add it to the player's hand
//                 } else {
//                     System.out.println("Not enough cards to deal to all players.");
//                     return;
//                 }
//             }
//         }
//         System.out.println("Each player has exactly 7 cards after dealing.");
//     }
//     private void gameLoop() {
//         while (true) {
//             greenApple();   // a green apple is chosen and shown to all players
//             play();         //all players except the judge are asked to play a red apple
//             showAnswers();  //the played apples get sent to everyone to see them
//             judge();        //The judge decides which apple wins and therefore who gets a point
//             //chosenApple();
//             switchJudge();  //The next player in the list gets to be judge
//             winner();       //
//             dealCards();    //Cards are dealt to all players until they have 7 each.


//             // Optional delay to make the loop easier to follow
//             try {
//                 Thread.sleep(1000);  // Wait 1 second before looping again
//             } catch (InterruptedException e) {
//                 Thread.currentThread().interrupt();
//                 break;
//             }
//         }
//     }


//     private void greenApple() {
//         if (greenApples.isEmpty()) {
//             System.out.println("No more green apples left.");
//             return;
//         }
    
//         chosenGreenApple = greenApples.remove(0);  // Get and remove the first green apple
//         System.out.println("Sending: greenApple " + chosenGreenApple);
    
//         for (PrintWriter writer : clientWriters) {
//             writer.println("The green apple is: " + chosenGreenApple);
//         }
//     }
//     private void play() {
//         System.out.println("Sending: play");
    
//         for (int i = 0; i < clientWriters.size(); i++) {
//             if (!playerInfos.get(i).isJudge()) {  // Only send to players who are not judges
//                 // Retrieve the player's hand from playerInfos
//                 List<String> hand = playerInfos.get(i).getHand();
                
//                 // Convert the hand list to a single string for easy sending
//                 String handMessage = String.join("#", hand);
    
//                 // Send the "play" message along with the player's hand
//                 clientWriters.get(i).println("play " + handMessage);
//                 // System.out.println("Sent to Player " + playerInfos.get(i).getPlayerId() + ": " + handMessage);
//             }
//         }
        
//         registerAnswers("play");
//     }

//     private void showAnswers() {
//         //shuffles the answers
//         Collections.shuffle(playedApples);
//         // Prepare the answers to be sent to players
//         // Prepare the answers to be sent to players
//         StringBuilder answersMessage = new StringBuilder("Submitted Answers:\n");
//         for (int i = 0; i < playedApples.size(); i++) {
//             answersMessage.append((i + 1)).append(". ").append(playedApples.get(i).redApple).append("\n");
//         }

//         // Send the message to all players
//         for (PrintWriter writer : clientWriters) {
//             writer.println(answersMessage.toString());
//         }
//     }

//     private void judge() {
//         System.out.println("Sending: judge");
//         for (int i = 0; i < clientWriters.size(); i++) {
//             if (playerInfos.get(i).isJudge()) {  // Only send to players who are judges
//                 clientWriters.get(i).println("judge");
//             }
//         }
//         registerAnswers("judge");
//     }
//     private void showJudging(int player, String winnerApple){
//         StringBuilder answersMessage = new StringBuilder("player" + player + " won with the apple " + winnerApple);
        

//         // Send the message to all players
//         for (PrintWriter writer : clientWriters) {
//             writer.println(answersMessage.toString());
//         }
//     }
//     private void switchJudge() {
//         // Find the current judge
//         int currentJudgeIndex = -1;
//         for (int i = 0; i < playerInfos.size(); i++) {
//             if (playerInfos.get(i).isJudge()) {
//                 currentJudgeIndex = i;
//                 playerInfos.get(i).setJudge(false);  // Remove judge role from current judge
//                 break;
//             }
//         }
    
//         // Determine the next player to be the judge
//         int nextJudgeIndex = (currentJudgeIndex + 1) % playerInfos.size();
//         playerInfos.get(nextJudgeIndex).setJudge(true);  // Assign judge role to the next player
    
//         // Print the new judge for confirmation
//         System.out.println("Player " + playerInfos.get(nextJudgeIndex).getPlayerId() + " is now the judge.");
//     }


//     private void winner() {
//         System.out.println("Sending: winner");
//         for (PrintWriter writer : clientWriters) {
//             writer.println("winner");
//         }
//     }

//     private void registerAnswers(String messageType) {
       

//         for (int i = 0; i < clientReaders.size(); i++) {
//             try {
//                 if ((messageType.equals("play") && !playerInfos.get(i).isJudge())) {
//                     String response = clientReaders.get(i).readLine();  // Wait for each relevant client to respond
//                     System.out.println("Received response to " + messageType + ": " + response);
//                     PlayedApple playedApple= new PlayedApple(i, playerInfos.get(i).hand.get(Integer.valueOf(response)));
//                     playedApples.add(playedApple);  // Add to submitted answers
//                 }
//                 else if (messageType.equals("judge") && playerInfos.get(i).isJudge()){
//                     System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
//                     String response = clientReaders.get(i).readLine();  // Wait for each relevant client to respond
//                     System.out.println("Received response to " + messageType + ": " + response);
//                     // playerInfos.get(i).submittedAnswer=playerInfos.get(i).hand.get(Integer.valueOf(response));
//                     PlayedApple winnerAnswer = playedApples.get(Integer.valueOf(response));
//                     playerInfos.get(winnerAnswer.playerID).addGreenApple(chosenGreenApple);
//                     showJudging(winnerAnswer.playerID, winnerAnswer.redApple);
//                     playedApples.clear();  // Clear previous answers before the next round of answers come.
//                 }
//             } catch (Exception e) {
//                 System.out.println("Error receiving response: " + e.getMessage());
//             }
//         }
        
//         System.out.println("Received all responses for " + messageType + ".");
//     }

//     private void closeAllConnections() {
//         for (Socket client : clients) {
//             try {
//                 client.close();
//             } catch (Exception e) {
//                 System.out.println("Error closing client connection: " + e.getMessage());
//             }
//         }
//     }

//     public static void main(String[] args) {
//         int port = 2024;
//         GameServer server = new GameServer(port);
//         int AmountOfPlayers = Integer.parseInt(args[0]);
//         server.start(AmountOfPlayers);
       
//     }
// }





import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

class PlayedApple {
    public int playerID;
    public String redApple;
    public PlayedApple(int playerID, String redApple) {
        this.playerID = playerID;
        this.redApple = redApple;
    }
}

public class GameServer {

    private final Network network;
    private final int port;
    List<PlayerInfo> playerInfos = new ArrayList<>();  // List of PlayerInfo objects
    ArrayList<String> redApples;
    ArrayList<String> greenApples;
    String chosenGreenApple;
    int winCondition;
    private Boolean winner = false;
    ArrayList<PlayedApple> playedApples = new ArrayList<>();

    public GameServer(int port, Network network) {
        this.port = port;
        this.network = network;
    }

    public void start(int amountOfPlayers) {
        setupWinCondition(amountOfPlayers);
        network.setupConnections(amountOfPlayers);
        setupPlayers(amountOfPlayers);
        setupGame(amountOfPlayers);
        gameLoop();
    }
    void setupWinCondition(int amountOfPlayers){
        if (amountOfPlayers<=4) winCondition=8;
        else if (amountOfPlayers==5) winCondition=7;
        else if (amountOfPlayers==6) winCondition=6;
        else if (amountOfPlayers==7) winCondition=5;
        else winCondition=4;
    }
    void setupPlayers(int amountOfPlayers){
        int playerId = 1;  // Start player IDs from 1
            while (playerId <= amountOfPlayers) {
                
                // Create a PlayerInfo instance for each player
                // Here, we're assuming every new player connected initially has judge = false.
                // Set judge = true for one of them later, as per your game rules
                playerInfos.add(new PlayerInfo(playerId, false));
                playerId++;
            }
    }
    void setupGame(int amountOfPlayers) {
        try {
            readCardDecks();
        } catch (Exception e) {
            System.out.println("Error reading the card decks");
        }
        redApples = shuffleCardDeck(redApples);
        greenApples = shuffleCardDeck(greenApples);
        dealCards();

        Random random = new Random();

        int randomIndex = random.nextInt(amountOfPlayers);

        playerInfos.get(randomIndex).setJudge(true);
    }

    void readCardDecks() throws Exception {
        redApples = new ArrayList<>(Files.readAllLines(Paths.get("./", "redApples.txt"), StandardCharsets.ISO_8859_1));
        greenApples = new ArrayList<>(Files.readAllLines(Paths.get("./", "greenApples.txt"), StandardCharsets.ISO_8859_1));
    }

    static ArrayList<String> shuffleCardDeck(ArrayList<String> deck) {
        Collections.shuffle(deck);
        return deck;
    }

    void dealCards() {
        for (PlayerInfo player : playerInfos) {
            int cardsNeeded = 7 - player.getHand().size();
            for (int i = 0; i < cardsNeeded; i++) {
                if (!redApples.isEmpty()) {
                    String card = redApples.remove(0);
                    player.addCardToHand(card);
                } else {
                    System.out.println("Not enough cards to deal to all players.");
                    return;
                }
            }
        }
        // System.out.println("Each player has exactly 7 cards after dealing.");
    }

    void gameLoop() {
        while (winner==false) {
            greenApple();
            play();
            showAnswers();
            judge();
            switchJudge();
            checkWinner();
            dealCards();

        }
    }

    void greenApple() {
        if (greenApples.isEmpty()) {
            System.out.println("No more green apples left.");
            return;
        }

        chosenGreenApple = greenApples.remove(0);
        System.out.println("Sending: greenApple " + chosenGreenApple);
        network.broadcastMessage("The green apple is: " + chosenGreenApple);
    }

    void play() {
        System.out.println("Sending: play");
        for (int i = 0; i < playerInfos.size(); i++) {
            if (!playerInfos.get(i).isJudge()) {
                List<String> hand = playerInfos.get(i).getHand();
                String handMessage = String.join("#", hand);
                network.sendMessageToClient(i, "play " + handMessage);
            }
        }
        registerAnswers("play");
    }

    void showAnswers() {
        Collections.shuffle(playedApples);
        StringBuilder answersMessage = new StringBuilder("showAnswers "+String.valueOf(playedApples.size()) + " Submitted Answers:\n");
        for (int i = 0; i < playedApples.size(); i++) {
            answersMessage.append((i)).append(". ").append(playedApples.get(i).redApple).append("\n");
        }
        network.broadcastMessage(answersMessage.toString());
    }

    void judge() {
        System.out.println("Sending: judge");
        for (int i = 0; i < playerInfos.size(); i++) {
            if (playerInfos.get(i).isJudge()) {
                network.sendMessageToClient(i, "judge");
            }
        }
        registerAnswers("judge");
    }

    void switchJudge() {
        int currentJudgeIndex = -1;
        for (int i = 0; i < playerInfos.size(); i++) {
            if (playerInfos.get(i).isJudge()) {
                currentJudgeIndex = i;
                playerInfos.get(i).setJudge(false);
                break;
            }
        }
        int nextJudgeIndex = (currentJudgeIndex + 1) % playerInfos.size();
        playerInfos.get(nextJudgeIndex).setJudge(true);
        System.out.println("Player " + playerInfos.get(nextJudgeIndex).getPlayerId() + " is now the judge.");
    }

    private void checkWinner() {
        for (int i = 0; i < playerInfos.size(); i++) {
            if (playerInfos.get(i).GreenAppleSize()>=winCondition){
                System.out.println("player " + i + " has won the game with " + winCondition +  " cards");
                network.broadcastMessage("player " + i + " has won the game with " + winCondition +  " cards");
                network.closeAllConnections();
                winner=true;
            }
        }
        
    }

    private void registerAnswers(String messageType) {
        for (int i = 0; i < playerInfos.size(); i++) {
            try {
                if ((messageType.equals("play") && !playerInfos.get(i).isJudge())) {
                    String response = network.receiveMessageFromClient(i);
                    System.out.println("Received response to " + messageType + ": " + response);

                    int chosenAppleIndex = Integer.parseInt(response);
                    String chosenApple = playerInfos.get(i).getHand().get(chosenAppleIndex);

                    // Create a PlayedApple instance with the chosen apple
                    PlayedApple playedApple = new PlayedApple(i, chosenApple);
                    playedApples.add(playedApple);

                    // Remove the played apple from the player's hand
                    playerInfos.get(i).getHand().remove(chosenAppleIndex);





                } else if (messageType.equals("judge") && playerInfos.get(i).isJudge()) {
                    String response = network.receiveMessageFromClient(i);
                    PlayedApple winnerAnswer = playedApples.get(Integer.parseInt(response));
                    playerInfos.get(winnerAnswer.playerID).addGreenApple(chosenGreenApple);
                    showJudging(winnerAnswer.playerID, winnerAnswer.redApple);

                    playedApples.clear();
                }
            } catch (Exception e) {
                System.out.println("Error receiving response: " + e.getMessage());
            }
        }
    }

    private void showJudging(int player, String winnerApple) {
        String message = "Player " + player + " won with the apple: " + winnerApple;
        network.broadcastMessage(message);
    }




    
    public static void main(String[] args) {
        int port = 2024;
        GameServer server = new GameServer(port, new Network(2024));
        int amountOfPlayers = Integer.parseInt(args[0]);
        server.start(amountOfPlayers);
    }
}
