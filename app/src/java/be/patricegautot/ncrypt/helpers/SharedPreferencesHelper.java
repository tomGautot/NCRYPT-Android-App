package be.patricegautot.ncrypt.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import be.patricegautot.ncrypt.R;

public class SharedPreferencesHelper {

    private static String SP_FILE_KEY = "NCRYPT_SP_FILEKEY";

    public static SharedPreferences.Editor getEditorInstance(Context context){
        SharedPreferences sp = getSPInstance(context);
        return sp.edit();
    }

    public static SharedPreferences getSPInstance(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences;
    }

    public static String getSavedOrder(Context context){
        SharedPreferences sharedPreferences = getSPInstance(context);
        return sharedPreferences.getString(context.getString(R.string.settings_order_by_key), context.getString(R.string.settings_order_by_new_to_old));
    }
    public static void setSavedOrder(Context context, String choice){
        SharedPreferences.Editor editor = getEditorInstance(context);
        editor.putString(context.getString(R.string.settings_order_by_key), choice);
        editor.apply();
    }

    public static boolean getQuickDeleteStatus(Context context){
        SharedPreferences sharedPreferences = getSPInstance(context);
        return sharedPreferences.getBoolean(context.getString(R.string.settings_quick_delete_key), false);
    }

    public static void setQuickDeleteStatus(Context context, boolean choice){
        SharedPreferences.Editor editor = getEditorInstance(context);
        editor.putBoolean(context.getString(R.string.settings_quick_delete_key), choice);
        editor.apply();
    }
}
