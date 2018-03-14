package com.example.dsekar.bakingapp;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class RecipeStepPlayerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_step_player);
        if (savedInstanceState == null) {
            StepExoplayerFragment exoplayerFragment = new StepExoplayerFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().add(R.id.recipe_step_frame_layout, exoplayerFragment).commit();
        }
    }
}
