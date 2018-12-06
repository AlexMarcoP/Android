package proyecto.whowantstobeamillionaire_alex;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Muestra un mensaje al iniciar la aplicacion por primera vez indicando que configures los ajustes
        SharedPreferences preferences = getSharedPreferences("MisPreferencias", MODE_PRIVATE);
        Boolean first = preferences.getBoolean("FirstGame", true);
        if (first) {
            Toast.makeText(this, getResources().getString(R.string.initial_message), Toast.LENGTH_LONG).show();
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("FirstGame", false);
            editor.commit();
        }
    }

    public void onClickDashboard(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.play:
                intent.setClass(this, PlayActivity.class);
                intent.putExtra("Data", "Play");
                break;
            case R.id.scores:
                intent.setClass(this, ScoresActivity.class);
                intent.putExtra("Data", "Scores");
                break;
            case R.id.settings:
                intent.setClass(this, SettingsActivity.class);
                intent.putExtra("Data", "Settings");
                break;
        }
        startActivity(intent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.dashboard_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent2 = new Intent();
        switch (item.getItemId()) {
            case R.id.action_information:
                intent2.setClass(this, CreditsActivity.class);
                intent2.putExtra("Data", "Credits");
                startActivity(intent2);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
