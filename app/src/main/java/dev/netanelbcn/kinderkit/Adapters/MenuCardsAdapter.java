package dev.netanelbcn.kinderkit.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

import dev.netanelbcn.kinderkit.Controllers.DataManager;
import dev.netanelbcn.kinderkit.Interfaces.KidCallback;
import dev.netanelbcn.kinderkit.Models.Kid;
import dev.netanelbcn.kinderkit.R;

public class MenuCardsAdapter extends RecyclerView.Adapter<MenuCardsAdapter.KidViewHolder> {
    private Context context;
    private ArrayList<Kid> kids;
    private KidCallback kidCallback;
    private DataManager dataManager = DataManager.getInstance();

    public MenuCardsAdapter(Context context) {
        this.context = context;
        setKids(dataManager.getKids());
    }

    public MenuCardsAdapter setPlayerCallback(KidCallback playerCallback) {
        this.kidCallback = playerCallback;
        return this;
    }

    public void setKids(ArrayList<Kid> kids) {
        dataManager.loadAllKids(new DataManager.OnKidsLoadedListener() {
            @Override
            public void onKidsLoaded(ArrayList<Kid> kids) {
                MenuCardsAdapter.this.kids = kids;
                notifyDataSetChanged();
            }

            @Override
            public void onFailure(Exception exception) {

            }
        });
    }

    @NonNull
    @Override
    public MenuCardsAdapter.KidViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.kid_card, parent, false);
        return new KidViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuCardsAdapter.KidViewHolder holder, int position) {
        Kid kid = getItem(position);
        holder.KC_MTV_name.setText(kid.getfName() + " " + kid.getlName());

        // Get the profile photo URI
        String profilePhotoUri = kid.getProfilePhotoUri();

        if (profilePhotoUri != null && !profilePhotoUri.isEmpty()) {
            // Load the image with Glide
            Glide.with(context)
                    .load(profilePhotoUri)
                    .placeholder(R.drawable.ic_launcher_background) // Placeholder image while loading
                    .into(holder.KC_SIV_photo);
        } else {
            // Set a placeholder image if the URI is null or empty
            holder.KC_SIV_photo.setImageResource(R.drawable.ic_launcher_background);
        }
    }


    @Override
    public int getItemCount() {
        if (kids == null) {
            return 0;
        }
        return kids.size();
    }

    private Kid getItem(int position) {
        return kids.get(position);
    }

    public class KidViewHolder extends RecyclerView.ViewHolder {
        private ShapeableImageView KC_SIV_photo;
        private MaterialTextView KC_MTV_name;
        private MaterialCardView KC_MCV_card;

        public KidViewHolder(@NonNull View itemView) {
            super(itemView);
            KC_SIV_photo = itemView.findViewById(R.id.KC_SIV_photo);
            KC_MTV_name = itemView.findViewById(R.id.KC_MTV_name);
            KC_MCV_card = itemView.findViewById(R.id.KC_MCV_card);
            KC_MCV_card.setOnClickListener(v -> {
                if (kidCallback != null) {
                    kidCallback.CardClicked(getItem(getAdapterPosition()), getAdapterPosition());
                }
            });

        }
    }
}
