package com.rdc.musicplayer.musicplayer.ui;

import android.content.Intent;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.rdc.musicplayer.musicplayer.R;
import com.rdc.musicplayer.musicplayer.adapter.EvaluateAdapter;
import com.rdc.musicplayer.musicplayer.base.BaseActivity;
import com.rdc.musicplayer.musicplayer.bean.EvaluateAddBean;
import com.rdc.musicplayer.musicplayer.bean.EvaluateBean;
import com.rdc.musicplayer.musicplayer.utils.GsonUtil;
import com.rdc.musicplayer.musicplayer.utils.OkHttpResultCallback;
import com.rdc.musicplayer.musicplayer.utils.OkHttpUtil;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.ArrayList;

import butterknife.BindView;
import okhttp3.Call;

/**
 * 评价页面
 */
public class EvaluateActivity extends BaseActivity {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.et_input)
    EditText mEtInput;
    @BindView(R.id.btn_add_evaluate)
    Button mBtnAdd;
    EvaluateAdapter mEvaluateAdapter;

    public String key = "";
    ArrayList<EvaluateBean.EvaluateData.Evaluate> evaluateBeans = new ArrayList<>();

    @Override
    protected int setLayoutResID() {
        return R.layout.activity_evaluate;
    }

    @Override
    protected void initData() {
    }

    @Override
    protected void initView() {
        Intent intent = getIntent();
        key = intent.getStringExtra("key");
        mEvaluateAdapter = new EvaluateAdapter(evaluateBeans);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mEvaluateAdapter);
        mBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mEtInput.getText().toString())) {
                    Toast.makeText(EvaluateActivity.this, "请输入评论内容", Toast.LENGTH_SHORT).show();
                    return;
                }
                //TODO 发布评论
                addEvaluate(mEtInput.getText().toString());
            }
        });
        //请求评论数据：
        getEvaluateList();
    }

    private void addEvaluate(String msg) {
        String url = "http://hb9.api.okayapi.com/?service=App.Market_Message.Post&message_content=" + msg + "&message_pid=0&message_key=" + key + "&app_key=8D95DEA77523C37DA3C2BFBBBBB8512E";
        OkHttpUtil.getInstance().getAsync(url,
                new OkHttpResultCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                    }

                    @Override
                    public void onResponse(byte[] bytes) {
                        try {
                            String s = OkHttpUtil.getInstance().streamToString(new ByteArrayInputStream(bytes));
                            final EvaluateAddBean evaluateAddBean = GsonUtil.getGson().fromJson(s, EvaluateAddBean.class);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (evaluateAddBean.data.err_code == 0) {
                                        Toast.makeText(EvaluateActivity.this, "发表评论成功", Toast.LENGTH_SHORT).show();
                                        EvaluateBean.EvaluateData.Evaluate evaluate = new EvaluateBean.EvaluateData.Evaluate();
                                        evaluate.message_content = mEtInput.getText().toString();
                                        evaluateBeans.add(evaluate);
                                        mEvaluateAdapter.notifyDataSetChanged();
                                    } else {
                                        Toast.makeText(EvaluateActivity.this, evaluateAddBean.data.err_msg, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } catch (Exception e) {
                            Log.e("TAG", e.getMessage());
                        }
                    }
                });
    }

    @Override
    protected void initListener() {

    }

    private void getEvaluateList() {
        String url = "http://hb9.api.okayapi.com/?service=App.Market_Message.Show&order=1&page=1&perpage=10&message_key=" + key + "&app_key=8D95DEA77523C37DA3C2BFBBBBB8512E";
        OkHttpUtil.getInstance().getAsync(url,
                new OkHttpResultCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                    }

                    @Override
                    public void onResponse(byte[] bytes) {
                        try {
                            String s = OkHttpUtil.getInstance().streamToString(new ByteArrayInputStream(bytes));
                            Log.e("TAG", s);
                            final EvaluateBean evaluateBean = GsonUtil.getGson().fromJson(s, EvaluateBean.class);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    evaluateBeans.clear();
                                    evaluateBeans.addAll(evaluateBean.data.items);
                                    mEvaluateAdapter.notifyDataSetChanged();
                                }
                            });
                        } catch (Exception e) {
                            Log.e("TAG", e.getMessage());
                        }
                    }
                });
    }
}
