package com.tec.zhang.prv.recyler;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tec.zhang.prv.R;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;
/**
 * Created by Administrator on 2017/5/4.
 */

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder>{
    private Context context;
    private List<Item> items;
    private OnItemsClickListener onItemsClickListener;
    private LayoutInflater inflater;

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
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        Item item = items.get(position);
        Glide.with(context).load(item.getItemImage()).into(holder.getCircleImageView());
        //holder.getCircleImageView().setImageResource(item.getItemImage());
        holder.getItemName().setText(item.getPartNumber());
        holder.getItemVersion().setText(item.getVersion());
        holder.getItemModified().setText(item.getLastModified());
        if (onItemsClickListener != null){
            holder.getCircleImageView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemsClickListener.onPictureClick();
                }
            });
            holder.getItemName().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemsClickListener.onNameClick();
                }
            });
            holder.getItemVersion().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemsClickListener.onVersionClick();
                }
            });
            holder.getItemModified().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemsClickListener.onDateClick();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder{
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
            this.circleImageView = (CircleImageView) view.findViewById(R.id.item_image);
            this.itemName = (TextView) view.findViewById(R.id.part_name);
            this.itemVersion = (TextView) view.findViewById(R.id.version);
            this.itemModified = (TextView) view.findViewById(R.id.last_modified);
        }
    }
    public interface OnItemsClickListener{
        void onNameClick();
        void onPictureClick();
        void onVersionClick();
        void onDateClick();
    }
}
