package com.example.asus.enginmonitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class motorDebugActivity extends AppCompatActivity {
    private Button SettingReturn4;
    private Button Motorreversal;//转换按钮
    private Button MotorStartStop;//停启按钮
    private ImageView MotorPower,FaultPower;     //电机电源和故障调速电源指示灯，红色为开，绿色为关；
    private ImageView MotorPhaseAState,MotorPhaseBState,MotorPhaseCState;//三相开关状态显示
    private Button MotorPhaseBswitch;
    private Button MotorPhaseAswitch;
    private Button MotorPhaseCswitch;
    private ImageView wifiRefresh5;
    private Intent intent;

    private int MotorPhaseAStateFlag=0;
    private int MotorPhaseBStateFlag=0;
    private int MotorPhaseCStateFlag=1;
    private byte[] sendmsg=new byte[10];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.motor_debug_activity);
        findViews();
        myReceicver receicver=new myReceicver();
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction("com.example.asus.enginmonitor.ConnectService");
        motorDebugActivity.this.registerReceiver(receicver,intentFilter);
        dataSendInit((byte)0x55,(byte)0x55,(byte)0x00,(byte)0x00);
        sendData(sendmsg);

        wifiRefresh5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*byte[] sendmsg=new byte[10];
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
                    Toast.makeText(motorDebugActivity.this,"未取得socket连接",Toast.LENGTH_SHORT).show();
                }*/
                dataSendInit((byte)0x55,(byte)0x55,(byte)0x00,(byte)0x00);
                sendData(sendmsg);
            }
        });
        SettingReturn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*byte[] psdCansle=new byte[10];
                psdCansle[0]=0x5a;
                psdCansle[1]=(byte) 0xa5;
                psdCansle[2]=0x07;
                psdCansle[3]=(byte) 0x83;
                psdCansle[4]=0x01;
                psdCansle[5]=0x2E;
                psdCansle[6]=0x00;
                psdCansle[7]=0x00;
                int a=CRC16(psdCansle,5,3);
                psdCansle[8]=(byte)(((a&0xff00)>>8)&0xff) ;
                psdCansle[9]=(byte)((a&0x00ff)&0xff);
                try {
                    ReeiveService.dataOutputStream.write(psdCansle);

                }catch (Exception e){
                    Toast.makeText(motorDebugActivity.this,"未取得socket连接",Toast.LENGTH_SHORT).show();
                }*/
                dataSendInit((byte)0x01,(byte)0x2E,(byte)0x00,(byte)0x00);
                sendData(sendmsg);
                intent=new Intent(motorDebugActivity.this,StepAdjActivity.class);
                startActivity(intent);
            }
        });
        MotorPhaseAswitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MotorPhaseAStateFlag==1){
                    byte[] MotorPhaseA=new byte[10];
                    MotorPhaseA[0]=0x5a;
                    MotorPhaseA[1]=(byte) 0xa5;
                    MotorPhaseA[2]=0x07;
                    MotorPhaseA[3]=(byte) 0x83;
                    MotorPhaseA[4]=0x01;
                    MotorPhaseA[5]=0x34;
                    MotorPhaseA[6]=0x00;
                    MotorPhaseA[7]=0x00;
                    int CRC=CRC16(MotorPhaseA,5,3);
                    MotorPhaseA[8]=(byte)(((CRC&0xff00)>>8)&0xff) ;
                    MotorPhaseA[9]=(byte)((CRC&0x00ff)&0xff);
                    try {
                        ReeiveService.dataOutputStream.write(MotorPhaseA);
                        MotorPhaseAStateFlag=0;
                    }catch (Exception e){
                        Toast.makeText(motorDebugActivity.this,"未取得socket连接",Toast.LENGTH_SHORT).show();
                    }
                    //dataSendInit((byte)0x01,(byte)0x34,(byte)0x00,(byte)0x00);
                    //sendData(sendmsg);
                }else if(MotorPhaseAStateFlag==0)
                {
                    byte[] MotorPhaseA=new byte[10];
                    MotorPhaseA[0]=0x5a;
                    MotorPhaseA[1]=(byte) 0xa5;
                    MotorPhaseA[2]=0x07;
                    MotorPhaseA[3]=(byte) 0x83;
                    MotorPhaseA[4]=0x01;
                    MotorPhaseA[5]=0x34;                   ;
                    MotorPhaseA[6]=0x00;
                    MotorPhaseA[7]=0x01;
                    int CRC=CRC16(MotorPhaseA,5,3);
                    MotorPhaseA[8]=(byte)(((CRC&0xff00)>>8)&0xff) ;
                    MotorPhaseA[9]=(byte)((CRC&0x00ff)&0xff);
                    try {
                        ReeiveService.dataOutputStream.write(MotorPhaseA);
                        MotorPhaseAStateFlag=1;
                    }catch (Exception e){
                        Toast.makeText(motorDebugActivity.this,"未取得socket连接",Toast.LENGTH_SHORT).show();
                    }
                    //dataSendInit((byte)0x01,(byte)0x34,(byte)0x01,(byte)0x00);
                   // sendData(sendmsg);
                }
            }
        });
        MotorPhaseBswitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MotorPhaseBStateFlag==1){
                    byte[] MotorPhaseA=new byte[10];
                    MotorPhaseA[0]=0x5a;
                    MotorPhaseA[1]=(byte) 0xa5;
                    MotorPhaseA[2]=0x07;
                    MotorPhaseA[3]=(byte) 0x83;
                    MotorPhaseA[4]=0x01;
                    MotorPhaseA[5]=0x36;
                    MotorPhaseA[6]=0x00;
                    MotorPhaseA[7]=0x00;
                    int CRC=CRC16(MotorPhaseA,5,3);
                    MotorPhaseA[8]=(byte)(((CRC&0xff00)>>8)&0xff) ;
                    MotorPhaseA[9]=(byte)((CRC&0x00ff)&0xff);
                    try {
                        ReeiveService.dataOutputStream.write(MotorPhaseA);
                        MotorPhaseAStateFlag=1;
                    }catch (Exception e){
                        Toast.makeText(motorDebugActivity.this,"未取得socket连接",Toast.LENGTH_SHORT).show();
                    }
                    MotorPhaseBStateFlag=0;

                }else if(MotorPhaseBStateFlag==0)
                {
                    byte[] MotorPhaseB=new byte[10];
                    MotorPhaseB[0]=0x5a;
                    MotorPhaseB[1]=(byte) 0xa5;
                    MotorPhaseB[2]=0x07;
                    MotorPhaseB[3]=(byte) 0x83;
                    MotorPhaseB[4]=0x01;
                    MotorPhaseB[5]=0x36;                   ;
                    MotorPhaseB[6]=0x00;
                    MotorPhaseB[7]=0x01;
                    int CRC=CRC16(MotorPhaseB,5,3);
                    MotorPhaseB[8]=(byte)(((CRC&0xff00)>>8)&0xff) ;
                    MotorPhaseB[9]=(byte)((CRC&0x00ff)&0xff);
                    try {
                        ReeiveService.dataOutputStream.write(MotorPhaseB);
                        MotorPhaseAStateFlag=1;
                    }catch (Exception e){
                        Toast.makeText(motorDebugActivity.this,"未取得socket连接",Toast.LENGTH_SHORT).show();
                    }
                    MotorPhaseBStateFlag=1;
                }
            }
        });
        MotorPhaseCswitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MotorPhaseCStateFlag==1){
                    byte[] MotorPhaseC=new byte[10];
                    MotorPhaseC[0]=0x5a;
                    MotorPhaseC[1]=(byte) 0xa5;
                    MotorPhaseC[2]=0x07;
                    MotorPhaseC[3]=(byte) 0x83;
                    MotorPhaseC[4]=0x01;
                    MotorPhaseC[5]=0x38;                   ;
                    MotorPhaseC[6]=0x00;
                    MotorPhaseC[7]=0x00;
                    int CRC=CRC16(MotorPhaseC,5,3);
                    MotorPhaseC[8]=(byte)(((CRC&0xff00)>>8)&0xff) ;
                    MotorPhaseC[9]=(byte)((CRC&0x00ff)&0xff);
                    try {
                        ReeiveService.dataOutputStream.write(MotorPhaseC);
                        MotorPhaseAStateFlag=1;
                    }catch (Exception e){
                        Toast.makeText(motorDebugActivity.this,"未取得socket连接",Toast.LENGTH_SHORT).show();
                    }
                    MotorPhaseCStateFlag=0;

                }else if(MotorPhaseCStateFlag==0)
                {
                    byte[] MotorPhaseC=new byte[10];
                    MotorPhaseC[0]=0x5a;
                    MotorPhaseC[1]=(byte) 0xa5;
                    MotorPhaseC[2]=0x07;
                    MotorPhaseC[3]=(byte) 0x83;
                    MotorPhaseC[4]=0x01;
                    MotorPhaseC[5]=0x38;                   ;
                    MotorPhaseC[6]=0x00;
                    MotorPhaseC[7]=0x01;
                    int CRC=CRC16(MotorPhaseC,5,3);
                    MotorPhaseC[8]=(byte)(((CRC&0xff00)>>8)&0xff) ;
                    MotorPhaseC[9]=(byte)((CRC&0x00ff)&0xff);
                    try {
                        ReeiveService.dataOutputStream.write(MotorPhaseC);
                        MotorPhaseAStateFlag=1;
                    }catch (Exception e){
                        Toast.makeText(motorDebugActivity.this,"未取得socket连接",Toast.LENGTH_SHORT).show();
                    }
                    MotorPhaseCStateFlag=1;
                }
            }
        });
        Motorreversal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] Motorreversal=new byte[10];
                Motorreversal[0]=0x5a;
                Motorreversal[1]=(byte) 0xa5;
                Motorreversal[2]=0x07;
                Motorreversal[3]=(byte) 0x83;
                Motorreversal[4]=0x01;
                Motorreversal[5]=0x32;                   ;
                Motorreversal[6]=0x00;
                Motorreversal[7]=0x00;
                int CRC=CRC16(Motorreversal,5,3);
                Motorreversal[8]=(byte)(((CRC&0xff00)>>8)&0xff) ;
                Motorreversal[9]=(byte)((CRC&0x00ff)&0xff);
                try {
                    ReeiveService.dataOutputStream.write(Motorreversal);
                    MotorPhaseAStateFlag=1;
                }catch (Exception e){
                    Toast.makeText(motorDebugActivity.this,"未取得socket连接",Toast.LENGTH_SHORT).show();
                }

            }
        });
        MotorStartStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] MotorStartStop=new byte[10];
                MotorStartStop[0]=0x5a;
                MotorStartStop[1]=(byte) 0xa5;
                MotorStartStop[2]=0x07;
                MotorStartStop[3]=(byte) 0x83;
                MotorStartStop[4]=0x01;
                MotorStartStop[5]=0x30;                   ;
                MotorStartStop[6]=0x00;
                MotorStartStop[7]=0x00;
                int CRC=CRC16(MotorStartStop,5,3);
                MotorStartStop[8]=(byte)(((CRC&0xff00)>>8)&0xff) ;
                MotorStartStop[9]=(byte)((CRC&0x00ff)&0xff);
                try {
                    ReeiveService.dataOutputStream.write(MotorStartStop);
                    MotorPhaseAStateFlag=1;
                }catch (Exception e){
                    Toast.makeText(motorDebugActivity.this,"未取得socket连接",Toast.LENGTH_SHORT).show();
                }
            }
        });




    }
    Handler handler=new Handler(){
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            switch (msg.what)
            {
                case 0x01:
                {
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

                    if(ReeiveService.MotorPhaseAStateData==0)
                    {
                        MotorPhaseAState.setImageResource(R.drawable.on);
                    }else if(ReeiveService.MotorPhaseAStateData==1){
                        MotorPhaseAState.setImageResource(R.drawable.off);
                    }
                    if(ReeiveService.MotorPhaseBStateData==0)
                    {
                        MotorPhaseBState.setImageResource(R.drawable.on);
                    }else if(ReeiveService.MotorPhaseBStateData==1){
                        MotorPhaseBState.setImageResource(R.drawable.off);
                    } if(ReeiveService.MotorPhaseCStateData==0)
                {
                    MotorPhaseCState.setImageResource(R.drawable.on);
                }else if(ReeiveService.MotorPhaseCStateData==1){
                    MotorPhaseCState.setImageResource(R.drawable.off);
                }

                }
            }
        }
    };

    class myReceicver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent){
            handler.sendEmptyMessage(0x01);
        }
    }
    public void findViews(){
        SettingReturn4=(Button)findViewById(R.id.SettingReturn4);
        Motorreversal=(Button)findViewById(R.id.Motorreversal);
        MotorStartStop=(Button)findViewById(R.id.MotorStartStop);

        MotorPhaseBswitch=(Button)findViewById(R.id.MotorPhaseBswitch);
        MotorPhaseAswitch=(Button)findViewById(R.id.MotorPhaseAswitch);
        MotorPhaseCswitch=(Button)findViewById(R.id.MotorPhaseCswitch);

        MotorPower=(ImageView)findViewById(R.id.MotorPower5);
        FaultPower=(ImageView)findViewById(R.id.FaultPower5);

        MotorPhaseAState=(ImageView)findViewById(R.id.MotorPhaseAState);
        MotorPhaseBState=(ImageView)findViewById(R.id.MotorPhaseBState);
        MotorPhaseCState=(ImageView)findViewById(R.id.MotorPhaseCState);
        wifiRefresh5=(ImageView)findViewById(R.id.wifi_state5);
    }
    /**
     *功能：完成CRC校验
     * 参数Buf：要校验的字节数组
     * 参数Len：要校验的长度
     * 参数offset：校验的起始位
     *
     */
    public static int  CRC16(byte[] Buf, int Len,int offset) {
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
    /**
     * 功能：向WiFi模块发送数据
     * 参数data：要发送的数据
     */

    public void sendData(byte[] data){
        try {
            ReeiveService.dataOutputStream.write(data);
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(motorDebugActivity.this,"未取得socket连接",Toast.LENGTH_SHORT).show();//若网络未连接，则给出提示
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
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode==KeyEvent.KEYCODE_BACK&&event.getRepeatCount()==0){
            dataSendInit((byte)0x01,(byte)0x2E,(byte)0x00,(byte)0x00);
            sendData(sendmsg);
            intent=new Intent(motorDebugActivity.this,StepAdjActivity.class);
            startActivity(intent);

        }
        return super.onKeyDown(keyCode,event);

    }

}

