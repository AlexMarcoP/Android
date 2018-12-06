package dsic.online.files;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class SDFiles extends AppCompatActivity {

    // Constantes que indican el permiso solicitado
    private final static int PERMISO_LECTURA = 0;
    private final static int PERMISO_ESCRITURA = 1;

    boolean sdDisponible = false;
    boolean sdAccesoEscritura = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sdfiles);

        // Comprobamos el estado de la memoria externa (tarjeta SD) y actualizamos
        // convenientemente las variables que muestran dicho estado

        String estado = Environment.getExternalStorageState();
        switch (estado) {
            case Environment.MEDIA_MOUNTED:
                // Acceso en lectura y escritura
                sdDisponible = true;
                sdAccesoEscritura = true;
                break;
            case Environment.MEDIA_MOUNTED_READ_ONLY:
                // Acceso en sólo lectura
                sdDisponible = true;
                sdAccesoEscritura = false;
                break;
            default:
                // Puede que exista algún problema MEDIA_UNMOUNTED, MEDIA_REMOVED, …
                sdDisponible = false;
                sdAccesoEscritura = false;
                break;
        }
    }

    public void onClickWriteToSDFile(View view) {
        if (sdDisponible && sdAccesoEscritura) {

            // Comprobamos si el usuario ha otorgado permiso de escritura en memoria externa
            if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, PERMISO_ESCRITURA)) {
                writeToSDFile();
            }
        } else {
            Toast.makeText(this, R.string.sd_not_available, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkPermission(String permission, int operacion) {
        int check = ContextCompat.checkSelfPermission(this, permission);
        // Permiso concedido
        if (check == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            // Solicitar permiso al usuario
            ActivityCompat.requestPermissions(this, new String[]{permission}, operacion);
            return false;
        }
    }

    private void writeToSDFile() {
        try {
            String _contentToWrite = ((EditText) findViewById(R.id.etSDFileContent)).getText().toString();
            File _f = new File(Environment.getExternalStorageDirectory().getPath(), "prueba_sd.txt");
            OutputStreamWriter _fout = new OutputStreamWriter(new FileOutputStream(_f));
            _fout.write(_contentToWrite);
            _fout.flush();
            _fout.close();
        } catch (Exception ex) {
            Log.e("files", "Error al escribir fichero a tarjeta SD");
            ex.printStackTrace();
        }
    }

    public void onClickReadFromSDFile(View view) {
        if (sdDisponible) {
            // Comprobamos si el usuario ha otorgado permiso de lectura en memoria externa
            if (checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, PERMISO_LECTURA)) {
                readFromSDFile();
            }
        } else {
            Toast.makeText(this, R.string.sd_not_available, Toast.LENGTH_SHORT).show();
        }

    }

    private void readFromSDFile() {
        try {
            File _f = new File(Environment.getExternalStorageDirectory().getPath(), "prueba_sd.txt");
            EditText _etContentRecovered = findViewById(R.id.etSDContentRecovered);
            BufferedReader _fin = new BufferedReader(new InputStreamReader(new FileInputStream(_f)));
            String _nextLine;
            StringBuilder builder = new StringBuilder();
            while ((_nextLine = _fin.readLine()) != null) {
                builder.append(_nextLine);
            }
            _etContentRecovered.setText(builder.toString());
            _fin.close();
        } catch (Exception ex) {
            Log.e("files", "Error al leer fichero desde tarjeta SD");
            ex.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            switch (requestCode) {
                case PERMISO_ESCRITURA:
                    writeToSDFile();
                    break;
                case PERMISO_LECTURA:
                    readFromSDFile();
                    break;
            }
        } else {
            Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
        }
    }
}
