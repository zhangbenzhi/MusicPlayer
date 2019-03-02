package com.rdc.musicplayer.musicplayer.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rdc.musicplayer.musicplayer.R;
import com.rdc.musicplayer.musicplayer.bean.EvaluateBean;

import java.util.ArrayList;

/**
 * 评价适配器
 */
public class EvaluateAdapter extends RecyclerView.Adapter {

    private ArrayList<EvaluateBean.EvaluateData.Evaluate> evaluateBeans = new ArrayList<>();

    public EvaluateAdapter(ArrayList<EvaluateBean.EvaluateData.Evaluate> evaluateBeans) {
        this.evaluateBeans = evaluateBeans;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.evaluate_item, null);
        return new EvaluateViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        EvaluateViewHolder evaluateViewHolder = (EvaluateViewHolder) holder;
        TextView textView = evaluateViewHolder.mTvEvaluate;
        textView.setText(evaluateBeans.get(position).message_content);
    }

    @Override
    public int getItemCount() {
        return evaluateBeans.size();
    }

    public static class EvaluateViewHolder extends RecyclerView.ViewHolder {

        TextView mTvEvaluate;

        public EvaluateViewHolder(View itemView) {
            super(itemView);

            mTvEvaluate = (TextView) itemView.findViewById(R.id.tv_item);
        }
    }
}
