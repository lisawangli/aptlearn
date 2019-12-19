package com.example.apt_test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.apt_annotation.BindView;
import com.example.apt_annotation.onClick;
import com.example.apt_library.BindViewTools;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tv1)
    TextView tv1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BindViewTools.bind(this);
        tv1.setText("注解获取成功");
    }

    @onClick(R.id.tv1)
    public void onClick(){
        Toast.makeText(this,"=====tvClick=====",Toast.LENGTH_LONG).show();
    }
}
