package br.com.bossini.realizacheckinfatecipinoite;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity{

    private Activity essaActivity;
    private Button checkinButton;
    private GoogleMap mMap = null;
    private static final int REQUEST_IMAGE_CAPTURE = 200;
    private static final int REQUEST_PERMISSION_GPS = 1068;
    private static final int REQUEST_PERMISSION_CAMERA = 1168;
    private Location currentLocation = null;
    private LatLng currentLatLng = null;
    private LocationManager locationManager = null;
    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        essaActivity = this;
        checkinButton =
                (Button) findViewById(R.id.checkinButton);
        checkinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mMap == null)
                    Toast.makeText
                            (essaActivity,
                                    getString(R.string.mapa_indisponivel),
                                    Toast.LENGTH_SHORT).show();

                else{
                    if (currentLocation == null){
                        Toast.makeText(essaActivity,
                                getString(R.string.gps_indisponivel),
                                Toast.LENGTH_SHORT).show();
                        currentLatLng = new LatLng(-23.5631338 , -46.6543286);
                    }
                    else{
                        double latitude = currentLocation.getLatitude();
                        double longitude = currentLocation.getLongitude();
                        currentLatLng = new LatLng (latitude, longitude);
                    }
                    tirarFotoEColocarNoMapa();
                }
            }
        });
        locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        FragmentManager fm = getSupportFragmentManager();

        SupportMapFragment mapFragment =
                (SupportMapFragment)fm.findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                // Add a marker in Sydney and move the camera
                /*LatLng sydney = new LatLng(-34, 151);
                mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                */
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        String nomeUsuario = "Ana";
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            //não tem permissão ainda
            if (ActivityCompat.
                    shouldShowRequestPermissionRationale(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)){
                Toast.makeText(this, getString(R.string.explicacao_gps, nomeUsuario), Toast.LENGTH_SHORT).show();
            }
            ActivityCompat.requestPermissions(this,
                    new String []
                            {Manifest.
                                    permission.
                                    ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSION_GPS);
        }
        else{
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 0, 0, locationListener
            );
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        locationManager.removeUpdates(locationListener);
    }

    private void tirarFotoEColocarNoMapa(){
        Intent intencaoDeTirarFoto =
                new Intent (MediaStore.ACTION_IMAGE_CAPTURE);
        if (intencaoDeTirarFoto.resolveActivity(getPackageManager())
                != null){
            if (ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.CAMERA) !=
                            PackageManager.PERMISSION_GRANTED){
                if (ActivityCompat.shouldShowRequestPermissionRationale
                        (this, Manifest.permission.CAMERA)){
                    Toast.makeText(essaActivity,
                            getString(R.string.explicacao_camera),
                            Toast.LENGTH_SHORT).show();
                }
                ActivityCompat.requestPermissions(this,
                        new String []{Manifest.permission.CAMERA},
                        REQUEST_PERMISSION_CAMERA);

            }
            else{
                startActivityForResult(intencaoDeTirarFoto,
                        REQUEST_IMAGE_CAPTURE);
            }

        }
        else{
            Toast.makeText(essaActivity,
                    getString(R.string.app_de_foto_indisponivel),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE){
            if (resultCode == Activity.RESULT_OK){
                Bitmap foto = (Bitmap) data.getExtras().get ("data");
                mMap.addMarker( new MarkerOptions().
                title(getString(R.string.estou_aqui)).
                        position(currentLatLng).
                        icon (BitmapDescriptorFactory.
                                fromBitmap(foto)));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng));
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_PERMISSION_GPS:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if (ActivityCompat.checkSelfPermission(
                            this, Manifest.
                                    permission.
                                    ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED){
                        locationManager.
                                requestLocationUpdates(
                                        LocationManager.GPS_PROVIDER,
                                        0,
                                        0,
                                        locationListener
                                );
                    }
                }
                else{
                    Toast.makeText(essaActivity,
                            getString(R.string.explicacao_gps),
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_PERMISSION_CAMERA:
                if (grantResults.length > 0 && grantResults[0]
                        == PackageManager.PERMISSION_GRANTED){
                    tirarFotoEColocarNoMapa();
                }
                break;
        }
    }
}
