package currency.converter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class CalculatorActivity extends Activity {

    // XML node keys
    static final String KEY_CURRENCY = "currency";
    static final String KEY_RATE = "rate";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_list_item);

        // getting intent data
        Intent in = getIntent();

        // Get XML values from previous intent
        String homeN = in.getStringExtra(KEY_CURRENCY);
        String visitorN = in.getStringExtra(KEY_RATE);

        // Displaying all values on the screen
        TextView hName = findViewById(R.id.home_name);
        TextView vName = findViewById(R.id.visitor_name);

        hName.setText(homeN);
        vName.setText(visitorN);
    }
}