package com.android.shortvideo.common.http;

import com.android.shortvideo.common.http.AbstractHttpClient.ResponseResult;



/**
 * @author pengjin
 *
 * @param <TResult>
 * 
 * http请求响应回调接口
 */
public interface ResponseListener<TResult extends BaseResponseResultData> {
    
    /**
     * 请求成功
     * 
     * @param responseCode 结果码
     * @param result 结果内容
     */
    public void onSuccess(int responseCode, ResponseResult<TResult> result);
    
    /**
     * 请求失败
     * 
     * @param responseCode 结果码 -1:请求参数有误
     */
    public void onFailed(int responseCode);
}
