package com.example.myapplication3;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.ArrayList;
import java.util.Date;

class GraphAxisValueFormatter implements IAxisValueFormatter {

    String[] date = ((MyRevDetail)MyRevDetail.context_rev).dateArr;
    @Override
    public String getFormattedValue(float value, AxisBase axis) {

        String dateStr = date[(int)value];
        return dateStr;
    }
}
