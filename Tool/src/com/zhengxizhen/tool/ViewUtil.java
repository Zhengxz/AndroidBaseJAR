package com.zhengxizhen.tool;

import java.lang.reflect.Field;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @Title Tool
 * @package com.zhengxizhen.tool
 * @Description 视图工具类
 * @author 郑锡真
 * @date 2015-03-17
 * @version 1.0.0
 * */
public class ViewUtil {
	/**
	 * 设置TextView的值
	 * 
	 * @param root
	 *            根视图
	 * @param resId
	 *            TextView Id
	 * @param str
	 *            值
	 * */
	public static void setTextView(View root, int resId, String str) {
		((TextView) root.findViewById(resId)).setText(str);
	}

	/**
	 * 展示欢迎图片
	 * 
	 * @param imgRes
	 *            图片ID
	 * @param delayTime
	 *            时间
	 * */
	public static void showLuancherImage(Context cxt, int imgRes, int delayTime) {
		final WindowManager mWindows = (WindowManager) cxt
				.getSystemService(Context.WINDOW_SERVICE);
		WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();
		wmParams.type = 2002;
		wmParams.flags = 8;
		wmParams.format = PixelFormat.RGBA_8888;
		wmParams.gravity = Gravity.LEFT | Gravity.TOP;
		// wmParams.windowAnimations = android.R.style.Animation_Toast;
		wmParams.width = -1;
		wmParams.height = -1;
		final View layoutLuanch = new View(cxt);
		layoutLuanch.setBackgroundResource(imgRes);
		mWindows.addView(layoutLuanch, wmParams);
		Handler mHandler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				try {
					mWindows.removeView(layoutLuanch);
				} catch (Exception e) {
					e.printStackTrace();
				}
				System.gc();
			};
		};
		mHandler.sendEmptyMessageDelayed(2, 3000);
	}

	/** 计算ListView的高度 */
	public static void setListViewHeightBasedOnChildren(ListView listView,
			int attHeight) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}

		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1))
				+ attHeight;
		listView.setLayoutParams(params);
	}

	/** 计算GridView的高度 */
	public static void setGridViewHeightBasedOnChildren(GridView gridView) {
		// 获取GridView对应的Adapter
		ListAdapter listAdapter = gridView.getAdapter();
		if (listAdapter == null) {
			return;
		}
		int rows;
		int columns = 0;
		int horizontalBorderHeight = 0;
		Class<?> clazz = gridView.getClass();
		try {
			// 利用反射，取得每行显示的个数
			Field column = clazz.getDeclaredField("mRequestedNumColumns");
			column.setAccessible(true);
			columns = (Integer) column.get(gridView);
			// 利用反射，取得横向分割线高度
			Field horizontalSpacing = clazz
					.getDeclaredField("mRequestedHorizontalSpacing");
			horizontalSpacing.setAccessible(true);
			horizontalBorderHeight = (Integer) horizontalSpacing.get(gridView);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 判断数据总数除以每行个数是否整除。不能整除代表有多余，需要加一行
		if (listAdapter.getCount() % columns > 0) {
			rows = listAdapter.getCount() / columns + 1;
		} else {
			rows = listAdapter.getCount() / columns;
		}
		int totalHeight = 0;
		for (int i = 0; i < rows; i++) { // 只计算每项高度*行数
			View listItem = listAdapter.getView(i, null, gridView);
			listItem.measure(0, 0); // 计算子项View 的宽高
			totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
		}
		ViewGroup.LayoutParams params = gridView.getLayoutParams();
		params.height = totalHeight + horizontalBorderHeight * (rows - 1);// 最后加上分割线总高度
		gridView.setLayoutParams(params);
	}

	/** 展示EditText的错误信息 */
	public static void showEditError(EditText v, String msg) {
		v.requestFocus();
		v.setError(Html.fromHtml("<font color=#B2001F>" + msg + "</font>"));
	}

	/** 展示EditText自带的清空按钮 */
	public static void setEditWithClearButton(final EditText edt,
			final int imgRes) {

		edt.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				Drawable[] drawables = edt.getCompoundDrawables();
				if (hasFocus && edt.getText().toString().length() > 0) {
					edt.setTag(true);

					edt.setCompoundDrawablesWithIntrinsicBounds(drawables[0],
							drawables[1], edt.getContext().getResources()
									.getDrawable(imgRes), drawables[3]);

				} else {
					edt.setTag(false);
					edt.setCompoundDrawablesWithIntrinsicBounds(drawables[0],
							drawables[1], null, drawables[3]);
				}
			}
		});
		final int padingRight = Dip2Px(edt.getContext(), 50);
		edt.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_UP:
					int curX = (int) event.getX();
					if (curX > v.getWidth() - padingRight
							&& !TextUtils.isEmpty(edt.getText())) {
						if (edt.getTag() != null && (Boolean) edt.getTag()) {
							edt.setText("");
							int cacheInputType = edt.getInputType();
							edt.setInputType(InputType.TYPE_NULL);
							edt.onTouchEvent(event);
							edt.setInputType(cacheInputType);
							return true;
						} else {
							return false;
						}
					}
					break;
				}
				return false;
			}
		});

		edt.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

				Drawable[] drawables = edt.getCompoundDrawables();
				if (edt.getText().toString().length() == 0) {
					edt.setTag(false);
					edt.setCompoundDrawablesWithIntrinsicBounds(drawables[0],
							drawables[1], null, drawables[3]);

				} else {
					edt.setTag(true);
					edt.setCompoundDrawablesWithIntrinsicBounds(drawables[0],
							drawables[1], edt.getContext().getResources()
									.getDrawable(imgRes), drawables[3]);

				}
			}
		});

	}

	/** 获取视图的宽度 */
	public static int getViewWidth(View view) {
		int measure = View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED);
		view.measure(measure, measure);
		return view.getMeasuredWidth();
	}

	/** 获取视图的高度 */
	public static int getViewHeight(View view) {
		int measure = View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED);
		view.measure(measure, measure);
		return view.getMeasuredHeight();
	}

	/** dip to px */
	public static int Dip2Px(Context c, int dipValue) {
		float scale = c.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5F);
	}

	/** dip to px */
	public static float Dip2Px(Context c, float dipValue) {
		float scale = c.getResources().getDisplayMetrics().density;
		return dipValue * scale + 0.5F;
	}
}
