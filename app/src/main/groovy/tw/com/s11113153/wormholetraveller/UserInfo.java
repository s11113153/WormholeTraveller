package tw.com.s11113153.wormholetraveller;

import android.content.Context;
import android.content.SharedPreferences;

import tw.com.s11113153.wormholetraveller.db.table.User;

/**
 * Created by xuyouren on 15/4/13.
 */
public class UserInfo {
  private UserInfo() {
  }

  private static final String USER = "user";

  private static final String ID = "id";
  private static final String NAME = "name";
  private static final String ACCOUNT = "account";
  private static final String PASSWORD = "password";
  private static final String MAIL = "mail";
  private static final String ICON_PATH = "iconPath";


  public static void setUser(Context context, User user) {
    SharedPreferences sharedPref = context.getSharedPreferences(USER, Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = sharedPref.edit();
    editor.putInt(ID, user.getId());
    editor.putString(NAME, user.getName());
    editor.putString(ACCOUNT, user.getAccount());
    editor.putString(PASSWORD, user.getPassword());
    editor.putString(MAIL, user.getMail());
    editor.putString(ICON_PATH, user.getIconPath());
    editor.apply();
  }

  public static User getUser(Context context) {
    SharedPreferences sharedPref = context.getSharedPreferences(USER, Context.MODE_PRIVATE);
    User user = new User();
    user.setId(sharedPref.getInt(ID, -1))
        .setName(sharedPref.getString(NAME, ""))
        .setAccount(sharedPref.getString(ACCOUNT, ""))
        .setPassword(sharedPref.getString(PASSWORD, ""))
        .setMail(sharedPref.getString(MAIL, ""))
        .setIconPath(sharedPref.getString(ICON_PATH, ""));
    return user;
  }
}
