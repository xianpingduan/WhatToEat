package com.whattoeat.controller;

import com.whattoeat.data.LoginInfo;

public interface ProtocolListener {

	public static final class REQUST_ACTION_PATH {
		public static final String LOGIN= "/login";

	}

	public static final class ERROR {
		public static int ERROR_ACTION_MISMATCH = 0x1000;
		public static int ERROR_BAD_PACKET = 0x1001;
		public static int ERROR_ACTION_FAIL = 0x1002;
	}
	
	public static final class LOGIN_TYPE{
		
		public static int APP = 1;
		public static int WEB = 0;
	}

	public interface AbstractNetListener {
		public void onNetError(int errCode, String errorMsg);
	}

	public interface ReqLoginListener extends AbstractNetListener {

		public void onReqFailed(int statusCode, String errorMsg);

		public void onReqLoginInfoSucceed(LoginInfo appInfo );

	}




}
