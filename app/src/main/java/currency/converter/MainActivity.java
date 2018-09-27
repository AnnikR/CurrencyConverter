package currency.converter;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends Activity
{
    private String LOG_TAG = "XML";

    static final String KEY_CURRENCY = "currency";
    static final String KEY_RATE = "rate";

    public ListView mListView;
    public TextView subjectView;
    public TextView datetimeView;
    public TextView nameView;

    private ProgressBar progressBar;

    ArrayList<HashMap<String, String>> currencies = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = findViewById(R.id.list_view);

        subjectView = findViewById(R.id.subject);
        datetimeView = findViewById(R.id.datetime);
        nameView = findViewById(R.id.name);

        progressBar = findViewById(R.id.progressBar);

        new GetXMLFromServer().execute();

        // Add currencies to ListView
        ListAdapter adapter = new SimpleAdapter(this, currencies,
                R.layout.adapter_view_layout,
                new String[] { KEY_CURRENCY, KEY_RATE }, new int[] {
                R.id.textViewCurrency, R.id.textViewRate });

        mListView.setAdapter(adapter);

        // Listening to single ListItem click
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id)
            {
                // Getting values from selected ListItem
                String currency = ((TextView) view.findViewById(R.id.textViewCurrency)).getText().toString();
                String rate = ((TextView) view.findViewById(R.id.textViewRate)).getText().toString();

                // Starting new intent with currency and rate information
                Intent in = new Intent(getApplicationContext(), CalculatorActivity.class);
                in.putExtra(KEY_CURRENCY, currency);
                in.putExtra(KEY_RATE, rate);
                startActivity(in);
            }
        });
    }

    /**
     * Parse XML values to Hashmap
     * @param xmlString
     */
    public void ParseXML(String xmlString)
    {
        try
        {
            // Parsing XML values to HashMap
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(xmlString));
            int eventType = parser.getEventType();

            String subject;

            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                if(eventType== XmlPullParser.START_TAG)
                {
                    String name = parser.getName();
                    HashMap<String, String> map = new HashMap<>();

                    //Here we get subject "Reference rates"
                    if ("subject".equals(name)) {
                        if (parser.next() == XmlPullParser.TEXT) {
                            subject = parser.getText();
                            Log.d(LOG_TAG, "Subject:" + subject);
                            subjectView.setText(subject);
                        }
                    }

                    //Here we get name "European Central Bank"
                    else if (name.equals("name")) {
                        if (parser.next() == XmlPullParser.TEXT) {
                            String bankName = parser.getText();
                            Log.d(LOG_TAG, "Name:" + bankName);
                            nameView.setText(bankName);
                        }
                    }
                    // Here we get currencies and add them to Hashmap
                    else if (name.equals("Cube")) {
                        if (parser.next() != XmlPullParser.TEXT)
                        {
                            String currency = parser.getAttributeValue(null, "currency".toLowerCase(Locale.ENGLISH));
                            String rate = parser.getAttributeValue(null, "rate");

                            Log.d(LOG_TAG, "Currency:" + currency);
                            Log.d(LOG_TAG, "Rate:" + rate);

                            map.put(KEY_CURRENCY, currency);
                            map.put(KEY_RATE, rate);
                            currencies.add(map);
                        }
                        // Here we get time-value so we know that currencies are up to time
                        else
                        {
                            parser.nextTag();
                            String time = parser.getAttributeValue(null, "time");
                            if ( time != null )
                            {
                                Log.d(LOG_TAG, "Time:" + time);
                                datetimeView.setText(time);
                            }
                        }
                    }
                }
                else if(eventType == XmlPullParser.END_TAG)
                {
                    Log.d(LOG_TAG, "END_TAG HERE");
                }
                eventType = parser.next();
            }
            //Update ListView
            mListView.invalidateViews();
        }

        catch (Exception e)
        {
            Log.d(LOG_TAG,"Error in ParseXML()",e);
        }
    }

    /**
     * Create connection
     */
    private class GetXMLFromServer extends AsyncTask<String,Void,String> {

        HttpHandler nh;

        @Override
        protected String doInBackground(String... strings) {

            String res;
            if (new ConnectionCheck(MainActivity.this).isNetworkAvailable())
            {
                // Open connection to get XML
                String URL = "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml";
                nh =  new HttpHandler();
                InputStream is = nh.CallServer(URL);

                if (is!=null)
                {
                    res = nh.StreamToString(is);
                }
                else
                {
                    res = "NotConnected";
                }
                return res;
            }

            else
            {
                res = "Network not connected";
            }
            return res;
        }
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);

            if(result.equals("NotConnected"))
            {
                Toast.makeText(getApplicationContext(),"Connection Error",Toast.LENGTH_SHORT).show();
            }
            if(result.equals("Network not connected"))
            {
                Toast.makeText(getApplicationContext(),"Network not connected",Toast.LENGTH_SHORT).show();
            }
            else
            {
                ParseXML(result);
            }
        }
    }
}

