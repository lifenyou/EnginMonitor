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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.EOFException;
import java.util.Arrays;

public class StepAdjActivity extends AppCompatActivity {
    private Intent intent1,intent2,intent;
    private Button SysParameter,MotorDebug,SettingReturn2,addStep,downStep,AdjStepSave;
    private ImageView MotorPower,FaultPower;     //电机电源和故障调速电源指示灯，红色为开，绿色为关；
    private byte[]message=new byte[10];
    private byte[]dataSend=new byte[]{0x5A,(byte) 0XA5,(byte)0x83,0x07,0x00,0x00,0x00,0x00,0x00,0x00};


    private ImageView wifiRefresh3;
    private TextView CurSpeedShower;              //显示当前转速
    private TextView CurGearShower;               //显示当前档位
    private TextView PermitGear;                  //显示允许档位
    private TextView CurStep;                     //显示当前步数
    private EditText AdjustStep;                  //显示调整步数
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adjstep);
        fineViews();
        startService(new Intent(StepAdjActivity.this,ReeiveService.class));
        myReceicver receicver=new myReceicver();
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction("com.example.asus.enginmonitor.ConnectService");
        registerReceiver(receicver,intentFilter);

        wifiRefresh3.setOnClickListener(new View.OnClickListener() {
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

                }catch (Exception e){
                    Toast.makeText(StepAdjActivity.this,"未取得socket连接",Toast.LENGTH_SHORT).show();
                }
            }
        });
        SysParameter.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                byte[] changePage=new byte[10];
                changePage[0]=0x5a;
                changePage[1]=(byte)0xa5;
                changePage[2]=0x07;
                changePage[3]=(byte)0x83;
                changePage[4]=0x01;
                changePage[5]=0x28;
                changePage[6]=0x00;
                changePage[7]=0x00;
                int a=CRC16(changePage,5,3);
                changePage[8]=(byte)(((a&0xff00)>>8)&0xff) ;
                changePage[9]=(byte)((a&0x00ff)&0xff);
                try {
                   ReeiveService.dataOutputStream.write(changePage);
                }catch (Exception e){
                    Toast.makeText(StepAdjActivity.this,"未取得socket连接",Toast.LENGTH_SHORT).show();
                }

                intent1=new Intent(StepAdjActivity.this,SystemSettingActivity.class);
                startActivity(intent1);
                //finish();
            }
        });
        MotorDebug.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                byte[] changePage=new byte[10];
                changePage[0]=0x5a;
                changePage[1]=(byte)0xa5;
                changePage[2]=0x07;
                changePage[3]=(byte)0x83;
                changePage[4]=0x01;
                changePage[5]=0x2A;
                changePage[6]=0x00;
                changePage[7]=0x00;
                int a=CRC16(changePage,5,3);
                changePage[8]=(byte)(((a&0xff00)>>8)&0xff) ;
                changePage[9]=(byte)((a&0x00ff)&0xff);
                try {
                    ReeiveService.dataOutputStream.write(changePage);
                }catch (Exception e){
                    Toast.makeText(StepAdjActivity.this,"未取得socket连接",Toast.LENGTH_SHORT).show();
                }
                //receiverThread.sendData(changePage);
                // receiverThread.close();
                intent2=new Intent(StepAdjActivity.this,motorDebugActivity.class);
                startActivity(intent2);

            }
        });
        SettingReturn2.setOnClickListener(new View.OnClickListener() {
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
                    Log.v("发出去的数据是：", Arrays.toString(psdCansle));
                }catch (Exception e){
                    Toast.makeText(StepAdjActivity.this,"未取得socket连接",Toast.LENGTH_SHORT).show();
                }
                //receiverThread.sendData(psdCansle);
                // receiverThread.close();
                intent=new Intent(StepAdjActivity.this,TextActivity.class);
                startActivity(intent);
            }
        });
        downStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if((!AdjustStep.getText().toString().equals("")))
                {
                    short  AdjustStepData=(short) (Integer.parseInt(AdjustStep.getText().toString()));

                    AdjustStep.setText(Integer.toString(AdjustStepData));
                    byte[] sendAdjustStepData=new byte[10];
                    sendAdjustStepData[0]=0x5a;
                    sendAdjustStepData[1]=(byte) 0xa5;
                    sendAdjustStepData[2]=0x07;
                    sendAdjustStepData[3]=(byte) 0x83;
                    sendAdjustStepData[4]=0x01;
                    sendAdjustStepData[5]=0x14;
                    sendAdjustStepData[6]=(byte)((AdjustStepData&0xff00)>>8);
                    sendAdjustStepData[7]=(byte)((AdjustStepData&0x00ff));
                    int a=CRC16(sendAdjustStepData,5,3);
                    sendAdjustStepData[8]=(byte)(((a&0xff00)>>8)&0xff) ;
                    sendAdjustStepData[9]=(byte)((a&0x00ff)&0xff);
                    try {
                        ReeiveService.dataOutputStream.write(sendAdjustStepData);
                        Log.v("发出去的数据是：", Arrays.toString(sendAdjustStepData));
                    }catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(StepAdjActivity.this,"未取得socket连接",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        addStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if((!AdjustStep.getText().toString().equals("")))
                {
                    int  AdjustStepData=Integer.parseInt(AdjustStep.getText().toString());
                    AdjustStep.setText(Integer.toString(AdjustStepData));
                    byte[] sendAdjustStepData=new byte[10];
                    sendAdjustStepData[0]=0x5a;
                    sendAdjustStepData[1]=(byte) 0xa5;
                    sendAdjustStepData[2]=0x07;
                    sendAdjustStepData[3]=(byte) 0x83;
                    sendAdjustStepData[4]=0x01;
                    sendAdjustStepData[5]=0x14;
                    sendAdjustStepData[6]=0x00;
                    sendAdjustStepData[7]=(byte)(AdjustStepData&0xff);
                    int a=CRC16(sendAdjustStepData,5,3);
                    sendAdjustStepData[8]=(byte)(((a&0xff00)>>8)&0xff) ;
                    sendAdjustStepData[9]=(byte)((a&0x00ff)&0xff);
                    try {
                      ReeiveService.dataOutputStream.write(sendAdjustStepData);
                    }catch (Exception e){
                        Toast.makeText(StepAdjActivity.this,"未取得socket连接",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        AdjStepSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    byte[] sendAdjustStepData=new byte[10];
                    sendAdjustStepData[0]=0x5a;
                    sendAdjustStepData[1]=(byte) 0xa5;
                    sendAdjustStepData[2]=0x07;
                    sendAdjustStepData[3]=(byte) 0x83;
                    sendAdjustStepData[4]=0x01;
                    sendAdjustStepData[5]=0x26;
                    sendAdjustStepData[6]=0x00;
                    sendAdjustStepData[7]=0x00;
                    int a=CRC16(sendAdjustStepData,5,3);
                    sendAdjustStepData[8]=(byte)(((a&0xff00)>>8)&0xff) ;
                    sendAdjustStepData[9]=(byte)((a&0x00ff)&0xff);
                    try{
                        Log.v("发出去的数据是：", Arrays.toString(sendAdjustStepData));
                        ReeiveService.dataOutputStream.write(sendAdjustStepData);
                    }catch (Exception e){
                        Toast.makeText(StepAdjActivity.this,"未取得socket连接",Toast.LENGTH_SHORT).show();
                    }
                }
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
                  CurSpeedShower.setText(Integer.toString(ReeiveService.CurSpeedData));
                  CurGearShower.setText(Integer.toString(ReeiveService.CurGearData));
                  PermitGear.setText(Integer.toString(ReeiveService.PermitGearData));
                  CurStep.setText(Integer.toString(ReeiveService.CurStepData));
                  AdjustStep.setText(Integer.toString(ReeiveService.AdjustSteppData));
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
    private void fineViews(){
        SysParameter=(Button)findViewById(R.id.SysParameter);
        MotorDebug=(Button)findViewById(R.id.MotorDebug);
        SettingReturn2=(Button)findViewById(R.id.SettingReturn2);

        CurSpeedShower=(TextView)findViewById(R.id.CurSpeedShower3);
        CurGearShower=(TextView)findViewById(R.id.CurGearShower3);
        PermitGear=(TextView)findViewById(R.id.PermitGearShower3);
        CurStep=(TextView)findViewById(R.id.CurStepShower3);
        AdjustStep=(EditText) findViewById(R.id.AdjustStepShower3);

        addStep=(Button)findViewById(R.id.AdjustStepUp);
        downStep=(Button)findViewById(R.id.AdjustStepDown);
        AdjStepSave=(Button)findViewById(R.id.AdjStepSave);

        MotorPower=(ImageView)findViewById(R.id.MotorPower3);
        FaultPower=(ImageView)findViewById(R.id.FaultPower3);
        wifiRefresh3=(ImageView)findViewById(R.id.wifi_state3);
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
                Toast.makeText(StepAdjActivity.this,"未取得socket连接",Toast.LENGTH_SHORT).show();
            }
            intent=new Intent(StepAdjActivity.this,TextActivity.class);
            startActivity(intent);

        }
        return super.onKeyDown(keyCode,event);

    }

}
