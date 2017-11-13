package com.foodtruck.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.foodtruck.R;

/**
 * Created by evilstorm on 2017. 10. 30..
 */

public class SearchActivity extends Activity {

    //최초 생성시 하는 일
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);

        final EditText et_search_word = (EditText) findViewById(R.id.et_search_word);
        ImageButton ibt_search = (ImageButton) findViewById(R.id.ibt_search);

        ibt_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //1. 검색어를 가져온다
                //2. SearchResult 화면으로 검색어를 넘긴다.
                Intent intent = new Intent(SearchActivity.this, SearchResultActivity.class);
                intent.putExtra("word", et_search_word.getText());
                startActivity(intent);
            }
        });

    }

    //화면에 나올때 마다 하는 일
    @Override
    protected void onResume() {
        super.onResume();
    }

    //화면에서 사라질 때 하는 일
    @Override
    protected void onPause() {
        super.onPause();
    }
}
