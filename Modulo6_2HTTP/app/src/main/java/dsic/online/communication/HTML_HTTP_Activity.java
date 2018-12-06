package dsic.online.communication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.webkit.WebView;
import android.widget.TextView;

import dsic.online.communication.classes.InternetConnectionChecker;
import dsic.online.communication.classes.WeatherHttpClient;

public class HTML_HTTP_Activity extends AppCompatActivity {

    private WebView webView;
    private TextView textview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (new InternetConnectionChecker().checkInternetConnection(this)) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_html_http);
            String city = "Valencia,ES";
            ObtenerTiempoHTMLTask htmlWeatherTask = new ObtenerTiempoHTMLTask();
            htmlWeatherTask.execute(city);
            this.webView = findViewById(R.id.webView);
            this.textview = findViewById(R.id.cityText);
            textview.setText(city);
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

    private class ObtenerTiempoHTMLTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            String data = ((new WeatherHttpClient()).getWeatherData(params[0]));
            return data;
        }

        @Override
        protected void onPostExecute(String s) {

            webView.loadData(s,"text/html", null);
        }
    }
}