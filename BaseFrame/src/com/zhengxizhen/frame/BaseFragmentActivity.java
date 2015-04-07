package com.zhengxizhen.frame;

import java.util.Timer;
import java.util.TimerTask;

import com.zhengxizhen.baseframe.R;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

/**
 * @package com.zhengxizhen.frame
 * @Description 基础框架主页面
 * @author 郑锡真
 * @date 2015-03-17
 * @version 1.0.0
 * */
public abstract class BaseFragmentActivity extends FragmentActivity implements
		BaseActivity {

	private ProgressDialog mProDialog;

	@Override
	protected void onCreate(Bundle arg0) {
		// if (android.os.Build.VERSION.SDK_INT < 19) {
		// setTheme(android.R.style.Theme_Black_NoTitleBar);
		// } else {
		// setTheme(android.R.style.Theme_Black_NoTitleBar_Fullscreen);
		// }
		super.onCreate(arg0);
		setContentView(R.layout.act_fragment_frame);
	}

	public final void toFragment(Fragment fragment, boolean canBack) {
		toFragment(fragment, canBack, false);
	}

	public void postToRightAnim(FragmentTransaction mTran) {
		mTran.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_out);
	}

	public void postToLeftAnim(FragmentTransaction mTran) {
		mTran.setCustomAnimations(R.anim.push_right_in, R.anim.push_right_out);
	}

	protected void showProDialog(String str) {
		mProDialog = ProgressDialog.show(this, null, str, true, true);
	}

	protected void cancelProDialog() {
		if (null != mProDialog) {
			mProDialog.cancel();
		}
	}

	@Override
	public void toFragment(Fragment fragment, boolean canBack, boolean anim) {
		BaseFragmentMrg.toFragment(this, fragment, canBack, anim);
	}

	private int exitCount = 0;

	private void eixt() {
		this.finish();
	}

	public void tryExit() {
		if (exitCount++ > 0) {
			eixt();
		} else {
			Toast.makeText(this, "再按一次“返回”退出", Toast.LENGTH_SHORT).show();
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					exitCount = 0;
				}
			}, 1000);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public void onBackPressed() {
		if (!BaseFragmentMrg.onKeyBack(this)) {
			super.onBackPressed();
		}
	}

	public abstract boolean isProgressDialogShowing();

	public abstract void showProgressDialog(String msg);

	public abstract void cancelProgressDialog();
}