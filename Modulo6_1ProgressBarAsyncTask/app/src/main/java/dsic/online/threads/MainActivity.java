package dsic.online.threads;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;


public class MainActivity extends AppCompatActivity {

    ProgressBar pb = null;
    TextView tv = null;

    Button bStart = null;
    Button bStop = null;

    boolean cancelled = false;
    boolean pause = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pb = findViewById(R.id.pbProgress);
        tv = findViewById(R.id.tvPercentage);

        bStart = findViewById(R.id.bStart);
        bStop = findViewById(R.id.bStop);

        bStart.setEnabled(true);
        bStop.setEnabled(false);

        updateProgress(0);
    }

    public void onClickButton(View view) {
        switch (view.getId()) {
            case R.id.bStart:
                //updateProgress(0);

                bStart.setEnabled(false);
                bStop.setEnabled(true);

                cancelled = false;

                new MyAsyncTask(this).execute();

                break;
            case R.id.bStop:
                cancelled = true;
                bStart.setEnabled(true);
                bStop.setEnabled(false);
                break;
        }
    }

    public void updateProgress(Integer progress) {
        pb.setProgress(progress);
        tv.setText(String.format(getResources().getString(R.string.x_out_of_100), progress));
        if (progress==100 && !pause){
            Toast.makeText(this, R.string.max_count, Toast.LENGTH_SHORT).show();
            bStart.setEnabled(true);
            bStop.setEnabled(false);
        }
    }

    @Override
    public void onPause(){
        pause = true;
        super.onPause();

    }
    @Override
    public void onResume(){
        super.onResume();
    }

    private class MyAsyncTask extends AsyncTask<Void, Integer, Void> {

        private WeakReference<MainActivity> activity;

        MyAsyncTask(MainActivity activity) {
            this.activity = new WeakReference<>(activity);
        }

        @Override
        protected Void doInBackground(Void... values) {
            int progress=0;
            while ((progress < pb.getMax()) && (!cancelled)) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Log.d("DEBUG", "Thread interrupted");
                }
                progress++;
                publishProgress(progress);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            if (activity.get() != null) {
                updateProgress(progress[0]);
            }
        }
    }
}

