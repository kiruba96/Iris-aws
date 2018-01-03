package com.example.hp.iris;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.IOException;
import java.util.HashMap;

import static com.example.hp.iris.MainActivity.c;

public class camera2 extends AppCompatActivity implements TextToSpeech.OnInitListener,TextToSpeech.OnUtteranceCompletedListener{
    Uri image;
    String s1;
    Bitmap bitmap;
    ImageView imageView;
    TextToSpeech tvvs1;
    SparseArray<Face> sparseArray;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.camera2);
        image=Uri.parse(getIntent().getStringExtra("uri"));
        Toast.makeText(this, ""+image, Toast.LENGTH_SHORT).show();
        tvvs1=new TextToSpeech(camera2.this,camera2.this);
        //image=Uri.parse(s1);
        try {
             bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),image);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(this, ""+bitmap, Toast.LENGTH_SHORT).show();
         imageView=(ImageView)findViewById(R.id.imageView);
        imageView.setImageURI(image);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FaceDetector faceDetector = new FaceDetector.Builder(getApplicationContext())
                        .setTrackingEnabled(false)
                        .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                        .setMode(FaceDetector.FAST_MODE)
                        .build();
                if(!faceDetector.isOperational())
                {
                    Toast.makeText(camera2.this, "Face Detector could not be set up on your device", Toast.LENGTH_SHORT).show();
                    return;
                }
                Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                 sparseArray = faceDetector.detect(frame);

                for(int i=0;i<sparseArray.size();i++)
                {
                    Face face = sparseArray.valueAt(i);
//                    float x1=face.getPosition().x;
//                    float y1 =face.getPosition().y;
//                    float x2 = x1+face.getWidth();
//                    float y2=y1+face.getHeight();
//                    //RectF rectF = new RectF(x1,y1,x2,y2);
//                    canvas.drawRoundRect(rectF,2,2,rectPaint);

                    detectLandmarks(face);

                }

                //.setImageDrawable(new BitmapDrawable(getResources(),tempBitmap));

            }
        });
        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startActivity(new Intent(camera2.this,MainActivity.class));
                return false;
            }
        });
    }
    private void detectLandmarks(Face face) {
        if(sparseArray!=null) {
            if (!tvvs1.isSpeaking() && face.getIsSmilingProbability() > 0) {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "sampleText");
                tvvs1.speak("Face is detected and the person is happy", TextToSpeech.QUEUE_ADD, params);
            } else if (!tvvs1.isSpeaking() && face.getIsSmilingProbability() < 0) {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "sampleText");
                tvvs1.speak("Face is detected and the person  is sad", TextToSpeech.QUEUE_ADD, params);
            } else {
                tvvs1.stop();
            }

            Toast.makeText(this, "Face is detected" + face.getIsSmilingProbability(), Toast.LENGTH_LONG).show();
        }
        else
        {

        }
//        for (Landmark landmark : face.getLandmarks()) {
//
//            int cx = (int) (landmark.getPosition().x);
//            int cy = (int) (landmark.getPosition().y);
//
//
//
//
//            drawEyePatchBitmap(landmark.getType(), cx, cy);
        //}
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
            startActivity(new Intent(camera2.this,MyLocationGetter.class));
            return true;
        }
        return onKeyLongPress(keycode, event);
    }

}
