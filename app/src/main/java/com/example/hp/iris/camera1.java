package com.example.hp.iris;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import static com.example.hp.iris.MainActivity.c;

public  class camera1 extends AppCompatActivity implements TextToSpeech.OnInitListener,TextToSpeech.OnUtteranceCompletedListener{
    CameraSource cameraSource;
    SurfaceView cameraView;
    ImageView imageView;
    Bitmap bmp,bmp2;
    String path;
    public Uri imageuri;
    Intent intent;
    TextToSpeech tvvs;
    //BarcodeDetector barcodeDetector;
    SurfaceHolder surfaceHolder;
    byte[] bytes1;
    final int RequestCameraPermissionID = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.camera1);
        cameraView = (SurfaceView) findViewById(R.id.surface_view);
        imageView=(ImageView)findViewById(R.id.imageView);
        tvvs=new TextToSpeech(camera1.this,camera1.this);
//        surfaceHolder = cameraView.getHolder();
//        surfaceHolder.addCallback(this);
//        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        final FaceDetector detector = new FaceDetector.Builder(getApplicationContext()).build();
        cameraSource = new CameraSource.Builder(getApplicationContext(), detector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedFps(2.0f)
                .setAutoFocusEnabled(true)
                .build();

        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {

                try {
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(camera1.this,
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
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }



        });
        cameraView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraSource.takePicture(null, new CameraSource.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] bytes) {
                                    detector.release();
                        cameraSource.release();
//                       bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//                        //bmp.compress(Bitmap.CompressFormat.PNG,bytes);
//                        File dir=
//                                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
//                        System.out.println("bit is "+bytes);
//                        System.out.println("LOL 1"+dir);
//                        File output = new File(dir, "CameraContentDemo.jpeg");
//                        System.out.println("LOL 1"+output);
//                        imageUri=Uri.fromFile(output);
//                        System.out.println("LOL 1"+imageUri);
//
//                        Toast.makeText(MainActivity.this, "Picture Captured"+imageUri, Toast.LENGTH_SHORT).show();
//
//                        Log.d("BITMAP", bmp.getWidth() + "x" + bmp.getHeight());
//                        bytes1=bytes;
                        if(!tvvs.isSpeaking()){
                            HashMap<String,String> params=new HashMap<String, String>();
                            params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"sampleText");
                            tvvs.speak("picture capture long press to enable face detection",TextToSpeech.QUEUE_ADD,params);
                        }
                        else{
                            tvvs.stop();
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


                    }

                });
//                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//                bytes= stream.toByteArray();

//


                //cameraSource.takePicture(CameraSource.ShutterCallback, CameraSource.PictureCallback,null);
            }
        });
        cameraView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
//                intent=new Intent(MainActivity.this,Main2Activity.class);
//                Toast.makeText(MainActivity.this, "Uri"+imageUri, Toast.LENGTH_SHORT).show();
//                intent.putExtra("img",bytes1);
//                startActivity(intent);
//                finish();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(!tvvs.isSpeaking()){
                            HashMap<String,String> params=new HashMap<String, String>();
                            params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"sampleText");
                            tvvs.speak("face detection enabled , press on screen to process",TextToSpeech.QUEUE_ADD,params);
                            if(path!=null) {
                                imageuri = Uri.parse(path);
                                Intent intt = new Intent(camera1.this,camera2.class);
                                //Intent intt = new Intent(camera1.this,cloudvision.class);
                                intt.putExtra("uri", imageuri.toString());
                                Toast.makeText(camera1.this, "" + imageuri, Toast.LENGTH_SHORT).show();
                                startActivity(intt);
                                finish();
                            }
                        }
                        else{
                            tvvs.stop();
                        }


                    }
                });

                return false;
            }
        });



        //detector.setProcessor(new MultiProcessor.Builder<Face>());
        //detector.setProcessor(new MultiProcessor.Builder<Face>().build(new GraphicFaceTrackerFactory()));

    }
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
            startActivity(new Intent(camera1.this,MyLocationGetter.class));
            return true;
        }
        return onKeyLongPress(keycode, event);
    }


}
