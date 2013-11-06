package com.sogou.upd.passport.zk;

import com.netflix.curator.framework.api.CompressionProvider;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-11-1
 * Time: 下午12:21
 */
public class SGCompressionProvider implements CompressionProvider {
    @Override
    public byte[] compress(String path, byte[] data) throws Exception {
        return data;
    }

    @Override
    public byte[] decompress(String path, byte[] compressedData) throws Exception {
        return compressedData;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
