package com.jwong.todo.todo;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
//import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by jonathan on 6/1/2014.
 */
public class DatePickerFragment extends DialogFragment {

    private static final String DATE_INFO = "dateInfo";
    private DatePickerDialog.OnDateSetListener onDateSetListener;

    private static final String TAG = "DatePickerFragment";

    static DatePickerFragment newInstance(Date date, DatePickerDialog.OnDateSetListener onDateSetListener) {
        DatePickerFragment pickerFragment = new DatePickerFragment();
        pickerFragment.setOnDateSetListener(onDateSetListener);

       // cancelListener = onCancelListener;
        //pickerFragment.setOnDismissListener();
        //pickerFragment.setCancelable(true);


        //Pass the date in a bundle.
        Bundle bundle = new Bundle();
        bundle.putSerializable(DATE_INFO, date);
        pickerFragment.setArguments(bundle);
        return pickerFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

//        DatePickerDialog.Builder builder = new DatePickerDialog(getActivity())
        Date initialDate = (Date) getArguments().getSerializable(DATE_INFO);
        int[] yearMonthDay = ymdTripleFor(initialDate);
        DatePickerDialog dialog = new DatePickerDialog(getActivity(), onDateSetListener, yearMonthDay[0], yearMonthDay[1],
                yearMonthDay[2]);

       dialog.setCancelable(true);
        //getDialog().setCanceledOnTouchOutside(true);
        //dialog.setOnCancelListener(cancelListener);


        return dialog;
    }
/*
    @Override
    public void onCancel(DialogInterface dialog) {
        onDateSetListener.clearFired();
        Log.v(TAG, "CANCEL");
    }
*/
    private void setOnDateSetListener(DatePickerDialog.OnDateSetListener listener) {
        this.onDateSetListener = listener;
    }

   // private void setCancelListener

    private int[] ymdTripleFor(Date date) {
        Calendar cal = Calendar.getInstance(Locale.US);
        cal.setTime(date);
        return new int[]{cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)};
    }


}
