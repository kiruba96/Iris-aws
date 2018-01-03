package com.example.hp.iris;

import android.content.Intent;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.telephony.SmsManager;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ScrollView;
import android.widget.Toast;

import java.util.HashMap;

import static com.example.hp.iris.MainActivity.c;

public class Main2Activity extends AppCompatActivity implements TextToSpeech.OnInitListener,TextToSpeech.OnUtteranceCompletedListener{
    CardView barcode,textscanner,facedetect,call,vision,msg;
    ScrollView scrollView;
    Handler handler=new Handler();

    TextToSpeech tvvs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main2);
        textscanner=(CardView)findViewById(R.id.textscanner);
        barcode=(CardView)findViewById(R.id.barcode);
        facedetect=(CardView)findViewById(R.id.facedetect);
        call=(CardView)findViewById(R.id.call);
        scrollView=(ScrollView)findViewById(R.id.scroll_view);
        vision=(CardView)findViewById(R.id.vision);
        msg=(CardView)findViewById(R.id.Message);
        tvvs=new TextToSpeech(Main2Activity.this,Main2Activity.this);
        textscanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!tvvs.isSpeaking()){
                    HashMap<String,String> params=new HashMap<String, String>();
                    params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"sampleText");
                    tvvs.speak("you have selected textscanner scanning Option, to enter long press in the same place",TextToSpeech.QUEUE_ADD,params);
                }
                else{
                    tvvs.stop();
                }


            }
        });
        scrollView.setSmoothScrollingEnabled(true);
        scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                    if(!tvvs.isSpeaking()){
                        HashMap<String,String> params=new HashMap<String, String>();
                        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"sampleText");
                        tvvs.speak("your view Scrolled",TextToSpeech.QUEUE_ADD,params);
                    }
                    else{
                        tvvs.stop();
                    }
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            tvvs.stop();
                        }
                    },2500);


            }
        });
        facedetect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!tvvs.isSpeaking()){
                    HashMap<String,String> params=new HashMap<String, String>();
                    params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"sampleText");
                    tvvs.speak("you have selected FaceDetection Option, to enter long press in the same place",TextToSpeech.QUEUE_ADD,params);
                }
                else{
                    tvvs.stop();
                }

            }
        });
        facedetect.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(!tvvs.isSpeaking()){
                                HashMap<String,String> params=new HashMap<String, String>();
                                params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"sampleText");
                                tvvs.speak("Opening Facedetection Camera click to take picute and long click to enable face detection",TextToSpeech.QUEUE_ADD,params);
                            }
                            else{
                                tvvs.stop();
                            }
                            startActivity(new Intent(Main2Activity.this,camera1.class));

                        }
                    });


//                            handler.postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    startActivity(new Intent(Main2Activity.this,camera1.class));
//
//                                }
//                            },5000);

                                //finish();



                return false;
            }
        });
        vision.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!tvvs.isSpeaking()){
                    HashMap<String,String> params=new HashMap<String, String>();
                    params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"sampleText");
                    tvvs.speak("you have selected Vision Option, to enter long press in the same place",TextToSpeech.QUEUE_ADD,params);
                }
                else{
                    tvvs.stop();
                }

            }
        });
//
        vision.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(!tvvs.isSpeaking()){
                    HashMap<String,String> params=new HashMap<String, String>();
                    params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"sampleText");
                    tvvs.speak("Opening vision Camera",TextToSpeech.QUEUE_ADD,params);
                }
                else{
                    tvvs.stop();
                }
                startActivity(new Intent(Main2Activity.this,cloudcamera.class));

                return false;
            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!tvvs.isSpeaking()){
                    HashMap<String,String> params=new HashMap<String, String>();
                    params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"sampleText");
                    tvvs.speak("you have selected Call Option, to enter long press in the same place",TextToSpeech.QUEUE_ADD,params);
                }
                else{
                    tvvs.stop();
                }
            }
        });
        call.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(!tvvs.isSpeaking()){
                    HashMap<String,String> params=new HashMap<String, String>();
                    params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"sampleText");
                    tvvs.speak("Click on the contact to hear the name .",TextToSpeech.QUEUE_ADD,params);
                    tvvs.speak("Long press on the contact to call the name ",TextToSpeech.QUEUE_ADD,params);
                }
                else{
                    tvvs.stop();
                }
                startActivity(new Intent(Main2Activity.this,contacts2.class));
                finish();
                return false;
            }
        });
        msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!tvvs.isSpeaking()){
                    HashMap<String,String> params=new HashMap<String, String>();
                    params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"sampleText");
                    tvvs.speak("you have selected Message Option, to enter long press in the same place",TextToSpeech.QUEUE_ADD,params);
                }
                else{
                    tvvs.stop();
                }

            }
        });
        msg.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(!tvvs.isSpeaking()){
                    HashMap<String,String> params=new HashMap<String, String>();
                    params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"sampleText");
                    tvvs.speak("Click on the contact to hear the name .",TextToSpeech.QUEUE_ADD,params);
                    tvvs.speak("Long press on the contact to Message the name ",TextToSpeech.QUEUE_ADD,params);
                }
                else{
                    tvvs.stop();
                }
                startActivity(new Intent(Main2Activity.this,Message.class));
                finish();
                return false;
            }
        });
        textscanner.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(!tvvs.isSpeaking()){
                    HashMap<String,String> params=new HashMap<String, String>();
                    params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"sampleText");
                    tvvs.speak("Entered Camera",TextToSpeech.QUEUE_ADD,params);
                }
                else{
                    tvvs.stop();
                }
                startActivity(new Intent(Main2Activity.this,textscanner.class));
                return false;
            }
        });
        barcode.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(!tvvs.isSpeaking()){
                    HashMap<String,String> params=new HashMap<String, String>();
                    params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"sampleText");
                    tvvs.speak("Entered Camera",TextToSpeech.QUEUE_ADD,params);
                }
                else{
                    tvvs.stop();
                }
                startActivity(new Intent(Main2Activity.this, com.example.hp.iris.barcode.class));
                return false;
            }
        });
        barcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!tvvs.isSpeaking()){
                    HashMap<String,String> params=new HashMap<String, String>();
                    params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"sampleText");
                    tvvs.speak("You have selected barcode option please long press to open",TextToSpeech.QUEUE_ADD,params);
                }
                else{
                    tvvs.stop();
                }


            }
        });


    }
    @Override
    protected void onDestroy() {
        if(tvvs!=null)
        {
            tvvs.stop();
            tvvs.shutdown();

            tvvs=null;
        }


        super.onDestroy();
    }

    @Override
    public void onInit(int status) {

    }

    @Override
    public void onUtteranceCompleted(String utteranceId) {

    }
    public  boolean onKeyDown(int keyCode,KeyEvent event)
    {
        if(keyCode==KeyEvent.KEYCODE_VOLUME_UP){
            event.startTracking();



            return true;
        }
        return super.onKeyDown(keyCode,event);

    }
    public boolean onKeyLongPress(int keycode,KeyEvent event){
        if(keycode==KeyEvent.KEYCODE_VOLUME_UP){
            startActivity(new Intent(Main2Activity.this,MyLocationGetter.class));
            return true;
        }
        return onKeyLongPress(keycode, event);
    }

}
