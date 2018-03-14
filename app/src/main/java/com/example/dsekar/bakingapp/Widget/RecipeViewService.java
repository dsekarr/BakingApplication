package com.example.dsekar.bakingapp.Widget;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.dsekar.bakingapp.Constants.AppConstants;
import com.example.dsekar.bakingapp.Data.Ingredient;
import com.example.dsekar.bakingapp.Data.Recipe;
import com.example.dsekar.bakingapp.R;

import java.util.List;


public class RecipeViewService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RecipeViewServiceFactory(getApplicationContext(), intent);
    }
}

class RecipeViewServiceFactory implements RemoteViewsService.RemoteViewsFactory {

    private Recipe recipe;
    private Context mContext;
    private List<Recipe> recipes;

    private int recipeId;
    Intent mIntent;

    public RecipeViewServiceFactory(Context context, Intent intent) {
        mContext = context;
        mIntent = intent;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        recipes = RecipeWidget.getRecipeList();
        if(mIntent.getAction() != null){
            if((AppConstants.ACTION_ALL_RECIPE).equals(mIntent.getAction())){
                recipeId = RecipeWidget.getRecipeId();
            } else if((AppConstants.ACTION_SINGLE_RECIPE).equals(mIntent.getAction())) {
                recipeId = RecipeWidget.getSingleRecipeId();
            }
        }
        else {
            recipeId = RecipeWidget.getRecipeId();
        }
        recipe = recipes.get(recipeId - 1);

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        if (recipe == null) {
            return 0;
        }
        if (recipe.getIngredients() == null || recipe.getIngredients().isEmpty()) {
            return 0;
        }
        return recipe.getIngredients().size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (recipe == null) return null;

        Ingredient ingredient = recipe.getIngredients().get(position);
        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.recipe_list_widget);
        remoteViews.setTextViewText(R.id.widget_ingredient_name, ingredient.getIngredient());
        remoteViews.setTextViewText(R.id.widget_ingredient_quantity, String.valueOf(ingredient.getQuantity() + " " + ingredient.getMeasure()));
        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
