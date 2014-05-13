package com.whattoeat.controller;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.whattoeat.DeviceInfo;
import com.whattoeat.controller.ProtocolListener.ERROR;
import com.whattoeat.net.AsyncHttpPostSession;
import com.whattoeat.net.AsyncHttpSession;
import com.whattoeat.net.HttpSessionCallBack;
import com.whattoeat.net.ServerUrl;
import com.whattoeat.utils.LogUtils;


/**
 * 数据转换采用fastJson解析
*/
public abstract class AbstractNetController {

	private AsyncHttpPostSession mSession;

	/**
	 * 子类实现，返回请求的业务包体
	 * 
	 * @return ByteString
	 */
	protected abstract ArrayList<NameValuePair> getRequestBody();

	/**
	 * 子类实现，返回请求的action设置
	 * 
	 * @return
	 */
	protected abstract String getRequestAction();

	/**
	 * 子类实现，处理返回的业务数据与业务本身的逻辑错误
	 * 
	 * @param byteString
	 */
	protected abstract void handleResponseBody(String resData);

	/**
	 * 子类实现，处理返回的网络与包数据相关的错误
	 * 
	 * @param errCode
	 * @param strErr
	 */
	protected abstract void handleResponseError(int errCode, String strErr);

	/**
	 * 
	 * @return 对应请求的服务器地址
	 */
	protected String getServerUrl() {
		LogUtils.e("ServerUrl ="+ServerUrl.getServerUrlApp() + getRequestAction());
		return ServerUrl.getServerUrlApp() + getRequestAction();
	}

	protected AbstractNetController() {
	}

	public AsyncHttpSession getSession() {
		return mSession;
	}

	public void doRequest() {
		mSession = new AsyncHttpPostSession(getServerUrl());
		mSession.registerCallBack(mHttpSessionCallBack);
		mSession.doPost(makePacket());
	}

	private ArrayList<NameValuePair> makePacket() {
		return getRequestBody();
	}

	private String getDeviceInfo() {
		LogUtils.e("DeviceInfo:" + DeviceInfo.getDeviceInfo());
		return DeviceInfo.getDeviceInfo();
	}

	private final HttpSessionCallBack mHttpSessionCallBack = new HttpSessionCallBack() {

		@Override
		public void onSucceed(String rspData) {
			try {
				JSONObject object = new JSONObject(rspData);
				LogUtils.e("object="+object.toString());
				int resCode = object.getInt("code");
				if (resCode>=0&&resCode <= 200) {
					handleResponseBody(object.getString("data"));
				} else {
					String msg = object.getString("msg");
					handleResponseError(ERROR.ERROR_ACTION_FAIL, msg);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				handleResponseError(ERROR.ERROR_BAD_PACKET, e.getMessage());
			}
		}

		@Override
		public void onError(int errCode, String strErr) {
			LogUtils.e("errCode="+errCode +"|"+strErr);
			handleResponseError(errCode, strErr);

		}
	};

}
