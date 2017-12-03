package com.example.asus.enginmonitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

public class SystemSettingActivity extends AppCompatActivity {
    private Button save,SettingReturn3;
    private ImageView MotorPower,FaultPower;     //电机电源和故障调速电源指示灯，红色为开，绿色为关；
    private ImageView wifiRefresh4;
    private TextView RiseRate;                    //显示上升速率
    private TextView AdjRange;                    //显示整定范围
    private TextView PermitGear;                  //显示允许档位
    private TextView RunawaySpeed;                //显示飞车转速
    private TextView MaxStep;                     //显示最大步数

    private ImageButton RiseRate_down ;
    private ImageButton RiseRate_up ;
    private ImageButton AdjRange_down ;
    private ImageButton AdjRange_up ;
    private ImageButton PermitGear_dwon ;
    private ImageButton PermitGear_up ;
    private ImageButton RunawaySpeed_down ;
    private ImageButton RunawaySpeed_up ;
    private ImageButton MaxStep_down ;
    private ImageButton MaxStep_up ;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.system_set_activity);
        findViews();
        startService(new Intent(SystemSettingActivity.this,ReeiveService.class));
        myReceicver receicver=new myReceicver();
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction("com.example.asus.enginmonitor.ConnectService");
        SystemSettingActivity.this.registerReceiver(receicver,intentFilter);

        wifiRefresh4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] sendmsg=new byte[10];
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
               }catch (Exception e)
               {
                   e.printStackTrace();
                   Toast.makeText(SystemSettingActivity.this,"未取得socket连接",Toast.LENGTH_SHORT).show();

               }
            }
        });
        SettingReturn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                }catch (Exception e)
                {
                    e.printStackTrace();
                    Toast.makeText(SystemSettingActivity.this,"未取得socket连接",Toast.LENGTH_SHORT).show();
                }
                intent=new Intent(SystemSettingActivity.this,StepAdjActivity.class);
                startActivity(intent);
            }
        });
        RiseRate_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!RiseRate.getText().toString().equals(""))
                {
                    int a =Integer.parseInt(RiseRate.getText().toString())-1;
                    if(a<2)
                    {
                        Toast.makeText(SystemSettingActivity.this,"输入错误，上升速率不能小于2！",Toast.LENGTH_LONG).show();
                    }else
                    {
                        RiseRate.setText(Integer.toString(a));
                    }
                    byte[] Temp_RiseRate=new byte[10];
                    Temp_RiseRate[0]=0x5a;
                    Temp_RiseRate[1]=(byte) 0xa5;
                    Temp_RiseRate[2]=0x07;
                    Temp_RiseRate[3]=(byte) 0x83;
                    Temp_RiseRate[4]=0x01;
                    Temp_RiseRate[5]=0x08;
                    Temp_RiseRate[6]=(byte)((a&0xff00)>>8);
                    Temp_RiseRate[7]=(byte)((a&0x00ff));
                    int CRC=CRC16(Temp_RiseRate,5,3);
                    Temp_RiseRate[8]=(byte)(((CRC&0xff00)>>8)&0xff) ;
                    Temp_RiseRate[9]=(byte)((CRC&0x00ff)&0xff);
                    try {
                        ReeiveService.dataOutputStream.write(Temp_RiseRate);
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                        Toast.makeText(SystemSettingActivity.this,"未取得socket连接",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        RiseRate_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!RiseRate.getText().toString().equals(""))
                {
                    String RiseRateStr=RiseRate.getText().toString();
                    int a =Integer.parseInt(RiseRateStr)+1;
                    if(a>50)
                    {
                        Toast.makeText(SystemSettingActivity.this,"输入错误，上升速率不能大于50！",Toast.LENGTH_LONG).show();
                    }else
                    {
                        RiseRate.setText(Integer.toString(a));
                    }
                    byte[] Temp_RiseRate=new byte[10];
                    Temp_RiseRate[0]=0x5a;
                    Temp_RiseRate[1]=(byte) 0xa5;
                    Temp_RiseRate[2]=0x07;
                    Temp_RiseRate[3]=(byte) 0x83;
                    Temp_RiseRate[4]=0x01;
                    Temp_RiseRate[5]=0x08;
                    Temp_RiseRate[6]=(byte)((a&0xff00)>>8);
                    Temp_RiseRate[7]=(byte)((a&0x00ff));
                    int CRC=CRC16(Temp_RiseRate,5,3);
                    Temp_RiseRate[8]=(byte)(((CRC&0xff00)>>8)&0xff) ;
                    Temp_RiseRate[9]=(byte)((CRC&0x00ff)&0xff);
                    try {
                        ReeiveService.dataOutputStream.write(Temp_RiseRate);
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                        Toast.makeText(SystemSettingActivity.this,"未取得socket连接",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        AdjRange_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!AdjRange.getText().toString().equals(""))
                {
                    int a =Integer.parseInt(AdjRange.getText().toString())-5;
                    if (a<0)
                    {
                        Toast.makeText(SystemSettingActivity.this,"输入错误，整定范围不能小于0！",Toast.LENGTH_LONG).show();
                    }else
                    {
                        AdjRange.setText(Integer.toString(a));
                    }
                    byte[] Temp_AdjRange=new byte[10];
                    Temp_AdjRange[0]=0x5a;
                    Temp_AdjRange[1]=(byte) 0xa5;
                    Temp_AdjRange[2]=0x07;
                    Temp_AdjRange[3]=(byte) 0x83;
                    Temp_AdjRange[4]=0x01;
                    Temp_AdjRange[5]=0x16;
                    Temp_AdjRange[6]=(byte)((a&0xff00)>>8);
                    Temp_AdjRange[7]=(byte)((a&0x00ff));
                    int CRC=CRC16(Temp_AdjRange,5,3);
                    Temp_AdjRange[8]=(byte)(((CRC&0xff00)>>8)&0xff) ;
                    Temp_AdjRange[9]=(byte)((CRC&0x00ff)&0xff);
                    try {
                        ReeiveService.dataOutputStream.write(Temp_AdjRange);
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                        Toast.makeText(SystemSettingActivity.this,"未取得socket连接",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        AdjRange_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!AdjRange.getText().toString().equals(""))
                {
                    String RiseRateStr=AdjRange.getText().toString();
                    int a =Integer.parseInt(RiseRateStr)+5;
                    if (a>300)
                    {
                        Toast.makeText(SystemSettingActivity.this,"输入错误，整定范围不能大于300！",Toast.LENGTH_LONG).show();
                    }else{
                        AdjRange.setText(Integer.toString(a));
                    }
                    byte[] Temp_AdjRange=new byte[10];
                    Temp_AdjRange[0]=0x5a;
                    Temp_AdjRange[1]=(byte) 0xa5;
                    Temp_AdjRange[2]=0x07;
                    Temp_AdjRange[3]=(byte) 0x83;
                    Temp_AdjRange[4]=0x01;
                    Temp_AdjRange[5]=0x16;
                    Temp_AdjRange[6]=(byte)((a&0xff00)>>8);
                    Temp_AdjRange[7]=(byte)((a&0x00ff));
                    int CRC=CRC16(Temp_AdjRange,5,3);
                    Temp_AdjRange[8]=(byte)(((CRC&0xff00)>>8)&0xff) ;
                    Temp_AdjRange[9]=(byte)((CRC&0x00ff)&0xff);
                    try {
                        ReeiveService.dataOutputStream.write(Temp_AdjRange);
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                        Toast.makeText(SystemSettingActivity.this,"未取得socket连接",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        PermitGear_dwon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!PermitGear.getText().toString().equals(""))
                {
                    int a =Integer.parseInt(PermitGear.getText().toString())-1;
                    if(a<0)
                    {
                        Toast.makeText(SystemSettingActivity.this,"输入错误，允许档位不能小于0！",Toast.LENGTH_LONG).show();
                    }else
                    {
                        PermitGear.setText(Integer.toString(a));
                    }
                    byte[] Temp_PermitGear=new byte[10];
                    Temp_PermitGear[0]=0x5a;
                    Temp_PermitGear[1]=(byte) 0xa5;
                    Temp_PermitGear[2]=0x07;
                    Temp_PermitGear[3]=(byte) 0x83;
                    Temp_PermitGear[4]=0x01;
                    Temp_PermitGear[5]=0x0C;
                    Temp_PermitGear[6]=(byte)((a&0xff00)>>8);
                    Temp_PermitGear[7]=(byte)((a&0x00ff));
                    int CRC=CRC16(Temp_PermitGear,5,3);
                    Temp_PermitGear[8]=(byte)(((CRC&0xff00)>>8)&0xff) ;
                    Temp_PermitGear[9]=(byte)((CRC&0x00ff)&0xff);
                    try {
                        ReeiveService.dataOutputStream.write(Temp_PermitGear);
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                        Toast.makeText(SystemSettingActivity.this,"未取得socket连接",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        PermitGear_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!PermitGear.getText().toString().equals(""))
                {

                    String RiseRateStr=PermitGear.getText().toString();
                    int a =Integer.parseInt(RiseRateStr)+1;
                    if(a>16)
                    {
                        Toast.makeText(SystemSettingActivity.this,"输入错误，允许档位不能大于16！",Toast.LENGTH_LONG).show();
                    }else
                    {
                        PermitGear.setText(Integer.toString(a));
                    }
                    byte[] Temp_PermitGear=new byte[10];
                    Temp_PermitGear[0]=0x5a;
                    Temp_PermitGear[1]=(byte) 0xa5;
                    Temp_PermitGear[2]=0x07;
                    Temp_PermitGear[3]=(byte) 0x83;
                    Temp_PermitGear[4]=0x01;
                    Temp_PermitGear[5]=0x0C;
                    Temp_PermitGear[6]=(byte)((a&0xff00)>>8);
                    Temp_PermitGear[7]=(byte)((a&0x00ff));
                    int CRC=CRC16(Temp_PermitGear,5,3);
                    Temp_PermitGear[8]=(byte)(((CRC&0xff00)>>8)&0xff) ;
                    Temp_PermitGear[9]=(byte)((CRC&0x00ff)&0xff);
                    try {
                        ReeiveService.dataOutputStream.write(Temp_PermitGear);
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                        Toast.makeText(SystemSettingActivity.this,"未取得socket连接",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        RunawaySpeed_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!RunawaySpeed.getText().toString().equals(""))
                {
                    int a =Integer.parseInt(RunawaySpeed.getText().toString())-10;
                    if(a<0)
                    {
                        Toast.makeText(SystemSettingActivity.this,"输入错误，飞车速度不能小于0！",Toast.LENGTH_LONG).show();
                    }else
                    {
                        RunawaySpeed.setText(Integer.toString(a));
                    }
                    byte[] Temp_RunawaySpeed=new byte[10];
                    Temp_RunawaySpeed[0]=0x5a;
                    Temp_RunawaySpeed[1]=(byte) 0xa5;
                    Temp_RunawaySpeed[2]=0x07;
                    Temp_RunawaySpeed[3]=(byte) 0x83;
                    Temp_RunawaySpeed[4]=0x01;
                    Temp_RunawaySpeed[5]=0x0E;
                    Temp_RunawaySpeed[6]=(byte)((a&0xff00)>>8);
                    Temp_RunawaySpeed[7]=(byte)((a&0x00ff));
                    int CRC=CRC16(Temp_RunawaySpeed,5,3);
                    Temp_RunawaySpeed[8]=(byte)(((CRC&0xff00)>>8)&0xff) ;
                    Temp_RunawaySpeed[9]=(byte)((CRC&0x00ff)&0xff);
                    try {
                        ReeiveService.dataOutputStream.write(Temp_RunawaySpeed);
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                        Toast.makeText(SystemSettingActivity.this,"未取得socket连接",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        RunawaySpeed_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!RunawaySpeed.getText().toString().equals(""))
                {
                    String RiseRateStr=RunawaySpeed.getText().toString();
                    int a =Integer.parseInt(RiseRateStr)+10;
                    if(a>1120)
                    {
                        Toast.makeText(SystemSettingActivity.this,"输入错误，飞车速度不能大于1120！",Toast.LENGTH_LONG).show();
                    }else {
                        RunawaySpeed.setText(Integer.toString(a));
                    }
                    byte[] Temp_RunawaySpeed=new byte[10];
                    Temp_RunawaySpeed[0]=0x5a;
                    Temp_RunawaySpeed[1]=(byte) 0xa5;
                    Temp_RunawaySpeed[2]=0x07;
                    Temp_RunawaySpeed[3]=(byte) 0x83;
                    Temp_RunawaySpeed[4]=0x01;
                    Temp_RunawaySpeed[5]=0x0E;
                    Temp_RunawaySpeed[6]=(byte)((a&0xff00)>>8);
                    Temp_RunawaySpeed[7]=(byte)((a&0x00ff));
                    int CRC=CRC16(Temp_RunawaySpeed,5,3);
                    Temp_RunawaySpeed[8]=(byte)(((CRC&0xff00)>>8)&0xff) ;
                    Temp_RunawaySpeed[9]=(byte)((CRC&0x00ff)&0xff);
                    try {
                        ReeiveService.dataOutputStream.write(Temp_RunawaySpeed);
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                        Toast.makeText(SystemSettingActivity.this,"未取得socket连接",Toast.LENGTH_SHORT).show();
                    }                }

            }
        });
        MaxStep_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!MaxStep.getText().toString().equals(""))
                {
                    int a =Integer.parseInt(MaxStep.getText().toString())-10;
                    if (a<0)
                    {
                        Toast.makeText(SystemSettingActivity.this,"输入错误，最大步数不能小于0！",Toast.LENGTH_LONG).show();

                    }else {
                        MaxStep.setText(Integer.toString(a));
                    }
                    byte[] Temp_MaxStep=new byte[10];
                    Temp_MaxStep[0]=0x5a;
                    Temp_MaxStep[1]=(byte) 0xa5;
                    Temp_MaxStep[2]=0x07;
                    Temp_MaxStep[3]=(byte) 0x83;
                    Temp_MaxStep[4]=0x01;
                    Temp_MaxStep[5]=0x10;
                    Temp_MaxStep[6]=(byte)((a&0xff00)>>8);
                    Temp_MaxStep[7]=(byte)((a&0x00ff));
                    int CRC=CRC16(Temp_MaxStep,5,3);
                    Temp_MaxStep[8]=(byte)(((CRC&0xff00)>>8)&0xff) ;
                    Temp_MaxStep[9]=(byte)((CRC&0x00ff)&0xff);
                    try {
                        ReeiveService.dataOutputStream.write(Temp_MaxStep);
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                        Toast.makeText(SystemSettingActivity.this,"未取得socket连接",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        MaxStep_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!MaxStep.getText().toString().equals(""))
                {
                    String RiseRateStr=MaxStep.getText().toString();
                    int a =Integer.parseInt(RiseRateStr)+10;
                    if (a>5000)
                    {
                        Toast.makeText(SystemSettingActivity.this,"输入错误，最大步数不能大于5000！",Toast.LENGTH_LONG).show();

                    }else
                    {
                        MaxStep.setText(Integer.toString(a));
                    }
                    byte[] Temp_MaxStep=new byte[10];
                    Temp_MaxStep[0]=0x5a;
                    Temp_MaxStep[1]=(byte) 0xa5;
                    Temp_MaxStep[2]=0x07;
                    Temp_MaxStep[3]=(byte) 0x83;
                    Temp_MaxStep[4]=0x01;
                    Temp_MaxStep[5]=0x10                    ;
                    Temp_MaxStep[6]=(byte)((a&0xff00)>>8);
                    Temp_MaxStep[7]=(byte)((a&0x00ff));
                    int CRC=CRC16(Temp_MaxStep,5,3);
                    Temp_MaxStep[8]=(byte)(((CRC&0xff00)>>8)&0xff) ;
                    Temp_MaxStep[9]=(byte)((CRC&0x00ff)&0xff);
                    try {
                        ReeiveService.dataOutputStream.write(Temp_MaxStep);
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                        Toast.makeText(SystemSettingActivity.this,"未取得socket连接",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] SettingSave=new byte[10];
                SettingSave[0]=0x5a;
                SettingSave[1]=(byte) 0xa5;
                SettingSave[2]=0x07;
                SettingSave[3]=(byte) 0x83;
                SettingSave[4]=0x01;
                SettingSave[5]=0x2C;
                SettingSave[6]=0x00;
                SettingSave[7]=0x00;
                int CRC=CRC16(SettingSave,5,3);
                SettingSave[8]=(byte)(((CRC&0xff00)>>8)&0xff) ;
                SettingSave[9]=(byte)((CRC&0x00ff)&0xff);
                try {
                    ReeiveService.dataOutputStream.write(SettingSave);
                }catch (Exception e)
                {
                    e.printStackTrace();
                    Toast.makeText(SystemSettingActivity.this,"未取得socket连接",Toast.LENGTH_SHORT).show();
                }
                intent=new Intent(SystemSettingActivity.this,StepAdjActivity.class);
                startActivity(intent);
            }
        });
    }
    Handler handler=new Handler(){
        public void handleMessage(Message message){
            super.handleMessage(message);
            switch (message.what)
            {
                case 0x01:
                {
                    RiseRate.setText(Integer.toString(ReeiveService.RiseRateData));
                    RiseRate.setText(Integer.toString(ReeiveService.RiseRateData));
                    AdjRange.setText(Integer.toString(ReeiveService.AdjRangeData));
                    PermitGear.setText(Integer.toString(ReeiveService.PermitGearData));
                    RunawaySpeed.setText(Integer.toString(ReeiveService.runawaySpeedData));
                    MaxStep.setText(Integer.toString(ReeiveService.MaxStepData));
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
    class myReceicver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent){
            handler.sendEmptyMessage(0x01);
        }
    }
    private void findViews(){
        save=(Button)findViewById(R.id.SettingSave);
        SettingReturn3=(Button)findViewById(R.id.SettingReturn3);

        RiseRate=(TextView)findViewById(R.id.RiseRate4);
        AdjRange=(TextView)findViewById(R.id.AdjRange);
        PermitGear=(TextView)findViewById(R.id.PermitGear4);
        RunawaySpeed=(TextView)findViewById(R.id.RunawaySpeed4);
        MaxStep=(TextView)findViewById(R.id.MaxStep4);

        RiseRate_down=(ImageButton)findViewById(R.id.RiseRate_down);
        RiseRate_up=(ImageButton)findViewById(R.id.RiseRate_up);
        AdjRange_down=(ImageButton)findViewById(R.id.AdjRange_down);
        AdjRange_up=(ImageButton)findViewById(R.id.AdjRange_up);
        PermitGear_dwon=(ImageButton)findViewById(R.id.PermitGear_dwon);
        PermitGear_up=(ImageButton)findViewById(R.id.PermitGear_up);
        RunawaySpeed_down=(ImageButton)findViewById(R.id.RunawaySpeed_down);
        RunawaySpeed_up=(ImageButton)findViewById(R.id.RunawaySpeed_up);
        MaxStep_down=(ImageButton)findViewById(R.id.MaxStep_down);
        MaxStep_up=(ImageButton)findViewById(R.id.MaxStep_up);

        MotorPower=(ImageView)findViewById(R.id.MotorPower4);
        FaultPower=(ImageView)findViewById(R.id.FaultPower4);
        wifiRefresh4=(ImageView)findViewById(R.id.wifi_state4);

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
                Toast.makeText(SystemSettingActivity.this,"未取得socket连接",Toast.LENGTH_SHORT).show();
            }
            Intent intent=new Intent(SystemSettingActivity.this,StepAdjActivity.class);
            startActivity(intent);

        }
        return super.onKeyDown(keyCode,event);

    }

}
