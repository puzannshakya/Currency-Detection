package com.example.user.ipcis;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.FeatureDetector;
import org.opencv.imgproc.Imgproc;

public class KeypointsActivity extends AppCompatActivity {
    Button mButton;

    ImageView imageView;
    TextView textView;

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("opencv_java3");
        System.loadLibrary("tess");
    }

    // PortraitCameraBridgeViewBase mOpenCvCameraView;// will point to our View widget for our image
    Mat imageMat, gray;
    TextToSpeech tts;
    private static String TAG = "KeypointsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keypoints);
        mButton = (Button) findViewById(R.id.btnKeypoint);
        imageView = (ImageView) findViewById(R.id.ivKeypoint);
        final TextView textView = (TextView) findViewById(R.id.tvKeypoint);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                for (int note = 1; note <= 18; note++) {

                    String filename = "mon"+note;
                    int id = getResources().getIdentifier(filename, "drawable", getPackageName());
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), id);
                    FeatureDetector siftDetector = FeatureDetector.create(FeatureDetector.SIFT);
                    //DescriptorExtractor siftExtractor = DescriptorExtractor.create(DescriptorExtractor.SIFT);
                    Mat m = new Mat();
                    Utils.bitmapToMat(bitmap, m);
                    Imgproc.cvtColor(m, m, Imgproc.COLOR_RGBA2GRAY);
                    MatOfKeyPoint sceneKeyPoints = new MatOfKeyPoint();
                    //MatOfKeyPoint sceneDescriptors = new MatOfKeyPoint();
                    siftDetector.detect(m, sceneKeyPoints);
                    //siftExtractor.compute(m, sceneKeyPoints, sceneDescriptors);
                    //  Scalar newKeypointColor = new Scalar(255, 0, 0);

                    DatabaseHelperKeypoints databaseHelperKeypoints=new DatabaseHelperKeypoints(KeypointsActivity.this);


                    long chck= databaseHelperKeypoints.insertKeyPoints(note,sceneKeyPoints);


                    if(chck!= -1)
                    {
                        Log.d(TAG,"Note's Keypoints Added " + note);

                    }
                    else
                        Log.d(TAG,"Note's Keypoints not Added " + note);

              /*  MatOfKeyPoint keyPointsFromDb=databaseHelperKeypoints.getKeyPoints(6);

                Features2d.drawKeypoints(m, keyPointsFromDb, m, newKeypointColor, 0);


                Utils.matToBitmap(m, bitmap);*/

                    // imageView.setImageBitmap(bitmap);



                }

                textView.setText("ALL Key point Added");


            }
        });


    }


}

