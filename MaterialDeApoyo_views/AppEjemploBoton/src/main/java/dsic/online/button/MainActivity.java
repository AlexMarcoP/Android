package dsic.online.button;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {

    private Button miBoton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        miBoton = findViewById(R.id.MiBoton);

    }

    public void ejecutaAccion(View v) {
        //Descomenta de las siguientes según desees
        //1. La siguiente línea muestra en el botón la hora
        //miBoton.setText(new Date().toString());

        //2. Si descomentas la siguiente línea el botón lanzará a ejecución la actividad EmptyLayoutActivity
        //   que muestra el ejemplo de cómo introducir dinámicamente un boton en un layout vacio
        //startActivity(new Intent(getApplicationContext(),EmptyLayoutActivity.class));

        //3. Si descomentas la siguiente línea el botón lanzará a ejecución la actividad SpinnerActivity
        //   el código de dicha actividad muestra un ejemplo de Spinner
        //startActivity(new Intent(getApplicationContext(),SpinnerActivity.class));
    }

}
