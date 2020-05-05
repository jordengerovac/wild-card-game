import java.io.Serializable;

enum MessageType {
    READY,
    DRAW,
    PLAY,
    JOINED,
    TURN,
    USERNAME,
    TEXT,
    UPDATECARD,
    UPDATEPLAYERS,
    START,
    NEXT,
    WIN
}

public class Message implements Serializable {
    MessageType type;
    String message;
    Card card;
    boolean effectEnabled;

    public Message(MessageType type, String message, Card card, boolean effectEnabled) {
        this.type = type;
        this.message = message;
        this.card = card;
        this.effectEnabled = effectEnabled;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public MessageType getType() {
        return type;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public Card getCard() {
        return card;
    }

    public void setEffectEnabled(boolean effectEnabled) {
        this.effectEnabled = effectEnabled;
    }

    public boolean isEffectEnabled() {
        return effectEnabled;
    }
}
