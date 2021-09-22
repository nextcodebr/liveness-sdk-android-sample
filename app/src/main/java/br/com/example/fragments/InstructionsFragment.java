package br.com.example.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import br.com.example.BuildConfig;
import br.com.example.FaceDetectionResult;
import br.com.example.R;
import br.com.example.adapters.InstructionsAdapter;
import br.com.example.adapters.InstructionsVerticalSpaceDecoration;
import br.com.nxcd.facedetection.NxcdFaceDetection;

public class InstructionsFragment extends BaseFragment {

    private static final String TAG = "FaceDetection";
    private int DETECTION_REQUEST_CODE = 1;

    public InstructionsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_instructions, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.ok_lets_go_button).setOnClickListener(button -> {
            startFaceDetection();
        });

        RecyclerView instructionsRecyclerView = view.findViewById(R.id.instructions_recycler_view);
        instructionsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        InstructionsVerticalSpaceDecoration instructionsVerticalSpaceDecoration = new InstructionsVerticalSpaceDecoration((int) getResources().getDimension(R.dimen.margin_medium));
        instructionsRecyclerView.addItemDecoration(instructionsVerticalSpaceDecoration);

        List<String> instructions = Arrays.asList(getResources().getString(R.string.position_your_face_centered_in_the_enclosed_area),
                getResources().getString(R.string.keep_a_neutral_expression_app_instruction),
                getResources().getString(R.string.keep_your_eyes_open_app_instruction),
                getResources().getString(R.string.look_for_a_well_lit_place),
                getResources().getString(R.string.remove_any_type_of_accessory));

        InstructionsAdapter instructionsAdapter = new InstructionsAdapter(getContext(), instructions);
        instructionsRecyclerView.setAdapter(instructionsAdapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DETECTION_REQUEST_CODE) {
            treatResultSDK(resultCode, data);
        }
    }

    private void treatResultSDK(final int resultCode, final Intent resultIntent) {
        if (Activity.RESULT_CANCELED == resultCode) {
            Log.d(TAG, "Face detection canceled");

            if (resultIntent != null) {
                if (resultIntent.hasExtra(NxcdFaceDetection.RESULT)) {
                    final HashMap<String, Object> result = (HashMap<String, Object>) resultIntent.getSerializableExtra(NxcdFaceDetection.RESULT);
                    Log.d(TAG, "Analyze image has failed: " + result.toString());
                    Toast.makeText(requireContext(), result.toString(), Toast.LENGTH_LONG).show();
                }

                if (resultIntent.hasExtra(NxcdFaceDetection.THROWABLE)) {
                    final Throwable throwable = (Throwable) resultIntent.getSerializableExtra(NxcdFaceDetection.THROWABLE);
                    Log.e(TAG, "Analyze image has failed.", throwable);
                    Toast.makeText(requireContext(), throwable.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
            return;
        }

        if (resultIntent != null && resultIntent.hasExtra(NxcdFaceDetection.RESULT)) {
            final HashMap<String, Object> resultDataFromAPI = (HashMap<String, Object>) resultIntent.getExtras().getSerializable(NxcdFaceDetection.RESULT);
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Result from API: " + resultDataFromAPI.toString());
            }

            boolean result = (boolean) ((HashMap<String, Object>) resultDataFromAPI.get("data")).get("isAlive");
            final NavDirections action = InstructionsFragmentDirections.actionToFaceDetectionResultFragment(result ? FaceDetectionResult.SUCCESS : FaceDetectionResult.FAILURE);
            Navigation.findNavController(requireView()).navigate(action);
        }
    }

    protected void startFaceDetection() {
        NxcdFaceDetection nxcdFaceDetection = new NxcdFaceDetection(DETECTION_REQUEST_CODE, getString(R.string.homolog_token));
        nxcdFaceDetection.setHomologation();
        //nxcdFaceDetection.setDevelopment();
        nxcdFaceDetection.startFaceDetection(this);
    }
}