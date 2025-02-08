package dev.jkcarino.extension.all.detection.signature.pms;

import android.app.Application;
import android.content.Context;

public final class SignatureHookApp extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        PmsHookApplication pmsHookApplication = new PmsHookApplication("<signature>");
        pmsHookApplication.hook(base);
        super.attachBaseContext(base);
    }
}
