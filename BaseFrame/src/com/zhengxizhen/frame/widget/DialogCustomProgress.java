package com.zhengxizhen.frame.widget;

import com.zhengxizhen.baseframe.R;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.widget.TextView;

public class DialogCustomProgress extends Dialog {

	private static DialogCustomProgress mDialogCustomProgress = null;

	public DialogCustomProgress(Context context) {
		super(context);
	}

	public DialogCustomProgress(Context context, int theme) {

		super(context, theme);

	}

	public static DialogCustomProgress createDialog(Context context) {
		mDialogCustomProgress = new DialogCustomProgress(context,
				R.style.CustomProgressDialog);
		mDialogCustomProgress.setContentView(R.layout.dialog_progress);
		mDialogCustomProgress.getWindow().getAttributes().gravity = Gravity.CENTER;
		return mDialogCustomProgress;

	}

	private void startPro() {
		// View pro = mDialogCustomProgress.findViewById(R.id.loadingImageView);
		// if (pro != null) {
		// pro.startAnimation(AnimationUtils.loadAnimation(getContext(),
		// R.anim.progress_round));
		// }
	}

	@Override
	public void show() {
		super.show();
		startPro();
	}

	public DialogCustomProgress setTitile(String strTitle) {
		return mDialogCustomProgress;
	}

	public DialogCustomProgress setMessage(String strMessage) {

		TextView tvMsg = (TextView) mDialogCustomProgress
				.findViewById(R.id.id_tv_loadingmsg);
		if (tvMsg != null) {
			tvMsg.setText(strMessage);
		}

		return mDialogCustomProgress;

	}

}