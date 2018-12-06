package dsic.online.files;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.EditText;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;

public class XMLFiles extends AppCompatActivity {

    static final String XML_CONTACT_LIST_TAG = "ContactList";
    static final String XML_CONTACT_TAG = "Contact";

    static final String XML_CONTACT_NAME = "Name";
    static final String XML_CONTACT_PHONE = "Phone";

    static final String XML_FILE_NAME = "pruebaXML.xml";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xmlfiles);
    }

    public void onClickWriteToXML(View view) {

        XmlSerializer _serializer = Xml.newSerializer();
        StringWriter _writer = new StringWriter();
        try {
            _serializer.setOutput(_writer);
            _serializer.startDocument(null, null);

            _serializer.startTag(null, XML_CONTACT_LIST_TAG);
            _serializer.startTag(null, XML_CONTACT_TAG);
            _serializer.attribute(null, XML_CONTACT_NAME, ((EditText) findViewById(R.id.etNombre)).getText().toString());
            _serializer.attribute(null, XML_CONTACT_PHONE, ((EditText) findViewById(R.id.etPhone)).getText().toString());
            _serializer.endTag(null, XML_CONTACT_TAG);
            _serializer.endTag(null, XML_CONTACT_LIST_TAG);

            _serializer.endDocument();
            _serializer.flush();
        } catch (Exception e) {
            Log.e("files", "Error generando fichero XML");
        }

        try {
            OutputStreamWriter _fout = new OutputStreamWriter(
                    openFileOutput(XML_FILE_NAME, Context.MODE_PRIVATE));

            _fout.write(_writer.toString());
            _fout.close();

        } catch (Exception e) {
            Log.e("files", "Error escribiendo XML generado");
        }
    }

    public void onClickReadFromXML(View view) {
        ReadFileAsXML();
    }

    private void ReadFileAsXML() {
        try {
            BufferedReader _fin = new BufferedReader(new InputStreamReader(openFileInput(XML_FILE_NAME)));

            EditText _etContentRecovered = findViewById(R.id.etXMLContent);
            _etContentRecovered.setText("");

            XmlPullParser _parser = XmlPullParserFactory.newInstance().newPullParser();
            _parser.setInput(_fin);

            StringBuilder _builder = new StringBuilder();
            int _eventType = _parser.getEventType(); // Al comienzo XmlPullParser.START_DOCUMENT
            while (_eventType != XmlPullParser.END_DOCUMENT) {
                switch (_eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        _builder.append("START_DOCUMENT\n");
                        break;
                    case XmlPullParser.START_TAG:
                        _builder.append("  START_TAG [").append(_parser.getName()).append("]\n");
                        if (_parser.getName().equals(XML_CONTACT_TAG)) {
                            _builder.append("    [").append(_parser.getName()).append("]:").append(_parser.getAttributeValue(null, XML_CONTACT_NAME)).append("\n");
                            _builder.append("    [").append(_parser.getName()).append("]:").append(_parser.getAttributeValue(null, XML_CONTACT_PHONE)).append("\n");
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        _builder.append("  END_TAG [").append(_parser.getName()).append("]\n");
                        break;
                    default:
                        break;
                }
                _parser.next();
                _eventType = _parser.getEventType();
            }
            _builder.append("END_DOCUMENT\n");
            _etContentRecovered.setText(_builder.toString());
            _fin.close();
        } catch (Exception ex) {
            Log.e("files", "Error leyendo XML desde fichero");
        }

    }

    private void ReadFileAsRawData() {
        try {
            BufferedReader _fin = new BufferedReader(new InputStreamReader(openFileInput(XML_FILE_NAME)));
            EditText _etContentRecovered = findViewById(R.id.etXMLContent);
            _etContentRecovered.setText("");
            StringBuilder _builder = new StringBuilder();
            String _nextLine;
            while ((_nextLine = _fin.readLine()) != null) {
                _builder.append(_nextLine);
            }
            _etContentRecovered.setText(_builder.toString());
            _fin.close();
        } catch (Exception ex) {
            Log.e("files", "Error leyendo XML desde fichero");
        }
    }

}
