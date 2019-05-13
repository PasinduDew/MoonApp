package com.industrialmaster.notationchordslyrics;


import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Custom implementation of Volley Request Queue
 */
public class MyVolleySingleton{

    private static MyVolleySingleton mInstance;
    private static Context mCtx;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;


    private MyVolleySingleton(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();

//        mImageLoader = new ImageLoader(mRequestQueue,
//                new ImageLoader.ImageCache() {
//                    private final LruCache<String, Bitmap> cache = new LruCache<String, Bitmap>(20);
//
//                    @Override
//                    public Bitmap getBitmap(String url) {
//                        return cache.get(url);
//                    }
//
//                    @Override
//                    public void putBitmap(String url, Bitmap bitmap) {
//                        cache.put(url, bitmap);
//                    }
//                });
    }

    public static synchronized MyVolleySingleton getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new MyVolleySingleton(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {

            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());

//            Cache cache = new DiskBasedCache(mCtx.getCacheDir(), 10 * 1024 * 1024);
//            Network network = new BasicNetwork(new HurlStack());
//            mRequestQueue = new RequestQueue(cache, network);
//            // Don't forget to start the volley request queue
//            mRequestQueue.start();
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> request){

        mRequestQueue.add(request);
    }

//    public ImageLoader getImageLoader() {
//        return mImageLoader;
//    }

}




