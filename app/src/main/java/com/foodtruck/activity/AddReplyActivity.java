package com.foodtruck.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.foodtruck.R;
import com.foodtruck.network.NetworkResponse;
import com.foodtruck.network.RequestApi;
import com.foodtruck.utils.LogUtil;
import com.foodtruck.utils.StringUtil;
import com.foodtruck.vo.StoreReplyVo;
import com.foodtruck.vo.StoreVo;
import com.foodtruck.vo.UpdateVo;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import retrofit2.Call;

/**
 * Created by Administrator on 2017-11-15.
 */

public class AddReplyActivity extends Activity {

    private ImageButton[] score;
    private EditText et_say;
    private LinearLayout lay_row;

    private String truck_id;
    private int userScore = -1;
    private StoreVo storeVo;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reply);

        truck_id = getIntent().getStringExtra("truck_id");
        storeVo = (StoreVo) getIntent().getSerializableExtra("data");


        score = new ImageButton[5];
        score[0] = (ImageButton) findViewById(R.id.ibt_1);
        score[1] = (ImageButton) findViewById(R.id.ibt_2);
        score[2] = (ImageButton) findViewById(R.id.ibt_3);
        score[3] = (ImageButton) findViewById(R.id.ibt_4);
        score[4] = (ImageButton) findViewById(R.id.ibt_5);

        for (int i=0; i<5; i++) {
            score[i].setTag((i+1));
            score[i].setOnClickListener(scoreSelete);
        }

        et_say = (EditText) findViewById(R.id.et_say);

        lay_row = (LinearLayout)findViewById(R.id.lay_row);

        findViewById(R.id.btn_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addReply();
            }
        });

        prevReplyAdd(storeVo);
    }

    private View.OnClickListener scoreSelete = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            userScore = (int)view.getTag();
            for (int i=0; i<5; i++) {
                score[i].setSelected(false);
                if(userScore > i) {
                    score[i].setSelected(true);
                }
            }
        }
    };

    private void addReply() {
        if(userScore == -1) {
            Toast.makeText(this, "평점을 입력하세요", Toast.LENGTH_SHORT).show();
            return;
        }
        if(StringUtil.isNull(et_say)) {
            Toast.makeText(this, "내용을 입력하세요/", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObject params = new JsonObject();
        params.addProperty("truck_id", truck_id);
        params.addProperty("say", StringUtil.getString(et_say));
        params.addProperty("rate", userScore);

        RequestApi.getInstance().postReply(truck_id, params, new NetworkResponse<UpdateVo>() {
            @Override
            public void onSuccess(Call call, UpdateVo clazz) {
                addReplyList(clazz.get_id());
            }

            @Override
            public void onFail(Call call, String msg) {
                Toast.makeText(AddReplyActivity.this, " 에러 : " + msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void prevReplyAdd(StoreVo storeVo){
        if(storeVo == null || storeVo.getReplys() == null) {
            return;
        }

        ArrayList<StoreReplyVo> list = storeVo.getReplys();
        for (StoreReplyVo item : list) {
            addReplyList(item);
        }
    }

    private void addReplyList(StoreReplyVo data) {

        LogUtil.e(" Reply ID : " + data.get_id());
        View baseView = getLayoutInflater().inflate(R.layout.row_add_reply, null);
        baseView.setTag(data.get_id());

        ImageButton[] score = new ImageButton[5];
        score[0] = (ImageButton) baseView.findViewById(R.id.ibt_1);
        score[1] = (ImageButton) baseView.findViewById(R.id.ibt_2);
        score[2] = (ImageButton) baseView.findViewById(R.id.ibt_3);
        score[3] = (ImageButton) baseView.findViewById(R.id.ibt_4);
        score[4] = (ImageButton) baseView.findViewById(R.id.ibt_5);
        TextView tv_say = (TextView) baseView.findViewById(R.id.tv_say);

        switch (data.getRate()){
            case 5:
                score[4].setSelected(true);
            case 4:
                score[3].setSelected(true);
            case 3:
                score[2].setSelected(true);
            case 2:
                score[1].setSelected(true);
            case 1:
                score[0].setSelected(true);
        }

        tv_say.setText(data.getSay());

        lay_row.addView(baseView);
        for (ImageButton btn: score) {
            btn.setOnClickListener(deleteReplay);
        }
        baseView.setOnClickListener(deleteReplay);
    }
    private View.OnClickListener deleteReplay = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            deleteReplyAlert((String)view.getTag(), view);
        }
    };

    private void addReplyList(String reply_id) {
        LogUtil.e(" Reply ID : " + reply_id);
        View baseView = getLayoutInflater().inflate(R.layout.row_add_reply, null);
        baseView.setTag(reply_id);

        ImageButton[] score = new ImageButton[5];
        score[0] = (ImageButton) baseView.findViewById(R.id.ibt_1);
        score[1] = (ImageButton) baseView.findViewById(R.id.ibt_2);
        score[2] = (ImageButton) baseView.findViewById(R.id.ibt_3);
        score[3] = (ImageButton) baseView.findViewById(R.id.ibt_4);
        score[4] = (ImageButton) baseView.findViewById(R.id.ibt_5);
        TextView tv_say = (TextView) baseView.findViewById(R.id.tv_say);

        switch (userScore){
            case 5:
                score[4].setSelected(true);
            case 4:
                score[3].setSelected(true);
            case 3:
                score[2].setSelected(true);
            case 2:
                score[1].setSelected(true);
            case 1:
                score[0].setSelected(true);
        }

        tv_say.setText(StringUtil.getString(et_say));

        lay_row.addView(baseView);

        for (ImageButton btn: score) {
            btn.setOnClickListener(deleteReplay);
        }
        baseView.setOnClickListener(deleteReplay);
    }

    private void deleteReplyAlert(final String reply_id, final View view) {
        new AlertDialog.Builder(AddReplyActivity.this)
                .setMessage("삭제 하시겠습니까?")
                .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteReply(reply_id, view);
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();

    }
    private void deleteReply(String reply_id, final View view) {
        RequestApi.getInstance().deleteReply(reply_id, new NetworkResponse() {
            @Override
            public void onSuccess(Call call, Object clazz) {
                Toast.makeText(AddReplyActivity.this, "삭제 되었습니다..", Toast.LENGTH_SHORT).show();
                lay_row.removeView(view);
            }

            @Override
            public void onFail(Call call, String msg) {
                Toast.makeText(AddReplyActivity.this, "삭제를 실패했습니다. " + msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
