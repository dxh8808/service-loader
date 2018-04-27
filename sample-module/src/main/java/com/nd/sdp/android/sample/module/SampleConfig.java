package com.nd.sdp.android.sample.module;

import com.nd.sdp.android.serviceloader.annotation.Service;
import com.nd.sdp.android.sample.sdk.ISample;

@Service(ISample.class)
public class SampleConfig implements ISample {
    @Override
    public void test() {
        System.out.println("Success");
    }
}
