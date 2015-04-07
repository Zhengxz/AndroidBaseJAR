package com.zhengxizhen.frame;

import android.support.v4.app.Fragment;

/**
 * @package com.zhengxizhen.frame
 * @Description 基础主框架接口
 * @author 郑锡真
 * @date 2015-03-17
 * @version 1.0.1
 * */
public interface BaseActivity {
	/**
	 * 前往某个子视图
	 * 
	 * @param fragment
	 *            目标子视图
	 * @param canBack
	 *            true：添加到视图堆栈中;false：清空原视图堆栈，再将视图添加入视图堆栈
	 * */
	public void toFragment(Fragment fragment, boolean canBack);

	/**
	 * 前往某个子视图
	 * 
	 * @param fragment
	 *            目标子视图
	 * @param canBack
	 *            true：添加到视图堆栈中;false：清空原视图堆栈，再将视图添加入视图堆栈
	 * @param anim
	 *            是否执行渐入渐出
	 * */
	public void toFragment(Fragment fragment, boolean canBack, boolean anim);

	/**
	 * 是否已存在进度框
	 * */
	public abstract boolean isProgressDialogShowing();

	/**
	 * 创建并展示进度框
	 * */
	public abstract void showProgressDialog(String msg);

	/**
	 * 取消并关闭进度框
	 * */
	public abstract void cancelProgressDialog();
}
