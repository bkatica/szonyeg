package com.example.szonyeg;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;



import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SzonyegekActivity extends AppCompatActivity {

    private static final String LOG_TAG = SzonyegekActivity.class.getName();
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private RecyclerView mRecyclerView;
    private ArrayList<ShopingItem> mItemList;
    private ShopingItemAdapter mAdapter;
    private int gridNumber = 1;
    private int cartItems = 0;
    private boolean nezetSor=true;

    private FrameLayout kekKor;
    private TextView counttv;
    private Integer itemLimit = 5;

    private Notif mNotificationHelper;
    //private AlarmManager mAlarmManager;
    private FirebaseFirestore mFirestore;
    //private JobScheduler mJobScheduler;
    //private SharedPreferences preferences;

    private CollectionReference mItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_szonyegek);
        mAuth = FirebaseAuth.getInstance();
        //mAuth.signOut();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            Log.d(LOG_TAG, "Authentikalt felhasznalo");
        }else{
            Log.d(LOG_TAG, "Nem authentikalt felhasznalo");
            finish();
        }

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, gridNumber));
        mItemList = new ArrayList<>();
        mAdapter = new ShopingItemAdapter(this, mItemList);

        mRecyclerView.setAdapter(mAdapter);

        adatInicializalas();

    }

    private void adatInicializalas() {
        String[] itemList = getResources().getStringArray(R.array.shopping_item_nevek);
        String[] itemInfo = getResources().getStringArray(R.array.shopping_item_infok);
        String[] itemPrice = getResources().getStringArray(R.array.shopping_item_arak);
        TypedArray itemsImageResource = getResources().obtainTypedArray(R.array.shopping_item_kepek);
        mItemList.clear();
        for(int i=0; i<itemList.length;i++){
            mItemList.add(new ShopingItem(itemList[i], itemInfo[i], itemPrice[i], itemsImageResource.getResourceId(i, 0),0));
        }

        itemsImageResource.recycle();
        mAdapter.notifyDataSetChanged();
    }

    private void queryData() {
        mItemList.clear();
        mItems.orderBy("cartedCount", Query.Direction.DESCENDING).limit(itemLimit).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        ShopingItem item = document.toObject(ShopingItem.class);
                        item.setId(document.getId());
                        mItemList.add(item);
                    }

                    if (mItemList.size() == 0) {
                        adatInicializalas();
                        queryData();
                    }

                    // Notify the adapter of the change.
                    mAdapter.notifyDataSetChanged();
                });
    }

    public void deleteItem(ShopingItem item) {
        DocumentReference ref = mItems.document(item.getId());
        ref.delete()
                .addOnSuccessListener(success -> {
                    Log.d(LOG_TAG, "Item is successfully deleted: " + item.getId());
                })
                .addOnFailureListener(fail -> {
                    Toast.makeText(this, "Item " + item.getId() + " cannot be deleted.", Toast.LENGTH_LONG).show();
                });

        queryData();
        mNotificationHelper.cancel();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.szonyeg_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.kereses);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String string) {
                Log.d(LOG_TAG, string);
                mAdapter.getFilter().filter(string);
                return false;
            }

        });
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.log_out_button){
            Log.d(LOG_TAG, "Kijelentkezes!");
            FirebaseAuth.getInstance().signOut();
            finish();
            return true;
        }else if(item.getItemId() == R.id.setting){
            Log.d(LOG_TAG, "Setting!");
            FirebaseAuth.getInstance().signOut();
            finish();
            return true;
        }else if(item.getItemId() == R.id.cart){
            Log.d(LOG_TAG, "Cart!");
            return true;
        }else if(item.getItemId() == R.id.nezet){
            if (nezetSor) {
                changeSpanCount(item, R.drawable.baseline_view_stream_24, 1);
            } else {
                changeSpanCount(item, R.drawable.baseline_view_module_24, 2);
            }
            return true;
        } else{
            return super.onOptionsItemSelected(item);
        }
    }

    private void changeSpanCount(MenuItem item, int drawableId, int spanCount) {
        nezetSor = !nezetSor;
        item.setIcon(drawableId);
        GridLayoutManager layoutManager = (GridLayoutManager) mRecyclerView.getLayoutManager();
        layoutManager.setSpanCount(spanCount);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem alertMenuItem = menu.findItem(R.id.cart);
        FrameLayout rootView = (FrameLayout) alertMenuItem.getActionView();

        kekKor = (FrameLayout) rootView.findViewById(R.id.view_alert_red_circle);
        counttv = (TextView) rootView.findViewById(R.id.view_alert_count_textview);

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(alertMenuItem);
            }
        });
        return super.onPrepareOptionsMenu(menu);
    }

    public void updateAlertIcon() {
        cartItems = (cartItems + 1);
        if (0 < cartItems) {
            counttv.setText(String.valueOf(cartItems));
        } else {
            counttv.setText("");
        }

        kekKor.setVisibility((cartItems > 0) ? VISIBLE : GONE);
    }

}
