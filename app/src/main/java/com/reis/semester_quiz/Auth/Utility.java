package com.reis.semester_quiz.Auth;

/**
 * Created by reis on 3/13/17.
 */

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.MODE_PRIVATE;

/**
 * Class which has Utility methods
 *
 */
public class Utility {
    private static Pattern pattern;
    private static Matcher matcher;
    private static Context context;

    //Email Pattern
    private static final String EMAIL_PATTERN =
//            "^[_A-Za-z0-9-\+]+(\.[_A-Za-z0-9-]+)*@"
//                    + "[A-Za-z0-9-]+(\.[A-Za-z0-9]+)*(\.[A-Za-z]{2,})$";
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@" +  "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
            "(" +  "\\." +  "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+";

    /**
     * Validate Email with regular expression
     *
     * @param email
     * @return true for Valid Email and false for Invalid Email
     */
    public static boolean validate(String email) {
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();

    }
    /**
     * Checks for Null String object
     *
     * @param txt
     * @return true for not null and false for null String object
     */
    public static boolean isNotNull(String txt){
        return txt!=null && txt.trim().length()>0 ? true: false;
    }

    public static String API_URL() {
        String domain = "http://52.220.127.134/api/";
        String local = "http://10.0.2.2:8000/api/";
        return domain;
    }

    public static String getToken() {
        // get token from shared preferences
        SharedPreferences preferences = context.getSharedPreferences("semester_quiz", MODE_PRIVATE);
        return preferences.getString("_token", null);
    }

    public static void setToken(Context ctx, JSONObject tokenObject) throws JSONException {
        // save context to Utility class
        context = ctx;

        // save token to context
        SharedPreferences.Editor preferences_editor= context.getSharedPreferences("semester_quiz", MODE_PRIVATE).edit();
        preferences_editor.putString("_token", tokenObject.getString("token"));
        preferences_editor.apply();
    }
}
