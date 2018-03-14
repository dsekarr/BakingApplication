package com.example.dsekar.bakingapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;

import com.example.dsekar.bakingapp.Constants.AppConstants;
import com.example.dsekar.bakingapp.Data.Recipe;
import com.example.dsekar.bakingapp.Widget.RecipeViewService;
import com.example.dsekar.bakingapp.Widget.RecipeWidget;

import java.util.List;

public class SingleRecipeWidget extends AppWidgetProvider{

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                         int appWidgetId) {
        RemoteViews remoteViews = setRemoteViews(context);
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        RecipeWidget.UpdateSingleRecipe();
    }

    public static void onUpdateSingleRecipe(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds){
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    public static RemoteViews setRemoteViews(Context context) {
        RemoteViews remoteViews;
        remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_listview);

        remoteViews.setViewVisibility(R.id.widget_left, View.GONE);

        remoteViews.setViewVisibility(R.id.widget_right, View.GONE);

        List<Recipe> recipes = RecipeWidget.getRecipeList();
        assert recipes != null;
        Recipe recipe = recipes.get(RecipeWidget.getSingleRecipeId() - 1);

        String widgetName;
        if (recipe != null) {
            widgetName = recipe.getName();
        } else {
            widgetName = App.getContext().getResources().getString(R.string.app_name);
        }
        remoteViews.setTextViewText(R.id.widget_title, widgetName);
        Intent intent = new Intent(context, RecipeViewService.class);
        intent.setAction(AppConstants.ACTION_SINGLE_RECIPE);

        Intent detailViewIntent = new Intent(context, RecipeDetailActivity.class);
        detailViewIntent.putExtra(AppConstants.A_RECIPE, recipe);
        detailViewIntent.setAction(AppConstants.ACTION_SINGLE_RECIPE);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, detailViewIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        remoteViews.setOnClickPendingIntent(R.id.widget_title, pendingIntent);

        remoteViews.setRemoteAdapter(R.id.widget_list_view, intent);
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
