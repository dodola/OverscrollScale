package com.dodola.listview.ext;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.Toolbar;

import com.dodola.listview.extlib.RecyclerViewExt;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewActivity extends AppCompatActivity {

    RecyclerViewExt recyclerView;
    List<String> mData = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activicy_recyclerview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("RecyclerViewEdgeScale");

        for (int i = 0; i < 25; i++) {
            mData.add(String.valueOf(i));
        }

        recyclerView = (RecyclerViewExt) findViewById(R.id.recycler_view);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        recyclerView.setHasFixedSize(true);
        RecyclerViewAdapter mAdapter = new RecyclerViewAdapter(mData);
        recyclerView.setAdapter(mAdapter);

    }
}
