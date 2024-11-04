import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MockNetwork extends Network {
    private Map<Integer, List<String>> messagesSent = new HashMap<>();
    private Map<Integer, String> messagesToReceive = new HashMap<>();
    
    public MockNetwork(int port) {
        super(port); // Call the parent constructor but this mock class won’t actually open network connections
    }

    @Override
    public void sendMessageToClient(int clientIndex, String message) {
        messagesSent.computeIfAbsent(clientIndex, k -> new ArrayList<>()).add(message);
    }

    @Override
    public String receiveMessageFromClient(int clientIndex) {
        return messagesToReceive.get(clientIndex);
    }

    // Method to set up messages that should be "received" by the mock
    public void mockClientResponse(int clientIndex, String message) {
        messagesToReceive.put(clientIndex, message);
    }

    // Method to verify if a message was sent to a specific client
    public List<String> getMessagesSentToClient(int clientIndex) {
        return messagesSent.getOrDefault(clientIndex, new ArrayList<>());
    }
}
