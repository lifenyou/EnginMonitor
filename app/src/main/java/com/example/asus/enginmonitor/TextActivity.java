package com.example.asus.enginmonitor;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Arrays;

public class TextActivity extends AppCompatActivity   {
    //界面的各个控件
    private Button SetParameter;
    private ImageView MotorPower,FaultPower,wifiRefresh;     //电机电源和故障调速电源指示灯，红色为开，绿色为关；
    private TextView CurSpeedShower;              //显示当前转速
    private TextView CurGearShower;               //显示当前档位
    private TextView CurStep;                     //显示当前步数
    private TextView RiseRate;                    //显示上升速率
    private TextView MaxSpeed;                    //显示最大转速
    private TextView PermitGear;                  //显示允许档位
    private TextView GearSpeed;                   //显示档位速度
    private TextView RunawaySpeed;                //显示飞车速度
    private TextView MaxStep;                     //显示最大步数
    private TextView FalutNum;                    //显示故障数量
    private TextView FaultMsg;                    //显示故障信息

    //数据
    private int CurSpeedData;
    private int CurGearData;
    private int GearSpeedData;
    private int CurStepData;
    private int RiseRateData;
    private int MaxStepData;
    private int MaxSpeedData;
    private int PermitGearData;
    private int runawaySpeedData;
    private int FalutNumData;
    private int MotorPowerData;
    private int FaultPowerData;



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
    private static final  byte[] MotorPowerAddr={0x01,0x18};//电机电源
    private static final  byte[] FaultPowerAddr={0x01,0x1A};//故障电源
    private static final  byte[] zhentou={0x5a,(byte)0xa5};//数据帧头
    //刷新的指令
    private byte[] sendmsg=new byte[10];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);//调用父类非空构造方法
        setContentView(R.layout.activity_test);//加载要显示的布局文件
        findViews();//寻找布局文件的控件
        Log.v("启动服务","...");
        startService(new Intent(TextActivity.this,ReeiveService.class));//启动连接套接字的服务
        myReceicver receicver=new myReceicver();//实例化广播接收器
        IntentFilter intentFilter=new IntentFilter();//实例化动作过滤器
        intentFilter.addAction("com.example.asus.enginmonitor.ConnectService");//添加过滤参数
        TextActivity.this.registerReceiver(receicver,intentFilter);//注册广播接收器
        //监听SetParameter的按下事件
       SetParameter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*byte[] sendmsg=new byte[10];//要发出去的数据
                sendmsg[0]=0x5a;
                sendmsg[1]=(byte)0xa5;
                sendmsg[2]=0x07;
                sendmsg[3]=(byte)0x83;
                sendmsg[4]=0x01;
                sendmsg[5]=0x20;
                sendmsg[6]=0x00;
                sendmsg[7]=0x01;
                int a=CRC16(sendmsg,5,3);
                sendmsg[8]=(byte)(((a&0xff00)>>8)&0xff) ;
                sendmsg[9]=(byte)((a&0x00ff)&0xff);
                try {
                    ReeiveService.dataOutputStream.write(sendmsg);
                    Intent intent=new Intent(TextActivity.this,Activity_2.class);
                    startActivity(intent);
                }catch (Exception e){
                    Toast.makeText(TextActivity.this,"未取得socket连接",Toast.LENGTH_SHORT).show();
                }*/
                dataSendInit((byte)0X01,(byte)0X20,(byte)0X00,(byte)0X01);
                sendData(sendmsg);
                startActivity(new Intent(TextActivity.this,Activity_2.class));
            }
        });
        //监听wifiRefresh按下的事件
        wifiRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* byte[] sendmsg=new byte[10];
                sendmsg[0]=0x5a;
                sendmsg[1]=(byte)0xa5;
                sendmsg[2]=0x07;
                sendmsg[3]=(byte)0x83;
                sendmsg[4]=0x55;
                sendmsg[5]=0x55;
                sendmsg[6]=0x00;
                sendmsg[7]=0x00;
                int a=CRC16(sendmsg,5,3);
                sendmsg[8]=(byte)(((a&0xff00)>>8)&0xff) ;
                sendmsg[9]=(byte)((a&0x00ff)&0xff);
               try {
                    ReeiveService.dataOutputStream.write(sendmsg);

                }catch (Exception e){
                    Toast.makeText(TextActivity.this,"未取得socket连接",Toast.LENGTH_SHORT).show();
                }*/
                dataSendInit((byte)0x55,(byte)0x55,(byte)0x00,(byte)0x00);
               sendData(sendmsg);
            }
        });
    }

    /**
     * 功能：向WiFi模块发送数据
     * 参数data：要发送的数据
     */

    public void sendData(byte[] data){
        try {
            ReeiveService.dataOutputStream.write(data);
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(TextActivity.this,"未取得socket连接",Toast.LENGTH_SHORT).show();//若网络未连接，则给出提示
        }
    }

    /*
    * 赋值要发送的数据的第 5 6 7 8位
    *
    * */
    public void dataSendInit(byte data_4,byte data_5,byte data_6,byte data_7){
        sendmsg[0]=0x5a;
        sendmsg[1]=(byte)0xa5;
        sendmsg[2]=0x07;
        sendmsg[3]=(byte)0x83;
        sendmsg[4]=data_4;
        sendmsg[5]=data_5;
        sendmsg[6]=data_6;
        sendmsg[7]=data_7;
        int a=CRC16(sendmsg,5,3);
        sendmsg[8]=(byte)(((a&0xff00)>>8)&0xff) ;
        sendmsg[9]=(byte)((a&0x00ff)&0xff);
    }

    public void findViews() {
        SetParameter = (Button) findViewById(R.id.SetParameter);
        CurSpeedShower = (TextView) findViewById(R.id.CurSpeedShower);
        CurGearShower = (TextView) findViewById(R.id.CurGearShower);
        CurStep = (TextView) findViewById(R.id.CurStepShower);
        RiseRate = (TextView) findViewById(R.id.RiseRateShower);
        MaxSpeed = (TextView) findViewById(R.id.MaxSpeedShower1);
        PermitGear = (TextView) findViewById(R.id.PermitGearShower1);
        GearSpeed = (TextView) findViewById(R.id.GearSpeed);
        RunawaySpeed = (TextView) findViewById(R.id.RunawaySpeedShower1);
        MaxStep = (TextView) findViewById(R.id.MaxStepShower);
        FalutNum = (TextView) findViewById(R.id.FaultMsg);
        FaultMsg = (TextView) findViewById(R.id.FaultMsgShower);
        MotorPower = (ImageView) findViewById(R.id.MotorPower1);
        FaultPower=(ImageView) findViewById(R.id.FaultPower1);
        GearSpeed=(TextView)findViewById(R.id.GearSpeedShower);
        wifiRefresh=(ImageView)findViewById(R.id.wifi_state);
    }
    class myReceicver extends BroadcastReceiver{
       public void onReceive(Context context,Intent intent){
           handler.sendEmptyMessage(0x01);
       }
   }
    Handler handler=new Handler(){
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            switch (msg.what)
            {
                case 0x01:
                {
                    CurSpeedData=ReeiveService.CurSpeedData;
                    CurGearData=ReeiveService.CurGearData;
                    GearSpeedData=ReeiveService.GearSpeedData;
                    CurStepData=ReeiveService.GearSpeedData;
                    RiseRateData=ReeiveService.RiseRateData;
                    MaxSpeedData= ReeiveService.MaxSpeedData;
                    PermitGearData=ReeiveService.PermitGearData;
                    MaxStepData=ReeiveService.MaxStepData;
                    runawaySpeedData=ReeiveService.runawaySpeedData;
                    MotorPowerData=ReeiveService.MotorPowerData;
                    FaultPowerData=ReeiveService.FaultPowerData;
                    CurSpeedShower.setText(Integer.toString(ReeiveService.CurSpeedData));
                    CurGearShower.setText(Integer.toString(ReeiveService.CurGearData));
                    GearSpeed.setText(Integer.toString(ReeiveService.GearSpeedData));
                    CurStep.setText(Integer.toString(ReeiveService.CurStepData));
                    RiseRate.setText(Integer.toString(ReeiveService.RiseRateData));
                    MaxSpeed.setText(Integer.toString(ReeiveService.MaxSpeedData));
                    PermitGear.setText(Integer.toString(ReeiveService.PermitGearData));
                    RunawaySpeed.setText(Integer.toString(ReeiveService.runawaySpeedData));
                    MaxStep.setText(Integer.toString(ReeiveService.MaxStepData));
                    FalutNum.setText(Integer.toString(ReeiveService.Falsnum));
                    if(ReeiveService.MotorPowerData==0)
                    {
                        MotorPower.setImageResource(R.drawable.off);
                    }else if(ReeiveService.MotorPowerData==1){
                        MotorPower.setImageResource(R.drawable.on);

                    }
                    if(ReeiveService.MotorPowerData==0)
                    {
                        FaultPower.setImageResource(R.drawable.on);
                    }else if(ReeiveService.MotorPowerData==1){
                        FaultPower.setImageResource(R.drawable.off);

                    }

                }

            }
        }

    };

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

