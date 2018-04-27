package com.nd.sdp.android.sample.module2;

import com.nd.sdp.android.serviceloader.annotation.Service;
import com.nd.sdp.android.sample.sdk.ISampleList;

@Service(ISampleList.class)
public class SampleList2 implements ISampleList {
    @Override
    public void test() {
        System.out.println(getClass().getName());
    }
}
