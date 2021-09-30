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
import androidx.annotation.StringRes;
import androidx.navigation.Navigation;

import java.util.List;

import br.com.example.R;
import br.com.example.models.OCRData;
import br.com.example.models.OCRExtraction;
import br.com.example.models.ResponseBody;

public class DocumentDetectionResultFragment extends BaseFragment {

    private ResponseBody<List<OCRData>> dataResponseBody;

    public DocumentDetectionResultFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_document_detection_result, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.dataResponseBody = DocumentDetectionResultFragmentArgs.fromBundle(getArguments()).getResult();

        final TextView resultTitleTextView = view.findViewById(R.id.result_title_text_view);
        final TextView name = view.findViewById(R.id.name);
        final TextView birthdate = view.findViewById(R.id.birthdate);
        final TextView mothersName = view.findViewById(R.id.mothers_name);
        final TextView documentId = view.findViewById(R.id.document_id);
        final TextView federalRevenueNumber = view.findViewById(R.id.federal_revenue_number);
        final Button resultButton = view.findViewById(R.id.result_button);
        final ImageView resultImageView = view.findViewById(R.id.result_image_view);

        if (hasResult()) {
            final OCRData firstResult = dataResponseBody.getData().get(0);
            final OCRExtraction extraction = firstResult.getExtraction();
            resultImageView.setBackgroundResource(R.drawable.check_circle_outline);
            fillData(name, R.string.name, extraction.getName());
            fillData(birthdate, R.string.birthdate, extraction.getBirthdate());
            fillData(mothersName, R.string.mothers_name, extraction.getMothersName());
            fillData(documentId, R.string.document_id, extraction.getDocumentId());
            fillData(federalRevenueNumber, R.string.federal_revenue_number, extraction.getFederalRevenueNumber());
            resultButton.setText(getString(R.string.text_continue));
        } else {
            resultImageView.setBackgroundResource(R.drawable.error_outline);
            resultTitleTextView.setText(getString(R.string.unable_to_get_the_data));
            resultButton.setText(getString(R.string.try_again));
        }

        resultButton.setOnClickListener(v -> {
            Navigation.findNavController(requireView()).popBackStack();
        });
    }

    private void fillData(final TextView textView, @StringRes final int resId, final String value) {
        if (value == null || value.isEmpty()) return;
        textView.setText(getString(resId, value));
        textView.setVisibility(View.VISIBLE);
    }

    private boolean hasResult() {
        return dataResponseBody != null && (dataResponseBody.getData() != null && !dataResponseBody.getData().isEmpty());
    }
}
