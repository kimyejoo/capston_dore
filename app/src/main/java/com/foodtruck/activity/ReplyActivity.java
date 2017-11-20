package com.foodtruck.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.foodtruck.R;
import com.foodtruck.network.NetworkResponse;
import com.foodtruck.network.RequestApi;
import com.foodtruck.vo.StoreReplyVo;
import com.foodtruck.vo.UpdateVo;
import com.google.gson.JsonObject;

import java.util.Date;

import retrofit2.Call;

/**
 * Created by evilstorm on 2017. 11. 20..
 */

public class ReplyActivity extends Activity {

    private String truck_id;
    private ImageView[] img_star;
    private EditText et_say;
    private Button btn_submit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reply_single);

        truck_id = getIntent().getStringExtra("truck_id");

        findViewById(R.id.img_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        img_star = new ImageView[5];
        img_star[0] = (ImageView) findViewById(R.id.img_star1);
        img_star[1] = (ImageView) findViewById(R.id.img_star2);
        img_star[2] = (ImageView) findViewById(R.id.img_star3);
        img_star[3] = (ImageView) findViewById(R.id.img_star4);
        img_star[4] = (ImageView) findViewById(R.id.img_star5);

        et_say = (EditText) findViewById(R.id.et_say);

        btn_submit = (Button) findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(submit);

        for (int i=0; i<img_star.length; i++) {
            img_star[i].setOnClickListener(starClickListener);
        }
    }

    private View.OnClickListener submit = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            addReply();

        }

    };

    private View.OnClickListener starClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            for (int i=0; i<img_star.length; i++) {
                img_star[i].setSelected(false);
            }

            switch (view.getId()) {
                case R.id.img_star5:
                    img_star[4].setSelected(true);
                case R.id.img_star4:
                    img_star[3].setSelected(true);
                case R.id.img_star3:
                    img_star[2].setSelected(true);
                case R.id.img_star2:
                    img_star[1].setSelected(true);
                case R.id.img_star1:
                    img_star[0].setSelected(true);
            }
        }
    };

    private boolean checkValidation() {

        if(!img_star[0].isSelected()){
            Toast.makeText(this, "별점을 입력하세요.", Toast.LENGTH_SHORT).show();
            return false;
        }


        if(et_say.getText() == null) {
            Toast.makeText(this, "리뷰를 입력하세요.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * 별점 가져오기
     * @return
     */
    private int getRatePoint() {
        int rate = -1;
        for (int i=0; i<img_star.length; i++) {
            if(img_star[i].isSelected()) {
                rate = i;

            }
        }
        return (rate+1);
    }

    /**
     * 댓글 등록하기
     */
    private void addReply() {
        if(!checkValidation()) {
            return;
        }

        btn_submit.setOnClickListener(null);

        JsonObject param = new JsonObject();
        param.addProperty("truck_id", truck_id);
        param.addProperty("say", et_say.getText().toString().trim());
        param.addProperty("rate", getRatePoint());

        RequestApi.getInstance().postReply(truck_id, param, new NetworkResponse<UpdateVo>() {
            @Override
            public void onSuccess(Call call, UpdateVo clazz) {
                StoreReplyVo data = new StoreReplyVo();
                data.set_id(clazz.get_id());
                data.setCreated_at(new Date());
                data.setRate(getRatePoint());
                data.setSay(et_say.getText().toString().trim());
                data.setTruck_id(truck_id);


                Intent intent = new Intent();
                intent.putExtra("data", data);
                setResult(RESULT_OK, intent);

                finish();
            }

            @Override
            public void onFail(Call call, String msg) {
                Toast.makeText(ReplyActivity.this, " 등록에 실패했습니다. : " + msg, Toast.LENGTH_SHORT).show();
                btn_submit.setOnClickListener(submit);
            }
        });

    }

}
