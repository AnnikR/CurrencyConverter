package currency.converter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class CalculatorActivity extends Activity {

    // XML node keys
    static final String KEY_CURRENCY = "currency";
    static final String KEY_RATE = "rate";

    private TextView resultEUR;
    private TextView resultCUR;

    private EditText currencyAmount;
    private EditText eurAmount;

    public String currency;
    public String rate;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_list_item);

        // getting intent data
        Intent in = getIntent();

        // Get XML values from previous intent
        currency = in.getStringExtra(KEY_CURRENCY);
        rate = in.getStringExtra(KEY_RATE);

        TextView cName1 = findViewById(R.id.currency_name1);
        TextView cName2 = findViewById(R.id.currency_name2);
        TextView rName = findViewById(R.id.rate_value);
        TextView cName = findViewById(R.id.textViewCurrency);
        resultEUR = findViewById(R.id.resultEUR);
        resultCUR = findViewById(R.id.resultCUR);
        currencyAmount = findViewById(R.id.valueInput1);
        eurAmount = findViewById(R.id.valueInputEUR);

        cName.setText(currency);
        cName1.setText(currency);
        cName2.setText(currency);
        rName.setText(rate);
        resultCUR.setText(rate);
        float calculation = 1 / Float.valueOf(String.valueOf(rate));
        resultEUR.setText(Float.toString(calculation));
    }

    public void calculateCUR(View view) {
        float value1 = Float.valueOf(rate);
        float value2 = Float.valueOf(String.valueOf(eurAmount.getText()));
        float calculation = value1 * value2;
        String result = Float.toString(calculation);
        resultCUR.setText(result);
    }

    public void calculateEUR(View view) {
        float value1 = Float.valueOf(rate);
        float value2 = Float.valueOf(String.valueOf(currencyAmount.getText()));
        float calculation = value2 / value1;
        String result = Float.toString(calculation);
        resultEUR.setText(result);
    }
}