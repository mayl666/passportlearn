//package com.sogou.upd.passport.web.internal.inteceptor;
//
//import com.sogou.upd.passport.common.lang.StringUtil;
//
//import java.io.*;
//import java.util.Enumeration;
//import java.util.Map;
//import java.util.Vector;
//import javax.servlet.ServletInputStream;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletRequestWrapper;
//
///**
// * User: ligang201716@sogou-inc.com
// * Date: 13-6-5
// * Time: 下午3:19
// */
//public class ParameterRequestWrapper extends HttpServletRequestWrapper {
//
//    private Map<String,String> headerMap;
//
//    private String data;
//
//    public ParameterRequestWrapper(HttpServletRequest request) {
//        super(request);
//    }
//
//
//    /**
//     * The default behavior of this method is to return getDateHeader(String name)
//     * on the wrapped request object.
//     */
//    @Override
//    public long getDateHeader(String name) {
//        return this._getHttpServletRequest().getDateHeader(name);
//    }
//
//    /**
//     * The default behavior of this method is to return getHeader(String name)
//     * on the wrapped request object.
//     */
//    @Override
//    public String getHeader(String name) {
//        return this._getHttpServletRequest().getHeader(name);
//    }
//
//    /**
//     * The default behavior of this method is to return getHeaders(String name)
//     * on the wrapped request object.
//     */
//    @Override
//    public Enumeration getHeaders(String name) {
//        return this._getHttpServletRequest().getHeaders(name);
//    }
//
//    /**
//     * The default behavior of this method is to return getHeaderNames()
//     * on the wrapped request object.
//     */
//    @Override
//    public Enumeration getHeaderNames() {
//        return this._getHttpServletRequest().getHeaderNames();
//    }
//
//    /**
//     * The default behavior of this method is to return getIntHeader(String name)
//     * on the wrapped request object.
//     */
//    @Override
//    public int getIntHeader(String name) {
//        return this._getHttpServletRequest().getIntHeader(name);
//    }
//
//    public void setHeader(String key,String value){
//        headerMap.put(key,value);
//    }
//
//    public String getContent(){
//
//    }
//
//    @Override
//    public ServletInputStream getInputStream() throws IOException {
//        if (!"".equals(data)) {
//            MyServletInputStream in = new MyServletInputStream(data);
//            return in;
//        }
//        return null;
//    }
//
//    private InputStream getStringStream(String data) {
//        if (!"".equals(data)) {
//            MyServletInputStream in = new MyServletInputStream(data);
//            return in;
//        }
//        return null;
//    }
//
//    private String getStreamString(InputStream in) {
//        if(StringUtil.isBlank(data)){
//            if (in != null) {
//                try {
//                    BufferedReader tBufferedReader = new BufferedReader(
//                            new InputStreamReader(in));
//                    StringBuffer tStringBuffer = new StringBuffer();
//                    String sTempOneLine = new String("");
//                    while ((sTempOneLine = tBufferedReader.readLine()) != null) {
//                        tStringBuffer.append(sTempOneLine);
//                    }
//                    data=tStringBuffer.toString();
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                }
//            }
//        }
//        return data;
//    }
//
//
//    public class MyServletInputStream extends ServletInputStream {
//
//        private ByteArrayInputStream byteArrIn = null;
//
//        public MyServletInputStream(String data) {
//            byteArrIn = new ByteArrayInputStream(data.getBytes());
//        }
//
//        @Override
//        public int read() throws IOException {
//            return byteArrIn.read();
//        }
//
//    }
//}
