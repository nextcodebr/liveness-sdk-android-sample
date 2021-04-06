package br.com.example.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import br.com.example.FaceDetectionResult;
import br.com.example.R;

public class FaceDetectionResultFragment extends BaseFragment {


    private FaceDetectionResult faceDetectionResult;

    public FaceDetectionResultFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_face_detection_result, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.faceDetectionResult = FaceDetectionResultFragmentArgs.fromBundle(getArguments()).getResult();

        TextView resultTitleTextView = view.findViewById(R.id.result_title_text_view);
        TextView resultDescriptionTextView = view.findViewById(R.id.result_description_text_view);
        Button resultButton = view.findViewById(R.id.result_button);
        ImageView resultImageView = view.findViewById(R.id.result_image_view);

        if (faceDetectionResult == FaceDetectionResult.SUCCESS) {
            resultImageView.setBackgroundResource(R.drawable.check_circle_outline);
            resultTitleTextView.setText(getResources().getString(R.string.verification_completed_successfully));
            resultDescriptionTextView.setText(getResources().getString(R.string.congratulations_your_facial_check_has_been_successfully_completed));
            resultButton.setText(getResources().getString(R.string.text_continue));
        } else {
            resultImageView.setBackgroundResource(R.drawable.error_outline);
            resultTitleTextView.setText(getResources().getString(R.string.verification_could_not_be_completed));
            resultDescriptionTextView.setText(getResources().getString(R.string.please_try_to_take_your_photo_again));
            resultButton.setText(getResources().getString(R.string.try_again));
        }

        resultButton.setOnClickListener(v -> {
            if (faceDetectionResult == FaceDetectionResult.FAILURE) {
                startFaceDetection();
            }
        });
    }
}
