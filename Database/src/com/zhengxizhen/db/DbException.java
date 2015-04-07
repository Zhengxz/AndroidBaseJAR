package com.zhengxizhen.db;

public class DbException extends ComveeException {
    private static final long serialVersionUID = 1L;
    private String strMsg = null;

    public DbException() {
    }

    public DbException(String strMsg) {
	this.strMsg = strMsg;
    }

    public void printStackTrace() {
	if (strMsg != null)
	    System.err.println(strMsg);

	super.printStackTrace();
    }

}
