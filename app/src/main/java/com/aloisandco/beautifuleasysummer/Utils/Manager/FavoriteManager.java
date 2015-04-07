package com.aloisandco.beautifuleasysummer.Utils.Manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.aloisandco.beautifuleasysummer.Utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by quentinmetzler on 30/03/15.
 */

/**
 * Handle the favorite data model
 */
public class FavoriteManager {

    /**
     * Add an article at the end of the favorite list
     * @param articleId the article to add to the favorite
     * @param context the context where the user preferences are stored
     */
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

    /**
     * Add an article at a custom index in the favorite list
     * @param articleId the article to add to the favorite
     * @param position the position where we want this article to be added in the favorite list
     * @param context the context where the user preferences are stored
     */
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

    /**
     * Check whether the article is a favorite
     * @param articleId the article we want to know if it's a favorite
     * @param context the context where the user preferences are stored
     * @return if the article is a favorite
     */
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

    /**
     * Get the article stored at a certain position on the favorite list
     * @param position the position of the favorite article we want
     * @param context the context where the user preferences are stored
     * @return the article stored at a certain position on the favorite list
     */
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

    /**
     * Get the number of article set as favorite
     * @param context the context where the user preferences are stored
     * @return the number of article set as favorite
     */
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

    /**
     * Get the position of an article in the favorite list
     * @param articleId the article we want to know the position
     * @param context the context where the user preferences are stored
     * @return the position of an article in the favorite list or -1 if it's not a favorite
     */
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

    /**
     * Remove an article from the favorite list
     * @param articleId the article we want to remove from the favorite
     * @param context the context where the user preferences are stored
     */
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
