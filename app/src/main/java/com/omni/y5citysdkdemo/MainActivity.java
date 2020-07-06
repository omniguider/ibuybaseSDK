package com.omni.y5citysdkdemo;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.omni.y5citysdk.Y5CitySDKActivity;

public class MainActivity extends AppCompatActivity {

    private static final String ARG_KEY_USER_ID = "arg_key_user_id";
    private static final String ARG_KEY_USER_NAME = "arg_key_user_name";
    private static final String ARG_KEY_PROJECT_ID = "arg_key_project_id";

    private EditText user_id_et;
    private EditText user_name_et;
    private EditText project_id_et;
    private TextView gotoSDK;
    private String user_id;
    private String user_name;
    private String project_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user_id_et = findViewById(R.id.activity_main_user_id_et);
        user_name_et = findViewById(R.id.activity_main_user_name_et);
        project_id_et = findViewById(R.id.activity_main_project_id_et);
        gotoSDK = findViewById(R.id.activity_main_to_sdk_tv);

        user_id_et.setText("10215018434602469");
        user_name_et.setText("Christine Chiu");

        gotoSDK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                user_id = user_id_et.getText().toString();
                user_name = user_name_et.getText().toString();
                project_id = project_id_et.getText().toString();

                Intent intent = new Intent(MainActivity.this, Y5CitySDKActivity.class);
                intent.putExtra(ARG_KEY_USER_ID, user_id);
                intent.putExtra(ARG_KEY_USER_NAME, user_name);
                intent.putExtra(ARG_KEY_PROJECT_ID, project_id);
                startActivity(intent);
            }
        });
    }
}
