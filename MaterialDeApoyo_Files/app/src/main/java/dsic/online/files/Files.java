package dsic.online.files;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class Files extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files);

        try {
            OutputStreamWriter fout = new OutputStreamWriter(
                    openFileOutput("prueba.txt", Context.MODE_PRIVATE));

            fout.write("Texto de prueba.");
            fout.close();
        } catch (Exception ex) {
            Log.e("Ficheros", "Error al escribir fichero a memoria interna");
        }
    }

    public void onClickWriteToFile(View view) {
        String _contentToWrite = ((EditText) findViewById(R.id.etFileContent)).getText().toString();

        try {
            OutputStreamWriter _fout = new OutputStreamWriter(
                    openFileOutput("prueba.txt", Context.MODE_PRIVATE));

            _fout.write(_contentToWrite);
            _fout.close();
        } catch (Exception ex) {
            Log.e("files", "Error al escribir fichero a memoria interna");
        }
    }

    public void onClickReadFromFile(View view) {
        try {
            BufferedReader _fin = new BufferedReader(new InputStreamReader(openFileInput("prueba.txt")));
            EditText _etContentRecovered = findViewById(R.id.etContentRecovered);
            _etContentRecovered.setText("");
            StringBuilder _builder = new StringBuilder();
            String _nextLine;
            while ((_nextLine = _fin.readLine()) != null) {
                _builder.append(_nextLine);
            }
            _etContentRecovered.setText(_builder.toString());
            _fin.close();
        } catch (Exception ex) {
            Log.e("files", "Error leyendo fichero desde memoria interna");
        }

    }
}
