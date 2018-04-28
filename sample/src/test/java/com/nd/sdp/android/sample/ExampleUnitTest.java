package com.nd.sdp.android.sample;

import com.nd.sdp.android.sample.sdk.ISampleList;
import com.nd.sdp.android.serviceloader.AnnotationServiceLoader;
import com.nd.sdp.android.sample.sdk.ISample;
import com.nd.sdp.android.serviceloader.exception.FetchException;

import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void get() throws Exception {
        assertEquals(4, 2 + 2);
        Iterator<ISample> iterator = AnnotationServiceLoader.load(ISample.class)
                .iterator();
        while (iterator.hasNext()) {
            iterator.next().test();
        }
    }

    @Test
    public void getByName() throws FetchException {
        ISampleList sampleList = AnnotationServiceLoader.load(ISampleList.class)
                .get("List2");
        sampleList.test();
    }
}