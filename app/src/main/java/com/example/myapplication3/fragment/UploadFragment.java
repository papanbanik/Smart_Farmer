package com.example.myapplication3.fragment;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication3.R;
import com.example.myapplication3.fragment.Upload;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

public class UploadFragment extends Fragment implements View.OnClickListener {

    private Button chooseButton, saveButton;
    private ImageView imageView;
    private EditText imageNameText, imageDescriptionText;
    private ProgressBar progressBar;
    private Uri imageUri;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    StorageTask uploadTask;
    private static final int IMAGE_REQUEST = 1;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload, container, false);

        chooseButton = view.findViewById(R.id.chooseButton);
        saveButton = view.findViewById(R.id.saveButton);
        imageView = view.findViewById(R.id.imageView);
        imageNameText = view.findViewById(R.id.imageNameEditText);
        imageDescriptionText = view.findViewById(R.id.imageDescriptionEditText);
        progressBar = view.findViewById(R.id.progressBar);
        databaseReference = FirebaseDatabase.getInstance().getReference("upload");
        storageReference = FirebaseStorage.getInstance().getReference("upload");

        chooseButton.setOnClickListener(this);
        saveButton.setOnClickListener(this);

        // Add text change listener to check name existence as user types
        imageNameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not used
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Check if the entered name exists in the database
                checkNameExistence(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not used
            }
        });

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.chooseButton) {
            openFileChooser();
        } else if (v.getId() == R.id.saveButton) {
            saveData();
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(imageView);
        }
    }

    public String getFileExtension(Uri imageUri) {
        ContentResolver contentResolver = getActivity().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageUri));
    }

    private void saveData() {
        String imageName = imageNameText.getText().toString().trim();
        String imageDescriptionName = imageDescriptionText.getText().toString().trim();

        if (imageName.isEmpty()) {
            imageNameText.setError("Enter the image name");
            imageNameText.requestFocus();
            return;
        }
        if (imageDescriptionName.isEmpty()) {
            imageDescriptionText.setError("Enter the image description");
            imageDescriptionText.requestFocus();
            return;
        }
        if (imageUri != null) {
            StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
            fileReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Get the URL of the uploaded image
                        fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            Upload upload = new Upload(imageName, uri.toString(), imageDescriptionName);
                            String uploadId = databaseReference.push().getKey();
                            if (uploadId != null) {
                                databaseReference.child(uploadId).setValue(upload);
                            }
                            progressBar.setVisibility(View.GONE);
                            imageNameText.setText("");
                            imageDescriptionText.setText("");
                            imageView.setImageResource(android.R.color.transparent); // Clear the imageView
                        });
                    })
                    .addOnFailureListener(e -> {
                        // Handle unsuccessful uploads
                    })
                    .addOnProgressListener(taskSnapshot -> {
                        // Track upload progress
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        progressBar.setProgress((int) progress);
                    });
        }
    }

    private void checkNameExistence(String name) {
        databaseReference.orderByChild("imageName").equalTo(name)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {

                            Toast.makeText(getActivity(), "Name already exists", Toast.LENGTH_SHORT).show();
                        } else {

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle errors here
                    }
                });
    }
}
