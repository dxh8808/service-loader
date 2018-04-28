package com.nd.sdp.android.sample.module2;

import com.nd.sdp.android.serviceloader.annotation.Service;
import com.nd.sdp.android.sample.sdk.ISampleList;
import com.nd.sdp.android.serviceloader.annotation.ServiceName;

@Service(ISampleList.class)
@ServiceName("List2")
public class SampleList2 implements ISampleList {
    @Override
    public void test() {
        System.out.println(getClass().getName());
    }
}
