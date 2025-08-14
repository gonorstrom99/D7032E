import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GameServerTest {

    public static void main(String[] args) {
        GameServerTest test = new GameServerTest();
        try {
            
        System.out.println("All tests completed.");

        } catch (Exception e) {
            System.out.println("Tests failed with an exception/error: " + e.getMessage());
        }
        test.testReadGreenApples();
        test.testReadRedApples();
        test.testShuffleDecks();
        test.testDealSevenCards();
        test.testRandomJudgeSelection();
        test.testDrawGreenApple();
        test.testPlay();
        test.testShufflePlayedApples();
        test.testAllPlayersPlayBeforeShow();
        test.testJudgeSelectsFavorite();
        test.testDiscardSubmittedApples();
        test.testRefillPlayersHand();
        test.testSwitchJudge();
        test.testKeepScore();
        test.testWinCondition();
    }

    private void assertCondition(boolean condition, String testDescription) {
        if (condition) {
            System.out.println("PASSED: " + testDescription);
        } else {
            System.out.println("FAILED: " + testDescription);
        }
    }

    // 1. Test reading green apples from a file
    public void testReadGreenApples() {
        MockNetwork mockNetwork = new MockNetwork(2024);
        GameServer server = new GameServer(mockNetwork);
        server.playedApples.clear(); // Clear playedApples before starting the test

        try {
            server.readCardDecks();
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertCondition(server.greenApples != null && !server.greenApples.isEmpty(), "Green apples deck should be populated from file");
    }

    // 2. Test reading red apples from a file
    public void testReadRedApples() {
        MockNetwork mockNetwork = new MockNetwork(2024);
        GameServer server = new GameServer(mockNetwork);
        server.playedApples.clear(); // Clear playedApples before starting the test

        try {
            server.readCardDecks();
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertCondition(server.redApples != null && !server.redApples.isEmpty(), "Red apples deck should be populated from file");
    }

    // 3. Test shuffling both decks
    public void testShuffleDecks() {
        MockNetwork mockNetwork = new MockNetwork(2024);
        GameServer server = new GameServer(mockNetwork);
        server.playedApples.clear(); // Clear playedApples before starting the test

        try {
            server.readCardDecks();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ArrayList<String> originalDeck = new ArrayList<>(server.redApples);
        server.shuffleCardDeck(server.redApples);
        assertCondition(!originalDeck.equals(server.redApples), "Red apples deck should be shuffled");
    }

    // 4. Test dealing seven red apples to each player
    public void testDealSevenCards() {
        MockNetwork mockNetwork = new MockNetwork(2024);
        GameServer server = new GameServer(mockNetwork);
        server.playedApples.clear(); // Clear playedApples before starting the test

        try {
            server.readCardDecks();
        } catch (Exception e) {
            e.printStackTrace();
        }
        server.setupPlayers(4);
        server.dealCards();
        boolean allPlayersHaveSeven = server.playerInfos.stream().allMatch(player -> player.getHand().size() == 7);
        assertCondition(allPlayersHaveSeven, "Each player should have 7 red apples after dealing");
    }

    // 5. Test random selection of the judge
    public void testRandomJudgeSelection() {
        MockNetwork mockNetwork = new MockNetwork(2024);
        GameServer server = new GameServer(mockNetwork);
        server.playedApples.clear(); // Clear playedApples before starting the test

        server.setupPlayers(4);
        server.setupGame(4);
        boolean hasJudge = server.playerInfos.stream().anyMatch(PlayerInfo::isJudge);
        assertCondition(hasJudge, "One player should be randomly assigned as judge");
    }

    // 6. Test drawing a green apple
    public void testDrawGreenApple() {
        MockNetwork mockNetwork = new MockNetwork(2024);
        GameServer server = new GameServer(mockNetwork);
        server.playedApples.clear(); // Clear playedApples before starting the test

        server.setupPlayers(4);
        server.setupGame(4);
        String previousGreenApple = server.chosenGreenApple;
        server.greenApple();
        assertCondition(server.chosenGreenApple != null && !server.chosenGreenApple.equals(previousGreenApple), "A new green apple should be drawn from the pile");
    }

    // 7. Test players (except judge) playing a red apple
    public void testPlay() {
        MockNetwork mockNetwork = new MockNetwork(2024);
        GameServer server = new GameServer(mockNetwork);
        server.playedApples.clear(); // Clear playedApples before starting the test

        // Setup players and game state
        server.setupPlayers(3);
        server.playerInfos.get(0).addCardToHand("Red Apple 1");
        server.playerInfos.get(1).addCardToHand("Red Apple 2");
        server.playerInfos.get(2).addCardToHand("Red Apple 3");

        // Set one player as judge
        server.playerInfos.get(0).setJudge(true);

        // Set expected client responses for the "play" phase
        mockNetwork.mockClientResponse(1, "0"); // Second player plays "Red Apple 2"
        mockNetwork.mockClientResponse(2, "0"); // Third player plays "Red Apple 3"

        // Call the play method, which should send messages and register answers
        server.play();

        // Verify messages were sent correctly to non-judge players
        assertCondition(mockNetwork.getMessagesSentToClient(1).contains("play Red Apple 2"), "Player 2 should receive 'play Red Apple 2'");
        assertCondition(mockNetwork.getMessagesSentToClient(2).contains("play Red Apple 3"), "Player 3 should receive 'play Red Apple 3'");

        // Verify that the playedApples list was updated with the correct responses
        assertCondition(server.playedApples.size() == 2, "Two played apples should be registered");
        assertCondition(server.playedApples.get(0).redApple.equals("Red Apple 2"), "Player 2's played apple should be registered");
        assertCondition(server.playedApples.get(1).redApple.equals("Red Apple 3"), "Player 3's played apple should be registered");

        System.out.println("testPlay completed.");
    }

    // 8. Test randomizing the order of played red apples
    public void testShufflePlayedApples() {
        MockNetwork mockNetwork = new MockNetwork(2024);
        GameServer server = new GameServer(mockNetwork);
        server.playedApples.clear(); // Clear playedApples before starting the test

        // Setup players and game state
        server.setupPlayers(4);
        server.playerInfos.get(0).addCardToHand("Red Apple 11");
        server.playerInfos.get(1).addCardToHand("Red Apple 22");
        server.playerInfos.get(2).addCardToHand("Red Apple 33");
        server.playerInfos.get(3).addCardToHand("Red Apple 44");

        mockNetwork.mockClientResponse(0, "0");
        mockNetwork.mockClientResponse(1, "0");
        mockNetwork.mockClientResponse(2, "0");
        mockNetwork.mockClientResponse(3, "0");
        server.play();

        ArrayList<PlayedApple> originalOrder = new ArrayList<>(server.playedApples);
        server.showAnswers();
        assertCondition(!originalOrder.equals(server.playedApples), "Played red apples should be shuffled before showing");
    }

    // 9. Test that all players (except judge) play before results
    public void testAllPlayersPlayBeforeShow() {
        MockNetwork mockNetwork = new MockNetwork(2024);
        GameServer server = new GameServer(mockNetwork);
        server.playedApples.clear(); // Clear playedApples before starting the test

        server.setupPlayers(4);
        server.playerInfos.get(0).addCardToHand("Red Apple 111");
        server.playerInfos.get(1).addCardToHand("Red Apple 222");
        server.playerInfos.get(2).addCardToHand("Red Apple 333");
        server.playerInfos.get(3).addCardToHand("Red Apple 444");
        server.playerInfos.get(0).setJudge(true);

        mockNetwork.mockClientResponse(1, "0");
        mockNetwork.mockClientResponse(2, "0");
        mockNetwork.mockClientResponse(3, "0");
        server.play();
        assertCondition(server.playedApples.size() == 3, "All players except the judge must play before showing answers");
    }

    // Continue with the rest of the methods in a similar way...

    // 10. Test judge selects favorite red apple
    public void testJudgeSelectsFavorite() {
        MockNetwork mockNetwork = new MockNetwork(2024);
        GameServer server = new GameServer(mockNetwork);
        server.playedApples.clear(); // Clear playedApples before starting the test

        // Setup players and game state
        server.setupPlayers(4);
        server.setupGame(4);
        mockNetwork.mockClientResponse(0, "0");
        mockNetwork.mockClientResponse(1, "0");
        mockNetwork.mockClientResponse(2, "0");
        mockNetwork.mockClientResponse(3, "0");
        server.play();
        server.judge();
        assertCondition(server.playerInfos.stream().anyMatch(player -> player.GreenAppleSize() == 1), "Judge should award a green apple to the winner");
    }

    // 11. Test discarding submitted red apples
    public void testDiscardSubmittedApples() {
        MockNetwork mockNetwork = new MockNetwork(2024);
        GameServer server = new GameServer(mockNetwork);
        server.playedApples.clear(); // Clear playedApples before starting the test

        server.setupPlayers(4);
        server.playerInfos.get(1).addCardToHand("Red Apple 222");
        server.playerInfos.get(2).addCardToHand("Red Apple 333");
        server.playerInfos.get(3).addCardToHand("Red Apple 444");
        server.playerInfos.get(0).setJudge(true);

        mockNetwork.mockClientResponse(1, "0");
        mockNetwork.mockClientResponse(2, "0");
        mockNetwork.mockClientResponse(3, "0");
        server.play();
        mockNetwork.mockClientResponse(0, "0");

        server.judge();
        assertCondition(server.playerInfos.stream().allMatch(player -> player.hand.size()==0), "All submitted red apples should be discarded");
    }

    // 12. Test refilling players' hands
    public void testRefillPlayersHand() {
        MockNetwork mockNetwork = new MockNetwork(2024);
        GameServer server = new GameServer(mockNetwork);
        server.playedApples.clear(); // Clear playedApples before starting the test

        server.setupPlayers(4);
        server.setupGame(4);
        mockNetwork.mockClientResponse(0, "0");
        mockNetwork.mockClientResponse(1, "0");
        mockNetwork.mockClientResponse(2, "0");
        mockNetwork.mockClientResponse(3, "0");
        server.play();
        server.dealCards();
        boolean allPlayersHaveSeven = server.playerInfos.stream().allMatch(player -> player.getHand().size() == 7);
        assertCondition(allPlayersHaveSeven, "Each player should have 7 red apples after refilling hands");
    }

    // 13. Test switching judge
    public void testSwitchJudge() {
        MockNetwork mockNetwork = new MockNetwork(2024);
        GameServer server = new GameServer(mockNetwork);

        server.setupPlayers(4);
        server.setupGame(4);
        int initialJudgeIndex = server.playerInfos.indexOf(server.playerInfos.stream().filter(PlayerInfo::isJudge).findFirst().orElse(null));
        server.switchJudge();
        int newJudgeIndex = server.playerInfos.indexOf(server.playerInfos.stream().filter(PlayerInfo::isJudge).findFirst().orElse(null));
        assertCondition(newJudgeIndex == (initialJudgeIndex+1)%4, "The judge should switch to the next player in line");
    }

    // 14. Test keeping score by counting green apples won
    public void testKeepScore() {
        MockNetwork mockNetwork = new MockNetwork(2024);
        GameServer server = new GameServer(mockNetwork);

        server.setupPlayers(4);
        server.playerInfos.get(0).addGreenApple("Green Apple");
        assertCondition(server.playerInfos.get(0).GreenAppleSize() == 1, "Score should increase when a player wins a green apple");
    }

    // 15. Test win condition based on player count
    public void testWinCondition() {
        MockNetwork mockNetwork = new MockNetwork(2024);
        GameServer server = new GameServer(mockNetwork);


        for (int numberOfPlayers = 4; numberOfPlayers<=8; numberOfPlayers++){
        server.setupWinCondition(numberOfPlayers);
        int winCondition = 12-numberOfPlayers;
        assertCondition(server.winCondition == winCondition, "Win condition for "+ numberOfPlayers + " players should be " + winCondition +" green apples");
        server.setupPlayers(4);
        server.setupGame(4);
        for (int i = 0; i < 40; i++) {
            mockNetwork.mockClientResponse(0, "0");
            mockNetwork.mockClientResponse(1, "0");
            mockNetwork.mockClientResponse(2, "0");
            mockNetwork.mockClientResponse(3, "0");
        }
        server.gameLoop();
        assertCondition(server.playerInfos.stream().anyMatch(playerInfo -> playerInfo.GreenAppleSize()>=winCondition), "with " + numberOfPlayers + " players, the winner won with " + winCondition + " cards");




        }
        
        
    }

}
