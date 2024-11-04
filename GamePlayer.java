import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class GamePlayer {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Scanner scanner;
    private int numberOfAnswers;
    public GamePlayer(String serverAddress, int serverPort) {
        try {
            // Connect to the sender (server) on specified IP and port
            socket = new Socket(serverAddress, serverPort);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            scanner = new Scanner(System.in);
            System.out.println("Connected to sender at " + serverAddress + ":" + serverPort);

            // Start listening for messages
            listenForMessages();
        } catch (Exception e) {
            System.out.println("Connection error: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }

    private void listenForMessages() {
        String message;
        try {
            // Continuously listen for messages from the sender
            while ((message = in.readLine()) != null) {
                handleMessage(message);
            }
        } catch (Exception e) {
            System.out.println("Error reading message: " + e.getMessage());
        }
    }

    private void handleMessage(String message) {
        // Get the keyword (first word in the message)
        String[] parts = message.split(" ", 2);
        String keyword = parts[0].toLowerCase();
        String content = parts.length > 1 ? parts[1] : "";

        // Print the message
        // System.out.println(content);

        // If the keyword is "play" or "judge", prompt for an integer and send it back
        if ("play".equals(keyword)){
            play(keyword, content);
    
            
        }
        else if("judge".equals(keyword)){
            judge();
            
        }
        else if("showanswers".equals(keyword)){
            String[] answers = content.split(" ", 2);
            numberOfAnswers = Integer.valueOf(answers[0]);
            System.out.println(content);

        }
        else {
            //This is only for messeages that dont require anything except printing,
            //such as green apples or telling the players who the winner is etc.
            System.out.println(keyword + " " + content);
        }
        
    
    }
    private void play(String keyword, String content){
        String[] cards = content.split("#");

        // Print each card on a new line
        System.out.println("Your hand:");
        int i = 0;
        for (String card : cards) {
            System.out.println("["+i+"] "+card);
            i++;
        }
        int response = getUserInput("Which card do you want to play? " + keyword + ": ");
        while (response < 0 || response > 6){
            response = getUserInput("That is not a valid option");

        }

        out.println(response);  // Send the response back to the sender
        System.out.println("---------------------------------------------------------------");

    }
    private void judge(){
        int response = getUserInput("Which card shall win? ");
        while (response < 0 || response > numberOfAnswers-1){
            response = getUserInput("That is not a valid option \n");

        }
        out.println(response);  // Send the response back to the sender
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

    private void closeConnection() {
        try {
            if (socket != null) socket.close();
            if (in != null) in.close();
            if (out != null) out.close();
            if (scanner != null) scanner.close();
            System.out.println("Disconnected.");
        } catch (Exception e) {
            System.out.println("Error closing connection: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        String serverAddress = args[0];  
        int serverPort = 2024;
        new GamePlayer(serverAddress, serverPort);
    }
}
