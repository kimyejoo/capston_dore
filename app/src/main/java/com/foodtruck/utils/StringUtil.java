package com.foodtruck.utils;

import android.widget.EditText;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class StringUtil {

    public static final DecimalFormat MONEY_FORMAT_1 = new DecimalFormat("#,##0");

    public static final SimpleDateFormat DATE_FORAMT_1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSsss");
    public static final SimpleDateFormat DATE_FORAMT_2 = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat DATE_FORAMT_3 = new SimpleDateFormat("yy.MM.dd");
    public static final SimpleDateFormat DATE_FORAMT_4 = new SimpleDateFormat("a hh:mm");

    public static boolean isValidEmail(String email) {
        boolean err = false;
        String regex = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(email);
        if(m.matches()) {
            err = true;
        }
        return err;
    }

    public static boolean isNull(String str){
        if(str == null || str.trim().equals("") || str.trim().toLowerCase().equals("none")){
            return true;
        }
        return false;
    }

    public static boolean isNull(TextView view){
        if(view == null){
            return true;
        }
        return isNull(view.getText().toString());
    }

    public static boolean isNull(EditText view){
        if(view == null){
            return true;
        }

        return isNull(view.getText().toString());
    }

    public static boolean isNotNull(String str){
        return !isNull(str);
    }
    public static boolean isNotNull(TextView view){
        return !isNull(view);
    }
    public static boolean isNotNull(EditText view){
        return !isNull(view);
    }

    public static String getString(EditText view){

        if(isNull(view)){
            return null;
        }

        return view.getText().toString().trim();
    }

    public static String getString(TextView view){
        if(isNull(view)){
            return null;
        }
        return view.getText().toString().trim();
    }

    public static int[] getCharactorPosition(String fullStr, String targetStr) {
        int[] result = new int[2];
        result[0] = fullStr.indexOf(targetStr);
        result[1] = result[0] + targetStr.length();

        return result;
    }
}
