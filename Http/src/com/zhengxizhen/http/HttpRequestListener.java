package com.zhengxizhen.http;

/**
 * @Title HttpRequestListener
 * @package com.zhengxz_lib.http
 * @Description 回调接口
 * @author 郑锡真
 * @date 2015-02-04
 * @version 1.0.0
 * */
public interface HttpRequestListener {
    /**
     * 成功
     * */
    public void onSussece(int nThreadID, byte[] b, boolean fromCache);
    /**
     * 失败
     * */
    public void onFialed(int nThreadID, int nErrorCode);
}