package dsic.online.files;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        Button _btn_files = findViewById(R.id.btnFiles);
        Button _btn_sdfiles = findViewById(R.id.btnSDFiles);
        Button _btn_xmlfiles = findViewById(R.id.btnXMLFiles);


        _btn_files.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Files.class));
            }
        });

        _btn_sdfiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), SDFiles.class));
            }
        });

        _btn_xmlfiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), XMLFiles.class));
            }
        });
    }
}
