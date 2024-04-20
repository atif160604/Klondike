package cs3500.klondike;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cs3500.klondike.controller.KlondikeController;
import cs3500.klondike.controller.KlondikeTextualController;
import cs3500.klondike.model.hw02.BasicKlondike;
import cs3500.klondike.model.hw02.Card;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Tests for the controller.
 */

public class ControllerTests {

  private KlondikeTextualController controller;

  List<Card> riggedDeck;
  List<Card> riggedDeck2;
  List<String> riggedDeck2Needed;

  List<Card> riggedDeck3;
  List<String> riggedDeck3Needed;

  List<Card> riggedDeck4;
  List<String> riggedDeck4Needed;
  BasicKlondike model;
  List<Card> deck;

  @Before
  public void setup() {

    Readable rd = new StringReader("");
    Appendable ap = new StringBuilder();
    controller = new KlondikeTextualController(rd, ap);
    model = new BasicKlondike();

    deck = model.getDeck();
    riggedDeck2Needed = new ArrayList<>(Arrays.asList("A♡", "2♡", "3♡", "A♣", "2♣", "3♣", "A♠",
            "2♠", "3♠", "A♢", "2♢", "3♢"));

    riggedDeck3Needed = new ArrayList<>(Arrays.asList("2♣", "2♡", "3♡", "3♣", "A♠",
            "2♠", "3♠", "A♢", "2♢", "3♢"));

    riggedDeck2Needed = new ArrayList<>(Arrays.asList("A♡", "2♡", "A♣", "2♣", "A♠",
            "2♠", "A♢", "2♢"));

    riggedDeck = new ArrayList<>();
    riggedDeck2 = new ArrayList<>();
    riggedDeck3 = new ArrayList<>();
    riggedDeck4 = new ArrayList<>();

    for (Card c : deck) {
      if (c.toString().equals("A♡") || c.toString().equals("A♣") || c.toString().equals("A♠")
              || c.toString().equals("A♢")) {
        riggedDeck.add(c);
      }
    }
    for (String wanted : riggedDeck2Needed) {
      for (Card c : deck) {
        if (c.toString().equals(wanted)) {
          riggedDeck2.add(c);
        }
      }
    }

    for (String wanted : riggedDeck3Needed) {
      for (Card c : deck) {
        if (c.toString().equals(wanted)) {
          riggedDeck3.add(c);
        }
      }
    }
  }

  @Test
  public void testPlayGameWithNullModel() {
    List<Card> deck = riggedDeck;
    try {
      controller.playGame(null, deck, false, 7, 3);
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      assertEquals("model be null.", e.getMessage());
    }
  }

  @Test
  public void testGameQuitOnQ() {
    Readable rdQuit = new StringReader("q");
    StringBuilder apResult = new StringBuilder();
    KlondikeTextualController quitController = new KlondikeTextualController(rdQuit, apResult);

    model.startGame(deck, false, 7, 3);

    String initialGameState = apResult.toString();

    quitController.playGame(model, deck, false, 7, 3);

    model.moveDraw(5);

    assertEquals(initialGameState, apResult.toString());
    Assert.assertTrue(apResult.toString().contains("Game quit!"));
  }


  @Test
  public void testMoveToFoundation() {
    Readable reader = new StringReader("mpf 1 2 q");
    Appendable output = new StringBuilder();
    KlondikeController controller = new KlondikeTextualController(reader, output);

    controller.playGame(model, deck, false, 3, 3);
    Assert.assertTrue(output.toString().contains("Game quit!"));
  }

  @Test
  public void testInvalidIntegerInput() {
    Readable reader = new StringReader("mdf abc q");
    Appendable output = new StringBuilder();
    KlondikeController controller = new KlondikeTextualController(reader, output);

    controller.playGame(model, deck, false, 2, 1);
    Assert.assertTrue(output.toString().contains("Expected an integer input."));
  }

  @Test
  public void testGameOverMessage() {
    Readable reader = new StringReader("q");
    Appendable output = new StringBuilder();
    KlondikeController controller = new KlondikeTextualController(reader, output);

    controller.playGame(model, deck, true, 7, 3);
    Assert.assertTrue(output.toString().contains("Game over. Score:"));
  }

  @Test
  public void testWinMessage() {
    Readable reader = new StringReader("md 3");
    Appendable output = new StringBuilder();
    KlondikeController controller = new KlondikeTextualController(reader, output);

    controller.playGame(model, riggedDeck2, true, 2, 1);
    Assert.assertTrue(output.toString().contains("You win!"));
  }

  @Test
  public void testMultipleInvalidCommands() {
    Readable reader = new StringReader("xyz mdf abc q");
    Appendable output = new StringBuilder();
    KlondikeController controller = new KlondikeTextualController(reader, output);

    controller.playGame(model, deck, false, 2, 1);
    Assert.assertTrue(output.toString().contains("Invalid command."));
  }

  @Test
  public void testEmptyInput() {
    Readable reader = new StringReader("");
    Appendable output = new StringBuilder();
    KlondikeController controller = new KlondikeTextualController(reader, output);

    controller.playGame(model, deck, true, 3, 3);
    Assert.assertFalse(output.toString().contains("Game quit!"));
  }

  @Test
  public void testNegativeIntegerInput() {
    Readable reader = new StringReader("mdf -1 q");
    Appendable output = new StringBuilder();
    KlondikeController controller = new KlondikeTextualController(reader, output);

    controller.playGame(model, deck, false, 3, 3);
    Assert.assertTrue(output.toString().contains("Expected an integer input."));
  }

  @Test
  public void testInvalidMoveToPile() {
    Readable reader = new StringReader("mpp 1 2 1 q");
    Appendable output = new StringBuilder();
    KlondikeController controller = new KlondikeTextualController(reader, output);

    controller.playGame(model, deck, false, 3, 3);
    Assert.assertTrue(output.toString().contains("Invalid move. Play again."));
  }

  @Test
  public void testGameStateDisplayOnStart() {
    Readable reader = new StringReader("q");
    Appendable output = new StringBuilder();
    KlondikeController controller = new KlondikeTextualController(reader, output);

    controller.playGame(model, deck, false, 7, 3);
    Assert.assertTrue(output.toString().contains("Score:"));
  }

  @Test
  public void testGameStateDisplayOnInvalidMove() {
    Readable reader = new StringReader("mpp 1 2 1 q");
    Appendable output = new StringBuilder();
    KlondikeController controller = new KlondikeTextualController(reader, output);

    controller.playGame(model, deck, false, 7, 3);
    Assert.assertTrue(output.toString().contains("Score:"));
  }

  @Test
  public void testStateOnMultipleCommands() {
    Readable reader = new StringReader("dd mdf 2 mpp 1 1 2 q");
    Appendable output = new StringBuilder();
    KlondikeController controller = new KlondikeTextualController(reader, output);

    controller.playGame(model, deck, true, 3, 3);
    Assert.assertTrue(output.toString().contains("Game quit!"));
  }


}