package cs3500.klondike.model.hw02;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * This is a stub implementation of the {@link cs3500.klondike.model.hw02.KlondikeModel}
 * interface. You may assume that the actual implementation of BasicKlondike will have a
 * zero-argument (i.e. default) constructor, and that all the methods below will be
 * implemented.  You may not make any other assumptions about the implementation of this
 * class (e.g. what fields it might have, or helper methods, etc.).
 *
 * <p>Once you've implemented all the constructors and methods on your own, you can
 * delete the placeholderWarning() method.
 */
public class BasicKlondike implements cs3500.klondike.model.hw02.KlondikeModel {

  private List<List<Card>> cascadePile;
  private List<List<Card>> foundationDecks;
  private List<CardModel> drawCards;
  private int numDraw;
  private boolean gameStarted;

  /**
   * Class for basicKlondike.
   */
  public BasicKlondike() {
    this.cascadePile = new ArrayList<>();
    this.foundationDecks = new ArrayList<>();
    this.drawCards = new ArrayList<>();
    this.gameStarted = false;

  }

  @Override
  public List<Card> getDeck() {
    return CardModel.getFullDeck();
  }

  @Override
  public void startGame(List<Card> deck, boolean shuffle, int numPiles, int numDraw)
          throws IllegalArgumentException {

    if (this.gameStarted) {
      throw new IllegalStateException("Game has already started");
    }

    if (deck == null || !this.validateDeck(deck) || numPiles <= 0 || numDraw <= 0
            || ((numPiles * (numPiles + 1)) / 2) > deck.size()) {
      throw new IllegalArgumentException("Invalid input to start game");
    }

    int numberOfDecks = deck.size() / 52;
    List<Card> standardDeck = CardModel.getFullDeck();
    Map<Card, Integer> expectedCardFrequencies = new HashMap<>();

    for (Card card : standardDeck) {
      expectedCardFrequencies.put(card, numberOfDecks);
    }

    for (Card card : deck) {
      if (!expectedCardFrequencies.containsKey(card) || expectedCardFrequencies.get(card) <= 0) {
        throw new IllegalArgumentException("Deck doesn't match the expected repetitions of the "
                + "standard deck.");
      }
      expectedCardFrequencies.put(card, expectedCardFrequencies.get(card) - 1);
    }

    if (shuffle) {
      Collections.shuffle(deck);
    }

    int numCascadeCards = ((numPiles) * (numPiles + 1)) / 2;

    this.cascadePile.clear();
    for (int i = 0; i < numPiles; i++) {
      cascadePile.add(new ArrayList<>());
    }

    int index = 0;
    for (int i = 0; i < numPiles; i++) {
      for (int j = 0; j < i + 1 && index < deck.size(); j++) {
        cascadePile.get(i).add(deck.get(index++));
      }
    }

    this.drawCards.clear();
    while (index < deck.size()) {
      if (deck.get(index) instanceof CardModel) {
        drawCards.add((CardModel) deck.get(index++));
      } else {
        throw new IllegalArgumentException("Unexpected card type.");
      }
    }

    this.foundationDecks.clear();
    for (int i = 0; i < 4; i++) {
      foundationDecks.add(new ArrayList<>());
    }

    this.numDraw = numDraw;

    gameStarted = true;
  }

  boolean validateDeck(List<Card> deck) {
    if (deck == null) {
      throw new IllegalArgumentException("Deck cannot be null.");
    }

    if (deck.stream().anyMatch(Objects::isNull)) {
      throw new IllegalArgumentException("Deck cannot contain null cards");
    }

    Map<Character, List<Card>> suitToCardsMap = new HashMap<>();
    suitToCardsMap.put('♣', new ArrayList<>());
    suitToCardsMap.put('♠', new ArrayList<>());
    suitToCardsMap.put('♡', new ArrayList<>());
    suitToCardsMap.put('♢', new ArrayList<>());

    for (Card card : deck) {
      char suit = card.toString().charAt(card.toString().length() - 1);
      suitToCardsMap.get(suit).add(card);
    }

    return true;
  }


  @Override
  public void movePile(int srcPile, int numCards, int destPile) throws IllegalStateException {
    verifyGameStarted();

    if (srcPile < 0 || destPile < 0 || numCards <= 0 || srcPile == destPile
            || srcPile >= cascadePile.size() || destPile >= cascadePile.size()) {
      throw new IllegalArgumentException("Invalid entry, cannot move pile");
    }

    List<Card> srcCascade = cascadePile.get(srcPile);
    List<Card> destCascade = cascadePile.get(destPile);

    if (srcCascade.size() < numCards) {
      throw new IllegalArgumentException("Move is not allowable");
    }

    if (destCascade.isEmpty() && !isKing(srcCascade.get(srcCascade.size() - numCards))) {
      throw new IllegalStateException("Move is not allowable");
    }

    if (!destCascade.isEmpty() && !isValidMove(srcCascade.get(srcCascade.size() - numCards),
            destCascade.get(destCascade.size() - 1))) {
      throw new IllegalStateException("This move is not allowable.");
    }

    destCascade.addAll(srcCascade.subList(srcCascade.size() - numCards, srcCascade.size()));
    srcCascade.subList(srcCascade.size() - numCards, srcCascade.size()).clear();
  }

  private boolean isKing(Card card) {
    return card.toString().endsWith("K");
  }

  private boolean isValidMove(Card moveCard, Card cascadeCard) {
    if (getCardColor(moveCard).equals(getCardColor(cascadeCard))) {
      return false;
    }
    return getCardRank(cascadeCard) - getCardRank(moveCard) == 1;
  }

  private String getCardColor(Card card) {
    char suit = card.toString().charAt(card.toString().length() - 1);
    return (suit == '♡' || suit == '♢') ? "red" : "black";
  }

  private int getCardRank(Card card) {
    String value = card.toString().substring(0, card.toString().length() - 1);
    String[] order = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};

    for (int i = 0; i < order.length; i++) {
      if (order[i].equals(value)) {
        return i;
      }
    }
    return -1;
  }

  private void verifyGameStarted() {
    if (!gameStarted) {
      throw new IllegalStateException("Game is not started");
    }
  }


  @Override
  public void moveDraw(int destPile) {
    this.verifyGameStarted();

    if (destPile < 0 || destPile >= cascadePile.size()) {
      throw new IllegalArgumentException("Invalid destination pile index");
    }

    List<Card> destCascade = cascadePile.get(destPile);

    if (drawCards.isEmpty()) {
      throw new IllegalStateException("Draw pile is empty.");
    }

    Card movingCard = drawCards.get(drawCards.size() - 1);

    if (destCascade.isEmpty()) {
      if (movingCard.toString().endsWith("K")) {
        destCascade.add(drawCards.remove(drawCards.size() - 1));
      } else {
        throw new IllegalStateException("Only a King can be placed on an empty cascade pile");
      }
    } else if (this.isValidCascadeMove(movingCard, destCascade.get(destCascade.size() - 1))) {
      destCascade.add(drawCards.remove(drawCards.size() - 1));
    } else {
      throw new IllegalStateException("Invalid move");
    }
  }

  private boolean isValidCascadeMove(Card moveCard, Card cascadeCard) {
    boolean isDifferentColor = (cascadeCard.toString().contains("♡") ||
            cascadeCard.toString().contains("♢"))
            && (moveCard.toString().contains("♣") || moveCard.toString().contains("♠"))
            || (cascadeCard.toString().contains("♣") || cascadeCard.toString().contains("♠"))
            && (moveCard.toString().contains("♡") || moveCard.toString().contains("♢"));

    int movingCardValue = moveCard.toString().charAt(0) - '0';
    int cascadeCardValue = cascadeCard.toString().charAt(0) - '0';

    return isDifferentColor && movingCardValue == cascadeCardValue - 1;
  }

  @Override
  public void moveToFoundation(int srcPile, int foundationPile) throws IllegalStateException {
    if (!gameStarted) {
      throw new IllegalStateException("Game is not started");
    }
    if (srcPile > cascadePile.size() || foundationPile > foundationDecks.size()) {
      throw new IllegalArgumentException("Pile out of range");
    }

    List<Card> currentCascade = cascadePile.get(srcPile);

    if (currentCascade.isEmpty()) {
      throw new IllegalStateException("Source pile is empty");
    }

    Card cardMove = currentCascade.get(currentCascade.size() - 1);

    if (!isValidMoveToFoundation(cardMove, foundationDecks.get(foundationPile))) {
      throw new IllegalStateException("Invalid move to foundation");
    }

    cardMove = currentCascade.remove(currentCascade.size() - 1);
    foundationDecks.get(foundationPile).add(cardMove);
  }

  @Override
  public void moveDrawToFoundation(int foundationPile) throws IllegalStateException {
    if (!gameStarted) {
      throw new IllegalStateException("Game is not started");
    }

    if (foundationPile < 0 || foundationPile >= foundationDecks.size()) {
      throw new IllegalArgumentException("Invalid foundation pile number");
    }

    if (drawCards.isEmpty()) {
      throw new IllegalStateException("Draw pile is empty");
    }

    List<Card> foundationList = foundationDecks.get(foundationPile);
    Card movingCard = drawCards.get(drawCards.size() - 1);

    if (foundationList.isEmpty() && !movingCard.toString().startsWith("A")) {
      throw new IllegalStateException("Draw card is not A, cannot move to empty foundation.");
    }

    if (foundationList.isEmpty() || isValidMoveToFoundation(movingCard, foundationList)) {
      drawCards.remove(drawCards.size() - 1);
      foundationList.add(movingCard);
    } else {
      throw new IllegalStateException("This move is not allowable");
    }
  }

  private boolean isValidMoveToFoundation(Card cardMove, List<Card> foundation) {
    if (foundation.isEmpty()) {
      return cardMove.toString().startsWith("A");
    } else {
      Card topFoundationCard = foundation.get(foundation.size() - 1);
      char movingSuit = cardMove.toString().charAt(cardMove.toString().length() - 1);
      char foundationSuit =
              topFoundationCard.toString().charAt(topFoundationCard.toString().length() - 1);

      if (movingSuit != foundationSuit) {
        return false;
      }

      String[] order = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
      String movingCardValue = cardMove.toString().substring(0, cardMove.toString().length() - 1);
      String foundationCardValue = topFoundationCard.toString().substring(0, topFoundationCard
              .toString().length() - 1);

      int movingIndex = Arrays.asList(order).indexOf(movingCardValue);
      int foundationIndex = Arrays.asList(order).indexOf(foundationCardValue);

      return movingIndex == foundationIndex + 1;
    }
  }


  @Override
  public void discardDraw() throws IllegalStateException {
    if (!gameStarted) {
      throw new IllegalStateException("Game is not started");
    }
    if (drawCards.isEmpty()) {
      throw new IllegalStateException("No cards available in the draw pile to discard");
    }
    CardModel toRemove = drawCards.remove(drawCards.size() - 1);
    drawCards.add(toRemove);
  }

  @Override
  public int getNumRows() {
    verifyGameStarted();
    return cascadePile.stream().mapToInt(List::size).max().orElse(0);
  }

  @Override
  public int getNumPiles() {
    verifyGameStarted();
    return cascadePile.size();
  }

  @Override
  public int getNumDraw() {
    verifyGameStarted();
    return numDraw;
  }


  @Override
  public boolean isGameOver() throws IllegalStateException {
    if (!gameStarted) {
      throw new IllegalStateException("Game is not started");
    }
    return cascadePile.stream().allMatch(List::isEmpty) && drawCards.isEmpty();
  }

  @Override
  public int getScore() throws IllegalStateException {
    if (!gameStarted) {
      throw new IllegalStateException("Game hasn't been started yet.");
    }

    int score = 0;

    for (List<Card> foundation : foundationDecks) {
      if (!foundation.isEmpty()) {
        Card topCard = foundation.get(foundation.size() - 1);
        String cardStr = topCard.toString();

        String valueStr = cardStr.substring(0, cardStr.length() - 1);
        switch (valueStr) {
          case "A":
            score += 1;
            break;
          case "2":
            score += 2;
            break;
          case "3":
            score += 3;
            break;
          case "4":
            score += 4;
            break;
          case "5":
            score += 5;
            break;
          case "6":
            score += 6;
            break;
          case "7":
            score += 7;
            break;
          case "8":
            score += 8;
            break;
          case "9":
            score += 9;
            break;
          case "10":
            score += 10;
            break;
          case "J":
            score += 11;
            break;
          case "Q":
            score += 12;
            break;
          case "K":
            score += 13;
            break;
          default:
            score += 0;
        }
      }
    }

    return score;
  }

  @Override
  public int getPileHeight(int pileNum) throws IllegalStateException {
    if (!gameStarted) {
      throw new IllegalStateException("Game is not started");
    }

    return cascadePile.get(pileNum).size();
  }

  @Override
  public boolean isCardVisible(int pileNum, int card) throws IllegalStateException {
    if (!gameStarted) {
      throw new IllegalStateException("Game is not started");
    }
    if (pileNum < 0 || pileNum >= cascadePile.size() || card < 0 || card >= cascadePile.
            get(pileNum).size()) {
      throw new IllegalArgumentException("Invalid pile or card number.");
    }
    return card == cascadePile.get(pileNum).size() - 1;
  }

  @Override
  public Card getCardAt(int pileNum, int card) throws IllegalStateException {
    if (!gameStarted) {
      throw new IllegalStateException("Game is not started");
    }
    if (pileNum < 0 || pileNum >= cascadePile.size() || card < 0 || card >=
            cascadePile.get(pileNum).size()) {
      throw new IllegalArgumentException("Invalid pile or card number.");
    }
    return cascadePile.get(pileNum).get(card);
  }

  @Override
  public Card getCardAt(int foundationPile) throws IllegalStateException {
    if (!gameStarted) {
      throw new IllegalStateException("Game is not started");
    }
    List<Card> foundation = foundationDecks.get(foundationPile);
    if (foundation.isEmpty()) {
      return null;
    } else {
      return foundation.get(foundation.size() - 1);
    }
  }

  @Override
  public List<Card> getDrawCards() throws IllegalStateException {
    if (!gameStarted) {
      throw new IllegalStateException("Game is not started");
    }

    int fromIndex = Math.max(0, drawCards.size() - numDraw);
    return new ArrayList<>(drawCards.subList(fromIndex, drawCards.size()));
  }

  @Override
  public int getNumFoundations() throws IllegalStateException {
    if (!gameStarted) {
      throw new IllegalStateException("Game is not started");
    }
    return foundationDecks.size();
  }
}
