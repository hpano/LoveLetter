package game;

import java.io.*;
import java.util.*;
import game.cards.Card;

public class Player {
    private String _name;

    private int _points; //現在の勝点

    private boolean _isAlive; //生き残っているか、否か

    private boolean _isProtected; //僧侶の効果で守られているか、否か

    private Card _hand; //手札

    private List<Card> _discard; // 捨て札

    private BufferedReader _in;

    private PrintWriter _out;

    public Player(String name, int points, boolean protection, Card hand, BufferedReader in, PrintWriter out) {
        this._name = name;
        this._points = points;
        this._isAlive = true;
        this._isProtected = protection;
        this._hand = hand;
        this._discard = new ArrayList<Card>();
        this._in = in;
        this._out = out;
    }

    //プレイヤーの名前を返すメソッド
    public String name() {
        return this._name;
    }

    //プレイヤーのポイントを返すメソッド
    public int getPoint() {
        return this._points;
    }

    //プレイヤーのポイントをセットするメソッド
    public void setPoint(int n) {
        this._points = n;
    }

    //プレイヤーが生き残っているかを返すメソッド
    public boolean isAlive() {
        return this._isAlive;
    }

    //プレイヤーを生きかえらせるメソッド
    public void revive() {
        this._isAlive = true;
    }

    //プレイヤーを殺すメソッド
    public void dead() {
        this._isAlive = false;
    }

    //勝点をwinPoint点だけ増やすメソッド
    public void incrementPoints(int winPoint) {
      this._points += winPoint;
    }

    //僧侶の効果で守られているか否かを返すメソッド
    public boolean isProtected() {
        return this._isProtected;
    }

    //僧侶の効果をセットするメソッド
    public void setProtected() {
        this._isProtected = true;
    }

    //僧侶の効果を解除するメソッド
    public void clearProtected() {
        this._isProtected = false;
    }

    //手札を返すメソッド
    public Card getHand() {
        return _hand;
    }

    //手札をセットするメソッド
    public void setHand(Card card) {
        this._hand = card;
    }

    //手札のカードインスタンスをcardに変更し、元持っていたカードを戻り値として返すメソッド
    public Card exchangeHand(Card card) {
        Card discard = this._hand;
        setHand(card);
        return discard;
    }

    //プレイヤーの捨て札を返すメソッド
    public List<Card> getDiscard() {
        return this._discard;
    }

    //プレイヤーの捨て札をリセットするメソッド
    public void resetDiscard() {
        this._discard.clear();
    }

    //プレイヤーの捨て札を追加するメソッド
    public void addDiscard(Card card) {
        this._discard.add(card);
    }

    public BufferedReader in() {
        return this._in;
    }

    public PrintWriter out() {
        return this._out;
    }
}