package ph.edu.ph.remediation;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ItemDetailActivity extends AppCompatActivity {

    private EditText editTextTitle, editTextDescription;
    private Button buttonSave, buttonDelete;

    private DatabaseHelper dbHelper;
    private int itemId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        dbHelper = new DatabaseHelper(this);

        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDescription = findViewById(R.id.editTextDescription);
        buttonSave = findViewById(R.id.buttonSave);
        buttonDelete = findViewById(R.id.buttonDelete);

        if (getIntent() != null && getIntent().hasExtra("itemId")) {
            itemId = getIntent().getIntExtra("itemId", -1);
            loadItem(itemId);
        } else {
            buttonDelete.setVisibility(View.GONE);
        }

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveItem();
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDelete();
            }
        });
    }

    private void loadItem(int id) {
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(
                "SELECT * FROM " + DatabaseHelper.TABLE_ITEMS + " WHERE " + DatabaseHelper.COLUMN_ITEM_ID + "=?",
                new String[]{String.valueOf(id)});
        if (cursor != null && cursor.moveToFirst()) {
            String title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ITEM_TITLE));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ITEM_DESCRIPTION));
            editTextTitle.setText(title);
            editTextDescription.setText(description);
            cursor.close();
        }
    }

    private void saveItem() {
        String title = editTextTitle.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            editTextTitle.setError("Title is required");
            return;
        }

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_ITEM_TITLE, title);
        values.put(DatabaseHelper.COLUMN_ITEM_DESCRIPTION, description);

        if (itemId == -1) {
            // Insert new item
            long id = dbHelper.getWritableDatabase().insert(DatabaseHelper.TABLE_ITEMS, null, values);
            if (id > 0) {
                Toast.makeText(this, "Item added", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Failed to add item", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Update existing item
            int rows = dbHelper.getWritableDatabase().update(DatabaseHelper.TABLE_ITEMS, values,
                    DatabaseHelper.COLUMN_ITEM_ID + "=?", new String[]{String.valueOf(itemId)});
            if (rows > 0) {
                Toast.makeText(this, "Item updated", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Failed to update item", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Item")
                .setMessage("Are you sure you want to delete this item?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteItem();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    private void deleteItem() {
        int rows = dbHelper.getWritableDatabase().delete(DatabaseHelper.TABLE_ITEMS,
                DatabaseHelper.COLUMN_ITEM_ID + "=?", new String[]{String.valueOf(itemId)});
        if (rows > 0) {
            Toast.makeText(this, "Item deleted", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "Failed to delete item", Toast.LENGTH_SHORT).show();
        }
    }
}
