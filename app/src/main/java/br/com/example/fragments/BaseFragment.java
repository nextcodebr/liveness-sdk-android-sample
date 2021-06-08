package br.com.example.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;


import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import br.com.example.FaceDetectionResult;
import br.com.nxcd.facedetection.NxcdFaceDetection;

public class BaseFragment extends Fragment {

    public int FACEDETECTION_REQUEST_CODE = 1;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FACEDETECTION_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {

                boolean result = false;
                Bitmap bitmap = null;
                if (data != null && data.getExtras() != null) {
                    result = data.getExtras().getBoolean(NxcdFaceDetection.RESULT);
                    try {
                        bitmap = BitmapFactory.decodeStream(getContext().openFileInput(NxcdFaceDetection.IMAGE_RESULT));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                NavDirections action = InstructionsFragmentDirections.actionToFaceDetectionResultFragment(result ? FaceDetectionResult.SUCCESS : FaceDetectionResult.FAILURE);
                if (getView() != null) {
                    Navigation.findNavController(getView()).navigate(action);
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.d("Activity result", "Face detection canceled");
            }
        }
    }

    void startFaceDetection() {
        NxcdFaceDetection nxcdFaceDetection = new NxcdFaceDetection(FACEDETECTION_REQUEST_CODE, "<TOKEN>");
        nxcdFaceDetection.setHomologation();
        nxcdFaceDetection.setTimeout(90000);
        nxcdFaceDetection.startFaceDetection(this);
    }
}
