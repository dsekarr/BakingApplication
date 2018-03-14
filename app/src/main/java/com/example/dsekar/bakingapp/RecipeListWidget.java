package com.example.dsekar.bakingapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.RemoteViews;

import com.example.dsekar.bakingapp.Constants.AppConstants;
import com.example.dsekar.bakingapp.Data.Recipe;
import com.example.dsekar.bakingapp.Widget.RecipeViewService;
import com.example.dsekar.bakingapp.Widget.RecipeWidget;

import java.util.List;

/**
 * Implementation of App Widget functionality.
 */
public class RecipeListWidget extends AppWidgetProvider {

    private static final String LEFT_CLICKED = "widgetLeftClicked";
    private static final String RIGHT_CLICKED = "widgetRightClicked";

    void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                         int appWidgetId) {
        RemoteViews remoteViews = setRemoteViews(context);
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    public PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(App.getContext(), RecipeListWidget.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list_view);
        onUpdateRecipe(context, appWidgetManager, appWidgetIds);
    }

    public void onUpdateRecipe(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        SharedPreferences pref = App.getContext().getApplicationContext()
                .getSharedPreferences(AppConstants.PREFERENCE_RECIPES_ID, 0); // 0 - for private mode
        int value = pref.getInt(AppConstants.RECIPE_FOR_WIDGET, 1);

        if (LEFT_CLICKED.equals(intent.getAction())) {
            if (value > 1) {
                pref.edit().putInt(AppConstants.RECIPE_FOR_WIDGET, value - 1).apply();
                UpdateNewRecipe(context);
            }
        }

        if (RIGHT_CLICKED.equals(intent.getAction())) {
            if (value < 4) {
                pref.edit().putInt(AppConstants.RECIPE_FOR_WIDGET, value + 1).apply();
                UpdateNewRecipe(context);
            }
        }
    }

    public void UpdateNewRecipe(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(App.getContext(), RecipeListWidget.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list_view);
        onUpdateRecipe(context, appWidgetManager, appWidgetIds);
    }

    public RemoteViews setRemoteViews(Context context) {
        RemoteViews remoteViews;
        remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_listview);
        List<Recipe> recipes = RecipeWidget.getRecipeList();

        if (recipes != null && recipes.size() > 0) {
            Recipe recipe = recipes.get(RecipeWidget.getRecipeId() - 1);

            remoteViews.setTextViewText(R.id.widget_title, recipe.getName());
            Intent intent = new Intent(context, RecipeViewService.class);
            intent.setAction(AppConstants.ACTION_ALL_RECIPE);

            remoteViews.setOnClickPendingIntent(R.id.widget_left, getPendingSelfIntent(context, LEFT_CLICKED));

            remoteViews.setOnClickPendingIntent(R.id.widget_right, getPendingSelfIntent(context, RIGHT_CLICKED));

            Intent detailViewIntent = new Intent(context, RecipeDetailActivity.class);
            detailViewIntent.putExtra(AppConstants.RECIPE, recipe);
            detailViewIntent.setAction(AppConstants.ACTION_ALL_RECIPE);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, detailViewIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            remoteViews.setOnClickPendingIntent(R.id.widget_title, pendingIntent);

            remoteViews.setRemoteAdapter(R.id.widget_list_view, intent);
        } else {
            remoteViews.setTextViewText(R.id.widget_title,
                    App.getContext().getResources().getString(R.string.app_name));

            remoteViews.setViewVisibility(R.id.widget_left, View.GONE);
            remoteViews.setViewVisibility(R.id.widget_right, View.GONE);
        }
        return remoteViews;
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

