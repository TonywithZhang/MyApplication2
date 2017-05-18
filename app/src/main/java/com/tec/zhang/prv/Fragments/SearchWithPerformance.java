package com.tec.zhang.prv.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import com.tec.zhang.prv.R;
import com.tec.zhang.prv.recyler.Item;
import com.tec.zhang.prv.recyler.ItemAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import static com.tec.zhang.prv.R.id.check_now;

/**
 * Created by zhang on 2017/4/27.
 */

public class SearchWithPerformance extends Fragment {
    private View view;
    private TextView textView;
    private MultiAutoCompleteTextView editText;
    private Button searchWithSound;
    private RecyclerView recyclerView;
    private FloatingActionButton confirm;

    private int[] cars= new int[]{
            R.drawable.ic_baoma,
            R.drawable.ic_asmd,
            R.drawable.ic_benzi,
            R.drawable.ic_chv,
            R.drawable.ic_eb,
            R.drawable.ic_buzhiming
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.search_with_performance,container,false);
        init();
        setList();
        return view;
    }

    private void init() {
        textView = (TextView) view.findViewById(R.id.textView4);
        editText = (MultiAutoCompleteTextView) view.findViewById(R.id.check_by_performance);
        searchWithSound = (Button) view.findViewById(R.id.performance_listen);
        recyclerView = (RecyclerView) view.findViewById(R.id.performance_list);
        confirm = (FloatingActionButton) view.findViewById(check_now);
    }
    private void setList(){
        List<Item> list = createData();
        ItemAdapter adapter = new ItemAdapter(getContext(),list);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
    }
    private List<Item> createData(){
        List<Item> list = new ArrayList<>();
        Random random = new Random(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.CHINA);
        for (int i = 0 ; i< 20 ; i ++){
            Item item = new Item("" + System.currentTimeMillis(),random.nextInt(50)+ "","" + format.format(new Date(random.nextLong())),cars[random.nextInt(5)]);
            list.add(item);
        }
        return list;
    }
}
