package dsic.online.button;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class SpinnerActivity extends AppCompatActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spinner_layout);


        Spinner spOpciones = findViewById(R.id.spOpciones);

        ArrayAdapter<CharSequence> adaptador = ArrayAdapter.createFromResource(
                this, R.array.dias_laborales, android.R.layout.simple_spinner_item);

        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spOpciones.setAdapter(adaptador);


        spOpciones.setOnItemSelectedListener(new OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> padre, View vista,
                                       int pos, long id) {

                int index = padre.getSelectedItemPosition();

                String[] diasLab = getResources().getStringArray(R.array.dias_laborales);
                //Acceso al elemento seleccionado a través de diasLab[pos] o diasLab[index]

                Toast.makeText(getBaseContext(), "Has elegido : " + diasLab[pos],
                        Toast.LENGTH_SHORT).show();

            }

            public void onNothingSelected(AdapterView<?> arg0) {
                // no hacer nada
            }


        });


    }

}
