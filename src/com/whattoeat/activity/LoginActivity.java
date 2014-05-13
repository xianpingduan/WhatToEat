package com.whattoeat.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.whattoeat.App;
import com.whattoeat.R;
import com.whattoeat.controller.ProtocolListener.ReqLoginListener;
import com.whattoeat.controller.ReqLoginController;
import com.whattoeat.controller.ProtocolListener.LOGIN_TYPE;
import com.whattoeat.data.LoginInfo;
import com.whattoeat.utils.LogUtils;

public class LoginActivity extends BaseActivity  implements  OnClickListener{
	
	private Button  mLoginBtn;
	private EditText mLoginAccoutEt;
	private EditText mLoginPwdEt;
	
	
	private String mLoginAcStr;
	private String mLoginPwdStr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		//		getActionBar().hide();
		initUI();
	}
	
	private void initUI(){
		mLoginAccoutEt  = (EditText) findViewById(R.id.login_account);
		mLoginPwdEt  = (EditText) findViewById(R.id.login_pwd);
		mLoginBtn   = (Button) findViewById(R.id.login);
		
		mLoginBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.login:
			if(vilidate()){
				ReqLoginController  loginController  = new ReqLoginController(mLoginAcStr, mLoginPwdStr, LOGIN_TYPE.APP, loginListener);
				loginController.doRequest();
			}
			break;
		default:
			break;
		}
	}
	
	
	private boolean  vilidate(){
		
		mLoginAcStr = mLoginAccoutEt.getText().toString();
		mLoginPwdStr = mLoginPwdEt.getText().toString();
		
		if(mLoginAcStr.isEmpty()){
			Toast.makeText(LoginActivity.this, App.getAppContext().getString(R.string.login_account_hint), Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if(mLoginPwdStr.isEmpty()){
			Toast.makeText(LoginActivity.this, App.getAppContext().getString(R.string.login_pwd_hint), Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}
	
	
	private ReqLoginListener   loginListener  = new ReqLoginListener() {
		
		@Override
		public void onNetError(int errCode, String errorMsg) {
			// TODO Auto-generated method stub
			LogUtils.e("onNetError,errCode="+errCode+"|errorMsg="+errorMsg);
		}
		
		@Override
		public void onReqLoginInfoSucceed(LoginInfo appInfo) {
			// TODO Auto-generated method stub
			LogUtils.e("onReqLoginInfoSucceed,appInfo="+appInfo);
		}
		
		@Override
		public void onReqFailed(int statusCode, String errorMsg) {
			// TODO Auto-generated method stub
			LogUtils.e("onReqFailed,errCode="+statusCode+"|errorMsg="+errorMsg);
		}
	};
}
