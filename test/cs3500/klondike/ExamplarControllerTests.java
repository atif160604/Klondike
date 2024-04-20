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


/**
 * Tests for the controller.
 */
public class ExamplarControllerTests {

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
  public void testquitGame() {
    Readable reader = new StringReader("q");
    Appendable output = new StringBuilder();
    KlondikeController controller = new KlondikeTextualController(reader, output);

    controller.playGame(model, deck, false, 2, 1);
    Assert.assertTrue(output.toString().contains("Game quit!"));
  }

  @Test
  public void testMoveDraw() {
    Readable reader = new StringReader("md 500 q");
    Appendable output = new StringBuilder();
    KlondikeController controller = new KlondikeTextualController(reader, output);

    controller.playGame(model, deck, false, 2, 1);
    Assert.assertTrue(output.toString().contains("Game quit!"));
  }

  @Test
  public void testMovePile() {
    Readable reader = new StringReader("mpp 500 2 3 q");
    Appendable output = new StringBuilder();
    KlondikeController controller = new KlondikeTextualController(reader, output);

    controller.playGame(model, deck, false, 2, 1);
    Assert.assertTrue(output.toString().contains("Game quit!"));
  }

  @Test
  public void testDiscardDraw() {
    Readable reader = new StringReader("q");
    Appendable output = new StringBuilder();
    KlondikeController controller = new KlondikeTextualController(reader, output);

    controller.playGame(model, riggedDeck, false, 2, 1);
    String[] display = output.toString().split("\n");
    Assert.assertEquals(display.length, 12);
  }

  @Test
  public void testInvalidDrawFoundation() {
    Readable reader = new StringReader("mdf *&x q");
    Appendable output = new StringBuilder();
    KlondikeController controller = new KlondikeTextualController(reader, output);

    controller.playGame(model, riggedDeck2, false, 2, 1);
    Assert.assertTrue(output.toString().contains("Game quit!"));
  }

  @Test
  public void testMoveCascadePileValidPileWrongCard() {
    Readable reader = new StringReader("mpp 1 1 2 q");
    Appendable output = new StringBuilder();
    KlondikeController controller = new KlondikeTextualController(reader, output);

    controller.playGame(model, riggedDeck3, false, 2, 1);
    Assert.assertFalse(output.toString().contains("Invalid move. Play again."));
  }

  @Test
  public void testInavlidMoveDraw() {
    Readable reader = new StringReader("md 1 q");
    Appendable output = new StringBuilder();
    KlondikeController controller = new KlondikeTextualController(reader, output);
    controller.playGame(model, riggedDeck3, false, 2, 1);

    String[] disp = output.toString().split("\n");
    Assert.assertTrue(disp[7].contains(model.getDrawCards().get(0).toString()));
  }


}


