package dsic.online.bd;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    final static int STATE_NONE = 0;
    final static int STATE_NEW = 1;
    final static int STATE_EDIT = 2;

    SimpleAdapter adapter = null;
    List<HashMap<String, String>> contactList = null;

    EditText etName = null;
    EditText etEmail = null;
    EditText etPhone = null;
    ImageButton ibSend = null;
    ImageButton ibCall = null;

    int state = STATE_NONE;
    int itemSelected = 0;

    MyHelper myhelper = new MyHelper(this);
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        ibSend = findViewById(R.id.bSend);
        ibCall = findViewById(R.id.bCall);

        disableEdition();

        ListView list = findViewById(R.id.lvAgenda);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                enableEdition();
                etName.setText(contactList.get(position).get("name"));
                etEmail.setText(contactList.get(position).get("email"));
                etPhone.setText(contactList.get(position).get("phone"));

                itemSelected = position;

                state = STATE_EDIT;
                etName.setEnabled(false);
                supportInvalidateOptionsMenu();
            }
        });

        contactList = new ArrayList<>();

        //CONSULTAS BD's

        db = myhelper.getWritableDatabase();
        String [] columns = {"name","email","phone"};
        Cursor mycur = db.query("tblContact", columns ,null,null,null,null,"name");

        int idCol = mycur.getColumnIndex("name");
        int emailCol = mycur.getColumnIndex("email");
        int phoneCol = mycur.getColumnIndex("phone");

        while(mycur.moveToNext()) {
            columns[0] = mycur.getString(idCol);
            columns[1] = mycur.getString(emailCol);
            columns[2] = mycur.getString(phoneCol);

            HashMap<String, String> m = new HashMap<>();
            m.put("name",columns[0]);
            m.put("email",columns[1]);
            m.put("phone",columns[2]);

            contactList.add(m);
        }


        adapter = new SimpleAdapter(
                this,
                contactList,
                R.layout.list_item, new String[]{"name", "email", "phone"},
                new int[]{R.id.tvName, R.id.tvEmail, R.id.tvPhone}
        );
        list.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        switch (state) {
            case STATE_NONE:
                menu.findItem(R.id.action_new).setVisible(true);
                menu.findItem(R.id.action_save).setVisible(false);
                menu.findItem(R.id.action_clear).setVisible(false);
                menu.findItem(R.id.action_delete).setVisible(false);
                break;
            case STATE_NEW:
                menu.findItem(R.id.action_new).setVisible(false);
                menu.findItem(R.id.action_save).setVisible(true);
                menu.findItem(R.id.action_clear).setVisible(true);
                menu.findItem(R.id.action_delete).setVisible(false);
                break;
            case STATE_EDIT:
                menu.findItem(R.id.action_new).setVisible(false);
                menu.findItem(R.id.action_save).setVisible(true);
                menu.findItem(R.id.action_clear).setVisible(true);
                menu.findItem(R.id.action_delete).setVisible(true);
                break;
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {

            case R.id.action_new:
                enableEdition();
                clearEdition();
                state = STATE_NEW;
                supportInvalidateOptionsMenu();
                return true;

            case R.id.action_save:

                if (etName.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(this, R.string.name_required, Toast.LENGTH_SHORT).show();
                } else {
                    HashMap<String, String> contact = new HashMap<>();
                    contact.put("name", etName.getText().toString());
                    contact.put("email", etEmail.getText().toString());
                    contact.put("phone", etPhone.getText().toString());

                    if (state == STATE_NEW) {
                        contactList.add(contact);

                        //Insertar un registro
                        db.beginTransaction();

                        try{
                            ContentValues values = new ContentValues();
                            values.put("name", etName.getText().toString());
                            values.put("email", etEmail.getText().toString());
                            values.put("phone", etPhone.getText().toString());
                            db.insert("tblContact",null, values );

                            db.setTransactionSuccessful();
                        }
                        catch (SQLiteException e){
                        }
                        finally{
                            db.endTransaction();
                        }

                    } else if (state == STATE_EDIT) {
                        contactList.set(itemSelected, contact);

                        //Actualizar un registro
                        db.beginTransaction();

                        try {
                            ContentValues values = new ContentValues();
                            values.put("name", etName.getText().toString());
                            values.put("email", etEmail.getText().toString());
                            values.put("phone", etPhone.getText().toString());
                            db.update("tblContact", values, "name='" + etName.getText().toString() + "'", null);

                            db.setTransactionSuccessful();
                        } catch (SQLiteException e) {
                        } finally {
                            db.endTransaction();
                        }

                    }
                    adapter.notifyDataSetChanged();

                    clearEdition();
                    disableEdition();
                    state = STATE_NONE;
                    supportInvalidateOptionsMenu();
                }
                return true;

            case R.id.action_clear:
                if (state == STATE_NEW) {
                    clearEdition();
                } else if (state == STATE_EDIT) {
                    clearEdition();
                    disableEdition();
                    state = STATE_NONE;
                    supportInvalidateOptionsMenu();
                }
                return true;

            case R.id.action_delete:

                //Eliminar un registro
                db.beginTransaction();

                try{
                    db.delete("tblContact", "name='" + etName.getText().toString()+"'", null);

                    db.setTransactionSuccessful();
                }
                catch (SQLiteException e){
                }
                finally{
                    db.endTransaction();
                }

                contactList.remove(itemSelected);
                adapter.notifyDataSetChanged();
                clearEdition();
                disableEdition();
                state = STATE_NONE;
                supportInvalidateOptionsMenu();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void enableEdition() {
        etName.setEnabled(true);
        etEmail.setEnabled(true);
        etPhone.setEnabled(true);
        ibSend.setEnabled(true);
        ibCall.setEnabled(true);
    }

    private void disableEdition() {
        etName.setEnabled(false);
        etEmail.setEnabled(false);
        etPhone.setEnabled(false);
        ibSend.setEnabled(false);
        ibCall.setEnabled(false);
    }

    private void clearEdition() {
        etName.setText("");
        etEmail.setText("");
        etPhone.setText("");
    }

    public void onClickButton(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.bSend:
                intent.setAction(Intent.ACTION_SEND);
                intent.setData(Uri.parse("mailto:"));
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{etEmail.getText().toString()});
                startActivity(Intent.createChooser(intent, "Send email..."));
                break;
            case R.id.bCall:
                intent.setAction(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + etPhone.getText().toString()));
                startActivity(intent);
                break;
        }
    }
}
