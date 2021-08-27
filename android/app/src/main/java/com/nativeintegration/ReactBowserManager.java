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

import android.annotation.SuppressLint;
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
    // This is the string we use to identify this view when we call
    // requireNativeComponent("RCTBowserView") in JS.
    public static final String REACT_CLASS = "RCTBowserView";

    // We hang onto a reference of our React app context for later use.
    ReactApplicationContext mCallerContext;
    ReactImageView mView;

    // This allows us to map JS events (like onPress) to our native events
    @Nullable @Override
    public Map<String, Object> getExportedCustomDirectEventTypeConstants() {
        return MapBuilder.<String, Object>builder()
                .put("onPress",
                        MapBuilder.of("registrationName", "onPress"))
                .build();
    }

    // This is the URL of the image we'll show
    private final String logoURL = "https://www.google.com/logos/doodles/2021/doodle-champion-island-games-august-27-6753651837109005-s.png";

    // Constructor -- saves a reference to the React context
    public ReactBowserManager(ReactApplicationContext reactContext) {
        mCallerContext = reactContext;
    }

    // Required method to allow React Native to know what the name of this class is.
    @Override
    public String getName() {
        return REACT_CLASS;
    }

    // This method is where we create our native view.
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected ReactImageView createViewInstance(ThemedReactContext reactContext) {
        // Instantiate a new ReactImageView
        // Fresco is a Facebook library for managing Android images and the memory they use.
        // https://github.com/facebook/fresco
        mView = new ReactImageView(reactContext, Fresco.newDraweeControllerBuilder(), null, mCallerContext);

        // This "handler" allows the `startDownloading` thread to call back to *this* thread.
        // Otherwise crashy crashy!
        final Handler mainThread = new Handler();

        // We'll download the image now and apply it back to this view
        startDownloading(mainThread);

        // Now we handle any touches!
        mView.setOnTouchListener((View v, MotionEvent ev) -> {
            // We get two events onTouch -- ACTION_DOWN and ACTION_UP
            if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                // Create our event, which is just a map/object with whatever we want in it.
                WritableMap event = Arguments.createMap();
                event.putString("message", "MyMessage");

                // Send off the event.
                sendEvent("onPress", event);

                // We handled this event, so return true
                return true;
            } else {
                // ACTION_UP
                // We did not handle this event, so return false
                return false;
            }
        });

        // Return our view back to React Native.
        return mView;
    }

    // This sends an event to the JavaScript version of this component
    private void sendEvent(String eventName, WritableMap event) {
        // We get the JS module and tell it we've received an event, what its name is, and more info.
        mCallerContext.getJSModule(RCTEventEmitter.class).receiveEvent(mView.getId(), eventName, event);
    }

    // Download our image.
    private void startDownloading(final Handler mainThread) {
        // Create a new background thread to download our image
        new Thread(() -> {
            try {
                // Download, blocking THIS background thread but not the main one
                URL url = new URL(logoURL);
                final Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                // Go back to the main thread and set the image bitmap
                mainThread.post(() -> mView.setImageBitmap(bmp));
            } catch (Exception e) {
                Log.e("ReactImageManager", "Error : " + e.getMessage());
            }
        }).start();
    }
}
