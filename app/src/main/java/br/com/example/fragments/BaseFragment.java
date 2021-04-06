package br.com.example.fragments;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

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
                if (data != null && data.getExtras() != null) {
                    result = data.getExtras().getBoolean(NxcdFaceDetection.RESULT);
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
        NxcdFaceDetection nxcdFaceDetection = new NxcdFaceDetection(FACEDETECTION_REQUEST_CODE,"60522c5432a25800127b0e74:0QN0s2VrNSQigKPxu0uMNDiK");
        nxcdFaceDetection.startFaceDetection(this);
    }
}
