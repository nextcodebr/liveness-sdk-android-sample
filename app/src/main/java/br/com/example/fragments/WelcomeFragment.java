package br.com.example.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import br.com.example.R;

public class WelcomeFragment extends BaseFragment {

    public WelcomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_welcome, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Button faceDetectionFlowButton = view.findViewById(R.id.face_detection_flow_button);
        final Button documentDetectionFlowButton = view.findViewById(R.id.document_detection_flow_button);

        faceDetectionFlowButton.setOnClickListener(v -> {
            final NavDirections action = WelcomeFragmentDirections.actionWelcomeFragmentToInstructionsFragment();
            Navigation.findNavController(requireView()).navigate(action);
        });

        documentDetectionFlowButton.setOnClickListener(v -> {
            final NavDirections action = WelcomeFragmentDirections.actionWelcomeFragmentToDocumentInstructionsFragment();
            Navigation.findNavController(requireView()).navigate(action);
        });
    }
}
