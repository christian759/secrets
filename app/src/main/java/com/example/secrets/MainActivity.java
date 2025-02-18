package com.example.secrets;

import android.graphics.Bitmap;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.io.InputStream;


public class MainActivity extends AppCompatActivity {
    ImageView imageView;
    private ActivityResultLauncher<Intent> pickImageLauncher;

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

        button3.setOnClickListener(v -> {
            ContentFragment3 contentFragment3 = new ContentFragment3();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, contentFragment3)
                    .commit();
        });

        button4.setOnClickListener(v -> {
            ContentFragment4 contentFragment4 = new ContentFragment4();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, contentFragment4)
                    .commit();
        });

        //Button openImageButton = findViewById(R.id.openImage);
        //imageView = findViewById(R.id.ImageViewer);

        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        Uri uri = data.getData();
                        if (uri != null) {
                            try {
                                InputStream inputStream = getContentResolver().openInputStream(uri);
                                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                                imageView.setImageBitmap(bitmap);
                                inputStream.close();
                            } catch (Exception e) {Toast.makeText(this, "Error loading image: " + e.getMessage(), Toast.LENGTH_SHORT).show();}
                        } else {Toast.makeText(this, "No image URI received", Toast.LENGTH_SHORT).show();}}});

        //openImageButton.setOnClickListener(view -> openFileChooser());
    }

    protected void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE); // Make sure only "openable" files are shown

        try {
            pickImageLauncher.launch(intent);
        } catch (android.content.ActivityNotFoundException e) {
            Toast.makeText(this, "No app found to handle file selection!", Toast.LENGTH_SHORT).show();
            Log.e("MainActivity", "No Activity found to handle Intent", e); //Log the exception
        }
    }
}
