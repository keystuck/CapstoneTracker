package com.example.emily.simplehealthtracker.data;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.emily.simplehealthtracker.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EntryAdapter extends RecyclerView.Adapter<EntryAdapter.EntryViewHolder> {

    private static final String LOG_TAG = EntryAdapter.class.getSimpleName();

    private List<Entry> mEntryList;
    private EntryAdapter.OnBoxChecked onBoxChecked;
    private boolean highlightMissed;
    final private EntryClickListener mEntryClickListener;
    private Context context;

    //taken from
    //https://android.jlelse.eu/android-handling-checkbox-state-in-recycler-views-71b03f237022
    private SparseBooleanArrayParcelable sparseBooleanArray;

    Entry currentItem;

    public interface EntryClickListener {
        void onEntryClick(int selectedEntryIndex);
    }

    public interface OnBoxChecked {
        void onCheckBoxClick(int position);
    }

    public EntryAdapter(Activity context, List<Entry> entryList, boolean highlightMissed, EntryClickListener listener) {
        this.context = context;
        mEntryList = entryList;
        this.highlightMissed = highlightMissed;
            mEntryClickListener = listener;
        sparseBooleanArray = new SparseBooleanArrayParcelable();
    }

    public SparseBooleanArrayParcelable getSparseBooleanArray() {
        return sparseBooleanArray;
    }

    public void clearSparseBooleanArray(){
        sparseBooleanArray.clear();
    }


    public Entry getCurrentItem(int position){
        return mEntryList.get(position);
    }

    @NonNull
    @Override
    public EntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_entry, parent, false);
        return new EntryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EntryViewHolder holder, int position) {
        currentItem = mEntryList.get(position);
        holder.descriptionView.setText(currentItem.getDescription());
        holder.timeView.setText(convertDate(currentItem.getTimeStamp()));
        holder.idHolder.setText(Integer.toString(currentItem.getEntryId()));
        holder.longDateView.setText(Long.toString(currentItem.getTimeStamp()));

        //if this is for a report, get info from the record
        //also highlight missed tasks
        if (highlightMissed) {
            if (currentItem.getTaken() == 0) {
                if (Long.parseLong(holder.longDateView.getText().toString()) < System.currentTimeMillis()){
                    holder.itemHolder.setBackgroundColor(context.getResources().getColor(R.color.remove));
                }

            }
            holder.typeView.setText(currentItem.getRecordType());
        }
        //if it's for CheckFragment, get info from the sparseBooleanArray
        else {
            int adapterPosition = holder.getAdapterPosition();
            if (sparseBooleanArray.get(adapterPosition)){
                holder.doneBox.setChecked(true);
                holder.doneBox.setBackgroundColor(context.getResources().getColor(R.color.add));
            } else {
                holder.doneBox.setChecked(false);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mEntryList.size();
    }

    public void clear(){
        mEntryList.clear();
        sparseBooleanArray.clear();
    }

    public void addAll(List<Entry> inputList){
        mEntryList.addAll(inputList);
    }



    class EntryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.tv_description)
        TextView descriptionView;
        @BindView(R.id.cb_done)
        CheckBox doneBox;
        @BindView(R.id.tv_timestamp)
        TextView timeView;
        @BindView(R.id.tv_dose)
        TextView doseView;
        @BindView(R.id.tv_id_holder)
        TextView idHolder;
        @BindView(R.id.tv_type)
        TextView typeView;
        @BindView(R.id.tv_check_msg)
        TextView checkView;
        @BindView(R.id.cl_layout_view)
        ConstraintLayout itemHolder;
        @BindView(R.id.tv_long_date)
        TextView longDateView;

        public EntryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);

            if (highlightMissed){
                checkView.setText(R.string.to_cancel_delete);
            } else {
                checkView.setText(R.string.to_check_off);
            }

        }



        @Override
        public void onClick(View view) {
            if (mEntryClickListener != null) {
                int adapterPosition = getAdapterPosition();
                mEntryClickListener.onEntryClick(adapterPosition);

                if (sparseBooleanArray.get(adapterPosition) == false){
                    doneBox.setChecked(true);
                    if (highlightMissed){
                        checkView.setText(R.string.will_cancel);
                    } else {
                        checkView.setText(R.string.will_check_off);
                    }
                    checkView.setBackgroundColor(context.getResources().getColor(R.color.add));
                    sparseBooleanArray.put(adapterPosition, true);
                } else {
                    doneBox.setChecked(false);

                    checkView.setBackgroundColor(context.getResources().getColor(R.color.white));
                    sparseBooleanArray.put(adapterPosition, false);
                }
            }
        }
    }



    public String convertDate(long timestamp){
        Date date = new Date(timestamp);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd hh:mm");
        String time = simpleDateFormat.format(date);
        return time;

    }
}
