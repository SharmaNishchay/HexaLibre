package com.darkhex.hexalibre.ui.home;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.darkhex.hexalibre.Book;
import com.darkhex.hexalibre.BookDescription;
import com.darkhex.hexalibre.BookSearch;
import com.darkhex.hexalibre.BookSearchCallback;
import com.darkhex.hexalibre.MainActivity2;
import com.darkhex.hexalibre.MainActivity;
import com.darkhex.hexalibre.R;
import com.darkhex.hexalibre.databinding.FragmentHomeBinding;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private MainActivity2 mainActivity2;
    private ExecutorService cameraExecutor;
    private boolean isCameraActive = false;
    private ProcessCameraProvider cameraProvider;

    public void filterData(String query){
        mainActivity2.filterBooks(query);
    }
    public void toggleCategory(boolean b){
        if(b)
            binding.categories.setVisibility(View.GONE);
        else
            binding.categories.setVisibility(View.VISIBLE);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        binding.startCameraButton.setOnClickListener(v -> toggleCamera());
        cameraExecutor = Executors.newSingleThreadExecutor();
        Animation fadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        TextView elevatedTextView = root.findViewById(R.id.elevated_text_view);
        elevatedTextView.setText(Html.fromHtml(getString(R.string.welcome_message), Html.FROM_HTML_MODE_LEGACY));
        elevatedTextView.startAnimation(fadeIn);
        elevatedTextView.setVisibility(View.VISIBLE);
        mainActivity2 = new MainActivity2(root);

        return root;
    }
    private void startCamera() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA )!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, 101);
        } else {
            initializeCamera();
            binding.scrollMenu.setVisibility(View.GONE);
        }
    }
    private void stopCamera() {
        if (cameraProvider != null) {
            cameraProvider.unbindAll();
        }
        binding.scrollMenu.setVisibility(View.VISIBLE);
        binding.previewView.setVisibility(View.GONE);
        isCameraActive = false;
    }
    private void toggleCamera() {
        if (isCameraActive) {
            stopCamera();  // Method to stop the camera
        } else {
            startCamera();
        }
    }
    private void initializeCamera() {
        if (isCameraActive) {
            return;
        }

        binding.previewView.setVisibility(View.VISIBLE);
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());
        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();
                bindCameraUseCases(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    private void bindCameraUseCases(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder().build();
        CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setTargetResolution(new android.util.Size(1280, 720))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        imageAnalysis.setAnalyzer(cameraExecutor, this::analyzeImage);

        cameraProvider.unbindAll();
        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);
        preview.setSurfaceProvider(binding.previewView.getSurfaceProvider());

        isCameraActive = true;
    }

    @OptIn(markerClass = ExperimentalGetImage.class)
    private void analyzeImage(ImageProxy imageProxy) {
        @androidx.camera.core.ExperimentalGetImage
        ImageProxy.PlaneProxy plane = imageProxy.getPlanes()[0];
        BookSearch bs=new BookSearch();
        if (imageProxy.getImage() != null) {
            InputImage image = InputImage.fromMediaImage(imageProxy.getImage(), imageProxy.getImageInfo().getRotationDegrees());
            BarcodeScanner scanner = BarcodeScanning.getClient();

            scanner.process(image)
                    .addOnSuccessListener(barcodes -> {
                        String rawValue="";
                        for (Barcode barcode : barcodes) {
                            rawValue = barcode.getRawValue();
                            Log.d("Home","All codes : "+rawValue);
                        }

                        if(rawValue!=null&& !rawValue.isEmpty()) {  // Check if rawValue is not null and not empty
                            Toast.makeText(requireContext(), "Barcode: " + rawValue, Toast.LENGTH_SHORT).show();
                            stopCamera();
                            bs.searchUserByEmail(rawValue, new BookSearchCallback() {
                                @Override
                                public void onBookFound(String uid) {
                                    Log.d("Home", uid);
                                    showConfirmationDialog(uid);
                                }

                                @Override
                                public void onBookNotFound(String s) {
                                    Log.d("Home", "Book Not found with ISBN: "+s);
                                    showPreview(s);
                                }
                            });

                        }
                    })
                    .addOnFailureListener(Throwable::printStackTrace)
                    .addOnCompleteListener(task -> imageProxy.close());
        }
    }

    private void showConfirmationDialog(String bookName) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Book Found")
                .setMessage("Do you want to select the book: " + bookName + "?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    Toast.makeText(requireContext(), "Book issued: " + bookName, Toast.LENGTH_SHORT).show();
                    // Add any additional actions if needed when user confirms
                })
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }


    private void showPreview(String isbn) {
        OkHttpClient client = new OkHttpClient();
        String url = "https://openlibrary.org/api/books?bibkeys=ISBN:" + isbn + "&format=json&jscmd=data";

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                requireActivity().runOnUiThread(() ->
                        Log.d("Home","Failed to fetch data")
                );
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String responseData = response.body().string();
                    Log.d("Home","Success to fetch data "+isbn);
                    try {
                        JSONObject jsonObject = new JSONObject(responseData);
                        JSONObject bookData = jsonObject.getJSONObject("ISBN:" + isbn);

                        String title = bookData.optString("title", "Unknown Title");
                        Log.d("Home","Title: "+title);
                        String author = "Unknown Author";

                        if (bookData.has("authors")) {
                            JSONObject authorObject = bookData.getJSONArray("authors").getJSONObject(0);
                            author = authorObject.optString("name", "Unknown Author");
                            Log.d("Home","Author: "+author);
                        }

                        String finalAuthor = author;
                        String finalTitle = title;

                        requireActivity().runOnUiThread(() -> {
                            new AlertDialog.Builder(requireContext())
                                    .setTitle("Book Found")
                                    .setMessage("Do you want to select the book: " + finalTitle + " by " + finalAuthor + "?")
                                    .setPositiveButton("Yes", (dialog, which) -> {
                                        Toast.makeText(requireContext(), "Book issued: " + finalTitle, Toast.LENGTH_SHORT).show();
                                        // Additional actions if needed
                                    })
                                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                                    .show();
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(requireContext(), "Error parsing book details", Toast.LENGTH_SHORT).show()
                        );
                    }
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        cameraExecutor.shutdown();
    }
    public void onBackPressed() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).handleDoubleBackPress(); // Call the method in MainActivity2 to handle double back press
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initializeCamera();
        } else {
            Toast.makeText(requireContext(), "Camera permission is required to use this feature", Toast.LENGTH_SHORT).show();
        }
    }

}

