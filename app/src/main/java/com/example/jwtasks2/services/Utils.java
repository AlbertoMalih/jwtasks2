package com.example.jwtasks2.services;

import android.content.Context;

import com.example.jwtasks2.ItemListActivityMain;
import com.example.jwtasks2.model.NoteDTO;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.jwtasks2.ItemListActivityMain.ANOTHER_DATA_LIST;

/**
 * Created by Олег on 16.10.2017.
 */

public class Utils {
    private static DateFormat startDateFormatter = new SimpleDateFormat("yyyy:MM:dd");
    private static DateFormat endDateFormatter = new SimpleDateFormat("HH:mm");

    public static CharSequence getStringResourceByName(Context c, String aString) {
        int resId = c.getResources().getIdentifier(aString, "string", c.getPackageName());
        if (resId < 1) {
            return aString;
        }
        return c.getString(resId);
    }

    public static String getGroupNameForWorkWithDb(String groupName) {
        String[] defaultTypes = ItemListActivityMain.getDefaultTypes();
        String[] defaultTypesDefLang = ItemListActivityMain.getDefaultTypesDefLang();
        String result = groupName;
        for (int i = 0; i < defaultTypes.length; i++) {
            if (defaultTypes[i].equals(groupName)) {
                result = defaultTypesDefLang[i];
                break;
            }
        }
        return result;
    }

//    public static int containsGroupInDefaultGroups(String groupName, String[] defaultTypes) {
//        int result = -1;
//        for (int i = 0; i < defaultTypes.length; i++) {
//            if (defaultTypes[i].equals(groupName)) {
//                result = i;
//                break;
//            }
//        }
//        return result;
//    }

    public static int getListIdOfTypeNote(NoteDTO note) {
        int position = ItemListActivityMain.ANOTHER_DATA_LIST;
        String[] defaultTypes = ItemListActivityMain.getDefaultTypes();
        String currentType = note.getType();
        for (int i = 0; i < defaultTypes.length; i++) {
            if (defaultTypes[i].equals(currentType)) {
                position = i;
                break;
            }
        }
        return position;
    }

    public static String getStringFromDateStart(Date date) {
        return startDateFormatter.format(date);
    }

    public static String getStringFromDateAll(Date date) {
        return new StringBuilder().append(getStringFromDateStart(date)).append(":::").append(getStringFromDateEnd(date)).toString();
    }

    public static String getStringFromDateEnd(Date date) {
        return endDateFormatter.format(date);
    }
}
