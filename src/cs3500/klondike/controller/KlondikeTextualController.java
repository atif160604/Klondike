package cs3500.klondike.controller;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import cs3500.klondike.model.hw02.Card;
import cs3500.klondike.model.hw02.KlondikeModel;
import cs3500.klondike.view.KlondikeTextualView;

/**
 * The textual controller.
 */

public class KlondikeTextualController implements KlondikeController {
  private final Readable rd;
  private final Appendable a;

  /**
   * The primary method for beginning and playing a game.
   *
   * @param rd the readable.
   * @param ap the appendable.
   * @throws IllegalArgumentException if the model is null
   */

  public KlondikeTextualController(Readable rd, Appendable ap) {
    if (rd == null || ap == null) {
      throw new IllegalArgumentException("Arguments cannot be null");
    }
    this.rd = rd;
    this.a = ap;
  }

  /**
   * The primary method for beginning and playing a game.
   *
   * @param model    The game of solitaire to be played
   * @param deck     The deck of cards to be used
   * @param shuffle  Whether to shuffle the deck or not
   * @param numPiles How many piles should be in the initial deal
   * @param numDraw  How many draw cards should be visible
   * @throws IllegalArgumentException if the model is null
   * @throws IllegalStateException    if the game cannot be started,
   *                                  or if the controller cannot interact with the player.
   */
  @Override
  public void playGame(KlondikeModel model, List<Card> deck, boolean shuffle,
                       int numPiles, int numDraw) {
    if (model == null || deck == null) {
      throw new IllegalArgumentException("model be null.");
    }
    if (deck.isEmpty()) {
      throw new IllegalStateException("deck cannot be null");
    }

    model.startGame(deck, shuffle, numPiles, numDraw);
    Scanner scanner = new Scanner(rd);
    KlondikeTextualView view = new KlondikeTextualView(model);

    while (!model.isGameOver() && scanner.hasNext()) {
      displayGameState(view, model);

      String input = scanner.next();

      if (!isValidCommand(input)) {
        appendErrorMessage(new IllegalArgumentException("Invalid command."));
        continue;
      }

      switch (input.toLowerCase()) {
        case "q":
          handleQuitCommand(view, model);
          return;
        case "mdf":
          handleMoveDrawToFoundation(scanner, model);
          break;
        case "dd":
          handleDiscardDraw(model);
          break;
        case "mpp":
          handleMovePile(scanner, model);
          break;
        case "md":
          handleMoveDraw(scanner, model);
          break;
        case "mpf":
          handleMoveToFoundation(scanner, model);
          break;
        default:
          break;
      }
    }
    displayGameState(view, model);
    displayGameOverMessage(model);
  }

  private boolean isValidCommand(String input) {
    String[] validCommands = {"q", "mdf", "dd", "mpp", "md", "mpf"};
    for (String cmd : validCommands) {
      if (cmd.equalsIgnoreCase(input)) {
        return true;
      }
    }
    return false;
  }

  private int nextInt(Scanner scanner) {
    if (scanner.hasNextInt()) {
      return scanner.nextInt();
    } else {
      scanner.next();
      appendErrorMessage(new IllegalArgumentException("Expected an integer input."));
      return -1;
    }
  }

  private void handleMoveDrawToFoundation(Scanner scanner, KlondikeModel model) {
    int foundationPile = nextInt(scanner);
    if (foundationPile == -1) {
      return;
    }

    foundationPile -= 1;
    try {
      model.moveDrawToFoundation(foundationPile);
    } catch (Exception e) {
      appendErrorMessage(e);
    }
  }

  private void handleDiscardDraw(KlondikeModel model) {
    try {
      model.discardDraw();
    } catch (Exception e) {
      appendErrorMessage(e);
    }
  }

  private void handleMovePile(Scanner scanner, KlondikeModel model) {
    int src = nextInt(scanner);
    if (src == -1) {
      return;
    }

    int count = nextInt(scanner);
    if (count == -1) {
      return;
    }

    int dest = nextInt(scanner);
    if (dest == -1) {
      return;
    }

    src -= 1;
    dest -= 1;

    try {
      model.movePile(src, count, dest);
    } catch (Exception e) {
      appendErrorMessage(e);
    }
  }

  private void handleMoveDraw(Scanner scanner, KlondikeModel model) {
    int dest = nextInt(scanner);
    if (dest == -1) {
      return;
    }

    dest -= 1;
    try {
      model.moveDraw(dest);
    } catch (Exception e) {
      appendErrorMessage(e);
    }
  }

  private void handleMoveToFoundation(Scanner scanner, KlondikeModel model) {
    int src = nextInt(scanner);
    if (src == -1) {
      return;
    }

    int foundationPile = nextInt(scanner);
    if (foundationPile == -1) {
      return;
    }

    src -= 1;
    foundationPile -= 1;

    try {
      model.moveToFoundation(src, foundationPile);
    } catch (Exception e) {
      appendErrorMessage(e);
    }
  }

  private void displayGameState(KlondikeTextualView view, KlondikeModel model) {
    try {
      a.append(view.toString());
      a.append("Score: ").append(String.valueOf(model.getScore())).append("\n");
    } catch (IOException e) {
      throw new IllegalStateException("Failed to render the game state.", e);
    }
  }

  private void handleQuitCommand(KlondikeTextualView view, KlondikeModel model) {
    try {
      a.append("Game quit!\n");
      a.append("State of game when quit:\n");
      a.append(view.toString());
      a.append("Score: ").append(String.valueOf(model.getScore())).append("\n");
    } catch (IOException e) {
      throw new IllegalStateException("Failed to render the game state.", e);
    }
  }

  private void appendErrorMessage(Exception e) {
    try {
      a.append("Invalid move. Play again. ").append(e.getMessage()).append("\n");
    } catch (IOException ioException) {
      throw new IllegalStateException("Failed to notify about the invalid move.", ioException);
    }
  }

  private void displayGameOverMessage(KlondikeModel model) {
    try {
      int totalFoundationCards = 0;

      for (int i = 0; i < model.getNumFoundations(); i++) {
        Card topCard = model.getCardAt(i);
        if (topCard != null) {
          totalFoundationCards += (i + 1);
        }
      }
      if (totalFoundationCards == model.getDeck().size()) {
        a.append("You win!\n");
      } else {
        a.append("Game over. Score: ").append(String.valueOf(model.getScore())).append("\n");
      }
    } catch (IOException e) {
      throw new IllegalStateException("Failed to render the game's ending state.", e);
    }
  }


}
