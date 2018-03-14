package com.example.dsekar.bakingapp.Retrofit;

import com.example.dsekar.bakingapp.Data.Recipe;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RecipeService {

    @GET("android-baking-app-json")
    Call<List<Recipe>> listRecipes();
}
