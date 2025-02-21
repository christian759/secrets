package com.example.secrets;

import android.os.Bundle;
import android.widget.ImageButton;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageButton button1 = findViewById(R.id.button);
        ImageButton button2 = findViewById(R.id.button2);
        ImageButton button3 = findViewById(R.id.button3);
        ImageButton button4 = findViewById(R.id.button4);

        button1.setOnClickListener(v -> {
            ContentFragment1 contentFragment1 = new ContentFragment1();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, contentFragment1)
                    .commit();
        });

        button2.setOnClickListener(v -> {
            ContentFragment2 contentFragment2 = new ContentFragment2();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, contentFragment2)
                    .commit();
        });

    }
}
