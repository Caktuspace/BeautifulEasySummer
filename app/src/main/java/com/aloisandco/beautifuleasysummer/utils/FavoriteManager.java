package com.aloisandco.beautifuleasysummer.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by quentinmetzler on 30/03/15.
 */
public class FavoriteManager {

    public static void addArticleToFavorite(int articleId, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        try {
            JSONArray jsonArray = new JSONArray(prefs.getString(Constants.SHARED_PREFERENCES_FAVORITE_ARTICLES_LIST, "[]"));
            if (!isArticleFavorite(articleId, context)) {
                jsonArray.put("" + articleId);
            }
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(Constants.SHARED_PREFERENCES_FAVORITE_ARTICLES_LIST, jsonArray.toString());
            Log.d("ArticleAddFavorite", jsonArray.toString());
            editor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void addArticleToFavoriteAtPosition(int articleId, int position, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        try {
            JSONArray jsonArray = new JSONArray(prefs.getString(Constants.SHARED_PREFERENCES_FAVORITE_ARTICLES_LIST, "[]"));
            if (isArticleFavorite(articleId, context)) {
                return;
            }
            JSONArray list = new JSONArray();

            for (int i=0; i < jsonArray.length(); i++) {
                //Excluding the item at position
                if (i == position) {
                    list.put("" + articleId);
                }
                list.put(jsonArray.get(i));
            }
            if (jsonArray.length() <= position) {
                list.put("" + articleId);
            }

            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(Constants.SHARED_PREFERENCES_FAVORITE_ARTICLES_LIST, list.toString());
            Log.d("ArticleAddFavorite", list.toString());
            editor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static boolean isArticleFavorite(int articleId, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        try {
            JSONArray jsonArray = new JSONArray(prefs.getString(Constants.SHARED_PREFERENCES_FAVORITE_ARTICLES_LIST, "[]"));
            return jsonArray.toString().contains("\"" + articleId + "\"");
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static int getArticleAtPosition(int position, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        try {
            JSONArray jsonArray = new JSONArray(prefs.getString(Constants.SHARED_PREFERENCES_FAVORITE_ARTICLES_LIST, "[]"));
            String jsonValue = jsonArray.getString(position);
            jsonValue = jsonValue.replaceAll("\"", "");

            return Integer.parseInt(jsonValue);
        } catch (JSONException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static int getNumberOfFavoriteArticle(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        try {
            JSONArray jsonArray = new JSONArray(prefs.getString(Constants.SHARED_PREFERENCES_FAVORITE_ARTICLES_LIST, "[]"));
            return jsonArray.length();
        } catch (JSONException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static int getPositionOfArticle(int articleId, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        try {
            JSONArray jsonArray = new JSONArray(prefs.getString(Constants.SHARED_PREFERENCES_FAVORITE_ARTICLES_LIST, "[]"));

            for (int i=0; i < jsonArray.length(); i++) {
                //Excluding the item at position
                if (jsonArray.getString(i).equals("" + articleId)) {
                    return i;
                }
            }
            return -1;
        } catch (JSONException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static void removeArticleFromFavorite(int articleId, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        try {
            JSONArray jsonArray = new JSONArray(prefs.getString(Constants.SHARED_PREFERENCES_FAVORITE_ARTICLES_LIST, "[]"));
            JSONArray list = new JSONArray();

            for (int i=0; i < jsonArray.length(); i++) {
                //Excluding the item at position
                if (!jsonArray.getString(i).equals("" + articleId)) {
                    list.put(jsonArray.get(i));
                }
            }

            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(Constants.SHARED_PREFERENCES_FAVORITE_ARTICLES_LIST, list.toString());
            Log.d("ArticleDeleteFavorite", list.toString());
            editor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
