package com.sogou.upd.passport.common.utils;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.http.HttpEntity;
import org.apache.http.util.ByteArrayBuffer;

import java.io.*;

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
            int i = (int) entity.getContentLength();
            if (i < 0) {
                i = 4096;
            }
            ByteArrayBuffer buffer = new ByteArrayBuffer(i);
            byte[] tmp = new byte[8092];
            int l;
            while ((l = instream.read(tmp)) != -1) {
                buffer.append(tmp, 0, l);
            }
            return buffer.toByteArray();
        } finally {
            instream.close();
        }
    }


    public static byte[] toByteArrayNew(final HttpMethod method) throws IOException {
        if (method == null) {
            throw new IllegalArgumentException("HTTP entity may not be null");
        }
        BufferedInputStream instream = new BufferedInputStream(method.getResponseBodyAsStream());
        if (instream == null) {
            return null;
        }
        try {
            if (method.getResponseBodyAsString().length() > Integer.MAX_VALUE) {
                throw new IllegalArgumentException("HTTP entity too large to be buffered in memory");
            }
            int i = method.getResponseBodyAsString().length();
            if (i < 0) {
                i = 4096;
            }
            ByteArrayBuffer buffer = new ByteArrayBuffer(i);
            byte[] tmp = new byte[8092];
            int l;
            while ((l = instream.read(tmp)) != -1) {
                buffer.append(tmp, 0, l);
            }
            return buffer.toByteArray();
        } finally {
            instream.close();
        }
    }


    public static String getContent1(HttpEntity entity) throws IOException {
        InputStream in = entity.getContent();
        BufferedReader stream = new BufferedReader(new InputStreamReader(in));
        StringBuilder response = new StringBuilder();
        String inputLine;
        while ((inputLine = stream.readLine()) != null) {
            response.append(inputLine);
        }
        return response.toString();
    }


    private static String getContent2(HttpEntity entity) throws IOException {
        InputStream in = entity.getContent();
        BufferedInputStream stream = new BufferedInputStream(in);
        int inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = stream.read()) != -1) {
            response.append(Integer.toHexString(inputLine));
        }
        return response.toString();
    }



}
