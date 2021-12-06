package com.foodorder.smartfoodordering;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private String phone;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button buttonLogin = findViewById(R.id.button_do_register);
        EditText etPhone = findViewById(R.id.et_phone_register);
        EditText etPassword = findViewById(R.id.et_password_register);
        Button buttonRegister = findViewById(R.id.button_register);
        buttonLogin.setOnClickListener(v -> {
            phone = etPhone.getText().toString();
            password = etPassword.getText().toString();
            if (password.length() == 0 && phone.length() == 0) {
                Toast.makeText(this, "Please enter details", Toast.LENGTH_SHORT).show();
                return;
            }
            if (password.length() == 0) {
                Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
                return;
            }
            if (phone.length() == 0) {
                Toast.makeText(this, "Please enter phone number", Toast.LENGTH_SHORT).show();
                return;
            }
            if (phone.length() != 10) {
                Toast.makeText(this, "Invalid Phone Number", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            intent.putExtra("phone", phone);
            intent.putExtra("password", password);
            startActivity(intent);
        });
        buttonRegister.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), RegisterActivity.class)));

    }
}