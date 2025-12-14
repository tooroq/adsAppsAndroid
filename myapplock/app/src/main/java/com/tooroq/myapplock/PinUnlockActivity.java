package com.tooroq.myapplock;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import android.content.Intent;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

public class PinUnlockActivity extends Activity {
    EditText etPin;
    ImageView imageView ;
    String targetPackage;

    String urlBanner = "https://tooroq.com/p/apps-google-play.html";
    private boolean exitedByHome = false;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle s){
        super.onCreate(s);
        setContentView(R.layout.activity_pin_unlock);
        setFinishOnTouchOutside(false);

        etPin = findViewById(R.id.etPinUnlock);
        imageView = findViewById(R.id.imgBanner) ;

        etPin.setFocusableInTouchMode(true);
        etPin.requestFocus();

        // تأخير بسيط لإظهار الكيبورد فوق التطبيقات الأخرى مثل واتساب
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(etPin, InputMethodManager.SHOW_FORCED);
        }, 150);

        String correctPin = Prefs.getPin(this);
        etPin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String entered = s.toString().trim();

                // إذا كانت كلمة السر مطابقة
                if (entered.equals(correctPin)) {
                    // نجاح
                    AppLockAccessibilityService.currentlyUnlockedPackage = targetPackage;
                    finish();
                }
            }
        });


        targetPackage = getIntent().getStringExtra("target");

        // إظهار لوحة المفاتيح مباشرة
        etPin.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(etPin, InputMethodManager.SHOW_IMPLICIT);

        getDataApp() ;

        imageView.setOnClickListener(v -> {
            startActivity(new Intent(Intent.ACTION_VIEW ,
                    Uri.parse( urlBanner ) ));
         });

    }

    @Override
    public void onBackPressed(){
        // لا نسمح بالرجوع لتطبيق مقفل → رجوع للهوم
        goHome();
    }

    private void goHome() {
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_HOME);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        exitedByHome = true;
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (!exitedByHome) {
            finish();
        }

        // إخفاء لوحة المفاتيح عند اختفاء النافذة
        hideKeyboard();
    }

    @Override
    protected void onResume() {
        super.onResume();
        exitedByHome = false;
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etPin.getWindowToken(), 0);
    }



    public void getDataApp() {

        // رابط JSON
        String url = "https://tooroq.github.io/adsAppsAndroid/myapplock.json";

        // إنشاء RequestQueue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        // إنشاء طلب JSON
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // جلب البيانات من JSON
                            String IMGBANNER = response.getString("BANNERIMG");
                            urlBanner = response.getString("URLAPP");
                            //  Toast.makeText(MainActivity.this, ""+app, Toast.LENGTH_SHORT).show();

                            Glide.with(PinUnlockActivity.this)
                                    .load(IMGBANNER)
                                    .placeholder(R.drawable.moba3tara) // صورة أثناء التحميل (اختياري)
                                    .error(R.drawable.moba3tara)         // صورة في حالة الخطأ (اختياري)
                                    .into(imageView);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(PinUnlockActivity.this, "Error parsing JSON", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //    Toast.makeText(MainActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                });

        // إضافة الطلب إلى قائمة الطلبات
        requestQueue.add(jsonObjectRequest);
    }





}
