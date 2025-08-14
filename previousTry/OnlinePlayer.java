package previousTry;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;


public class OnlinePlayer {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Scanner scanner;

    public OnlinePlayer(String serverAddress, int serverPort) {
        try {
            // Connect to the server
            socket = new Socket(serverAddress, serverPort);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            scanner = new Scanner(System.in); // For reading user input
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
            case "greenapple":
                System.out.println("The green apple chosen is: " + content);
                break;
            case "play":
                System.out.println("Play one of the cards you have: \n " + content);
                int playChoice = getUserInput("Enter the number of the card to play: ");

                sendResponse(playChoice);

                break;
            case "judge":
                System.out.println("Judge one of the following cards: \n " + content);
                break;
            case "winner":
                System.out.println("The winner is:  " + content);
                closeConnection();
                break;
            default:
                System.out.println("Unknown command: " + message);
                break;
        }
    }
    private int getUserInput(String prompt) {
        int choice = -1;
        System.out.print(prompt);
        while (true) {
            try {
                choice = Integer.parseInt(scanner.nextLine());
                break; // Exit loop if input is valid
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number: ");
            }
        }
        return choice;
    }

    private void sendResponse(int response) {
        // Send the integer response back to the server
        out.println(response);
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