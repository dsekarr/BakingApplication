package com.example.dsekar.bakingapp.Widget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.SharedPreferences;

import com.example.dsekar.bakingapp.App;
import com.example.dsekar.bakingapp.Constants.AppConstants;
import com.example.dsekar.bakingapp.Data.Recipe;
import com.example.dsekar.bakingapp.R;
import com.example.dsekar.bakingapp.SingleRecipeWidget;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class RecipeWidget {
    public static List<Recipe> getRecipeList() {
        SharedPreferences pref = App.getContext().getApplicationContext()
                .getSharedPreferences(AppConstants.PREFERENCE_RECIPES, 0); // 0 - for private mode
        String getRecipe = pref.getString(AppConstants.RECIPES_FOR_WIDGET, null);
        if (getRecipe != null) {
            Type listType = new TypeToken<List<Recipe>>() {
            }.getType();
            return new Gson().fromJson(getRecipe, listType);
        }
        return null;
    }

    public static int getRecipeId(){
        SharedPreferences pref = App.getContext().getApplicationContext()
                .getSharedPreferences(AppConstants.PREFERENCE_RECIPES_ID, 0);
        return pref.getInt(AppConstants.RECIPE_FOR_WIDGET, 1);
    }

    public static int getSingleRecipeId(){
        SharedPreferences pref = App.getContext().getApplicationContext()
                .getSharedPreferences(AppConstants.PREFERENCE_SINGLE_RECIPE_ID, 0);
        return pref.getInt(AppConstants.A_RECIPE_FOR_SINGLE_WIDGET, 1);
    }

    public static void UpdateSingleRecipe(){
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(App.getContext());
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(App.getContext(), SingleRecipeWidget.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list_view);
        //Now update all widgets
        SingleRecipeWidget.onUpdateSingleRecipe(App.getContext(), appWidgetManager,appWidgetIds);
    }
}
