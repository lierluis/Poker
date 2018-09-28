/*************************************************************************************
 *
 * This program is used to test game.MyPokerGame class
 *
 **************************************************************************************/

import game.MyPokerGame;

class TestMyPokerGame {

    public static void main(String args[]) {
        MyPokerGame mypokergame;
        if (args.length > 0) {
            mypokergame = new MyPokerGame(Integer.parseInt(args[0]));
        }
        else {
            mypokergame = new MyPokerGame();
        }
        mypokergame.play();
    }
}
