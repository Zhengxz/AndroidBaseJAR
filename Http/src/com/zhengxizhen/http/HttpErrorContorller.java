package com.zhengxizhen.http;

import com.zhengxizhen.tool.MachineUtil;

import android.app.Activity;
import android.content.DialogInterface;
import android.widget.Toast;

/**
 * @Title ComveeHttpErrorControl
 * @package com.zhengxizhen.http
 * @Description 网络请求错误控制器
 * @author 郑锡真
 * @date 2015-02-04
 * @version 1.0.0
 * */
public class HttpErrorContorller {

	public static final void parseError(final Activity cxt, int errorCode) {
		try {
			switch (errorCode) {
			case 0:
				break;
			case HttpRequest.ERRCODE_NETWORK:
				try {
					CustomDialog.Builder builder = new CustomDialog.Builder(cxt);
					builder.setTitle("提示");
					builder.setMessage("未检测到可用的网络连接，请开启网络连接");
					builder.setPositiveButton("设置",
							new CustomDialog.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									MachineUtil.goToSetNetwork(cxt);
								}
							});
					builder.create().show();
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			default:
				Toast.makeText(cxt, R.string.time_out, Toast.LENGTH_SHORT)
						.show();
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
