import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Network {
    private final int port;
    private final List<Socket> clients = new ArrayList<>();
    private final List<PrintWriter> clientWriters = new ArrayList<>();
    private final List<BufferedReader> clientReaders = new ArrayList<>();

    public Network(int port) {
        this.port = port;
    }

    // Sets up connections with the specified number of clients
    public void setupConnections(int numberOfClients) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Waiting for " + numberOfClients + " clients to connect on port " + port);
            
            while (clients.size() < numberOfClients) {
                Socket clientSocket = serverSocket.accept();
                clients.add(clientSocket);
                clientWriters.add(new PrintWriter(clientSocket.getOutputStream(), true));
                clientReaders.add(new BufferedReader(new InputStreamReader(clientSocket.getInputStream())));

                System.out.println("Client connected: " + clientSocket.getInetAddress());
            }
        } catch (Exception e) {
            System.out.println("Network setup error: " + e.getMessage());
        }
    }

    // Sends a message to a specific client by index
    public void sendMessageToClient(int clientIndex, String message) {
        clientWriters.get(clientIndex).println(message);
    }

    // Broadcasts a message to all clients
    public void broadcastMessage(String message) {
        for (PrintWriter writer : clientWriters) {
            writer.println(message);
        }
    }

    // Receives a message from a specific client by index
    public String receiveMessageFromClient(int clientIndex) {
        try {
            return clientReaders.get(clientIndex).readLine();
        } catch (Exception e) {
            System.out.println("Error receiving message from client: " + e.getMessage());
            return null;
        }
    }

    // Closes all client connections
    public void closeAllConnections() {
        for (Socket client : clients) {
            try {
                client.close();
            } catch (Exception e) {
                System.out.println("Error closing client connection: " + e.getMessage());
            }
        }
    }
}
