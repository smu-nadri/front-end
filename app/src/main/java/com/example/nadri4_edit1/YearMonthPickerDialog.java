package com.example.nadri4_edit1;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class YearMonthPickerDialog extends DialogFragment {
    private static final int MAX_YEAR = 2099;
    private static final int MIN_YEAR = 1980;

    private DatePickerDialog.OnDateSetListener listener;
    public Calendar cal = Calendar.getInstance();



    public void setListener(DatePickerDialog.OnDateSetListener listener) {
        this.listener = listener;
    }
    Button btnConfirm, btnCancel;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialog = inflater.inflate(R.layout.year_month_picker, null);


        // 다이얼로그 레이아웃배경 투명하게 만들고싶다... 사이즈 조절 하고싶다..... 대체 어케하누...
        //dialog.

        //화면 사이즈 가져오기
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);
        int width = 2;
                //(int) ((size.x) * 0.8);
        int height = 2;
                //(int) ((size.y) * 0.8);


        //다이얼로그 사이즈 변경
        //AlertDialog alertDialog = builder.create();
        //WindowManager.LayoutParams params = new WindowManager.LayoutParams(width, height);
        //WindowManager.LayoutParams params = new WindowManager.LayoutParams()
        //alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //alertDialog.getWindow().setAttributes(params);

        btnConfirm = dialog.findViewById(R.id. btn_confirm);
        btnCancel = dialog.findViewById(R.id.btn_cancel);

        final NumberPicker monthPicker = (NumberPicker) dialog.findViewById(R.id.picker_month);
        final NumberPicker yearPicker = (NumberPicker) dialog.findViewById(R.id.picker_year);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                YearMonthPickerDialog.this.getDialog().cancel();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                listener.onDateSet(null, yearPicker.getValue(), monthPicker.getValue(), 0);

                CalendarUtil.selectedDate.set(yearPicker.getValue(), monthPicker.getValue(), 0);   //데이트피커에서 설정한 날짜에 맞춰 화면 세팅
                CalendarMainActivity.setMonthView();
                YearMonthPickerDialog.this.getDialog().cancel();
            }
        });

        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);
        monthPicker.setValue(cal.get(Calendar.MONTH)+1);

        int year = cal.get(Calendar.YEAR);
        yearPicker.setMinValue(MIN_YEAR);
        yearPicker.setMaxValue(MAX_YEAR);
        yearPicker.setValue(year);

        builder.setView(dialog);

        return builder.create();
    }
}
