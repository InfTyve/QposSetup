package com.android.setup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.util.ArrayList;
import java.util.List;

/** 标准折叠标题栏：随列表滚动自动折叠（对照演示） */
public class StandardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_standard);

        Toolbar toolbar = findViewById(R.id.action_bar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(getString(R.string.app_name));

        List<String> items = new ArrayList<>();
        for (int i = 1; i <= 40; i++) {
            items.add("列表项 " + i);
        }

        RecyclerView list = findViewById(R.id.list);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(new SimpleAdapter(items));
    }

    /** 返回按钮点击：返回上一页 */
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private static class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.VH> {

        private final List<String> items;

        SimpleAdapter(List<String> items) {
            this.items = items;
        }

        static class VH extends RecyclerView.ViewHolder {
            final TextView text;

            VH(View view) {
                super(view);
                text = view.findViewById(android.R.id.text1);
            }
        }

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
            return new VH(view);
        }

        @Override
        public void onBindViewHolder(VH holder, int position) {
            holder.text.setText(items.get(position));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }
}