package com.nd.sdp.android.sample.module;

import android.content.Context;

import com.nd.sdp.android.serviceloader.annotation.Service;
import com.nd.sdp.android.sample.sdk.ISampleContext;

/**
 * Created by Young on 2018/4/26.
 */
@Service(ISampleContext.class)
public class SampleContext implements ISampleContext {

    private Context mContext;

    public SampleContext(Context context) {
        mContext = context;
    }

    @Override
    public void test() {
        System.out.println("Success " + mContext);
    }

}
