package com.example.myapplication3.fragment;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication3.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CustomAdapter extends ArrayAdapter<UploadWithKey> {
    private Activity context;
    private List<UploadWithKey> uploadList;
    private DatabaseReference mDatabaseLike;

    public CustomAdapter(Activity context, List<UploadWithKey> uploadList) {
        super(context, R.layout.sample_layout, uploadList);
        this.context = context;
        this.uploadList = uploadList;
        mDatabaseLike = FirebaseDatabase.getInstance().getReference("Likes");
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            view = inflater.inflate(R.layout.sample_layout, null, true);
        }

        UploadWithKey uploadWithKey = uploadList.get(position);
        String uploadKey = uploadWithKey.getKey();
        Upload upload = uploadWithKey.getUpload();

        ImageView imageView = view.findViewById(R.id.imageView);
        TextView nameTextView = view.findViewById(R.id.nameTextViewId);
        TextView imageDescription = view.findViewById(R.id.nameDescriptionViewId);
        ImageView likeButton = view.findViewById(R.id.likeButton);
        TextView likeCountTextView = view.findViewById(R.id.likeCountTextView);

        nameTextView.setText(upload.getImageName());
        Picasso.get().load(upload.getImageUrl()).into(imageView);
        imageDescription.setText(upload.getImageDescrption());

        // Check if the current user has liked this post
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabaseLike.child(uploadKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean isLiked = dataSnapshot.hasChild(userId);
                updateLikeButtonState(likeButton, isLiked);
                long likeCount = dataSnapshot.getChildrenCount();
                likeCountTextView.setText(String.valueOf(likeCount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });

        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleLike(uploadKey, likeCountTextView);
            }
        });

        return view;
    }

    private void toggleLike(String uploadKey, TextView likeCountTextView) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabaseLike.child(uploadKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(userId)) {
                    // Post is already liked, so unlike it
                    mDatabaseLike.child(uploadKey).child(userId).removeValue();
                } else {
                    // Like the post
                    mDatabaseLike.child(uploadKey).child(userId).setValue(true);
                }

                // Update the like count after the like/unlike operation
                long likeCount = dataSnapshot.getChildrenCount();
                likeCountTextView.setText(String.valueOf(likeCount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void updateLikeButtonState(ImageView likeButton, boolean isLiked) {
        // Update like button state based on isLiked
    }
}
