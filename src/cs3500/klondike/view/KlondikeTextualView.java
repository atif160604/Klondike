package cs3500.klondike.view;

import java.io.IOException;
import java.util.List;

import cs3500.klondike.model.hw02.Card;
import cs3500.klondike.model.hw02.KlondikeModel;


/**
 * The textual view representation.
 */
public class KlondikeTextualView implements TextualView {
  private final KlondikeModel model;
  private final Appendable ap;

  public KlondikeTextualView(KlondikeModel model) {
    this.model = model;
    this.ap = new StringBuilder();
  }

  public KlondikeTextualView(KlondikeModel model, Appendable ap) {
    this.model = model;
    this.ap = ap;
  }

  @Override
  public void render() throws IOException {
    ap.append(toString());
  }

  @Override
  public String toString() {
    StringBuilder result = new StringBuilder();

    result.append("Draw: ");
    List<Card> drawPile = model.getDrawCards();
    for (Card c : drawPile) {
      result.append(c.toString()).append(", ");
    }
    if (!drawPile.isEmpty()) {
      result.setLength(result.length() - 2);
    }
    result.append("\n");

    // Foundation piles
    result.append("Foundation: ");
    for (int i = 0; i < model.getNumFoundations(); i++) {
      Card topCard = model.getCardAt(i);
      if (topCard == null) {
        result.append("<empty>, ");
      } else {
        result.append(topCard.toString()).append(", ");
      }
    }
    result.setLength(result.length() - 2);
    result.append("\n");

    // Cascade piles
    for (int i = 0; i < model.getNumPiles(); i++) {
      result.append("Cascade ").append(i + 1).append(": ");
      for (int j = 0; j < model.getPileHeight(i); j++) {
        if (model.isCardVisible(i, j)) {
          result.append(model.getCardAt(i, j).toString()).append(", ");
        } else {
          result.append("?, ");
        }
      }
      if (model.getPileHeight(i) > 0) {
        result.setLength(result.length() - 2);
      }
      result.append("\n");
    }

    return result.toString();
  }


}
