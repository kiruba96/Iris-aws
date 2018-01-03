package com.example.hp.iris;

import android.Manifest;
import android.content.AsyncQueryHandler;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.rekognition.AmazonRekognitionClient;
import com.amazonaws.services.rekognition.model.DetectTextRequest;
import com.amazonaws.services.rekognition.model.DetectTextResult;
import com.amazonaws.services.rekognition.model.Image;

import com.amazonaws.services.rekognition.model.TextDetection;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;

import id.zelory.compressor.Compressor;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;



/**
 * Created by HP on 8/26/2017.
 */

public class textscanner extends AppCompatActivity implements TextToSpeech.OnInitListener,TextToSpeech.OnUtteranceCompletedListener{
    SurfaceView cameraView;
    TextToSpeech tvvs;
    TextView textView;
    CameraSource cameraSource;
    Image image;
    CognitoCachingCredentialsProvider credentialsProvider;
    //BarcodeDetector barcodeDetector;
    String decresult;
    StringBuilder stringBuilder;
    final int RequestCameraPermissionID = 1001;
    String path;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestCameraPermissionID: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
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
        setContentView(R.layout.textscanner);

        cameraView = (SurfaceView) findViewById(R.id.surface_view);
        textView = (TextView) findViewById(R.id.text_view);
        tvvs=new TextToSpeech(textscanner.this,textscanner.this);

        credentialsProvider =new CognitoCachingCredentialsProvider( getApplicationContext(),"us-west-2:20e04e1d-cd9d-46ca-9305-93fe4f13f312", Regions.US_WEST_2);

        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        if (!textRecognizer.isOperational()) {
            Log.w("MainActivity", "Detector dependencies are not yet available");
        } else {

            cameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1280, 1024)
                    .setRequestedFps(2.0f)
                    .setAutoFocusEnabled(true)
                    .build();
            cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder surfaceHolder) {

                    try {
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                            ActivityCompat.requestPermissions(textscanner.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    RequestCameraPermissionID);
                            return;
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



            textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
                @Override
                public void release() {

                }

                @Override
                public void receiveDetections(Detector.Detections<TextBlock> detections) {

                    final SparseArray<TextBlock> items = detections.getDetectedItems();
                    if(items.size() != 0)
                    {
                        textView.post(new Runnable() {
                            @Override
                            public void run() {

                                Vibrator vibrator=(Vibrator)getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                                vibrator.vibrate(100);
//                                 stringBuilder = new StringBuilder();
//                                for(int i =0;i<items.size();++i)
//                                {
//                                    TextBlock item = items.valueAt(i);
//                                    stringBuilder.append(item.getValue());
//                                    stringBuilder.append("\n");
//                                }
//                                if(!tvvs.isSpeaking()){
//                                    HashMap<String,String> params=new HashMap<String, String>();
//                                    params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"sampleText");
//                                    tvvs.speak("some detection found please press on the screen to listen",TextToSpeech.QUEUE_ADD,params);
//                                }
                            }
                        });
                    }
                }
            });
        }
        cameraView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startActivity(new Intent(textscanner.this,MainActivity.class));
                finish();
                tvvs.stop();
                return false;
            }
        });
        cameraView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrator vibrator=(Vibrator)getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.cancel();
                cameraSource.takePicture(null, new CameraSource.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] bytes) {
                        cameraSource.release();
                        if(!tvvs.isSpeaking()){
                            HashMap<String,String> params=new HashMap<String, String>();
                            params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"sampleText");
                            tvvs.speak("picture captured please wait a minute for a results ",TextToSpeech.QUEUE_ADD,params);
                        }
                        Bitmap picture = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//                        int rotation = camera1.this.getWindowManager().getDefaultDisplay().getRotation();
//                        if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) {
//                            Matrix matrix = new Matrix();
//                            matrix.postRotate(90);
//                            // create a rotated version and replace the original bitmap
//                            picture = Bitmap.createBitmap(picture, 0, 0, picture.getWidth(), picture.getHeight(), matrix, true);
//                        }
                        MediaStore.Images.Media.insertImage(getContentResolver(), picture, "NiceCameraExample", "NiceCameraExample test");
                        path=MediaStore.Images.Media.insertImage(getContentResolver(), picture, "NiceCameraExample", "NiceCameraExample test");
                        image=new Image();
                        image.withBytes(ByteBuffer.wrap(bytes));
                        new Rekog_Text_Task().execute();




                    }

                });
//                if(stringBuilder!=null) {
//                    if (!tvvs.isSpeaking()) {
//                        HashMap<String, String> params = new HashMap<String, String>();
//                        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "sampleText");
//                        tvvs.speak("" + stringBuilder+"          Long press to go back", TextToSpeech.QUEUE_ADD, params);
//                    } else {
//                        tvvs.stop();
//                    }
//                }
            }
        });
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
    public void onInit(int status) {
        tvvs.setOnUtteranceCompletedListener(this);

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
            startActivity(new Intent(textscanner.this,MyLocationGetter.class));
            return true;
        }
        return onKeyLongPress(keycode, event);
    }
    public void speakout(String s){
        if(!tvvs.isSpeaking()){
            HashMap<String,String> params=new HashMap<String, String>();
            params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"sampleText");
            tvvs.speak(s,TextToSpeech.QUEUE_ADD,params);
        }
    }
    public class Rekog_Text_Task extends AsyncTask<String, Integer, String >{
        @Override
        protected String doInBackground(String... strings) {
            AmazonRekognitionClient rekognitionClient = new AmazonRekognitionClient(credentialsProvider);
            rekognitionClient.setRegion(Region.getRegion(Regions.US_WEST_2));
            DetectTextRequest request = new DetectTextRequest().withImage(image);
            DetectTextResult result = rekognitionClient.detectText(request);
            System.out.println("\nDetection RESULT : "+result.getTextDetections());

            List<TextDetection> bow=result.getTextDetections();
            System.out.println("list size  : "+bow.size());
            stringBuilder = new StringBuilder();
            for(int i=0; i<bow.size();i++){
               stringBuilder.append(bow.get(i).getDetectedText());
               stringBuilder.append(" ");
               System.out.println("Detected text :"+bow.get(i).getDetectedText());
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("The builder is : "+ stringBuilder.toString());
                    speakout(stringBuilder.toString());
                }
            });

            return null;
        }
    }


}
