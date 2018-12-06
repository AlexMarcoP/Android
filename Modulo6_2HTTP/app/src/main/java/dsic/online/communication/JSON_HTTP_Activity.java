package dsic.online.communication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.lang.ref.WeakReference;

import dsic.online.communication.classes.InternetConnectionChecker;
import dsic.online.communication.classes.JSONWeatherParser;
import dsic.online.communication.classes.WeatherHttpClient;
import dsic.online.communication.classes.WeatherInformation;
import dsic.online.communication.classes.WeatherInformationParaGSON;

public class JSON_HTTP_Activity extends AppCompatActivity {


    private TextView cityText;
    private TextView condDescr;
    private TextView temp;
    private TextView press;
    private TextView windSpeed;
    private TextView windDeg;

    private TextView hum;
    private ImageView imgView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (new InternetConnectionChecker().checkInternetConnection(this)) {
            setContentView(R.layout.activity_json_http);
            String city = "Valencia,ES";
            int infotype = WeatherInformation.GSON;

            cityText = findViewById(R.id.cityText);
            condDescr = findViewById(R.id.condDescr);
            temp = findViewById(R.id.temp);
            hum = findViewById(R.id.hum);
            press = findViewById(R.id.press);
            windSpeed = findViewById(R.id.windSpeed);
            windDeg = findViewById(R.id.windDeg);
            imgView = findViewById(R.id.condIcon);

            ObtenerTiempoTask tiempoTask = new ObtenerTiempoTask(this, infotype);
            tiempoTask.execute(city);

        } else {
            new AlertDialog.Builder(this).
                    setTitle("Problema de conectividad").
                    setMessage("Por favor, inténtelo más tarde").
                    setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).show();
        }
    }


    private static class ObtenerTiempoTask extends AsyncTask<String, byte[], WeatherInformation> {

        ObtenerTiempoTask(JSON_HTTP_Activity activity, int infotype) {
            this.activity = new WeakReference<>(activity);
            this.infoType = infotype;
        }

        private int infoType;
        private WeakReference<JSON_HTTP_Activity> activity;

        @Override
        protected WeatherInformation doInBackground(String... params) {

            WeatherInformation weather = new WeatherInformation();

            // 1. Realizamos la petición y obtenemos los datos
            String data = ((new WeatherHttpClient()).getWeatherData(params[0]));

            try {
                // 2. Parseamos los datos recibidos

                if (this.infoType == WeatherInformation.GSON) {
                    Gson gson = new Gson();
                    JSONObject datos = new JSONObject(data);
                    weather.setInformation(gson.fromJson(datos.toString(), WeatherInformationParaGSON.class));
                } else if (this.infoType == WeatherInformation.JSON) {
                    weather.setInformation(JSONWeatherParser.getWeather(data));
                } else {
                    throw new Exception();
                }


                String iconName = weather.getIconName();
                if (iconName != null) {
                    byte[] iconData = (new WeatherHttpClient()).getImage(iconName);
                    if (iconData != null) this.publishProgress(iconData);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return weather;

        }

        @Override
        protected void onProgressUpdate(byte[]... iconDataArray) {
            if (activity.get() != null) {
                //Sólo necesitamos el primer elemento del Array;
                byte[] iconData = iconDataArray[0];

                if ((iconData != null) && (iconData.length > 0)) {
                    Bitmap img = BitmapFactory.decodeByteArray(iconData, 0, iconData.length);
                    activity.get().imgView.setImageBitmap(img);
                }
            }
        }

        @Override
        protected void onPostExecute(WeatherInformation weather) {
            super.onPostExecute(weather);

            if (activity.get() != null) {
                activity.get().cityText.setText(String.format("%1s, %2s", weather.getCity(), weather.getCountry()));
                activity.get().condDescr.setText(String.format("%1s (%2s)", weather.getCondition(), weather.getDescription()));
                activity.get().temp.setText(String.format("%1sºC", weather.getTemperature()));
                activity.get().hum.setText(String.format("%1s%%", weather.getHumidity()));
                activity.get().press.setText(String.format("%1s hPa", weather.getPressure()));
                activity.get().windSpeed.setText(String.format("%1s mps", weather.getWindSpeed()));
                activity.get().windDeg.setText(String.format("%1sº", weather.getWindDeg()));
            }
        }

    }

}
