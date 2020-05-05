import java.util.*;

public class Deck {
    private List<Card> deck;

    public Deck() {
        List<String> colours = new ArrayList<>(Arrays.asList("red", "blue", "green", "yellow"));
        // reverse card does nothing in OnePlayerGame
        Map<String, String> values = new HashMap<String, String>();
        values.put("zero", "0");
        values.put("one", "1");
        values.put("two", "2");
        values.put("three", "3");
        values.put("four", "4");
        values.put("five", "5");
        values.put("six", "6");
        values.put("seven", "7");
        values.put("eight", "8");
        values.put("nine", "9");
        values.put("draw two", "+2");
        values.put("skip", "⌀");
        values.put("reverse", "↩︎");
        values.put("wild card", "Wild");
        values.put("wild card draw four", "+4");

        deck = new ArrayList<>();

        for(String colour : colours) {
            for(String value : values.keySet()) {
                // make colour null initially so that user can choose colour when card is played
                if (value.contains("wild")) {
                    deck.add(new Card(null, value, values.get(value)));
                    deck.add(new Card(null, value, values.get(value)));
                }
                else {
                    deck.add(new Card(colour, value, values.get(value)));
                    deck.add(new Card(colour, value, values.get(value)));
                }
            }
        }
    }

    public void shuffle() {
        Collections.shuffle(deck);
    }

    public Card deal() {
        return deck.remove(deck.size() - 1);
    }

    public int deckSize() {
        return deck.size();
    }
}
