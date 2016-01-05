package com.android.shortvideo.common.http;


import org.json.JSONException;
import org.json.JSONObject;

/**
 * http请求的基类 
 * 
 * 注意：
 * 1.所有的http请求相关业务类需要继承本类
 * 2.子类需定义BaseRequestParam和BaseResponseResultData
 * 3.子类负责实现convRequestParam(TParam param)和convResponseResult(JSONObject jsonObject)方法
 * 4.如果请求参数或者返回结果没有任何定制参数，可以直接传入EmptyRequestParam和EmptyResponseResultData
 * 
 * @author ls
 * 
 */
public abstract class AbstractHttpClient<TParam extends BaseRequestParam, TResult extends BaseResponseResultData> {
	
	protected static final String TAG = AbstractHttpClient.class.getSimpleName();
	
	
	/**
	 * 设置http请求参数
	 */
	public abstract JDHttpClientParam convRequestParam(TParam param);

	
	/**
	 * 转换http请求结果
	 */
	public abstract TResult convResponseResult(String jsonString) throws JSONException;

	
	/**
	 * http请求结果结构
	 */
	public static class ResponseResult<TResult> {

		/**
		 * 结果码 0：成功；其他：失败
		 */
		public int rtn;

		/** 相关信息 */
		public String msg;

		/** session状态 */
		public int sessionStatus;
		
		/** 业务相关数据对象 */
		public TResult data;
		
		/** 解析前，http返回的ResponseContent，用作上层做缓存时保存 */
        public String responseContent; 
	}

	

	/**
	 * 获取推荐数据列表
	 * 
	 * @param <T>
	 */
	public void request(TParam param, final ResponseListener<TResult> listener) {

		final JDHttpClientParam clientParam = convRequestParam(param);
		JDHttpClientListener clientListener = new JDHttpClientListener() {

			// 成功
			@Override
			public void onSuccess(int responseCode, String responseContent) {
				if (listener != null) {
					ResponseResult<TResult> result = processResponseResult(responseContent,clientParam.method);
					result.responseContent = responseContent;
					listener.onSuccess(responseCode, result);
					//检查session状态
//					HttpSessionUtil.checkSession(result.rtn, result.sessionStatus);
				}
			}

			// 失败
			@Override
			public void onFailed(int responseCode) {
				if (listener != null) {
					listener.onFailed(responseCode);
				}
			}
		};

		JDHttpClient client = new JDHttpClient();
		client.request(clientParam, clientListener);
	}

	

	/**
	 * 处理查询结果
	 */
	public ResponseResult<TResult> processResponseResult(String responseContent,int method) {
		ResponseResult<TResult> result = new ResponseResult<TResult>();

		try {
			
				//在执行登录注册，提交订单这些操作是才使用post请求，商店和商品使用get请求
				if(method==JDHttpClientParam.METHOD_POST){
					try {
						JSONObject jsonObject = new JSONObject(responseContent);
						result.msg = jsonObject.optString("msg");
					} catch (Exception e) {
						// TODO: handle exception
					}
					result.data = convResponseResult(responseContent);
					
				}
				else{
//					JSONArray jsonArray = new JSONArray(responseContent);
					try {
						JSONObject jsonObject = new JSONObject(responseContent);
						result.rtn = jsonObject.optInt("rtn");
					} catch (Exception e) {
						// TODO: handle exception
					}					
					result.data = convResponseResult(responseContent);
				}
			

		} catch (JSONException e) {
			e.printStackTrace();
			result.rtn = -1; // 异常情况
		}
		return result;
	}
}
