package com.zhengxizhen.frame;

import com.zhengxizhen.baseframe.R;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

/**
 * @package com.zhengxizhen.frame
 * @Description 子视图管理器
 * @author 郑锡真
 * @date 2015-03-17
 * @version 1.0.0
 * */
public class BaseFragmentMrg {

	private static final String TAG_FRAG = "zxz_frag";

	public static final void toFragment(FragmentActivity activity,
			Fragment frag, boolean canBack, boolean anim) {
		System.gc();
		try {
			FragmentManager mrg = activity.getSupportFragmentManager();
			FragmentTransaction mTran = mrg.beginTransaction();
			if (anim) {
				mTran.setCustomAnimations(R.anim.push_left_in,
						R.anim.push_left_out, R.anim.push_right_in,
						R.anim.push_right_out);
			}
			mTran.replace(R.id.content, frag, TAG_FRAG);
			if (canBack) {
				mTran.addToBackStack(null);
			}
			mTran.commit();
			mrg.executePendingTransactions();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static final void toBack(Fragment fragment) {
		fragment.getFragmentManager().popBackStack();
		fragment.getFragmentManager().beginTransaction().remove(fragment)
				.commit();
	}

	public static final boolean onKeyBack(FragmentActivity activity) {
		final BaseFragment frag = ((BaseFragment) activity
				.getSupportFragmentManager().findFragmentByTag(TAG_FRAG));
		if (frag == null) {
			return false;
		} else if (frag.onBackPress()) {
			return true;
		} else {
			toBack(frag);
			return true;
		}
	}

	public static final void showProDialog(Activity activity, Dialog dialog,
			String msg) {
		if (dialog == null) {
			dialog = new ProgressDialog(activity);
			dialog.setCanceledOnTouchOutside(false);
		}
		((ProgressDialog) dialog).setMessage(msg);
		if (!dialog.isShowing()) {
			dialog.show();
		}
	}

	public static final void closeProDialog(Dialog dialog) {
		if (dialog != null) {
			dialog.dismiss();
		}
	}

}
