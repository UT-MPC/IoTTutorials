package com.example.temperatureconverter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

public class MainActivity extends Activity {
    public static final String TEXT = "TEXT";
    private EditText text;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text = (EditText) findViewById(R.id.inputValue);

    }

    // this method is called at button click because we assigned the name to the
    // "OnClick" property of the button
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button1:
                RadioButton celsiusButton = (RadioButton) findViewById(R.id.radio0);
                RadioButton fahrenheitButton = (RadioButton) findViewById(R.id.radio1);
                if (text.getText().length() == 0) {
                    Toast.makeText(this, "Please enter a valid number",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                float inputValue = Float.parseFloat(text.getText().toString());
                String conversionText = "";
                if (celsiusButton.isChecked()) {
                    float cValue = ConverterUtil.convertFahrenheitToCelsius(inputValue);
                    text.setText(String.valueOf(cValue));
                    celsiusButton.setChecked(false);
                    fahrenheitButton.setChecked(true);
                    conversionText += inputValue + "F is " + cValue + "C";
                } else {
                    float fValue = ConverterUtil.convertCelsiusToFahrenheit(inputValue);
                    text.setText(String.valueOf(fValue));
                    fahrenheitButton.setChecked(false);
                    celsiusButton.setChecked(true);
                    conversionText += inputValue + "C is " + fValue + "F";
                }
                Intent intent = createConversion(this, conversionText);
                startActivity(intent);
                break;
        }
    }

    public static Intent createConversion(Context context, String conversionText) {
        Intent i = new Intent(context, DisplayConversionActivity.class);
        i.putExtra(TEXT, conversionText);
        return i;
    }

}