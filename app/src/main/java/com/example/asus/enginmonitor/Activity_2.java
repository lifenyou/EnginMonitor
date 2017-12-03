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
import android.support.v7.widget.ButtonBarLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Arrays;

public class Activity_2 extends AppCompatActivity {
    private Button SysParameter,MotorDebug,SettingReturn2,addStep,downStep,AdjStepSave;

    private byte[]SetParamete=new byte[]{0x5a,(byte)0xa5,(byte)0x83,0x07,0x01,0x00,0x00,0x00,0x00,0x00};
    private Button PsdInputDone;
    private Button SettingReturn1;
    private EditText PsdTips;//显示输入的密码
    private ImageView wifiRefresh2;
    String password="1472580";//密码
    private ImageView MotorPower,FaultPower;     //电机电源和故障调速电源指示灯，红色为开，绿色为关；
    private Intent intent1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.input_pds_activity);
        fineViews();
        startService(new Intent(Activity_2.this,ReeiveService.class));
        IntentFilter intentFilter=new IntentFilter();
        myReceicver receicver=new myReceicver();
        intentFilter.addAction("com.example.asus.enginmonitor.ConnectService");
        registerReceiver(receicver,intentFilter);
        SettingReturn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] psdCansle=new byte[10];
                psdCansle[0]=0x5a;
                psdCansle[1]=(byte) 0xa5;
                psdCansle[2]=0x07;
                psdCansle[3]=(byte) 0x83;
                psdCansle[4]=0x01;
                psdCansle[5]=0x24;
                psdCansle[6]=0x00;
                psdCansle[7]=0x00;
                int a=CRC16(psdCansle,5,3);
                psdCansle[8]=(byte)(((a&0xff00)>>8)&0xff) ;
                psdCansle[9]=(byte)((a&0x00ff)&0xff);

                try {
                    ReeiveService.dataOutputStream.write(psdCansle);

                }catch (Exception e){
                    Toast.makeText(Activity_2.this,"未取得socket连接",Toast.LENGTH_SHORT).show();
                }
                intent1=new Intent(Activity_2.this,TextActivity.class);
                startActivity(intent1);
            }
        });
        PsdInputDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*password=PsdTips.getText().toString();
                byte[] temp=password.getBytes();
                byte[] order=new byte[temp.length+8];
                order[0]=0x5a;
                order[1]=(byte) 0xa5;
                order[2]=0x07;
                order[0]=0x5a;*/
                String inputPsd=PsdTips.getText().toString();
                if(inputPsd.equals(""))
                {
                    Toast.makeText(Activity_2.this,"请输入密码！",Toast.LENGTH_LONG).show();
                }
                else if(!inputPsd.equals(password))
                {
                    Toast.makeText(Activity_2.this,"密码错误！请重新输入",Toast.LENGTH_LONG).show();
                    PsdTips.setText("");

                }else {
                    PsdTips.setText("");
                    Intent intent=new Intent(Activity_2.this,StepAdjActivity.class);
                    startActivity(intent);
                }
                byte[] sendmsg=new byte[15];
                sendmsg[0]=0x5a;
                sendmsg[1]=(byte)0xa5;
                sendmsg[2]=0x0c;
                sendmsg[3]=(byte)0x83;
                sendmsg[4]=0x10;
                sendmsg[5]=0x00;
                sendmsg[6]=0x31;
                sendmsg[7]=0x34;
                sendmsg[8]=0x37;
                sendmsg[9]=0x32;
                sendmsg[10]=0x35;
                sendmsg[11]=0x38;
                sendmsg[12]=0x30;

                int a=CRC16(sendmsg,10,3);
                sendmsg[13]=(byte)(((a&0xff00)>>8)&0xff) ;
                sendmsg[14]=(byte)((a&0x00ff)&0xff);
                try {
                  ReeiveService.dataOutputStream.write(sendmsg);
                }catch (Exception e){
                    Toast.makeText(Activity_2.this,"未取得socket连接",Toast.LENGTH_SHORT).show();
                }
            }
        });

        wifiRefresh2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[]sendmsg=new byte[10];
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
                    Toast.makeText(Activity_2.this,"未取得socket连接",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void fineViews(){
        PsdInputDone=(Button)findViewById(R.id.PsdInputDone);
        SettingReturn1=(Button)findViewById(R.id.SettingReturn1);
        PsdTips=(EditText)findViewById(R.id.PsdTips);
        MotorPower=(ImageView)findViewById(R.id.MotorPower2);
        FaultPower=(ImageView)findViewById(R.id.FaultPower2);
        wifiRefresh2=(ImageView)findViewById(R.id.wifi_state2);
    }

    class myReceicver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent){
            handler.sendEmptyMessage(0x01);
        }
    }

    Handler handler=new Handler(){
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            switch (msg.what){
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




                }
            }

        }

    };
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
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode==KeyEvent.KEYCODE_BACK&&event.getRepeatCount()==0){
            byte[] psdCansle=new byte[10];
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
                Log.v("发出去的数据是：", Arrays.toString(psdCansle));
            }catch (Exception e){
                Toast.makeText(Activity_2.this,"未取得socket连接",Toast.LENGTH_SHORT).show();
            }
            Intent intent=new Intent(Activity_2.this,TextActivity.class);
            startActivity(intent);

        }
        return super.onKeyDown(keyCode,event);
    }
}
