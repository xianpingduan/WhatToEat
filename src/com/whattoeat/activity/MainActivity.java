package com.whattoeat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.whattoeat.R;


public class MainActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getActionBar().hide();
		
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				login();
			}
		}, 0);
	}
	

	private void login(){
		
		Intent  intent = new Intent();
		intent.setClass(MainActivity.this, LoginActivity.class);
		startActivity(intent);
	}

}
