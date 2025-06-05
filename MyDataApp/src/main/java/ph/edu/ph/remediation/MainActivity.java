package ph.edu.ph.remediation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ItemAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private ItemAdapter itemAdapter;
    private List<Item> itemList;
    private DatabaseHelper dbHelper;
    private UserSessionManager session;

    private static final int REQUEST_CODE_ADD_ITEM = 1;
    private static final int REQUEST_CODE_EDIT_ITEM = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = new UserSessionManager(this);
        if (!session.isLoggedIn()) {
            // Redirect to login if not logged in
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        itemList = new ArrayList<>();
        itemAdapter = new ItemAdapter(itemList, this);
        recyclerView.setAdapter(itemAdapter);

        loadItems();

        FloatingActionButton fab = findViewById(R.id.fabAddItem);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ItemDetailActivity.class);
                startActivityForResult(intent, REQUEST_CODE_ADD_ITEM);
            }
        });
    }

    private void loadItems() {
        itemList.clear();
        itemList.addAll(dbHelper.getAllItems());
        itemAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(int position) {
        Item item = itemList.get(position);
        Intent intent = new Intent(MainActivity.this, ItemDetailActivity.class);
        intent.putExtra("itemId", item.getId());
        startActivityForResult(intent, REQUEST_CODE_EDIT_ITEM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            loadItems();
        }
    }
}
