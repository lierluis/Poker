package game;

import java.util.*;

/*
 * Ref: https://en.wikipedia.org/wiki/Video_poker
 *
 * Short Description and Poker rules:
 *
 * Video poker is also known as draw poker.
 * The dealer uses a 52-card deck, which is played fresh after each currentHand.
 * The player is dealt one five-card poker currentHand.
 * After the first draw, which is automatic, you may hold any of the cards and draw
 *   again to replace the cards that you haven't chosen to hold.
 * Your cards are compared to a table of winning combinations.
 * The object is to get the best possible combination so that you earn the highest
 *   payout on the bet you placed.
 *
 * Winning Combinations
 *
 * 1. Jacks or Better: a pair pays out only if the cards in the pair are Jacks,
 *    Queens, Kings, or Aces. Lower pairs do not pay out.
 * 2. Two Pair: two sets of pairs of the same card denomination.
 * 3. Three of a Kind: three cards of the same denomination.
 * 4. Straight: five consecutive denomination cards of different suit.
 * 5. Flush: five non-consecutive denomination cards of the same suit.
 * 6. Full House: a set of three cards of the same denomination plus
 *    a set of two cards of the same denomination.
 * 7. Four of a kind: four cards of the same denomination.
 * 8. Straight Flush: five consecutive denomination cards of the same suit.
 * 9. Royal Flush: five consecutive denomination cards of the same suit,
 *    starting from 10 and ending with an ace
 */

/*
 * Main poker game class uses Decks and Card objects to implement the poker game.
 */
public class MyPokerGame {

    // default constant value
    private static final int startingBalance = 100;
    private static final int numberOfCards = 5;

    // default constant payout value and currentHand types
    private static final int[] multipliers = {1, 2, 3, 5, 6, 9, 25, 50, 250};
    private static final String[] goodHandTypes = {
        "Royal Pair", "Two Pair", "Three of a Kind", "Straight", "Flush     ",
        "Full House", "Four of a Kind", "Straight Flush", "Royal Flush"};

    // must use only one deck
    private static final Decks oneDeck = new Decks(1);

    // holding current poker 5-card hand, balance, bet
    private List<Card> currentHand;
    private List<Card> tempHand;
    private int balance;
    private int bet;
    private int[] value;

    /**
     * default constructor, set balance = startingBalance
     */
    public MyPokerGame() {
        this(startingBalance);
    }

    /**
     * constructor, set given balance
     *
     * @param balance
     */
    public MyPokerGame(int balance) {
        this.balance = balance;
    }

    /**
     * This displays the payout table based on multipliers and goodHandTypes arrays
     */
    private void showPayoutTable() {
        System.out.println("\n\n");
        System.out.println("Payout Table         Multiplier   ");
        System.out.println("=======================================");
        int size = multipliers.length;
        for (int i = size - 1; i >= 0; i--) {
            System.out.println(goodHandTypes[i] + "\t|\t" + multipliers[i]);
        }
        System.out.println("\n\n");
    }

    /**
     * Check currentHand using multipliers and goodHandTypes arrays.
     */
    private void checkHands() {

        int[] ranks = new int[14]; // keeps track of the occurences of ranks
        for (int i = 0; i <= 13; i++) {
            ranks[i] = 0; // initialize
        }
        for (int i = 0; i <= 4; i++) {
            // increment rank array at the index of each card's rank
            ranks[currentHand.get(i).getRank()]++;
        }

        ////////// PAIR, 2 PAIR, 3 OF A KIND, 4 OF A KIND, FULL HOUSE //////////
        // sameCards used to check if there is a pair, sameCards2 if 2nd pair exists
        int sameCards = 1, sameCards2 = 1;

        // largeRank & smallRank hold the ranks of the pairs of cards, e.g. J's & 10's
        int largeRank = 0, smallRank = 0;

        for (int i = 13; i >= 1; i--) {
            if (ranks[i] > sameCards) {

                if (sameCards == 1) {
                    largeRank = i;
                } else {
                    sameCards2 = sameCards;
                    smallRank = i;
                }
                sameCards = ranks[i];

            } else if (ranks[i] > sameCards2) {
                sameCards2 = ranks[i];
                smallRank = i;
            }
        }

        ////////////////////////////// FLUSH //////////////////////////////////
        boolean flush = true; // assume there is a flush
        for (int i = 0; i < 4; i++) {
            if (currentHand.get(i).getSuit() != currentHand.get(i + 1).getSuit()) {
                flush = false;
            }
        }

        ///////////////////////////// STRAIGHT ////////////////////////////////
        boolean straight = false;
        int topStraightValue = 0; // used to hold highest rank if there is a straight

        for (int i = 1; i <= 9; i++) { // can't have straight with lowest value > 10
            // there is 1 card for 5 sequential ranks
            if (ranks[i] == 1 && ranks[i + 1] == 1 && ranks[i + 2] == 1 && ranks[i + 3] == 1 && ranks[i + 4] == 1) {
                straight = true;
                topStraightValue = i + 4; // 4 above bottom value
                break;
            }
        }
        // ace high straight
        if (ranks[10] == 1 && ranks[11] == 1 && ranks[12] == 1 && ranks[13] == 1 && ranks[1] == 1) {
            straight = true;
            topStraightValue = 14; // higher than king
        }

        ///////////////////////////////////////////////////////////////////////
        // otherRanks holds ranks of cards, including kickers
        int[] otherRanks = new int[5];
        int index = 0;

        if (ranks[1] == 1) {        // if ace, run this before because ace is highest card
            otherRanks[index] = 14; // record an ace as 14 instead of 1
            index++;
        }
        for (int i = 13; i >= 2; i--) { // ranks excluding ace
            if (ranks[i] == 1) {
                otherRanks[index] = i;
                index++;
            }
        }

        /* DETERMINE HAND RANKINGS */
        // value[0] contains the type of hand, from worst to best
        //      1-high card, 2-one pair, 3-two pair, 4-three of a kind, 5-straight
        //      6-flush, 7-full house, 8-four of a kind, 9-straight flush
        // value[1 to 5] contain the card value from highest to lowest
        value = new int[3]; // value holds the value of each card in hand
        //value = new int[6];

        if (sameCards == 1) { // if no pair
            value[0] = 1;

            //values[1] = otherRanks[0]; // highest rank
            //values[2] = otherRanks[1]; // kickers...
            //values[3] = otherRanks[2];
            //values[4] = otherRanks[3];
            //values[5] = otherRanks[4];
        }
        if (sameCards == 2 && sameCards2 == 1) { // if 1 pair
            value[0] = 2;
            value[1] = largeRank; // rank of pair

            //values[2] = otherRanks[0]; // kickers...
            //values[3] = otherRanks[1];
            //values[4] = otherRanks[2];
        }
        if (sameCards == 2 && sameCards2 == 2) { // if two pair
            value[0] = 3;
            value[1] = (largeRank > smallRank) ? largeRank : smallRank; // rank of larger pair
            value[2] = (largeRank < smallRank) ? largeRank : smallRank; // rank of smaller pair

            //values[3] = otherRanks[0]; // kicker
        }
        if (sameCards == 3 && sameCards2 != 2) { // if 3 of a kind, not full house
            value[0] = 4;
            value[1] = largeRank; // rank of 3 of a kind

            //values[2] = otherRanks[0]; // kickers...
            //values[3] = otherRanks[1];
        }
        if (straight) { // if straight
            value[0] = 5;
            value[1] = topStraightValue; // highest rank
        }
        if (flush) { // if flush
            value[0] = 6;
            value[1] = otherRanks[0]; // highest rank

            //value[2] = otherRanks[1];
            //value[3] = otherRanks[2];
            //value[4] = otherRanks[3];
            //value[5] = otherRanks[4];
        }
        if (sameCards == 3 && sameCards2 == 2) { // if full house
            value[0] = 7;
            value[1] = largeRank; // higher rank
            value[2] = smallRank; // lower rank
        }
        if (sameCards == 4) { // if 4 of a kind
            value[0] = 8;
            value[1] = largeRank; // rank of 4 of a kind

            //value[2] = otherRanks[0];
        }
        if (straight && flush) { // straight + flush = straight flush
            value[0] = 9;
            value[1] = topStraightValue; // highest rank
        }

        display(); // displays hands
    }

    // Displays hand types
    private void display() {

        // if A, change value from 14 back to 1 to be able to print A
        if (value[1] == 14) {
            value[1] = 1;
        }

        String s;
        // value[0] contains type of hand
        switch (value[0]) {
            case 1: // high card (not a winning rank)
                s = "\n\t" + "No pair" + "\n\t" + "Sorry, you lost!";
                break;
            case 2: // 1 pair
                if (value[1] == 1 || value[1] == 11 || value[1] == 12 || value[1] == 13) { // royal pair
                    s = "\n\t" + "Pair of " + Card.Rank[value[1]] + "'s"
                            + "\n\t" + goodHandTypes[0] + "!";
                    balance += bet;
                } else {
                    s = "\n\t" + "Pair of " + Card.Rank[value[1]] + "'s"
                            + "\n\t" + "Sorry, you lost!";
                }
                break;
            case 3: // 2 pair
                s = "\n\t" + goodHandTypes[1] + "!";
                balance += (bet * 2);
                break;
            case 4: // 3 of a kind
                s = "\n\t" + "Three " + Card.Rank[value[1]] + "'s"
                        + "\n\t" + goodHandTypes[2] + "!";
                balance += (bet * 3);
                break;
            case 5: // straight
                s = "\n\t" + goodHandTypes[3] + "!";
                balance += (bet * 5);
                break;
            case 6: // flush
                s = "\n\t" + "Flush!";
                balance += (bet * 6);
                break;
            case 7: // full house
                s = "\n\t" + goodHandTypes[5] + "!";
                balance += (bet * 9);
                break;
            case 8: // 4 of a kind
                s = "\n\t" + "Four " + Card.Rank[value[1]] + "'s"
                        + "\n\t" + goodHandTypes[6] + "!";
                balance += (bet * 25);
                break;
            case 9: // straight flush
                if (value[1] == 1) { // royal flush
                    s = "\n\t" + goodHandTypes[8] + "!";
                    balance += (bet * 250);
                } else {
                    s = "\n\t" + goodHandTypes[7] + "!";
                    balance += (bet * 50);
                }
                break;
            default:
                s = "wut";
        }
        System.out.println(s);
    }

    public void play() {
        /**
         * The main algorithm for single player poker game
         *
         * Steps: showPayoutTable()
         *
         * ++ show balance, get bet & verify bet value, update balance, reset
         * deck, shuffle deck, deal cards and display cards, ask for position of
         * cards to keep & get positions in one input line, update cards, check
         * hands, display proper messages, update balance if there is a payout,
         * if balance = O: end of program; else ask if the player wants to play
         * a new game. if the answer is "no" : end of program; else :
         * showPayoutTable() if user wants to see it go to ++
         */

        boolean play = false;
        showPayoutTable();
        do {
            System.out.println("----------------------------------------");
            System.out.println("Balance: $" + balance);

            Scanner input = new Scanner(System.in);

            // get bet
            System.out.print("Enter bet (0 > bet < balance): ");
            bet = input.nextInt(); // read int
            while (bet <= 0 || bet > balance) {
                System.out.print("Please enter valid bet: ");
                bet = input.nextInt();
            }
            input.nextLine(); // read \n

            // update balance
            balance -= bet;

            // prepare deck
            oneDeck.reset();
            oneDeck.shuffle();

            // deal cards
            try {
                currentHand = oneDeck.deal(numberOfCards);
                System.out.println("Hand: " + currentHand);
            } catch (PlayingCardException ex) {
                System.out.println("Derp");
            }

            // read cards to keep
            System.out.print("Enter positions (1-5) of cards to keep (e.g. 1 4 5): ");

            Scanner stringInput = new Scanner(input.nextLine());
            stringInput = stringInput.useDelimiter("\\s*");

            // keep user's chosen elements in currentHand
            Card[] tempArrayHand = new Card[currentHand.size()];
            for (int i = 0; i < currentHand.size(); i++) {
                tempArrayHand[i] = currentHand.get(i);
            }
            currentHand.removeAll(currentHand);
            while (stringInput.hasNext()) {
                if (stringInput.hasNextInt()) {
                    String dataString = stringInput.findInLine("\\d+");
                    Integer dataInt = Integer.parseInt(dataString);

                    currentHand.add(tempArrayHand[dataInt - 1]);
                }
            }

            // deal again to fill remaining cards user didn't choose to keep
            try {
                tempHand = oneDeck.deal(numberOfCards);
            } catch (PlayingCardException ex) {
                System.out.println("Derp");
            }
            for (int i = currentHand.size(); i < 5; i++) {
                currentHand.add(tempHand.remove(0));
            }
	    System.out.println();
            System.out.println("Hand: " + currentHand);

            // check hands
            checkHands();
            System.out.println();

            // show new balance
            System.out.println("Your balance: $" + balance);

            if (balance == 0) {
                System.out.println("We have enjoyed taking all of your money. Bye! :D");
                System.exit(0);
            }

            // play again?
            System.out.print("Would you like to play again? (y or n): ");
            String playAgain = input.next();

            while (!(playAgain.equals("y") || playAgain.equals("n"))) {
                System.out.print("Incorrect input. Please enter again: ");
                playAgain = input.next();
            }
            switch (playAgain) {
                case "y":
                    play = true;
                    break;
                case "n":
                    System.out.println("Thanks for playing!");
                    play = false;
                    break;
                default:
                    System.out.println("Wut");
            }

            // print payout table
            if (play == true) {
                System.out.print("Would you like to see the payout table? (y or n): ");
                String seeTable = input.next();

                while (!(seeTable.equals("y") || seeTable.equals("n"))) {
                    System.out.print("Incorrect input. Please enter again: ");
                    seeTable = input.next();
                }
                switch (seeTable) {
                    case "y":
                        showPayoutTable();
                        break;
                    default:
                }
            }
        } while (play == true);
    }

    /**
     * Test checkHands() method, which prints current hand type
     */
    public void testCheckHands() {
        try {
            currentHand = new ArrayList<Card>();

            // set Royal Flush
            currentHand.add(new Card(1, 3));
            currentHand.add(new Card(10, 3));
            currentHand.add(new Card(12, 3));
            currentHand.add(new Card(11, 3));
            currentHand.add(new Card(13, 3));
            System.out.println(currentHand);
            checkHands();
            System.out.println("-----------------------------------");

            // set Straight Flush
            currentHand.set(0, new Card(9, 3));
            System.out.println(currentHand);
            checkHands();
            System.out.println("-----------------------------------");

            // set Straight
            currentHand.set(4, new Card(8, 1));
            System.out.println(currentHand);
            checkHands();
            System.out.println("-----------------------------------");

            // set Flush
            currentHand.set(4, new Card(5, 3));
            System.out.println(currentHand);
            checkHands();
            System.out.println("-----------------------------------");

            // set Four of a Kind
            currentHand.clear();
            currentHand.add(new Card(8, 3));
            currentHand.add(new Card(8, 0));
            currentHand.add(new Card(12, 3));
            currentHand.add(new Card(8, 1));
            currentHand.add(new Card(8, 2));
            System.out.println(currentHand);
            checkHands();
            System.out.println("-----------------------------------");

            // set Three of a Kind
            currentHand.set(4, new Card(11, 3));
            System.out.println(currentHand);
            checkHands();
            System.out.println("-----------------------------------");

            // set Full House
            currentHand.set(2, new Card(11, 1));
            System.out.println(currentHand);
            checkHands();
            System.out.println("-----------------------------------");

            // set Two Pairs
            currentHand.set(1, new Card(9, 1));
            System.out.println(currentHand);
            checkHands();
            System.out.println("-----------------------------------");

            // set Royal Pair
            currentHand.set(0, new Card(3, 1));
            System.out.println(currentHand);
            checkHands();
            System.out.println("-----------------------------------");

            // non Royal Pair
            currentHand.set(2, new Card(3, 3));
            System.out.println(currentHand);
            checkHands();
            System.out.println("-----------------------------------");

            // no pair
            currentHand.set(2, new Card(1, 3));
            System.out.println(currentHand);
            checkHands();
            System.out.println("-----------------------------------");

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /* Quick testCheckHands() */
    public static void main(String args[]) {
        MyPokerGame mypokergame = new MyPokerGame();
        mypokergame.testCheckHands();
    }

}
