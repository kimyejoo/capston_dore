package com.foodtruck.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.foodtruck.R;
import com.foodtruck.utils.Constants;
import com.foodtruck.utils.LogUtil;
import com.foodtruck.utils.StringUtil;
import com.foodtruck.vo.StoreMenuVo;
import com.foodtruck.vo.StoreReplyVo;
import com.foodtruck.vo.StoreVo;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * Created by evilstorm on 2017. 11. 6..
 */

public class TruckInfoActivity extends Activity {

    private StoreVo storeVo;
    private ImageView img_truck;
    private TextView tv_truck_name;

    private TextView[] tv_menus;
    private TextView[] tv_menus_bg;

    private RecyclerView listView;
    private Adapter adapter;
    private GridLayoutManager gManager;

    private ScrollView lay_info;
    private TextView tv_owner_say;
    private TextView tv_runtime;
    private TextView tv_holiday;
    private TextView tv_location;

    private String[] days = new String[]{"일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일"};

    private LinearLayout lay_reply;
    private LinearLayout lay_replys;
    private TextView tv_rating;
    private ImageView[] img_reply_star = new ImageView[5];

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.truck_info_activity);

        storeVo = (StoreVo) getIntent().getSerializableExtra("data");

        img_truck = (ImageView) findViewById(R.id.img_truck);
        tv_truck_name = (TextView) findViewById(R.id.tv_truck_name);

        tv_menus = new TextView[3];
        tv_menus[0] = (TextView) findViewById(R.id.tv_menu);
        tv_menus[1] = (TextView) findViewById(R.id.tv_info);
        tv_menus[2] = (TextView) findViewById(R.id.tv_reply);

        tv_menus_bg = new TextView[3];
        tv_menus_bg[0] = (TextView) findViewById(R.id.tv_menu_bg);
        tv_menus_bg[1] = (TextView) findViewById(R.id.tv_info_bg);
        tv_menus_bg[2] = (TextView) findViewById(R.id.tv_reply_bg);


        listView = (RecyclerView) findViewById(R.id.listView);
        lay_info = (ScrollView) findViewById(R.id.lay_info);

        tv_owner_say = (TextView) findViewById(R.id.tv_owner_say);
        tv_runtime = (TextView) findViewById(R.id.tv_runtime);
        tv_holiday = (TextView) findViewById(R.id.tv_holiday);
        tv_location = (TextView) findViewById(R.id.tv_location);

        for (int i=0; i<3; i++) {
            tv_menus[i].setTag(i);
            tv_menus[i].setOnClickListener(menuClickListener);
            tv_menus_bg[i].setTag(i);
            tv_menus_bg[i].setOnClickListener(menuClickListener);
        }


        Glide.with(TruckInfoActivity.this)
                .load(storeVo.getImg())
                .into(img_truck);

        tv_truck_name.setText(storeVo.getName());


        listView = (RecyclerView) findViewById(R.id.listView);
        gManager = new GridLayoutManager(this, 2);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(TruckInfoActivity.this, R.dimen.d5);
        listView.addItemDecoration(itemDecoration);
        listView.setLayoutManager(gManager);
        adapter = new Adapter(storeVo.getMenus());
        listView.setAdapter(adapter);

        lay_reply = (LinearLayout) findViewById(R.id.lay_reply);
        lay_replys = (LinearLayout) findViewById(R.id.lay_replys);
        img_reply_star[0] = (ImageView) findViewById(R.id.img_reply_star1);
        img_reply_star[1] = (ImageView) findViewById(R.id.img_reply_star2);
        img_reply_star[2] = (ImageView) findViewById(R.id.img_reply_star3);
        img_reply_star[3] = (ImageView) findViewById(R.id.img_reply_star4);
        img_reply_star[4] = (ImageView) findViewById(R.id.img_reply_star5);

        tv_rating = (TextView) findViewById(R.id.tv_rating);
        setReplysInfo();

        findViewById(R.id.ibt_add_reply).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addReply();
            }
        });

        selectedMenu(0);

    }

    private void setReplysInfo() {
        if(storeVo.getReplys() == null || storeVo.getReplys().size() == 0) {
            return;
        }


        ArrayList<StoreReplyVo> replys = storeVo.getReplys();


        int totalRate = 0;

        for (StoreReplyVo data : replys ) {
            totalRate += data.getRate();
            LogUtil.d(" Rate : " + data.getRate());
            addReplyView(data);
        }



        setStars(img_reply_star, totalRate/replys.size());
        tv_rating.setText(String.format("%.1f", (double)totalRate/replys.size()));

    }

    private void addReplyView(StoreReplyVo reply) {
        View baseView = getLayoutInflater().inflate(R.layout.row_reply_review, null);
        ImageView[] img_reply_star = new ImageView[5];
        img_reply_star[0] = (ImageView) baseView.findViewById(R.id.img_star1);
        img_reply_star[1] = (ImageView) baseView.findViewById(R.id.img_star2);
        img_reply_star[2] = (ImageView) baseView.findViewById(R.id.img_star3);
        img_reply_star[3] = (ImageView) baseView.findViewById(R.id.img_star4);
        img_reply_star[4] = (ImageView) baseView.findViewById(R.id.img_star5);

        setStars(img_reply_star, reply.getRate());

        TextView tv_date = (TextView) baseView.findViewById(R.id.tv_date);
        TextView tv_say = (TextView) baseView.findViewById(R.id.tv_say);

        tv_date.setText(StringUtil.getCalculateTime(reply.getCreated_at()));
        tv_say.setText(reply.getSay());

        lay_replys.addView(baseView);

    }

    private void setStars(ImageView[] img, double rate) {
        LogUtil.e(" rate:  " + rate);
        for (int i=0; i<img.length; i++) {
            if( i < rate) {
                img[i].setSelected(true);
            } else {
                img[i].setSelected(false);
            }
        }

    }

    private View.OnClickListener menuClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            selectedMenu((int)view.getTag());
        }
    };

    private void selectedMenu(int pos) {

        for (int i=0; i<3; i++){
            tv_menus[i].setSelected(false);
            tv_menus_bg[i].setSelected(false);
        }

        tv_menus[pos].setSelected(true);
        tv_menus_bg[pos].setSelected(true);

        switch (pos) {
            case 0 :
                updateMenu();
                break;
            case 1 :
                updateTruckInfo();
                break;
            case 2 :
                updateReplys();
                break;
        }
    }

    private void updateReplys() {

        listView.setVisibility(View.GONE);
        lay_info.setVisibility(View.GONE);
        lay_reply.setVisibility(View.VISIBLE);






    }

    private void addReply() {

    }

    private void updateMenu() {

        listView.setVisibility(View.VISIBLE);
        lay_info.setVisibility(View.GONE);
        lay_reply.setVisibility(View.GONE);

    }

    public class ItemOffsetDecoration extends RecyclerView.ItemDecoration {

        private int mItemOffset;

        public ItemOffsetDecoration(int itemOffset) {
            mItemOffset = itemOffset;
        }

        public ItemOffsetDecoration(@NonNull Context context, @DimenRes int itemOffsetId) {
            this(context.getResources().getDimensionPixelSize(itemOffsetId));
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(mItemOffset, mItemOffset , mItemOffset, mItemOffset);
        }
    }


    public class Adapter extends RecyclerView.Adapter<ViewHolder> {
        private ArrayList<StoreMenuVo> list;
        DecimalFormat formatter = new DecimalFormat("#,###");

        public Adapter(ArrayList<StoreMenuVo> list) {
            this.list = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_menu, parent, false);
            ViewHolder vh = new ViewHolder(v);

            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.tv_info.setText(list.get(position).getName() + "\n" + formatter.format(list.get(position).getPrice()) + "원");

            Glide.with(TruckInfoActivity.this)
                    .load(list.get(position).getImg())
                    .into(holder.img_thumbnail);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView img_thumbnail;
        private TextView tv_info;


        public ViewHolder(View v) {
            super(v);

            img_thumbnail = (ImageView) itemView.findViewById(R.id.img_thumbnail);
            tv_info = (TextView) itemView.findViewById(R.id.tv_info);

        }
    }

    private void updateTruckInfo() {

        listView.setVisibility(View.GONE);
        lay_info.setVisibility(View.VISIBLE);
        lay_reply.setVisibility(View.GONE);

        if(storeVo.getInfo() == null || storeVo.getInfo().getSay() == null) {
           return;
        }
        tv_owner_say.setText(storeVo.getInfo().getSay());
        tv_runtime.setText(storeVo.getInfo().getsDate() +":00 ~ " + storeVo.getInfo().geteDate() + ":00");
        tv_location.setText(storeVo.getLocation());
        tv_holiday.setText(getHoliday(storeVo.getInfo().getHolidays()));

    }

    private String getHoliday(String holiday) {
        String[] holidays = holiday.split(",");
        StringBuffer result = new StringBuffer();

        for (int i=0; i<holidays.length; i++) {
            if("1".equals(holidays[i])) {
                result.append(days[i]);
                result.append(" ");
            }
        }
        return result.toString();
    }


}
