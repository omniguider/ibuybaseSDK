package com.omni.y5citysdk.tool;

import android.content.Context;
import android.widget.FrameLayout;
import android.widget.NumberPicker;

import com.omni.y5citysdk.R;

import java.util.Calendar;

public class TimePicker extends FrameLayout {
    private final NumberPicker mhourSpinner;
    private final NumberPicker mMinuteSpinner;
    private Calendar mDate;
    private int mHour, mMinute;
    private OnDateTimeChangedListener mOnDateTimeChangedListener;

    public TimePicker(Context context) {
        super(context);
        mDate = Calendar.getInstance();
//        mHour = mDate.get(Calendar.HOUR);
//        mMinute = mDate.get(Calendar.MINUTE);
        inflate(context, R.layout.custom_time_picker, this);

        mhourSpinner = this.findViewById(R.id.np_hour);
        mhourSpinner.setMaxValue(12);
        mhourSpinner.setMinValue(0);
        mhourSpinner.setValue(0);
        mhourSpinner.setOnValueChangedListener(mOnHourChangedListener);

        mMinuteSpinner = this.findViewById(R.id.np_minute);
        mMinuteSpinner.setMaxValue(59);
        mMinuteSpinner.setMinValue(0);
        mMinuteSpinner.setValue(0);
        mMinuteSpinner.setOnValueChangedListener(mOnMinuteChangedListener);
    }

    private NumberPicker.OnValueChangeListener mOnHourChangedListener = new NumberPicker.OnValueChangeListener() {
        @Override
        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
            mHour = mhourSpinner.getValue();
            onDateTimeChanged();
        }
    };

    private NumberPicker.OnValueChangeListener mOnMinuteChangedListener = new NumberPicker.OnValueChangeListener() {
        @Override
        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
            mMinute = mMinuteSpinner.getValue();
            onDateTimeChanged();
        }
    };

    public interface OnDateTimeChangedListener {
        void onDateTimeChanged(TimePicker view, int hour, int minute);
    }

    public void setOnDateTimeChangedListener(OnDateTimeChangedListener callback) {
        mOnDateTimeChangedListener = callback;
    }

    private void onDateTimeChanged() {
        if (mOnDateTimeChangedListener != null) {
            mOnDateTimeChangedListener.onDateTimeChanged(this, mHour, mMinute);
        }
    }
}
