package currency.converter;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends Activity
{
    private String LOG_TAG = "XML";

    static final String KEY_GESMES_SUBJECT = "subject";
    static final String KEY_GESMES_NAME = "name";
    static final String KEY_CURRENCY = "currency";
    static final String KEY_RATE = "rate";
    private int UpdateFlag = 0;

    public ListView mListView;

    ArrayList<HashMap<String, String>> currencies = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = findViewById(R.id.list_view);

        new GetXMLFromServer().execute();

        // ADD CURRENCIES TO LISTVIEW
        ListAdapter adapter = new SimpleAdapter(this, currencies,
                R.layout.adapter_view_layout,
                new String[] { KEY_CURRENCY, KEY_RATE }, new int[] {
                R.id.textViewCurrency, R.id.textViewRate });

        mListView.setAdapter(adapter);

        // LISTENING TO SINGLE LISTITEM CLICK
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id)
            {
                // GETTING VALUES FROM SELECTED LIST ITEM
                String currency = ((TextView) view.findViewById(R.id.textViewCurrency)).getText().toString();
                String rate = ((TextView) view.findViewById(R.id.textViewRate)).getText().toString();

                // STARTING NEW INTENT WITH CURRENCY AND RATE INFORMATION
                Intent in = new Intent(getApplicationContext(), CalculatorActivity.class);
                in.putExtra(KEY_CURRENCY, currency);
                in.putExtra(KEY_RATE, rate);
                startActivity(in);
            }
        });
    }

    public void ParseXML(String xmlString)
    {
        try
        {
            // PARSING XML VALUES TO HASHMAP
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

                    //NOT IN USE RIGHT NOW
                    if ("subject".equals(name)) {
                        if (parser.next() == XmlPullParser.TEXT) {
                            subject = parser.getText();
                            Log.d(LOG_TAG, "Subject:" + subject);
//                            map.put(KEY_GESMES_SUBJECT, subject);
//                            currencies.add(map);
                        }
                    }
                    //NOT IN USE RIGHT NOW
                    else if ("name".equals(name)) {
                        if (parser.next() == XmlPullParser.TEXT) {
                            name = parser.getText();
                            Log.d(LOG_TAG, "Name:" + name);
//                            map.put(KEY_GESMES_NAME, name);
//                            currencies.add(map);
                        }
                    }
                    // THESE VALUES IS ADDED TO HASHMAP CURRENCIES
                    else if (name.equals("Cube")) {
                        if (parser.next() != XmlPullParser.TEXT) {
                            String currency = parser.getAttributeValue(null, "currency".toLowerCase(Locale.ENGLISH));
                            String rate = parser.getAttributeValue(null, "rate");

                            Log.d(LOG_TAG, "Currency:" + currency);
                            Log.d(LOG_TAG, "Rate:" + rate);

                            map.put(KEY_CURRENCY, currency);
                            map.put(KEY_RATE, rate);
                            currencies.add(map);
                        }
                    }
                }
                else if(eventType== XmlPullParser.END_TAG)
                {
                    Log.d(LOG_TAG, "END_TAG HERE");
                }
                eventType = parser.next();
            }
            //UPDATE LISTVIEW
            mListView.invalidateViews();
        }

        catch (Exception e)
        {
            Log.d(LOG_TAG,"Error in ParseXML()",e);
        }
    }

    private class GetXMLFromServer extends AsyncTask<String,Void,String> {

        HttpHandler nh;

        @Override
        protected String doInBackground(String... strings) {

            // OPEN CONNECTION TO GET XML
            String URL = "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml";
            String res;
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
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);

            if(result.equals("NotConnected"))
            {
                Toast.makeText(getApplicationContext(),"Connection Error",Toast.LENGTH_SHORT).show();

            }
            else
            {
                ParseXML(result);
            }
        }
    }
}
