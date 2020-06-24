package com.omni.y5citysdk.tool;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.omni.y5citysdk.R;

import java.util.Calendar;

public class SetTimePickerDialog extends AlertDialog implements DialogInterface.OnClickListener {
    private TimePicker mDateTimePicker;
    private Calendar mDate = Calendar.getInstance();
    private OnDateTimeSetListener mOnDateTimeSetListener;
    private int mHour, mMinute;

    @SuppressWarnings("deprecation")
    public SetTimePickerDialog(Context context) {
        super(context);
        mDateTimePicker = new TimePicker(context);
        setView(mDateTimePicker);
//        mHour = mDate.get(Calendar.HOUR);
//        mMinute = mDate.get(Calendar.MINUTE);
        mDateTimePicker.setOnDateTimeChangedListener(new TimePicker.OnDateTimeChangedListener() {
            @Override
            public void onDateTimeChanged(TimePicker view, int hour, int minute) {
                mHour = hour;
                mMinute = minute;
            }
        });

        setButton(getContext().getString(R.string.dialog_button_confirm_text), this);
        setButton2(getContext().getString(R.string.dialog_button_cancel_text), (OnClickListener) null);
    }

    public interface OnDateTimeSetListener {
        void OnDateTimeSet(AlertDialog dialog, int hour, int minute);
    }

    public void setOnDateTimeSetListener(OnDateTimeSetListener callBack) {
        mOnDateTimeSetListener = callBack;
    }

    public void onClick(DialogInterface arg0, int arg1) {
        if (mOnDateTimeSetListener != null) {
            mOnDateTimeSetListener.OnDateTimeSet(this, mHour, mMinute);
        }
    }
}
