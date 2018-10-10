package com.example.user.ipcis;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import org.opencv.R;
import org.opencv.android.FpsMeter;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Size;

import java.util.List;


public abstract class PortraitCameraBridgeViewBase extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "CameraBridge";
    private static final int MAX_UNSPECIFIED = -1;
    private static final int STOPPED = 0;
    private static final int STARTED = 1;

    private int mState = STOPPED;
    private Bitmap mCacheBitmap;
    private CvCameraViewListener2 mListener;
    private boolean mSurfaceExist;
    private Object mSyncObject = new Object();

    protected int mFrameWidth;
    protected int mFrameHeight;
    protected int mMaxHeight;
    protected int mMaxWidth;
    protected float mScale = 0;
    protected int mPreviewFormat = RGBA;
    protected int mCameraIndex = CAMERA_ID_ANY;
    protected boolean mEnabled;
    protected FpsMeter mFpsMeter = null;

    public static final int CAMERA_ID_ANY = -1;
    public static final int CAMERA_ID_BACK = 99;
    public static final int CAMERA_ID_FRONT = 98;
    public static final int RGBA = 1;
    public static final int GRAY = 2;

    public PortraitCameraBridgeViewBase(Context context, int cameraId) {
        super(context);
        mCameraIndex = cameraId;
        getHolder().addCallback(this);
        mMaxWidth = MAX_UNSPECIFIED;
        mMaxHeight = MAX_UNSPECIFIED;
    }

    public PortraitCameraBridgeViewBase(Context context, AttributeSet attrs) {
        super(context, attrs);

        int count = attrs.getAttributeCount();
        Log.d(TAG, "Attr count: " + Integer.valueOf(count));

        TypedArray styledAttrs = getContext().obtainStyledAttributes(attrs, R.styleable.CameraBridgeViewBase);
        if (styledAttrs.getBoolean(R.styleable.CameraBridgeViewBase_show_fps, false))
            enableFpsMeter();

        mCameraIndex = styledAttrs.getInt(R.styleable.CameraBridgeViewBase_camera_id, -1);

        getHolder().addCallback(this);
        mMaxWidth = MAX_UNSPECIFIED;
        mMaxHeight = MAX_UNSPECIFIED;
        styledAttrs.recycle();
    }


    public void setCameraIndex(int cameraIndex) {
        this.mCameraIndex = cameraIndex;
    }

    public interface CvCameraViewListener {

        public void onCameraViewStarted(int width, int height);


        public void onCameraViewStopped();


        public Mat onCameraFrame(Mat inputFrame);
    }

    public interface CvCameraViewListener2 {

        public void onCameraViewStarted(int width, int height);


        public void onCameraViewStopped();


        public Mat onCameraFrame(CvCameraViewFrame inputFrame);
    }

    ;

    protected class CvCameraViewListenerAdapter implements CvCameraViewListener2 {
        public CvCameraViewListenerAdapter(CvCameraViewListener oldStypeListener) {
            mOldStyleListener = oldStypeListener;
        }

        public void onCameraViewStarted(int width, int height) {
            mOldStyleListener.onCameraViewStarted(width, height);
        }

        public void onCameraViewStopped() {
            mOldStyleListener.onCameraViewStopped();
        }

        public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
            Mat result = null;
            switch (mPreviewFormat) {
                case RGBA:
                    result = mOldStyleListener.onCameraFrame(inputFrame.rgba());
                    break;
                case GRAY:
                    result = mOldStyleListener.onCameraFrame(inputFrame.gray());
                    break;
                default:
                    Log.e(TAG, "Invalid frame format! Only RGBA and Gray Scale are supported!");
            }
            ;

            return result;
        }

        public void setFrameFormat(int format) {
            mPreviewFormat = format;
        }

        private int mPreviewFormat = RGBA;
        private CvCameraViewListener mOldStyleListener;
    }

    ;


    public interface CvCameraViewFrame {


        public Mat rgba();


        public Mat gray();
    }

    ;

    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        Log.d(TAG, "call surfaceChanged event");
        synchronized (mSyncObject) {
            if (!mSurfaceExist) {
                mSurfaceExist = true;
                checkCurrentState();
            } else {
                /** Surface changed. We need to stop camera and restart with new parameters */
                /* Pretend that old surface has been destroyed */
                mSurfaceExist = false;
                checkCurrentState();
                /* Now use new surface. Say we have it now */
                mSurfaceExist = true;
                checkCurrentState();
            }
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        /* Do nothing. Wait until surfaceChanged delivered */
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        synchronized (mSyncObject) {
            mSurfaceExist = false;
            checkCurrentState();
        }
    }


    public void enableView() {
        synchronized (mSyncObject) {
            mEnabled = true;
            checkCurrentState();
        }
    }


    public void disableView() {
        synchronized (mSyncObject) {
            mEnabled = false;
            checkCurrentState();
        }
    }


    public void enableFpsMeter() {
        if (mFpsMeter == null) {
            mFpsMeter = new FpsMeter();
            mFpsMeter.setResolution(mFrameWidth, mFrameHeight);
        }
    }

    public void disableFpsMeter() {
        mFpsMeter = null;
    }



    public void setCvCameraViewListener(CvCameraViewListener2 listener) {
        mListener = listener;
    }

    public void setCvCameraViewListener(CvCameraViewListener listener) {
        CvCameraViewListenerAdapter adapter = new CvCameraViewListenerAdapter(listener);
        adapter.setFrameFormat(mPreviewFormat);
        mListener = adapter;
    }


    public void setMaxFrameSize(int maxWidth, int maxHeight) {
        mMaxWidth = maxWidth;
        mMaxHeight = maxHeight;
    }

    public void SetCaptureFormat(int format) {
        mPreviewFormat = format;
        if (mListener instanceof CvCameraViewListenerAdapter) {
            CvCameraViewListenerAdapter adapter = (CvCameraViewListenerAdapter) mListener;
            adapter.setFrameFormat(mPreviewFormat);
        }
    }


    private void checkCurrentState() {
        Log.d(TAG, "call checkCurrentState");
        int targetState;

        if (mEnabled && mSurfaceExist && getVisibility() == VISIBLE) {
            targetState = STARTED;
        } else {
            targetState = STOPPED;
        }

        if (targetState != mState) {
            /* The state change detected. Need to exit the current state and enter target state */
            processExitState(mState);
            mState = targetState;
            processEnterState(mState);
        }
    }

    private void processEnterState(int state) {
        Log.d(TAG, "call processEnterState: " + state);
        switch (state) {
            case STARTED:
                onEnterStartedState();
                if (mListener != null) {
                    mListener.onCameraViewStarted(mFrameWidth, mFrameHeight);
                }
                break;
            case STOPPED:
                onEnterStoppedState();
                if (mListener != null) {
                    mListener.onCameraViewStopped();
                }
                break;
        }
        ;
    }

    private void processExitState(int state) {
        Log.d(TAG, "call processExitState: " + state);
        switch (state) {
            case STARTED:
                onExitStartedState();
                break;
            case STOPPED:
                onExitStoppedState();
                break;
        }
        ;
    }

    private void onEnterStoppedState() {
        /* nothing to do */
    }

    private void onExitStoppedState() {
        /* nothing to do */
    }

    // NOTE: The order of bitmap constructor and camera connection is important for android 4.1.x
    // Bitmap must be constructed before surface
    private void onEnterStartedState() {
        Log.d(TAG, "call onEnterStartedState");
        /* Connect camera */
        if (!connectCamera(getWidth(), getHeight())) {
            AlertDialog ad = new AlertDialog.Builder(getContext()).create();
            ad.setCancelable(false); // This blocks the 'BACK' button
            ad.setMessage("It seems that you device does not support camera (or it is locked). Application will be closed.");
            ad.setButton(DialogInterface.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    ((Activity) getContext()).finish();
                }
            });
            ad.show();

        }
    }

    private void onExitStartedState() {
        disconnectCamera();
        if (mCacheBitmap != null) {
            mCacheBitmap.recycle();
        }
    }


    protected void deliverAndDrawFrame(CvCameraViewFrame frame) {
        Mat modified;

        if (mListener != null) {
            modified = mListener.onCameraFrame(frame);
        } else {
            modified = frame.rgba();
        }

        boolean bmpValid = true;
        if (modified != null) {
            try {
                Utils.matToBitmap(modified, mCacheBitmap);
            } catch (Exception e) {
                Log.e(TAG, "Mat type: " + modified);
                Log.e(TAG, "Bitmap type: " + mCacheBitmap.getWidth() + "*" + mCacheBitmap.getHeight());
                Log.e(TAG, "Utils.matToBitmap() throws an exception: " + e.getMessage());
                bmpValid = false;
            }
        }

        if (bmpValid && mCacheBitmap != null) {
            Canvas canvas = getHolder().lockCanvas();
            if (canvas != null) {
                canvas.drawColor(0, android.graphics.PorterDuff.Mode.CLEAR);

                Log.d(TAG, String.format("scale: %f, canvas width x height: %d x %d, bitmap width x height:%d x %d ", mScale, canvas.getWidth(), canvas.getHeight(), mCacheBitmap.getWidth(), mCacheBitmap.getHeight()));

                // 이미지 세로 회전
                Matrix matrix = new Matrix();
                matrix.postRotate(90f);
                Bitmap bitmap = Bitmap.createBitmap(mCacheBitmap, 0, 0, mCacheBitmap.getWidth(), mCacheBitmap.getHeight(), matrix, true);

                if (mScale != 0) {
                    canvas.drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()),
                            new Rect((int) ((canvas.getWidth() - mScale * bitmap.getWidth()) / 2),
                                    (int) ((canvas.getHeight() - mScale * bitmap.getHeight()) / 2),
                                    (int) ((canvas.getWidth() - mScale * bitmap.getWidth()) / 2 + mScale * bitmap.getWidth()),
                                    (int) ((canvas.getHeight() - mScale * bitmap.getHeight()) / 2 + mScale * bitmap.getHeight())), null);
                } else {
                    canvas.drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()),
                            new Rect((canvas.getWidth() - bitmap.getWidth()) / 2,
                                    (canvas.getHeight() - bitmap.getHeight()) / 2,
                                    (canvas.getWidth() - bitmap.getWidth()) / 2 + bitmap.getWidth(),
                                    (canvas.getHeight() - bitmap.getHeight()) / 2 + bitmap.getHeight()), null);
                }

                if (mFpsMeter != null) {
                    mFpsMeter.measure();
                    mFpsMeter.draw(canvas, 20, 30);
                }
                getHolder().unlockCanvasAndPost(canvas);
            }
        }
    }


    protected abstract boolean connectCamera(int width, int height);


    protected abstract void disconnectCamera();

    // NOTE: On Android 4.1.x the function must be called before SurfaceTextre constructor!
    protected void AllocateCache() {
        mCacheBitmap = Bitmap.createBitmap(mFrameWidth, mFrameHeight, Bitmap.Config.ARGB_8888);
    }

    public interface ListItemAccessor {
        public int getWidth(Object obj);

        public int getHeight(Object obj);
    }

    ;


    protected Size calculateCameraFrameSize(List<?> supportedSizes, ListItemAccessor accessor, int surfaceWidth, int surfaceHeight) {
        int calcWidth = 0;
        int calcHeight = 0;

        int maxAllowedWidth = (mMaxWidth != MAX_UNSPECIFIED && mMaxWidth < surfaceWidth) ? mMaxWidth : surfaceWidth;
        int maxAllowedHeight = (mMaxHeight != MAX_UNSPECIFIED && mMaxHeight < surfaceHeight) ? mMaxHeight : surfaceHeight;

        for (Object size : supportedSizes) {
            int width = accessor.getWidth(size);
            int height = accessor.getHeight(size);

            if (width <= maxAllowedWidth && height <= maxAllowedHeight) {
                if (width >= calcWidth && height >= calcHeight) {
                    calcWidth = (int) width;
                    calcHeight = (int) height;
                }
            }
        }

        return new Size(calcWidth, calcHeight);
    }
}