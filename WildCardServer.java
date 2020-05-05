import java.io.*;
import java.net.*;
import java.util.*;

public class WildCardServer {
    private static List<ObjectOutputStream> outputs;
    private static List<String> users;
    private static int userCount;
    private static int readyCount;
    private static int nextPlayer;
    private static int sequence;
    private static Deck deck;
    private static Card topCard;

    public WildCardServer() {
        outputs = new ArrayList<>();
        users = new ArrayList<>();
        userCount = 0;
        readyCount = 0;
        sequence = 1;
        deck = new Deck();
        deck.shuffle();
        nextPlayer = 0;
        topCard = new Card("red", "five", "5");
    }

    public static void main(String[] args) throws Exception {
        WildCardServer s = new WildCardServer();
        s.startServer(5062);
    }

    public void startServer(int port) throws Exception {
        ServerSocket welcomeSocket = new ServerSocket(port);
        System.out.println("Server is running...");

        while (true) {
            Socket connectionSocket = null;

            try {
                connectionSocket = welcomeSocket.accept();
                System.out.println("A new client is connected: " + connectionSocket);
                ObjectOutputStream outToClient = new ObjectOutputStream(connectionSocket.getOutputStream());
                ObjectInputStream inFromClient = new ObjectInputStream(connectionSocket.getInputStream());
                System.out.println("Assigning new thread for this client");

                Thread t = new ClientHandler(connectionSocket, inFromClient, outToClient);
                t.start();

            } catch (Exception e) {
                assert connectionSocket != null;
                connectionSocket.close();
            }
        }
    }

    private static class ClientHandler extends Thread {
        final ObjectInputStream in;
        final ObjectOutputStream out;
        final Socket s;

        public ClientHandler(Socket s, ObjectInputStream in, ObjectOutputStream out) {
            this.s = s;
            this.in = in;
            this.out = out;
            outputs.add(out);
        }

        @Override
        public void run() {

            Message clientMsg;

            while (true) {
                try {
                    clientMsg = (Message) in.readObject();

                    if (clientMsg.getType() == MessageType.USERNAME) {
                        if (!clientMsg.getMessage().isEmpty() && !users.contains(clientMsg.getMessage())) {
                            userCount++;
                            users.add(clientMsg.getMessage());
                            out.writeObject(new Message(MessageType.JOINED, "You have joined the lobby", null, false));

                            // sending joined players to all players
                            String playersMsg = "";
                            for (int i = 0; i < users.size(); i++) {
                                if (i != 0) {
                                    playersMsg += ",";
                                }
                                playersMsg += users.get(i);
                            }
                            for (ObjectOutputStream o : outputs) {
                                o.writeObject(new Message(MessageType.UPDATEPLAYERS, playersMsg, null, false));
                            }
                        }
                        else {
                            out.writeObject(new Message(MessageType.USERNAME, null, null, false));
                        }
                    } else if (clientMsg.getType() == MessageType.READY) {
                        readyCount++;
                        out.writeObject(new Message(MessageType.TEXT, "you are ready to play!", null, false));
                        // sending READY message to all players to update ready status in GUI
                        for (ObjectOutputStream o : outputs) {
                            o.writeObject(new Message(MessageType.READY, clientMsg.getMessage(), null, false));
                        }

                        if (readyCount == userCount) {
                            int rand = new Random().nextInt(userCount);
                            nextPlayer = rand;
                            for (ObjectOutputStream o : outputs) {
                                o.writeObject(new Message(MessageType.START, null, null, false));
                                o.writeObject(new Message(MessageType.TEXT, "" + users.get(rand) + " goes first", null, false));
                                o.writeObject(new Message(MessageType.UPDATECARD, null, topCard, false));
                            }
                            outputs.get(nextPlayer).writeObject(new Message(MessageType.TURN, "Your turn!", topCard, false));
                        }
                    } else if (clientMsg.getType() == MessageType.DRAW) {
                        System.out.println("send the player a card or just send a TURN message to the next player");
                        if (deck.deckSize() <= 1) {
                            deck = new Deck();
                            deck.shuffle();
                        }
                        outputs.get(nextPlayer).writeObject(new Message(MessageType.DRAW, "You drew a card", deck.deal(), false));

                        if (nextPlayer + sequence >= userCount) {
                            nextPlayer = 0;
                        }
                        else if (nextPlayer + sequence < 0) {
                            nextPlayer = userCount - 1;
                        }
                        else {
                            nextPlayer += sequence;
                        }
                        outputs.get(nextPlayer).writeObject(new Message(MessageType.TURN, "Your turn!", topCard, false));
                    } else if (clientMsg.getType() == MessageType.PLAY) {
                        topCard = clientMsg.getCard();
                        for (ObjectOutputStream o : outputs) {
                            o.writeObject(new Message(MessageType.UPDATECARD, null, topCard, false));
                        }
                        if (clientMsg.getCard().getValue().equals("reverse")) {
                            sequence = -sequence;
                        }

                        if (nextPlayer + sequence >= userCount) {
                            nextPlayer = 0;
                        }
                        else if (nextPlayer + sequence < 0) {
                            nextPlayer = userCount - 1;
                        }
                        else {
                            nextPlayer += sequence;
                        }
                        outputs.get(nextPlayer).writeObject(new Message(MessageType.TURN, null, topCard, true));
                        System.out.println("send the player a card or just send a PLAY message to the next player");
                        System.out.println("Also keep track of how many cards each player has");
                    } else if (clientMsg.getType() == MessageType.NEXT) {
                        /*
                       if (clientMsg.getCard().getValue().equals("reverse")) {
                            sequence = -1 * sequence;
                        }
                         */
                        if (nextPlayer + sequence >= userCount) {
                            nextPlayer = 0;
                        }
                        else if (nextPlayer + sequence < 0) {
                            nextPlayer = userCount - 1;
                        }
                        else {
                            nextPlayer += sequence;
                        }
                        outputs.get(nextPlayer).writeObject(new Message(MessageType.TURN, null, topCard, false));
                    }
                    else if (clientMsg.getType() == MessageType.WIN) {
                        topCard = clientMsg.getCard();
                        for (ObjectOutputStream o : outputs) {
                            o.writeObject(new Message(MessageType.UPDATECARD, null, topCard, false));
                        }
                        for (ObjectOutputStream o : outputs) {
                            o.writeObject(new Message(MessageType.WIN, "Player " + (nextPlayer + 1) + " wins!", null, false));
                        }
                        return;
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
