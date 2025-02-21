package com.example.secrets;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.*;
import java.nio.ByteBuffer;

import static android.app.Activity.RESULT_OK;

public class ContentFragment1 extends Fragment {

    private ImageView imageView;
    private Uri imageUri;
    private Uri fileUri;

    @SuppressLint("WrongThread")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.content_fragment_1, container, false);

        imageView = view.findViewById(R.id.imageView1);
        Button selectImageButton = view.findViewById(R.id.frag1_button1);
        Button selectFileButton = view.findViewById(R.id.frag1_button2);
        Button hideFileButton = view.findViewById(R.id.frag1);

        ActivityResultLauncher<Intent> getImageContent = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            imageUri = data.getData();
                            displayImage(imageUri);
                        }
                    }
                });

        // Register activity result launcher for selecting files
        ActivityResultLauncher<Intent> getFileContent = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            fileUri = data.getData();
                        }
                    }
                });

        selectImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("image/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            getImageContent.launch(intent);
        });

        selectFileButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("*/*"); // Allow any type of file
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            getFileContent.launch(intent);
        });

        hideFileButton.setOnClickListener(v -> {
            if (imageUri != null && fileUri != null) {
                try {
                    hideFileInImage(imageUri, fileUri);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        return view;
    }

    private void displayImage(Uri uri) {
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            imageView.setImageBitmap(bitmap); // Display the selected image
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            // Handle exceptions (e.g., show a Toast message)
        }
    }

    private void hideFileInImage(Uri imageUri, Uri fileUri) throws IOException {
        // Read the cover image
        InputStream imageInputStream = requireContext().getContentResolver().openInputStream(imageUri);
        Bitmap bitmap = BitmapFactory.decodeStream(imageInputStream);

        // Read the file to hide
        InputStream fileInputStream = requireContext().getContentResolver().openInputStream(fileUri);
        byte[] fileData = new byte[fileInputStream.available()];
        fileInputStream.read(fileData);
        fileInputStream.close();

        // Get the size of the hidden file as bytes
        byte[] fileSizeBytes = ByteBuffer.allocate(8).putLong(fileData.length).array();

        // Convert Bitmap to ByteArray
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream); // Compress bitmap to PNG format
        byte[] imageData = byteArrayOutputStream.toByteArray();

        // Combine image data and hidden file data
        byte[] combinedData = new byte[imageData.length + fileData.length + 8];
        System.arraycopy(imageData, 0, combinedData, 0, imageData.length);
        System.arraycopy(fileData, 0, combinedData, imageData.length, fileData.length);
        System.arraycopy(fileSizeBytes, 0, combinedData, imageData.length + fileData.length, 8);

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "hidden_image_" + System.currentTimeMillis() + ".png");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures"); // Save in Pictures directory

        Uri savedImageUri = requireContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        try (FileOutputStream outputStream = (FileOutputStream) requireContext().getContentResolver().openOutputStream(savedImageUri)) {
            outputStream.write(combinedData); // Write combined data to output stream
            outputStream.flush();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } catch (Exception e) {
            e.printStackTrace();
            // Handle exceptions (e.g., show a Toast message)
            Toast.makeText(requireContext(), "Error occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}