package com.zhengxizhen.http;

/**
 * @Title ApnObject
 * @package com.zhengxz_lib.http
 * @Description APN
 * @author 郑锡真
 * @date 2015-02-04
 * @version 1.0.0
 * */
public class ApnObject {
    public int id;
    public String proxy;
    public int port;
    public String apn = "";

    public String getProxy() {
	return this.proxy;
    }

    public void setProxy(String proxy) {
	this.proxy = proxy;
    }

    public int getId() {
	return this.id;
    }

    public void setId(int id) {
	this.id = id;
    }

    public int getPort() {
	return this.port;
    }

    public void setPort(int port) {
	this.port = port;
    }
}
