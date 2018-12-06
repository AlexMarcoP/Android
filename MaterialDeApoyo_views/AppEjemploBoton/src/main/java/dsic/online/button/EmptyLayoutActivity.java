package dsic.online.button;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.Date;


public class EmptyLayoutActivity extends AppCompatActivity {

    private LinearLayout miLayout = null;
    private Button miBoton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.colocarBotonEnLayoutDinamicamente();

        setContentView(miLayout);

    }

    private void colocarBotonEnLayoutDinamicamente() {
        miLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.empty_layout, null);

        miBoton = new Button(this);
        miBoton.setText(R.string.btn_texto);
        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        llp.gravity = Gravity.CENTER;
        miBoton.setLayoutParams(llp);
        miBoton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                miBoton.setText(new Date().toString());
            }

        });
        miLayout.addView(miBoton);
    }

}
