package com.whattoeat.net;

public interface HttpSessionCallBack {

	public void onSucceed(String rspData);

	public void onError(int errCode, String strErr);

}
