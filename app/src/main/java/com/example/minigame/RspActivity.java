package com.example.minigame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class RspActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private String RSP = "RSP";
    private List<String> GameElement = new ArrayList<String>();


    private Camera mCamera;
    private CameraPreview mPreview;

    // opencv
    CameraBridgeViewBase cameraBridgeViewBase;
    BaseLoaderCallback baseLoaderCallback;




    Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            camera.setDisplayOrientation(180);

            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

            int w = bitmap.getWidth();
            int h = bitmap.getHeight();
            Matrix mtx = new Matrix();
            mtx.postRotate(0); // bitmap 이미지를 180도 돌려져있음 -> 돌린다
//
            Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
//            Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h);

            if (bitmap == null) {
                Toast.makeText(RspActivity.this, "Capture image is empty", Toast.LENGTH_LONG).show();
                return;
            }
//            captureedImageHolder.setImageBitmap(scaleDownBItmapImage(rotatedBitmap, 450, 300));

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rsp);

        GameInfo.setGameStage(3);

        // TODO: 2022/12/06 Camera 실습 때 코드
        Button btn = (Button) findViewById(R.id.rsp_test_btn);

        // Create an instance of Camera
        mCamera = getCameraInstance();
//        mCamera.setDisplayOrientation(180);

        // Create out Preview view and set is as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.rsp_camera_preview);
        preview.addView(mPreview);

        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mCamera.takePicture(null, null, pictureCallback);
            }
        });


        Log.d(RSP, "=== Game Start === GameInfo - GameStage: " + GameInfo.getGameStage() +
                "GameInfo - GameScore" + GameInfo.getTotalScore());

        // TODO: 2022/12/06 컴퓨터가 무작위로 제시 (Rock or Scissors or Paper)


        // TODO: 2022/12/06 사용자가 손으로 제시한 것을 mediapipe 을 이용하여 인식. hand detection


        // TODO: 2022/12/06 컴퓨터와 사용자의 승부를 판단 (비긴 경우 점수 +1, 이긴 경우 점수 +3, 진 경우 Game Over)

        Button testBtn = findViewById(R.id.rsp_test_btn);
        testBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 2022/12/06 게임 오버화면으로 넘어가기
                Intent intent = new Intent(getApplicationContext(), GameOverActivity.class);
                startActivity(intent);

            }
        });


    }



    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    private void releaseMediaRecorder() {
        mCamera.lock();
    }

    private static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {
            // Camera is not available (in use of does not exits)
        }
        return c;
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseMediaRecorder(); // if you are using MediaRecorder, release it
        releaseCamera();        // release the camera immediately on pause event
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        Mat frame = inputFrame.rgba();
        Imgproc.cvtColor(frame, frame, Imgproc.COLOR_RGBA2GRAY);
        Imgproc.Canny(frame, frame, 100, 80);

        return frame;
    }

    // opencv method



}