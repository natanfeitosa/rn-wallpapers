package com.natanapps.rnwallpapers;

import android.app.Activity;
import android.app.WallpaperManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.views.imagehelper.ResourceDrawableIdHelper;
import com.facebook.react.views.imagehelper.ImageSource;
import com.facebook.react.module.annotations.ReactModule;

@ReactModule(name = RNWallpapersModule.NAME)
public class RNWallpapersModule extends ReactContextBaseJavaModule {
    public static final String NAME = "RNWallpapers";

    private WallpaperManager wallpaperManager;
    private Promise rctPromise;
    private ResourceDrawableIdHelper mResourceDrawableIdHelper;
    private Uri mUri;
    private ReactApplicationContext mApplicationContext;
    private Activity mCurrentActivity;
    private DisplayMetrics displayMetrics = null;

    public RNWallpapersModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mApplicationContext = getReactApplicationContext();
        wallpaperManager = WallpaperManager.getInstance(mApplicationContext);
    }

    @Override
    @NonNull
    public String getName() {
        return NAME;
    }

    public void sendReject(String msg) {
        rctPromise.reject(RNWallpapersModule.NAME + "_JavaError", msg);
        rctPromise = null;
    }

    public void sendResolve(@Nullable Object value) {
        rctPromise.resolve(value);
        rctPromise = null;
    }

    private void setWallpaperBitmap(Bitmap bitmap, Integer typeScreen, String source) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                wallpaperManager.setBitmap(bitmap, null, true, typeScreen);
            } else {
                wallpaperManager.setBitmap(bitmap);
            }
            sendResolve(null);
        } catch (Exception e) {
            sendReject("Exception in SimpleTarget：" + e.getMessage());
        }
    }

    @ReactMethod
    public void setWallpaper(final ReadableMap params, final Integer type, final Promise promise) {
        final String source = params.hasKey("uri") ? params.getString("uri") : null;
        ReadableMap headers = params.hasKey("headers") ? params.getMap("headers") : null;
        final Integer typeScreen = type == null ? WallpaperManager.FLAG_SYSTEM : type;

        if (rctPromise != null) {
            promise.reject("You cannot use 'setWallpaper', already is in use");
            return;
        }

        rctPromise = promise;

        final CustomTarget<Bitmap> customTarget = this.getCustomTarget(source, typeScreen);
        mCurrentActivity = getCurrentActivity();

        if (mCurrentActivity == null) {
            sendReject("CurrentActivity is null");
        }

        //handle base64
        if ("data:image/png;base64,".startsWith(source)) {
            mCurrentActivity.runOnUiThread(new Runnable() {
                public void run() {
                    Util.assertMainThread();
                    try {
                        Glide
                            .with(mApplicationContext)
                            .asBitmap()
                            .apply(requestOptions())
                            .load(Base64.decode(source.replaceAll("data:image\\/.*;base64,", ""), Base64.DEFAULT))
                            .into(customTarget);
                    } catch (Exception e) {
                        sendReject("Exception in Glide：" + e.getMessage());
                    }
                }
            });

            return;
        }

        boolean useStorageFile = false ;

        // handle bundled app resources
        try {
            mUri = Uri.parse(source);
            // Verify scheme is set, so that relative uri (used by static resources) are not handled.
            if (mUri.getScheme() == null) {
                mUri = null;
            } else if (!mUri.getScheme().equals("http") && !mUri.getScheme().equals("https")) {
                useStorageFile = true;
            }
        } catch (Exception e) {
            // ignore malformed uri, then attempt to extract resource ID.
        }

        if (mUri == null) {
            ImageSource is = new ImageSource(this.getReactApplicationContext(), source);
            if (is.isResource()) {
                int resId = mResourceDrawableIdHelper.getInstance().getResourceDrawableId(this.getReactApplicationContext(), source);
                Bitmap mBitmap = BitmapFactory.decodeResource(this.getReactApplicationContext().getResources(), resId);
                setWallpaperBitmap(mBitmap, typeScreen, source);
                return;
            }

            mUri = is.getUri();
            mCurrentActivity.runOnUiThread(new Runnable() {
                public void run() {
                    Util.assertMainThread();
                    try{
                        Glide
                            .with(mApplicationContext)
                            .asBitmap()
                            .apply(requestOptions())
                            .load(mUri)
                            .into(customTarget);
                    } catch (Exception e) {
                        sendReject("Exception in Glide：" + e.getMessage());
                    }
                }
            });
        } else if (useStorageFile) {
            mCurrentActivity.runOnUiThread(new Runnable() {
                public void run() {
                    Util.assertMainThread();
                    try {
                        Glide
                            .with(mApplicationContext)
                            .asBitmap()
                            .apply(requestOptions())
                            .load(mUri)
                            .into(customTarget);
                    } catch (Exception e) {
                        sendReject("Exception in Glide：" + e.getMessage());
                    }
                }
            });
        } else {
            // Handle an http / https address
            final LazyHeaders.Builder lazyHeaders = new LazyHeaders.Builder();

            if (headers != null) {
                ReadableMapKeySetIterator it = headers.keySetIterator();
                Log.d("next headers", String.valueOf(it.hasNextKey()));
                while (it.hasNextKey()) {
                    String Key = it.nextKey();
                    lazyHeaders.addHeader(Key, headers.getString(Key));
                }
            }

            mCurrentActivity.runOnUiThread(new Runnable() {
                public void run() {
                    Util.assertMainThread();
                    try {
                        Glide
                            .with(mApplicationContext)
                            .asBitmap()
                            .apply(requestOptions())
                            .load(new GlideUrl(mUri.toString(), lazyHeaders.build()))
                            .into(customTarget);
                    } catch (Exception e) {
                        sendReject("Exception in Glide：" + e.getMessage());
                    }
                }
            });
        }
    }

    private RequestOptions requestOptions() {
        return new RequestOptions()
                .format(DecodeFormat.PREFER_ARGB_8888)
                .centerCrop();
    }

    private void initializeDisplayMetrics() {
        if (displayMetrics == null) {
            displayMetrics = new DisplayMetrics();
            WindowManager windowManager = (WindowManager) reactContext.getSystemService(reactContext.WINDOW_SERVICE);
            Display display = windowManager.getDefaultDisplay();
            display.getMetrics(displayMetrics);
        }
    }

    private int getDisplayHeight() {
        initializeDisplayMetrics();
        return displayMetrics.heightPixels;
    }

    private int getDisplayWidth() {
        initializeDisplayMetrics();
        return displayMetrics.widthPixels;
    }

    private CustomTarget<Bitmap> getCustomTarget(final String source, final Integer typeScreen) {
        int height = getDisplayHeight();
        int width = getDisplayWidth();
        // return new CustomTarget<Bitmap>(1080, 1920) {
        return new CustomTarget<Bitmap>(width, height) {
            @Override
            public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                setWallpaperBitmap(bitmap, typeScreen, source);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }

            @Override
            public void onLoadFailed(Drawable errorDrawable) {
                // Do nothing.
                String errorMessage = "Set Wallpaper Failed：Image not loaded";
                if(errorDrawable != null) {
                    errorMessage = "Set Wallpaper Failed：" + errorDrawable.toString();
                }
                sendReject(errorMessage);
            }
        };
    }
}
