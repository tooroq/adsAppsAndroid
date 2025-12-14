package com.tooroq.myapplock;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    Button btnSave, btnApps, btnOverlay, btnAccessibility;

    @Override
    protected void onCreate(Bundle s){
        super.onCreate(s);
        setContentView(R.layout.activity_main);

        btnSave = findViewById(R.id.btnSave);
        btnApps = findViewById(R.id.btnApps);
        btnOverlay = findViewById(R.id.btnOverlay);
        btnAccessibility = findViewById(R.id.btnAccessibility);

        btnSave.setOnClickListener(v -> {
            showChangePinDialog() ;
        });

        btnApps.setOnClickListener(v -> startActivity(new Intent(this, AppListActivity.class)));

        btnOverlay.setOnClickListener(v -> {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            } else {
                Toast.makeText(this, "إذن الظهور مفعل", Toast.LENGTH_SHORT).show();
            }
        });

        btnAccessibility.setOnClickListener(v -> {
          showAccessibilityDialog() ;
        });

    }




    private void showAccessibilityDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("تفعيل خدمة الوصول");
        builder.setMessage("لتعمل ميزة قفل التطبيقات، يجب تفعيل خدمة الوصول لتطبيقنا." +
                "\nهل تريدين الانتقال إلى إعدادات Accessibility الآن؟ \n" +
                "ثم فعل التطبيق ضمن 'التطبيقات المثبتة'");

        // زر حسناً → الانتقال للإعدادات
        builder.setPositiveButton("حسناً", (dialog, which) -> {
            // يفتح صفحة إعدادات إمكانية الوصول لتمكين الخدمة
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

        // زر إلغاء
        builder.setNegativeButton("إلغاء", (dialog, which) -> dialog.dismiss());

        builder.setCancelable(false); // لا يمكن الإغلاق بالضغط خارج Dialog
        builder.show();
    }

    private void showChangePinDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("تغيير كلمة المرور");

        // إنشاء Layout ديناميكي
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        // EditText لإدخال كلمة المرور الجديدة
        final EditText etNewPin = new EditText(this);
        etNewPin.setHint("كلمة المرور الجديدة");
        etNewPin.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        layout.addView(etNewPin);

        // EditText لإعادة إدخال كلمة المرور
        final EditText etConfirmPin = new EditText(this);
        etConfirmPin.setHint("أعد إدخال كلمة المرور");
        etConfirmPin.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        layout.addView(etConfirmPin);

        builder.setView(layout);

        // زر حفظ
        builder.setPositiveButton("حفظ", null);

        // زر إلغاء
        builder.setNegativeButton("إلغاء", (dialog, which) -> dialog.dismiss());

        final AlertDialog dialog = builder.create();
        dialog.show();

        // تعديل زر الحفظ للتحقق قبل الإغلاق
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String newPin = etNewPin.getText().toString().trim();
            String confirmPin = etConfirmPin.getText().toString().trim();

            if (newPin.isEmpty() || confirmPin.isEmpty()) {
                Toast.makeText(this, "يرجى إدخال كلمة المرور", Toast.LENGTH_SHORT).show();
            } else if (!newPin.equals(confirmPin)) {
                Toast.makeText(this, "كلمة المرور غير متطابقة", Toast.LENGTH_SHORT).show();
            } else {
                // حفظ كلمة المرور في SharedPreferences
                Prefs.savePin(this, newPin);
                Toast.makeText(this, "تم تغيير كلمة المرور بنجاح", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }



}
