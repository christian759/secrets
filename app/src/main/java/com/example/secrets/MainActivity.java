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

import java.io.IOException;
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
        imageView = findViewById(R.id.my_ImageView);

        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        Uri uri = data.getData();

                        if (uri != null) {
                            try {
                                InputStream inputStream = getContentResolver().openInputStream(uri);
                                if (inputStream != null) {
                                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                                    if (bitmap != null) {
                                        imageView.setImageBitmap(bitmap);
                                    } else {
                                        Toast.makeText(this, "Error decoding image", Toast.LENGTH_SHORT).show();
                                        Log.e("MainActivity", "Error decoding image from stream"); // Log error
                                    }
                                    inputStream.close();
                                } else {
                                    Toast.makeText(this, "Error opening input stream", Toast.LENGTH_SHORT).show();
                                    Log.e("MainActivity", "Error: InputStream is null");
                                }


                            } catch (IOException e) {
                                Log.e("MainActivity", "IOException: " + e.getMessage(), e); // Log full exception
                                Toast.makeText(this, "Error loading image: " + e.getMessage(), Toast.LENGTH_SHORT).show(); //Show the cause to the user
                            } catch (SecurityException e){
                                Log.e("MainActivity", "SecurityException: " + e.getMessage(), e);
                                Toast.makeText(this, "Security Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "No image URI received", Toast.LENGTH_SHORT).show();
                            Log.w("MainActivity", "Warning: URI is null");
                        }
                    } else {
                        Log.d("MainActivity", "Activity result not OK or data is null");
                    }
                });

        openImageButton.setOnClickListener(view -> openFileChooser());
    }

    protected void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*"); // Corrected line
        //intent.setType("image/*");  //Avoid
        intent.addCategory(Intent.CATEGORY_OPENABLE); // Make sure only "openable" files are shown

        try {
            pickImageLauncher.launch(intent);
        } catch (android.content.ActivityNotFoundException e) {
            Toast.makeText(this, "No app found to handle file selection!", Toast.LENGTH_SHORT).show();
            Log.e("MainActivity", "No Activity found to handle Intent", e); //Log the exception
        }


    }
}
