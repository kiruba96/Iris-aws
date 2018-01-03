package com.example.hp.iris;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequest;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class cloudvision extends AppCompatActivity implements  TextToSpeech.OnInitListener,TextToSpeech.OnUtteranceCompletedListener {

    Bitmap bitmap2,bitmap3;
    ImageView img;
    Uri image;
    RelativeLayout relative3;
    TextToSpeech tvvs2;
    ArrayList<String> arrayList=new ArrayList<String>();
    private static final String CLOUD_VISION_API_KEY = "AIzaSyADtvO9_3vK3TkkHW5imY4t25AXaE8Gkis";
    public static final String FILE_NAME = "temp.jpg";
    private static final String ANDROID_CERT_HEADER = "X-Android-Cert";
    private static final String ANDROID_PACKAGE_HEADER = "X-Android-Package";

    private static final String TAG = cloudvision.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.cloudvision);

        relative3=(RelativeLayout)findViewById(R.id.relative3);
        img=(ImageView)findViewById(R.id.imageView);
        image=Uri.parse(getIntent().getStringExtra("uri"));
        img.setImageURI(image);
        bitmap2= BitmapFactory.decodeResource(getResources(),R.drawable.photo_preview);
        tvvs2=new TextToSpeech(cloudvision.this,cloudvision.this);
            try {
            bitmap3=scaleBitmapDown( MediaStore.Images.Media.getBitmap(getContentResolver(), image),500);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        click.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Bitmap bitmap =
//                        scaleBitmapDown(bitmap2,200
//                                );
//                //img.setImageBitmap(bitmap);
//                try {
//                    callCloudVision(bitmap3);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
        img.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
//                Bitmap bitmap =
//                        scaleBitmapDown(bitmap2,200);
                try {
                    callCloudVision(bitmap3);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        img.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startActivity(new Intent(cloudvision.this,MainActivity.class));
                return false;
            }
        });

    }
    private void callCloudVision(final Bitmap bitmap) throws IOException
    {
        // Switch text to loading
        //mImageDetails.setText(R.string.loading_message);

        // Do the real work in an async task, because we need to use the network anyway
        new AsyncTask<Object, Void, String>() {
            @Override
            protected String doInBackground(Object... params) {
                try {
                    HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
                    JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

                    VisionRequestInitializer requestInitializer =
                            new VisionRequestInitializer(CLOUD_VISION_API_KEY) {
                                /**
                                 * We override this so we can inject important identifying fields into the HTTP
                                 * headers. This enables use of a restricted cloud platform API key.
                                 */
                                @Override
                                protected void initializeVisionRequest(VisionRequest<?> visionRequest)
                                        throws IOException {
                                    super.initializeVisionRequest(visionRequest);

                                    String packageName = getPackageName();
                                    visionRequest.getRequestHeaders().set(ANDROID_PACKAGE_HEADER, packageName);

                                    String sig = PackageManagerUtils.getSignature(getPackageManager(), packageName);

                                    visionRequest.getRequestHeaders().set(ANDROID_CERT_HEADER, sig);
                                }
                            };

                    Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
                    builder.setVisionRequestInitializer(requestInitializer);

                    Vision vision = builder.build();

                    BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                            new BatchAnnotateImagesRequest();
                    batchAnnotateImagesRequest.setRequests(new ArrayList<AnnotateImageRequest>() {{
                        AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();

                        // Add the image
                        Image base64EncodedImage = new Image();
                        // Convert the bitmap to a JPEG
                        // Just in case it's a format that Android understands but Cloud Vision
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                        byte[] imageBytes = byteArrayOutputStream.toByteArray();

                        // Base64 encode the JPEG
                        base64EncodedImage.encodeContent(imageBytes);
                        annotateImageRequest.setImage(base64EncodedImage);

                        // add the features we want
                        annotateImageRequest.setFeatures(new ArrayList<Feature>()
                        {

                            {
                            Feature labelDetection = new Feature();
                            labelDetection.setType("LABEL_DETECTION");
                            labelDetection.setMaxResults(10);
                            add(labelDetection);
                        }});

                        // Add the list of one thing to the request
                        add(annotateImageRequest);
                    }});

                    Vision.Images.Annotate annotateRequest = vision.images().annotate(batchAnnotateImagesRequest);
                    // Due to a bug: requests to Vision API containing large images fail when GZipped.
                    annotateRequest.setDisableGZipContent(true);
                    Log.d(TAG, "created Cloud Vision request object, sending request");

                    BatchAnnotateImagesResponse response = annotateRequest.execute();
                    return convertResponseToString(response);

                }
                catch (GoogleJsonResponseException e)
                {
                    Log.d(TAG, "failed to make API request because " + e.getContent());
                } catch (IOException e)
                {
                    Log.d(TAG, "failed to make API request because of other IOException " +
                            e.getMessage());
                }
                return "Cloud Vision API request failed. Check logs for details.";
            }

            protected void onPostExecute(String result) {
//                Toast.makeText(cloudvision.this, ""+result, Toast.LENGTH_SHORT).show();
                //mImageDetails.setText(result);
//                if(result!=null) {
//                finish();
//                }
                System.out.println("Array="+arrayList.size());
                Iterator<String> iterator=arrayList.iterator();
                String done="";
                int flag=0,count=0;
                while(iterator.hasNext())
                {       if(iterator.next().equals("text"))
                        {
                            flag=1;
                            count=1;
                        }
//                      if(iterator.next().equals(null)){
//                          startActivity(new Intent(cloudvision.this,MainActivity.class));
//                      }

//                    System.out.println("Values="+iterator.next());
                }
                if(!tvvs2.isSpeaking()){
                    HashMap<String,String> params=new HashMap<String, String>();
                    params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"sampleText");
                    tvvs2.speak(result,TextToSpeech.QUEUE_ADD,params);
                }
                else{
                    tvvs2.stop();
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(count==1)
                    {
                        startActivity(new Intent(cloudvision.this,textscanner.class));
                        finish();
                    }

            }
        }.execute();
    }

    public Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }

    private String convertResponseToString(BatchAnnotateImagesResponse response) {
        String message = "I found these things:\n\n";

        List<EntityAnnotation> labels = response.getResponses().get(0).getLabelAnnotations();
        if (labels != null)
        {
            for (EntityAnnotation label : labels)
            {
                arrayList.add(label.getDescription());
                message += String.format(Locale.US, "%.3f: %s", label.getScore(), label.getDescription());
                message += "\n";
            }
        }
        else
        {
            message += "nothing";
        }

        return message;
    }
    @Override
    protected void onDestroy() {
        if (tvvs2 != null) {
            tvvs2.stop();
            tvvs2.shutdown();

            tvvs2 = null;
        }
        super.onDestroy();
    }

    @Override
    public void onInit(int status)
    {


    }

    @Override
    public void onUtteranceCompleted(String utteranceId)
    {

    }
}
