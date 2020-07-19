package main;

import java.io.*;
import java.net.*;
import java.util.*;
import game.*;
import game.cards.*;
import main.Console;

public class Server {
    public static final int PORT = 8080; // Set the Port Number.

    public static void main(String[] args) throws IOException {
            File file = new File("test.txt");  //for debug
            FileWriter filewriter = new FileWriter(file); //for debug


        String playerName;
        String serverName;
        int playerNum;
        List<Player> playerList = new ArrayList<Player>();

        ServerSocket s = new ServerSocket(PORT); // Construct the socket with port number.
        //System.out.println("Started: " + s);
        InetAddress ia = InetAddress.getLocalHost();
        String ip = ia.getHostAddress();       //IPアドレス
        String hostname = ia.getHostName();    //ホスト名
        String ans = null;

        /*** ゲーム人数の設定 ***/
        playerNum = Integer.parseInt(args[0]);

        Socket[] socket = new Socket[playerNum];
        BufferedReader in;
        PrintWriter out;

        /*** サーバープレイヤーの設定 ***/
        socket[0] = s.accept(); // Wait the connection setting requirements.
        in = new BufferedReader(new InputStreamReader(socket[0].getInputStream())); // Set the buffer for data serving.
        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket[0].getOutputStream())), true); // Set the buffer for sending.
        playerName = Console.acceptMsg(in);

        Player owner = new Player(playerName, 0, false, null, in, out);
        playerList.add(owner);
        Console.sendMsg(owner.out(), Console.red + "[server]" + Console.cyan + " Started up " + Console.green + playerName + Console.cyan + "'s server" + Console.reset);
        Console.sendMsg(owner.out(), Console.red + "[server]" + Console.cyan + " Your IP Address：" + ip + Console.reset);
        Console.sendMsg(owner.out(), "Now Loading...");

        /*** クライアントプレイヤーの設定 ***/
        for(int i = 1; i < playerNum; i++) {
            socket[i] = s.accept(); // Wait the connection setting requirements.
            in = new BufferedReader(new InputStreamReader(socket[i].getInputStream())); // Set the buffer for data serving.
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket[i].getOutputStream())), true); // Set the buffer for sending.
            playerName = in.readLine();

            Console.sendMsg(out, Console.red + "[server] " + Console.cyan + "Connected to " + Console.green + owner.name() + Console.cyan + "'s server" + Console.reset);
            Console.sendMsg(out, Console.red + "[server] " + Console.cyan + "Now " + Console.green + owner.name() + Console.cyan + " is judging whether to accept you." + Console.reset);
            Console.sendMsg(owner.out(), "/console readAorB y n " + Console.red + "[server] " + Console.green + playerName + Console.cyan + " connected, accept?" + Console.reset);
            ans = Console.acceptMsg(owner.in());
            if(ans.equals("y")) {
                Console.sendMsgAll(playerList, Console.red + "[server] " + Console.green + playerName + Console.cyan + " joined." + Console.reset);
                playerList.add(new Player(playerName, 0, false, null, in, out));
                Console.sendMsg(playerList.get(i).out(), Console.red + "[server]" + Console.cyan + " Joined to " + Console.green + owner.name() + Console.cyan + "'s server" + Console.reset);
            } else if(ans.equals("n")) {
                Console.sendMsg(out, Console.red + "[server]" + Console.magenta + " You have been rejected." + Console.reset);
                Console.sendMsg(out, "/fin");
                i--;
            } else {
                Console.sendMsg(out, Console.magenta + "error : " + ans + Console.reset); //for debug
            }
        }

        /*** ゲームの開始 ***/
        Console.sendMsgAll(playerList, Console.red + "[server]" + Console.cyan + " Participation reception is over. Let's start game !" + Console.reset);

        while(true) {
            Game game = new Game(playerList);
            game.startGame();

            Console.sendMsgExceptName(playerList, owner.name(), Console.red + "[server] " + Console.cyan + "Now, " + Console.green + owner.name() + Console.cyan + " deciding whether to play one more game." + Console.reset);
            Console.sendMsg(owner.out(), "/console readAorB y n " + Console.red + "[server] "+ Console.cyan + "Play one more game?" + Console.reset);
            ans = Console.acceptMsg(owner.in());
            if(ans.equals("n")) {
                Console.sendMsgExceptName(playerList, owner.name(), Console.red + "[server] " + Console.green + owner.name() + Console.cyan + " decided to finish the server." + Console.reset);
                break;
            }
            Console.sendMsgAll(playerList, Console.red + "[server] " + Console.cyan + "Let's play one more game !" + Console.reset);
        }

        /*** 終了通知 ***/
        Console.sendMsgAll(playerList, "/fin");
        for(Socket sc : socket) sc.close();
        s.close();
    }
}