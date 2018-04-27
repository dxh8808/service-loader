package com.nd.sdp.android.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.nd.sdp.android.serviceloader.AnnotationServiceLoader;
import com.nd.sdp.android.sample.sdk.ISample;
import com.nd.sdp.android.sample.sdk.ISampleList;

import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        long start = System.currentTimeMillis();
        ISample sample = AnnotationServiceLoader.load(ISample.class)
                .iterator()
                .next();
        sample.test();
        Iterator<ISampleList> iterator = AnnotationServiceLoader.load(ISampleList.class)
                .iterator();
        while (iterator.hasNext()) {
            ISampleList next = iterator.next();
            next.test();
        }
        Log.d("MainActivity", String.valueOf(System.currentTimeMillis() - start));
    }
}
