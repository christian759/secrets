package com.example.secrets;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.io.InputStream;

public class ContentFragment1 extends Fragment {

    ImageView imageView11 = requireActivity().findViewById(R.id.imageView1);
    ActivityResultLauncher<Intent> pickImageLauncher11;
    Button button1 = requireActivity().findViewById(R.id.frag1_button1);
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
        @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {

        pickImageLauncher11 = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == AppCompatActivity.RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        Uri uri = data.getData();
                        if (uri != null) {
                            try {
                                InputStream inputStream = requireActivity().getContentResolver().openInputStream(uri);
                                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                                imageView11.setImageBitmap(bitmap);
                                inputStream.close();
                            } catch (Exception e) {
                                Toast.makeText(requireContext(), "Error loading image: " + e.getMessage(), Toast.LENGTH_SHORT).show();}
                        } else {Toast.makeText(requireContext(), "No image URI received", Toast.LENGTH_SHORT).show();}}});

        button1.setOnClickListener(view -> openFileChooser());

        return inflater.inflate(R.layout.content_fragment_1, container, false);
                
    }

    protected void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE); // Make sure only "openable" files are shown

        try {
            pickImageLauncher11.launch(intent);
        } catch (android.content.ActivityNotFoundException e) {
            Toast.makeText(requireContext(), "No app found to handle file selection!", Toast.LENGTH_SHORT).show();
            Log.e("MainActivity", "No Activity found to handle Intent", e); //Log the exception
        }
    }
}
