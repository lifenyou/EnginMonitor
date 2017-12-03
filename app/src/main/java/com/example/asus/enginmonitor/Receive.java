package com.example.asus.enginmonitor;

import android.os.Handler;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

/**
 * Created by ASUS on 2017/6/8.
 */

public class Receive extends Thread {
    private Handler handler;
    public  BufferedInputStream bufferedReader;
    public  static  byte[] data=new byte[20];
    String tag="TAG";
    public Socket client;
    public DataInputStream dataInputStream;
    public DataOutputStream dataOutputStream;
    public String ip;
    public int PORT;
    Receive(String ip,int PORT){
        this.PORT=PORT;
        this.ip=ip;

    }
    public void run(){
        try {
            Log.v(tag,"尝试连接套接字");
            client=new Socket(ip,PORT);
            Log.v(tag,"尝试连接套接字");
            dataInputStream=new DataInputStream(client.getInputStream());
             bufferedReader=new BufferedInputStream(client.getInputStream());
            dataOutputStream=new DataOutputStream(client.getOutputStream());

            //dataInputStream.read(data);
            //dataInputStream.close();
            Thread readDtaThread=new Thread(){
                public void run(){
                    while (true)
                    {
                        try {
                            bufferedReader.read(data,0,data.length);
                            handler.sendEmptyMessage(0x02);
                            Log.v(tag, Arrays.toString(data));
                            //bufferedReader.close();

                        }catch (IOException e){
                            e.printStackTrace();
                        }finally {
                        }
                    }

                }
            };
            //readDtaThread.start();

        }catch (UnknownHostException e){
            e.printStackTrace();
        }catch (IOException e)
        {
            e.printStackTrace();
        }

    }
    public void sendData(byte[] data){

        try{
            dataOutputStream.write(data);
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            try {
                dataOutputStream.flush();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

}
