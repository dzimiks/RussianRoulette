package app.server;

import app.Constants;

public class GamesInfo {

    private int numberOfSticksInHand;
    private int nextClientNumber;
    private int numberOfPlayedGames;

    public GamesInfo() {
        this.nextClientNumber = 0;
        this.numberOfSticksInHand = Constants.CLIENTS_NUMBER;
        this.numberOfPlayedGames = 0;
    }

    public int getNumberOfSticksInHand() {
        return numberOfSticksInHand;
    }

    public void decrementSticks() {
        --numberOfSticksInHand;
    }

    public void resetSticks() {
        numberOfSticksInHand = Constants.CLIENTS_NUMBER;
    }

    public int getNextClientNumber() {
        return nextClientNumber;
    }

    public void incrementNextClientNumber() {
        this.nextClientNumber = (this.nextClientNumber + 1 == Constants.CLIENTS_NUMBER)
                ? 0 : ++this.nextClientNumber;
//        this.nextClientNumber++;
//
//        if (nextClientNumber == Constants.CLIENTS_NUMBER) {
//            nextClientNumber = 0;
//        }
    }

    public void resetNextClientNumber() {
        nextClientNumber = 0;
    }

    public int getNumberOfPlayedGames() {
        return numberOfPlayedGames;
    }

    public void incrementNumberOfPlayedGames() {
        ++numberOfPlayedGames;
    }
}
