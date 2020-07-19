package main;

import java.io.*;
import java.net.*;
import java.util.regex.*;
import game.*;
import main.Server;

public class Client {
    public static String playerName;

    public Client(String name) {
        this.playerName = name;
    }

    public void client(String args) throws IOException {
        String hostName = "localhost";
        String ans;

        if (args == "localhost") {
            System.err.println("Usage: JabberClient MachineName");
            System.err.println("Connect as localhost...");
        } else {
            hostName = args;
        }

        InetAddress addr = InetAddress.getByName(hostName); // Converting to the IP Address.
        //System.out.println("addr = " + addr);
        Socket socket = null;
        while(true) {
            Console.writeLn("Now Loading...");
            try {
                socket = new Socket(addr, Server.PORT); // Making the socket.
                break;
            } catch(ConnectException ce) {
                Console.writeLn(Console.red + "[client]" + Console.magenta + " Server not found." + Console.reset);
                ans = Console.readAorB(
                    "y",
                    "n",
                    Console.red + "[client]" + Console.cyan + " Try again?" + Console.reset,
                    Console.red + "[client]" + Console.yellow + " Enter y (try again) / n (exit game) : " + Console.reset
                    );
                if(ans.equals("n")) {
                    return;
                }
            } catch(NoRouteToHostException nrte) {
                Console.write(Console.red + "[client]" + Console.magenta + " IP not found. Input again : " + Console.reset);
                addr = InetAddress.getByName(Console.readLn());
            } catch(SocketException se) {
                Console.writeLn(Console.magenta + "error: se" + Console.reset); //for debug
            }
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // Set the buffer for data serving.
        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true); // Set the buffer for sending.
        out.println(this.playerName);

        String msg;
        String pattern = "^[/]" ;
        Pattern p = Pattern.compile(pattern);
        boolean endFlag = true;
        while(endFlag) {
            try {
                msg = in.readLine();
                if(p.matcher(msg).find()) {
                    endFlag = Console.readCommand(in, out, msg);
                } else {
                    Console.writeLn(msg);
                }
            } catch(NullPointerException npe) {
            }
        }

        socket.close();

    }
}