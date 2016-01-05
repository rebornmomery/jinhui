package com.android.shortvideo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class RegisterActivity extends Activity implements OnClickListener {

	public static final int REGISTER_CODE = 0;
	public static final int SUCCESS_CODE = 1;
	public static final int FAIL_CODE = 2;

	// 用户头像
	private ImageView userPhoto;
	// 返回
	private Button returnBtn;
	// 快速注册
	private Button regiterBtn;
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
		setContentView(R.layout.activity_regist);
		initViews();
		initViewsListener();
	}

	public void initViews() {
		// TODO Auto-generated method stub
		userPhoto = (ImageView) findViewById(R.id.user_img2);
		regiterBtn = (Button) findViewById(R.id.regist_btn);
		returnBtn = (Button) findViewById(R.id.return_btn);
		nickNameEdt = (EditText) findViewById(R.id.login_name_reg);
		pwdEdt = (EditText) findViewById(R.id.login_password_reg);
	}

	public void initViewsListener() {
		// TODO Auto-generated method stub
		returnBtn.setOnClickListener(this);
		regiterBtn.setOnClickListener(this);
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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.return_btn:
			if (checkInput()) {
				Intent intent = new Intent();
				intent.putExtra("nickname", nickNameEdt.getText().toString());
				intent.putExtra("password", pwdEdt.getText().toString());
				setResult(RESULT_CANCELED);
				finish();
			}
			break;
		case R.id.regist_btn:
			setResult(RESULT_OK);
			break;
		default:
			break;
		}

	}
}