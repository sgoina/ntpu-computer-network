package com.example.computer_network;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.*;

public class second_clinet extends AppCompatActivity {

    private globalvariable gv;
    String ip;
    TextView tv;
    EditText et_name;



    private Runnable Connection = new Runnable(){
        @Override
        public void run() {

            try{
                gv.socket = new Socket(ip, 5050);
                gv.br = new BufferedReader(new InputStreamReader(gv.socket.getInputStream()));
                gv.bw = new BufferedWriter(new OutputStreamWriter(gv.socket.getOutputStream()));
                gv.bw.write(et_name.getText().toString());
                gv.bw.newLine();
                gv.bw.flush();
                String msg = gv.br.readLine().toString();
                Bundle bundle = new Bundle();
                bundle.putString("content", msg);
                Intent intent = new Intent();
                intent.putExtras(bundle);
                intent.setClass(second_clinet.this, wait.class);
                startActivity(intent);
            }catch(Exception e){

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_clinet);
        gv = (globalvariable)getApplicationContext();
    }

    public void join_room_onclick(View view) {
        gv.mode = "client";
        EditText addr1 = (EditText) findViewById(R.id.et_address1);
        EditText addr2 = (EditText) findViewById(R.id.et_address2);
        EditText addr3 = (EditText) findViewById(R.id.et_address3);
        EditText addr4 = (EditText) findViewById(R.id.et_address4);
        et_name = (EditText) findViewById(R.id.et_input_name_client);
        tv = (TextView)findViewById(R.id.tv_input_room_address);

        ip = addr1.getText().toString() + "." + addr2.getText().toString() + "." + addr3.getText().toString() + "." + addr4.getText().toString();
        Thread thread=new Thread(Connection);
        thread.start();
    }
}