package dsic.online.patterns;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
    }

    public void onClickDashboard(View v) {
        Intent intent = new Intent();
        intent.setClass(this, BlankActivity.class);
        switch (v.getId()) {
            case R.id.bAdd:
                intent.putExtra("Data", "Add");
                Toast.makeText(this, R.string.toast_add, Toast.LENGTH_SHORT).show();
                break;
            case R.id.bDelete:
                intent.putExtra("Data", "Delete");
                Toast.makeText(this, R.string.toast_delete, Toast.LENGTH_SHORT).show();
                break;
            case R.id.bCamera:
                intent.putExtra("Data", "Camera");
                Toast.makeText(this, R.string.toast_camera, Toast.LENGTH_SHORT).show();
                break;
            case R.id.bGallery:
                intent.putExtra("Data", "Gallery");
                Toast.makeText(this, R.string.toast_gallery, Toast.LENGTH_SHORT).show();
                break;
            case R.id.bAgenda:
                intent.putExtra("Data", "Agenda");
                Toast.makeText(this, R.string.toast_agenda, Toast.LENGTH_SHORT).show();
                break;
            case R.id.bSend:
                intent.putExtra("Data", "Send");
                Toast.makeText(this, R.string.toast_send, Toast.LENGTH_SHORT).show();
                break;

        }
        startActivity(intent);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                Toast.makeText(this, R.string.hello_world,Toast.LENGTH_LONG).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
