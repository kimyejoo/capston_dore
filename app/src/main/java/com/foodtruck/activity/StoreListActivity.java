package com.foodtruck.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.foodtruck.R;
import com.foodtruck.network.NetworkResponse;
import com.foodtruck.network.RequestApi;
import com.foodtruck.utils.LogUtil;
import com.foodtruck.vo.StoreInfoVo;
import com.foodtruck.vo.StoreListVo;
import com.foodtruck.vo.StoreVo;

import java.util.ArrayList;

import retrofit2.Call;

/**
 * Created by evilstorm on 2017. 11. 13..
 */

public class StoreListActivity extends Activity {

    private Button btn_add;

    private RecyclerView listView;
    private LinearLayoutManager lManager;

    private ArrayList<StoreVo> storeList = new ArrayList<>();
    private Adapter adapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_list);

        listView = (RecyclerView) findViewById(R.id.listView);
        lManager = new LinearLayoutManager(this);
        listView.setLayoutManager(lManager);

        adapter = new Adapter(storeList);

        listView.setAdapter(adapter);

        findViewById(R.id.btn_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StoreListActivity.this, AddTruckActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        storeList.clear();
        requestData();
    }

    private void requestData() {


        RequestApi.getInstance().getTruckList(new NetworkResponse<StoreListVo>() {
            @Override
            public void onSuccess(Call call,  StoreListVo clazz) {

                storeList.addAll(clazz.getData());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFail(Call call, String msg) {
                Toast.makeText(StoreListActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private View.OnClickListener listClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int itemPosition = listView.getChildLayoutPosition(view);
            StoreVo item = storeList.get(itemPosition);
            showModifyDialog(item);
        }
    };
    private View.OnLongClickListener deleteClick = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            int itemPosition = listView.getChildLayoutPosition(view);
            StoreVo item = storeList.get(itemPosition);
            deleteAlert(item);

            return true;
        }
    };

    private void deleteAlert(final StoreVo item) {
        new AlertDialog.Builder(StoreListActivity.this)
                .setMessage(" 트럭을 삭제 합니다. ")
                .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteTruck(item);
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

    private void deleteTruck(final StoreVo item) {
        RequestApi.getInstance().deleteTruck(item.get_id(), new NetworkResponse() {
            @Override
            public void onSuccess(Call call, Object clazz) {
                Toast.makeText(StoreListActivity.this, "삭제 되었습니다.", Toast.LENGTH_SHORT).show();
                storeList.remove(item);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFail(Call call, String msg) {

            }
        });
    }

    private void showModifyDialog(final StoreVo item) {
        new AlertDialog.Builder(this)
                .setMessage("수정 할 목록을 고르세용.")
                .setPositiveButton("뎃글", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(StoreListActivity.this, AddReplyActivity.class);
                        intent.putExtra("data", item);
                        intent.putExtra("truck_id", item.get_id());
                        startActivity(intent);
                    }
                })
                .setNegativeButton("메뉴", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(StoreListActivity.this, AddMenuActivity.class);
                        intent.putExtra("data", item);
                        intent.putExtra("truck_id", item.get_id());
                        startActivity(intent);

                    }
                })
                .setNeutralButton("설명", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(StoreListActivity.this, AddTruckInfoActivity.class);
                        intent.putExtra("data", item);
                        intent.putExtra("truck_id", item.get_id());
                        startActivity(intent);
                    }
                })
                .show()
        ;
    }


    public class Adapter extends RecyclerView.Adapter<ViewHolder> {
        private ArrayList<StoreVo> list;
        public Adapter(ArrayList<StoreVo> list) {
            this.list = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_store_list, parent, false);
            v.setOnClickListener(listClick);
            v.setOnLongClickListener(deleteClick);
            ViewHolder vh = new ViewHolder(v);

            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.tv_name.setText(list.get(position).getName());
            holder.tv_local.setText(list.get(position).getLocation());
            LogUtil.e(" Reply Count " + list.get(position).getReplys().size());
            Glide.with(StoreListActivity.this)
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
        private TextView tv_name;
        private TextView tv_local;


        public ViewHolder(View v) {
            super(v);

            img_thumbnail = (ImageView) itemView.findViewById(R.id.img_thumbnail);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_local = (TextView) itemView.findViewById(R.id.tv_local);

        }
    }

}
