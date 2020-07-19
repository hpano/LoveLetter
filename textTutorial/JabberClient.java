package textTutorial;

import java.io.*;
import java.net.*;
import game.*;
import textTutorial.JabberServer;

public class JabberClient {
    public static String playerName;

    public JabberClient(String name) {
        this.playerName = name;
    }

    public void client(String args) throws IOException {
        String hostName = "localhost";
        if (args == "localhost") {
            System.err.println("Usage: JabberClient MachineName");
            System.err.println("Connect as localhost...");
        } else {
            hostName = args;
        }

        InetAddress addr = InetAddress.getByName(hostName); // Converting to the IP Address.
        //System.out.println("addr = " + addr);
        Socket socket = new Socket(addr, JabberServer.PORT); // Making the socket.

        try {
            System.out.println("socket = " + socket);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // Set the buffer for data serving.
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),
                    true); // Set the buffer for sending.

            out.println(this.playerName);
            String servName = in.readLine();
            System.out.println("Connected to " + servName + " room.");

            int[] ts2 = in.readLine();
            System.out.println(ts2[0]);

            String msg;
            boolean endFlag = true;
            while(endFlag) {
                msg = in.readLine();
                switch(msg) {
                    case "fin":
                        endFlag = false;
                        break;
                    default:
                        break;
                }
            }


            out.println("END");
        } finally {
            System.out.println("See you...");
            socket.close();
        }
    }
}