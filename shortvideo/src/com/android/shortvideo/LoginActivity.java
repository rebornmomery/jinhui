package com.android.shortvideo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.shortvideo.UserLoginHttp.Data;
import com.android.shortvideo.common.http.ResponseListener;
import com.android.shortvideo.common.http.AbstractHttpClient.ResponseResult;
import com.android.shortvideo.common.utils.LoginHelper;

/**
 * @author YeGuangRong
 * 
 *         用户登录
 * 
 */
public class LoginActivity extends BaseActivity implements OnClickListener {

	//用户头像
	private ImageView userPhoto;
	// 退出
	private ImageButton exitImg;
	// 快速注册
	private Button regiterBtn;
	// 登录
	private Button loginBtn;
	// 用户名
	private EditText nickNameEdt;
	// 密码
	private EditText pwdEdt;

	String nickName;
	String pwd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// 程序在后台被通知拉起的情况：这时候登录已经成功，则直接跳转到主页，让主页来处理通知数据
		if (LoginHelper.getInstance().isLogined()) {
			doOnLogined();
			return;
		}
		if(com.android.shortvideo.common.utils.LoginHelper.getInstance().hasDoneReloadUserState() == false){
			if(LoginHelper.getInstance().reloadUserState(true)){
				doOnLogined();
				return;
			}
		}
		setContentView(R.layout.activity_login);
		initViews();
		initViewsListener();
		initData();
	}
	
	
	public void initViews() {
		// TODO Auto-generated method stub
		userPhoto = (ImageView) findViewById(R.id.user_img);
//		exitImg = (ImageButton) findViewById(R.id.cancel_btn);
		regiterBtn = (Button) findViewById(R.id.register_btn);
		loginBtn = (Button) findViewById(R.id.login_btn);
		nickNameEdt = (EditText) findViewById(R.id.login_account_edt);
		pwdEdt = (EditText) findViewById(R.id.login_password_edt);
	}

	
	public void initViewsListener() {
		// TODO Auto-generated method stub
		exitImg.setOnClickListener(this);
		regiterBtn.setOnClickListener(this);
		loginBtn.setOnClickListener(this);
	}

	public void initData() {
		// TODO Auto-generated method stub
		if ((nickName = UserDataPrefenceUtil.getUserValue("nickname")) != null
				&& (pwd = UserDataPrefenceUtil.getUserValue("password")) != null) {
			nickNameEdt.setText(nickName);
			pwdEdt.setText(pwd);
		}
	}

	/**
	 * 登录成功的操作
	 */
	private void doOnLogined() {
		// 跳转到首页
		startActivity(new Intent(this, MainActivity.class));
		finish();

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.cancel_btn:
			finish();
			break;
		case R.id.register_btn:
			startActivityForResult(
					new Intent(this, RegisterActivity.class),
					RegisterActivity.REGISTER_CODE);
			break;
		case R.id.login_btn:
			startLogin();
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (RegisterActivity.REGISTER_CODE == requestCode) {

			switch (resultCode) {
			case RegisterActivity.SUCCESS_CODE:
				if (data.getStringExtra("nickname") != null) {
					nickNameEdt.setText(data.getStringExtra("nickname"));
				}
				if (data.getStringExtra("password") != null) {
					pwdEdt.setText(data.getStringExtra("password"));
				}
				break;
			case RegisterActivity.FAIL_CODE:
				break;
			default:
				break;
			}

		}
	}

	/**
	 * 
	 * 验证输入
	 */
	private boolean checkInput() {
		nickName = nickNameEdt.getText().toString();
		pwd = pwdEdt.getText().toString();
		if (nickName.equals("")) {
			Toast.makeText(this, "昵称不能为空", Toast.LENGTH_LONG).show();
			return false;
		}
		if (pwd.equals("")) {
			Toast.makeText(this, "密码不能为空", Toast.LENGTH_LONG).show();
			return false;
		} else {
			return true;
		}

	}

	/**
	 * 请求登录
	 */
	private void startLogin() {
		if (checkInput()) {
			UserLoginHttp.RequestParam param = new UserLoginHttp.RequestParam();
			param.nickname = nickName;
			param.pwd = pwd;
			param.Tag = "loginAPI";
			UserLoginHttp http = new UserLoginHttp();
			http.request(param, new ResponseListener<UserLoginHttp.Data>() {
				
				@Override
				public void onSuccess(int responseCode, ResponseResult<Data> result) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onFailed(int responseCode) {
					// TODO Auto-generated method stub
					
				}
			});
			
		}
	}

}
