import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class CardComponent extends JComponent {
    private static CardComponent selected = null;
    private Card card;
    private int xCoord, yCoord;
    private Rectangle cardFace, cardBorder;
    private boolean selectable;

    public CardComponent(Card card, boolean selectable) {
        this.card = card;
        xCoord = 5;
        yCoord = 5;
        cardFace = new Rectangle(xCoord, yCoord, 50, 72);
        cardBorder = new Rectangle(xCoord, yCoord, 50, 72);

        class MyMouseListener implements MouseListener {
            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (selectable) {
                    System.out.println("clicked the " + card.getColour() + " " + card.getValue());

                    if (selected != null) {
                        selected.setLocation(selected.getxCoord(), yCoord + 55);
                    }
                    selected = CardComponent.this;
                    setLocation(xCoord, yCoord + 10);
                }

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        }
        MouseListener listener = new MyMouseListener();
        addMouseListener(listener);
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public int getxCoord() {
        return xCoord;
    }

    public void setxCoord(int xCoord) {
        this.xCoord = xCoord;
    }

    public int getyCoord() {
        return yCoord;
    }

    public void setyCoord(int yCoord) {
        this.yCoord = yCoord;
    }

    public static CardComponent getSelected() {
        return selected;
    }

    public static void setSelected(CardComponent selected) {
        CardComponent.selected = selected;
    }

    @Override
    public void paintComponent(Graphics g) {
        //super.paintComponent(g);
        setPreferredSize(new Dimension(xCoord + 55, yCoord + 77));
        //revalidate();
        Graphics2D g2 = (Graphics2D) g;
        float thickness = 5;
        Stroke oldStroke = g2.getStroke();
        g2.setStroke(new BasicStroke(thickness));
        g2.setColor(Color.WHITE);
        g2.draw(cardBorder);
        g2.setStroke(oldStroke);
        if (card.isShowing()) {
            if (card.getColour() == null) {
                g2.setColor(Color.GRAY);
            } else if (card.getColour().equals("green")) {
                g2.setColor(new Color(0, 220, 0));
            } else if (card.getColour().equals("red")) {
                g2.setColor(new Color(215, 0, 0));
            } else if (card.getColour().equals("blue")) {
                g2.setColor(new Color(0, 128, 255));
            } else if (card.getColour().equals("yellow")) {
                g2.setColor(new Color(240, 240, 0));
            }
            g2.fill(cardFace);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD, 20));
            g2.drawString(card.getSymbol(), 30 - (card.getSymbol().length() * 5), yCoord + 40);
        }
        else {
            g2.setColor(Color.BLACK);
            g2.fill(cardFace);
            g2.setColor(Color.DARK_GRAY);
            g2.draw(new Rectangle(12, yCoord + 7, 36, 58));
            g2.setColor(Color.GRAY);
            g2.setFont(new Font("Serif", Font.BOLD, 20));
            g2.drawString("W", 20, yCoord + 42);
        }
    }
}
