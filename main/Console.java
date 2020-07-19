package main;

import java.io.*;
import java.util.*;
import game.Player;

public class Console {
    public static final String black   = "\u001b[30m";
    public static final String red     = "\u001b[31m";
    public static final String green   = "\u001b[32m";
    public static final String yellow  = "\u001b[33m";
    public static final String blue    = "\u001b[34m";
    public static final String magenta = "\u001b[35m";
    public static final String cyan    = "\u001b[36m";
    public static final String white   = "\u001b[37m";
    public static final String reset   = "\u001b[0m";

    private static Scanner _in;
    private static BufferedReader _br;
    private static final String errMsgChar = Console.magenta + "Wrong character! Please enter the correct ones." + Console.reset;
    private static final String errMsgNum = Console.magenta + "Wrong number! Please enter the correct ones." + Console.reset;

    public static void initialize() {
        _in = new Scanner(System.in);
        _br = new BufferedReader(new InputStreamReader(System.in));
    }

    public static boolean isScNull() {
        return _in == null;
    }

    public static void write(String message) {
        System.out.print(message);
    }

    public static void writeLn(String message) {
        System.out.println(message);
    }

    public static void writeSplitLn(String[] message, int start, int end) {
        int i = 0;
        for(i = start; i < end; i++) {
            System.out.print(message[i]);
        }
        if(i == end - 1) {
            System.out.print("\n");
        } else {
            System.out.print(" ");
        }
    }

    public static String readSplitLn(String[] message, int start, int end) {
        StringBuilder buf = new StringBuilder();
        int i = 0;
        for(i = start; i < end; i++) {
            buf.append(message[i] + " ");
        }
        return buf.toString();
    }

    public static void newLn() {
        System.out.println();
    }

    private static String osName() {
        return System.getProperty("os.name").toLowerCase();
    }

    public static void clearScreen(long ms) throws IOException {
        try {
            if(ms != 0) {
                Thread.sleep(ms);
            }

            if(osName().equals("linux") || osName().equals("mac")) {
                new ProcessBuilder("bash", "-c", "clear").inheritIO().start().waitFor();
            }
            else if(osName().equals("windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            }
        }
        catch(InterruptedException e){
            e.printStackTrace();
        }
    }

    public static String read() {
        return _in.next();
    }

    public static String readLn() throws IOException {
        return _br.readLine();
    }

    public static String readAorB(String answerA, String answerB, String iniMessage, String inputMessage) {
        String check = null;

        writeLn(iniMessage);
        while(true) {
            write(inputMessage);

            check = read();
            if(check.equals(answerA) || check.equals(answerB)) {
                return check;
            }

            writeLn(errMsgChar);
        }
    }

    public static boolean readTorF(String tAns, String fAns, String iniMessage, String inputMessage) {
        String check = null;

        writeLn(iniMessage);
        while(true) {
            write(inputMessage);

            check = read();
            if(check.equals(tAns)) {
                return true;
            }
            else if(check.equals(fAns)) {
                return false;
            }

            writeLn(errMsgChar);
        }
    }

    public static int readNum(String min, String max, String iniMessage, String inputMessage) {
        int check = 0;
        int minNum = Integer.parseInt(min);
        int maxNum = Integer.parseInt(max);
        writeLn(iniMessage);
        while (true) {
            write(inputMessage);
            try {
                check = Integer.parseInt(read());
                if(check >= minNum && check <= maxNum) {
                    return check;
                }
            }
            catch (NumberFormatException nfe) {
                System.out.println(errMsgNum);
                continue;
            }
            System.out.println(errMsgNum);
        }
    }

    public static String readString(String iniMessage, String inputMessage) {
        String check = null;

        writeLn(iniMessage);
        while(true) {
            write(inputMessage);

            check = read();
            return check;
        }
    }

    public static void dispose() throws IOException {
        if(!isScNull()) {
            _in.close();
        }
    }

    public static String acceptMsg(BufferedReader in) throws IOException {
        return in.readLine();
    }

    public static void sendMsg(PrintWriter out, String msg) {
        out.println(msg);
    }

    public static void sendMsgAll(List<Player> playerList, String msg) {
        for(int i = 0; i < playerList.size(); i++) {
            playerList.get(i).out().println(msg);
        }
    }

    public static void sendMsgExceptIndex(List<Player> playerList, int n, String msg) {
        for(int i = 0; i < playerList.size(); i++) {
            if(i != n) playerList.get(i).out().println(msg);
        }
    }

    public static void sendMsgExceptName(List<Player> playerList, String name, String msg) {
        for(int i = 0; i < playerList.size(); i++) {
            if(!playerList.get(i).name().equals(name)) playerList.get(i).out().println(msg);
        }
    }

    // 名前からプレイヤーのインデックスを検索するメソッド
    public static int searchPlayer(String name, List<Player> plist) {
        for(int i = 0; i < plist.size(); i++) {
            if(plist.get(i).name().equals(name)) return i;
        }
        return -1; //見つからなかった場合
    }

    public static boolean readCommand(BufferedReader in, PrintWriter out, String msg) {
        String[] splitMsg = msg.split(" ");
                    switch(splitMsg[0]) {
                        case "/fin":
                            return false;
                        case "/console":
                            switch(splitMsg[1]) {
                                case "readAorB":
                                    out.println(readAorB(splitMsg[2], splitMsg[3], readSplitLn(splitMsg, 4, splitMsg.length), splitMsg[4] + Console.yellow + " " + "Enter " + splitMsg[2] + " / " + splitMsg[3] + " : " + Console.reset));
                                    break;

                                case "readNum":
                                    out.println(readNum(splitMsg[2], splitMsg[3], readSplitLn(splitMsg, 4, splitMsg.length), splitMsg[4] + Console.yellow + " " + "Input " + splitMsg[2] + " ~ " + splitMsg[3] + " : " + Console.reset));
                                    break;

                                case "readName":
                                    out.println(readString(readSplitLn(splitMsg, 2, splitMsg.length), splitMsg[2] + Console.yellow + " " + "Input a player name : " + Console.reset));
                                    break;

                                case "readString":
                                    out.println(readString(readSplitLn(splitMsg, 2, splitMsg.length), splitMsg[2] + Console.yellow + " " + "Input your select : " + Console.reset));
                                    break;

                                default:
                                    Console.write(Console.magenta + "Unknown message : " + Console.reset);
                                    Console.writeLn(msg);
                                    break;
                            }
                            break;
                        default:
                            Console.write(Console.magenta + "Unknown message : " + Console.reset);
                            Console.writeLn(msg);
                            break;
                    }
        return true;
    }
}