package mine;

import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class TestServer {

    public static void main(String[] args) {
        int port = 12345;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is running on port " + port);

            // Wait for a client connection
            try (Socket clientSocket = serverSocket.accept();
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                 Scanner scanner = new Scanner(System.in)) {

                System.out.println("Client connected.");

                // Continuously send messages to the client
                while (true) {
                    System.out.println("Enter a message (keyword message), or 'exit' to quit:");
                    String input = scanner.nextLine();

                    if (input.equalsIgnoreCase("exit")) {
                        break;
                    }

                    // Send message to client
                    out.println(input);
                }

            } catch (Exception e) {
                System.out.println("Client disconnected: " + e.getMessage());
            }

        } catch (Exception e) {
            System.out.println("Error starting server: " + e.getMessage());
        }
    }
}
