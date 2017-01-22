package org.ilapin.transactionviewer.product;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

public class TransactionParser {

	private static final String TAG = "TransactionParser";

	public static Set<Transaction> parseTransactions(final String jsonString) {
		final Set<Transaction> transactions = new HashSet<>();

		final JSONArray transactionsJsonArray;
		try {
			transactionsJsonArray = new JSONArray(jsonString);
		} catch (final JSONException e) {
			Log.e(TAG, "Failed to parse Transactions JSON", e);
			return transactions;
		}

		for (int i = 0; i < transactionsJsonArray.length(); i++) {
			try {
				final JSONObject transactionJsonObject = transactionsJsonArray.getJSONObject(i);
				final String sku = transactionJsonObject.optString("sku", null);
				final float amount;
				final String currency;
				if (sku != null) {
					if (!transactionJsonObject.isNull("amount")) {
						amount = (float) transactionJsonObject.getDouble("amount");
					} else {
						Log.e(TAG, "No amount found for transaction with SKU: " + sku);
						continue;
					}

					currency = transactionJsonObject.optString("currency", null);
					if (currency != null) {
						transactions.add(new Transaction(sku, amount, currency));
					} else {
						Log.e(TAG, "No currency found for transaction with SKU: " + sku);
					}
				} else {
					Log.e(TAG, "No SKU found");
				}
			} catch (final JSONException e) {
				Log.e(TAG, "Failed to parse one of the transactions");
			}
		}

		return transactions;
	}
}
