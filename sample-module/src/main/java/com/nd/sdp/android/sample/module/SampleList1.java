package com.nd.sdp.android.sample.module;

import com.nd.sdp.android.serviceloader.annotation.Service;
import com.nd.sdp.android.sample.sdk.ISampleList;

@Service(ISampleList.class)
public class SampleList1 implements ISampleList {
    @Override
    public void test() {
        System.out.println(getClass().getName());
    }
}
