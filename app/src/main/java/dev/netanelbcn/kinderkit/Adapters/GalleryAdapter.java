package dev.netanelbcn.kinderkit.Adapters;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;

import dev.netanelbcn.kinderkit.Controllers.DataManager;
import dev.netanelbcn.kinderkit.Interfaces.DelPicCallback;
import dev.netanelbcn.kinderkit.Interfaces.SetAsProfilePictureCallback;
import dev.netanelbcn.kinderkit.Models.Kid;
import dev.netanelbcn.kinderkit.Models.MyPhoto;
import dev.netanelbcn.kinderkit.R;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.PictureViewHolder> {

    private Context context;

    private DelPicCallback delPicCallback;
    private SetAsProfilePictureCallback setAsProfilePictureCallback;
    private ArrayList<MyPhoto> pictures;
    private DataManager dataManager = DataManager.getInstance();
    private int currentKidPosition;

    public GalleryAdapter(Context context, ArrayList<MyPhoto> pictures, int currentKidPosition) {
        this.context = context;
        this.pictures = pictures;
        this.currentKidPosition = currentKidPosition;
        setPictures(currentKidPosition);

    }

    public void setPictures(int currentKidPosition) {
        dataManager.loadAllKids(new DataManager.OnKidsLoadedListener() {
            @Override
            public void onKidsLoaded(ArrayList<Kid> kids) {
                GalleryAdapter.this.pictures = kids.get(currentKidPosition).getPhotosUri();
            }

            @Override
            public void onFailure(Exception exception) {
                Log.e("errr", exception.getMessage());
            }
        });
    }

    public GalleryAdapter setDelPicCallback(DelPicCallback delPicCallback) {
        this.delPicCallback = delPicCallback;
        return this;
    }


    public GalleryAdapter setSetAsProfilePictureCallback(SetAsProfilePictureCallback setAsProfilePictureCallback) {
        this.setAsProfilePictureCallback = setAsProfilePictureCallback;
        return this;
    }

    @NonNull
    @Override
    public PictureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_galley_kid, parent, false);
        return new PictureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryAdapter.PictureViewHolder holder, int position) {
        MyPhoto picture = getItem(position);
        holder.PG_SIV_Photo.setImageURI(Uri.parse(picture.getPhotoUri()));
        Glide.with(this.context).load(picture.getPhotoUri()).placeholder(R.drawable.ic_launcher_background).into(holder.PG_SIV_Photo);
        holder.PG_MB_SetAsProfilePicture.setOnClickListener(v -> {
            if (setAsProfilePictureCallback != null)
                setAsProfilePictureCallback.onSetAsProfilePicture(Uri.parse(picture.getPhotoUri()));
        });
        holder.PG_MB_Delete.setOnClickListener(v -> {
            if (delPicCallback != null) delPicCallback.onDelPic(picture);
        });
    }

    @Override
    public int getItemCount() {
        return pictures.size();
    }

    private MyPhoto getItem(int position) {
        return pictures.get(position);
    }


    public class PictureViewHolder extends RecyclerView.ViewHolder {
        private ShapeableImageView PG_SIV_Photo;
        private MaterialButton PG_MB_SetAsProfilePicture;
        private MaterialButton PG_MB_Delete;

        public PictureViewHolder(@NonNull View itemView) {
            super(itemView);
            PG_SIV_Photo = itemView.findViewById(R.id.PG_SIV_Photo);
            PG_MB_SetAsProfilePicture = itemView.findViewById(R.id.PG_MB_SetAsProfilePicture);
            PG_MB_Delete = itemView.findViewById(R.id.PG_MB_Delete);

        }
    }
}
