package com.example.asus.enginmonitor;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

public class ReeiveService extends Service {
    public static byte[]data=new byte[110];//用于缓存WiFi传过来的数据
    private static String ip="10.10.100.254";//WiFi地址
    private static int port=8899;//通信端口
    public static Socket client;
    //输入输出流
    public static DataInputStream dataInputStream;
    public static DataOutputStream dataOutputStream;
    public static BufferedInputStream bufferedReader;
    private Thread receicve;//建立套接字连接的线程
    private Thread readData;//循环读数据的线程
    //public static Handler serviceHandler;
    private static final  byte[] CurSpeedAddr={0x01,0x00};//当前转速
    private static final  byte[] CurGearAddr={0x01,0x02};//当前档位
    private static final  byte[] GearSpeedAddr={0x01,0x04};//档位速度
    private static final  byte[] CurStepAddr={0x01,0x06};//当前步数
    private static final  byte[] RiseRateAddr={0x01,0x08};//上升速率
    private static final  byte[] MaxStepAddr={0x01,0x10};//最大步数
    private static final  byte[] MaxSpeedAddr={0x01,0x0A};//最大转速
    private static final  byte[] PermitGearAddr={0x01,0x0C};//允许档位
    private static final  byte[] runawaySpeedAddr={0x01,0x0E};//飞车转速
    private static final  byte[] FalutNumAddr={0x01,0x12};//故障数量
    private static final  byte[] AdjustSteppAddr={0x01,0x14};//调整步数
    private static final  byte[] AdjRangeAddr={0x01,0x16};///整定范围
    private static final  byte[] MotorPowerAddr={0x01,0x18};//电机电源
    private static final  byte[] FaultPowerAddr={0x01,0x1A};//故障电源
    private static final  byte[] MotorPhaseAStateAddr={0x01,0x34};//A相开开关
    private static final  byte[] MotorPhaseBStateAddr={0x01,0x36};//B相开开关
    private static final  byte[] MotorPhaseCStateAddr={0x01,0x38};//C相开开关
    private static final  byte[] zhentou={0x5a,(byte)0xa5};//数据帧头
    //数据
    public static int  CurSpeedData;
    public static int  CurGearData;
    public static int GearSpeedData;
    public static int CurStepData;
    public static int RiseRateData;
    public static int MaxStepData;
    public static int MaxSpeedData;
    public static int PermitGearData;
    public static int runawaySpeedData;
    public static int AdjustSteppData;
    public static int AdjRangeData;
    public static int Falsnum;
    public static int MotorPowerData;
    public static int FaultPowerData;
    public static int MotorPhaseAStateData;
    public static int MotorPhaseBStateData;
    public static int MotorPhaseCStateData;
    byte[] wifiRefresh=new byte[10];//要发出去的数据


    public ReeiveService() {
    }
    @Override
    public void onCreate(){
        Log.v("绑定服务正在创建",".....");
        super.onCreate();
        wifiRefresh[0]=0x5a;
        wifiRefresh[1]=(byte)0xa5;
        wifiRefresh[2]=0x07;
        wifiRefresh[3]=(byte)0x83;
        wifiRefresh[4]=0x55;
        wifiRefresh[5]=0x55;
        wifiRefresh[6]=0x00;
        wifiRefresh[7]=0x00;
        int a=CRC16(wifiRefresh,5,3);
        wifiRefresh[8]=(byte)(((a&0xff00)>>8)&0xff) ;
        wifiRefresh[9]=(byte)((a&0x00ff)&0xff);
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public int  onStartCommand(Intent intent,int flags,int startId){
        Log.v("绑定服务正在创建","onStartCommand方法正在执行");
        receicve=new Thread(){
            public void run(){
                try {
                    client=new Socket(ip,port);//创建指定地址和端口的套接字
                    Log.v("套接字已经创建完成","...");
                    dataInputStream=new DataInputStream(client.getInputStream());//取得套接字输入流
                    bufferedReader=new BufferedInputStream(client.getInputStream());//取得套接字输出流
                    dataOutputStream=new DataOutputStream(client.getOutputStream());//取得套接字输出流
                    try {
                        dataOutputStream.write(wifiRefresh);//连接成功就发出一个刷新信号
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                        Log.v("发送失败！","网络尚未建立连接！");

                    }
                    Log.v("取得输入输出流","...");
                    while (true){
                        try {
                            int datalength=bufferedReader.read(data);
                            Log.v("完成数据读取",Arrays.toString(data));
                            for(int i=0;i<data.length-2;i++)
                            {
                                if(((data[i]&0xff)==(zhentou[0]&0xff))&&((data[i+1]&0xff)==(zhentou[1]&0xff)))//判断是否为数据头。是则进行下一步数据解析
                                {
                                    byte[] temp=Arrays.copyOfRange(data, i, i+10);//截取数据帧头开始的10个比特数据装进temp中
                                    Log.v("截取出来的数据帧是：",""+Arrays.toString(temp));
                                    int CRCvalue = ((temp[8] & 0xff) << 8) + (temp[9] & 0xff);//对截取出来的数据进行校验
                                    if((CRCvalue==CRC16(temp,5,3)))//校验成功
                                    {
                                        Log.v("校验成功：","校验成功");
                                        if(((CurSpeedAddr[0]&0xff)==temp[4])&&((CurSpeedAddr[1]&0xff)==temp[5]))
                                        {
                                            int a = ((temp[6] & 0xff) << 8) + (temp[7] & 0xff);
                                            CurSpeedData=a;
                                        }
                                        if(((CurGearAddr[0]&0xff)==temp[4])&&((CurGearAddr[1]&0xff)==temp[5]))
                                        {
                                            int a = ((temp[6] & 0xff) << 8) + (temp[7] & 0xff);
                                            CurGearData=a;
                                        }
                                        if(((GearSpeedAddr[0]&0xff)==temp[4])&&((GearSpeedAddr[1]&0xff)==temp[5]))
                                        {
                                            int a = ((temp[6] & 0xff) << 8) + (temp[7] & 0xff);
                                            GearSpeedData=a;
                                        }
                                        if(((CurStepAddr[0]&0xff)==temp[4])&&((CurStepAddr[1]&0xff)==temp[5]))
                                        {
                                            int a = ((temp[6] & 0xff) << 8) + (temp[7] & 0xff);
                                            CurStepData=a;
                                        }
                                        if(((FalutNumAddr[0]&0xff)==temp[4])&&((FalutNumAddr[1]&0xff)==temp[5]))
                                        {
                                             int a = ((temp[6] & 0xff) << 8) + (temp[7] & 0xff);
                                             Falsnum=a;
                                        }
                                        if(((AdjustSteppAddr[0]&0xff)==temp[4])&&((AdjustSteppAddr[1]&0xff)==temp[5]))
                                        {
                                            int a = ((temp[6] & 0xff) << 8) + (temp[7] & 0xff);
                                            AdjustSteppData=a;
                                        }
                                        if(((RiseRateAddr[0]&0xff)==temp[4])&&((RiseRateAddr[1]&0xff)==temp[5]))
                                        {
                                            int a = ((temp[6] & 0xff) << 8) + (temp[7] & 0xff);
                                            RiseRateData=a;

                                        }
                                        if(((MaxSpeedAddr[0]&0xff)==temp[4])&&((MaxSpeedAddr[1]&0xff)==temp[5]))
                                        {  int a = ((temp[6] & 0xff) << 8) + (temp[7] & 0xff);
                                            MaxSpeedData=a;
                                        }
                                        if(((PermitGearAddr[0]&0xff)==temp[4])&&((PermitGearAddr[1]&0xff)==temp[5]))
                                        {  int a = ((temp[6] & 0xff) << 8) + (temp[7] & 0xff);
                                           PermitGearData=a;
                                        }
                                        if(((runawaySpeedAddr[0]&0xff)==temp[4])&&((runawaySpeedAddr[1]&0xff)==temp[5]))
                                        {  int a = ((temp[6] & 0xff) << 8) + (temp[7] & 0xff);
                                           runawaySpeedData=a;
                                        }
                                        if(((MaxStepAddr[0]&0xff)==temp[4])&&((MaxStepAddr[1]&0xff)==temp[5]))
                                        {  int a = ((temp[6] & 0xff) << 8) + (temp[7] & 0xff);
                                           MaxStepData=a;
                                        }
                                        if(((AdjRangeAddr[0]&0xff)==temp[4])&&((AdjRangeAddr[1]&0xff)==temp[5]))
                                        {  int a = ((temp[6] & 0xff) << 8) + (temp[7] & 0xff);
                                            AdjRangeData=a;
                                        }
                                        if(((FaultPowerAddr[0]&0xff)==temp[4])&&((FaultPowerAddr[1]&0xff)==temp[5]))
                                        {  int a = ((temp[6] & 0xff) << 8) + (temp[7] & 0xff);
                                            FaultPowerData=a;
                                        }
                                        if(((MotorPowerAddr[0]&0xff)==temp[4])&&((MotorPowerAddr[1]&0xff)==temp[5]))
                                        {  int a = ((temp[6] & 0xff) << 8) + (temp[7] & 0xff);
                                            MotorPowerData=a;
                                        }
                                        if(((MotorPhaseAStateAddr[0]&0xff)==temp[4])&&((MotorPhaseAStateAddr[1]&0xff)==temp[5]))
                                        {  int a = ((temp[6] & 0xff) << 8) + (temp[7] & 0xff);
                                            MotorPhaseAStateData=a;
                                        }
                                        if(((MotorPhaseBStateAddr[0]&0xff)==temp[4])&&((MotorPhaseBStateAddr[1]&0xff)==temp[5]))
                                        {  int a = ((temp[6] & 0xff) << 8) + (temp[7] & 0xff);
                                            MotorPhaseBStateData=a;
                                        }
                                        if(((MotorPhaseCStateAddr[0]&0xff)==temp[4])&&((MotorPhaseCStateAddr[1]&0xff)==temp[5]))
                                        {  int a = ((temp[6] & 0xff) << 8) + (temp[7] & 0xff);
                                            MotorPhaseCStateData=a;
                                        }
                                    }
                                }
                            }
                            Intent intent=new Intent();
                            intent.setAction("com.example.asus.enginmonitor.ConnectService");
                            if (datalength!=9)
                            {
                                sendBroadcast(intent);
                                Log.v("CountService", " 广播已经发送" );
//                            serviceHandler.sendEmptyMessage(0x01);
                            }
                        }catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        };
        receicve.start();
        readData=new Thread(){
          public void run(){
             // if(bufferedReader!=null)
              {
                  while (true)
                  {
                      try {
                          bufferedReader.read(data);
                          Log.v("完成数据读取",Arrays.toString(data));

                      }catch (IOException e)
                      {
                          e.printStackTrace();
                      }
                  }
              }
          }
        };//readData.start();
        return  Service.START_NOT_STICKY;
    }
    public static  int  findAddr(byte[] data,byte[] addr){
        boolean isAddr=false;
        int i=0,j,k;
        for( i=0;i<data.length-1;i++){
            if(((data[i]&0xff)==(addr[0]&0xff))&&((data[i+1]&0xff)==(addr[1]&0xff)))
            {
                i=i;
                isAddr=true;
                break;
            }
        }
        return i;
    }
    public static byte[] cutData(int i,byte[]dataArray){
        byte[] data=new byte[10];
        data=Arrays.copyOfRange(dataArray, i-4, i+6);
        int[] datavalue=new int[10];
        for(int k=0;i<10;i++)
        {
            datavalue[k]=(data[k]&0xff);
        }
        System.out.println(Arrays.toString(data));
        return data;
    }


    /**
     *功能：完成CRC校验
     * 参数Buf：要校验的字节数组
     * 参数Len：要校验的长度
     * 参数offset：校验的起始位
     *
     */
    public  static  int  CRC16(byte[] Buf, int Len,int offset) {
        int CRC;
        int i, j;
        int Temp=0;
        CRC = 0xffff;
        for (i = offset; i < Len+offset; i++) {
            CRC= (CRC^(Buf[i]&0xff));
            // System.out.println(byteToInteger(Buf[i]));
            for (j = 0; j < 8; j++) {
                Temp=(CRC & 0x0001);
                CRC =  (CRC >> 1);
                if (Temp == 1)
                {
                    CRC =  (CRC^0xA001);
                }
            }
        }
        return ((CRC&0x00ff)<<8)+((CRC&0xff00)>>8);
    }


}
