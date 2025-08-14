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
    List<PlayerInfo> playerInfos = new ArrayList<>();  // List of PlayerInfo objects
    ArrayList<String> redApples;
    ArrayList<String> greenApples;
    String chosenGreenApple;
    int winCondition;
    private Boolean winner = false;
    ArrayList<PlayedApple> playedApples = new ArrayList<>();

    public GameServer( Network network) {
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
        GameServer server = new GameServer(new Network(2024));
        int amountOfPlayers = Integer.parseInt(args[0]);
        server.start(amountOfPlayers);
    }
}
