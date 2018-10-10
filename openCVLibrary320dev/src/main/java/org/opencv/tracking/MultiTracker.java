
//
// This file is auto-generated. Please don't modify it!
//
package org.opencv.tracking;

import java.lang.String;
import java.util.ArrayList;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect2d;
import org.opencv.core.Rect2d;

// C++: class MultiTracker
//javadoc: MultiTracker
public class MultiTracker {

    protected final long nativeObj;
    protected MultiTracker(long addr) { nativeObj = addr; }

    public long getNativeObjAddr() { return nativeObj; }

    //
    // C++:   MultiTracker(String trackerType = "")
    //

    //javadoc: MultiTracker::MultiTracker(trackerType)
    public   MultiTracker(String trackerType)
    {
        
        nativeObj = MultiTracker_0(trackerType);
        
        return;
    }

    //javadoc: MultiTracker::MultiTracker()
    public   MultiTracker()
    {
        
        nativeObj = MultiTracker_1();
        
        return;
    }


    //
    // C++:  bool add(Mat image, Rect2d boundingBox)
    //

    //javadoc: MultiTracker::add(image, boundingBox)
    public  boolean add(Mat image, Rect2d boundingBox)
    {
        
        boolean retVal = add_0(nativeObj, image.nativeObj, boundingBox.x, boundingBox.y, boundingBox.width, boundingBox.height);
        
        return retVal;
    }


    //
    // C++:  bool add(Mat image, vector_Rect2d boundingBox)
    //

    //javadoc: MultiTracker::add(image, boundingBox)
    public  boolean add(Mat image, MatOfRect2d boundingBox)
    {
        Mat boundingBox_mat = boundingBox;
        boolean retVal = add_1(nativeObj, image.nativeObj, boundingBox_mat.nativeObj);
        
        return retVal;
    }


    //
    // C++:  bool add(String trackerType, Mat image, Rect2d boundingBox)
    //

    //javadoc: MultiTracker::add(trackerType, image, boundingBox)
    public  boolean add(String trackerType, Mat image, Rect2d boundingBox)
    {
        
        boolean retVal = add_2(nativeObj, trackerType, image.nativeObj, boundingBox.x, boundingBox.y, boundingBox.width, boundingBox.height);
        
        return retVal;
    }


    //
    // C++:  bool add(String trackerType, Mat image, vector_Rect2d boundingBox)
    //

    //javadoc: MultiTracker::add(trackerType, image, boundingBox)
    public  boolean add(String trackerType, Mat image, MatOfRect2d boundingBox)
    {
        Mat boundingBox_mat = boundingBox;
        boolean retVal = add_3(nativeObj, trackerType, image.nativeObj, boundingBox_mat.nativeObj);
        
        return retVal;
    }


    //
    // C++:  bool update(Mat image, vector_Rect2d& boundingBox)
    //

    //javadoc: MultiTracker::update(image, boundingBox)
    public  boolean update(Mat image, MatOfRect2d boundingBox)
    {
        Mat boundingBox_mat = boundingBox;
        boolean retVal = update_0(nativeObj, image.nativeObj, boundingBox_mat.nativeObj);
        
        return retVal;
    }


    @Override
    protected void finalize() throws Throwable {
        delete(nativeObj);
    }



    // C++:   MultiTracker(String trackerType = "")
    private static native long MultiTracker_0(String trackerType);
    private static native long MultiTracker_1();

    // C++:  bool add(Mat image, Rect2d boundingBox)
    private static native boolean add_0(long nativeObj, long image_nativeObj, double boundingBox_x, double boundingBox_y, double boundingBox_width, double boundingBox_height);

    // C++:  bool add(Mat image, vector_Rect2d boundingBox)
    private static native boolean add_1(long nativeObj, long image_nativeObj, long boundingBox_mat_nativeObj);

    // C++:  bool add(String trackerType, Mat image, Rect2d boundingBox)
    private static native boolean add_2(long nativeObj, String trackerType, long image_nativeObj, double boundingBox_x, double boundingBox_y, double boundingBox_width, double boundingBox_height);

    // C++:  bool add(String trackerType, Mat image, vector_Rect2d boundingBox)
    private static native boolean add_3(long nativeObj, String trackerType, long image_nativeObj, long boundingBox_mat_nativeObj);

    // C++:  bool update(Mat image, vector_Rect2d& boundingBox)
    private static native boolean update_0(long nativeObj, long image_nativeObj, long boundingBox_mat_nativeObj);

    // native support for java finalize()
    private static native void delete(long nativeObj);

}
