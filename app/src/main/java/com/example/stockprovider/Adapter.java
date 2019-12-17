package com.example.stockprovider;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    private Context mContext;
    private Cursor mCursor;

    public Adapter(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mstockName;
        private TextView mstockDetail2;
        private TextView mstockDetail3;
        public ViewHolder(View itemView){
            super(itemView);
            mstockName = itemView.findViewById(R.id.stockName);
            mstockDetail2 = itemView.findViewById(R.id.stockDetail2);
            mstockDetail3 = itemView.findViewById(R.id.stockDetail3);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.stock_list, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(!mCursor.moveToPosition(position)) {
            return;
        }

        String StockName        = mCursor.getString(mCursor.getColumnIndex(DBConnect.StockName));
        String StockMarketPrice = mCursor.getString(mCursor.getColumnIndex(DBConnect.StockMarketPrice));
        String StockChange      = mCursor.getString(mCursor.getColumnIndex(DBConnect.StockChange));

        holder.mstockName.setText(StockName);
        holder.mstockDetail2.setText(StockMarketPrice);
        if(Double.parseDouble(StockChange) < 0) {
            holder.mstockDetail3.setTextColor(Color.RED);
        }
        holder.mstockDetail3.setText(StockChange);
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        if(mCursor != null) {
            mCursor.close();
        }

        mCursor = newCursor;

        if(newCursor != null) {
            notifyDataSetChanged();
        }
    }
}
