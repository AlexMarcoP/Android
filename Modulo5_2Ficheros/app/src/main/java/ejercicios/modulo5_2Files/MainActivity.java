package ejercicios.modulo5_2Files;

import android.app.ProgressDialog;
import android.content.res.XmlResourceParser;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import ejercicios.modulo4_2gridview.R;

import static java.lang.Integer.parseInt;

public class MainActivity extends AppCompatActivity {
    public GridView gridView;
    public WebView webView;
    public TextView lblMensaje;
    public ProgressDialog progressDialog;
    public Button modify;
    static final String XML_ITEM_LIST_TAG = "itemList";
    static final String XML_ITEM_TAG = "item";

    static final String XML_ITEM_IMAGE = "Image";
    static final String XML_ITEM_TITLE = "Title";
    static final String XML_ITEM_URL = "URL";


    @Override
    protected void onCreate(Bundle savedInstanceState)   {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.gridView = findViewById(R.id.gvOpciones);
        this.webView = findViewById(R.id.webView);
        this.lblMensaje = findViewById(R.id.lblMensaje);

        final List<Item> items = new ArrayList<>();

        items.add(new Item(R.drawable.following, "Following",
                "http://www.imdb.com/title/tt0154506/"));
        items.add(new Item(R.drawable.memento, "Memento",
                "http://www.imdb.com/title/tt0209144/"));
        items.add(new Item(R.drawable.batman_begins, "Batman Begins",
                "http://www.imdb.com/title/tt0372784/"));
        items.add(new Item(R.drawable.the_prestige, "The Prestige",
                "http://www.imdb.com/title/tt0482571/"));

        try {
            InputStream inputStream = this.getResources().openRawResource(R.raw.archivo);

            XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
            parser.setInput(inputStream, null);

            int eventType = XmlPullParser.START_DOCUMENT;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG && parser.getName().equals("item")) {
                    Item item;
                            Integer id = getResources().getIdentifier(parser.getAttributeValue(null, XML_ITEM_IMAGE), "drawable", getPackageName());
                            String title = parser.getAttributeValue(null, XML_ITEM_TITLE);
                            String url = parser.getAttributeValue(null, XML_ITEM_URL);
                    item = new Item(id,title,url);
                    items.add(item);
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
        }catch (XmlPullParserException e){};

        // Sets the data behind this GridView
        this.gridView.setAdapter(new ItemAdapter(this, items));

        gridView.setOnItemClickListener(
                new AdapterView.OnItemClickListener()	{
                    public void onItemClick(AdapterView<?>	parent,
                                            android.view.View	v,	int position,	long id)	{

                        lblMensaje.setText(items.get(position).getTitle());

                        // Show progress dialog
                         progressDialog = ProgressDialog.show(MainActivity.this,
                                "ProgressDialog", "Loading!");

                        // Tells JavaScript to open windows automatically.
                        webView.getSettings().setJavaScriptEnabled(true);
                        // Sets our custom WebViewClient.
                        webView.setWebViewClient(new myWebClient());
                        // Loads the given URL
                        Item item = (Item) gridView.getAdapter().getItem(position);
                        webView.loadUrl(item.getUrl());
                    }

                });

    }



    private class myWebClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // Load the given URL on our WebView.
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // When the page has finished loading, hide progress dialog and progress bar
            super.onPageFinished(view, url);
            progressDialog.dismiss();
        }
    }
}
