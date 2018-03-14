package com.example.dsekar.bakingapp.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dsekar.bakingapp.Data.Ingredient;
import com.example.dsekar.bakingapp.Data.Recipe;
import com.example.dsekar.bakingapp.Data.Step;
import com.example.dsekar.bakingapp.R;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeIntegrientStepAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Context mContext;
    private static final int INGREDIENT = 1;
    private static final int STEP = 2;
    private Recipe mRecipe;
    private StepItemClickIinterface mStepItemClick;

    public RecipeIntegrientStepAdapter(Context context, StepItemClickIinterface stepItemClickIinterface, Recipe recipe) {
        mContext = context;
        mStepItemClick = stepItemClickIinterface;
        mRecipe = recipe;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == INGREDIENT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.recipe_ingredient, parent, false);
            return new RecipeIntegrientStepAdapter.IngredientViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.recipe_step, parent, false);
            return new RecipeIntegrientStepAdapter.StepViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder == null){
            return;
        }

        if (holder.getItemViewType() == INGREDIENT) {
            Ingredient ingredient = mRecipe.getIngredients().get(position);
            ((IngredientViewHolder) holder).ingredientName.setText(ingredient.getIngredient());
            ((IngredientViewHolder) holder).ingredientQuantity.setText(String.valueOf(ingredient.getQuantity() + ingredient.getMeasure()));
        } else {
            position = position - mRecipe.getIngredients().size();
            Step step = mRecipe.getSteps().get(position);
            ((StepViewHolder) holder).recipeStepTitle.setText(step.getShortDescription());
            if (!step.getThumbnailURL().isEmpty()) {
                Picasso.with(mContext).load(step.getThumbnailURL()).placeholder(R.drawable.recipe_icon_md)
                        .into(((StepViewHolder) holder).recipeStepImage);
            } else {
                Picasso.with(mContext).load(R.drawable.recipe_icon_md)
                        .into(((StepViewHolder) holder).recipeStepImage);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position < mRecipe.getIngredients().size())
        {
            return INGREDIENT;
        }
        return STEP;
    }

    @Override
    public int getItemCount() {
        if (mRecipe == null) {
            return 0;
        }
        return mRecipe.getIngredients().size() + mRecipe.getSteps().size();
    }

    public class IngredientViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.recipe_ingredient_name)
        TextView ingredientName;

        @BindView(R.id.recipe_ingredient_quantity)
        TextView ingredientQuantity;

        public IngredientViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class StepViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.recipe_step_title)
        TextView recipeStepTitle;

        @BindView(R.id.recipe_step_image)
        ImageView recipeStepImage;

        public StepViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Step step = mRecipe.getSteps().get(position - mRecipe.getIngredients().size());
            mStepItemClick.onStepClicked(step);
        }
    }


    public interface StepItemClickIinterface {
        void onStepClicked(Step step);
    }
}
