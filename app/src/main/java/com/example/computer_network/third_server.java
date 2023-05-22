package com.example.computer_network;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.TextView;

import java.io.*;
import java.net.*;

public class third_server extends AppCompatActivity {

    globalvariable gv;
    TextView waitperson;

    public class Connection implements Runnable{

        int count = 1;
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    waitperson.setText("wait for players(" + String.valueOf(count) + "/" + String.valueOf(gv.num_people) + ")...");
                }
            });
            try {
                while (true) {
                    Socket socket = gv.serverSocket.accept();
                    count++;
                    String msg = "wait for players(" + String.valueOf(count) + "/" + String.valueOf(gv.num_people) + ")...";
                    gv.buildplayer(socket, count);
                    if (count == gv.num_people) {
                        break;
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            waitperson.setText(msg);
                        }
                    });
                    gv.broadcast(msg, 0);
                }
                gv.broadcast("wait_question", 0);
                Intent intent = new Intent();
                intent.setClass(third_server.this, set_question.class);
                startActivity(intent);
            } catch (IOException e) {

            } catch (Exception e) {

            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third_server);
        getSupportActionBar().hide();
        gv = (globalvariable)getApplicationContext();
        waitperson = (TextView) findViewById(R.id.tv_waitperson);
        try {
            showipaddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        Thread thread=new Thread(new Connection());
        thread.start();

    }

    public void showipaddress() throws UnknownHostException {
        WifiManager ip = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        TextView tv = (TextView) findViewById(R.id.tv_status);
        tv.setText(Formatter.formatIpAddress(ip.getConnectionInfo().getIpAddress()));
    }
}