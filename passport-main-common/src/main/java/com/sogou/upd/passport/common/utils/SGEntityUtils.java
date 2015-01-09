package com.sogou.upd.passport.common.utils;

import org.apache.http.HttpEntity;
import org.apache.http.util.ByteArrayBuffer;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: lzy_clement
 * Date: 15-1-9
 * Time: 上午11:51
 * To change this template use File | Settings | File Templates.
 */
public final class SGEntityUtils {
    private SGEntityUtils() {
    }

    public static byte[] toByteArray(final HttpEntity entity) throws IOException {
        if (entity == null) {
            throw new IllegalArgumentException("HTTP entity may not be null");
        }
        BufferedInputStream instream = new BufferedInputStream(entity.getContent());
        if (instream == null) {
            return null;
        }
        try {
            if (entity.getContentLength() > Integer.MAX_VALUE) {
                throw new IllegalArgumentException("HTTP entity too large to be buffered in memory");
            }
            int i = (int)entity.getContentLength();
            if (i < 0) {
                i = 4096;
            }
            ByteArrayBuffer buffer = new ByteArrayBuffer(i);
            byte[] tmp = new byte[4096];
            int l;
            while((l = instream.read(tmp)) != -1) {
                buffer.append(tmp, 0, l);
            }
            return buffer.toByteArray();
        } finally {
            instream.close();
        }
    }
}
