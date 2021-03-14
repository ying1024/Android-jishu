package com.example.qiuqiu;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class game extends AppCompatActivity implements View.OnClickListener {

    private TextView textView,num;
    private  int i = 15,j = 11,x = 0;
    private Timer timer = null;
    private TimerTask task = null;
    private int[] index,value,label;
    public Button[] buttons;
    public boolean isguess = false,isone = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        getWindow().setBackgroundDrawableResource(R.drawable.bg);

        textView = findViewById(R.id.textView);
        num = findViewById(R.id.textView2);
        textView.setText("  操作时间："+Integer.toString(i));
        value = new int[25];
        index = new int[25];
        label = new int[10];
        setbtn();
        init();



    }


    @Override
    protected void  onDestroy() {
        super. onDestroy();
        Log.i("-----","active");
        timer.cancel();
    }


    private void init(){
        i = 15;
        j = 11;
        x = 0;
        num.setText("");
        isguess = false;
        isone = false;
        if(timer!=null){
            timer.cancel();
        }

        for (int i = 0;i<25;i++){
            buttons[i].setText("");
        }
        startTime();

        setrand();
    }

    private Handler mHandler = new Handler(){
        public void handleMessage(Message msg){

            if(msg.arg1 == -1){
                over();
            }else {
                textView.setText("  操作时间："+ msg.arg1);
            }
            if(isone){
                Toast.makeText(game.this,"请找到："+label[9],Toast.LENGTH_SHORT).show();
                num.setText("请找到："+label[9]);
                isone = false;
            }
//            startTime();

        }
    };
    public void startTime(){
        timer = new Timer();
        task = new TimerTask() {

            @Override
            public void run() {
                if(i>0){
                    i--;
                    Message message = mHandler.obtainMessage();
                    message.arg1 = i;
                    mHandler.sendMessage(message);
                }else{
                    if(!isguess){
                        int j = 0;
                        for (int t = 0;t<25;t++){
                            buttons[t].setText("");

                            if(value[t]!=-1 && j<10){
                                label[j] = value[t];

                                Log.i(t+"",label[j]+"");

                                j++;
                            }
                        }
                        isone = true;
                        isguess = !isguess;
                        int tx = (int)(Math.random()*9);
                        int t = label[tx];
                        label[tx] = label[9];
                        label[9] = t;
//                        x++;
                        Log.i("------",String.valueOf(tx)+ ":" + String.valueOf(label[tx]));
                    }
                    Message message = mHandler.obtainMessage();
                    if(j>0){
                        j--;

                        message.arg1 = j;
                        mHandler.sendMessage(message);
                    }else{
                        message.arg1 = -1;
                        mHandler.sendMessage(message);
                        timer.cancel();
                    }


                }
            }
        };
        timer.schedule(task,1000,1000);
    }

    public void guessnum(){
        if(x>-1 && x<9){
            this.x++;
            int tx = (int)(Math.random()*(9-x));
//            Toast.makeText(game.this,"请找到："+label[tx],Toast.LENGTH_SHORT).show();
            num.setText("请找到："+label[tx]);
            int t = label[tx];
            label[tx] = label[9-x];
            label[9-x] = t;

        }else if (x==9){
            timer.cancel();
            new  AlertDialog.Builder(this)
                    .setTitle("成功" )
                    .setMessage("恭喜你，成功了！" )
                    .setNegativeButton("再来一局", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            init();
                        }
                    })
                    .setPositiveButton("不没意思", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })

                    .show();
        }


    }

    public void setrand(){
        for (int i = 0;i<25;i++){
            index[i] = i;
            value[i] =-1;
        }
        for (int i = 0;i<10;i++){
            int tx = (int)(Math.random()*(24-i));
            buttons[index[tx]].setText(Integer.toString(i));
            value[index[tx]] = i;
            int t = index[tx];
            index[tx] = index[24-i];
            index[24-i] = t;
        }
    }

    private void setbtn(){
        LinearLayout ls[] = new LinearLayout[5];
        ls[0] = findViewById(R.id.layout1);
        ls[1] = findViewById(R.id.layout2);
        ls[2] = findViewById(R.id.layout3);
        ls[3] = findViewById(R.id.layout4);
        ls[4] = findViewById(R.id.layout5);
        buttons = new Button[25];
        int j=-1;
        for (int i=0;i<25;i++){
            buttons[i] = new Button(this);
            buttons[i].setId(2000+i);
            buttons[i].setText("");
            buttons[i].setTextSize(25);
            buttons[i].setTag(i);
            buttons[i].setOnClickListener(game.this);
           LinearLayout.LayoutParams btParams = new LinearLayout.LayoutParams(180,180);
            if(i%5==0){
                j++;
            }
            ls[j].addView(buttons[i],btParams);

            Log.i(Integer.toString(i),Integer.toString(j));

        }
    }

    @Override
    public void onClick(View v) {
        if(isguess){

            int x = (Integer) v.getTag();
            Log.i(String.valueOf(label[9-this.x]),String.valueOf(value[x]) + "-------" + String.valueOf(this.x));
            if(value[x]==label[9-this.x]){
                Log.i("result","正确");
                Toast.makeText(game.this,"正确！",Toast.LENGTH_SHORT).show();
                buttons[x].setText(String.valueOf(value[x]));
                guessnum();
                this.j = 11;
            }else {
                Log.i("result","错误");
                over();
                timer.cancel();
            }
        }
    }

    public void over(){
        new  AlertDialog.Builder(this)
                .setTitle("失败" )
                .setMessage("很遗憾，你失败了！" )
                .setNegativeButton("再来一局", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        init();
                    }
                })
                .setPositiveButton("不太难了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })

                .show();
    }
}
