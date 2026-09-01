package br.com.example;

import android.content.Context;

import br.com.nxcd.facedetection.core.app.AppState;

/**
 * Token and base URL for the sample, injected at build time so no credential has to be committed.
 * Put them in local.properties, which is gitignored and -- unlike a -P flag -- also applies when
 * running from the IDE:
 *
 * <pre>
 * livenessToken=&lt;serviceAccount:secret&gt;
 * livenessBaseUrl=https://api-homolog.nxcd.app/
 * </pre>
 *
 * livenessBaseUrl is optional but useful: {@code setApiBaseURL} takes precedence over
 * setProduction/setHomologation/setDevelopment, so it overrides whatever the fragments ask for
 * without editing code.
 */
public final class DemoConfig {

    private DemoConfig() {
    }

    /** Must run before any SDK call: the Retrofit base URL is resolved once per process. */
    public static void apply() {
        if (!BuildConfig.LIVENESS_BASE_URL.isEmpty()) {
            AppState.get().setApiBaseURL(BuildConfig.LIVENESS_BASE_URL);
        }
    }

    public static String token(Context context) {
        if (!BuildConfig.LIVENESS_TOKEN.isEmpty()) {
            return BuildConfig.LIVENESS_TOKEN;
        }

        final String fallback = context.getString(R.string.homolog_token);
        if (fallback.isEmpty() || fallback.startsWith("COLE")) {
            throw new IllegalStateException("No liveness token configured. Add "
                    + "livenessToken=<serviceAccount:secret> to local.properties (works from the "
                    + "IDE too), or pass -PlivenessToken=<serviceAccount:secret>.");
        }
        return fallback;
    }
}
