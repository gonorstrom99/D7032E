// Source code is decompiled from a .class file using FernFlower decompiler.
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;

class PlayerInfo {
   private int playerId;
    ArrayList<String> hand;
   private ArrayList<String> greenApples = new ArrayList<>();
   private boolean judge = false;
//    public String submittedAnswer;

   public PlayerInfo(int playerId, boolean judge) {
    this.playerId = playerId;
    this.judge = judge;
    this.hand = new ArrayList<>();
}

    public int getPlayerId() {
        return playerId;
    }

    public boolean isJudge() {
        return judge;
    }

    public void setJudge(boolean judge) {
        this.judge = judge;
    }

    public ArrayList<String> getHand() {
        return hand;
    }

    public void addCardToHand(String card) {
        hand.add(card);
    }
    public void addGreenApple(String card){
        this.greenApples.add(card);
    }
    public int GreenAppleSize(){
        return greenApples.size();
    }
}