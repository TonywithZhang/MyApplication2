package com.tec.zhang.prv.recyler;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tec.zhang.prv.R;
import com.tec.zhang.prv.databaseUtil.PartDetail;

import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;
/**
 * Created by Administrator on 2017/5/4.
 */

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder>{
    private static final String TAG = "适配器";
    private Context context;
    private List<Item> items;
    private OnItemsClickListener onItemsClickListener;
    private LayoutInflater inflater;
    private ImageView imageView;

    public  ItemAdapter(Context context,List<Item> items,OnItemsClickListener listener){
        this.context = context;
        this.onItemsClickListener = listener;
        this.items = items;
    }
    public ItemAdapter(Context context){
        this(context,null,null);
    }
    public ItemAdapter(Context context,List<Item> items){
        this(context,items,null);
    }
    public void setItems(List<Item> items){
        this.items = items;
    }
    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.recycler,parent,false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, int position) {
        final Item item = items.get(position);
        //Glide.with(context).load(item.getItemImage()).into(holder.getCircleImageView());
        //Glide.with(context).load(item.getItemImage()).into(holder.getCircleImageView());
        Bitmap bitmap = decodeSampleBitmapFromResource(context.getResources(),item.getItemImage(),80,80);
        holder.getCircleImageView().setImageBitmap(bitmap);
        holder.getItemName().setText(item.getPartNumber());
        holder.getItemVersion().setText(item.getVersion());
        holder.getItemModified().setText(item.getLastModified());
        if (onItemsClickListener != null){
            holder.getCircleImageView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemsClickListener.onPictureClick(item.getPartNumber(),item.getPartNumber());
                }
            });
            holder.getItemName().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imageView = holder.getCircleImageView();
                    onItemsClickListener.onNameClick(item.getId());
                }
            });
            holder.getItemVersion().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imageView = holder.getCircleImageView();
                    onItemsClickListener.onVersionClick(item.getId());
                }
            });
            holder.getCardView().setOnClickListener(v -> {
                onItemsClickListener.onItemClick(item.getPartNumber());
            });
            holder.getItemModified().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imageView = holder.getCircleImageView();
                    onItemsClickListener.onDateClick(item.getId());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder{
        private View cardView;

        public View getCardView() {
            return cardView;
        }

        public void setCardView(View cardView) {
            this.cardView = cardView;
        }

        private CircleImageView circleImageView;
        private TextView itemName;
        private TextView itemVersion;
        private TextView itemModified;

        public CircleImageView getCircleImageView() {
            return circleImageView;
        }

        public void setCircleImageView(CircleImageView circleImageView) {
            this.circleImageView = circleImageView;
        }

        public TextView getItemName() {
            return itemName;
        }

        public void setItemName(TextView itemName) {
            this.itemName = itemName;
        }

        public TextView getItemVersion() {
            return itemVersion;
        }

        public void setItemVersion(TextView itemVersion) {
            this.itemVersion = itemVersion;
        }

        public TextView getItemModified() {
            return itemModified;
        }

        public void setItemModified(TextView itemModified) {
            this.itemModified = itemModified;
        }

        public ItemViewHolder(View view){
            super(view);
            this.cardView  = view.findViewById(R.id.card_rec);
            this.circleImageView = (CircleImageView) view.findViewById(R.id.item_image);
            this.itemName = (TextView) view.findViewById(R.id.part_name);
            this.itemVersion = (TextView) view.findViewById(R.id.version);
            this.itemModified = (TextView) view.findViewById(R.id.last_modified);
        }
    }

    public ImageView getImageView() {
        return imageView;
    }

    public interface OnItemsClickListener{
        void onItemClick(String partNum);
        void onNameClick(String name);
        void onPictureClick(String name,String number);
        void onVersionClick(String version);
        void onDateClick(String date);
    }

    private int calculateInSampleSize(BitmapFactory.Options options,int requiredWidth,int requiredHeight){
        final int height = options.outHeight;
        final int width = options.outHeight;
        int inSampleSize = 1;
        if (height > requiredHeight || width > requiredWidth){
            final int heightRatio = Math.round((float) height/(float) requiredHeight);
            final int widthRatio = Math.round((float)width/(float)requiredWidth);
            inSampleSize = heightRatio > widthRatio ? widthRatio : heightRatio;
        }
        Log.d(TAG, "calculateInSampleSize: " + inSampleSize);
        return inSampleSize;

    }
    private Bitmap decodeSampleBitmapFromResource(Resources resources,int resId,int requiredWidth,int requiredHeight){
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(resources,resId,options);
        options.inSampleSize = calculateInSampleSize(options,requiredWidth,requiredHeight);
        options.inJustDecodeBounds =false;
        return BitmapFactory.decodeResource(resources,resId,options);
    }
}
