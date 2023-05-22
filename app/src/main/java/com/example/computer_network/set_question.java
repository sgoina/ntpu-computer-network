package com.example.computer_network;

import androidx.appcompat.app.AppCompatActivity;

import android.content.*;
import android.graphics.*;
import android.net.*;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class set_question extends AppCompatActivity {

    CheckBox chka, chkb, chkc;
    EditText et_question, et_ans_a, et_ans_b, et_ans_c;
    globalvariable gv;
    private ContentResolver resolver;
    private ImageView imageView;
    boolean have_image = false;
    Bitmap bitmap;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_question);
        getSupportActionBar().hide();
        gv = (globalvariable)getApplicationContext();
        imageView = (ImageView) findViewById(R.id.imageView);
        resolver = this.getContentResolver();
        chka = (CheckBox) findViewById(R.id.checkBox);
        chkb = (CheckBox) findViewById(R.id.checkBox2);
        chkc = (CheckBox) findViewById(R.id.checkBox3);
        et_question = (EditText) findViewById(R.id.et_set_question);
        et_ans_a = (EditText) findViewById(R.id.et_ans_a);
        et_ans_b = (EditText) findViewById(R.id.et_ans_b);
        et_ans_c = (EditText) findViewById(R.id.et_ans_c);
    }

    public void ans_a_onclick(View view) {
        if(chka.isChecked()){
            chkb.setEnabled(false);
            chkb.setVisibility(View.INVISIBLE);
            chkc.setEnabled(false);
            chkc.setVisibility(View.INVISIBLE);
        }
        else{
            chkb.setEnabled(true);
            chkb.setVisibility(View.VISIBLE);
            chkc.setEnabled(true);
            chkc.setVisibility(View.VISIBLE);
        }
    }
    public void ans_b_onclick(View view) {
        if(chkb.isChecked()){
            chka.setEnabled(false);
            chka.setVisibility(View.INVISIBLE);
            chkc.setEnabled(false);
            chkc.setVisibility(View.INVISIBLE);
        }
        else{
            chka.setEnabled(true);
            chka.setVisibility(View.VISIBLE);
            chkc.setEnabled(true);
            chkc.setVisibility(View.VISIBLE);
        }
    }
    public void ans_c_onclick(View view) {
        if(chkc.isChecked()){
            chkb.setEnabled(false);
            chkb.setVisibility(View.INVISIBLE);
            chka.setEnabled(false);
            chka.setVisibility(View.INVISIBLE);
        }
        else{
            chkb.setEnabled(true);
            chkb.setVisibility(View.VISIBLE);
            chka.setEnabled(true);
            chka.setVisibility(View.VISIBLE);
        }
    }

    public class server_send implements Runnable{

        @Override
        public void run() {
            try {
                gv.broadcast("ready_question", 0);
                gv.broadcast(et_question.getText().toString(), 0);
                if (have_image) {
                    gv.broadcast("have_image", 0);
                    int size = gv.imagebuffer.length;
                    for (globalvariable.player p : gv.players) {
                        p.output.writeInt(size);
                        p.output.flush();
                        p.output.write(gv.imagebuffer, 0, gv.imagebuffer.length);
                        p.output.flush();
                    }
                }
                else
                    gv.broadcast("no_image", 0);
                gv.broadcast(et_ans_a.getText().toString(), 0);
                gv.broadcast(et_ans_b.getText().toString(), 0);
                gv.broadcast(et_ans_c.getText().toString(), 0);
                if (chka.isChecked())
                    gv.broadcast("A", 0);
                else if (chkb.isChecked())
                    gv.broadcast("B", 0);
                else if (chkc.isChecked())
                    gv.broadcast("C", 0);

                Bundle bundle = new Bundle();
                Intent intent = new Intent();
                bundle.putString("content", "wait_ans");
                intent.putExtras(bundle);
                intent.setClass(set_question.this, wait.class);
                startActivity(intent);
            } catch (Exception e) {

            }
        }
    }

    private Runnable client_send = new Runnable(){

        @Override
        public void run() {
            try {
                gv.send_to_server("ready_question");
                gv.send_to_server(et_question.getText().toString());
                if (have_image) {
                    gv.send_to_server("have_image");
                    int size = gv.imagebuffer.length;
                    gv.output.writeInt(size);
                    gv.output.flush();
                    gv.output.write(gv.imagebuffer, 0, gv.imagebuffer.length);
                    gv.output.flush();
                }
                else
                    gv.send_to_server("no_image");
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
            Thread thread = new Thread(new server_send());
            thread.start();
        }
        else{
            Thread thread=new Thread(client_send);
            thread.start();
        }
    }

    public void add_image_onclick(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            uri = data.getData();
            have_image = true;
            try {
                bitmap = BitmapFactory.decodeStream(resolver.openInputStream(uri));
                imageView.setImageBitmap(bitmap);
                Thread thread = new Thread(new bitmapcopress());
                thread.start();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    public class bitmapcopress implements Runnable{
        @Override
        public void run() {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 0, baos);
                gv.imagebuffer = baos.toByteArray();
            } catch (Exception e) {

            }
        }
    }
}