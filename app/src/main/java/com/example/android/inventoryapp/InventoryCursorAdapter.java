package com.example.android.inventoryapp;


import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.InventoryContract.InventoryEntry;

import static android.R.attr.id;
import static com.example.android.inventoryapp.R.id.price;

//referenc code https://github.com/laramartin/android_inventory

public class InventoryCursorAdapter extends CursorAdapter {

    private final MainActivity activity;


    public InventoryCursorAdapter(MainActivity context, Cursor c) {
        super(context, c, 0);
        this.activity = context;
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }
    int productQuatity;
    String productName ;
    int productPrice ;
    String supplierName;
    int supplierPhone;
    int id;
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = view.findViewById(R.id.name);
        TextView priceTextView =  view.findViewById(price);
        TextView quatityTextView =  view.findViewById(R.id.quatity);
        Button saleButton =  view.findViewById(R.id.sale);
        // Find the columns of product
        int idColumnIndex = cursor.getColumnIndex(InventoryEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PRICE);
        int quatityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_QUANTITY);
        int supplierNameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
        int supplierPhoneColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER);
        // Read the product attributes from the Cursor
           productName = cursor.getString(nameColumnIndex);
           productPrice = cursor.getInt(priceColumnIndex);
           productQuatity = cursor.getInt(quatityColumnIndex);
           supplierName = cursor.getString(supplierNameColumnIndex);
           supplierPhone = cursor.getInt(supplierPhoneColumnIndex);
        id = cursor.getInt(idColumnIndex);
        // Update the textview with the attributes
        nameTextView.setText(productName);
        priceTextView.setText("Price:  " + productPrice);
        quatityTextView.setText("Quatity : " + productQuatity);

        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                 activity.sale(id, productName, productPrice, productQuatity, supplierName, supplierPhone);
            }
        });
    }
}



