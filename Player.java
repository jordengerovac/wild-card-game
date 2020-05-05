import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Player {
    private HashSet<Card> hand;

    public Player() {
        hand = new HashSet<>();
    }

    public int handSize() {
        return hand.size();
    }

    public void addCard(Card c) {
        hand.add(c);
    }

    public void removeCard(Card c) {
        hand.remove(c);
    }

    public HashSet<Card> getHand() {
        return hand;
    }

    public String showHand() {
        String result = "";
        int count = 1;
        for (Card card : hand) {
            result += count + " - " + card + "\n";
            count++;
        }
        return result;
    }
}
