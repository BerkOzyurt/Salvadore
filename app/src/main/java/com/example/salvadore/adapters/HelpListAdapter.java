package com.example.salvadore.adapters;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.salvadore.R;
import com.example.salvadore.activities.MainActivity;
import com.example.salvadore.clouddb.Help;
import com.example.salvadore.listeners.HelpListItemClickListener;
import com.example.salvadore.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

public class HelpListAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    Context context;
    private List<Help> arrayList;
    HelpListItemClickListener listener;

    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private boolean isLoaderVisible = false;

    public HelpListAdapter(List<Help> arrayList, Context context, HelpListItemClickListener listener){
        this.arrayList = arrayList;
        this.listener = listener;
        this.context = context;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false));
            case VIEW_TYPE_LOADING:
                return new FooterHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_loading, parent, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends BaseViewHolder {
        TextView listItem_userName;
        TextView listItem_helpKind;
        TextView listItem_helpAddress;
        TextView listItem_helpDetail;
        TextView listItem_helpDate;
        Button btn_showHelpDetail;

        public ViewHolder(View view){
            super(view);
            listItem_userName = view.findViewById(R.id.listItem_userName);
            listItem_helpKind = view.findViewById(R.id.listItem_helpKind);
            listItem_helpAddress = view.findViewById(R.id.listItem_helpAddress);
            listItem_helpDetail = view.findViewById(R.id.listItem_helpDetail);
            listItem_helpDate = view.findViewById(R.id.listItem_helpDate);
            btn_showHelpDetail = view.findViewById(R.id.btn_showHelpDetail);
        }

        @Override
        protected void clear() {

        }

        @Override
        public void onBind(int position) {
            super.onBind(position);

            final Help help = arrayList.get(position);
            listItem_userName.setText(help.getUser_name());
            listItem_helpKind.setText(help.getState());
            listItem_helpAddress.setText(help.getAddress());
            listItem_helpDetail.setText(help.getDetail());
            listItem_helpDate.setText(help.getUser_token());

            btn_showHelpDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClicked(help,position);
                }
            });
        }
    }

    public void removeLoading(){
        isLoaderVisible = false;
        int position = arrayList.size() - 1;
        Help help = getItem(position);
        if(help != null){
            arrayList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void addLoading(){
        isLoaderVisible = true;
    }

    public void addAll(List<Help> postItems){
        for(Help response : postItems){
            add(response);
        }
    }

    public void add(Help response){
        arrayList.add(response);
        notifyItemInserted(arrayList.size() - 1);
    }

    Help getItem(int position){
        return arrayList.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        if(isLoaderVisible){
            return position == arrayList.size() - 1 ? VIEW_TYPE_LOADING : VIEW_TYPE_NORMAL;
        }else{
            return VIEW_TYPE_NORMAL;
        }
    }

    public class FooterHolder extends BaseViewHolder{

        FooterHolder(View itemView){
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        @Override
        protected void clear() {

        }
    }

    public void cleaer(){
        while (getItemCount() > 0){
            remove(getItem(0));
        }
    }

    public void remove(Help postItems){
        int position = arrayList.indexOf(postItems);
        if(position > -1){
            arrayList.remove(position);
            notifyItemRemoved(position);
        }
    }
}
