package com.example.computer_network;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.io.*;
import java.net.*;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void create_room_onclick(View view) {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, second_server.class);
        startActivity(intent);
    }

    public void join_room_onclick(View view) {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, second_clinet.class);
        startActivity(intent);
    }
}