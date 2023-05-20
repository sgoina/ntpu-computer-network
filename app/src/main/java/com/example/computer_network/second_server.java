package com.example.computer_network;

import androidx.appcompat.app.AppCompatActivity;

import java.io.*;
import java.net.*;

import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import org.w3c.dom.Text;

public class second_server extends AppCompatActivity {

    private globalvariable gv;
    NumberPicker np_person, np_round;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_server);

        np_person = findViewById(R.id.number_people);
        np_round = findViewById(R.id.number_round);
        np_person.setMinValue(2);
        np_person.setMaxValue(99);
        np_round.setMinValue(1);
        np_round.setMaxValue(99);
        gv = (globalvariable)getApplicationContext();
    }

    public void createroom_onclick(View view) {
        gv.mode = "server";
        EditText et = (EditText) findViewById(R.id.et_input_name_server);
        gv.firstplayername = et.getText().toString();
        gv.num_people = np_person.getValue();
        gv.num_round = np_round.getValue();
        try {
            gv.serverSocket = new ServerSocket(5050);
            Intent intent = new Intent();
            intent.setClass(second_server.this, third_server.class);
            startActivity(intent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}