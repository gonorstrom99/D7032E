import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class GameBot extends GamePlayer {

    public GameBot(String serverAddress, int serverPort) {
        super(serverAddress, serverPort); // Call GamePlayer's constructor to set up the connection
    }

    // Override play method to automatically send "0" as input
    @Override
    protected void play(String keyword, String content) {
        String[] cards = content.split("#");

        // Print each card on a new line
        System.out.println("Your hand:");
        int i = 0;
        for (String card : cards) {
            System.out.println("[" + i + "] " + card);
            i++;
        }

        // Automatically choose 0 without asking for input
        int response = 0;
        out.println(response);  // Send the response back to the sender
        System.out.println("Bot selected: " + response);
        System.out.println("---------------------------------------------------------------");
    }

    // Override judge method to automatically send "0" as input
    @Override
    protected void judge() {
        // Automatically choose 0 without asking for input
        int response = 0;
        out.println(response);  // Send the response back to the sender
        System.out.println("Bot selected as judge: " + response);
    }

    // Override getUserInput to always return 0 without prompting
    @Override
    protected int getUserInput(String prompt) {
        System.out.println(prompt + "Bot automatically selects: 0");
        return 0; // Always return 0
    }

    public static void main(String[] args) {
        String serverAddress = args[0];
        int serverPort = 2024;
        new GameBot(serverAddress, serverPort);
    }
}
