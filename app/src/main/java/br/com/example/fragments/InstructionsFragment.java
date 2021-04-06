package br.com.example.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

import br.com.example.R;
import br.com.example.adapters.InstructionsAdapter;
import br.com.example.adapters.InstructionsVerticalSpaceDecoration;

public class InstructionsFragment extends BaseFragment {

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
                getResources().getString(R.string.keep_a_neutral_expression),
                getResources().getString(R.string.keep_your_eyes_open),
                getResources().getString(R.string.look_for_a_well_lit_place),
                getResources().getString(R.string.remove_any_type_of_accessory));

        InstructionsAdapter instructionsAdapter = new InstructionsAdapter(getContext(), instructions);
        instructionsRecyclerView.setAdapter(instructionsAdapter);
    }
}