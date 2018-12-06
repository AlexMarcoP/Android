package dsic.online.geolocation;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    final int DONT_LOCATE = 0;
    final int LOCATE = 1;

    int i = 1;

    int state = DONT_LOCATE;

    EditText etLongitude = null;
    EditText etLatitude = null;
    TextView tvAddress = null;
    LocationManager locationManager = null;
    MyLocationListener myLocationListener = null;
    String locationProvider;

    GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mymap);
        mapFragment.getMapAsync(this);

        etLongitude = findViewById(R.id.etLongitude);
        etLatitude = findViewById(R.id.etLatitude);
        tvAddress = findViewById(R.id.tvAddress);

        state = DONT_LOCATE;

        // TODO: Get references to the LocationManager and our own defined LocationListener
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            locationProvider = LocationManager.GPS_PROVIDER;
        else {
            locationProvider = LocationManager.NETWORK_PROVIDER;
        }

        myLocationListener = new MyLocationListener();
    }

    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(39.472864, -0.401238)));
        googleMap.moveCamera(CameraUpdateFactory.zoomIn());
        this.googleMap = googleMap;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);

        switch (state) {
                case DONT_LOCATE:
                menu.findItem(R.id.action_locate).setVisible(true);
                menu.findItem(R.id.action_dont_locate).setVisible(false);
                break;
            case LOCATE:
                menu.findItem(R.id.action_locate).setVisible(false);
                menu.findItem(R.id.action_dont_locate).setVisible(true);
                break;
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_locate:
                state = LOCATE;

                // TODO: Request location updates from the LocationManager
                try {
                    locationManager.requestLocationUpdates(locationProvider, 0, 0, myLocationListener);
                } catch (SecurityException securityEx) {
                    Log.e("[ERROR]", "Couldn't get location updates. Check your GPS/Network provider: " +
                            securityEx.getMessage());
                    return false;
                }
                break;
            case R.id.action_dont_locate:
                state = DONT_LOCATE;

                // TODO: Stop receiving location updates
                locationManager.removeUpdates(myLocationListener);
                break;
            case R.id.action_add_marker:

                if (etLatitude.getText() != null && etLongitude.getText() != null) {
                    MarkerOptions markerOptions =  new MarkerOptions();
                    LatLng locationLatLng = new LatLng(Double.valueOf(etLatitude.getText().toString()), Double.valueOf(etLongitude.getText().toString())) ;
                    markerOptions.position(locationLatLng);
                    markerOptions.title("Marker "+i);
                    i++;
                    markerOptions.snippet(etLongitude.getText().toString() + " , " + etLatitude.getText().toString());
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    this.googleMap.addMarker(markerOptions);
                    this.googleMap.setInfoWindowAdapter(new LocationInfoAdapter(getLayoutInflater()));

                    new LocationTranslationTask().execute();
                } else {
                    Toast.makeText(this, "No location to mark", Toast.LENGTH_SHORT).show();
                    return true;
                }
                break;
        }
        supportInvalidateOptionsMenu();
        return true;
    }

    // TODO: Create a new private class implementing LocationListener
    private class MyLocationListener implements LocationListener {

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.w("[WARNING]","GPS Status changed. Provider: " + provider + " status " + status);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.i("[INFO]", "Provider enabled: " + provider);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.w("[WARNING]","Provider disabled: " + provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            etLongitude.setText(String.valueOf(location.getLongitude()));
            etLatitude.setText(String.valueOf(location.getLatitude()));
            Log.d("[DEBUG]", location.toString());
            new LocationTranslationTask().execute();
        }
    }
    // TODO: Create an AsyncTask to translate latitude and longitude into a human readable address using Geocoder
    private class LocationTranslationTask extends AsyncTask<Void, Void, List<Address>> {

        @Override
        protected List<Address> doInBackground(Void... params) {

            Geocoder geocoder = new Geocoder(getApplicationContext());
            try {

                List<Address> addresses = geocoder.getFromLocation(Double.valueOf(etLatitude.getText().toString()),
                        Double.valueOf(etLongitude.getText().toString()),
                        5);
                return addresses;

            } catch (IOException ioEx) {
                Log.e("ERROR", "Error while trying to translate location: " + ioEx.getMessage());
                ioEx.printStackTrace();
                return null;
            } catch (IllegalArgumentException argEx) {
                Log.e("ERROR", "Error while trying to translate location: " + argEx.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Address> addresses) {
            super.onPostExecute(addresses);
            if (addresses != null) {
                for (Address address: addresses) {
                    tvAddress.setText(address.getLocality() + "," + address.getCountryName());
                }
            }
        }
    }
}
