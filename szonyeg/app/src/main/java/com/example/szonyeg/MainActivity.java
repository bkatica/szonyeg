package com.example.szonyeg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getName();
    private static final String PREF_KEY = MainActivity.class.getPackage().toString();
    private static final int SECRET_KEY = 9876;

    EditText etFelhnev;
    EditText etJlsz;

    private SharedPreferences preferences;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etFelhnev = findViewById(R.id.editTextFelhasznaloNev);
        etJlsz = findViewById(R.id.editTextJelszo);

        preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        String email = preferences.getString("email", "");
        etFelhnev.setText(email);

        mAuth = FirebaseAuth.getInstance();

        Log.i(LOG_TAG, "onCreate");
    }

    public void bejelentkezni(View view) {
        //szoveg elkerese
        String felhnev = etFelhnev.getText().toString();
        String jlsz = etJlsz.getText().toString();

        //logolas
        //Log.i(LOG_TAG, "Bejelentkezett: " + felhnev + ": " + jlsz);
        mAuth.signInWithEmailAndPassword(felhnev, jlsz).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d(LOG_TAG, "Felhasznalo bejelentkezzett");
                    vasarlasKezdese();
                }else{
                    Log.d(LOG_TAG, "Felhasznalo nem jelentkezett be");
                    Toast.makeText(MainActivity.this, "Felhasznalo nem jelentkezett be"+ task.getException().getMessage(), Toast.LENGTH_LONG).show();

                }
            }
        });
    }

    public void vasarlasKezdese() {
        Intent intent = new Intent(this, SzonyegekActivity.class);
        startActivity(intent);
    }

    public void regisztralni(View view) {
        Intent intent = new Intent(this, RegisztralasActivity.class);
        intent.putExtra("SECRET_KEY", SECRET_KEY);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(LOG_TAG, "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(LOG_TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "onDestroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("felhasznalonev", etFelhnev.getText().toString());
        editor.putString("jelszo", etJlsz.getText().toString());
        editor.apply();

        Log.i(LOG_TAG, "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "onResume");
    }
}