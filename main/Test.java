package main;

import java.io.*;
import java.net.*;
import java.lang.*;
import main.Console;
//import client.Client;
//import server.Server;
import main.Server;
import main.Client;

import java.util.*;
import game.*;
import game.cards.*;

public class Test {
    public static void main(String[] args) throws IOException {
        List<Integer> test = new ArrayList<Integer>();
        test.add(1);
        //test.add(2);
        for(int i = 0; i < test.size(); i++) {
            System.out.println(test.get(i));
        }
        int n = test.remove(0);
        for(int i = 0; i < test.size(); i++) {
            System.out.println(test.get(i));
        }
        System.out.println(n);

/*
        final int PORT = 8080; // Set the Port Number.

        String playerName;

        String serverName;

        int playerNum;

        Map<String, Player> _playerList = new HashMap<String, Player>();

        ServerSocket s = new ServerSocket(PORT); // Construct the socket with port number.
        //System.out.println("Started: " + s);
        InetAddress ia = InetAddress.getLocalHost();
        String ip = ia.getHostAddress();       //IPアドレス
        String hostname = ia.getHostName();    //ホスト名

        playerNum = Integer.parseInt(args[0]);

        Socket[] socket = new Socket[playerNum];
        BufferedReader[] in = new BufferedReader[playerNum];
        PrintWriter[] out = new PrintWriter[playerNum];

        socket[0] = s.accept(); // Wait the connection setting requirements.
        in[0] = new BufferedReader(new InputStreamReader(socket[0].getInputStream())); // Set the buffer for data serving.
        out[0] = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket[0].getOutputStream())), true); // Set the buffer for sending.
        playerName = in[0].readLine();
        Player owner = new Player(playerName, 0, false, null, in[0], out[0]);
        _playerList.put(playerName, owner);

        System.out.println("[game] Started up " + playerName + "'s' server");
        System.out.println("[game] Your IP Address：" + ip);

        System.out.println("See you...");
        socket[0].close();
        s.close();
*/
/*
        try {
            File file = new File("test.txt");
            FileWriter filewriter = new FileWriter(file);

            String t = String.join(",", args);

            filewriter.write(t);

            filewriter.close();
        }catch(IOException e) {
            System.out.println(e);
        }
*/
/*
        try {
            Runtime rt = Runtime.getRuntime();
            //rt.exec("open -n -a Terminal ~/Desktop");
            //rt.exec("open -n -a Terminal");
            rt.exec(args);
        } catch (IOException ex) {
            System.out.println("debug: error");
            ex.printStackTrace();
        }

        System.out.println("Hello world!");
*/

    }
}