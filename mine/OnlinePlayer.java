import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class OnlinePlayer {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public OnlinePlayer(String serverAddress, int serverPort) {
        try {
            // Connect to the server
            socket = new Socket(serverAddress, serverPort);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            System.out.println("Connected to the server.");

            // Start listening for messages from the server
            listenForMessages();

        } catch (Exception e) {
            System.out.println("Failed to connect to server: " + e.getMessage());
        }
    }

    private void listenForMessages() {
        String message;
        try {
            // Continuously read messages from the server
            while ((message = in.readLine()) != null) {
                handleMessage(message);
            }
        } catch (Exception e) {
            System.out.println("Error reading message: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }

    private void handleMessage(String message) {
        // Extract the keyword (assuming the first word is the keyword)
        String[] parts = message.split(" ", 2);
        String keyword = parts[0];
        String content = parts.length > 1 ? parts[1] : "";

        // Perform actions based on the keyword
        switch (keyword.toLowerCase()) {
            case "GreenApple":
                System.out.println("The green apple chosen is: " + content);
                break;
            case "Play":
                System.out.println("Play one of the cards you have: \n " + content);
                break;
            case "Judge":
                System.out.println("Judge one of the following cards: \n " + content);
                break;
            case "game":
                System.out.println("Game message: " + content);
                break;
            default:
                System.out.println("Unknown command: " + message);
                break;
        }
    }

    private void closeConnection() {
        try {
            if (socket != null) socket.close();
            if (in != null) in.close();
            if (out != null) out.close();
            System.out.println("Disconnected from the server.");
        } catch (Exception e) {
            System.out.println("Error closing connection: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        
        int serverPort = 2024; // Port number should match the server's port
        // Create and start the OnlinePlayer client
        OnlinePlayer client = new OnlinePlayer(args[0], serverPort);
    }
}