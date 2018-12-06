package upv.ejercicios.modulo3_2ciclodevida;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class CicloVida extends AppCompatActivity {
    StringBuilder mensajes = new StringBuilder();
    TextView visor;

    private void log(String text){
        Log.d("CicloVida",text);
        mensajes.append(text);
        mensajes.append("\n");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        visor = new TextView(this);
        visor.setText(mensajes.toString());
        setContentView(visor);
        log("Se ha llamado onCreate()");
    }
    @Override
    protected void onResume(){
        super.onResume();
        log("Se ha llamado onResume()");
    }
    @Override
    protected void onPause(){
        super.onPause();
        log("Se ha llamado onPause()");
    }
    @Override
    protected void onStop(){
        super.onStop();
        log("Se ha llamado onStop()");
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        log("Se ha llamado onDestroy()");
    }

}
