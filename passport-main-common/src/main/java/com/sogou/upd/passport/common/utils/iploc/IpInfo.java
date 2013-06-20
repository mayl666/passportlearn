package com.sogou.upd.passport.common.utils.iploc;

public class IpInfo {
	public static final String STR_UNKNOWN = "unknown";
	long left;
	long right;
	long value;
	public IpInfo(long left,long right,long value){
		this.left = left;
		this.right = right;
		this.value = value;
	}
	public String IPLC_Decode() {
		return IPLC_Decode(value);
	}
	public static String IPLC_Decode(long value) {
	    long flag = value>>24;
		String retStr;
	    if (flag==0){
	    	retStr = String.format("CN%d", value);
	    }
	    else if (flag==0xFEl){
//	    	String b = new String(new Long(value&0xFF00l>>8).);
	    	retStr = String.format("%c%c", (char)((value>>8)&0xFFl), (char)(value&0xFFl));
	    }
	    else{
	    	retStr = STR_UNKNOWN;
	    }
	    return retStr;
	}
}
