package proyecto.whowantstobeamillionaire_alex;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

public class ScoresActivity extends AppCompatActivity implements OnMapReadyCallback {

    MyHelper myhelper = new MyHelper(this);
    SQLiteDatabase db;

    GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);

        TabHost host = findViewById(R.id.MyTabHost);
        host.setup();

        //Listener para saber que ha seleccionado una tabla u otra, y poner o no la opcion borrar puntuacion en el menu
        host.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String arg0) {
                invalidateOptionsMenu();
            }
        });

        TabHost.TabSpec spec = host.newTabSpec("Locales");
        spec.setIndicator("Locales");
        spec.setContent(R.id.MyTable);
        host.addTab(spec);

        spec = host.newTabSpec("Amigos");
        spec.setIndicator("Amigos");
        spec.setContent(R.id.MyTable2);
        host.addTab(spec);

        host.setCurrentTabByTag("Locales");

        db = myhelper.getWritableDatabase();

        //Creacion de la tabla de puntuaciones locales
        TableLayout locales = findViewById(R.id.MyTable);

        //Extraemos de la base de datos las puntuaciones obtenidas por el jugador
        db = myhelper.getWritableDatabase();
        String[] columns = {"id", "name", "score", "longitude", "latitude"};
        Cursor mycur = db.query("tblPlayers", columns, null, null, null, null, "id");

        int idCol = mycur.getColumnIndex("id");
        int nameCol = mycur.getColumnIndex("name");
        int scoreCol = mycur.getColumnIndex("score");
        int longitudeCol = mycur.getColumnIndex("longitude");
        int latitudeCol = mycur.getColumnIndex("latitude");

        while (mycur.moveToNext()) {
            columns[0] = mycur.getString(idCol);
            columns[1] = mycur.getString(nameCol);
            columns[2] = mycur.getString(scoreCol);
            columns[3] = mycur.getString(longitudeCol);
            columns[4] = mycur.getString(latitudeCol);

            //Traducimos la localizacion
            //LocationTranslationTask task2 = new LocationTranslationTask();
            //task2.execute(columns[3],columns[4]);
            Geocoder geocoder = new Geocoder(getApplicationContext());
            List<Address> addresses =
                    null;
            try {
                addresses = geocoder.getFromLocation(Double.valueOf(columns[4]), Double.valueOf(columns[3]), 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if ((addresses != null)) {

                    TableRow row2 = new TableRow(this);

                    TextView tv2 = new TextView(this);

                    tv2.setText(columns[1]);
                    row2.addView(tv2);

                    tv2 = new TextView(this);
                    tv2.setText(addresses.get(0).getLocality());
                    row2.addView(tv2);

                    tv2 = new TextView(this);
                    tv2.setText(addresses.get(0).getCountryName());
                    row2.addView(tv2);

                    tv2 = new TextView(this);
                    tv2.setText(columns[2]);
                    row2.addView(tv2);

                    locales.addView(row2);
            }


        }

        //Obtenemos la puntuacion maxima de los jugadores
        //..si tenemos conexion a internet
        if (isConnected()) {
            getFriendScores task = new getFriendScores();
            task.execute();
        } else {
            Toast.makeText(this, getResources().getString(R.string.not_connected),
                    Toast.LENGTH_SHORT).show();
        }

        //Apartado Google Maps
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mymap);
        mapFragment.getMapAsync(this);

    }

    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(39.472864, -0.401238)));
        googleMap.moveCamera(CameraUpdateFactory.zoomIn());
        this.googleMap = googleMap;
    }

    public void createNewMarker(HighScoreList lista){

        //Tabla Amigos
        //Rellenamos la tabla con los datos recibidos del servidor(con GSON)
        HighScore record;
        for (int i = 0; i < lista.getCount(); i++) {

            record = lista.getScores().get(i);

            //En caso en que alguno de los valores del servidor de puntuaciones sea nulo mostramos error
            if(record.longitude == null || record.latitude == null || record.scoring == null || record.name == null ) {
                Toast.makeText(this, "No se ha podido insertar en la tabla el jugador "+ record.getName() + " porque su posicion es nula.", Toast.LENGTH_SHORT).show();
            }else{ //Sino lo mostramos en el mapa con un nuevo Marker
                MarkerOptions markerOptions = new MarkerOptions();
                LatLng locationLatLng = new LatLng(Double.valueOf(record.longitude), Double.valueOf(record.latitude));
                markerOptions.position(locationLatLng);
                markerOptions.title(record.name);
                markerOptions.snippet(record.scoring);
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker());
                this.googleMap.addMarker(markerOptions);
                this.googleMap.setInfoWindowAdapter(new LocationInfoAdapter(getLayoutInflater()));
            }
        }
    }


    public boolean isConnected() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        return ((info != null) && (info.isConnected()));
    }

    public void onClickTab(View view){
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.scores_menu, menu);

        //Si selecciona la tabla Amigos, la opcion del menu en la esquina superior derecha desaparece
        TabHost host = findViewById(R.id.MyTabHost);
        if(host.getCurrentTab()==1) {
            MenuItem amigos = menu.findItem(R.id.scores_delete);
            amigos.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.scores_delete:
                //Eliminar un registro
                db.beginTransaction();

                try{
                    db.delete("tblPlayers", null, null);
                    db.setTransactionSuccessful();
                }
                catch (SQLiteException e){
                }
                finally{
                    db.endTransaction();
                }

                //Los eliminamos visualmente
                TableLayout table = findViewById(R.id.MyTable);
                table.removeAllViews();

                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private class getFriendScores extends AsyncTask<Void, Void, Void> {

        private HighScoreList response;

        @Override
        protected void onPreExecute() {
        }

        @Override
            protected Void doInBackground(Void... params) {
            try {
                SharedPreferences preferences = getSharedPreferences("MisPreferencias", MODE_PRIVATE);
                String name = preferences.getString("TAG_NAME", "def");

                //Formamos el body
                String arg1= URLEncoder.encode("name", "UTF-8");
                String arg2= URLEncoder.encode(name, "UTF-8");
                String body=arg1+"="+arg2;

                //Peticion GET
                //URL url = new URL("https://wwtbam-1076.appspot.com/_ah/api/highscores/v1/friends?" + body);
                URL url = new URL("https://wwtbamandroid.appspot.com/rest/highscores?" + body);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.connect();

                InputStreamReader reader = new InputStreamReader(connection.getInputStream());
                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
                response = gson.fromJson(reader, HighScoreList.class);

                connection.disconnect();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(Void params) {

            createNewMarker(response);
        }
    }

}
