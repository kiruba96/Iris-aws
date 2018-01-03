package com.example.hp.iris;

import android.Manifest;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.zagum.speechrecognitionview.RecognitionProgressView;
import com.github.zagum.speechrecognitionview.adapters.RecognitionListenerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements  TextToSpeech.OnInitListener,TextToSpeech.OnUtteranceCompletedListener {
    private TextToSpeech tvvs;
     TextToSpeech myTTS;
    // status check code
    private int MY_DATA_CHECK_CODE = 0;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION_CODE = 1;
    RecognitionProgressView recognitionProgressView;
    private SpeechRecognizer speechRecognizer;
    static int c=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        Intent checkTTSIntent = new Intent();
        checkTTSIntent
                .setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);
        Thread logoTimer = new Thread() {
            public void run() {
                try {
                    try {
                        sleep(1000);
                        speakWords("Tap on the screen to enable mic");
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }



                }

                finally {
                    //finish();
                }
            }

        };
        logoTimer.start();
        tvvs=new TextToSpeech(MainActivity.this,MainActivity.this);

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        int[] colors = {
                ContextCompat.getColor(this, R.color.color1),
                ContextCompat.getColor(this, R.color.color1),
                ContextCompat.getColor(this, R.color.color1),
                ContextCompat.getColor(this, R.color.color1),
                ContextCompat.getColor(this, R.color.color1)
//                ContextCompat.getColor(this, R.color.color2),
//                ContextCompat.getColor(this, R.color.color3),
//                ContextCompat.getColor(this, R.color.color4),
//                ContextCompat.getColor(this, R.color.color3),
//                ContextCompat.getColor(this, R.color.color4),
//                ContextCompat.getColor(this, R.color.color5),
//                ContextCompat.getColor(this, R.color.color2)

        };

        int[] heights = {60, 76, 58, 80, 55};
         recognitionProgressView= (RecognitionProgressView) findViewById(R.id.recognition_view);
        recognitionProgressView.setSpeechRecognizer(speechRecognizer);
        recognitionProgressView.setRecognitionListener(new RecognitionListenerAdapter() {
            @Override
            public void onResults(Bundle results) {
                showResults(results);
            }
        });

        recognitionProgressView.setColors(colors);
        recognitionProgressView.setBarMaxHeightsInDp(heights);


        RelativeLayout relativeLayout=(RelativeLayout)findViewById(R.id.relative);


        recognitionProgressView.play();
        requestPermission();

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!tvvs.isSpeaking()){
                    HashMap<String,String> params=new HashMap<String, String>();
                    params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"sampleText");
                    tvvs.speak("Mic is enabled you can speak",TextToSpeech.QUEUE_ADD,params);
                }
                else{
                    tvvs.stop();
                }


            }
        });
        relativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(!tvvs.isSpeaking()){
                    HashMap<String,String> params=new HashMap<String, String>();
                    params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"sampleText");
                    tvvs.speak("Option menu is loaded",TextToSpeech.QUEUE_ADD,params);
                }
                else{
                    tvvs.stop();
                }
                startActivity(new Intent(MainActivity.this,Main2Activity.class));
                return false;
            }
        });



}

    @Override
    public void onInit(int status) {
        try {
            if (status == TextToSpeech.SUCCESS) {
                if (myTTS.isLanguageAvailable(Locale.US) == TextToSpeech.LANG_AVAILABLE)
                    myTTS.setLanguage(Locale.US);
            } else if (status == TextToSpeech.ERROR) {
                Toast.makeText(this, "Sorry! Text To Speech failed...",
                        Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception e)
        {
            System.out.print("eroro"+e);
        }
        tvvs.setOnUtteranceCompletedListener(this);
    }

    @Override
    public void onUtteranceCompleted(String utteranceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "COMPLETED", Toast.LENGTH_SHORT).show();
                recognitionProgressView.play();
                requestPermission();
                startRecognition();
//                if (ContextCompat.checkSelfPermission(Main3Activity.this,
//                        Manifest.permission.RECORD_AUDIO)
//                        != PackageManager.PERMISSION_GRANTED) {
//                    requestPermission();
//                } else {
//                    RecognitionProgressView recognitionProgressView = (RecognitionProgressView) findViewById(R.id.recognition_view);
//                    startRecognition();
//                    recognitionProgressView.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            startRecognition();
//                        }
//                    }, 50);
//                }
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
        if(myTTS != null) {

            myTTS.stop();
            myTTS.shutdown();
            //Log.d(TAG, "TTS Destroyed");
        }
        if (speechRecognizer != null) {
            speechRecognizer.destroy();

        }
        super.onDestroy();
    }
    private void showResults(Bundle results)
    {
        ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        Toast.makeText(this, matches.get(0), Toast.LENGTH_LONG).show();
        if(matches.get(0).equals("qrcode"))
        {
            startActivity(new Intent(MainActivity.this,barcode.class));
        }
        else if(matches.get(0).equals("vision")){
            if(!tvvs.isSpeaking()){
                HashMap<String,String> params=new HashMap<String, String>();
                params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"sampleText");
                tvvs.speak("vision camera enabled",TextToSpeech.QUEUE_ADD,params);
                startActivity(new Intent(MainActivity.this,Main2Activity.class));
            }
            else{
                tvvs.stop();
            }
            startActivity(new Intent(MainActivity.this,cloudcamera.class));
        }
        else if(matches.get(0).equals("call")){
            startActivity(new Intent(MainActivity.this,contacts2.class));
        }
        else if(matches.get(0).equals("text scanner")){
            startActivity(new Intent(MainActivity.this,textscanner.class));
        }
        else if(matches.get(0).equals("face detector")){
            startActivity(new Intent(MainActivity.this,camera1.class));
        }
        else if(matches.get(0).equals("message")){
            startActivity(new Intent(MainActivity.this,Message.class));
        }
        else if(matches.get(0).equals(null)){
            if(!tvvs.isSpeaking()){
                HashMap<String,String> params=new HashMap<String, String>();
                params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"sampleText");
                tvvs.speak("Option menu is loaded",TextToSpeech.QUEUE_ADD,params);
                startActivity(new Intent(MainActivity.this,Main2Activity.class));
            }
            else{
                tvvs.stop();
            }

        }
        else
        {
            speakWords("The option menu is loaded ");

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            startActivity(new Intent(MainActivity.this,Main2Activity.class));
        }
        speechRecognizer.stopListening();

    }
    private void startRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en");
        speechRecognizer.startListening(intent);
    }
    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.RECORD_AUDIO)) {
            Toast.makeText(this, "Requires RECORD_AUDIO permission", Toast.LENGTH_SHORT).show();
        }
        else
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_RECORD_AUDIO_PERMISSION_CODE);
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                // the user has the necessary data - create the TTS
                myTTS = new TextToSpeech(this, this);
            } else {
                // no data - install it now
                Intent installTTSIntent = new Intent();
                installTTSIntent
                        .setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
            }
        }
    }
    private void speakWords(String speech) {

        // speak straight away
        if(myTTS != null)
        {
            myTTS.speak(speech, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

  public  boolean onKeyDown(int keyCode,KeyEvent event)
  {
      if(keyCode==KeyEvent.KEYCODE_VOLUME_UP){
           event.startTracking();



          return true;
      }
          return super.onKeyDown(keyCode,event);

  }
  public boolean onKeyLongPress(int keycode,KeyEvent event) {
      if(keycode==KeyEvent.KEYCODE_VOLUME_UP){
          startActivity(new Intent(MainActivity.this,MyLocationGetter.class));
          return true;
      }
      return onKeyLongPress(keycode, event);
  }


}
