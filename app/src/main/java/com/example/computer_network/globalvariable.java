package com.example.computer_network;

import android.app.Application;
import android.graphics.Bitmap;

import java.io.*;
import java.net.*;
import java.util.*;

public class globalvariable extends Application {
    public ServerSocket serverSocket;
    public ArrayList<player> players = new ArrayList<>();
    public Socket socket;
    DataOutputStream output;
    DataInputStream input;
    public String firstplayername, mode;
    public int num_people, num_round;
    public int firstplayerscore = 0;
    public int now_turn = 1;
    public int now_round = 1;
    public byte[] imagebuffer;
    class player {
        Socket playerSocket;
        DataOutputStream output;
        DataInputStream input;
        String playername;
        int playernumber;
        int score;


        public player(Socket socket, int number) {
            try {
                this.playerSocket = socket;
                this.playernumber = number;
                this.output = new DataOutputStream(playerSocket.getOutputStream());
                this.input = new DataInputStream(playerSocket.getInputStream());

                this.playername = input.readUTF();
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
                    player.output.writeUTF(msg);
                    player.output.flush();
                }
            } catch (IOException e) {

            }
        }
    }
    public void send_to_server(String msg) {
        try {
            output.writeUTF(msg);
            output.flush();
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
                playeranswer = nowplayer.input.readUTF();
                if (playeranswer.equals("correct"))
                    nowplayer.score++;
                nowplayer.output.writeUTF("wait_ans");
                nowplayer.output.flush();
            } catch (IOException e) {
                System.out.println(nowplayer.playername + " close");
            }
            Thread.currentThread().interrupt();
        }
    }

    ArrayList<server_waitanswer> waits = new ArrayList<>();
    ArrayList<Thread> threads = new ArrayList<>();
    public void threadinit(){
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