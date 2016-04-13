package com.example.android.flashlight;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.Timer;

public class MainActivity extends AppCompatActivity {
    Button controlButton;
    Camera camera;
    Camera.Parameters params;
    boolean isFlashOn;
    int cameraId;
    Context context;
    Activity activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        controlButton=(Button)findViewById(R.id.controlId);
        // check weather camera has flash or not
        boolean hasFlash=getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        if (!hasFlash) {
            AlertDialog alert=new AlertDialog.Builder(this).create();
            alert.setTitle(getString(R.string.error));
            alert.setMessage(getString(R.string.error_message));
            alert.setButton("OK", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            alert.show();
        }
       context =this;
        activity=this;
        controlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isFlashOn) {
                    turnOffFlash();
                } else {
                    turnOnFlash();
                }
            }
        });
        getCamera();
        turnOnFlash();
    }
    private void getCamera() {
        if (camera == null) {
            try {
                int noOfCamera=Camera.getNumberOfCameras();
                Camera.CameraInfo cameraInfo=new Camera.CameraInfo();
                for(int i=0;i<noOfCamera;i++)
                {
                    Camera.getCameraInfo(i,cameraInfo);
                    if( cameraInfo.facing== Camera.CameraInfo.CAMERA_FACING_BACK) {
                        camera = Camera.open(i);
                        params = camera.getParameters();
                        cameraId=i;
                        break;
                    }
                }
            } catch (RuntimeException e) {
                Log.e("Camera Error ", e.getMessage());
            }
        }
    }
    private  void turnOnFlash() {
        if(!isFlashOn) {
            if (camera == null || params == null) {
                return;
            }
        }
            params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        camera.setParameters(params);
        camera.startPreview();
        isFlashOn=true;
        controlButton.setText(getString(R.string.off));
        new Timer().schedule(new java.util.TimerTask() {
            @Override
            public void run() {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                       final AlertDialog alert = new AlertDialog.Builder(context).create();
                        alert.setMessage(getString(R.string.flashContinue));
                        alert.setButton2("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                turnOffFlash();
                                cancel();
                            }
                        });
                        alert.setButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                alert.dismiss();
                            }
                        });
                        alert.show();
                    }
                });
            }
        }, 60000, 60000);
    }
    private void turnOffFlash() {
        if (isFlashOn) {
            if (camera == null || params == null) {
                return;
            }

            params = camera.getParameters();
            params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(params);
            camera.stopPreview();
            isFlashOn = false;
            controlButton.setText(getString(R.string.on));
        }
    }
    @Override
    protected void onStart() {
        super.onStart();

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        turnOffFlash();
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }
    @Override
    protected void onPause() {
        super.onPause();

    }
    @Override
    protected void onResume() {
        super.onResume();
    }

}
