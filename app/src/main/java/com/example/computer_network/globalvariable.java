package com.example.computer_network;

import android.app.Application;
import java.io.*;
import java.net.*;
import java.util.*;

public class globalvariable extends Application {
    public ServerSocket serverSocket;
    public ArrayList<player> players = new ArrayList<>();
    public Socket socket;
    public BufferedWriter bw;
    public BufferedReader br;
    public String firstplayername, mode;
    public int num_people, num_round;
    public int firstplayerscore = 0;
    public int now_turn = 1;
    public int now_round = 1;

    class player {
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
    public void buildplayer(Socket socket, int number){
        player tmp = new player(socket, number);
        players.add(tmp);
    }
    public void broadcast(String msg, int number) {
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
    public void send_to_server(String msg) {
        try {
            bw.write(msg);
            bw.newLine();
            bw.flush();
        } catch (IOException e) {

        }
    }

    public static class server_waitanswer implements Runnable {
        private player nowplayer;
        public String playeranswer;

        public server_waitanswer(globalvariable.player nowplayer) {
            this.nowplayer = nowplayer;
        }

        @Override

        public void run() {
            try {
                playeranswer = nowplayer.br.readLine();
                if (playeranswer.equals("correct"))
                    nowplayer.score++;
                nowplayer.bw.write("wait_ans");
                nowplayer.bw.newLine();
                nowplayer.bw.flush();
            } catch (IOException e) {
                System.out.println(nowplayer.playername + " close");
            }
            Thread.currentThread().interrupt();
        }
    }

    ArrayList<server_waitanswer> waits = new ArrayList<>();
    ArrayList<Thread> threads = new ArrayList<>();
    public void threadinit(){
//        waits.add(new server_waitanswer(null));
        waits.clear();
        threads.clear();
        for (player nowplayer : players) {
            waits.add(new server_waitanswer(nowplayer));
        }

        for (server_waitanswer w : waits) {
            threads.add(new Thread(w));
        }
    }

}