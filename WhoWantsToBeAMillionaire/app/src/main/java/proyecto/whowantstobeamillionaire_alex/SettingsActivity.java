package proyecto.whowantstobeamillionaire_alex;

import android.app.Activity;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class SettingsActivity extends AppCompatActivity {

        final int DONT_LOCATE = 0;
        final int LOCATE = 1;

        int state = DONT_LOCATE;

        EditText etLongitude = null;
        EditText etLatitude = null;
        LocationManager locationManager = null;
        MyLocationListener myLocationListener = null;
        String locationProvider;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_settings);

            etLongitude = findViewById(R.id.longitude);
            etLatitude = findViewById(R.id.latitude);

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
        @Override
        public boolean onCreateOptionsMenu(Menu menu){
            getMenuInflater().inflate(R.menu.settings_menu, menu);
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
            }
            supportInvalidateOptionsMenu();
            return super.onOptionsItemSelected(item);
        }

        @Override
        public void onPause() {
            super.onPause();
            SharedPreferences _prefs = getSharedPreferences("MisPreferencias", Activity.MODE_PRIVATE);

            if (_prefs == null) return;

            SharedPreferences.Editor _prefsEditor = _prefs.edit();
            if (_prefsEditor == null) return;

            _prefsEditor.putString("TAG_NAME", ((EditText)findViewById(R.id.name_settings)).getText().toString());
            _prefsEditor.putString("TAG_HELP", ((EditText)findViewById(R.id.help)).getText().toString());
            _prefsEditor.putString("TAG_LONGITUDE", ((EditText)findViewById(R.id.longitude)).getText().toString());
            _prefsEditor.putString("TAG_LATITUDE", ((EditText)findViewById(R.id.latitude)).getText().toString());
            _prefsEditor.putString("TAG_FRIENDS_NAME", ((EditText)findViewById(R.id.friend)).getText().toString());
            _prefsEditor.commit();
        }
        @Override
        protected void onResume() {
            super.onResume();
            SharedPreferences _prefs = getSharedPreferences("MisPreferencias", MODE_PRIVATE);

            if(_prefs==null)return;

                ((EditText)findViewById(R.id.name_settings)).setText(_prefs.getString("TAG_NAME","def"));
                ((EditText)findViewById(R.id.help)).setText(_prefs.getString("TAG_HELP","3"));
                ((EditText)findViewById(R.id.longitude)).setText(_prefs.getString("TAG_LONGITUDE","-3.70325"));
                ((EditText)findViewById(R.id.latitude)).setText(_prefs.getString("TAG_LATITUDE","40.4167"));
                ((EditText)findViewById(R.id.friend)).setText(_prefs.getString("TAG_FRIENDS_NAME","AMP"));

        }

        public String getName(){
            EditText textName = findViewById(R.id.name_settings);
            return textName.getText().toString();
        }
        public String getLongitude(){
            EditText textLong = findViewById(R.id.longitude);
            return textLong.getText().toString();
        }
        public String getLatitude(){
            EditText textLatitud = findViewById(R.id.latitude);
            return textLatitud.getText().toString();
        }

        public void AddFriend(View view){
            EditText texto = findViewById(R.id.friend);

            //Si no han introducido el nombre del amigo
            if(texto.getText().toString().isEmpty()){
                Toast.makeText(this, R.string.enterName, Toast.LENGTH_SHORT).show();
            }
            else{
                //Recuperamos los valores nombre y nombre del amigo de preferences
                SharedPreferences sharedPref = getSharedPreferences("MisPreferencias", MODE_PRIVATE);

                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("TAG_NAME", ((EditText)findViewById(R.id.name_settings)).getText().toString());
                editor.putString("TAG_FRIENDS_NAME", ((EditText)findViewById(R.id.friend)).getText().toString());
                editor.commit();
                //Enviamos el nombre del amigo al servidor
                sendFriendNameToServer task = new sendFriendNameToServer();
                task.execute();
            }
        }

        //SUBCLASES
        private class sendFriendNameToServer extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... params) {
                try {

                    SharedPreferences preferences = getSharedPreferences("MisPreferencias", MODE_PRIVATE);

                    String name = preferences.getString("TAG_NAME", "def");
                    String friend_name = preferences.getString("TAG_FRIENDS_NAME", "NoFriendName");

                    // Formamos el String body
                    String arg1= URLEncoder.encode("name", "UTF-8");
                    String arg2= URLEncoder.encode(name, "UTF-8");
                    String arg3= URLEncoder.encode("friend_name", "UTF-8");
                    String arg4= URLEncoder.encode(friend_name, "UTF-8");

                    String body=arg1+"="+arg2+"&"+arg3+"="+arg4;

                    // Peticion POST
                    //URL url = new URL("https://wwtbam-1076.appspot.com/_ah/api/friends/v1/add");
                    URL url = new URL("https://wwtbamandroid.appspot.com/rest/friends");


                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setDoOutput(true);
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
                    writer.write(body);
                    writer.close();

                    connection.getInputStream();

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }

        }

        // TODO: Create a new private class implementing LocationListener
        private class MyLocationListener implements LocationListener {

            @Override
            public void onLocationChanged(Location location) {
                etLongitude.setText(String.valueOf(location.getLongitude()));
                etLatitude.setText(String.valueOf(location.getLatitude()));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        }
}
