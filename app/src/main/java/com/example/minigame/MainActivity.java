package com.example.minigame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.minigame.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'minigame' library on application startup.
    static {
        System.loadLibrary("minigame");
    }

    private ActivityMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.mainStartBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), MemoryActivity.class);
            startActivity(intent);
        });


    }

    /**
     * A native method that is implemented by the 'minigame' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}