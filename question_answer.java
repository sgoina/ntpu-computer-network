import java.io.*;
import java.net.*;
import java.util.*;

public class question_answer {
    // 1.登入畫面
    // 2.等待 玩家or出題 畫面
    // 3.出題畫面
    // 4.回答畫面
    // 5.結果畫面
    private static ServerSocket serverSocket;
    private static ArrayList<player> players = new ArrayList<>();
    private static Socket socket;
    private static BufferedWriter bw;
    private static BufferedReader br;
    private static String firstplayername;
    private static int num_people, num_round;
    private static int firstplayerscore;

    static class player {
        Socket playerSocket;
        BufferedReader br;
        BufferedWriter bw;
        String playername;
        int playernumber;
        int score;

        public player(Socket socket, int number) {
            try {
                this.playerSocket = socket;
                this.playernumber = number;
                this.br = new BufferedReader(new InputStreamReader(playerSocket.getInputStream()));
                this.bw = new BufferedWriter(new OutputStreamWriter(playerSocket.getOutputStream()));
                this.playername = br.readLine();
                this.score = 0;
            } catch (IOException e) {

            }
        }
    }

    public static void main(String[] args) throws IOException {
        System.out.println("which mode?");
        Scanner sc = new Scanner(System.in);
        int serverorclient = sc.nextInt();
        if (serverorclient == 1)
            createroom();
        else
            joinroom();
    }

    public static void createroom() throws IOException {
        showipaddress();
        serverSocket = new ServerSocket(5050);
        System.out.println("what's your name?");
        Scanner sc = new Scanner(System.in, "big5");
        firstplayername = sc.nextLine();
        System.out.println("how many people?");
        num_people = sc.nextInt();
        System.out.println("how many rounds");
        num_round = sc.nextInt();
        startServer();
    }

    public static void startServer() {
        try {
            broadcast("wait for players(" + (players.size() + 1) + "/" + num_people + ")...", 0);
            while (players.size() != num_people - 1) {
                Socket socket = serverSocket.accept();
                player tmp = new player(socket, players.size() + 2);
                players.add(tmp);
                broadcast("player " + tmp.playernumber + " " + tmp.playername + " has connected!", tmp.playernumber);
                broadcast("wait for players(" + (players.size() + 1) + "/" + num_people + ")...", 0);
            }
            broadcast("Start Game!", 0);

            ArrayList<waitanswer> waits = new ArrayList<>();
            waits.add(new waitanswer(null, ""));
            for (player nowplayer : players) {
                waits.add(new waitanswer(nowplayer, ""));
            }
            ArrayList<Thread> threads = new ArrayList<>();
            for (waitanswer w : waits) {
                threads.add(new Thread(w));
            }
            for (int round = 1; round <= num_round; round++) {
                for (int turn = 1; turn <= players.get(players.size() - 1).playernumber; turn++) {
                    if (turn != 1)
                        System.out.println("Wait for question...");
                    for (player nowplayer : players) {
                        if (turn != nowplayer.playernumber) {
                            nowplayer.bw.write("Wait for question...");
                            nowplayer.bw.newLine();
                            nowplayer.bw.flush();
                        }
                    }
                    String msg = "";
                    if (turn == 1) {
                        System.out.println("your turn");
                        Scanner sc = new Scanner(System.in, "big5");
                        for (int i = 0; i < 4; i++) {
                            if (i != 0)
                                msg = msg + "\n" + sc.nextLine();
                            else
                                msg = sc.nextLine();
                        }
                    } else {
                        players.get(turn - 2).bw.write("your turn");
                        players.get(turn - 2).bw.newLine();
                        players.get(turn - 2).bw.flush();
                        for (int i = 0; i < 4; i++) {
                            if (i != 0)
                                msg = msg + "\n" + players.get(turn - 2).br.readLine();
                            else
                                msg = players.get(turn - 2).br.readLine();
                        }
                    }
                    String trueans;
                    if (turn != 1)
                        trueans = players.get(turn - 2).br.readLine();
                    else {
                        Scanner sc = new Scanner(System.in);
                        while (true) {
                            trueans = sc.nextLine();
                            if (trueans.equals("A") || trueans.equals("B") || trueans.equals("C"))
                                break;
                            else
                                System.out.println("Error!");
                        }
                    }
                    broadcast(msg, turn);
                    if (turn != 1) {
                        System.out.println("answer");
                        waits.get(0).trueans = trueans;
                        threads.set(0, new Thread(waits.get(0)));
                        threads.get(0).start();
                    }
                    for (player nowplayer : players) {
                        if (turn == nowplayer.playernumber)
                            continue;
                        nowplayer.bw.write("answer");
                        nowplayer.bw.newLine();
                        nowplayer.bw.flush();
                        waits.get(nowplayer.playernumber - 1).trueans = trueans;
                        threads.set(nowplayer.playernumber - 1, new Thread(waits.get(nowplayer.playernumber - 1)));
                        threads.get(nowplayer.playernumber - 1).start();
                    }
                    for (int i = 0; i < threads.size(); i++) {
                        if (turn == i + 1)
                            continue;
                        threads.get(i).join();
                    }
                    broadcast(firstplayername + " : " + firstplayerscore, 0);
                    for (player nowplayer : players) {
                        broadcast(nowplayer.playername + " : " + nowplayer.score, 0);
                    }
                }
            }
        } catch (

        IOException e) {

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void showipaddress() throws UnknownHostException {
        InetAddress ip = InetAddress.getLocalHost();
        System.out.println("Your ip address is " + ip.getHostAddress());
    }

    public static void broadcast(String msg, int number) {
        if (number != 1)
            System.out.println(msg);
        for (player player : players) {
            try {
                if (player.playernumber != number) {
                    player.bw.write(msg);
                    player.bw.newLine();
                    player.bw.flush();
                }
            } catch (IOException e) {

            }
        }
    }

    public static class waitanswer implements Runnable {
        private player nowplayer;
        String playeranswer, trueans;

        public waitanswer(player nowplayer, String ans) {
            this.nowplayer = nowplayer;
            trueans = ans;
        }

        @Override

        public void run() {
            if (nowplayer == null) {
                Scanner sc = new Scanner(System.in);
                while (true) {
                    playeranswer = sc.nextLine();
                    if (playeranswer.equals("A") || playeranswer.equals("B") || playeranswer.equals("C")) {
                        break;
                    } else
                        System.out.println("Error!");
                }
                if (playeranswer.equals(trueans)) {
                    firstplayerscore++;
                    System.out.println("Correct!");
                } else
                    System.out.println("Failed");
            } else {
                try {
                    playeranswer = nowplayer.br.readLine();
                    if (playeranswer.equals(trueans)) {
                        nowplayer.score++;
                        nowplayer.bw.write("Correct!");
                    } else
                        nowplayer.bw.write("Failed!");
                    nowplayer.bw.newLine();
                    nowplayer.bw.flush();
                } catch (IOException e) {
                    System.out.println(nowplayer.playername + " close");
                }
            }
            Thread.currentThread().interrupt();
        }
    }

    public static void joinroom() {
        try {
            socket = new Socket("127.0.0.1", 5050);
            System.out.println("connected");
            bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("enter a name");
            Scanner sc = new Scanner(System.in, "big5");
            String name;
            name = sc.nextLine();
            bw.write(name);
            bw.newLine();
            bw.flush();
            receive();
        } catch (IOException e) {
            System.out.println("GG");
            shutdown();
        }
    }

    public static void receive() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msg;
                while (socket.isConnected()) {
                    try {
                        msg = br.readLine();
                        System.out.println(msg);
                        if (msg.equals("your turn")) {
                            System.out.println("問題時間");
                            sendquestion();
                        } else if (msg.equals("answer")) {
                            System.out.println("回答時間");
                            sendanswer();
                        }
                    } catch (IOException e) {
                        shutdown();
                        break;
                    }

                }
            }
        }).start();
    }

    public static void sendquestion() {
        try {
            Scanner sc = new Scanner(System.in, "big5");
            for (int i = 0; i < 4; i++) {
                String msg = sc.nextLine();
                if (i == 1)
                    bw.write("A. " + msg);
                else if (i == 2)
                    bw.write("B. " + msg);
                else if (i == 3)
                    bw.write("C. " + msg);
                else
                    bw.write(msg);
                bw.newLine();
            }
            bw.flush();
            while (true) {
                String ans = sc.nextLine();
                if (ans.equals("A") || ans.equals("B") || ans.equals("C")) {
                    bw.write(ans);
                    bw.newLine();
                    bw.flush();
                    break;
                } else
                    System.out.println("Error!");
            }
        } catch (IOException e) {
            shutdown();
        }
    }

    public static void sendanswer() {
        try {
            Scanner sc = new Scanner(System.in, "big5");
            while (true) {
                String ans = sc.nextLine();
                if (ans.equals("A") || ans.equals("B") || ans.equals("C")) {
                    bw.write(ans);
                    bw.newLine();
                    bw.flush();
                    break;
                } else
                    System.out.println("Error!");
            }
        } catch (IOException e) {
            shutdown();
        }
    }

    public static void shutdown() {
        try {
            if (br != null)
                br.close();
            if (bw != null)
                bw.close();
            if (socket != null)
                socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}