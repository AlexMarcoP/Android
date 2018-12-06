package holamundo.holamundo.ejercicios.upv.myandroidapp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class HolaMundo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ejercicio_modulo_4);

        recoverState();
    }
    @Override
    protected void onPause(){
        super.onPause();

        saveState();
    }
    public void saveState() {
        SharedPreferences _prefs = getSharedPreferences("MisPreferencias", Activity.MODE_PRIVATE);
        if(_prefs==null)return;

        SharedPreferences.Editor _prefsEditor = _prefs.edit();
        if(_prefsEditor==null)return;

        _prefsEditor.putString("TAG_NAME", ((EditText) findViewById(R.id.nombre)).getText().toString());
        _prefsEditor.putString("TAG_PHONE", ((EditText) findViewById(R.id.telefono)).getText().toString());

        _prefsEditor.commit();
    }
    public void saveState(View view){
        saveState();
    }
    private void clearState(){
        SharedPreferences _prefs = getSharedPreferences("MisPreferencias", MODE_PRIVATE);
        SharedPreferences.Editor _prefsEditor = _prefs.edit();
        _prefsEditor.clear();
        _prefsEditor.commit();
    }
    public void clearState(View view){
        clearState();
    }
    private void recoverState(){
        SharedPreferences _prefs = getSharedPreferences("MisPreferencias", MODE_PRIVATE);
        if(_prefs==null)return;

        ((EditText) findViewById(R.id.nombre)).setText(_prefs.getString("TAG_NAME","Empty"));
        ((EditText) findViewById(R.id.telefono)).setText(_prefs.getString("TAG_PHONE","Empty"));
    }
    public void recoverState(View view){
        recoverState();
    }
}
