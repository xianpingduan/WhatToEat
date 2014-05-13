package com.whattoeat.controller;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.alibaba.fastjson.JSON;
import com.whattoeat.controller.ProtocolListener.REQUST_ACTION_PATH;
import com.whattoeat.controller.ProtocolListener.ReqLoginListener;
import com.whattoeat.data.LoginInfo;
import com.whattoeat.utils.LogUtils;

public class ReqLoginController  extends AbstractNetController{

	private final  String user_account ;
	private final String user_pwd;
	private final int type;
    private final ReqLoginListener mListener;
	
	public  ReqLoginController( String useraccout , String userpwd , int type,ReqLoginListener listener){
		user_account = useraccout;
		user_pwd = userpwd;
		this.type= type;
		mListener  = listener;
	}
	
	@Override
	protected ArrayList<NameValuePair> getRequestBody() {
		LogUtils.e("getRequestBody");
		final  ArrayList<NameValuePair> arrayList= new ArrayList<NameValuePair>();
		arrayList.add(new BasicNameValuePair("user_account",user_account));
		arrayList.add(new BasicNameValuePair("user_pwd",user_pwd));
		arrayList.add(new BasicNameValuePair("type",type+""));
		return arrayList;
	}

	@Override
	protected String getRequestAction() {
		return REQUST_ACTION_PATH.LOGIN ;
	}

	@Override
	protected void handleResponseBody(String resData) {
		final LoginInfo loginInfo =JSON.parseObject(resData, LoginInfo.class);
		if( mListener == null )
		{
		    return;
		}
		mListener.onReqLoginInfoSucceed(loginInfo);
	}

	@Override
	protected void handleResponseError(int errCode, String strErr) {
		if( mListener == null )
		{
		    return;
		}
		mListener.onNetError( errCode , strErr );
	}

}
