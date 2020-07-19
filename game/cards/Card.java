package game.cards;

import java.io.*;
import java.util.*;
import main.Console;
import game.Player;
import game.Round;
import java.lang.ArrayIndexOutOfBoundsException;

public class Card {
    // Num of cards definition
    public static final int soldierNum = 5;
    public static final int clownNum = 2;
    public static final int knightNum = 2;
    public static final int monkNum = 2;
    public static final int magicianNum = 2;
    public static final int generalNum = 1;
    public static final int ministerNum = 1;
    public static final int princessNum = 1;
    public static final int kingNum = 1;

    public static List<Card> generateCardList(boolean hasDuchess, boolean hasPrince, boolean hasKing) {
        List<Card> ret = new ArrayList<Card>();
        try {
            int i = 0;
            for(int j = 0; j < soldierNum; j++) {
                ret.add(new Soldier());
                i++;
            }
            for(int j = 0; j < clownNum; j++) {
                ret.add(new Clown());
                i++;
            }
            for(int j = 0; j < knightNum; j++) {
                ret.add(new Knight());
                i++;
            }
            for(int j = 0; j < monkNum; j++) {
                ret.add(new Monk());
                i++;
            }
            for(int j = 0; j < magicianNum; j++) {
                ret.add(new Magician());
                i++;
            }
            for(int j = 0; j < generalNum; j++) {
                ret.add(new General());
                i++;
            }
            for(int j = 0; j < ministerNum; j++) {
                ret.add(new Minister());
                i++;
            }
            for(int j = 0; j < princessNum; j++) {
                ret.add(new Princess());
                i++;
            }
            if(hasKing) {
                for(int j = 0; j < kingNum; j++) {
                    ret.add(new King());
                    i++;
                }
            }
            if(hasPrince) {
                for(int j = 0; j < 1; j++) {
                    ret.add(new Prince());
                    i++;
                }
            }
            if(hasDuchess) {
                for(int j = 0; j < 1; j++) {
                    ret.add(new Duchess());
                    i++;
                }
            }
        }
        catch(ArrayIndexOutOfBoundsException aioobe) {
            Console.writeLn("Generating error in Cardlist.");
            Console.writeLn("(Perhaps Num of cards definition wrong?)");
        }

        return ret;
    }

    private String _name; //カードの名前

    private int _strength; //カードの強さ

    private String _effectText; //カード効果の説明文

    public Card(String name, int strength, String effectText){
        this._name = name;
        this._strength = strength;
        this._effectText = effectText;
    }

    //カードの名前を返すメソッド
    public String name() {
        return this._name;
    }

    //カードの強さを返すメソッド
    public int strength(){
        return this._strength;
    }

    //カード効果の説明文を返すメソッド
    public String effectText(){
        return this._effectText;
    }

    // プレイヤーに引いたカードの説明をするメソッド
    public static void explainCard(Player player, Card card) {
        Console.sendMsg(player.out(), "Name    : " + Console.blue + card.name() + Console.reset);
        Console.sendMsg(player.out(), "Strength: " + card.strength());
        Console.sendMsg(player.out(), "Effect  : " + card.effectText());
    }

    public int drawMethod(Player player, List<Player> plist) throws IOException {
        return -1;
    }

    public int throwMethod(Player player, List<Player> plist) throws IOException {
        return -1;
    }
}