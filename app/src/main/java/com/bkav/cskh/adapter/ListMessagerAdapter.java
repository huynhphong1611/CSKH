package com.bkav.cskh.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bkav.cskh.MainActivity;
import com.bkav.cskh.R;
import com.bkav.cskh.data.StaticConfig;
import com.bkav.cskh.model.Consersation;

import de.hdodenhof.circleimageview.CircleImageView;

public class ListMessagerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private Consersation mConsersation;

    public ListMessagerAdapter(){

    }
    public ListMessagerAdapter(Context context, Consersation consersation) {
        mContext = context;
        mConsersation = consersation;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == MainActivity.VIEW_TYPE_SUPPORTER_MESSAGE) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.rc_item_message_supporter, parent, false);
            return new ItemMessageSupporterHolder(view);
        } else if (viewType == MainActivity.VIEW_TYPE_USER_MESSAGE) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.rc_item_message_user, parent, false);
            return new ItemMessageUserHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemMessageSupporterHolder) {
            ((ItemMessageSupporterHolder) holder).txtContent.setText(mConsersation.getListMessageData().get(position).mText);
        } else if (holder instanceof ItemMessageUserHolder) {
            ((ItemMessageUserHolder) holder).txtContent.setText(mConsersation.getListMessageData().get(position).mText);
        }
    }

    @Override
    public int getItemViewType(int position) {

       return mConsersation.getListMessageData().get(position).mIdSender.equals(StaticConfig.UID) ? MainActivity.VIEW_TYPE_USER_MESSAGE : MainActivity.VIEW_TYPE_SUPPORTER_MESSAGE;

    }

    @Override
    public int getItemCount() {
        return mConsersation.getListMessageData().size();
    }
}

     class ItemMessageUserHolder extends RecyclerView.ViewHolder {
        public TextView txtContent;
        public CircleImageView avata;

        public ItemMessageUserHolder(View itemView) {
            super(itemView);
            txtContent = (TextView) itemView.findViewById(R.id.textContentUser);
            avata = (CircleImageView) itemView.findViewById(R.id.imageView2);
        }
}

     class ItemMessageSupporterHolder extends RecyclerView.ViewHolder {
        public TextView txtContent;
        public CircleImageView avata;

        public ItemMessageSupporterHolder(View itemView) {
            super(itemView);
            txtContent = (TextView) itemView.findViewById(R.id.textContentFriend);
            avata = (CircleImageView) itemView.findViewById(R.id.imageView3);
        }
}
