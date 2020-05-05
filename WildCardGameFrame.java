import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class WildCardGameFrame extends JFrame {
    private String[] colours;
    private String username;
    private JLabel playerOneLabel, playerTwoLabel, playerThreeLabel, playerFourLabel;
    private ArrayList<JPanel> playerPanels;
    private ArrayList<JLabel> playerLabels;
    private JPanel mainPanel, playerOnePanel, playerTwoPanel, playerThreePanel, playerFourPanel, deckPanel;
    private JButton readyButton, drawButton;
    private Deck deck;
    private Card topCard;
    private CardComponent topCardComponent;
    private ArrayList<CardComponent> cardComponents;
    private HashMap<String, Boolean> players;
    private Player p;
    private boolean turn;
    ObjectOutputStream outToServer;
    ObjectInputStream inFromServer;

    public WildCardGameFrame() throws Exception {
        playerPanels = new ArrayList<>();
        playerLabels = new ArrayList<>();
        turn = false;
        players = new HashMap<String, Boolean>();
        colours = new String[]{"green", "red", "yellow", "blue"};
        p = new Player();
        deck = new Deck();
        cardComponents = new ArrayList<>();
        deck.shuffle();
        p.addCard(deck.deal());
        p.addCard(deck.deal());
        p.addCard(deck.deal());
        p.addCard(deck.deal());
        p.addCard(deck.deal());
        p.addCard(deck.deal());
        p.addCard(deck.deal());

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(0, 135, 0));
        addPlayerTwoPanel();
        addPlayerThreePanel();
        addPlayerFourPanel();
        addPlayerOnePanel();
        addDeckPanel();

        connectToServer();

        mainPanel.add(playerOnePanel, BorderLayout.SOUTH);
        mainPanel.add(playerPanels.get(0), BorderLayout.NORTH);
        mainPanel.add(playerPanels.get(1), BorderLayout.EAST);
        mainPanel.add(playerPanels.get(2), BorderLayout.WEST);
        mainPanel.add(deckPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    public void addPlayerOnePanel() {
        playerOnePanel = new JPanel();
        playerOnePanel.setBackground(new Color(0, 135, 0));
        playerOnePanel.setLayout(null);
        playerOnePanel.setPreferredSize(new Dimension(200, 150));
        playerOneLabel = new JLabel("Player 2");
        playerOnePanel.add(playerOneLabel);
        int i = 0;
        drawHand();
    }

    public void drawHand() {
        cardComponents.clear();
        int i = 0;
        int x = 200 - (5 * p.handSize());
        int spacing = 45 - (p.handSize());
        int y = 60;
        for (Card card : p.getHand()) {
            cardComponents.add(new CardComponent(card, true));
            cardComponents.get(cardComponents.size() - 1).setxCoord(x + (spacing * i));
            cardComponents.get(cardComponents.size() - 1).setBounds(x + (spacing * i), y, 60, 82);
            playerOnePanel.add(cardComponents.get(cardComponents.size() - 1));
            i++;
        }
    }

    public void addPlayerTwoPanel() {
        playerPanels.add(new JPanel());
        //playerPanels = new JPanel();
        playerPanels.get(0).setBackground(new Color(0, 135, 0));
        playerLabels.add(new JLabel("Player 2"));
        //playerTwoLabel = new JLabel("Player 2");
        playerPanels.get(0).add(playerLabels.get(0));
    }

    public void addPlayerThreePanel() {
        playerPanels.add(new JPanel());
        //playerThreePanel = new JPanel();
        playerPanels.get(1).setBackground(new Color(0, 135, 0));
        //playerThreeLabel = new JLabel("Player 3");
        playerLabels.add(new JLabel("Player 3"));
        playerPanels.get(1).add(playerLabels.get(1));
    }

    public void addPlayerFourPanel() {
        playerPanels.add(new JPanel());
        //playerThreePanel = new JPanel();
        playerPanels.get(2).setBackground(new Color(0, 135, 0));
        //playerThreeLabel = new JLabel("Player 3");
        playerLabels.add(new JLabel("Player 4"));
        playerPanels.get(2).add(playerLabels.get(2));
    }

    public void addDeckPanel() {
        readyButton = new JButton();
        readyButton.setBounds(20, 300 ,120, 40);
        readyButton.setText("Not Ready");
        readyButton.setBackground(Color.GRAY);
        readyButton.setOpaque(true);
        readyButton.setBorderPainted(false);

        drawButton = new JButton();
        drawButton.setBounds(20, 300 ,120, 40);
        drawButton.setText("Draw Card");
        drawButton.setBackground(Color.yellow);
        drawButton.setOpaque(true);
        drawButton.setBorderPainted(false);
        drawButton.setVisible(false);
        drawButton.setEnabled(false);

        deckPanel = new JPanel();
        deckPanel.setLayout(null);
        topCard = new Card("green", "six", "6");
        topCard.setShowing(false);
        topCardComponent = new CardComponent(topCard, false);
        topCardComponent.setxCoord(260);
        topCardComponent.setBounds(260, 190, 60, 82);
        deckPanel.add(topCardComponent);
        deckPanel.add(readyButton);
        deckPanel.add(drawButton);
        deckPanel.setBackground(new Color(0, 135, 0));

        class MyMouseListener implements MouseListener {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (turn) {
                    if (CardComponent.getSelected() != null) {
                        if(canPlayCard(CardComponent.getSelected().getCard(), topCard)) {
                            System.out.println("you played a " + CardComponent.getSelected().getCard() + " on a " + topCard);
                            if (CardComponent.getSelected().getCard().getColour() == null) {
                                Object selected = JOptionPane.showInputDialog(null, "What colour would you like to set the card?", "Selection", JOptionPane.DEFAULT_OPTION, null, colours, "green");
                                CardComponent.getSelected().getCard().setColour((String) selected);
                            }
                            try {
                                CardComponent.getSelected().setVisible(false);
                                p.removeCard(CardComponent.getSelected().getCard());
                                turn = false;
                                drawButton.setEnabled(false);
                                for (CardComponent cc : cardComponents) {
                                    playerOnePanel.remove(cc);
                                }
                                drawHand();

                                if (p.handSize() == 0) {
                                    outToServer.writeObject(new Message(MessageType.WIN, null, CardComponent.getSelected().getCard(), false));
                                }
                                else {
                                    outToServer.writeObject(new Message(MessageType.PLAY, null, CardComponent.getSelected().getCard(), true));
                                }
                            } catch (IOException ioException) {
                                ioException.printStackTrace();
                            }
                        }
                        else {
                            System.out.println("You can't play that card!");
                        }
                    }
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        }
        MouseListener deckListener = new MyMouseListener();
        topCardComponent.addMouseListener(deckListener);

        class ReadyListener implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent e) {
                readyButton.setBackground(Color.GREEN);
                readyButton.setText("Ready");
                try {
                    outToServer.writeObject(new Message(MessageType.READY, username, null, false));
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }
        ActionListener readyListener = new ReadyListener();
        readyButton.addActionListener(readyListener);

        class DrawListener implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    turn = false;
                    outToServer.writeObject(new Message(MessageType.DRAW, null, null, false));
                    drawButton.setEnabled(false);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }
        ActionListener drawListener = new DrawListener();
        drawButton.addActionListener(drawListener);
    }

    public boolean canPlayCard(Card c1, Card c2) {
        if (c1.getValue().contains("wild")) {
            return true;
        }
        else if (c1.getColour().equals(c2.getColour())) {
            return true;
        }
        else if (c1.getValue().equals(c2.getValue())) {
            return true;
        }
        return false;
    }

    public void connectToServer() throws Exception {
        // connecting to server
        Socket clientSocket = new Socket("localhost", 5062);

        // text input from user
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

        // Card/text io for UnoServer
        inFromServer = new ObjectInputStream(clientSocket.getInputStream());
        outToServer = new ObjectOutputStream(clientSocket.getOutputStream());


        // getting unique username and starting the io Threads
        Message incomingMsg;
        while (true) {
            username = JOptionPane.showInputDialog("Input a unique username", null);
            outToServer.writeObject(new Message(MessageType.USERNAME, username, null, false));

            incomingMsg = (Message) inFromServer.readObject();

            if (incomingMsg.getType() == MessageType.JOINED) {
                System.out.println(incomingMsg.getMessage());
                Thread r = new WildCardGameFrame.receiveHandler(inFromServer, outToServer);
                r.start();
                break;
            }
        }
    }

    // Thread for handling Card io to/from UnoServer
    private class receiveHandler extends Thread {
        ObjectInputStream in;
        ObjectOutputStream out;
        Message incoming;

        public receiveHandler(ObjectInputStream in, ObjectOutputStream out) {
            this.in = in;
            this.out = out;
        }

        public void run() {
            while (true) {
                try {
                    incoming = (Message)in.readObject();

                    if (incoming.getType() == MessageType.TEXT) {
                        System.out.println(incoming.getMessage());
                    }
                    else if (incoming.getType() == MessageType.TURN) {
                        // check if the card has effects enabled
                        turn = true;
                        System.out.println(incoming.getMessage());
                        //System.out.println("The top card is a " + incoming.getCard());
                        topCardComponent.setCard(incoming.getCard());
                        topCardComponent.getCard().setShowing(true);
                        drawButton.setEnabled(true);
                        repaint();

                        if (incoming.isEffectEnabled()) {
                            if (deck.deckSize() <= 5) {
                                deck = new Deck();
                                deck.shuffle();
                            }
                            switch (incoming.getCard().getValue()) {
                                case "draw two":
                                    p.addCard(deck.deal());
                                    p.addCard(deck.deal());
                                    for (CardComponent cc : cardComponents) {
                                        playerOnePanel.remove(cc);
                                    }
                                    drawHand();
                                    System.out.println("draw two cards");
                                    turn = false;
                                    drawButton.setEnabled(false);
                                    outToServer.writeObject(new Message(MessageType.NEXT, null, incoming.getCard(), false));
                                    repaint();
                                    break;
                                case "wild card draw four":
                                    p.addCard(deck.deal());
                                    p.addCard(deck.deal());
                                    p.addCard(deck.deal());
                                    p.addCard(deck.deal());
                                    for (CardComponent cc : cardComponents) {
                                        playerOnePanel.remove(cc);
                                    }
                                    drawHand();
                                    System.out.println("draw four cards");
                                    turn = false;
                                    drawButton.setEnabled(false);
                                    outToServer.writeObject(new Message(MessageType.NEXT, null, incoming.getCard(), false));
                                    repaint();
                                    break;
                                case "skip":
                                    System.out.println("skip your turn");
                                    turn = false;
                                    drawButton.setEnabled(false);
                                    outToServer.writeObject(new Message(MessageType.NEXT, null, incoming.getCard(), false));
                                    repaint();
                                    break;
                            }
                        }
                    }
                    else if (incoming.getType() == MessageType.UPDATECARD) {
                        topCard = incoming.getCard();
                        System.out.println("The top card is a " + incoming.getCard());
                        topCardComponent.setCard(incoming.getCard());
                        topCardComponent.getCard().setShowing(true);
                        repaint();
                    }
                    else if (incoming.getType() == MessageType.UPDATEPLAYERS) {
                        String[] playerMsg = incoming.getMessage().split(",");
                        for (String p : playerMsg) {
                            if (!players.containsKey(p)) {
                                players.put(p, false);
                            }
                        }
                        // updating GUI
                        int j = 0;
                        for (String key : players.keySet()) {
                            if (!key.equals(username)) {
                                playerLabels.get(j).setText(key);
                                j++;
                            }
                        }
                    }
                    else if (incoming.getType() == MessageType.START) {
                        readyButton.setVisible(false);
                        drawButton.setVisible(true);
                    }
                    else if (incoming.getType() == MessageType.DRAW) {
                        p.addCard(incoming.getCard());
                        //System.out.println(incoming.getMessage());
                        for (CardComponent cc : cardComponents) {
                            playerOnePanel.remove(cc);
                        }
                        drawHand();
                        repaint();
                    }
                    else if (incoming.getType() == MessageType.READY) {
                        players.put(incoming.getMessage(), true);

                        // updating ready status of other players
                        for (String key : players.keySet()) {
                            if (incoming.getMessage().equals(key)) {
                                players.put(key, true);
                            }
                        }
                        // updating labels
                        for (JLabel label : playerLabels) {
                            if (label.getText().equals(incoming.getMessage())) {
                                label.setForeground(Color.GREEN);
                                repaint();
                            }
                        }

                    }
                    else if (incoming.getType() == MessageType.WIN) {
                        System.out.println(incoming.getMessage());
                        return;
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
