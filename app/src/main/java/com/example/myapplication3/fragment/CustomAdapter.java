package com.example.myapplication3.fragment;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    private DatabaseReference mDatabaseDislike;
    private DatabaseReference mDatabaseComments;

    public CustomAdapter(Activity context, List<UploadWithKey> uploadList) {
        super(context, R.layout.sample_layout, uploadList);
        this.context = context;
        this.uploadList = uploadList;
        mDatabaseLike = FirebaseDatabase.getInstance().getReference("Likes");
        mDatabaseDislike = FirebaseDatabase.getInstance().getReference("Dislikes");
        mDatabaseComments = FirebaseDatabase.getInstance().getReference("Comments");
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
        ImageView dislikeButton = view.findViewById(R.id.dislikeButton);
        TextView likeCountTextView = view.findViewById(R.id.likeCountTextView);
        TextView dislikeCountTextView = view.findViewById(R.id.dislikeCountTextView);
        EditText commentEditText = view.findViewById(R.id.commentEditText);
        Button commentButton = view.findViewById(R.id.commentButton);
        LinearLayout commentLayout = view.findViewById(R.id.commentContainer); // Change this to LinearLayout

        nameTextView.setText(upload.getImageName());
        Picasso.get().load(upload.getImageUrl()).into(imageView);
        imageDescription.setText(upload.getImageDescrption());

        // Check if the current user has liked this post
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabaseLike.child(uploadKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long likeCount = dataSnapshot.getChildrenCount();
                likeCountTextView.setText(String.valueOf(likeCount)); // Update total like count
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });

        mDatabaseDislike.child(uploadKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long dislikeCount = dataSnapshot.getChildrenCount();
                dislikeCountTextView.setText(String.valueOf(dislikeCount)); // Update total dislike count
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });

        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleLike(uploadKey, likeCountTextView, likeButton);
            }
        });

        dislikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleDislike(uploadKey, dislikeCountTextView, dislikeButton);
            }
        });

        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String commentText = commentEditText.getText().toString().trim();
                if (!commentText.isEmpty()) {
                    addComment(uploadKey, commentText);
                    commentEditText.setText(""); // Clear the EditText after adding comment
                }
            }
        });

        // Display comments for the current post
        displayComments(uploadKey, commentLayout);

        return view;
    }

    private void toggleLike(String uploadKey, TextView likeCountTextView, ImageView likeButton) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference likesRef = mDatabaseLike.child(uploadKey);

        likesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(userId)) {
                    // User has already liked, so unlike it
                    likesRef.child(userId).removeValue();
                } else {
                    // Like the post
                    likesRef.child(userId).setValue(true);
                }

                // Update like count
                likesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        long likeCount = snapshot.getChildrenCount();
                        likeCountTextView.setText(String.valueOf(likeCount)); // Update total like count
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle error
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void toggleDislike(String uploadKey, TextView dislikeCountTextView, ImageView dislikeButton) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference dislikesRef = mDatabaseDislike.child(uploadKey);

        dislikesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(userId)) {
                    // User has already disliked, so remove dislike
                    dislikesRef.child(userId).removeValue();
                } else {
                    // Dislike the post
                    dislikesRef.child(userId).setValue(true);
                }

                // Update dislike count
                dislikesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        long dislikeCount = snapshot.getChildrenCount();
                        dislikeCountTextView.setText(String.valueOf(dislikeCount)); // Update total dislike count
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle error
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }
    private void addComment(String uploadKey, String commentText) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference postCommentsRef = mDatabaseComments.child(uploadKey);
        String commentId = postCommentsRef.push().getKey();
        postCommentsRef.child(commentId).setValue(commentText);
    }
    private void displayComments(String uploadKey, LinearLayout commentLayout) {
        DatabaseReference postCommentsRef = mDatabaseComments.child(uploadKey);

        postCommentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Clear the existing comments from the layout to avoid duplicates
                commentLayout.removeAllViews();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String commentText = snapshot.getValue(String.class);
                    if (commentText != null) {
                        // Create a new TextView for each comment
                        TextView commentTextView = new TextView(context);
                        commentTextView.setText(commentText);
                        commentTextView.setTextSize(16);
                        commentTextView.setPadding(8, 8, 8, 8);

                        // Add the comment TextView to the LinearLayout
                        commentLayout.addView(commentTextView);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

}
