package com.example.android.inventoryapp;

import android.Manifest;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static com.example.android.inventoryapp.data.InventoryContract.InventoryEntry;


public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_PRODUCT_LOADER = 0;
    String quatityText;
    EditText quantityEditText;
    Button decreaseButton;
    Button increaseButton;
    private Uri mCurrentProductUri;
    private EditText mNameEditText;
    private EditText mPriceEditText;
    private EditText mSuplierNameEditText;
    private EditText mSuplierPhoneEditText;
    private Button order;
    private boolean mPorductHasChanged = false;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mPorductHasChanged = true;
            return false;
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();

        if (mCurrentProductUri == null) {
            setTitle(getString(R.string.editor_activity_title_add_product));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_product));
            getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
        }

        mNameEditText = (EditText) findViewById(R.id.edit_product_name);
        mPriceEditText = (EditText) findViewById(R.id.edit_product_price);
        mSuplierNameEditText = (EditText) findViewById(R.id.edit_product_supplier_name);
        mSuplierPhoneEditText = (EditText) findViewById(R.id.edit_product_supplier_phone);
        quantityEditText = (EditText) findViewById(R.id.edit_product_quantity);
        order = (Button) findViewById(R.id.supplierorder);

        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mSuplierNameEditText.setOnTouchListener(mTouchListener);
        mSuplierPhoneEditText.setOnTouchListener(mTouchListener);
        quantityEditText.setOnTouchListener(mTouchListener);

        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(EditorActivity.this, Manifest.permission.CALL_PHONE)) {
                    if (!mSuplierPhoneEditText.getText().toString().isEmpty()) {
                        String phone = mSuplierPhoneEditText.getText().toString();
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                        startActivity(intent);
                    }
                }


            }
        });

        decreaseButton = (Button) findViewById(R.id.decrease_quantity);
        decreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                decreasequantity();
                mPorductHasChanged = true;
            }
        });

        increaseButton = (Button) findViewById(R.id.increase_quantity);
        increaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                increasequantity();
                mPorductHasChanged = true;
            }
        });
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteProduct();
                Intent intent = new Intent(EditorActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void saveProduct() {

        String nameString = mNameEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String quantityString = quantityEditText.getText().toString().trim();
        String suplierNameString = mSuplierNameEditText.getText().toString().trim();
        String suplierphoneString = mSuplierPhoneEditText.getText().toString().trim();

        if (TextUtils.isEmpty(nameString)) {
            Toast.makeText(this, getString(R.string.empty_name),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(priceString)) {
            Toast.makeText(this, getString(R.string.empty_price),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(suplierNameString)) {
            Toast.makeText(this, getString(R.string.empty_supplier_name),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a ContentValues object
        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_PRODUCT_NAME, nameString);
        values.put(InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NAME, suplierNameString);

        int price = 0;
        if (TextUtils.isEmpty(priceString)) {
            Toast.makeText(this, getString(R.string.empty_price),
                    Toast.LENGTH_SHORT).show();
            return;
        } else {
            price = Integer.parseInt(priceString);
        }
        values.put(InventoryEntry.COLUMN_PRODUCT_PRICE, price);

        int quantity = 0;
        if (TextUtils.isEmpty(quantityString)) {
            Toast.makeText(this, getString(R.string.empty_quantity),
                    Toast.LENGTH_SHORT).show();
            return;
        } else {
            quantity = Integer.parseInt(quantityString);
        }
        values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, quantity);
        int phonNumber = 0;
        if (TextUtils.isEmpty(suplierphoneString)) {
            Toast.makeText(this, getString(R.string.empty_supplier_phone),
                    Toast.LENGTH_SHORT).show();
            return;
        } else {
            phonNumber = Integer.parseInt(suplierphoneString);
        }
        values.put(InventoryEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER, phonNumber);

        if (mCurrentProductUri == null) {
            Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, getString(R.string.editor_insert_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_insert_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentProductUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.editor_update_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_update_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    private void deleteProduct() {
        if (mCurrentProductUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void decreasequantity() {
        quantityEditText = (EditText) findViewById(R.id.edit_product_quantity);
        quatityText = quantityEditText.getText().toString();
        Log.v("quatity", "" + quatityText.toString());
        int quatity = 0;
        if (quatityText.isEmpty()) {
            quatity = 0;
            return;
        } else if (quatityText.equals("")) {
            quatityText = "0";
        }
        try {
            quatity = Integer.parseInt(quatityText);
            if (quatity == 0) {
                return;
            }
            quatity--;
            if (quatity >= 1) {
                quantityEditText.setText(String.valueOf(quatity));
            } else if (quatity < 1) {
                quatity = 1;
                quantityEditText.setText(String.valueOf(quatity));
            }
        } catch (NumberFormatException e) {
            System.out.println("not a number");
        }

    }

    private void increasequantity() {
        quantityEditText = (EditText) findViewById(R.id.edit_product_quantity);
        quatityText = quantityEditText.getText().toString();
        int quatity;
        if (quatityText.isEmpty()) {
            quatity = 0;
        }
        try {
            quatity = Integer.parseInt(quatityText);
            quatity++;
            if (quatity >= 1) {
                quantityEditText.setText(String.valueOf(quatity));
            } else if (quatity < 1) {
                quatity = 1;
                quantityEditText.setText(String.valueOf(quatity));
            }
        } catch (NumberFormatException e) {
            System.out.println("not a number");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveProduct();
                finish();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();

                return true;
            case android.R.id.home:
                if (!mPorductHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryEntry.COLUMN_PRODUCT_PRICE,
                InventoryEntry.COLUMN_PRODUCT_QUANTITY,
                InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NAME,
                InventoryEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER
        };
        return new CursorLoader(this,
                InventoryEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER);

            String name = cursor.getString(nameColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            int supplierPhone = cursor.getInt(supplierPhoneColumnIndex);

            mNameEditText.setText(name);
            mPriceEditText.setText(Integer.toString(price));
            quantityEditText.setText(Integer.toString(quantity));
            mSuplierNameEditText.setText(supplierName);
            mSuplierPhoneEditText.setText(Integer.toString(supplierPhone));

        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        mPriceEditText.setText("0");
        quantityEditText.setText("0");
        mSuplierNameEditText.setText("");
        mSuplierPhoneEditText.setText("eg:050000000");
    }
}


