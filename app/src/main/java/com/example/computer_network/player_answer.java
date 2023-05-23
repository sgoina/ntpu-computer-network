package com.example.computer_network;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.*;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.FileNotFoundException;
import java.io.IOException;


public class player_answer extends AppCompatActivity {
    TextView tv_question;
    Button bt_a, bt_b, bt_c;
    String true_ans;

    ImageView imageView;
    private ContentResolver resolver;

    Uri uri;
    globalvariable gv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_answer);
        getSupportActionBar().hide();
        Bundle bundle = getIntent().getExtras();
        gv = (globalvariable)getApplicationContext();
        tv_question = (TextView) findViewById(R.id.tv_set_question);
        imageView = (ImageView)findViewById(R.id.imageView2);
        bt_a = (Button) findViewById(R.id.bt_ans_a);
        bt_b = (Button) findViewById(R.id.bt_ans_b);
        bt_c = (Button) findViewById(R.id.bt_ans_c);
        tv_question.setText(bundle.getString("question"));
        bt_a.setText(bundle.getString("ans_a"));
        bt_b.setText(bundle.getString("ans_b"));
        bt_c.setText(bundle.getString("ans_c"));
        true_ans = bundle.getString("true_ans");
        if(bundle.getString("have_image").equals("have_image")){
            Bitmap bitmap = BitmapFactory.decodeByteArray(gv.imagebuffer, 0, gv.imagebuffer .length);
            imageView.setImageBitmap(bitmap);
        }
    }

    public class send_client implements Runnable{

        String user_ans;
        send_client(String ans){
            this.user_ans = ans;
        }
        @Override

        public void run(){
            try {
                if (user_ans.equals(true_ans)){
                    gv.output.writeUTF("correct");
                    gv.output.flush();
                }
                else{
                    gv.output.writeUTF("wrong");
                    gv.output.flush();
                }
                Bundle bundle = new Bundle();
                Intent intent = new Intent();
                String msg = gv.input.readUTF();
                bundle.putString("content", msg);
                intent.putExtras(bundle);
                intent.setClass(player_answer.this, wait.class);
                startActivity(intent);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    };

    public class send_server implements Runnable{

        String user_ans;
        send_server(String ans){
            this.user_ans = ans;
        }
        @Override

        public void run(){
            if (user_ans.equals(true_ans))
                gv.your_score++;
            Bundle bundle = new Bundle();
            Intent intent = new Intent();
            bundle.putString("content", "wait_ans");
            intent.putExtras(bundle);
            intent.setClass(player_answer.this, wait.class);
            startActivity(intent);
        }
    };
    public void ans_a_onclick(View view) {
        bt_a.setEnabled(false);
        bt_b.setEnabled(false);
        bt_c.setEnabled(false);
        if (gv.mode.equals("client")){
            Thread thread = new Thread(new send_client("A"));
            thread.start();
        }
        else{
            Thread thread = new Thread(new send_server("A"));
            thread.start();
        }
    }
    public void ans_b_onclick(View view) {
        bt_a.setEnabled(false);
        bt_b.setEnabled(false);
        bt_c.setEnabled(false);
        if (gv.mode.equals("client")){
            Thread thread = new Thread(new send_client("B"));
            thread.start();
        }
        else{
            Thread thread = new Thread(new send_server("B"));
            thread.start();
        }
    }
    public void ans_c_onclick(View view) {
        bt_a.setEnabled(false);
        bt_b.setEnabled(false);
        bt_c.setEnabled(false);
        if (gv.mode.equals("client")){
            Thread thread = new Thread(new send_client("C"));
            thread.start();
        }
        else{
            Thread thread = new Thread(new send_server("C"));
            thread.start();
        }
    }
}