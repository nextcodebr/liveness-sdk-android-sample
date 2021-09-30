# NXCD Face Detection

SDK para detecção e padronização de fotos da face.

### Android SDK Mínimo

A versão mínima do SDK do Android para a utilização do SDK é a 21.

### Gradle

```
allprojects {
    repositories {
        maven {
            url 'https://pkgs.dev.azure.com/nxcd/ef9b4772-b664-4276-aeca-9b430422da12/_packaging/nxcd_SDK/maven/v1'
        }
    }
}
```

```implementation 'br.com.nxcd:facedetection:1.0.14```

### Utilização

Primeiramente é necessário criar uma instância de NxcdFaceDetection passando o request code, que será utilizado no onActivityResult para obter o resultado
da análise, e o token que será utilizado nas chamadas de API.

```NxcdFaceDetection nxcdFaceDetection = new NxcdFaceDetection(REQUEST_CODE, TOKEN/*,THEME OPCIONAL */);```

Após isso é só chamar o método startFaceDetection ou startDocumentDetection que iniciará o processo de validação.

### Retorno

Após concluído a detecção e análise, é retornado o JSON integralmente, também é retornada a imagem utilizada na análise.

```
public int DETECTION_REQUEST_CODE = 1;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DETECTION_REQUEST_CODE) {
            if (Activity.RESULT_CANCELED == resultCode) {
                Log.d(TAG, "Detection canceled");

                if (resultIntent != null) {
                    if (resultIntent.hasExtra(NxcdFaceDetection.RESULT)) {
                        final HashMap<String, Object> result = (HashMap<String, Object>) resultIntent.getSerializableExtra(NxcdFaceDetection.RESULT);
                        Log.d(TAG, "Analyze/Classify image has failed: " + result.toString());
                        Toast.makeText(requireContext(), result.toString(), Toast.LENGTH_LONG).show();
                    }

                    if (resultIntent.hasExtra(NxcdFaceDetection.THROWABLE)) {
                        final Throwable throwable = (Throwable) resultIntent.getSerializableExtra(NxcdFaceDetection.THROWABLE);
                        Log.e(TAG, "Analyze/Classify image has failed.", throwable);
                        Toast.makeText(requireContext(), throwable.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                return;
            }

            if (resultIntent != null && resultIntent.hasExtra(NxcdFaceDetection.RESULT)) {
                final HashMap<String, Object> resultDataFromAPI = (HashMap<String, Object>) resultIntent.getExtras().getSerializable(NxcdFaceDetection.RESULT);
                Log.d(TAG, "Result from API: " + resultDataFromAPI.toString());           
                try {
                    final Bitmap imageResult = BitmapFactory.decodeStream(requireContext().openFileInput(NxcdFaceDetection.IMAGE_RESULT));                    
                } catch (FileNotFoundException e) {
                    Log.e(TAG, "Error when trying open result image.", e);
                }
            }
        }
    }
```

### Homologação

Por padrão o utilizado é o ambiente de produção, caso você queira utilizar o ambiente de homologação, você precisa utilizar o método setHomologation.

```nxcdFaceDetection.setHomologation();```

### Timeout

Por padrão, o timeout utilizado caso o usuário não consiga seguir todas as dicas é de 30 segundos. É possível alterar esse tempo
chamando o método setTimeout passando o tempo desejado em milissegundos.

```nxcdFaceDetection.setTimeout(5000);```

### Customização do Tema
```
<style name="SDKTheme.Ex" parent="@style/SDKTheme">
    <item name="tipsTextStyle">@style/TipsText.Ex</item>
    <item name="buttonStyle">@style/Button.Ex</item>
    <item name="tipsLayoutStyle">@style/TipsLayout.Ex</item>
    <item name="closeButtonStyle">@style/CloseButton.Ex</item>
    <item name="progressBarStyle">@style/ProgressBar.Ex</item>
    <item name="overlayPositiveColorStyle">@android:color/holo_blue_dark</item>
    <item name="overlayNegativeColorStyle">@android:color/holo_purple</item>
    <item name="showDetectedOverlay">true</item>
</style>

<style name="ProgressBar.Ex" parent="@style/ProgressBar">
    <item name="android:indeterminateTint">@android:color/holo_blue_bright</item>
</style>

<style name="CloseButton.Ex" parent="@style/CloseButton">
    <item name="android:src">@mipmap/close</item>
    <item name="android:visibility">gone</item>
</style>

<style name="TipsLayout.Ex" parent="@style/TipsLayout">
    <item name="android:background">@drawable/tips_layout_ex</item>
</style>

<style name="TipsText.Ex" parent="@style/TipsText">
    <item name="android:fontFamily">@font/roboto_bold</item>
    <item name="android:textSize">@dimen/margin_medium</item>
    <item name="android:textStyle">bold</item>
    <item name="android:textColor">@color/black</item>
</style>

<style name="Button.Ex" parent="@style/Button">
    <item name="android:fontFamily">@font/roboto_bold</item>
    <item name="android:textSize">@dimen/margin_medium</item>
    <item name="android:textStyle">normal</item>
    <item name="android:textColor">@android:color/white</item>
    <item name="backgroundTint">@android:color/holo_green_light</item>
</style>
```

### Customização do Textos/Resources
```
<string name="sdk_message_camera_permission_denied">Você não pode usar este recurso sem conceder a permissão de Camera</string>

<string name="sdk_label_processing">Processando</string>

<string name="sdk_tips_keep_a_neutral_expression">Mantenha uma expressão neutra</string>
<string name="sdk_tips_keep_your_eyes_open">Mantenha os olhos abertos</string>
<string name="sdk_tips_center_your_face">Centralize seu rosto</string>
<string name="sdk_tips_move_your_face_closer_to_the_device">Aproxime o seu rosto do dispositivo</string>
<string name="sdk_tips_move_your_face_far_to_the_device">Afaste o seu rosto do dispositivo</string>
<string name="sdk_tips_more_than_one_face_was_found_in_the_frame_of_camera">Foram encontrados mais de um rosto no enquadro da câmera</string>
<string name="sdk_tips_move_face_left">Vire para esquerda</string>
<string name="sdk_tips_move_face_right">Vire para direita</string>
<string name="sdk_tips_keep_head_aligned">Mantenha o rosto alinhado</string>
<string name="sdk_tips_error_taking_snaphot">Erro ao capturar Selfie</string>
<string name="sdk_tips_keep_center">Centralize o documento</string>
<string name="sdk_tips_too_far">Aproxime a câmera</string>
<string name="sdk_tips_too_near">Afaste a câmera</string>
<string name="sdk_tips_taking_photo">Capturando foto</string>
<string name="sdk_tips_taking_document">Capturando o documento</string>
<string name="sdk_tips_photo_has_good_quality">A foto ficou nítida?</string>
<string name="sdk_tips_error_taking_document_snapshot">Erro ao capturar documento</string>
<string name="sdk_tips_finding_document">Procurando documento</string>
<string name="sdk_label_no">Não</string>
<string name="sdk_label_yes">Sim</string>

<string name="sdk_content_description_preview">Pré-visualização</string>
```
