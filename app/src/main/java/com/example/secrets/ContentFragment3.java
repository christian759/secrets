package com.example.secrets;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ContentFragment3 extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ImageView imageView11 = requireActivity().findViewById(R.id.imageView1);
        ActivityResultLauncher<Intent> pickImageLauncher11;
        Button button1 = requireActivity().findViewById(R.id.frag1_button1);


        return inflater.inflate(R.layout.content_fragment_3, container, false);
    }
}

