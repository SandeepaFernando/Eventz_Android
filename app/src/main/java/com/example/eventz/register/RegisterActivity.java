package com.example.eventz.register;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.eventz.MainActivity;
import com.example.eventz.R;
import com.example.eventz.home.HomeActivity;

public class RegisterActivity extends AppCompatActivity {
    private Button venderReg, organizerReg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        venderReg = findViewById(R.id.buttonVenderReg);
        organizerReg = findViewById(R.id.buttonOrginizerReg);

        venderReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, RegVenderScrollingActivity.class);
                startActivity(intent);
            }
        });

        organizerReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, RegOrganizerScrollingActivity.class);
                startActivity(intent);
            }
        });

    }
}
