package com.zhengxizhen.frame;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @package com.zhengxizhen.frame
 * @Description 基础框架子视图
 * @author 郑锡真
 * @date 2015-03-17
 * @version 1.0.1
 * */
public abstract class BaseFragment extends Fragment {
	/**
	 * 当前布局视图
	 * */
	protected View mRoot;

	protected Context mContext;
	/**
	 * 当前页面标题
	 * */
	protected TextView tv_title;
	/**
	 * 当前页面进度条
	 * */
	protected ProgressBar mBar;

	public BaseFragment() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity().getApplicationContext();
	}

	/**
	 * 布局文件ID
	 * */
	public abstract int getViewLayoutId();

	/**
	 * 标题ID
	 * */
	public abstract int getTitle();

	/**
	 * 进度条ID
	 * */
	public abstract int getProgressBar();

	/**
	 * 初始化ID
	 * */
	public abstract void initView();

	/**
	 * 机械返回按键响应事件
	 * */
	public abstract boolean onBackPress();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRoot = inflater.inflate(getViewLayoutId(), container, false);
		tv_title = (TextView) mRoot.findViewById(getTitle());
		mBar = (ProgressBar) mRoot.findViewById(getProgressBar());
		initView();
		return mRoot;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

	public final void setTitle(String title) {
		tv_title.setText(title);
	}

	public final void toFragment(Fragment fragment, boolean canBack) {
		((BaseFragmentActivity) getActivity()).toFragment(fragment, canBack);
	}

	public final void toFragment(Fragment fragment, boolean canBack,
			boolean anim) {
		((BaseFragmentActivity) getActivity()).toFragment(fragment, canBack,
				anim);
	}

	protected Context getContext() {
		return mContext;
	}

	protected Context getApplicationContext() {
		return mContext;
	}

	public View findViewById(int id) {
		return mRoot.findViewById(id);
	}

	public void showToast(String msg) {
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
	}

	public void showToast(int resString) {
		if (isAdded()) {
			showToast(getResources().getString(resString));
		}
	}

}
