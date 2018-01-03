package com.example.hp.iris;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.util.HashMap;

import static com.example.hp.iris.MainActivity.c;

public class barcode extends AppCompatActivity implements TextToSpeech.OnInitListener,TextToSpeech.OnUtteranceCompletedListener {
    TextToSpeech tvvs,textToSpeech;
    SurfaceView cameraView;
    TextView textView;
    CameraSource cameraSource;
    BarcodeDetector barcodeDetector;
    final int RequestCameraPermissionID = 1001;
    final Handler handler = new Handler();
    String decresult;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestCameraPermissionID: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    try {
                        cameraSource.start(cameraView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
            break;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.barcode);
        tvvs=new TextToSpeech(barcode.this,barcode.this);
        cameraView = (SurfaceView)findViewById(R.id.surface_view);
        textView = (TextView)findViewById(R.id.text_view);

        barcodeDetector=new BarcodeDetector.Builder(barcode.this)
                .setBarcodeFormats(Barcode.QR_CODE)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();
        cameraSource=new CameraSource.Builder(barcode.this,barcodeDetector)
                .setRequestedPreviewSize(640,480)
                .setAutoFocusEnabled(true)
                .build();
        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {

                try {
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(barcode.this,
                                new String[]{Manifest.permission.CAMERA},
                                RequestCameraPermissionID);
                        return;
                    }
                    if(!tvvs.isSpeaking()){
                        HashMap<String,String> params=new HashMap<String, String>();
                        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"sampleText");
                        tvvs.speak("Camera services enabled",TextToSpeech.QUEUE_ADD,params);
                    }
                    else{
                        tvvs.stop();
                    }
                    cameraSource.start(cameraView.getHolder());

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {

            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> items = detections.getDetectedItems();
                if(items.size()!=0)
                {

                            //Do something after 100ms
                            textView.post(new Runnable() {
                                @Override
                                public void run() {
                                    decresult=items.valueAt(0).displayValue;
                                    Vibrator vibrator=(Vibrator)getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                                    vibrator.vibrate(100);

                                    textView.setText(items.valueAt(0).displayValue);
                                    if(!tvvs.isSpeaking()){
                                        HashMap<String,String> params=new HashMap<String, String>();
                                        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"sampleText");
                                        tvvs.speak("some detection found please press on the screen to listen",TextToSpeech.QUEUE_ADD,params);
                                    }
                                    else{
                                        tvvs.stop();
                                    }

                                }
                            });


                }

            }
        });
        cameraView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(decresult!=null) {
                    if (!tvvs.isSpeaking()) {
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "sampleText");
                        tvvs.speak("" + decresult+"          Long press to go back", TextToSpeech.QUEUE_ADD, params);
                    } else {
                        tvvs.stop();
                    }
                }

            }
        });
        cameraView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startActivity(new Intent(barcode.this,MainActivity.class));
                finish();
                return false;
            }
        });




    }

    @Override
    public void onInit(int status) {
        tvvs.setOnUtteranceCompletedListener(this);

    }
    @Override
    protected void onDestroy() {
        if (tvvs != null) {
            tvvs.stop();
            tvvs.shutdown();

            tvvs = null;

        }
        super.onDestroy();
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
            startActivity(new Intent(barcode.this,MyLocationGetter.class));
            return true;
        }
        return onKeyLongPress(keycode, event);
    }

}
