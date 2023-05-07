package com.example.szonyeg;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

//import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.EventListener;

import java.util.ArrayList;


public class ShopingItemAdapter extends RecyclerView.Adapter<ShopingItemAdapter.ViewHolder> implements Filterable {
    private ArrayList<ShopingItem> mShopingItemsData;
    private ArrayList<ShopingItem> mShopingItemsDataAll;
    private Context mContext;
    private int lastPosition = -1;

    ShopingItemAdapter(Context context, ArrayList<ShopingItem> itemsData){
        this.mShopingItemsData = itemsData;
        this.mShopingItemsDataAll = itemsData;
        this.mContext = context;
    }

    @Override
    public ShopingItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ShopingItemAdapter.ViewHolder holder, int position) {
        ShopingItem currentItem = mShopingItemsData.get(position);

        holder.bindTo(currentItem);
        if(holder.getAdapterPosition()>lastPosition){
            Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_row);
            holder.itemView.startAnimation(anim);
            lastPosition = holder.getAdapterPosition();
        }
    }

    @Override
    public int getItemCount() {
        return mShopingItemsData.size();
    }

    @Override
    public Filter getFilter() {
        return shopingFilter;
    }
    private Filter shopingFilter = new Filter(){
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<ShopingItem> filteredList = new ArrayList<>();
            FilterResults results = new FilterResults();

            if(charSequence == null || charSequence.length() == 0){
                results.count = mShopingItemsDataAll.size();
                results.values = mShopingItemsDataAll;
            }else{
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for(ShopingItem item : mShopingItemsDataAll){
                    if(item.getNev().toLowerCase().contains(filterPattern)){
                        filteredList.add(item);
                    }
                }
                results.count = filteredList.size();
                results.values = filteredList;
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            mShopingItemsData = (ArrayList)filterResults.values;
            notifyDataSetChanged();

        }
    };

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTitleText;
        private TextView mInfoText;
        private TextView mPriceText;
        private ImageView mItemImage;
        public ViewHolder(View itemView) {
            super(itemView);
            mTitleText = itemView.findViewById(R.id.itemTitle);
            mInfoText = itemView.findViewById(R.id.itemInfos);
            mPriceText = itemView.findViewById(R.id.itemPrice);
            mItemImage = itemView.findViewById(R.id.itemImage);

            itemView.findViewById(R.id.add_to_cart).setOnClickListener( new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    Log.d("Activity", "Add cart button clicked!");
                    ((SzonyegekActivity)mContext).updateAlertIcon();
                }
            });
        }

        void bindTo(ShopingItem currentItem){
            mTitleText.setText(currentItem.getNev());
            mInfoText.setText(currentItem.getInfo());
            mPriceText.setText(currentItem.getAr());

            // Load the images into the ImageView using the Glide library.
           // Glide.with(mContext).load(currentItem.getImageResource()).into(mItemImage);

           // itemView.findViewById(R.id.add_to_cart).setOnClickListener(view -> ((SzonyegekActivity)mContext).updateAlertIcon(currentItem));
           // itemView.findViewById(R.id.delete).setOnClickListener(view -> ((SzonyegekActivity)mContext).deleteItem(currentItem));

        }
    }
}
