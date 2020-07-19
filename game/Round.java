package game;

import java.io.*;
import java.util.*;
import main.Console;
import game.Game;
import game.cards.*;

public class Round {

    private List<Player> _playerList;   // ゲーム参加者の順番リスト

    private int _alivePlayer; // 生き残っているプレイヤーの数

    private List<Card> _deck;    // 山札

    private boolean _hasDuchess; // 女公爵が山札に含まれているか否か

    private boolean _hasPrince;  // 王子が山札に含まれているか否か

    private boolean _hasKing;    // 王が山札に含まれているか否か

    public Round(List<Player> playerList, List<Card> deck, boolean hasDuchess, boolean hasPrince, boolean hasKing) {
        this._playerList = playerList;
        this._alivePlayer = playerList.size();
        this._deck = new ArrayList<Card>(deck);
        this._hasDuchess = hasDuchess;
        this._hasPrince = hasPrince;
        this._hasKing = hasKing;
    }

    // LoveLetterのゲームの1ラウンドを開始するメソッド.
    public void start(int roundNum) throws IOException {
        shufflePlayer();
        shuffleCard();

        Console.sendMsgAll(this._playerList, Console.red + "[game]" + Console.cyan + " Round " + roundNum + Console.reset);
        wait(3000);
        Console.sendMsgAll(this._playerList, Console.red + "[round]" + Console.cyan + " Player order : " + Console.green + alivePlayer() + Console.reset);

/*** ↓ for debug ↓ **/
//        StringBuilder buf = new StringBuilder();
//        buf.setLength(0);
//        for(int i = 0; i < this._deck.size(); i++) {
//            buf.append(this._deck.get(i).name());
//            if(i != (this._deck.size() - 1)) buf.append(Console.cyan + ", " + Console.blue);
//        }
//        String clist = buf.toString();
//
//        Console.sendMsgAll(this._playerList, Console.red + "[round]" + Console.cyan + " Cards : " + Console.blue + clist + Console.reset);
/*** ↑ for debug ↑ ***/

        wait(3000);
        setInitialCard();

        boolean endFlag = true;
        while(endFlag) {
            for(int i = 0; i < this._playerList.size(); i++) {
                if(this._playerList.get(i).isAlive()) {
                    wait(4000);
                    turn(i);
                    if(finished()) {
                        winJudge();
                        endFlag = false;
                        break;
                    }
                }
            }
        }
        wait(1000);
        List<Player> plist = new ArrayList<Player>(this._playerList); //プレイヤーリストをコピー //まあなくてもいい
        Console.sendMsgAll(this._playerList, Game.rank(plist));
    }

    // 一人のプレイヤーがカードを引き、その効果を発揮させるメソッド
    private void turn(int n) throws IOException {
        Player turnPlayer = this._playerList.get(n);
        if(turnPlayer.isAlive()) {
            Console.sendMsgAll(this._playerList, Console.red + "[round] " + Console.green + turnPlayer.name() + Console.cyan + "'s turn." + Console.reset);
            turnPlayer.clearProtected();

            Card drawCard = this._deck.remove(0);
            wait(1000);
            Console.sendMsg(turnPlayer.out(), Console.red + "[round]" + Console.cyan + " You drew a card." + Console.reset);
            Card.explainCard(turnPlayer, drawCard);

            wait(500);
            int afterFlag = beforeEffect(turnPlayer, drawCard);

            wait(500);
            if(afterFlag == 0) {
                Card invokeCard = selectCard(turnPlayer, drawCard);
                endTurn(turnPlayer, invokeCard);
            }
        }
    }

    // プレイヤーをシャッフルするメソッド
    private void shufflePlayer() {
        Collections.shuffle(this._playerList);
    }

    // カードをシャッフルするメソッド
    private void shuffleCard() {
        Collections.shuffle(this._deck);
    }

    // カードの名前と強さ一覧を文字列で返すメソッド
    private String allCards(List<Card> cardList) {
        StringBuilder buf = new StringBuilder();
        for(int i = 0; i < cardList.size(); i++) {
            buf.append(Console.cyan + "(" + Console.blue + cardList.get(i).name() + Console.cyan + ", " + Console.reset + cardList.get(i).strength() + Console.cyan + ")" + Console.reset);
            if(i != (cardList.size() - 1)) {
                buf.append(Console.cyan + ", " + Console.reset);
            }
        }
        return buf.toString();
    }

    //各プレイヤーに初期カードを配って通知するメソッド
    private void setInitialCard() {
        for(int i = 0; i < this._playerList.size(); i++) {
            Player player = this._playerList.get(i);
            player.setHand(this._deck.remove(0));
            Console.sendMsg(player.out(), Console.red + "[round]" + Console.cyan + " Your initial card : " + Console.reset);
            Card.explainCard(player, player.getHand());
        }
    }

    // cardをプライヤーの捨て札の一番上に追加するメソッド
    private void throwCard(Player player, Card card) {
        player.addDiscard(card);
        wait(500);
        Console.sendMsgAll(this._playerList, Console.red + "[round] " + Console.green + player.name() + Console.cyan + "'s discard list : " + allCards(player.getDiscard()) + Console.reset);
    }

    // 負けプレイヤーの敗戦処理を行うメソッド
    private void lose(Player loser) {
        wait(2000);
        Console.sendMsgAll(this._playerList, Console.red + "[round] " + Console.green + loser.name() + Console.cyan + " has " + Console.magenta + "dropped out" + Console.cyan + "." + Console.reset);
        Console.sendMsgExceptName(this._playerList, loser.name(), Console.red + "[round] " + Console.green + loser.name() + Console.cyan + "'s hand card is " + Console.blue + loser.getHand().name() + Console.cyan + " ." + Console.reset);
        throwCard(loser, loser.getHand());
        loser.setHand(null);
        loser.clearProtected();
        this._alivePlayer--;
        loser.dead();
        Console.sendMsgAll(this._playerList, Console.red + "[round]" + Console.cyan + " Alive : " + Console.green + alivePlayer() + Console.reset);
    }

    // プレイヤーのターンを終了するメソッド
    private void endTurn(Player turnPlayer, Card invokeCard) throws IOException {
        Console.sendMsgExceptName(this._playerList, turnPlayer.name(), Console.red + "[round] " + Console.green + turnPlayer.name() + Console.cyan + " throws " + Console.blue + invokeCard.name() + Console.cyan + " card." + Console.reset);
        throwCard(turnPlayer, invokeCard);
        int afterFlag;
        afterEffect(turnPlayer, invokeCard);
    }

    // 山札から引いたカードを手札に残すか否かを確認するメソッド
    private Card selectCard(Player turnPlayer, Card drawCard) throws IOException {
        try {
            Console.sendMsg(turnPlayer.out(), "/console readAorB 0 1 " + Console.red + "[round] " + Console.cyan + "Select discard. 0: " + Console.blue + turnPlayer.getHand().name() + Console.cyan + ", 1: " + Console.blue + drawCard.name() + Console.reset);
            String ans = Console.acceptMsg(turnPlayer.in()); /***あとで例外処理***/
            if(ans.equals("0")) {
                return turnPlayer.exchangeHand(drawCard);
            }
            else if(ans.equals("1")) {
                return drawCard;
            }
            else {
                Console.sendMsgAll(this._playerList, Console.magenta + "error" + Console.reset);
                return drawCard;
            }
        } catch(IOException e) {
            return drawCard;
        }
    }

    // 生き残っているプレイヤーの一覧を文字列で返すメソッド
    public String alivePlayer() {
        StringBuilder buf = new StringBuilder();
        for(int i = 0; i < this._playerList.size(); i++) {
            if(this._playerList.get(i).isAlive()) {
                buf.append(Console.green + this._playerList.get(i).name() + Console.reset);
                if(i != (this._playerList.size() - 1)) {
                    buf.append(Console.cyan + ", " + Console.reset);
                }
            }
        }
        return buf.toString();
    }

    //自分以外のプレイヤーを選ぶメソッド
    private Player selectPlayer(Player player, List<Player> plist) throws IOException {
        String ans;
        Player target = null;

        Console.sendMsg(player.out(), Console.red + "[card] " + alivePlayer() + Console.reset);
        Console.sendMsg(player.out(), "/console readName " + Console.red + "[card] " + Console.cyan + "Who do you select?" + Console.reset);
        while(true) {
            ans = Console.acceptMsg(player.in());
            int n = Console.searchPlayer(ans, plist);
            if(n != -1) {
                target = plist.get(n);
                if(!target.name().equals(player.name())) {
                    break;
                }
            }
            Console.sendMsg(player.out(), "/console readName " + Console.red + "[card] " + Console.magenta + "Con't select the player." + Console.reset);
        }
        return target;
    }

    //自分以外の生きているプレイヤーを選ぶメソッド
    private Player selectAlivePlayer(Player player, List<Player> plist) throws IOException {
        String ans;
        Player target = null;

        Console.sendMsg(player.out(), Console.red + "[card] " + alivePlayer() + Console.reset);
        Console.sendMsg(player.out(), "/console readName " + Console.red + "[card] " + Console.cyan + "Who do you select?" + Console.reset);
        while(true) {
            ans = Console.acceptMsg(player.in());
            int n = Console.searchPlayer(ans, plist);
            if(n != -1) {
                target = plist.get(n);
                if(!target.name().equals(player.name())) {
                    if(target.isAlive()) {
                        break;
                    }
                }
            }
            Console.sendMsg(player.out(), "/console readName " + Console.red + "[card] " + Console.magenta + "Con't select the player." + Console.reset);
        }
        return target;
    }

    // ゲームが終了したか否かを判定するメソッド
    private boolean finished() {
        return this._alivePlayer == 1 || this._deck.size() == 0;
    }

    // 勝者を決定し、勝ち点を与えるメソッド
    private void winJudge() {
        Player winPlayer = null;
        int winPoint = 1;

        wait(2000);

        // １人残った場合
        if(this._alivePlayer == 1) {
            for(int i = 0; i < this._playerList.size(); i++) {
                if(this._playerList.get(i).isAlive()) {
                    winPlayer = this._playerList.get(i);
                    break;
                }
            }
        }

        // 山札が尽きた場合
        else {
            int winStrength = -1;
            for(int i = 0; i < this._playerList.size(); i++) {
                if(this._playerList.get(i).isAlive()) {
                    if(this._playerList.get(i).getHand().strength() > winStrength) {
                        winPlayer = this._playerList.get(i);
                        winStrength = winPlayer.getHand().strength();
                    }

                    // 引き分けの場合
                    else if(this._playerList.get(i).getHand().strength() == winStrength) {
                        Console.sendMsgAll(this._playerList, Console.red + "[round]" + Console.cyan + " Draw game. No one get point." + Console.reset);
                        return;
                    }
                }
            }
        }

        // 勝者のカードが姫だった場合
        if(winPlayer.getHand().name().equals("Princess")) {
            winPoint++;
        }

        Console.sendMsgAll(this._playerList, Console.red + "[round]" + Console.cyan + " Round finish !" + Console.reset);
        wait(2000);
        Console.sendMsgAll(this._playerList, Console.red + "[round]" + Console.cyan + " Winner : " + Console.green + winPlayer.name() + Console.cyan + " !" + Console.reset);
        wait(500);
        Console.sendMsgAll(this._playerList, Console.red + "[round] " + Console.green + winPlayer.name() + Console.cyan + " get " + Console.magenta + winPoint + Console.cyan + "point." + Console.reset);
        winPlayer.incrementPoints(winPoint);
    }

    // 引いた時に発動する効果 (引いたカードと手持ちのカードがある)
    public int beforeEffect(Player turnPlayer, Card card) throws IOException {
        int ans = 0;
        Player target = null;
        switch(card.name()) {
            case "Duchess":
                if(turnPlayer.getHand().strength() >= 5) {
                    endTurn(turnPlayer, card);
                    ans = 1;
                }
                break;

            case "King":
                throwCard(turnPlayer, card);
                lose(turnPlayer);
                ans = 1;
                break;

            case "Minister":
                if(turnPlayer.getHand().strength() >= 5) {
                    throwCard(turnPlayer, card);
                    lose(turnPlayer);
                    ans = 1;
                }
                break;

            default:
                break;
        }
        return ans;
    }

    // 捨てた時に発動する効果 (手持ちのカードだけ)
    public void afterEffect(Player turnPlayer, Card card) throws IOException {
        Player target = null;
        switch(card.name()) {
            case "Clown":
                target = selectAlivePlayer(turnPlayer, this._playerList);
                Console.sendMsgExceptName(this._playerList, turnPlayer.name(), Console.red + "[card] " + Console.green + turnPlayer.name() + Console.cyan + " selected " + Console.green + target.name() + Console.cyan + "." + Console.reset);

                if(!target.isProtected()) {
                    Console.sendMsg(turnPlayer.out(), Console.red + "[card] " + Console.green + target.name() + Console.cyan + " has " + Console.blue + target.getHand().name() + Console.cyan + " card." + Console.reset);
                } else {
                    Console.sendMsgAll(this._playerList, Console.red + "[card] " + Console.green + turnPlayer.name() + Console.cyan + " missed." + Console.reset);
                }
                break;

            case "General":
                target = selectAlivePlayer(turnPlayer, this._playerList);
                Console.sendMsgExceptName(this._playerList, turnPlayer.name(), Console.red + "[card] " + Console.green + turnPlayer.name() + Console.cyan + " selected " + Console.green + target.name() + Console.cyan + "." + Console.reset);

                if(!target.isProtected()) {
                    turnPlayer.setHand(target.exchangeHand(turnPlayer.getHand()));
                    Console.sendMsg(turnPlayer.out(), Console.red + "[card] " + Console.cyan + "Your hand card is " + Console.blue + turnPlayer.getHand().name() + Console.cyan + " card." + Console.reset);
                    Card.explainCard(turnPlayer, turnPlayer.getHand());
                    Console.sendMsg(target.out(), Console.red + "[card] " + Console.cyan + "Your hand card is " + Console.blue + target.getHand().name() + Console.cyan + " card." + Console.reset);
                    Card.explainCard(target, target.getHand());
                } else {
                    Console.sendMsgAll(this._playerList, Console.red + "[card] " + Console.green + turnPlayer.name() + Console.cyan + " missed." + Console.reset);
                }
                break;

            case "Knight":
                target = selectAlivePlayer(turnPlayer, this._playerList);
                Console.sendMsgExceptName(this._playerList, turnPlayer.name(), Console.red + "[card] " + Console.green + turnPlayer.name() + Console.cyan + " selected " + Console.green + target.name() + Console.cyan + "." + Console.reset);

                if(!target.isProtected()) {
                    Console.sendMsg(turnPlayer.out(), Console.red + "[card] " + Console.green + target.name() + Console.cyan + " has " + Console.blue + target.getHand().name() + Console.cyan + " card." + Console.reset);
                    Console.sendMsg(target.out(), Console.red + "[card] " + Console.green + turnPlayer.name() + Console.cyan + " has " + Console.blue + turnPlayer.getHand().name() + Console.cyan + " card." + Console.reset);

                    if(turnPlayer.getHand().strength() > target.getHand().strength()) {
                        lose(target);
                    } else if(target.getHand().strength() > turnPlayer.getHand().strength()) {
                        lose(turnPlayer);
                    }
                } else {
                    Console.sendMsgAll(this._playerList, Console.red + "[card] " + Console.green + turnPlayer.name() + Console.cyan + " missed." + Console.reset);
                }
                break;

            case "Magician":
                target = selectAlivePlayer(turnPlayer, this._playerList);
                Console.sendMsgExceptName(this._playerList, turnPlayer.name(), Console.red + "[card] " + Console.green + turnPlayer.name() + Console.cyan + " selected " + Console.green + target.name() + Console.cyan + "." + Console.reset);

                if(!target.isProtected()) {
                    Console.sendMsgExceptName(this._playerList, target.name(), Console.red + "[round] " + Console.green + target.name() + Console.cyan + " throws " + Console.blue + target.getHand().name() + Console.cyan + " card." + Console.reset);
                    if(target.getHand().name().equals("Princess")) {
                        lose(target);
                    } else {
                        throwCard(target, target.getHand());

                        Card drawCard = this._deck.remove(0);
                        wait(1000);
                        Console.sendMsg(target.out(), Console.red + "[round]" + Console.cyan + " You drew a card." + Console.reset);
                        Card.explainCard(target, drawCard);

                        wait(500);
                        int afterFlag = beforeEffect(target, drawCard);

                        wait(500);
                        if(afterFlag == 0) {
                            target.setHand(drawCard);
                        }
                    }
                } else {
                    Console.sendMsgAll(this._playerList, Console.red + "[card] " + Console.green + turnPlayer.name() + Console.cyan + " missed." + Console.reset);
                }
                break;

            case "Soldier":
                target = selectAlivePlayer(turnPlayer, this._playerList);
                Console.sendMsgExceptName(this._playerList, turnPlayer.name(), Console.red + "[card] " + Console.green + turnPlayer.name() + Console.cyan + " selected " + Console.green + target.name() + Console.cyan + "." + Console.reset);

                String ans;
                Console.sendMsg(turnPlayer.out(), "/console readString " + Console.red + "[card] " + Console.cyan + "What card do you select?" + Console.reset);
                ans = Console.acceptMsg(turnPlayer.in());

                if(!target.isProtected()) {
                    if(ans.equals(target.getHand().name())) {
                        lose(target);
                    } else {
                        Console.sendMsgAll(this._playerList, Console.red + "[card] " + Console.green + turnPlayer.name() + Console.cyan + " missed." + Console.reset);
                    }
                } else {
                    Console.sendMsgAll(this._playerList, Console.red + "[card] " + Console.green + turnPlayer.name() + Console.cyan + " missed." + Console.reset);
                }
                break;

            case "Monk":
                turnPlayer.setProtected();
                break;

            case "Princess":
            case "Prince":
                lose(turnPlayer);
                break;

            default:
                break;
        }
    }

    public void wait(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
        }
    }

}