package org.ilapin.transactionviewer.currency;

import android.util.Log;
import org.json.*;

import java.util.HashSet;
import java.util.Set;

public class RateParser {

	private static final String TAG = "RateParser";

	public static Set<Rate> parseRates(final String jsonString) {
		final Set<Rate> rates = new HashSet<>();

		final JSONArray ratesJsonArray;
		try {
			ratesJsonArray = new JSONArray(jsonString);
		} catch (final JSONException e) {
			Log.e(TAG, "Failed to parse Rates JSON", e);
			return rates;
		}

		for (int i = 0; i < ratesJsonArray.length(); i++) {
			try {
				final JSONObject rateJsonObject = ratesJsonArray.getJSONObject(i);
				if (!rateJsonObject.isNull("from")) {
					if (!rateJsonObject.isNull("to")) {
						if (!rateJsonObject.isNull("rate")) {
							final String from = rateJsonObject.getString("from");
							final String to = rateJsonObject.getString("to");
							final float rate = (float) rateJsonObject.getDouble("rate");
							rates.add(new Rate(from, to, rate));
						} else {
							Log.d(TAG, "No Rate found");
						}
					} else {
						Log.d(TAG, "No To found");
					}
				} else {
					Log.d(TAG, "No From found");
				}
			} catch (final JSONException e) {
				Log.e(TAG, "Failed to parse one of the rates");
			}
		}

		return rates;
	}
}
