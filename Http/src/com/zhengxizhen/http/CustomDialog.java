package com.zhengxizhen.http;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

public class CustomDialog extends Dialog {

    public static final int ID_OK = R.id.btn_ok;
    public static final int ID_NO = R.id.btn_no;

    public CustomDialog(Context context, int theme) {
	super(context, theme);
    }

    public CustomDialog(Context context) {
	super(context);
    }

    public static class Builder {

	private Context context;

	private String title;
	private String message;
	private String positiveButtonText;
	private String negativeButtonText;
	private OnCancelListener mCancelListener;
	private DialogInterface.OnClickListener positiveButtonClickListener,
		negativeButtonClickListener;
	private int mButtonCount;

	public Builder(Context context) {
	    this.context = context;
	}

	public Builder setMessage(String message) {
	    this.message = message;
	    return this;
	}

	public Builder setMessage(int message) {
	    this.message = (String) context.getText(message);
	    return this;
	}

	public Builder setTitle(int title) {
	    this.title = (String) context.getText(title);
	    return this;
	}

	public Builder setTitle(String title) {
	    this.title = title;
	    return this;
	}

	public Builder setPositiveButton(int positiveButtonText,
		DialogInterface.OnClickListener listener) {
	    this.positiveButtonText = (String) context
		    .getText(positiveButtonText);
	    this.positiveButtonClickListener = listener;
	    mButtonCount++;
	    return this;
	}

	public Builder setPositiveButton(String positiveButtonText,
		DialogInterface.OnClickListener listener) {
	    this.positiveButtonText = positiveButtonText;
	    this.positiveButtonClickListener = listener;
	    mButtonCount++;
	    return this;
	}

	public Builder setNegativeButton(int negativeButtonText,
		DialogInterface.OnClickListener listener) {
	    this.negativeButtonText = (String) context
		    .getText(negativeButtonText);
	    this.negativeButtonClickListener = listener;
	    mButtonCount++;
	    return this;
	}

	public Builder setNegativeButton(String negativeButtonText,
		DialogInterface.OnClickListener listener) {
	    this.negativeButtonText = negativeButtonText;
	    this.negativeButtonClickListener = listener;
	    mButtonCount++;
	    return this;
	}

	public Builder setListener(DialogInterface.OnClickListener listener) {
	    this.negativeButtonClickListener = listener;
	    this.positiveButtonClickListener = listener;
	    return this;
	}

	public Builder setOnCancelListener(
		DialogInterface.OnCancelListener listener) {
	    mCancelListener = listener;
	    return this;
	};

	@SuppressLint("InflateParams")
	public CustomDialog create() {
	    LayoutInflater inflater = (LayoutInflater) context
		    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    final CustomDialog dialog = new CustomDialog(context,
		    R.style.ZhengxzCtDialog);
	    View layout = inflater.inflate(R.layout.dialog_layout, null);
	    final TextView tvMsg = (TextView) layout.findViewById(R.id.tv_msg);
	    final TextView tvTitle = (TextView) layout
		    .findViewById(R.id.tv_title);
	    tvTitle.setText(title);
	    tvMsg.setText(message);

	    dialog.addContentView(layout, new LayoutParams(-1, -2));
	    dialog.setContentView(layout);

	    if (mButtonCount < 2) {
		layout.findViewById(R.id.line1).setVisibility(View.GONE);
	    }

	    if (mCancelListener != null) {
		dialog.setOnCancelListener(mCancelListener);
	    }

	    final View.OnClickListener listener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
		    final int id = v.getId();
		    if (id == R.id.btn_ok) {
			if (null != positiveButtonClickListener) {
			    positiveButtonClickListener.onClick(dialog, id);
			}
		    } else if (id == R.id.btn_no) {
			if (null != negativeButtonClickListener) {
			    negativeButtonClickListener.onClick(dialog, id);
			}
		    } else if (id == R.id.btn_close) {
		    } else {
		    }

		    dialog.dismiss();
		}
	    };

	    layout.findViewById(R.id.btn_close).setOnClickListener(listener);
	    final Button btnOk = (Button) layout.findViewById(R.id.btn_ok);
	    btnOk.setOnClickListener(listener);
	    if (!TextUtils.isEmpty(positiveButtonText)) {
		btnOk.setText(positiveButtonText);
		btnOk.setVisibility(View.VISIBLE);
	    } else {
		btnOk.setVisibility(View.GONE);
	    }

	    final Button btnNo = (Button) layout.findViewById(R.id.btn_no);
	    btnNo.setOnClickListener(listener);
	    if (!TextUtils.isEmpty(negativeButtonText)) {
		btnNo.setText(negativeButtonText);
		btnNo.setVisibility(View.VISIBLE);
	    } else {
		btnNo.setVisibility(View.GONE);
	    }
	    return dialog;
	}

    }
}
