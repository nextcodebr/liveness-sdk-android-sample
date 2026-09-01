package br.com.example;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.net.URL;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Before anything touches the SDK: the Retrofit base URL is resolved once per
        // process, so this cannot be done later.
        DemoConfig.apply();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            Class.forName("com.google.mlkit.vision.common.internal.Detector", false, Thread.currentThread().getContextClassLoader());
        } catch (Exception e) {
            Log.e("ERROR", "Class Not Found", e);
        }
    }
}