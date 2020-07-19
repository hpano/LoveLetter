package game;

import java.io.*;
import java.util.*;
import java.security.SecureRandom;
import java.lang.ArrayIndexOutOfBoundsException;
import main.Console;
import game.cards.*;

public class Game {
    private List<Player> _playerList;   // 参加者リスト

    private Player _owner; //オーナープレイヤー

    private List<Card> _cardList;   //使うカードのリスト

    private int _winPoint;

    private boolean _finished;    //ゲームが終了したか否か

    private List<Round> _roundList;   //ラウンドのリスト

    private boolean _hasDuchess;    //女公爵が山札に含まれているか否か

    private boolean _hasPrince;   //王子が山札に含まれているか否か

    private boolean _hasKing;   //王が山札に含まれているか否か

    public int playerNumber() {  //現在のゲームの参加者人数を返すメソッド
        return this._playerList.size();
    }

    public boolean hasDuchess() {
        return this._hasDuchess;
    }

    public boolean hasPrince() {
        return this._hasPrince;
    }

    public boolean hasKing() {
        return this._hasKing;
    }

    /*** コンストラクタ (5人以上の時にKingをcardListに加える) ***/
    public Game(List<Player> playerList) {
        this._playerList = playerList;
        this._owner = this._playerList.get(0);
        this._winPoint = 1;
        this._finished = false;
        this._roundList = new ArrayList<Round>();
        this._hasDuchess = false;
        this._hasPrince = false;
        this._hasKing = this._playerList.size() >= 5;
        this._cardList = null;
    }

    /*** ゲームを開始するメソッド ***/
    public void startGame() throws IOException {
        int roundNum = 0;

        resetPoint();
        Console.sendMsgExceptIndex(this._playerList, 0, Console.red + "[game] " + Console.cyan + "Now, " + Console.green + this._owner.name() + Console.cyan + " is setting Game Rules." + Console.reset);
        setRule();
        this._cardList =  Card.generateCardList(this._hasDuchess, this._hasPrince, this._hasKing);

        wait(2000);
        Console.sendMsgAll(this._playerList, Console.red + "[game]" + Console.cyan + " Game start !" + Console.reset);
        do {
            resetPlayer();
            roundNum++;
            this._roundList.add(new Round(this._playerList, this._cardList, this._hasDuchess, this._hasPrince, this._hasKing));

            wait(2000);
            this._roundList.get(roundNum - 1).start(roundNum);
            judgeFinish();
        } while(!this._finished);

        wait(4000);
        Console.sendMsgAll(this._playerList, Console.red + "[game]" + Console.cyan + " Game finish !" + Console.reset);
        wait(2000);
        Console.sendMsgAll(this._playerList, Console.red + "[game]" + Console.cyan + " Winner : " + this._playerList.get(0).name() + Console.cyan + " !" + Console.reset);
        wait(500);
        Console.sendMsgAll(this._playerList, rank(this._playerList));
        wait(1000);

    }

    //ゲームルールを設定するメソッド
    public void setRule() throws IOException {
        String ans;

        //勝利点の設定
        Console.sendMsg(this._owner.out(), "/console readNum 1 10 " + Console.red + "[game]" + Console.cyan + " Decide Win_Point." + Console.reset);
        ans = Console.acceptMsg(this._owner.in());
        this._winPoint = Integer.parseInt(ans);
        Console.sendMsgExceptIndex(this._playerList, 0, Console.red + "[game] " + Console.green + this._owner.name() + Console.cyan + " set Win_Point " + Console.magenta + this._winPoint + Console.cyan + "." + Console.reset);

        //王子の設定
        Console.sendMsg(this._owner.out(), "/console readAorB y n " + Console.red + "[game]" + Console.cyan + " Add " + Console.blue + "Prince" + Console.cyan + " card?" + Console.reset);
        ans = Console.acceptMsg(this._owner.in());
        if(ans.equals("y")) {
            this._hasPrince = true;
            Console.sendMsgExceptIndex(this._playerList, 0, Console.red + "[game] " + Console.green + this._owner.name() + Console.cyan + " add " + Console.blue + "Prince" + Console.cyan + " card." + Console.reset);
        } else {
            Console.sendMsgExceptIndex(this._playerList, 0, Console.red + "[game] " + Console.green + this._owner.name() + Console.cyan + " don't add " + Console.blue + "Prince" + Console.cyan + " card." + Console.reset);
        }

        //女公爵の設定
        Console.sendMsg(this._owner.out(), "/console readAorB y n " + Console.red + "[game]" + Console.cyan + " Add " + Console.blue + "Duchess" + Console.cyan + " card?" + Console.reset);
        ans = Console.acceptMsg(this._owner.in());
        if(ans.equals("y")) {
            this._hasDuchess = true;
            Console.sendMsgExceptIndex(this._playerList, 0, Console.red + "[game] " + Console.green + this._owner.name() + Console.cyan + " add " + Console.blue + "Duchess" + Console.cyan + " card." + Console.reset);
        } else {
            Console.sendMsgExceptIndex(this._playerList, 0, Console.red + "[game] " + Console.green + this._owner.name() + Console.cyan + " don't add " + Console.blue + "Duchess" + Console.cyan + " card." + Console.reset);
        }
    }

    //プレイヤーを勝点順にソートするメソッド
    public static void sortPoint(List<Player> plist) {
        Collections.sort(
            plist,
            new Comparator<Player>() {
                @Override
                public int compare(Player p1, Player p2) {
                    if(p2.getPoint() > p1.getPoint()) return 1;
                    else return -1;
                }
            }
        );
    }

    //勝利点に達したプレイヤーがいるか判定するメソッド
    private void judgeFinish() {
        sortPoint(this._playerList);
        if(this._playerList.get(0).getPoint() >= this._winPoint) this._finished = true;
    }

    //プレイヤーの勝点順を返すメソッド
    public static String rank(List<Player> plist) {
        sortPoint(plist);

        StringBuilder buf = new StringBuilder();
        buf.append("*** Players Rank ***\n");
        for(int i = 0; i < plist.size(); i++) {
            buf.append((i + 1) + " : " + Console.green + plist.get(i).name() + Console.reset + ", " + Console.magenta + plist.get(i).getPoint() + Console.reset + "points\n");
        }
        buf.append("********************");

        return buf.toString();
    }

    //プレイヤーの状態をリセットするメソッド
    public void resetPlayer() {
        for(int i = 0; i < this._playerList.size(); i++) {
            Player p = this._playerList.get(i);
            p.revive();
            p.clearProtected();
            p.setHand(null);
            p.resetDiscard();
        }
    }

    //プレイヤーの勝点をリセットするメソッド
    public void resetPoint(){
        for(int i = 0; i < this._playerList.size(); i++) {
            Player p = this._playerList.get(i);
            p.setPoint(0);
        }
    }

    //ゲーム終了時にゲームログファイルを出力する (オプション)
    // private boolean viewLogFile() {
    //     return false;
    // }

    public void wait(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
        }
    }
}
