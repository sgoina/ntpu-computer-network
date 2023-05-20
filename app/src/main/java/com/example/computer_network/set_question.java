package com.example.computer_network;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;

public class set_question extends AppCompatActivity {

    CheckBox chka, chkb, chkc;
    EditText et_question, et_ans_a, et_ans_b, et_ans_c;
    globalvariable gv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_question);
        gv = (globalvariable)getApplicationContext();
        chka = (CheckBox) findViewById(R.id.checkBox);
        chkb = (CheckBox) findViewById(R.id.checkBox2);
        chkc = (CheckBox) findViewById(R.id.checkBox3);
        et_question = (EditText) findViewById(R.id.et_set_question);
        et_ans_a = (EditText) findViewById(R.id.et_ans_a);
        et_ans_b = (EditText) findViewById(R.id.et_ans_b);
        et_ans_c = (EditText) findViewById(R.id.et_ans_c);
    }

    private Runnable server_send = new Runnable(){

        @Override
        public void run() {
            try {
                gv.broadcast("ready_question", 0);
                gv.broadcast(et_question.getText().toString(), 0);
                gv.broadcast(et_ans_a.getText().toString(), 0);
                gv.broadcast(et_ans_b.getText().toString(), 0);
                gv.broadcast(et_ans_c.getText().toString(), 0);
                if (chka.isChecked())
                    gv.broadcast("A", 1);
                else if (chkb.isChecked())
                    gv.broadcast("B", 1);
                else if (chkc.isChecked())
                    gv.broadcast("C", 1);
                gv.broadcast("A", 0);
                Bundle bundle = new Bundle();
                Intent intent = new Intent();
                bundle.putString("content", "wait_ans");
                intent.putExtras(bundle);
                intent.setClass(set_question.this, wait.class);
                startActivity(intent);
            } catch (Exception e) {

            }
        }
    };

    private Runnable client_send = new Runnable(){

        @Override
        public void run() {
            try {
                gv.send_to_server("ready_question");
                gv.send_to_server(et_question.getText().toString());
                gv.send_to_server(et_ans_a.getText().toString());
                gv.send_to_server(et_ans_b.getText().toString());
                gv.send_to_server(et_ans_c.getText().toString());
                if (chka.isChecked())
                    gv.send_to_server("A");
                else if (chkb.isChecked())
                    gv.send_to_server("B");
                else if (chkc.isChecked())
                    gv.send_to_server("C");
                Bundle bundle = new Bundle();
                Intent intent = new Intent();
                bundle.putString("content", "wait_ans");
                intent.putExtras(bundle);
                intent.setClass(set_question.this, wait.class);
                startActivity(intent);
            } catch (Exception e) {

            }
        }
    };
    public void Send_question_onclick(View view) {
        if (gv.mode.equals("server")){
            Thread thread=new Thread(server_send);
            thread.start();
        }
        else{
            Thread thread=new Thread(client_send);
            thread.start();
        }
    }
}