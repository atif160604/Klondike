package cs3500.klondike.model.hw02;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * model for card representations.
 */
public class CardModel implements Card {

  private final Suit suit;
  private final Value value;


  // Inside CardModel class
  private boolean visible = true;

  public CardModel(Suit suit, Value value) {
    this.suit = suit;
    this.value = value;
  }

  /**
   * getting a full deck.
   */
  public static List<Card> getFullDeck() {
    List<Card> deck = new ArrayList<>();
    for (Suit suit : Suit.values()) {
      for (Value value : Value.values()) {
        deck.add(new CardModel(suit, value));
      }
    }
    return deck;
  }

  /**
   * enum for suits.
   */
  public enum Suit {
    CLUBS("♣"),
    SPADES("♠"),
    HEARTS("♡"),
    DIAMONDS("♢");

    private final String symbol;

    Suit(String symbol) {
      this.symbol = symbol;
    }

    @Override
    public String toString() {
      return symbol;
    }
  }

  /**
   * enum for value of cards.
   */
  public enum Value {
    ACE("A"),
    TWO("2"),
    THREE("3"),
    FOUR("4"),
    FIVE("5"),
    SIX("6"),
    SEVEN("7"),
    EIGHT("8"),
    NINE("9"),
    TEN("10"),
    JACK("J"),
    QUEEN("Q"),
    KING("K");

    private final String representation;

    Value(String representation) {
      this.representation = representation;
    }

    @Override
    public String toString() {
      return representation;
    }
  }

  @Override
  public String toString() {
    return value.toString() + suit.toString();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    CardModel card = (CardModel) obj;
    return suit == card.suit && value == card.value;
  }

  @Override
  public int hashCode() {
    return Objects.hash(suit, value);
  }
}
