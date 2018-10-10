package com.example.user.ipcis;

import com.example.user.ipcis.*;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Spinner;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.DMatch;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;

import com.googlecode.tesseract.android.*;

import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.engine.OpenCVEngineInterface;
import org.opencv.features2d.*;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.HOGDescriptor;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import static org.opencv.core.CvType.CV_8UC4;

public class ReaderMainActivity extends AppCompatActivity implements PortraitCameraBridgeViewBase.CvCameraViewListener2, View.OnTouchListener {
    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("opencv_java3");
        System.loadLibrary("tess");
    }

    PortraitCameraBridgeViewBase mOpenCvCameraView;// will point to our View widget for our image
    Mat imageMat, gray;
    TextToSpeech tts;
    private  static String TAG="ReaderMainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader_main);
        mOpenCvCameraView = (PortraitCameraBridgeViewBase) findViewById(R.id.cameraView);
        mOpenCvCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        mOpenCvCameraView.setOnTouchListener(this);
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.US);
                }
            }
        });
    }

    //Code will tell us when camera connected it will enable the mOpenCVCameraView
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i("OPENCV", "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this, mLoaderCallback);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }


    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */


    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(PortraitCameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        System.gc();
        imageMat = inputFrame.rgba();
        gray   = inputFrame.gray();
        return imageMat;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        view.performClick();
        Log.d(TAG,"getting inside");

        Mat image01 = gray;
        String toSpeak = "No matches found I think you are not focusing on currency";
        Mat grayImage01 = new Mat(image01.rows(), image01.cols(), CvType.CV_8UC1);
        Imgproc.pyrDown(gray, image01);
        Core.normalize(image01, grayImage01, 0, 255, Core.NORM_MINMAX);
        MatOfKeyPoint keyPoint01 = new MatOfKeyPoint();
        FeatureDetector siftDetector = FeatureDetector.create(FeatureDetector.SIFT);
        siftDetector.detect(grayImage01, keyPoint01);
        //  KeyPoint[] keypoints = keyPoint01.toArray();

        MatOfKeyPoint objectDescriptors = new MatOfKeyPoint();
        DescriptorExtractor siftExtractor = DescriptorExtractor.create(DescriptorExtractor.SIFT);
        siftExtractor.compute(grayImage01, keyPoint01, objectDescriptors);
        //Mat outputImage = new Mat();
        //Scalar newKeypointColor = new Scalar(255, 0, 0);
        //Log.d(TAG,"Drawing key points on object image...");

        //Features2d.drawKeypoints(image01, keyPoint01, outputImage, newKeypointColor, 0);
        boolean flag=false;
        DatabaseHelperKeypoints databaseHelperKeypoints=new DatabaseHelperKeypoints(ReaderMainActivity.this);
      //  DatabaseHelperMat databaseHelperMat=new DatabaseHelperMat(ReaderMainActivity.this);
        DescriptorMatcher descriptorMatcher = DescriptorMatcher.create(DescriptorMatcher.FLANNBASED);
        for (int note = 1; note <= 18; note++) {
            if(flag==true){
                return  false;
            }
            String filename = "mon"+note;
            int id = getResources().getIdentifier(filename, "drawable", getPackageName());
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), id);
            Mat m=new Mat();
            //  Mat m = databaseHelperMat.getKeyPoints(note);
            Utils.bitmapToMat(bitmap, m);
            //  m.convertTo(m,CV_8UC4);

            MatOfKeyPoint sceneKeyPoints = databaseHelperKeypoints.getKeyPoints(note);
            // MatOfKeyPoint sceneKeyPoints = new MatOfKeyPoint();
            MatOfKeyPoint sceneDescriptors = new MatOfKeyPoint();
            //  siftDetector.detect(m, sceneKeyPoints);
            siftExtractor.compute(m, sceneKeyPoints, sceneDescriptors);
            List<MatOfDMatch> matches = new LinkedList<MatOfDMatch>();

            descriptorMatcher.knnMatch(objectDescriptors, sceneDescriptors, matches, 2);
            Log.d(TAG,"Calculating good match list...");

            LinkedList<DMatch> goodMatchesList = new LinkedList<DMatch>();
            float nndrRatio = 0.7f;
            for (int i = 0; i < matches.size(); i++) {
                MatOfDMatch matofDMatch = matches.get(i);
                DMatch[] dmatcharray = matofDMatch.toArray();
                DMatch m1 = dmatcharray[0];
                DMatch m2 = dmatcharray[1];
                if (m1.distance <= m2.distance * nndrRatio) {
                    goodMatchesList.addLast(m1);
                }
            }
            String s="a";

int a=goodMatchesList.size();
            Log.d("main",String.valueOf(a));
            Log.d("main",String.valueOf(note));

            if(goodMatchesList.size() >=50 )
            {
                Log.d("main",s);
                Log.d("main",String.valueOf(a));
                Log.d("main",String.valueOf(note));

            }

            if (goodMatchesList.size() >= 50) {

                switch (note){
                    case 1:  toSpeak = "1000 Nepalese Rupees Front";   flag=true;   break;
                    case 2:  toSpeak = "1000 Nepalese Rupees Front";   flag=true;   break;
                    case 3:  toSpeak = "1 US Dollar Front";  flag=true;   break;
                    case 4:  toSpeak = "2 US Dollar Front";   flag=true;   break;
                    case 5:  toSpeak = "1000 Nepalese Rupees Back";  flag=true;   break;
                    case 6:  toSpeak = "1000 Nepalese Rupees Back";  flag=true;   break;
                    case 7:  toSpeak = "1 US Dollar Back";   flag=true;   break;
                    case 8:  toSpeak = "2 US Dollar Back";  flag=true;   break;
                    case 9:  toSpeak = "5 US Dollar Back";  flag=true;   break;
                    case 10:  toSpeak = "5 US Dollar Front"; flag=true;   break;
                    case 11:  toSpeak = "10 US Dollar Back";  flag=true;   break;
                    case 12: toSpeak = "10 US Dollar Front"; flag=true;   break;
                    case 13: toSpeak = "20 US Dollar Back";  flag=true;   break;
                    case 14: toSpeak = "20 US Dollar Front"; flag=true;   break;
                    case 15: toSpeak = "50 US Dollar Back"; flag=true;   break;
                    case 16: toSpeak = "50 US Dollar Front";flag=true;  break;
                    case 17: toSpeak = "100 US Dollar Back";flag=true;  break;
                    case 18: toSpeak = "100 US Dollar Front";flag=true;  break;
                    default: toSpeak = "No matches found I think you are not focusing on currency "; break;
                }
                Log.d(TAG,"Object Found!!!");
                //   System.out.println("Object Found!!!");
                Toast.makeText(ReaderMainActivity.this,toSpeak,Toast.LENGTH_LONG).show();
                tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
            } else {
                Log.d(TAG,"Currency loop:");
                Log.d(TAG,"Object Not Found");

            }
        }
        if(toSpeak.equals("No matches found I think you are not focusing on currency")){
            Toast.makeText(ReaderMainActivity.this,toSpeak,Toast.LENGTH_LONG).show();
            tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
        }
        return false;
    }
}
