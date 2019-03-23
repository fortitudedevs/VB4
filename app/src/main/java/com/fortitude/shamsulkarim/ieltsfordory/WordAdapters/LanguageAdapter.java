package com.fortitude.shamsulkarim.ieltsfordory.WordAdapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fortitude.shamsulkarim.ieltsfordory.Language;
import com.fortitude.shamsulkarim.ieltsfordory.MainActivity;
import com.fortitude.shamsulkarim.ieltsfordory.R;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import java.util.List;

/**
 * Created by karim on 9/10/17.
 */

public class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.LanguageViewHolder> {

    List<Language> languages;
    Language language = new Language(0,0,"");
    public LanguageAdapter() {
        languages = language.getLanguages();

    }

    @Override
    public LanguageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.language_row,parent,false);

        LanguageViewHolder viewHolder = new LanguageViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(LanguageViewHolder holder, int position) {


        Language language = languages.get(position);

        holder.flag.setImageResource(language.getImage());
        holder.languageName.setText(language.getName());

    }

    @Override
    public int getItemCount() {
        return languages.size();
    }

    public class LanguageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView flag;
        TextView languageName;
        SharedPreferences sp;
        Context ctx;

        public LanguageViewHolder(View itemView) {
            super(itemView);
            ctx= itemView.getContext();
            sp = ctx.getSharedPreferences("com.example.shamsulkarim.vocabulary", Context.MODE_PRIVATE);

            flag = (ImageView)itemView.findViewById(R.id.flag);
            languageName = (TextView)itemView.findViewById(R.id.language_name);
            languageName.setTextColor(ctx.getResources().getColor(R.color.background_snackbar));

            flag.setOnClickListener(this);
            languageName.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            int position = getAdapterPosition();

            if(v == flag  || v == languageName){
             sp.edit().putInt("language",getAdapterPosition()).apply();


                StyleableToast.makeText(ctx, languages.get(getAdapterPosition()).getName()+" selected", 10, R.style.favorite).show();


            }



        }
    }
}
