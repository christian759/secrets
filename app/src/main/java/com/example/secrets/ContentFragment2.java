package com.example.secrets;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static android.app.Activity.RESULT_OK;

/** @noinspection ALL*/
public class ContentFragment2 extends Fragment {
    private Uri imageUri; // URI of the selected image
    private TextView textViewStatus;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.content_fragment_2, container, false);
        textViewStatus = view.findViewById(R.id.text_view_status);

        Button selectImageButton = view.findViewById(R.id.button_select_image);
        Button revealContentButton = view.findViewById(R.id.button_reveal_content);

        // Register activity result launcher for selecting images
        ActivityResultLauncher<Intent> getImageContent = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            imageUri = data.getData();
                            textViewStatus.setText("Image selected successfully.");
                        }
                    }
                });

        selectImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("image/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            getImageContent.launch(intent);
        });

        revealContentButton.setOnClickListener(v -> {
            if (imageUri != null) {
                revealHiddenContent(imageUri);
            } else {
                textViewStatus.setText("Please select an image first.");
            }
        });

        return view;
    }

    @SuppressLint("SetTextI18n")
    private void revealHiddenContent(Uri imageUri) {
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(imageUri);
            byte[] combinedData = new byte[inputStream.available()];
            inputStream.read(combinedData);
            inputStream.close();

            // Extract the last 8 bytes to get the file size
            byte[] fileSizeBytes = new byte[8];
            System.arraycopy(combinedData, combinedData.length - 8, fileSizeBytes, 0, 8);
            long fileSize = ByteBuffer.wrap(fileSizeBytes).getLong();

            // Validate the file size
            if (fileSize <= 0 || fileSize > combinedData.length - 8) {
                textViewStatus.setText("Error: Invalid file size embedded in the image.");
                return;
            }

            // Extract the hidden file data using the exact size
            byte[] fileData = new byte[(int) fileSize];
            System.arraycopy(combinedData, combinedData.length - 8 - (int) fileSize, fileData, 0, (int) fileSize);

            // Save the extracted data as a new file in the appropriate directory
            saveHiddenFile(fileData);

        } catch (Exception e) {
            e.printStackTrace();
            textViewStatus.setText("Error revealing content.");
        }
    }

    private void saveHiddenFile(byte[] fileData) {
        try {
            // Determine the appropriate filename and extension based on user input or context
            String filename = "hidden_file_" + System.currentTimeMillis(); // Base filename

            // For demonstration purposes, let's assume we want to save it as .img by default.
            String extension = ".pdf"; // Change this based on your needs or user selection

            filename += extension; // Append extension to filename

            // Define where to save the extracted file
            ContentValues values = new ContentValues();
            values.put(MediaStore.Files.FileColumns.DISPLAY_NAME, filename);
            values.put(MediaStore.Files.FileColumns.MIME_TYPE, getMimeType(extension)); // Set MIME type based on extension
            values.put(MediaStore.Files.FileColumns.RELATIVE_PATH, "Documents/HiddenFiles"); // Save in Documents/HiddenFiles

            Uri savedFileUri = requireContext().getContentResolver().insert(MediaStore.Files.getContentUri("external"), values);

            if (savedFileUri != null) {
                try (FileOutputStream outputStream = (FileOutputStream) requireContext().getContentResolver().openOutputStream(savedFileUri)) {
                    outputStream.write(fileData); // Write extracted data to output stream
                    outputStream.flush();
                    textViewStatus.setText("Hidden content saved successfully as " + filename);
                }
            } else {
                textViewStatus.setText("Error saving hidden content.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            textViewStatus.setText("Error saving hidden content.");
        }
    }

    private String getMimeType(String extension) {
        switch (extension.toLowerCase()) {
            case ".jpg":
                return "image/jpeg";
            case ".png":
                return "image/png";
            case ".pdf":
                return "application/pdf";
            case ".txt":
                return "text/plain";
            case ".docx":
                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case ".img":
                return "application/octet-stream"; // Generic binary type for .img files
            default:
                return "*/*"; // Fallback for unknown types
        }
    }
}

