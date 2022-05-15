package com.picassos.noted.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.picassos.noted.R;
import com.picassos.noted.sharedPreferences.SharedPref;
import com.picassos.noted.utils.Helper;

public class CreatePinActivity extends AppCompatActivity {

    private SharedPref sharedPref;

    private EditText pinCode;
    private Button pinCodeAction;
    private TextView length;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);

        super.onCreate(savedInstanceState);

        // OPTIONS
        Helper.dark_mode(this);
        Helper.fullscreen_mode(this);
        Helper.screen_state(this);

        setContentView(R.layout.activity_create_pin);

        // return back and finish activity
        ImageView goBack = findViewById(R.id.go_back);
        goBack.setOnClickListener(v -> finish());

        // pin code
        pinCode = findViewById(R.id.pin_code);
        pinCode.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        pinCode.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
        pinCode.addTextChangedListener(pinCodeTextWatcher);
        pinCodeAction = findViewById(R.id.pin_code_action);

        // length
        length = findViewById(R.id.length);

        // pin code is not set
        // set a new pin code
        pinCodeAction.setText(getString(R.string.save));
        pinCodeAction.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(pinCode.getText().toString())) {
                sharedPref.setNotePinCode(Integer.parseInt(pinCode.getText().toString()));
                Toast.makeText(this, getString(R.string.pin_code_set), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.putExtra("result", "lock");
                setResult(Activity.RESULT_OK, intent);
            } else {
                Toast.makeText(this, getString(R.string.pin_code_empty), Toast.LENGTH_SHORT).show();
            }
            finish();
        });

    }

    /**
     * text watcher enables action
     * button when pin code field not empty
     */
    private final TextWatcher pinCodeTextWatcher = new TextWatcher() {
        @SuppressLint("SetTextI18n")
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            length.setText(s.length() + "/8");
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // Set length while text changing
            length.setText(s.length() + "/8");

            pinCodeAction.setEnabled(s.length() == 8);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(CreatePinActivity.this, MainActivity.class));
    }
}