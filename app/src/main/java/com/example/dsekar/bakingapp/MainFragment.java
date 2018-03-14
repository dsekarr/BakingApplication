package com.example.dsekar.bakingapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.dsekar.bakingapp.Adapters.RecipeAdapter;
import com.example.dsekar.bakingapp.Constants.AppConstants;
import com.example.dsekar.bakingapp.Data.Recipe;
import com.example.dsekar.bakingapp.Retrofit.RecipeClient;
import com.example.dsekar.bakingapp.Retrofit.RecipeService;
import com.example.dsekar.bakingapp.Utils.RecipeUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainFragment extends android.support.v4.app.Fragment implements RecipeAdapter.OnClickInterface {

    @BindView(R.id.receipeView)
    RecyclerView mRecipeRecyclerView;

    @BindView(R.id.loading_progress_bar)
    ProgressBar mProgressBar;

    @BindView(R.id.error_message)
    TextView mErrorMessage;

    private RecipeAdapter mRecipeAdapter;
    private onNetworkChangeReceiver networkChangeReceiver;
    private IntentFilter intentFilter;

    List<Recipe> recipeList = new ArrayList<>();

    private static final String RECIPE_LIST = "recipe_list_saved_state";

    public MainFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);
        mRecipeAdapter = new RecipeAdapter(rootView.getContext(), this);
        GridLayoutManager layoutManager = new GridLayoutManager(rootView.getContext(), getResources().getInteger(R.integer.no_of_colums));
        mRecipeRecyclerView.setLayoutManager(layoutManager);
        mRecipeRecyclerView.setHasFixedSize(true);
        mRecipeRecyclerView.setAdapter(mRecipeAdapter);
        networkChangeReceiver = new onNetworkChangeReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        if (savedInstanceState == null) {
            LoadRecipeData();
        } else {
            recipeList = savedInstanceState.getParcelableArrayList(RECIPE_LIST);
            if (recipeList != null && recipeList.size() > 0) {
                mRecipeAdapter.setReceipeData(recipeList);
            } else {
                errorLoadingData();
            }
        }
        return rootView;
    }

    private void loadingData() {
        mProgressBar.setVisibility(View.VISIBLE);
        mRecipeRecyclerView.setVisibility(View.GONE);
        mErrorMessage.setVisibility(View.GONE);
    }

    private void errorLoadingData() {
        mProgressBar.setVisibility(View.GONE);
        mRecipeRecyclerView.setVisibility(View.GONE);
        mErrorMessage.setVisibility(View.VISIBLE);
        mErrorMessage.setText(getResources().getString(R.string.no_network_error_message));
    }

    private void loadingFinished() {
        mProgressBar.setVisibility(View.GONE);
        mRecipeRecyclerView.setVisibility(View.VISIBLE);
        mErrorMessage.setVisibility(View.GONE);
    }

    private void LoadRecipeData(){
        if (RecipeUtils.isConnectedToInternet()) {
            loadingData();
            getRecipeData();
        } else {
            errorLoadingData();
        }
    }

    private void getRecipeData() {
        RecipeService recipeService = RecipeClient.getClient().create(RecipeService.class);
        Call<List<Recipe>> call = recipeService.listRecipes();
        call.enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(@NonNull Call<List<Recipe>> call, @NonNull Response<List<Recipe>> response) {
                if (response.isSuccessful()) {
                    loadingFinished();
                    recipeList = response.body();
                    mRecipeAdapter.setReceipeData(recipeList);
                    storeInSharedPreference();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Recipe>> call, @NonNull Throwable t) {
                mErrorMessage.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
                mErrorMessage.setText(getResources().getString(R.string.network_call_error_message));
            }
        });
    }

    public void storeInSharedPreference(){
        if(recipeList.size() > 0){
            SharedPreferences pref = App.getContext().getSharedPreferences(AppConstants.PREFERENCE_RECIPES, 0); // 0 - for private mode
            SharedPreferences.Editor editor = pref.edit();
            String json = new Gson().toJson(recipeList);
            editor.putString(AppConstants.RECIPES_FOR_WIDGET, json).apply();
            editor.commit();

            SharedPreferences prefForId = App.getContext().getSharedPreferences(AppConstants.PREFERENCE_RECIPES_ID, 0); // 0 - for private mode
            prefForId.getInt(AppConstants.RECIPE_FOR_WIDGET, 1);
        }
    }

    @Override
    public void onRecipeClicked(Recipe recipe) {
        Intent recipeIntent = new Intent(getActivity(), RecipeDetailActivity.class);
        recipeIntent.putExtra(AppConstants.RECIPE, recipe);
        recipeIntent.setAction(AppConstants.ACTION_ALL_RECIPE);
        startActivity(recipeIntent);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(networkChangeReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(networkChangeReceiver);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (recipeList != null && recipeList.size() > 0) {
            outState.putParcelableArrayList(RECIPE_LIST, (ArrayList<? extends Parcelable>) recipeList);
        } else {
            outState.putParcelableArrayList(RECIPE_LIST, null);
        }
    }

    public class onNetworkChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(RecipeUtils.isConnectedToInternet() && recipeList!= null && recipeList.size() == 0){
                LoadRecipeData();
            }
        }
    }
}
