package textTutorial;

import java.io.*;
import java.net.*;
import java.util.*;
import game.*;
import game.cards.*;

public class JabberServer {
    public static final int PORT = 8080; // Set the Port Number.
    public String playerName;

    public JabberServer(String name) {
        this.playerName = name;
    }

    public void server() throws IOException {
        ServerSocket s = new ServerSocket(PORT); // Construct the socket with port number.
        //System.out.println("Started: " + s);
        InetAddress ia = InetAddress.getLocalHost();
        String ip = ia.getHostAddress();       //IPアドレス
        String hostname = ia.getHostName();    //ホスト名
        //画面表示
        System.out.println("Your IP Address：" + ip);
        System.out.println("Your Host Name：" + hostname);

        try {
            Socket socket = s.accept(); // Wait the connection setting requirements.
            try {
                System.out.println("Connection accepted: " + socket);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // Set the buffer for data serving.
                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),
                        true); // Set the buffer for sending.

                Player serv = new Player(this.playerName, 0, false, null);
                String clieName = in.readLine();
                Player clie = new Player(clieName, 0, false, null);
                System.out.println(clieName + " joined.");
                out.println(this.playerName);

                ArrayList<Player> playerQueue = new ArrayList<Player>();
                playerQueue.add(serv);
                playerQueue.add(clie);

                ArrayList<Card> startCard = new ArrayList<Card>();

                //Round round = new Round(playerQueue, false, false, false);

                //round.start();
                int[] ts = {0, 1};
                out.println(ts);
                out.println("fin");


            } finally {
                System.out.println("See you...");
                socket.close();
            }
        } finally {
            s.close();
        }
    }
}