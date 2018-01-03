package com.example.hp.iris;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.example.hp.iris.MainActivity.c;

/**
 * Created by HP on 8/5/2017.
 */

public class contacts2 extends AppCompatActivity implements TextToSpeech.OnInitListener,TextToSpeech.OnUtteranceCompletedListener
{
    public contacts2() {

    }

    public ArrayList<String> StoreContacts ;
    ArrayAdapter<String> arrayAdapter ;
    Cursor cursor;
    String name, phonenumber ;
    String n,ph;
    TextView textView;
    TextToSpeech tvvs;
    TextToSpeech myTTS;
    private int MY_DATA_CHECK_CODE = 0;
    public  static final int RequestPermissionCode  = 1 ;
    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.share);
        Intent checkTTSIntent = new Intent();
        checkTTSIntent
                .setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);
        handler= new Handler();
        Thread logoTimer = new Thread() {
            public void run() {
                try {
                    try {
                        sleep(500);

                        //speakWords("Click to hear the name ");
                        sleep(500);
                        speakWords("Click to hear the name  and Long press to hear and call them");
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
        EnableRuntimePermission();

        final ListView listView=(ListView)findViewById(R.id.listView);
        tvvs=new TextToSpeech(contacts2.this,contacts2.this);
        StoreContacts = new ArrayList<String>();


        GetContactsIntoArrayList();

        arrayAdapter = new ArrayAdapter<String>(
               this,
                R.layout.share_1, StoreContacts
        );
        System.out.print("Store"+StoreContacts.size());
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                textView=(TextView)view.findViewById(R.id.textView13);
                String name=textView.getText().toString();
                String[] s=name.split(":");
                n=s[0].trim();
                ph=s[1].trim();

                if(!tvvs.isSpeaking()){
                    HashMap<String,String> params=new HashMap<String, String>();
                    params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"sampleText");
                    tvvs.speak(n,TextToSpeech.QUEUE_ADD,params);
                }
                else{
                    tvvs.stop();
                }

                Toast.makeText(contacts2.this, ph +position, Toast.LENGTH_SHORT).show();
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                textView=(TextView)view.findViewById(R.id.textView13);
                String name=textView.getText().toString();
                String[] s=name.split(":");
                n=s[0].trim();
                ph=s[1];
                ph.trim();
                if(!tvvs.isSpeaking()){
                    HashMap<String,String> params=new HashMap<String, String>();
                    params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"sampleText");
                    tvvs.speak("Calling"+n,TextToSpeech.QUEUE_ADD,params);
                }
                else{
                    tvvs.stop();
                }
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent=new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:"+ph));
                        startActivity(intent);
                        finish();

                    }
                },2000);

                //Toast.makeText(contacts2.this, "Call :"+ ph, Toast.LENGTH_SHORT).show();
                return false;
            }
        });


    }



        public void GetContactsIntoArrayList () {
            try {
                cursor =getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null, null, null);

                while (cursor.moveToNext()) {

                    name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

                    phonenumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                    StoreContacts.add(name + " " + ":" + " " + phonenumber);
                }

                cursor.close();
            }
            catch (Exception e) {

                Toast toast = Toast.makeText(this,"Enable permissions in settings", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();

            }



    }


    public void EnableRuntimePermission(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.READ_CONTACTS))
        {

            Toast.makeText(this,"CONTACTS permission allows us to Access CONTACTS app", Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(this,new String[]{
                    Manifest.permission.READ_CONTACTS}, RequestPermissionCode);

        }
    }

    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] PResult) {

        switch (RC) {

            case RequestPermissionCode:

                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this,"Permission Granted, Now your application can access CONTACTS.", Toast.LENGTH_LONG).show();

                } else {

                    Toast.makeText(this,"Permission Canceled, Now your application cannot access CONTACTS.", Toast.LENGTH_LONG).show();

                }
                break;
        }
    }
    @Override
    protected void onDestroy() {
        if (tvvs != null) {
            tvvs.stop();
            tvvs.shutdown();

            tvvs = null;

        }
        if(myTTS != null) {

            myTTS.stop();
            myTTS.shutdown();
            //Log.d(TAG, "TTS Destroyed");
        }
        super.onDestroy();
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

    }


    @Override
    public void onUtteranceCompleted(String utteranceId) {

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
    public boolean onKeyLongPress(int keycode,KeyEvent event){
        if(keycode==KeyEvent.KEYCODE_VOLUME_UP){
            startActivity(new Intent(contacts2.this,MyLocationGetter.class));
            return true;
        }
        return onKeyLongPress(keycode, event);
    }


}



