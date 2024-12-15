package com.example.myapplication;

import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private EditText startPoint;
    private EditText endPoint;
    private EditText fuelConsumption; // Расход топлива
    private EditText fuelPrice; // Цена топлива
    private TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webView);
        startPoint = findViewById(R.id.startPoint);
        endPoint = findViewById(R.id.endPoint);
        fuelConsumption = findViewById(R.id.fuelConsumption);
        fuelPrice = findViewById(R.id.fuelPrice);
        resultTextView = findViewById(R.id.resultTextView);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new WebAppInterface(), "Android");
        webView.loadUrl("file:///android_asset/map.html");

        Button calculateButton = findViewById(R.id.calculateButton);
        calculateButton.setOnClickListener(v -> calculateRoute());
    }

    private void calculateRoute() {
        String start = startPoint.getText().toString();
        String end = endPoint.getText().toString();

        if (start.isEmpty() || end.isEmpty()) {
            resultTextView.setText("Пожалуйста, заполните обе точки.");
            return;
        }

        webView.evaluateJavascript("calculateRoute('" + start + "', '" + end + "')", null);
    }

    public class WebAppInterface {
        @JavascriptInterface
        public void showResult(String distance, String duration) {
            // Получаем расход топлива и цену топлива
            String fuelConsumptionText = fuelConsumption.getText().toString();
            String fuelPriceText = fuelPrice.getText().toString();

            double distanceInKm = Double.parseDouble(distance);
            double fuelConsumptionValue = fuelConsumptionText.isEmpty() ? 0 : Double.parseDouble(fuelConsumptionText);
            double fuelPriceValue = fuelPriceText.isEmpty() ? 0 : Double.parseDouble(fuelPriceText);

            // Рассчитываем стоимость топлива
            double fuelCost = (distanceInKm / 100) * fuelConsumptionValue * fuelPriceValue;

            // Отображаем результат
            runOnUiThread(() -> resultTextView.setText(
                    "Расстояние: " + distance + " км\n" +
                            "Время: " + duration + "\n" +
                            "Стоимость топлива: " + String.format("%.2f", fuelCost) + " руб."
            ));
        }
    }
}