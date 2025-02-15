package com.example.secrets;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
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

        Button openImageButton = findViewById(R.id.openImage);
        imageView = findViewById(R.id.ImageViewer);

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

        openImageButton.setOnClickListener(view -> openFileChooser());
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
