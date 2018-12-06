package dsic.online.patterns;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class BlankActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blank);

        TextView tv = findViewById(R.id.tvText);
        tv.setText(getIntent().getStringExtra("Data"));
    }

}
