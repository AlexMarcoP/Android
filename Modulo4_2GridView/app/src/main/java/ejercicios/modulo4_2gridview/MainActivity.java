package ejercicios.modulo4_2gridview;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public GridView gridView;
    public WebView webView;
    public TextView lblMensaje;
    public ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
