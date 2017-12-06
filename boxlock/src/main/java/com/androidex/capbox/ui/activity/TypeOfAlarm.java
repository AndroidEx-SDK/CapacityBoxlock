package com.androidex.capbox.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.androidex.capbox.MyApplication;
import com.androidex.capbox.R;
import com.androidex.capbox.base.BaseActivity;
import com.androidex.capbox.data.entity.AlarmInfo;

/**
 * 报警方式
 * @author Administrator
 *
 */
public class TypeOfAlarm extends BaseActivity implements OnClickListener{
	//返回，确认，防丢button，铃声button1，发烧button，铃声button2，踢被子button，铃声button3
	private Button back, ok, lost,  fever;
	private ImageView bells1,bells2;
	//铃声1，铃声2，铃声3
	//	private TextView ringtone1, ringtone2, ringtone3;
	//用于选择铃声后作相应的判断标记
	private static final int REQUEST_CODE_PICK_RINGTONE = 1;
	//保存铃声的Uri的字符串形式
	private String mRingtoneUri = null;
	//用于按钮点击判断标记
	private int type;

	private AlarmInfo alarm;

	//音乐选择
	private int isLost;
	private String lostType = "";
	private String mLostRingtoneName, mLostRingtoneUri;
	private int isFever;
	private String feverType = "";
	private String mFeverRingtoneName, mFeverRingtoneUri;
	private int isKickAQuilt;
	private String kickAQuiltType = "";
	private String mKickAQuiltRingtoneName, mKickAQuiltRingtoneUri;
	//提醒方式
	//用于按钮点击判断标记
	private int callType;
	private String[] areas;
	private RadioOnClick OnClick = new RadioOnClick(3);//不选中(>2    areas.length)

	@Override
	public void initData(Bundle savedInstanceState) {
		initView();
		//initData();
		areas = getResources().getStringArray(R.array.areas);
		alarm = (AlarmInfo)getIntent().getSerializableExtra("data");
	}

	@Override
	public void setListener() {

	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		back = (Button) findViewById(R.id.typeofalarm_btn_back);
		back.setOnClickListener(this);
		ok = (Button) findViewById(R.id.typeofalarm_btn_ok);
		ok.setOnClickListener(this);
		lost = (Button) findViewById(R.id.typeofalarm_btn_lost);
		lost.setTypeface(MyApplication.getInstance().getTypeface());
		lost.setOnClickListener(this);
		bells1 = (ImageView) findViewById(R.id.typeofalarm_btn_bells1);
		bells1.setOnClickListener(this);
		fever = (Button) findViewById(R.id.typeofalarm_btn_fever);
		fever.setTypeface(MyApplication.getInstance().getTypeface());
		fever.setOnClickListener(this);
		bells2 = (ImageView) findViewById(R.id.typeofalarm_btn_bells2);
		bells2.setOnClickListener(this);

	}

	/**
	 * 加载数据
	 */
	private void initData() {

		if(alarm.getIsLost() == 0){
			lost.setText(areas[0]);
		} else if(alarm.getIsLost() == 1){
			lost.setText(areas[1]);
		} else if(alarm.getIsLost() == 2){
			lost.setText(areas[2]);
		}
//		ringtone1.setText(alarm.getmLostRingtoneName());

		if(alarm.getIsFever() == 0){
			fever.setText(areas[0]);
		} else if(alarm.getIsFever() == 1){
			fever.setText(areas[1]);
		} else if(alarm.getIsFever() == 2){
			fever.setText(areas[2]);
		}
//
//		ringtone3.setText(alarm.getmKickAQuiltRingtoneName());

	}

	/**
	 * 点击事件
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.typeofalarm_btn_back:
				finish();
				break;
			case R.id.typeofalarm_btn_ok:
				alarm.setId(alarm.getId());
				//防丢报警保存
				if(!lostType.equals("")){
					if(lostType.equals(areas[0])){
						isLost = 0;
						alarm.setIsLost(isLost);
					} else if(lostType.equals(areas[1])){
						isLost = 1;
						alarm.setIsLost(isLost);
					} else if(lostType.equals(areas[2])){
						isLost = 2;
						alarm.setIsLost(isLost);
					}
				}
				if(mLostRingtoneName != null){
					alarm.setmLostRingtoneName(mLostRingtoneName);
				}
				if(mLostRingtoneUri != null){
					alarm.setmLostRingtoneUri(mLostRingtoneUri);
				}
				//发烧报警保存
				if(!"".equals(feverType)){
					if(feverType.equals(areas[0])){
						isFever = 0;
						alarm.setIsFever(isFever);
					} else if(feverType.equals(areas[1])){
						isFever = 1;
						alarm.setIsFever(isFever);
					} else if(feverType.equals(areas[2])){
						isFever = 2;
						alarm.setIsFever(isFever);
					}
				}
				if(mFeverRingtoneName != null){
					alarm.setmFeverRingtoneName(mFeverRingtoneName);
				}
				if(mFeverRingtoneUri != null){
					alarm.setmFeverRingtoneUri(mFeverRingtoneUri);
				}
				//踢被子报警保存
				if(!"".equals(kickAQuiltType)){
					if(kickAQuiltType.equals(areas[0])){
						isKickAQuilt = 0;
						alarm.setIsKickAQuilt(isKickAQuilt);
					} else if(kickAQuiltType.equals(areas[1])){
						isKickAQuilt = 1;
						alarm.setIsKickAQuilt(isKickAQuilt);
					} else if(kickAQuiltType.equals(areas[2])){
						isKickAQuilt = 2;
						alarm.setIsKickAQuilt(isKickAQuilt);
					}
				}
				if(mKickAQuiltRingtoneName != null){
					alarm.setmKickAQuiltRingtoneName(mKickAQuiltRingtoneName);
				}
				if(mKickAQuiltRingtoneUri != null){
					alarm.setmKickAQuiltRingtoneUri(mKickAQuiltRingtoneUri);
				}

				finish();
				break;
			case R.id.typeofalarm_btn_lost:
				callType = 0;
//			AlertDialog ad0 =new AlertDialog.Builder(this).setTitle("提醒方式")
				AlertDialog ad0 =new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.typeofalarm_type))
						.setSingleChoiceItems(areas,OnClick.getIndex(),OnClick).create();
//		    areaListView=ad0.getListView();
				ad0.show();
				break;
			case R.id.typeofalarm_btn_bells1:
				type = 0;
				doPickRingtone();
				break;
			case R.id.typeofalarm_btn_fever:
				callType = 1;
//			AlertDialog ad1 =new AlertDialog.Builder(this).setTitle("提醒方式")
				AlertDialog ad1 =new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.typeofalarm_type))
						.setSingleChoiceItems(areas,OnClick.getIndex(),OnClick).create();
//			areaListView=ad1.getListView();
				ad1.show();
				break;
			case R.id.typeofalarm_btn_bells2:
				type = 1;
				doPickRingtone();
				break;
			default:
				break;
		}

	}

	/**
	 * 获取系统音乐
	 */
	private void doPickRingtone() {
		Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
		// Allow user to pick 'Default'
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
		// Show only ringtones
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE,
				RingtoneManager.TYPE_RINGTONE);
		// Don't show 'Silent'
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);

		Uri ringtoneUri;
		if (mRingtoneUri != null) {
			ringtoneUri = Uri.parse(mRingtoneUri);
			Log.i("TypeOfAlam", "startPlayMediaPlayer ringtoneUri1:	"+ringtoneUri  );
		} else {
			// Otherwise pick default ringtone Uri so that something is
			// selected.
			ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
			Log.i("TypeOfAlam", "startPlayMediaPlayer ringtoneUri2:	"+ringtoneUri  );
		}

		// Put checkmark next to the current ringtone for this contact
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, ringtoneUri);

		// Launch!
		// startActivityForResult(intent, REQUEST_CODE_PICK_RINGTONE);
		startActivityForResult(intent, REQUEST_CODE_PICK_RINGTONE);
	}

	/**
	 * 返回音乐信息
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK) {
			return;
		}

		switch (requestCode) {
			case REQUEST_CODE_PICK_RINGTONE: {
				Uri pickedUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
				handleRingtonePicked(pickedUri);
				break;
			}
		}
	}

	private void handleRingtonePicked(Uri pickedUri) {
		if (pickedUri == null || RingtoneManager.isDefault(pickedUri)) {
			mRingtoneUri = null;
		} else {
			mRingtoneUri = pickedUri.toString();
		}
		// get ringtone name and you can save mRingtoneUri for database.
		switch (type) {
			case 0:
				if (mRingtoneUri != null) {
					mLostRingtoneName = RingtoneManager.getRingtone(this, pickedUri).getTitle(this);
					mLostRingtoneUri = mRingtoneUri;
//				ringtone1.setText(mLostRingtoneName);
				} else {
//				ringtone1.setText("");
				}
				break;
			case 1:
				if (mRingtoneUri != null) {
					mFeverRingtoneName = RingtoneManager.getRingtone(this, pickedUri).getTitle(this);
					mFeverRingtoneUri = mRingtoneUri;
//				ringtone2.setText(mFeverRingtoneName);
				} else {
//				ringtone2.setText("");
				}
				break;
			case 2:
				if (mRingtoneUri != null) {
					mKickAQuiltRingtoneName = RingtoneManager.getRingtone(this, pickedUri).getTitle(this);
					mKickAQuiltRingtoneUri = mRingtoneUri;
//				ringtone3.setText(mKickAQuiltRingtoneName);
				} else {
//				ringtone3.setText("");
				}
				break;

			default:
				break;
		}

//		 ContentValues values = new ContentValues();
//		 values.put(Contacts.CUSTOM_RINGTONE, mRingtoneUri);
//		 //mContactId mean contacts id
//		 getContentResolver().update(Contacts.CONTENT_URI, values,
//		 Contacts._ID + " = " + mContactId, null);
	}

	/**
	 *  提醒方式Radio选择
	 * @author Administrator
	 *
	 */
	class RadioOnClick implements DialogInterface.OnClickListener {
		private int index;

		public RadioOnClick(int index) {
			this.index = index;
		}

		public void setIndex(int index) {
			this.index = index;
		}

		public int getIndex() {
			return index;
		}

		public void onClick(DialogInterface dialog, int whichButton) {
			setIndex(whichButton);
//			Toast.makeText(TypeOfAlarm.this, "您已经选择了 " + ":" + areas[index],Toast.LENGTH_LONG).show();
			switch (callType) {
				case 0:
					lostType = areas[index];
					lost.setText(lostType);
					break;
				case 1:
					feverType = areas[index];
					fever.setText(feverType);
					break;

				default:
					break;
			}
			dialog.dismiss();
		}
	}

	@Override
	public int getLayoutId() {
		return R.layout.activity_typeofalarm;
	}

}
