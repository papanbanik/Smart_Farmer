package com.example.myapplication3.fragment;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.myapplication3.R;
import com.example.myapplication3.ml.Model;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;

public class HomeFragment extends Fragment {
    TextView result;
    ImageView imageView;
    Button picture;
    int imageSize = 96;
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    @SuppressLint("MissingInflatedId")
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        result = rootView.findViewById(R.id.result);
        imageView = rootView.findViewById(R.id.imageView);
        picture = rootView.findViewById(R.id.button);

        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launch camera if we have permission
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
                } else {
                    // Request camera permission if we don't have it
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
                }
            }
        });
        return rootView;
    }

    public void classifyImage(Bitmap image) {
        try {
            // Load the TensorFlow Lite model
            Model model = Model.newInstance(requireContext());

            // Allocate buffer for 96x96x1 (Grayscale image)
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * 96 * 96); // 96x96x1 grayscale, 4 bytes per float
            byteBuffer.order(ByteOrder.nativeOrder());

            // Convert the image to grayscale
            Bitmap grayImage = convertToGrayscale(image);

            // Resize the grayscale image to 96x96
            Bitmap resizedImage = Bitmap.createScaledBitmap(grayImage, 96, 96, true);

            // Get pixel values of the grayscale image
            int[] intValues = new int[96 * 96];
            resizedImage.getPixels(intValues, 0, resizedImage.getWidth(), 0, 0, resizedImage.getWidth(), resizedImage.getHeight());

            // Iterate over pixels and add the grayscale values to the byte buffer
            for (int pixelValue : intValues) {
                float normalizedValue = (pixelValue & 0xFF) * (1.f / 255.f); // Normalize between 0 and 1
                byteBuffer.putFloat(normalizedValue);
            }

            // Create input tensor with shape [1, 96, 96, 1] for the grayscale image
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 96, 96, 1}, DataType.FLOAT32);
            inputFeature0.loadBuffer(byteBuffer);

            // Run the model and get the result
            Model.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            // Process the output from the model (e.g., getting classification results)
            float[] confidences = outputFeature0.getFloatArray();
            int maxPos = 0;
            float maxConfidence = 0;
            for (int i = 0; i < confidences.length; i++) {
                if (confidences[i] > maxConfidence) {
                    maxConfidence = confidences[i];
                    maxPos = i;
                }
            }

            // Predefined list of classes for classification
            String[] classes = {
                    "টমেটো লিফ কার্ল",
                    "টমেটোর বিলম্বিত ধ্বসা রোগ",
                    "টমেটোর আগাম ধ্বসা রোগ",
                    "আলুর আগাম ধ্বসা রোগ",
                    "আলুর বিলম্বিত ধ্বসা রোগ",
                    "মরিচের ব্যাকটেরিয়াজনিত দাগ রোগ"
            };


            if (maxPos == 0) { // Specific class for which you want to show "Read more"
                String description = "              টমেটো লিফ কার্ল \n \n টমেটো লিফ কার্ল  বা টমেটো পাতার মোচড় একটি সাধারণ রোগ যা মূলত ভাইরাস দ্বারা সৃষ্ট হয়। এই রোগে টমেটো গাছের পাতাগুলি উপরের দিকে বা পাশের দিকে ঘুরে যায় এবং কখনও কখনও পাতাগুলি মোচড়ানো বা শুকিয়ে যায়। এই রোগটি গাছের বৃদ্ধি কমিয়ে দেয় এবং ফলনেও প্রভাব ফেলে";
                String readMoreText = "Read more";

                // Create a SpannableString for the description and "Read more" link
                SpannableString spannable = new SpannableString(description + " " + readMoreText);

                // Set up the clickable span for "Read more"
                ClickableSpan clickableSpan = new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View widget) {
                        // Open the link when "Read more" is clicked
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://krishi.gov.bd/pest/706"));
                        startActivity(browserIntent);
                    }
                };

                // Apply the clickable span to "Read more"
                spannable.setSpan(clickableSpan, description.length() + 1, spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                // Set the SpannableString to the TextView and enable links
                result.setText(spannable);
                result.setMovementMethod(LinkMovementMethod.getInstance());
            }
            else if (maxPos == 1) { // Specific class for which you want to show "Read more"
                String description = "      টমেটোর বিলম্বিত/নাবী ধ্বসা রোগ \n\n (Late Blight) একটি ছত্রাকজনিত রোগ যা Phytophthora infestans নামক ছত্রাক দ্বারা সৃষ্ট। এটি বিশেষ করে বর্ষাকালে এবং শীতল, আর্দ্র আবহাওয়ায় দ্রুত ছড়িয়ে পড়ে।   ";
                String readMoreText = "  Read more";

                // Create a SpannableString for the description and "Read more" link
                SpannableString spannable = new SpannableString(description + " " + readMoreText);

                // Set up the clickable span for "Read more"
                ClickableSpan clickableSpan = new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View widget) {
                        // Open the link when "Read more" is clicked
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://krishi.gov.bd/pest/709"));
                        startActivity(browserIntent);
                    }
                };

                // Apply the clickable span to "Read more"
                spannable.setSpan(clickableSpan, description.length() + 1, spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                // Set the SpannableString to the TextView and enable links
                result.setText(spannable);
                result.setMovementMethod(LinkMovementMethod.getInstance());
            }
            else if (maxPos == 2) { // Specific class for which you want to show "Read more"
                String description = "              টমেটোর আগাম ধ্বসা রোগ \n\n টমেটোর আগাম ধ্বসা রোগ  (Early Blight) একটি ফাঙ্গাসজনিত রোগ, যা মূলত Alternaria solani ফাঙ্গাস দ্বারা সৃষ্ট। এই রোগটি টমেটো গাছের পাতায়, কাণ্ডে এবং ফলের উপর আক্রমণ করে, যার ফলে গাছের উৎপাদন কমে যায় এবং ফলন নষ্ট হয়ে যায়\n";
                String readMoreText = "Read more";

                // Create a SpannableString for the description and "Read more" link
                SpannableString spannable = new SpannableString(description + " " + readMoreText);

                // Set up the clickable span for "Read more"
                ClickableSpan clickableSpan = new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View widget) {
                        // Open the link when "Read more" is clicked
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://krishi.gov.bd/pest/708"));
                        startActivity(browserIntent);
                    }
                };

                // Apply the clickable span to "Read more"
                spannable.setSpan(clickableSpan, description.length() + 1, spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                // Set the SpannableString to the TextView and enable links
                result.setText(spannable);
                result.setMovementMethod(LinkMovementMethod.getInstance());
            }
           else if (maxPos == 3) { // Specific class for which you want to show "Read more"
                String description = "              আলুর আগাম ধ্বসা রোগ \n\n আলুর আগাম ধ্বসা রোগ (Early Blight) একটি ফাঙ্গাসজনিত রোগ, যা মূলত Alternaria solani ফাঙ্গাসের কারণে হয়ে থাকে। এই রোগে আলুর গাছের পাতা, কাণ্ড এবং আলুর গায়ে দাগ দেখা যায়, যা গাছের বৃদ্ধি ও আলুর গুণগত মানে প্রভাব ফেলে। এই রোগটি সাধারণত গ্রীষ্ম ও বর্ষাকালে বেশি দেখা যায়, বিশেষত আর্দ্র ও উষ্ণ পরিবেশে।";
                String readMoreText = "Read more";

                // Create a SpannableString for the description and "Read more" link
                SpannableString spannable = new SpannableString(description + " " + readMoreText);

                // Set up the clickable span for "Read more"
                ClickableSpan clickableSpan = new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View widget) {
                        // Open the link when "Read more" is clicked
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://krishi.gov.bd/pest/593"));
                        startActivity(browserIntent);
                    }
                };

                // Apply the clickable span to "Read more"
                spannable.setSpan(clickableSpan, description.length() + 1, spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                // Set the SpannableString to the TextView and enable links
                result.setText(spannable);
                result.setMovementMethod(LinkMovementMethod.getInstance());
            }
           else if (maxPos == 4) { // Specific class for which you want to show "Read more"
               String description ="           আলুর বিলম্বিত ধ্বসা রোগ \n\n আলুর বিলম্বিত ধ্বসা রোগ (Late Blight) একটি মারাত্মক ফাঙ্গাসজনিত রোগ, যা Phytophthora infestans নামক ফাঙ্গাসের কারণে হয়। এই রোগটি আলুর গাছের পাতার, কাণ্ডের এবং আলুর গায়ে আক্রমণ করে এবং ফসলের ব্যাপক ক্ষতি সাধন করে। বিলম্বিত ধ্বসা রোগটি সাধারণত বর্ষাকাল বা আর্দ্র পরিবেশে দ্রুত ছড়িয়ে পড়ে";
                String readMoreText = "Read more";

                // Create a SpannableString for the description and "Read more" link
                SpannableString spannable = new SpannableString(description + " " + readMoreText);

                // Set up the clickable span for "Read more"
                ClickableSpan clickableSpan = new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View widget) {
                        // Open the link when "Read more" is clicked
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://sattacademy.com/admission/%E0%A6%86%E0%A6%B2%E0%A7%81%E0%A6%B0-%E0%A6%AC%E0%A6%BF%E0%A6%B2%E0%A6%AE%E0%A7%8D%E0%A6%AC%E0%A6%BF%E0%A6%A4-%E0%A6%A7%E0%A7%8D%E0%A6%AC%E0%A6%B8%E0%A6%BE-%E0%A6%B0%E0%A7%8B%E0%A6%97"));
                        startActivity(browserIntent);
                    }
                };

                // Apply the clickable span to "Read more"
                spannable.setSpan(clickableSpan, description.length() + 1, spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                // Set the SpannableString to the TextView and enable links
                result.setText(spannable);
                result.setMovementMethod(LinkMovementMethod.getInstance());
            }


            else if (maxPos == 5) { // Specific class for which you want to show "Read more"
                String description="        মরিচের ব্যাকটেরিয়াজনিত দাগ রোগ \n\n (Bacterial Spot of Chili) একটি মারাত্মক ব্যাকটেরিয়া সংক্রমিত রোগ, যা মরিচের গাছের পাতার, কাণ্ডের এবং ফলের উপর দাগ সৃষ্টি করে। এই রোগটি সাধারণত Xanthomonas campestris ব্যাকটেরিয়ার কারণে হয় এবং গাছের উৎপাদন কমিয়ে দেয়।";
                    String readMoreText = "Read more";

                // Create a SpannableString for the description and "Read more" link
                SpannableString spannable = new SpannableString(description + " " + readMoreText);

                // Set up the clickable span for "Read more"
                ClickableSpan clickableSpan = new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View widget) {
                        // Open the link when "Read more" is clicked
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://plantix.net/bn/library/plant-diseases/300003/bacterial-spot-of-pepper/"));
                        startActivity(browserIntent);
                    }
                };

                // Apply the clickable span to "Read more"
                spannable.setSpan(clickableSpan, description.length() + 1, spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                // Set the SpannableString to the TextView and enable links
                result.setText(spannable);
                result.setMovementMethod(LinkMovementMethod.getInstance());
            }


            // Close the model to release resources
            model.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Grayscale conversion method
    private Bitmap convertToGrayscale(Bitmap src) {
        Bitmap grayBitmap = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(grayBitmap);
        Paint paint = new Paint();
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0); // Set saturation to 0 to convert to grayscale
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrix);
        paint.setColorFilter(filter);
        canvas.drawBitmap(src, 0, 0, paint);
        return grayBitmap;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            assert data != null;
            Bitmap image = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
            image = ThumbnailUtils.extractThumbnail(image, imageSize, imageSize);
            imageView.setImageBitmap(image);
            classifyImage(image);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
