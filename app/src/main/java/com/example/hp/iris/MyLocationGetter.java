package com.example.hp.iris;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MyLocationGetter extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ActivityCompat.OnRequestPermissionsResultCallback ,TextToSpeech.OnInitListener,TextToSpeech.OnUtteranceCompletedListener{

    GoogleApiClient googleApiClient;
//    TextView tv,a;
    RelativeLayout b;
    Location location;
    String loc="";
    TextToSpeech myTTS;
    private int MY_DATA_CHECK_CODE = 0;

    double latitude, longitude;
    Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.mylocationgetter);
        Intent checkTTSIntent = new Intent();
        checkTTSIntent
                .setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);
        handler= new Handler();
        Thread logoTimer = new Thread() {
            public void run() {
                try {
                    try {
                        //speakWords("Click to hear the name ");
                        sleep(0);
                        speakWords("Click to continue");

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
        b = (RelativeLayout) findViewById(R.id.relativelayout);
       // tv = (TextView) findViewById(R.id.address);
       // a=(TextView)findViewById(R.id.addr);
        buildgoogleapiclient();



        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(MyLocationGetter.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MyLocationGetter.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                getlocation();
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                getAddress();
               // tv.setText("Latitude :" + latitude +"\n Longitude : "+ longitude);
               // a.setText(loc);
                try{
                    SmsManager sms=SmsManager.getDefault();
                    if(loc.equals(null)){
                        sms.sendTextMessage("9952478932",null,"i am in a emergency at "+"\n Latitude: "+latitude+"\n Longitude :"+longitude,null,null);
                    }
                    else{
                        sms.sendTextMessage("9952478932",null,"i am in a emergency at "+ loc +"\n Latitude: "+latitude+"\n Longitude :"+longitude,null,null);
                    }
                    speakWords("Message sent");
                    finish();
                }catch(Exception e)
                {
                    speakWords("Again click on the screen");
                }
                Toast.makeText(MyLocationGetter.this,loc, Toast.LENGTH_SHORT).show();
               // tv.setText(loc);
            }
        });}

    @Override
    protected void onDestroy() {
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

    public void getlocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
    }
    public Address getAddress(double latitude, double longitude)
    {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude,longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            return addresses.get(0);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }


    public void getAddress() {

        Address locationAddress = getAddress(latitude, longitude);

        if (locationAddress != null) {
            String address = locationAddress.getAddressLine(0);
            String address1 = locationAddress.getAddressLine(1);
            String city = locationAddress.getLocality();
            String state = locationAddress.getAdminArea();
            String country = locationAddress.getCountryName();
            String postalCode = locationAddress.getPostalCode();

            StringBuilder stringBuilder=new StringBuilder();

            if(!address.equals(null)){stringBuilder.append(address);}
            //if(!address1.equals(null)){stringBuilder.append(address1);}
            if(!city.equals(state)){if(!city.equals(null)){stringBuilder.append(" "+city);}}
            if(!state.equals(null)){stringBuilder.append( " " +state);}
            if(!country.equals(null)){stringBuilder.append(" "+country);}
            if(!postalCode.equals(null)){stringBuilder.append(" "+postalCode);}

            loc=stringBuilder.toString();


        }
    }

        protected synchronized void buildgoogleapiclient(){

        googleApiClient = new GoogleApiClient.Builder(this).addOnConnectionFailedListener(this).addConnectionCallbacks(this).addApi(LocationServices.API).build();

        googleApiClient.connect();

        LocationRequest locationRequest=new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(500);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder locationSettingsRequest=new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        PendingResult<LocationSettingsResult>  result=LocationServices.SettingsApi.checkLocationSettings(googleApiClient, locationSettingsRequest.build());






    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getlocation();

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
      googleApiClient.connect();
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
}
