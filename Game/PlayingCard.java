package game;

import java.util.*;

/**
 * class PlayingCardException is used for errors related to Card and Deck objects
 */
class PlayingCardException extends Exception {

    /* Constructor to create a PlayingCardException object */
    PlayingCardException() {
        super();
    }

    PlayingCardException(String reason) {
        super(reason);
    }
}

/**
 * class Card (immutable): for creating playing card objects
 * Rank - valid values are 1 to 13
 * Suit - valid values are 0 to 3
 */
class Card {

    /* constant suits and ranks */
    static final String[] Suit = {"Clubs", "Diamonds", "Hearts", "Spades"};
    static final String[] Rank = {"", "A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};

    /* Data fields of a card: rank and suit */
    private int cardRank; /* values: 1-13 (see Rank[] above) */
    private int cardSuit; /* values: 0-3  (see Suit[] above) */

    /* Constructor to create a card */
    /* throw PlayingCardException if rank or suit is invalid */
    public Card(int rank, int suit) throws PlayingCardException {
        if ((rank < 1) || (rank > 13)) {
            throw new PlayingCardException("Invalid rank:" + rank);
        } else {
            cardRank = rank;
        }
        if ((suit < 0) || (suit > 3)) {
            throw new PlayingCardException("Invalid suit:" + suit);
        } else {
            cardSuit = suit;
        }
    }

    /* Accessors */
    public int getRank() {
        return cardRank;
    }

    public int getSuit() {
        return cardSuit;
    }

    /* toString method */
    @Override
    public String toString() {
        return Rank[cardRank] + " " + Suit[cardSuit];
    }

    // A few quick tests here
    public static void main(String args[]) {
        try {
            Card c1 = new Card(1, 3); // A Spades
            System.out.println(c1);
            c1 = new Card(10, 0);     // 10 Clubs
            System.out.println(c1);
            c1 = new Card(10, 5);     // generate exception here
        } catch (PlayingCardException e) {
            System.out.println("PlayingCardException: " + e.getMessage());
        }
    }
}

/**
 * class Decks represents n decks of 52 playing cards
 */
class Decks {

    /* this is used to keep track of original n*52 cards */
    private List<Card> originalDecks;

    /* this starts with n*52 cards deck from original deck */
    /* it is used to keep track of remaining cards to deal */
    /* see reset(): it resets dealDecks to a full deck     */
    private List<Card> dealDecks;

    /* number of decks in this object */
    private int numberDecks;

    /**
     * Constructor: Creates default one deck of 52 playing cards in
     * originalDecks and copies them to dealDecks.
     */
    public Decks() {
        this(1);
    }

    /**
     * Constructor: Creates n decks (52 cards each deck) of playing cards in
     * originalDecks and copies them to dealDecks.
     */
    public Decks(int n) {
        numberDecks = n;
        originalDecks = new ArrayList<>(n);
        dealDecks = new ArrayList<>(n);

        // putting cards into originalDecks
        for (int i = 0; i < numberDecks; i++) {
            int rank = 1;
            int suit = -1;

            for (int j = 0; j < 52; j++) {
                if (j % 13 == 0) {
                    suit++;
                    rank = 1;
                }
                try {
                    originalDecks.add(new Card(rank, suit));
                } catch (PlayingCardException ex) {
                    System.out.println("Error in method Decks(int n)");
                }
                rank++;
            }
        }

        // copy cards from originalDecks into dealDecks
        for (Card originalDeck : originalDecks) {
            dealDecks.add(originalDeck);
        }
    }

    /**
     * Shuffles cards in deal deck.
     */
    public void shuffle() {
        Collections.shuffle(dealDecks);
    }

    /**
     * Deals cards from the deal deck.
     *
     * @param numberCards number of cards to deal
     * @return a list containing the cards that were dealt
     * @throws PlayingCardException if numberCards > number of remaining cards
     */
    public List<Card> deal(int numberCards) throws PlayingCardException {

        if (numberCards > remain()) {
            throw new PlayingCardException("Not enough cards to deal");
        }

        // add selected number of cards to dealtCards from dealDecks
        List<Card> dealtCards = new ArrayList<>(numberCards);
        for (int i = 0; numberCards > 0; numberCards--) {
            dealtCards.add(dealDecks.remove(i));
        }

        return dealtCards;
    }

    /**
     * Resets deal deck by getting all cards from the original deck.
     */
    public void reset() {
        dealDecks.removeAll(dealDecks);
        for (Card originalDeck : originalDecks) {
            dealDecks.add(originalDeck);
        }
    }

    /**
     * Returns number of remaining cards in deal deck.
     */
    public int remain() {
        return dealDecks.size();
    }

    /**
     * Returns a string representing cards in the deal deck
     */
    @Override
    public String toString() {
        return "" + dealDecks;
    }

    /* Quick test
     *
     * Generate 2 decks of cards
     * Loop 2 times:
     *   Deal 30 cards for 4 times
     *   Expect exception last time
     *   reset()
     */
    public static void main(String args[]) {

        System.out.println("*******    Create 2 decks of cards    *********\n\n");
        Decks decks = new Decks(2);

        for (int j = 0; j < 2; j++) {
            System.out.println("\n************************************************\n");
            System.out.println("Loop # " + j + "\n");
            System.out.println("Before shuffle: " + decks.remain() + " cards");
            System.out.println("\n\t" + decks);
            System.out.println("\n==============================================\n");

            int numHands = 4;
            int cardsPerHand = 30;

            for (int i = 0; i < numHands; i++) {
                decks.shuffle();
                System.out.println("After shuffle: " + decks.remain() + " cards");
                System.out.println("\n\t" + decks);
                try {
                    System.out.println("\n\nHand " + i + ": " + cardsPerHand + " cards");
                    System.out.println("\n\t" + decks.deal(cardsPerHand));
                    System.out.println("\n\nRemain: " + decks.remain() + " cards");
                    System.out.println("\n\t" + decks);
                    System.out.println("\n==============================================\n");
                } catch (PlayingCardException e) {
                    System.out.println("*** In catch block : PlayingCardException : msg : " + e.getMessage());
                }
            }

            decks.reset();
        }
    }
}
