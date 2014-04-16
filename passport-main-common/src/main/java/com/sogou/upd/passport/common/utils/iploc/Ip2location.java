package com.sogou.upd.passport.common.utils.iploc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


public class Ip2location {
	static long MAX_FILE_LEN = 100*1024*1024;
	static long EXPACTED_VERSION = 4;
	
	byte[] buff;

	public static void main(String[] args){
		try {
			Ip2location iptools = new Ip2location("./data/location.dat"); 
//			System.out.println(iptools.getLocation("117.136.9.128"));
//			System.out.println(iptools.getCode("114.112.45.128"));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void readData(InputStream in) throws IOException {
		try  {
			byte[] header = new byte[12];
			if (in.read(header) != header.length) {
				throw new IOException("Illegal file format: file too short");
			}
			if(header[0]!='I' || header[1]!='P' || header[2]!='L' || header[3]!='C'){
				throw new IOException("Illegal file format: 'IPLC' missing");
			}
			long version = byte2long(header, 4);
			if (version != EXPACTED_VERSION) {
				throw new IOException("Illegal file format: version mismatch");
			}
			long objnum = byte2long(header,8);
			byte[] buff = new byte[(int)(objnum*3*4)];
			
			// 读到缓存中
			final int readLen = 1024;
			byte[] readBuf = new byte[readLen];
			
			int offset = 0;
			int readBytes = 0;
			while ((readBytes = in.read(readBuf)) != -1) {
				System.arraycopy(readBuf, 0, buff, offset, readBytes);
				offset += readBytes;
			}
			this.buff = buff;
		} catch (Exception e) {
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {}
			}
		}
	}
	
	public void readData(String ipFileStream) throws IOException {
		File f = new File(ipFileStream);
		long len = f.length();
		if (len > MAX_FILE_LEN) {
			throw new IOException("file size too large: " + len + "(>" + MAX_FILE_LEN + ")");
		}
		FileInputStream in = null;
		try {
			in = new FileInputStream(ipFileStream);
			byte[] header = new byte[12];
			if (in.read(header) != header.length) {
				throw new IOException("Illegal file format: file too short");
			}
			if(header[0]!='I' || header[1]!='P' || header[2]!='L' || header[3]!='C'){
				throw new IOException("Illegal file format: 'IPLC' missing");
			}
			long version = byte2long(header, 4);
			if (version != EXPACTED_VERSION) {
				throw new IOException("Illegal file format: version mismatch");
			}
			long objnum = byte2long(header,8);
			if (objnum * 3* 4 + 4 + 8 != len) {
				throw new IOException("Illegal file format: file length not match");
			}
			byte[] buff = new byte[(int)(objnum*3*4)];
			in.read(buff);
			this.buff = buff;
		} catch (Exception e) {
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {}
			}
		}
		
	}
	public Ip2location(){
		
	}
	public Ip2location(String ipFile){
		
		try {
			readData(ipFile);
		} catch (Exception e) {
		}
	}
	private long getLongFromIp(String ip){
		String[] items = ip.split("\\.");
		if(items.length != 4){
			return -1;
		}
		int[] buf = new int[4];
		buf[3] = Integer.parseInt(items[0]);
		buf[2] = Integer.parseInt(items[1]);
		buf[1] = Integer.parseInt(items[2]);
		buf[0] = Integer.parseInt(items[3]);
		
		long x = ((buf[3] & 0xFFl) << 24)
		+ ((buf[2] & 0xFFl) << 16)
		+ ((buf[1] & 0xFFl) << 8)
		+ ((buf[0] & 0xFFl) << 0);
		return x;
		
	}
	public String getCode(String ip){
		long ip_long = getLongFromIp(ip);
		if(ip_long < 0){
			return "unknown";
		}
		Long ipinfo = getLocationFromIp(ip_long);
		if(ipinfo == null){
			return "unknown";
		}
		String code   = IpInfo.IPLC_Decode(ipinfo);
		if(code.startsWith("CN")){
			return code.substring(0,6);
		}
		return "unknown";
	}
	public String getLocation(String ip){
		
		long ip_long = getLongFromIp(ip);
		if(ip_long < 0){
			return "unknown";
		}
		Long ipinfo = getLocationFromIp(ip_long);
		if(ipinfo == null){
			return IpInfo.STR_UNKNOWN;
		}
		String code   = IpInfo.IPLC_Decode(ipinfo);
		if(code != null){
			return code;
		}
		return IpInfo.STR_UNKNOWN;
	}
	private static long byte2long(byte[] buf, int start){
		
		long x = ((buf[start+3] & 0xFFl) << 24)
		+ ((buf[start+2] & 0xFFl) << 16)
		+ ((buf[start+1] & 0xFFl) << 8)
		+ ((buf[start+0] & 0xFFl) << 0);
		return x;
	}
	
	private Long getLocationFromIp(Long ip){
		int low,mid,high;
	    low = 0;
	    mid = 0;
	    high = buff.length /(3*4) -1;
	    while(low<=high)
	    {
	        mid = low+((high-low)/2);
	        if (byte2long(buff, mid*3*4)==ip)
	            return byte2long(buff, mid*3*4 + 8);
	        else if (byte2long(buff, mid*3*4) > ip)
	            high = mid-1;
	        else
	            low = mid+1;
	    }
	    if (byte2long(buff, mid*3*4) < ip)
	    {
	        if (byte2long(buff, mid*3*4 + 4) >= ip)
	            return byte2long(buff, mid*3*4 + 8);
	    }
	    else
	    {
	        if (mid>0 && byte2long(buff, (mid-1)*3*4) <ip && byte2long(buff, (mid-1)*3*4+4) >= ip)
	            return byte2long(buff, (mid-1)*3*4 + 8);
	    }

		return null;
	}
}
