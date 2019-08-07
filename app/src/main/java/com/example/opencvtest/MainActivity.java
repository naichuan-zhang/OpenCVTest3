package com.example.opencvtest;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.FeatureInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.media.Image;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private final int SELECT_PHOTO = 1;
    ImageView ivImage, ivImageProcessed;
    Mat src;
    static int ACTION_MODE = 0;
    private Bitmap selectedImage;

    static {
        if (!(OpenCVLoader.initDebug())) {

        } else {

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ivImage = findViewById(R.id.ivImage);
        ivImageProcessed = findViewById(R.id.ivImageProcessed);

        Intent intent = getIntent();

        if (intent.hasExtra("ACTION_MODE")) {
            ACTION_MODE = intent.getIntExtra("ACTION_MODE", 0);
        }
    }

    private BaseLoaderCallback mOpenCVCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case BaseLoaderCallback.SUCCESS:
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()){
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, mOpenCVCallback);
        } else {
            mOpenCVCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_load_image) {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, SELECT_PHOTO);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        switch (requestCode) {
            case SELECT_PHOTO:
            if (resultCode == RESULT_OK) {
                try {
                    final Uri imageUri = imageReturnedIntent.getData();
                    final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    selectedImage = BitmapFactory.decodeStream(imageStream);
                    src = new Mat(selectedImage.getHeight(), selectedImage.getWidth(), CvType.CV_8UC4);
                    Utils.bitmapToMat(selectedImage, src);

                    switch (ACTION_MODE) {
                        case HomeActivity.MEAN_BLUR:
                            //Imgproc.GaussianBlur(src, src, new Size(3, 3), 0);
                            Mat kernal = new Mat(3, 3, CvType.CV_16SC1);
                            kernal.put(0, 0, 0, -1, 0, -1, 5, -1, 0, -1, 0);
                            //Mat kernalDilate = Imgproc.getStructuringElement(Imgproc.MORPH_DILATE, new Size(3, 3));
                            //Imgproc.dilate(src, src, kernalDilate);
                            //Imgproc.threshold(src, src, 100, 255, Imgproc.THRESH_TRUNC);
                            //Imgproc.cvtColor(src, src, Imgproc.COLOR_BGR2BGRA);
                            //Imgproc.adaptiveThreshold(src, src, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 3, 0);
                            break;
                    }

                    Bitmap processedImage = Bitmap.createBitmap(src.cols(), src.rows(), Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(src, processedImage);
                    ivImage.setImageBitmap(selectedImage);
                    ivImageProcessed.setImageBitmap(processedImage);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            break;
        }
    }

}
