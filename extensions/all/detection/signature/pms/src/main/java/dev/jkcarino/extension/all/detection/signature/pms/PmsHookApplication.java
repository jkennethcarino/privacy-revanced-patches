package dev.jkcarino.extension.all.detection.signature.pms;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * A custom {@link Application} class that hooks into the Android's {@link PackageManager} to modify the
 * package information returned for the app. This is used to override the package signatures,
 * allowing us to bypass signature verification checks.
 * <p>
 * Source: https://github.com/L-JINBIN/ApkSignatureKiller/blob/master/hook/cc/binmt/signature/PmsHookApplication.java
 */
public final class PmsHookApplication implements InvocationHandler {

    private static final int GET_SIGNATURES = 0x00000040;

    private final String signature;
    private byte[][] signatures;
    private Object packageManager;
    private String appPackageName = "";

    /**
     * Constructs a new {@code PmsHookApplication} with the specified signature.
     *
     * @param signature the base64-encoded signature string
     */
    public PmsHookApplication(String signature) {
        this.signature = signature;
    }

    /**
     * This intercepts calls to the {@code getPackageInfo} method of the package manager and
     * modifies the returned {@link PackageInfo} object to use the new package signatures.
     *
     * @param proxy  the proxy instance that the method was invoked on
     * @param method the {@link Method} instance corresponding to the interface method invoked on the proxy instance
     * @param args   an array of objects containing the values of the arguments passed in the method invocation on the
     *               proxy instance
     * @return the value to return from the method invocation on the proxy instance.
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if ("getPackageInfo".equals(method.getName())) {
            String packageName = (String) args[0];
            int flag = Integer.parseInt(args[1].toString());

            if ((flag & GET_SIGNATURES) != 0 && appPackageName.equals(packageName)) {
                PackageInfo packageInfo = (PackageInfo) method.invoke(packageManager, args);
                Signature[] newSignatures = new Signature[signatures.length];
                for (int i = 0; i < signatures.length; i++) {
                    newSignatures[i] = new Signature(signatures[i]);
                }
                packageInfo.signatures = newSignatures;
                return packageInfo;
            }
        }
        return method.invoke(packageManager, args);
    }

    /**
     * This hooks into the {@link PackageManager} and replaces the original package manager with a
     * proxy object that modifies the package information returned for the app.
     *
     * @param context the Application context
     */
    @SuppressLint({"PrivateApi", "DiscouragedPrivateApi"})
    public void hook(Context context) {
        try {
            byte[] byteArray = Base64.decode(signature, Base64.DEFAULT);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
            DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);

            int signatureCount = dataInputStream.read() & 0xFF;
            byte[][] signatures = new byte[signatureCount][];

            for (int i = 0; i < signatureCount; i++) {
                signatures[i] = new byte[dataInputStream.readInt()];
                dataInputStream.readFully(signatures[i]);
            }

            Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
            Method currentActivityThreadMethod = activityThreadClass.getDeclaredMethod("currentActivityThread");
            Object currentActivityThread = currentActivityThreadMethod.invoke(null);

            Field sPackageManagerField = activityThreadClass.getDeclaredField("sPackageManager");
            sPackageManagerField.setAccessible(true);
            Object sPackageManager = sPackageManagerField.get(currentActivityThread);

            this.signatures = signatures;
            this.packageManager = sPackageManager;
            this.appPackageName = context.getPackageName();

            Class<?> iPackageManagerInterface = Class.forName("android.content.pm.IPackageManager");
            Object proxy = Proxy.newProxyInstance(
                    iPackageManagerInterface.getClassLoader(),
                    new Class<?>[]{iPackageManagerInterface},
                    this
            );

            sPackageManagerField.set(currentActivityThread, proxy);

            PackageManager pm = context.getPackageManager();
            Field mPmField = pm.getClass().getDeclaredField("mPM");
            mPmField.setAccessible(true);
            mPmField.set(pm, proxy);

            System.out.println("PmsHook success.");
        } catch (Exception error) {
            System.err.println("PmsHook failed.");
            error.printStackTrace();
        }
    }
}
