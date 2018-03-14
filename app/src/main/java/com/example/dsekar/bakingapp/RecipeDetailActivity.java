package com.example.dsekar.bakingapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.example.dsekar.bakingapp.Adapters.RecipeIntegrientStepAdapter;
import com.example.dsekar.bakingapp.Constants.AppConstants;
import com.example.dsekar.bakingapp.Data.Ingredient;
import com.example.dsekar.bakingapp.Data.Recipe;
import com.example.dsekar.bakingapp.Data.Step;
import com.example.dsekar.bakingapp.Utils.RecipeUtils;
import com.example.dsekar.bakingapp.Widget.RecipeWidget;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeDetailActivity extends AppCompatActivity implements RecipeIntegrientStepAdapter.StepItemClickIinterface {

    private static final String Step = "clicked_step";

    @BindView(R.id.receipe_detail_recycler_view)
    RecyclerView recyclerView;

    List<Ingredient> ingredients = new ArrayList<>();
    List<Step> steps = new ArrayList<>();
    Recipe recipe;
    boolean mTwoPane;

    private RecipeIntegrientStepAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        ButterKnife.bind(this);
        if (findViewById(R.id.recipe_step_frame_layout) != null) {
            mTwoPane = true;
        }
        getIntentData();
        if (getSupportActionBar() != null && recipe != null) {
            getSupportActionBar().setTitle(recipe.getName());
        }
        if (savedInstanceState == null && mTwoPane) {
            setImage();
        } else {
            if (mTwoPane) {
                findViewById(R.id.recipe_step_frame_layout).setVisibility(View.VISIBLE);
                findViewById(R.id.recipe_default_image).setVisibility(View.GONE);
            }

        }
        mAdapter = new RecipeIntegrientStepAdapter(this, this, recipe);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mAdapter);

    }

    public void setImage() {
        String imageName = recipe.getName();
        int imageId = R.drawable.recipe_icon_md;
        switch (imageName) {
            case AppConstants.nutella_pie:
                imageId = R.drawable.ic_nutella_pie;
                break;
            case AppConstants.brownies:
                imageId = R.drawable.ic_brownie;
                break;
            case AppConstants.yellow_cake:
                imageId = R.drawable.ic_yellowcake;
                break;
            case AppConstants.cheesecake:
                imageId = R.drawable.ic_cheesecake;
                break;
        }
        ImageView defaultImageView = findViewById(R.id.recipe_default_image);
        defaultImageView.setImageResource(imageId);
    }

    private void getIntentData() {
        if (getIntent() != null) {
            if ((AppConstants.ACTION_ALL_RECIPE).equals(getIntent().getAction())) {
                if (getIntent().hasExtra(AppConstants.RECIPE)) {
                    recipe = getIntent().getParcelableExtra(AppConstants.RECIPE);
                    ingredients = recipe.getIngredients();
                    steps = recipe.getSteps();
                }
            } else if ((AppConstants.ACTION_SINGLE_RECIPE).equals(getIntent().getAction())) {
                recipe = getIntent().getParcelableExtra(AppConstants.A_RECIPE);
                ingredients = recipe.getIngredients();
                steps = recipe.getSteps();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.widget_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_widget) {
            SharedPreferences pref = getApplicationContext()
                    .getSharedPreferences(AppConstants.PREFERENCE_SINGLE_RECIPE_ID, 0); // 0 - for private mode
            pref.edit().putInt(AppConstants.A_RECIPE_FOR_SINGLE_WIDGET, recipe.getId()).apply();

            RecipeWidget.UpdateSingleRecipe();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onStepClicked(Step step) {
        if (!RecipeUtils.isConnectedToInternet()) {
            RecipeUtils.setSnackBar(findViewById(R.id.receipe_detail_recycler_view), getResources().getString(R.string.no_connection)
                    , Snackbar.LENGTH_SHORT, getResources().getColor(R.color.black));
            return;
        }
        if (mTwoPane) {
            StepExoplayerFragment exoplayerFragment = new StepExoplayerFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            exoplayerFragment.getStepData(step);
            (findViewById(R.id.recipe_default_image)).setVisibility(View.GONE);
            (findViewById(R.id.recipe_step_frame_layout)).setVisibility(View.VISIBLE);
            fragmentManager.beginTransaction().replace(R.id.recipe_step_frame_layout, exoplayerFragment).commit();
        } else {
            Intent stepIntent = new Intent(this, RecipeStepPlayerActivity.class);
            stepIntent.putExtra(Step, step);
            startActivity(stepIntent);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
    }
}
