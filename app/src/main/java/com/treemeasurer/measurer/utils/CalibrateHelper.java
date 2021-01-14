package com.treemeasurer.measurer.utils;

import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfPoint3f;
import org.opencv.core.Point3;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CalibrateHelper {
    private static final String TAG = "CameraCalibrator";

    public static enum CalibrateStatus{
        CORNEREXTRACTION_SUCCESS,
        CALIBRATION_SUCCESS,
        FAILD
    }


    private Size imgSize;
    private Size boardSize;
    private Mat coners;
    private MatOfPoint2f imgCorners;
    private Mat mCameraMatrix;
    private Mat mDistortionCoefficients;
    private static List<Mat> mCornersBuffer;
    private int mCornersSize;
    private Size square_size;
    private List<Mat> objectPoints;//标定板角点三维坐标
    private static boolean mIsCalibrated = false;
    private static double mRms;
    private List<Mat> rvecs = new ArrayList<Mat>();//旋转向量
    private List<Mat> tvecs = new ArrayList<Mat>();//平移向量
    private Mat reprojectionErrors;

    public CalibrateHelper (int boardSizeW, int boardSizeH, int squireSize) {
        imgSize = new Size();
        boardSize = new Size(boardSizeW - 1, boardSizeH - 1);
        coners = new Mat();
        imgCorners = new MatOfPoint2f();
        mCameraMatrix = new Mat();
        mDistortionCoefficients = new Mat();
        mCornersBuffer = new ArrayList<Mat>();
        mCornersSize = (int)(boardSize.width * boardSize.height);
        square_size = new Size(squireSize,squireSize);
        objectPoints = new ArrayList<Mat>();//标定板角点三维坐标
        rvecs = new ArrayList<Mat>();//旋转向量
        tvecs = new ArrayList<Mat>();//平移向量
        reprojectionErrors = new Mat();//重投影误差
    }

    private void cornerExtraction(File srcPath, File desPath) {
        Log.d(TAG, "cornerExtraction: " + desPath.getAbsolutePath());
        Mat src = Imgcodecs.imread(srcPath.getAbsolutePath());//直接用Opencv读取本地图片是三通道BGR
        Log.d(TAG, "cornerExtraction: " + src);
        Log.d(TAG, "cornerExtraction: " + srcPath);
        imgSize.width = src.width();
        imgSize.height = src.height();
        Mat.eye(3, 3, CvType.CV_64FC1).copyTo(mCameraMatrix);
        mCameraMatrix.put(0, 0, 1.0);
        Mat.zeros(5, 1, CvType.CV_64FC1).copyTo(mDistortionCoefficients);
        if(!Calib3d.findChessboardCorners(src,boardSize,imgCorners)){
            Log.e(TAG, "cornerExtraction: 角点提取失败");
            throw new RuntimeException("input error Cant find Corners");
        }
        Mat gray = new Mat();
        Imgproc.cvtColor(src,gray,Imgproc.COLOR_BGR2GRAY);
        Calib3d.find4QuadCornerSubpix(gray,imgCorners,new Size(11,11));
        mCornersBuffer.add(imgCorners.clone());//保存亚像素角点
        Calib3d.drawChessboardCorners(src,boardSize,imgCorners,true);

        Imgcodecs.imwrite(desPath.getAbsolutePath(),src);
        src.release();
    }

    public void calibrateCamera(int imgNum) {
        for (int t = 0; t < imgNum; t++){
            MatOfPoint3f tempPointSet = new MatOfPoint3f();
            //objectPoints.add(Mat.zeros(mCornersSize, 1, CvType.CV_32FC3));
            for (int i = 0; i < boardSize.height; i++) {
                for (int j = 0; j < boardSize.width ; j ++) {
                    Point3 real = new Point3();
                    real.x = (float)(i *square_size.width);
                    real.y= (float)(j *square_size.height);
                    real.z = 0;
                    tempPointSet.push_back(new MatOfPoint3f(real));
                }
            }
            objectPoints.add(tempPointSet);
        }
        Calib3d.calibrateCamera(objectPoints, mCornersBuffer, imgSize,
                mCameraMatrix, mDistortionCoefficients, rvecs, tvecs, 0);
        mIsCalibrated = Core.checkRange(mCameraMatrix)
                && Core.checkRange(mDistortionCoefficients);
        mRms = computeReprojectionErrors(objectPoints, rvecs, tvecs, reprojectionErrors);
    }

    private double computeReprojectionErrors(List<Mat> objectPoints, List<Mat> rvecs,
                                             List<Mat> tvecs, Mat perViewErrors) {
        MatOfPoint2f cornersProjected = new MatOfPoint2f();
        double totalError = 0;
        double error;
        float viewErrors[] = new float[objectPoints.size()];
        MatOfDouble distortionCoefficients = new MatOfDouble(mDistortionCoefficients);
        int totalPoints = 0;
        for (int i = 0; i < objectPoints.size(); i++) {
            MatOfPoint3f points = new MatOfPoint3f(objectPoints.get(i));
            Calib3d.projectPoints(points, rvecs.get(i), tvecs.get(i),
                    mCameraMatrix, distortionCoefficients, cornersProjected);
            error = Core.norm(mCornersBuffer.get(i), cornersProjected, Core.NORM_L2);

            int n = objectPoints.get(i).rows();
            viewErrors[i] = (float) Math.sqrt(error * error / n);
            totalError  += error * error;
            totalPoints += n;
        }
        for(float each : viewErrors){
            Log.d(TAG, "computeReprojectionErrors: "+each);
        }
        perViewErrors.create(objectPoints.size(), 1, CvType.CV_32FC1);
        perViewErrors.put(0, 0, viewErrors);
        return Math.sqrt(totalError / totalPoints);
    }


    ExecutorService calibrateExecutor = Executors.newSingleThreadExecutor();
    public void calibrate(int imgNum, List<File> srcPath, File dir, Handler handler) {
        calibrateExecutor.execute(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < imgNum; i++) {
//                    File destPth = new File(dir, "cornerExtract_"+ i + ".jpg");
                    try {
                        cornerExtraction(new File(srcPath.get(i).toString()), srcPath.get(i));
                    } catch (RuntimeException e) {
                        sendStatus(handler, CalibrateStatus.FAILD);
                        calibrateExecutor.shutdown();
                        return;
                    }
                    sendStatus(handler, CalibrateStatus.CORNEREXTRACTION_SUCCESS);
                }
                calibrateCamera(imgNum);
                sendStatus(handler, CalibrateStatus.CALIBRATION_SUCCESS);
                calibrateExecutor.shutdown();
            }
        });
    }

    private void sendStatus(Handler handler, CalibrateStatus status) {
        Message msg = new Message();
        msg.obj = status;
        handler.sendMessage(msg);
    }

    public Mat getmCameraMatrix() {
        return mCameraMatrix;
    }

    public Mat getmDistortionCoefficients() {
        return mDistortionCoefficients;
    }

    public static double getmRms() {
        return mRms;
    }
}
