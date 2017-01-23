package org.ilapin.transactionviewer.backend;

import android.content.Context;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import org.ilapin.common.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Backend {

	private static final String RATES_FILENAME = "rates.json";
	private static final String TRANSACTIONS_FILENAME = "transactions.json";

	private final Context mContext;
	private final Executor mCallbackExecutor;
	private final ListeningExecutorService mExecutorService =
		MoreExecutors.listeningDecorator(Executors.newSingleThreadExecutor());

	public Backend(final Context context, final Executor callbackExecutor) {
		mContext = context;
		mCallbackExecutor = callbackExecutor;
	}

	public void loadTransactions(final FutureCallback<String> callback) {
		Futures.addCallback(mExecutorService.submit(() -> loadData(TRANSACTIONS_FILENAME)), callback, mCallbackExecutor);
	}

	public void loadRates(final FutureCallback<String> callback) {
		Futures.addCallback(mExecutorService.submit(() -> loadData(RATES_FILENAME)), callback, mCallbackExecutor);
	}

	private String loadData(final String filename) throws IOException {
		final InputStream is = mContext.getAssets().open(filename);
		final String data = IOUtils.readInputStreamToString(is);
		is.close();
		return data;
	}
}
