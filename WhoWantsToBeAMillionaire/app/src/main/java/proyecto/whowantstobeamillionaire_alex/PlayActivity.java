package proyecto.whowantstobeamillionaire_alex;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


public class PlayActivity extends AppCompatActivity {

    private String SHARED_PREF_GAME = "TAG_GAME";
    private String SHARED_PREF_EXIT = "TAG_EXIT";

    private String SHARED_PREF_AUDIENCE = "isUsedAudienceJoker";
    private String SHARED_PREF_FIFTY_USED = "questionFiftyJokerWereUsed";
    private String SHARED_PREF_FIFTY = "isUsedFiftyJoker";
    private String SHARED_PREF_PHONE = "isUsedPhoneJoker";

    private String SHARED_PREF_QUESTION_MONEY = "TAG_QUESTION_MONEY";
    private String SHARED_PREF_QUESTION_NUMBER = "TAG_QUESTION_NUMBER";

    private List<Question> questions = null;
    private int actualQuestion;
    private int actualMoneyQuestion;

    private boolean isUsedAudienceJoker;
    private boolean isUsedFiftyJoker;
    private int questionFiftyIsUsed;
    private boolean isUsedPhoneJoker;

    private boolean isGameSaved;
    private boolean exitPressed;
    private String listMoneyQuestions[] = {"0", "100", "200", "300", "500", "1000", "2000", "4000", "8000",
            "16000", "32000", "64000", "125000", "250000", "500000", "1000000"};

    MyHelper myhelper = new MyHelper(this);
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        if (questions == null)
            try {
                questions = crearCuestionario();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }

        SharedPreferences sharedPref = getSharedPreferences("MisPreferencias", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(SHARED_PREF_EXIT, false);
        isGameSaved = sharedPref.getBoolean(SHARED_PREF_GAME, false);
        exitPressed = sharedPref.getBoolean(SHARED_PREF_EXIT, false);
        if (isGameSaved && !exitPressed)
            cargarEstadoPartida();
        editor.putBoolean(SHARED_PREF_GAME, true);


        editor.commit();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.play_menu, menu);

        //Dependiendo de la ayuda seleccionada tendremos uno, dos o tres comodines
        SharedPreferences sharedPref = getSharedPreferences("MisPreferencias", MODE_PRIVATE);
        if(Integer.valueOf(sharedPref.getString("TAG_HELP","3"))==2){
            MenuItem fifty = menu.findItem(R.id.play_fifty);
            fifty.setVisible(false);
        }

        if(Integer.valueOf(sharedPref.getString("TAG_HELP","3"))==1){
            MenuItem fifty = menu.findItem(R.id.play_fifty);
            fifty.setVisible(false);

            MenuItem people = menu.findItem(R.id.play_people);
            people.setVisible(false);
        }

        //Comprobamos si los comodines ya han sido usados al continuar con la partida
        if(isUsedFiftyJoker){
            MenuItem fifty = menu.findItem(R.id.play_fifty);
            fifty.setVisible(false);
        }
        if(isUsedAudienceJoker){
            MenuItem people = menu.findItem(R.id.play_people);
            people.setVisible(false);
        }
        if(isUsedPhoneJoker){
            MenuItem call = menu.findItem(R.id.play_call);
            call.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.play_call:
                item.setVisible(false);
                isUsedPhoneJoker= true;
                Toast.makeText(this, getResources().getString(R.string.text_call) + questions.get(actualQuestion).getPhone()  , Toast.LENGTH_LONG).show();
                invalidateOptionsMenu();
                return true;
            case R.id.play_fifty:
                item.setVisible(false);
                isUsedFiftyJoker= true;
                questionFiftyIsUsed = actualQuestion;
                disableButtons(questions.get(actualQuestion).getFifty1());
                disableButtons(questions.get(actualQuestion).getFifty2());
                invalidateOptionsMenu();
                return true;
            case R.id.play_people:
                item.setVisible(false);
                isUsedAudienceJoker= true;
                Toast.makeText(this,  getResources().getString(R.string.text_people) + questions.get(actualQuestion).getAudience()  , Toast.LENGTH_LONG).show();
                invalidateOptionsMenu();
                return true;
            case R.id.play_exit:
                //SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences sharedPref = getSharedPreferences("MisPreferencias", MODE_PRIVATE);

                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean(SHARED_PREF_EXIT, true);
                editor.commit();

                //Enviamos la puntuacion al servidor
                sendScoreToServer task = new sendScoreToServer();
                task.execute();

                guardarPuntuacionBD();
                borrarEstadoPartida();
                finish();
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void disableButtons(String id) {
        Button button = findViewById(getResources().getIdentifier("button" + id, "id",
                this.getBaseContext().getPackageName()));

        button.setEnabled(false);
        button.setVisibility(View.INVISIBLE);

    }
    @Override
    protected void onPause() {
        super.onPause();
        guardarEstadoPartida();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (actualQuestion == 0 && !isGameSaved)
            crearPreguntaActual();
        else
            crearPreguntaActual();

        if(isUsedFiftyJoker && questionFiftyIsUsed == actualQuestion) {
            disableButtons(questions.get(actualQuestion).getFifty1());
            disableButtons(questions.get(actualQuestion).getFifty2());
        }
    }


    public void onClick1(View view) { comprobarRespuesta("1"); }

    public void onClick2(View view) { comprobarRespuesta("2"); }

    public void onClick3(View view) { comprobarRespuesta("3"); }

    public void onClick4(View view) { comprobarRespuesta("4"); }

    public void crearPreguntaActual() {

        Question q = questions.get(actualQuestion);
        TextView textQuestion = findViewById(R.id.questionName);
        TextView a = findViewById(R.id.moneyQuestion);
        TextView b = findViewById(R.id.questionNumber);

        Button buttonA =  findViewById(R.id.button1);
        Button buttonB =  findViewById(R.id.button2);
        Button buttonC =  findViewById(R.id.button3);
        Button buttonD =  findViewById(R.id.button4);

        textQuestion.setText(q.text);
        a.setText((listMoneyQuestions[actualQuestion+1]));
        b.setText(q.number);

        buttonA.setText(q.answer1);
        buttonB.setText(q.answer2);
        buttonC.setText(q.answer3);
        buttonD.setText(q.answer4);

        buttonA.setEnabled(true);
        buttonB.setEnabled(true);
        buttonC.setEnabled(true);
        buttonD.setEnabled(true);
        buttonA.setVisibility(View.VISIBLE);
        buttonB.setVisibility(View.VISIBLE);
        buttonC.setVisibility(View.VISIBLE);
        buttonD.setVisibility(View.VISIBLE);
        a.setVisibility(View.VISIBLE);
        b.setVisibility(View.VISIBLE);

    }
    public void comprobarRespuesta(String answer) {

        if (answer.equals(questions.get(actualQuestion).right)) {
            if (actualQuestion >= questions.size() - 1) {
                Toast.makeText(this, "¡HAS GANADO!", Toast.LENGTH_SHORT).show();
                actualMoneyQuestion = Integer.parseInt(listMoneyQuestions[actualQuestion+1]);
                actualQuestion++;

                //Enviamos la puntuacion al servidor
                sendScoreToServer task = new sendScoreToServer();
                task.execute();

                guardarPuntuacionBD();
                borrarEstadoPartida();
                NavUtils.navigateUpFromSameTask(this);
                //save
            } else {
                Toast.makeText(this, "¡Respuesta correcta!", Toast.LENGTH_SHORT).show();
                actualMoneyQuestion = Integer.parseInt(listMoneyQuestions[actualQuestion+1]);
                actualQuestion++;
                this.crearPreguntaActual();
            }
        } else {
            Toast.makeText(this, "Respuesta incorrecta, vuelva a intentarlo", Toast.LENGTH_SHORT).show();
            borrarEstadoPartida();
            NavUtils.navigateUpFromSameTask(this);
        }

    }

    public List<Question> crearCuestionario() throws XmlPullParserException {
        List<Question> list = new ArrayList<>();
        Question q;
        InputStream inputStream = this.getResources().openRawResource(R.raw.questions);

        XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
        parser.setInput(inputStream, null);

        int eventType = XmlPullParser.START_DOCUMENT;

        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG && parser.getName().equals("question")) {
                q = new Question(
                        parser.getAttributeValue(null, "number"),
                        parser.getAttributeValue(null, "text"),
                        parser.getAttributeValue(null, "answer1"),
                        parser.getAttributeValue(null, "answer2"),
                        parser.getAttributeValue(null, "answer3"),
                        parser.getAttributeValue(null, "answer4"),
                        parser.getAttributeValue(null, "right"),
                        parser.getAttributeValue(null, "audience"),
                        parser.getAttributeValue(null, "phone"),
                        parser.getAttributeValue(null, "fifty1"),
                        parser.getAttributeValue(null, "fifty2")
                );
                list.add(q);
            }
            try {
                eventType = parser.next();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void guardarPuntuacionBD(){
        SharedPreferences sharedPref = getSharedPreferences("MisPreferencias", MODE_PRIVATE);

        //Guardamos valores en la base de datos
        db = myhelper.getWritableDatabase();
        db.beginTransaction();

        try{
            ContentValues values = new ContentValues();

            values.put("name", sharedPref.getString("TAG_NAME", "no_existe"));
            values.put("score", actualMoneyQuestion );
            values.put("longitude",sharedPref.getString("TAG_LONGITUDE","no") );
            values.put("latitude",sharedPref.getString("TAG_LATITUDE","no"));

            db.insert("tblPlayers",null, values );

            db.setTransactionSuccessful();
        }
        catch (SQLiteException e){
        }
        finally{
            db.endTransaction();
        }

    }


    public void guardarEstadoPartida() {
        SharedPreferences sharedPref = getSharedPreferences("MisPreferencias", MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(SHARED_PREF_QUESTION_NUMBER, actualQuestion);
        editor.putInt(SHARED_PREF_QUESTION_MONEY, actualMoneyQuestion);
        editor.putBoolean(SHARED_PREF_PHONE, isUsedPhoneJoker);
        editor.putBoolean(SHARED_PREF_FIFTY, isUsedFiftyJoker);
        editor.putInt(SHARED_PREF_FIFTY_USED, questionFiftyIsUsed);
        editor.putBoolean(SHARED_PREF_AUDIENCE, isUsedAudienceJoker);
        editor.commit();
    }
    public void cargarEstadoPartida() {
        SharedPreferences preferences = getSharedPreferences("MisPreferencias", MODE_PRIVATE);

        actualQuestion = preferences.getInt(SHARED_PREF_QUESTION_NUMBER, 0);
        actualMoneyQuestion = preferences.getInt(SHARED_PREF_QUESTION_MONEY,100);
        isUsedPhoneJoker = preferences.getBoolean(SHARED_PREF_PHONE, false);
        isUsedFiftyJoker = preferences.getBoolean(SHARED_PREF_FIFTY, false);
        questionFiftyIsUsed = preferences.getInt(SHARED_PREF_FIFTY_USED, -1);
        isUsedAudienceJoker = preferences.getBoolean(SHARED_PREF_AUDIENCE, false);
    }
    public void borrarEstadoPartida() {
        SharedPreferences sharedPref = getSharedPreferences("MisPreferencias", MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(SHARED_PREF_GAME, false);
        editor.putBoolean(SHARED_PREF_PHONE, false);
        editor.putBoolean(SHARED_PREF_FIFTY, false);
        editor.putBoolean(SHARED_PREF_AUDIENCE, false);
        editor.commit();
    }

    private class sendScoreToServer extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                SharedPreferences preferences = getSharedPreferences("MisPreferencias", MODE_PRIVATE);
                String actualName = preferences.getString("TAG_NAME", "default");
                String longitude = preferences.getString("TAG_LONGITUDE", "0.00000");
                String latitude = preferences.getString("TAG_LATITUDE", "0.00000");

                // Formamos el String body
                String name= URLEncoder.encode("name", "UTF-8");
                String valueName= URLEncoder.encode(actualName, "UTF-8");
                String score= URLEncoder.encode("score", "UTF-8");
                String valueScore= URLEncoder.encode(String.valueOf(actualMoneyQuestion), "UTF-8");
                String lon= URLEncoder.encode("longitude", "UTF-8");
                String valueLongitude= URLEncoder.encode(latitude, "UTF-8");
                String lat= URLEncoder.encode("latitude", "UTF-8");
                String valueLatitude= URLEncoder.encode(longitude, "UTF-8");

                String body= name+"="+valueName+"&"+   score+"="+valueScore+"&"+   lon+"="+valueLongitude+"&"+   lat+"="+valueLatitude;

                //Operacion PUT
                //URL url = new URL(" https://wwtbam-1076.appspot.com/_ah/api/highscores/v1/new");
                URL url = new URL("https://wwtbamandroid.appspot.com/rest/highscores");

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("PUT");
                connection.setDoOutput(true);
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
                writer.write(body);
                writer.close();
                connection.getInputStream();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

    }
}
