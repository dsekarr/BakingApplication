package com.example.dsekar.bakingapp.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

import com.example.dsekar.bakingapp.App;

public class RecipeUtils {
    public static Boolean isConnectedToInternet(){
        ConnectivityManager cm =
                (ConnectivityManager) App.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm != null ? cm.getActiveNetworkInfo() : null;
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    public static void setSnackBar(View view,CharSequence charSequence, int duration, int color){
        Snackbar snackbar = Snackbar.make(view,
                charSequence, duration );
        View getView = snackbar.getView();
        TextView tv = getView.findViewById(android.support.design.R.id.snackbar_text);
        tv.setBackgroundColor(color);
        tv.setTextSize(14);
        snackbar.show();
    }
}
