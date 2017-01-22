package org.ilapin.transactionviewer.backend;

import android.content.Context;
import android.util.Log;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import org.ilapin.common.IOUtils;
import org.ilapin.common.Observable;
import org.ilapin.common.Observer;
import org.ilapin.common.SimpleObservable;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Backend implements Observable {

	private static final String TAG = "Backend";

	private static final String RATES_FILENAME = "rates.json";
	private static final String TRANSACTIONS_FILENAME = "transactions.json";

	private final Context mContext;
	private final SimpleObservable mObservable = new SimpleObservable();
	private final ListeningExecutorService mExecutorService =
		MoreExecutors.listeningDecorator(Executors.newSingleThreadExecutor());

	private State mState = State.IDLE;

	public Backend(final Context context) {
		mContext = context;
	}

	public void loadTransactions(final FutureCallback<String> callback) {
		if (mState != State.IDLE) {
			Log.d(TAG, "Backend.loadTransactions called not in IDLE state");
			return;
		}

		changeState(State.LOADING);

		Futures.addCallback(mExecutorService.submit(() -> {
			final String data = loadData(TRANSACTIONS_FILENAME);
			changeState(State.IDLE);
			return data;
		}), callback);
	}

	public void loadRates(final FutureCallback<String> callback) {
		if (mState != State.IDLE) {
			Log.d(TAG, "Backend.loadRates called not in IDLE state");
			return;
		}

		changeState(State.LOADING);

		Futures.addCallback(mExecutorService.submit(() -> {
			final String data = loadData(RATES_FILENAME);
			changeState(State.IDLE);
			return data;
		}), callback);
	}

	@Override
	public void addObserverAndNotify(final Observer observer) {
		mObservable.addObserverAndNotify(observer);
	}

	@Override
	public void addObserverAndNotify(final Observer observer, final Executor executor) {
		mObservable.addObserverAndNotify(observer, executor);
	}

	@Override
	public void addObserver(final Observer observer, final Executor executor) {
		mObservable.addObserver(observer, executor);
	}

	@Override
	public void addObserver(final Observer observer) {
		mObservable.addObserver(observer);
	}

	@Override
	public void removeObserver(final Observer observer) {
		mObservable.removeObserver(observer);
	}

	public enum State {
		IDLE, LOADING
	}

	private String loadData(final String filename) throws IOException {
		final InputStream is = mContext.getAssets().open(filename);
		final String data = IOUtils.readInputStreamToString(is);
		is.close();
		return data;
	}

	private void changeState(final State newState) {
		Preconditions.checkState(newState != mState);
		mState = newState;
		mObservable.notifyObservers();
	}
}
