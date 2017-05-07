package com.gabrielaangebrandt.gdjejegabriela;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {

    private static final String TAG = "ggg";
    private static final int REQUEST_IMAGE_CAPTURE = 1 ;
    private static final int REQUEST_LOCATION_PERMISSION = 10;
    TextView tvLoc;
    Button snimi;
    File image;
    Uri uri;

    LocationListener locationListener;
    LocationManager locationManager;
    GoogleMap googleMap;
    MapFragment mapFragment;
    Marker marker;
    String naziv_grad, naziv_drzava;
    private GoogleMap.OnMapClickListener mCustomOnMapClickListener;
    NotificationManagerCompat notificationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setUp();
        this.locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        this.locationListener = new SimpleLocationListener();
        this.notificationManager = NotificationManagerCompat.from(this);

    }

    private void setUp() {
        this.mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.fragment);
        this.mapFragment.getMapAsync(this);
        this.mCustomOnMapClickListener = new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {
                if (marker != null) {
                    marker.remove();
                }

                marker = googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)).position(new LatLng(latLng.latitude,
                        latLng.longitude)));
                playSound();
                Location loc = new Location(LocationManager.GPS_PROVIDER);
                loc.setLatitude(latLng.latitude);
                loc.setLongitude(latLng.longitude);
                updateLocationDisplay(loc);
            }
        };
        this.tvLoc = (TextView) findViewById(R.id.tvLocation);
        this.snimi = (Button) findViewById(R.id.bSnimi);
        snimi.setOnClickListener(this);

    }

    //postavke karte
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        UiSettings uiSettings = this.googleMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);
        uiSettings.setZoomGesturesEnabled(true);
        this.googleMap.setOnMapClickListener(this.mCustomOnMapClickListener);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        this.googleMap.setMyLocationEnabled(true);
    }

   @Override
    protected void onStart() {
        super.onStart();
        if (!hasLocationPermission()) {
            requestPermission();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (this.hasLocationPermission()) {
            startTracking();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopTracking();
    }

    private void startTracking() {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        String locationProvider = this.locationManager.getBestProvider(criteria, true);
        long minTime = 1000;
        float minDistance = 10;
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
        this.locationManager.requestLocationUpdates(locationProvider, minTime, minDistance, this.locationListener);
    }

    private void stopTracking() {
        this.locationManager.removeUpdates(this.locationListener);
    }

    private void updateLocationDisplay(Location location){
        String message ="Geografska širina: " + location.getLatitude() + "\nGeografska dužina:" + location.getLongitude() +"\n";
        tvLoc.setText(message);

        if(Geocoder.isPresent()){
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> address = geocoder.getFromLocation(
                        location.getLatitude(), location.getLongitude(),1);
                if(address.size() > 0) {
                    StringBuilder stringBuilder = new StringBuilder();
                    Address blizu = address.get(0);
                    stringBuilder.append(blizu.getAddressLine(0))
                            .append("\n")
                            .append(blizu.getLocality())
                            .append("\n")
                            .append(blizu.getCountryName());
                    tvLoc.append(stringBuilder.toString());

                    naziv_grad = blizu.getLocality();
                    naziv_drzava = blizu.getCountryName();
                }
            } catch (IOException e) { e.printStackTrace(); }
        }
    }
    private boolean hasLocationPermission(){
        String LocPermission = Manifest.permission.ACCESS_FINE_LOCATION;
        int status = ContextCompat.checkSelfPermission(this,LocPermission);
        return status == PackageManager.PERMISSION_GRANTED;
    }
    private void requestPermission(){
        String[] permissions = new String[]{ Manifest.permission.ACCESS_FINE_LOCATION };
        ActivityCompat.requestPermissions(this,
                permissions, REQUEST_LOCATION_PERMISSION);
    }


    private class SimpleLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            updateLocationDisplay(location);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) { }

        @Override
        public void onProviderEnabled(String s) { }

        @Override
        public void onProviderDisabled(String s) { }
    }

//-----------------------------------------------------------ZVUK PRI STAVLJANJU MARKERA----------------------------------------


    private void playSound() {

        SoundPool soundPool = new SoundPool(100, AudioManager.STREAM_MUSIC, 0);
        soundPool.load(this, R.raw.sound, 1);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                soundPool.play(sampleId, 1, 1, 1, 0, 1f);
            }
        });

    }

    //---------------------------------------------------------INTENT ZA KAMERU ---------------------------------------------


    @Override
    public void onClick(View view) {

        Intent imageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File picFolder = Environment.getExternalStorageDirectory();

        if(naziv_drzava== null && naziv_grad==null)
        {
           Toast.makeText(this, "Nije očitana lokacija.", Toast.LENGTH_LONG).show();
        }
        else if(naziv_grad == null) {

            image = new File(picFolder, naziv_drzava + ".png");
            uri = Uri.fromFile(image);
            Log.d(TAG, "ovo je uri" + uri);
            imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(imageIntent, REQUEST_IMAGE_CAPTURE);
        }
        else {
            image = new File(picFolder, naziv_grad + "," + naziv_drzava + ".png");

            uri = Uri.fromFile(image);
            Log.d(TAG, "ovo je uri" + uri);
            imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(imageIntent, REQUEST_IMAGE_CAPTURE);

        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_IMAGE_CAPTURE){
            switch (resultCode){
                case RESULT_OK:
                    if(image.exists()){
                        Toast.makeText(this, "Slika je spremljena u : " + image.getAbsolutePath(), Toast.LENGTH_LONG).show();
                        sendNotification(image.getAbsolutePath());

                        Log.d(TAG, "Abolute path: " + image.getAbsolutePath());
                    } else{
                        Toast.makeText(this, "Slika nije spremljena", Toast.LENGTH_LONG).show();
                    }
                    break;
                case RESULT_CANCELED:
                    File image1 = new File(image.getAbsolutePath());
                    image1.delete();
                    break;
            }

        } }

    //------------------------------------------------------------- NOTIFIKACIJE --------------------------------------------


    private void sendNotification(String path) {
        Intent notificationIntent = new Intent();
        notificationIntent.setAction(Intent.ACTION_VIEW);
        notificationIntent.setDataAndType(Uri.parse("file://"  + path), "image/*");

        PendingIntent notificationPendingIntent = PendingIntent.getActivity(this,0,notificationIntent,PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder.setAutoCancel(true)
                .setContentTitle("Slika spremljena. Želite li pogledati fotografiju?")
                .setContentText("Klikni za prikaz.")
                .setSmallIcon(android.R.drawable.ic_dialog_email)
                .setContentIntent(notificationPendingIntent)
                .setLights(Color.BLUE, 2000, 1000)
                .setVibrate(new long[]{1000,1000,1000,1000,1000})
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        Notification notification = notificationBuilder.build();
        notificationManager.notify(0,notification);
        this.finish();
    }

}