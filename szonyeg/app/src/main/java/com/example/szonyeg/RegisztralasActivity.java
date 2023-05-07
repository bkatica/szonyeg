package com.example.szonyeg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisztralasActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String LOG_TAG = RegisztralasActivity.class.getName();
    private static final String PREF_KEY = RegisztralasActivity.class.getPackage().toString();

    private static final int SECRET_KEY = 9876;

    EditText uNevet;
    EditText uEmailet;
    EditText uJelszoet;
    EditText uJelszoMegerositeset;
    EditText uTeloszamet;
    Spinner uTeloFajtas;
    EditText uPostacimet;
    RadioGroup uElVasr;

    private SharedPreferences preferences;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regisztralas);

        int secret_key = getIntent().getIntExtra("SECRET_KEY", 0);
        if (secret_key != 9876){
            finish();
        }

        uNevet = findViewById(R.id.uetNev);
        uEmailet = findViewById(R.id.uetEmail);
        uJelszoet = findViewById(R.id.uetJelszo);
        uJelszoMegerositeset = findViewById(R.id.uetJelszoMegerosites);
        uTeloszamet = findViewById(R.id.uetTeloSzam);
        uTeloFajtas = findViewById(R.id.usTeloFajta);
        uPostacimet = findViewById(R.id.uetPostacim);
        uElVasr = findViewById(R.id.urElVas);
        uElVasr.check(R.id.urVasarlo);

        preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        String felhasznalonev = preferences.getString("felhasznalonev", "");
        String jelszo = preferences.getString("jelszo", "");

        uEmailet.setText(felhasznalonev);
        uJelszoet.setText(jelszo);
        uJelszoMegerositeset.setText(jelszo);

        uTeloFajtas.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.teloFajta, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        uTeloFajtas.setAdapter(adapter);

        mAuth = FirebaseAuth.getInstance();

        Log.i(LOG_TAG, "onCreate");
    }

    public void megsem(View view) {
        finish();
    }


    public void regisztralas(View view) {
        String felhnev = uNevet.getText().toString();
        String email = uEmailet.getText().toString();
        String jlsz = uJelszoet.getText().toString();
        String jlszMeg = uJelszoMegerositeset.getText().toString();

        if(!jlsz.equals(jlszMeg)){
            Log.e(LOG_TAG, "A két jelszó nem egyezik meg.");
            return;
        }

        String teloszam = uTeloszamet.getText().toString();
        String telofajta = uTeloFajtas.getSelectedItem().toString();
        String postacim = uPostacimet.getText().toString();

        int elVas = uElVasr.getCheckedRadioButtonId();
        RadioButton radiobutton = uElVasr.findViewById(elVas);
        String uElVasr = radiobutton.getText().toString();

        //logolas
        Log.i(LOG_TAG, "Regisztralt: " + felhnev + ": " + email);
        // TODO.
        // vasarlasKezdese();
        mAuth.createUserWithEmailAndPassword(email,jlsz).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d(LOG_TAG, "Felhasznalo sikeresen letrehozva");
                    vasarlasKezdese();
                    bejel();
                } else{
                    Log.d(LOG_TAG, "Felhasznalo nem lett letrehozva");
                    Toast.makeText(RegisztralasActivity.this, "Felhasznalo nem lett letrehozva"+ task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void vasarlasKezdese(/* registered user data */){
        Intent intent = new Intent(this, SzonyegekActivity.class);
        //intent.putExtra("SECRET_KEY", SECRET_KEY);
        startActivity(intent);
    }

    private void bejel(/* registered user data */){
        Intent intent = new Intent(this, MainActivity.class);
        //intent.putExtra("SECRET_KEY", SECRET_KEY);
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
        editor.putString("email", uEmailet.getText().toString());
        editor.apply();
        Log.i(LOG_TAG, "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "onResume");
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String kivalasztott = parent.getItemAtPosition(position).toString();
        Log.i(LOG_TAG, kivalasztott);
        //TODO
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //TODO
    }
}