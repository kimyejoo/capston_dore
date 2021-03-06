package com.foodtruck.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.foodtruck.R;
import com.foodtruck.network.NetworkResponse;
import com.foodtruck.network.RequestApi;
import com.foodtruck.utils.StringUtil;
import com.foodtruck.vo.ResponseVoBase;
import com.foodtruck.vo.StoreVo;
import com.foodtruck.vo.UpdateVo;
import com.google.gson.JsonObject;

import retrofit2.Call;

/**
 * Created by evilstorm on 2017. 11. 14..
 */

public class AddTruckInfoActivity extends Activity {

    private String truck_id;
    private String info_id;
    private ToggleButton[] holiyday;
    private EditText et_eDate;
    private EditText et_sDate;
    private EditText et_say;
    private StoreVo storeVo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_truck_info);

        truck_id = getIntent().getStringExtra("truck_id");
        storeVo  = (StoreVo) getIntent().getSerializableExtra("data");

        et_say = (EditText) findViewById(R.id.et_say);
        et_eDate = (EditText) findViewById(R.id.et_eDate);
        et_sDate = (EditText) findViewById(R.id.et_sDate);

        holiyday = new ToggleButton[7];
        holiyday[0] = (ToggleButton) findViewById(R.id.btn_sun);
        holiyday[1] = (ToggleButton) findViewById(R.id.btn_mon);
        holiyday[2] = (ToggleButton) findViewById(R.id.btn_tue);
        holiyday[3] = (ToggleButton) findViewById(R.id.btn_wed);
        holiyday[4] = (ToggleButton) findViewById(R.id.btn_thu);
        holiyday[5] = (ToggleButton) findViewById(R.id.btn_fri);
        holiyday[6] = (ToggleButton) findViewById(R.id.btn_sat);

        findViewById(R.id.btn_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isPatch) {
                    patchInfo();
                } else {
                    addInfo();
                }

            }
        });

        restorePrevValue(storeVo);
    }

    private boolean isPatch = false;
    private void restorePrevValue(StoreVo storeVo) {
        if(storeVo == null || storeVo.getInfo() == null || storeVo.getInfo().get_id() == null){
            return;
        }

        isPatch = true;
        info_id = storeVo.getInfo().get_id();

        et_eDate.setText(storeVo.getInfo().geteDate() + "");
        et_sDate.setText(storeVo.getInfo().getsDate() + "");
        et_say.setText(storeVo.getInfo().getSay());

        setHoliday(storeVo.getInfo().getHolidays());
    }

    private boolean isValidation() {
        if(StringUtil.isNull(et_say)) {
            Toast.makeText(this, "한마디 입력해요.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(StringUtil.isNull(et_sDate)) {
            Toast.makeText(this, "시작 시간 입력해요.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(StringUtil.isNull(et_eDate)) {
            Toast.makeText(this, "종료 시간 입력해요.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
    private void patchInfo() {

        if(!isValidation()) {
            return;
        }
        JsonObject params = new JsonObject();
        params.addProperty("truck_id", truck_id);
        params.addProperty("say", StringUtil.getString(et_say));
        params.addProperty("sDate", StringUtil.getString(et_sDate));
        params.addProperty("eDate", StringUtil.getString(et_eDate));
        params.addProperty("holidays", getHolidays());

        RequestApi.getInstance().patchTruckInfo(info_id, params, new NetworkResponse<ResponseVoBase>() {
            @Override
            public void onSuccess(Call call, ResponseVoBase clazz) {
                finish();
            }

            @Override
            public void onFail(Call call, String msg) {
                Toast.makeText(AddTruckInfoActivity.this, " error : " + msg, Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void addInfo() {

        if(!isValidation()) {
            return;
        }

        JsonObject params = new JsonObject();
        params.addProperty("truck_id", truck_id);
        params.addProperty("say", StringUtil.getString(et_say));
        params.addProperty("sDate", StringUtil.getString(et_sDate));
        params.addProperty("eDate", StringUtil.getString(et_eDate));
        params.addProperty("holidays", getHolidays());

        RequestApi.getInstance().postTruckInfo(truck_id, params, new NetworkResponse<UpdateVo>() {
            @Override
            public void onSuccess(Call call, UpdateVo clazz) {
                launchAddMenu();
            }

            @Override
            public void onFail(Call call, String msg) {
                Toast.makeText(AddTruckInfoActivity.this, " error : " + msg, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void launchAddMenu() {
        Intent intent = new Intent(this, AddMenuActivity.class);
        intent.putExtra("truck_id", truck_id);
        startActivity(intent);
        finish();

    }

    private String getHolidays() {
        StringBuffer sb = new StringBuffer();
       for (int i=0; i<7; i++){
           sb.append(holiyday[i].isChecked() ? "1": "0");
           sb.append(",");
       }
       if(sb.length() > 1 ) {
           sb.delete(sb.length()-1, sb.length());
       }
        return sb.toString();
    }

    private void setHoliday(String holiday) {
        String[] holidays = holiday.split(",");

        for (int i=0; i<holidays.length; i++) {
            if("1".equals(holidays[i])) {
                holiyday[i].setChecked(true);
            }
        }

    }

}
