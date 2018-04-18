
package com.example.android.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
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


import com.example.android.inventoryapp.data.ProductContract.ProductEntry;

/**
 * {@link ProductCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of product data as its data source. This adapter knows
 * how to create list items for each row of product data in the {@link Cursor}.
 */
public class ProductCursorAdapter extends CursorAdapter {

    private static final String LOG_TAG = ProductCursorAdapter.class.getSimpleName();

    /**
     * Constructs a new {@link ProductCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the product data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current product can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(final View view, final Context context, final Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView nameProductTextView = view.findViewById(R.id.list_name_product_text_view);
        TextView priceProductTextView = view.findViewById(R.id.list_price_product_text_view);
        TextView quantityProductTextView = view.findViewById(R.id.list_quantity_product_text_view);

        // Find the columns of product attributes that we're interested in
        int nameProductColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
        int priceProductColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
        int quantityProductColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);

        // Read the product attributes from the Cursor for the current product
        String nameProduct = cursor.getString(nameProductColumnIndex);
        String priceProduct = context.getString(R.string.unit_product_price) + " " + cursor.getString(priceProductColumnIndex);
        final String quantityProduct = cursor.getString(quantityProductColumnIndex);

        final int quantityProduct_int = cursor.getInt(quantityProductColumnIndex);
        final Uri uri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, cursor.getInt(cursor.getColumnIndexOrThrow(ProductEntry._ID)));

        // Update the TextViews with the attributes for the current product
        nameProductTextView.setText(nameProduct);
        priceProductTextView.setText(priceProduct);
        quantityProductTextView.setText(quantityProduct);

        //Reduce 1 in product quantity if > 0, update in the database of the current row
        //Update the TextViews with the new value of the atribute
        Button saleButton = view.findViewById(R.id.sale_button);
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantityProduct_int > 0) {
                    int newQuantity = quantityProduct_int - 1;

                    ContentValues values = new ContentValues();
                    values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, newQuantity);
                    context.getContentResolver().update(uri, values, null, null);
                    Log.d(LOG_TAG, "URI for update: " + uri);
                } else {
                    Toast.makeText(context, context.getString(R.string.product_sold_out), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}