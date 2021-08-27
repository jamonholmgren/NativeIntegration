package com.nativeintegration;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.facebook.react.views.image.ReactImageView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import javax.annotation.Nullable;

public class ReactBowserManager extends SimpleViewManager<ReactImageView> {

    public static final String REACT_CLASS = "RCTBowserView";
    ReactApplicationContext mCallerContext;
    private ImgStartListener imgStartListener;

    public Map getExportedCustomBubblingEventTypeConstants() {
        return MapBuilder.builder()
                .put(
                        "pressed",
                        MapBuilder.of(
                                "phasedRegistrationNames",
                                MapBuilder.of("bubbled", "onPress")))
                .build();
    }

    private String logoURL = "https://pbs.twimg.com/profile_images/1303766581982437376/GZA5PwNg_400x400.jpg";

    /* Interface Listener to start loading the image if the source is set */
    private interface ImgStartListener {
        void startLoading();
    }

    public ReactBowserManager(ReactApplicationContext reactContext) {
        mCallerContext = reactContext;
    }

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    protected ReactImageView createViewInstance(ThemedReactContext reactContext) {

        final ReactImageView reactImageView = new ReactImageView(reactContext, Fresco.newDraweeControllerBuilder(), null, mCallerContext);

        final Handler handler = new Handler();
        startDownloading(handler, reactImageView);

        reactImageView.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              WritableMap event = Arguments.createMap();
              event.putString("message", "MyMessage");
              mCallerContext.getJSModule(RCTEventEmitter.class).receiveEvent(v.getId(), "pressed", event);
          }
        });

        return reactImageView;
    }

    private void startDownloading(final Handler handler, final ReactImageView reactImageView) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(logoURL);
                    final Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    setImage(bmp, handler, reactImageView);
                } catch (Exception e) {
                    Log.e("ReactImageManager", "Error : " + e.getMessage());
                }
            }
        }).start();
    }

    private void setImage(final Bitmap bmp, Handler handler, final ReactImageView reactImageView) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                reactImageView.setImageBitmap(bmp);
            }
        });
    }
}
