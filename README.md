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

```implementation 'br.com.nxcd:facedetection:1.0.0```

### Utilização

Primeiramente é necessário criar uma instância de NxcdFaceDetection passando o request code, que será utilizado no onActivityResult para obter o resultado
da análise, e o token que será utilizado nas chamadas de API.

```NxcdFaceDetection nxcdFaceDetection = new NxcdFaceDetection(REQUEST_CODE, TOKEN);```

Após isso é só chamar o método startFaceDetection que iniciará o processo de validação.

```nxcdFaceDetection.startFaceDetection(this);```

### Retorno

Após concluído a detecção e análise, é retornado uma variável booleana que indica o sucesso da operação (true caso sucesso e false o contrário). Também é retornada a imagem utilizada na análise.

```
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

                /// seu código
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.d("Activity result", "Face detection canceled");
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