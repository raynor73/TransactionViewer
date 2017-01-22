package org.ilapin.transactionviewer;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.FutureCallback;

import org.ilapin.common.Observable;
import org.ilapin.common.Observer;
import org.ilapin.common.SimpleObservable;
import org.ilapin.transactionviewer.backend.Backend;
import org.ilapin.transactionviewer.currency.CurrencyCalculator;
import org.ilapin.transactionviewer.product.ProductContainer;

import java.util.concurrent.Executor;

public class TransactionViewer implements Observable {

	private final static String TAG = "TransactionViewer";

	private final SimpleObservable mObservable = new SimpleObservable();

	private final Backend mBackend;
	private final ProductContainer mProductContainer;
	private final CurrencyCalculator mCurrencyCalculator;

	private State mState = State.INITIALIZING;

	public TransactionViewer(final Backend backend, final ProductContainer productContainer,
							 final CurrencyCalculator currencyCalculator) {
		mBackend = backend;
		mProductContainer = productContainer;
		mCurrencyCalculator = currencyCalculator;

		mBackend.loadTransactions(new FutureCallback<String>() {

			@Override
			public void onSuccess(final String s) {
			}

			@Override
			public void onFailure(@NonNull final Throwable throwable) {
				Log.e(TAG, "Failed to load transactions", throwable);
			}
		});
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
		INITIALIZING, READY
	}

	private void changeState(final State newState) {
		Preconditions.checkState(newState != mState);
		mState = newState;
		mObservable.notifyObservers();
	}
}
