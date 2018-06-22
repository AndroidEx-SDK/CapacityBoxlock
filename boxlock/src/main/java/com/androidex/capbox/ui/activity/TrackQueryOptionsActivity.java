package com.androidex.capbox.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.androidex.capbox.MyApplication;
import com.androidex.capbox.R;
import com.androidex.capbox.base.BaseActivity;
import com.androidex.capbox.data.cache.SharedPreTool;
import com.androidex.capbox.utils.Constants;
import com.androidex.capbox.map.CommonUtil;
import com.androidex.capbox.map.dialog.DateDialog;
import com.androidex.capbox.utils.RLog;
import com.baidu.trace.api.track.SupplementMode;
import com.baidu.trace.model.CoordType;
import com.baidu.trace.model.SortType;
import com.baidu.trace.model.TransportMode;

import java.text.SimpleDateFormat;

import static com.androidex.capbox.utils.Constants.CACHE.CACHE_TRACK_QUERY_TIME;

public class TrackQueryOptionsActivity extends BaseActivity
        implements CompoundButton.OnCheckedChangeListener {

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
    private MyApplication trackApp = null;
    private Intent result = null;
    private DateDialog startDateDialog = null;
    private DateDialog endDateDialog = null;
    private Button startTimeBtn = null;
    private Button endTimeBtn = null;
    private CheckBox processedCBx = null;
    private CheckBox denoiseCBx = null;
    private CheckBox vacuateCBx = null;
    private CheckBox mapmatchCBx = null;
    private TextView radiusText = null;
    private DateDialog.Callback startTimeCallback = null;
    private DateDialog.Callback endTimeCallback = null;
    private long startTime = CommonUtil.getCurrentTime() - 24 * 60 * 60;
    private long endTime = CommonUtil.getCurrentTime();
    private boolean isProcessed = true;
    private boolean isDenoise = true;
    private boolean isVacuate = false;
    private boolean isMapmatch = true;
    private long lowDay;

    @Override
    public void initData(Bundle savedInstanceState) {
        init();
        trackApp = (MyApplication) getApplication();
    }

    @Override
    public void setListener() {

    }

    private void init() {
        result = new Intent();
        startTimeBtn = (Button) findViewById(R.id.start_time);
        endTimeBtn = (Button) findViewById(R.id.end_time);
        processedCBx = (CheckBox) findViewById(R.id.processed);
        denoiseCBx = (CheckBox) findViewById(R.id.denoise);
        vacuateCBx = (CheckBox) findViewById(R.id.vacuate);
        mapmatchCBx = (CheckBox) findViewById(R.id.mapmatch);
        radiusText = (TextView) findViewById(R.id.radius_threshold);

        StringBuilder startTimeBuilder = new StringBuilder();
        startTimeBuilder.append(getResources().getString(R.string.start_time));
        long mTime = SharedPreTool.getInstance(context).getLongData(CACHE_TRACK_QUERY_TIME, 0) * 1000;
        RLog.d("startTime--> time = " + mTime);
        lowDay = System.currentTimeMillis() - 24 * 60 * 60 * 1000;
        if (mTime < System.currentTimeMillis() && mTime > lowDay) {
            startTimeBuilder.append(simpleDateFormat.format(mTime));
        } else {
            startTimeBuilder.append(simpleDateFormat.format(lowDay));
        }
        startTimeBtn.setText(startTimeBuilder.toString());

        StringBuilder endTimeBuilder = new StringBuilder();
        endTimeBuilder.append(getResources().getString(R.string.end_time));
        endTimeBuilder.append(simpleDateFormat.format(System.currentTimeMillis()));
        endTimeBtn.setText(endTimeBuilder.toString());

        processedCBx.setOnCheckedChangeListener(this);
        denoiseCBx.setOnCheckedChangeListener(this);
        vacuateCBx.setOnCheckedChangeListener(this);
        mapmatchCBx.setOnCheckedChangeListener(this);

    }

    public void onStartTime(View v) {
        if (null == startTimeCallback) {
            startTimeCallback = new DateDialog.Callback() {
                @Override
                public void onDateCallback(long timeStamp) {
                    TrackQueryOptionsActivity.this.startTime = timeStamp;
                    SharedPreTool.getInstance(context).setLongData(CACHE_TRACK_QUERY_TIME, timeStamp);
                    StringBuilder startTimeBuilder = new StringBuilder();
                    startTimeBuilder.append(getResources().getString(R.string.start_time));
                    startTimeBuilder.append(simpleDateFormat.format(timeStamp * 1000));
                    startTimeBtn.setText(startTimeBuilder.toString());
                }
            };
        }
        if (null == startDateDialog) {
            long mTime = SharedPreTool.getInstance(context).getLongData(CACHE_TRACK_QUERY_TIME, 0) * 1000;
            if (mTime < System.currentTimeMillis() && mTime > lowDay) {
                RLog.d("startTime timeStamp -->111111  " + mTime);
                startDateDialog = new DateDialog(this, startTimeCallback, mTime, false);
            } else {
                RLog.d("startTime timeStamp --> 22222222" + lowDay);
                startDateDialog = new DateDialog(this, startTimeCallback, lowDay, false);
            }
        } else {
            startDateDialog.setCallback(startTimeCallback);
        }
        startDateDialog.show();
    }

    public void onEndTime(View v) {
        if (null == endTimeCallback) {
            endTimeCallback = new DateDialog.Callback() {
                @Override
                public void onDateCallback(long timeStamp) {
                    TrackQueryOptionsActivity.this.endTime = timeStamp;
                    StringBuilder endTimeBuilder = new StringBuilder();
                    endTimeBuilder.append(getResources().getString(R.string.end_time));
                    endTimeBuilder.append(simpleDateFormat.format(timeStamp * 1000));
                    endTimeBtn.setText(endTimeBuilder.toString());
                }
            };
        }
        if (null == endDateDialog) {
            endDateDialog = new DateDialog(this, endTimeCallback, endTime*1000,true);
        } else {
            endDateDialog.setCallback(endTimeCallback);
        }
        endDateDialog.show();
    }

    public void onCancel(View v) {
        super.onBackPressed();
    }

    public void onFinish(View v) {
        result.putExtra("startTime", startTime);
        result.putExtra("endTime", endTime);
        result.putExtra("processed", isProcessed);
        result.putExtra("denoise", isDenoise);
        result.putExtra("vacuate", isVacuate);
        result.putExtra("mapmatch", isMapmatch);

        String radiusStr = radiusText.getText().toString();
        if (!TextUtils.isEmpty(radiusStr)) {
            try {
                result.putExtra("radius", Integer.parseInt(radiusStr));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        RadioGroup transportModeGroup = (RadioGroup) findViewById(R.id.transport_mode);
        RadioButton transportModeBtn = (RadioButton) findViewById(transportModeGroup.getCheckedRadioButtonId());
        TransportMode transportMode = TransportMode.driving;
        switch (transportModeBtn.getId()) {
            case R.id.driving_mode:
                transportMode = TransportMode.driving;
                break;

            case R.id.riding_mode:
                transportMode = TransportMode.riding;
                break;

            case R.id.walking_mode:
                transportMode = TransportMode.walking;
                break;

            default:
                break;
        }
        result.putExtra("transportMode", transportMode.name());

        RadioGroup supplementModeOptionGroup = (RadioGroup) findViewById(R.id.supplement_mode);
        RadioButton supplementModeOptionRadio = (RadioButton) findViewById(supplementModeOptionGroup.getCheckedRadioButtonId());
        SupplementMode supplementMode = SupplementMode.driving;
        switch (supplementModeOptionRadio.getId()) {
            case R.id.no_supplement:
                supplementMode = SupplementMode.no_supplement;
                break;

            case R.id.driving:
                supplementMode = SupplementMode.driving;
                break;

            case R.id.riding:
                supplementMode = SupplementMode.riding;
                break;

            case R.id.walking:
                supplementMode = SupplementMode.walking;
                break;

            default:
                break;
        }
        result.putExtra("supplementMode", supplementMode.name());

        RadioGroup sortTypeGroup = (RadioGroup) findViewById(R.id.sort_type);
        RadioButton sortTypeRadio = (RadioButton) findViewById(sortTypeGroup.getCheckedRadioButtonId());
        SortType sortType = SortType.asc;
        switch (sortTypeRadio.getId()) {
            case R.id.asc:
                sortType = SortType.asc;
                break;

            case R.id.desc:
                sortType = SortType.desc;
                break;

            default:
                break;
        }
        result.putExtra("sortType", sortType.name());

        RadioGroup coordTypeOutputGroup = (RadioGroup) findViewById(R.id.coord_type_output);
        RadioButton coordTypeOutputOptionBtn =
                (RadioButton) findViewById(coordTypeOutputGroup.getCheckedRadioButtonId());
        CoordType coordTypeOutput = CoordType.bd09ll;
        switch (coordTypeOutputOptionBtn.getId()) {
            case R.id.bd09ll:
                coordTypeOutput = CoordType.bd09ll;
                break;

            case R.id.gcj02:
                coordTypeOutput = CoordType.gcj02;
                break;

            default:
                break;
        }
        result.putExtra("coordTypeOutput", coordTypeOutput.name());

        setResult(Constants.baiduMap.RESULT_CODE, result);
        super.onBackPressed();
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        switch (compoundButton.getId()) {
            case R.id.processed:
                isProcessed = isChecked;
                break;

            case R.id.denoise:
                isDenoise = isChecked;
                break;

            case R.id.vacuate:
                isVacuate = isChecked;
                break;

            case R.id.mapmatch:
                isMapmatch = isChecked;
                break;
            default:
                break;
        }

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public int getLayoutId() {
        return  R.layout.activity_trackquery_options;
    }
}
