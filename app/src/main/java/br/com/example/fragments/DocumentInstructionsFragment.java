package br.com.example.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kaopiz.kprogresshud.KProgressHUD;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import br.com.example.BuildConfig;
import br.com.example.R;
import br.com.example.adapters.InstructionsAdapter;
import br.com.example.adapters.InstructionsVerticalSpaceDecoration;
import br.com.example.models.OCRData;
import br.com.example.models.ResponseBody;
import br.com.nxcd.facedetection.NxcdFaceDetection;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public class DocumentInstructionsFragment extends BaseFragment {

    private static final String TAG = "DocumentInstructions";
    private static final int REQUEST_CODE = 1000;
    private final List<Bitmap> images = new ArrayList<>();

    public DocumentInstructionsFragment() {
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
            startDocumentDetection();
        });

        final RecyclerView instructionsRecyclerView = view.findViewById(R.id.instructions_recycler_view);
        instructionsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        final InstructionsVerticalSpaceDecoration instructionsVerticalSpaceDecoration = new InstructionsVerticalSpaceDecoration((int) getResources().getDimension(R.dimen.margin_medium));
        instructionsRecyclerView.addItemDecoration(instructionsVerticalSpaceDecoration);

        final List<String> instructions = Arrays.asList(getResources().getString(R.string.place_the_document_without_a_cover_on_a_table),
                getResources().getString(R.string.look_for_a_well_lit_place),
                getResources().getString(R.string.position_your_document_centered_in_the_enclosed_area),
                getResources().getString(R.string.keep_your_device_steady_and_avoid_tilting));

        final InstructionsAdapter instructionsAdapter = new InstructionsAdapter(requireContext(), instructions);
        instructionsRecyclerView.setAdapter(instructionsAdapter);
    }

    private void startDocumentDetection() {
        final NxcdFaceDetection nxcdFaceDetection = new NxcdFaceDetection(REQUEST_CODE, getResources().getString(R.string.homolog_token), R.style.SDKTheme_Ex);
        nxcdFaceDetection.setHomologation();
        //nxcdFaceDetection.setDevelopment();
        // TODO Outra forma de usar: nxcdFaceDetection.startDocumentDetection(this);
        nxcdFaceDetection.startDocumentDetection(requireContext(), launcher);
    }

    private final ActivityResultLauncher<Intent> launcher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> treatResultSDK(result.getResultCode(), result.getData()));

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_CODE == requestCode) {
            treatResultSDK(resultCode, data);
        }
    }

    private void treatResultSDK(final int resultCode, final Intent resultIntent) {
        if (Activity.RESULT_CANCELED == resultCode) {
            Log.d(TAG, "Document detection canceled");

            if (resultIntent != null) {
                if (resultIntent.hasExtra(NxcdFaceDetection.RESULT)) {
                    final HashMap<String, Object> result = (HashMap<String, Object>) resultIntent.getSerializableExtra(NxcdFaceDetection.RESULT);
                    Log.d(TAG, "Classify image has failed: " + result.toString());
                    Toast.makeText(requireContext(), result.toString(), Toast.LENGTH_LONG).show();
                }

                if (resultIntent.hasExtra(NxcdFaceDetection.THROWABLE)) {
                    final Throwable throwable = (Throwable) resultIntent.getSerializableExtra(NxcdFaceDetection.THROWABLE);
                    Log.e(TAG, "Classify image has failed.", throwable);
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

            try {
                final Bitmap imageResult = BitmapFactory.decodeStream(requireContext().openFileInput(NxcdFaceDetection.IMAGE_RESULT));
                images.add(imageResult);
                askUserWantCaptureAnotherImage();
            } catch (FileNotFoundException e) {
                Log.e(TAG, "Error when trying open result image.", e);
            }
        }
    }

    private void askUserWantCaptureAnotherImage() {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.instructions)
                .setMessage(R.string.do_you_want_capture_another_document_image)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, (dialog, which) -> startDocumentDetection())
                .setNegativeButton(R.string.no, (dialog, which) -> captureDone())
                .show();
    }

    private void captureDone() {
        doRequest();
    }

    private void doRequest() {
        final KProgressHUD progress = KProgressHUD.create(requireActivity())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(getString(R.string.requesting_ocr))
                .setCancellable(true)
                .show();

        final Retrofit retro = provideRetrofit();
        final Service service = retro.create(Service.class);
        service.ocr(convertImageToRequest(images)).enqueue(new Callback<ResponseBody<List<OCRData>>>() {
            @Override
            public void onResponse(Call<ResponseBody<List<OCRData>>> call, Response<ResponseBody<List<OCRData>>> response) {
                progress.dismiss();

                if (!response.isSuccessful()) {
                    new AlertDialog.Builder(requireContext())
                            .setTitle(R.string.instructions)
                            .setMessage(R.string.ocr_request_failed)
                            .setPositiveButton(R.string.sdk_label_yes, (dialog, which) -> doRequest())
                            .setNegativeButton(R.string.sdk_label_no, (dialog, which) -> images.clear())
                            .setCancelable(false)
                            .show();
                    Log.e(TAG, "Response from API: " + response.toString());
                    return;
                }

                final ResponseBody<List<OCRData>> responseBody = response.body();
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "Response body from API: " + response.toString());
                }

                navigateToResult(responseBody);
                images.clear();
            }

            @Override
            public void onFailure(Call<ResponseBody<List<OCRData>>> call, Throwable t) {
                progress.dismiss();
                Log.e(TAG, "Response error from API", t);
            }
        });
    }

    private void navigateToResult(final ResponseBody<List<OCRData>> responseBody) {
        final NavDirections action = DocumentInstructionsFragmentDirections
                .actionDocumentInstructionsFragmentToDocumentDetectionResultFragment(responseBody);
        Navigation.findNavController(requireView()).navigate(action);
    }

    private List<MultipartBody.Part> convertImageToRequest(@NonNull final List<Bitmap> images) {
        if (images.isEmpty()) return Collections.emptyList();
        final List<MultipartBody.Part> files = new ArrayList<>();
        final MediaType mediaType = MediaType.parse("image/jpeg");
        final String fileNameTemplate = "file_{count}";
        RequestBody body;
        int count = 1;
        String name;
        for (Bitmap image : images) {
            name = fileNameTemplate.replace("{count}", String.valueOf(count));
            body = RequestBody.create(mediaType, toJpeg(image));
            files.add(MultipartBody.Part.createFormData(name, name + ".jpeg", body));
            count++;
        }

        return files;
    }

    private byte[] toJpeg(final Bitmap bitmap) {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        return out.toByteArray();
    }

    private Retrofit provideRetrofit() {
        final OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(90, TimeUnit.SECONDS)
                .callTimeout(120, TimeUnit.SECONDS);

        httpClient.addInterceptor(chain -> {
            final Request request = chain.request()
                    .newBuilder()
                    .addHeader("Authorization", "ApiKey " + getResources().getString(R.string.homolog_token)).build();
            return chain.proceed(request);
        });

        return new Retrofit.Builder().baseUrl("https://api-homolog.nxcd.app")
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
    }

    private interface Service {
        @Multipart
        @POST("full-ocr/v3")
        Call<ResponseBody<List<OCRData>>> ocr(@Part List<MultipartBody.Part> files/*, @Query("federalRevenueNumber") String cpf*/);
    }
}
