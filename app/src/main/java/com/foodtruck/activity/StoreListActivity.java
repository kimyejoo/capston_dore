package com.foodtruck.activity;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.foodtruck.R;
import com.foodtruck.utils.LogUtil;
import com.foodtruck.vo.StoreReplyVo;
import com.foodtruck.vo.StoreVo;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by evilstorm on 2017. 11. 17..
 */

public class StoreListActivity extends Activity {

    private RecyclerView listView;
    private LinearLayoutManager lManager;

    private ArrayList<StoreVo> storeList = new ArrayList<>();
    private Adapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_list);

        storeList = (ArrayList<StoreVo>) getIntent().getSerializableExtra("data");

        listView = (RecyclerView) findViewById(R.id.listview);
        lManager = new LinearLayoutManager(this);
        listView.setLayoutManager(lManager);

        adapter = new Adapter(storeList);

        listView.setAdapter(adapter);

    }

    public class Adapter extends RecyclerView.Adapter<ViewHolder> {
        private ArrayList<StoreVo> list;
        public Adapter(ArrayList<StoreVo> list) {
            this.list = list;
        }

        private View.OnClickListener listClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int itemPosition = listView.getChildLayoutPosition(view);
                StoreVo item = storeList.get(itemPosition);
                Intent intent = new Intent(StoreListActivity.this, TruckInfoActivity.class);
                intent.putExtra("data", item);
                startActivity(intent);
            }
        };


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_store_list, parent, false);
            v.setOnClickListener(listClick);
            ViewHolder vh = new ViewHolder(v);

            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.tv_name.setText(list.get(position).getName());
            holder.tv_location.setText(list.get(position).getLocation());

            ArrayList<StoreReplyVo> replys = list.get(position).getReplys();
            int totalScore = 0;
            for (StoreReplyVo data : replys) {
                totalScore += data.getRate();
            }
            Glide.with(StoreListActivity.this)
                    .load(list.get(position).getImg())
                    .into(holder.img_thumbnail);

            if(replys == null || replys.size() == 0) {
                return;
            }

            int avgScore = totalScore / replys.size();
            for (int i=0; i<5; i++) {
                if(i < (avgScore -1)) {
                    holder.stars[i].setSelected(true);
                } else {
                    holder.stars[i].setSelected(false);
                }
            }


            LogUtil.e(" Reply Count " + list.get(position).getReplys().size());
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView img_thumbnail;
        private TextView tv_name;
        private TextView tv_location;

        private ImageView[] stars;


        public ViewHolder(View v) {
            super(v);

            stars = new ImageView[5];
            stars[0] = (ImageView) itemView.findViewById(R.id.img_star1);
            stars[1] = (ImageView) itemView.findViewById(R.id.img_star2);
            stars[2] = (ImageView) itemView.findViewById(R.id.img_star3);
            stars[3] = (ImageView) itemView.findViewById(R.id.img_star4);
            stars[4] = (ImageView) itemView.findViewById(R.id.img_star5);

            img_thumbnail = (ImageView) itemView.findViewById(R.id.img_thumbnail);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_location = (TextView) itemView.findViewById(R.id.tv_location);

        }
    }
}
