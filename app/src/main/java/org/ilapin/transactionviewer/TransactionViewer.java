package org.ilapin.transactionviewer;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import org.ilapin.common.Observable;
import org.ilapin.common.Observer;
import org.ilapin.common.SimpleObservable;
import org.ilapin.transactionviewer.backend.Backend;
import org.ilapin.transactionviewer.currency.CurrencyCalculator;
import org.ilapin.transactionviewer.product.ProductContainer;
import org.ilapin.transactionviewer.product.Transaction;
import org.ilapin.transactionviewer.product.TransactionParser;

import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import java8.util.stream.StreamSupport;

public class TransactionViewer implements Observable {

	private final static String TAG = "TransactionViewer";

	private final SimpleObservable mObservable = new SimpleObservable();
	private final ListeningExecutorService mExecutorService =
			MoreExecutors.listeningDecorator(Executors.newSingleThreadExecutor());

	private final Backend mBackend;
	private final ProductContainer mProductContainer;
	private final CurrencyCalculator mCurrencyCalculator;
	private final Executor mCallbackExecutor;

	private State mState = State.INITIALIZING;

	public TransactionViewer(final Backend backend, final ProductContainer productContainer,
							 final CurrencyCalculator currencyCalculator, final Executor callbackExecutor) {
		mBackend = backend;
		mProductContainer = productContainer;
		mCurrencyCalculator = currencyCalculator;
		mCallbackExecutor = callbackExecutor;

		mBackend.loadTransactions(new FutureCallback<String>() {

			@Override
			public void onSuccess(final String s) {
				parseTransactions(s);
			}

			@Override
			public void onFailure(@NonNull final Throwable throwable) {
				Log.e(TAG, "Failed to load transactions", throwable);
			}
		});
	}

	public State getState() {
		return mState;
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

	private void parseTransactions(final String s) {
		final FutureCallback<Set<Transaction>> callback = new FutureCallback<Set<Transaction>>() {

			@Override
			public void onSuccess(final Set<Transaction> transactions) {
				StreamSupport.stream(transactions).forEach(mProductContainer::addTransaction);
				changeState(State.READY);
			}

			@Override
			public void onFailure(@NonNull final Throwable throwable) {
				Log.e(TAG, "Failed to parse transactions");
				changeState(State.READY);
			}
		};

		Futures.addCallback(
				mExecutorService.submit(() -> TransactionParser.parseTransactions(s)),
				callback,
				mCallbackExecutor
		);
	}
}
