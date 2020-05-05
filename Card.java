import java.io.Serializable;

public class Card implements Serializable {
    private String colour;
    private String value;
    private String symbol;
    private boolean showing;

    public Card(String colour, String value, String symbol) {
        this.colour = colour;
        this.value = value;
        this.symbol = symbol;
        showing = true;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public boolean isShowing() {
        return showing;
    }

    public void setShowing(boolean showing) {
        this.showing = showing;
    }

    @Override
    public String toString() {
        if (colour == null) {
            return value;
        }
        else {
            return colour + " " + value;
        }
    }
}
