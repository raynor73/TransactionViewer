package org.ilapin.transactionviewer;

import android.support.annotation.NonNull;
import android.util.Log;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.*;
import java8.util.stream.StreamSupport;
import org.ilapin.common.*;
import org.ilapin.transactionviewer.backend.Backend;
import org.ilapin.transactionviewer.currency.*;
import org.ilapin.transactionviewer.product.*;

import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

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

	public float getTotal(final String sku) throws NoConversionException {
		float total = 0;
		for (final Transaction transaction : mProductContainer.getTransactions(sku)) {
			total += mCurrencyCalculator.calculate(transaction.getCurrency(), "GBP", transaction.getAmount());
		}
		return total;
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

	private void parseRates(final String s) {
		final FutureCallback<Set<Rate>> callback = new FutureCallback<Set<Rate>>() {

			@Override
			public void onSuccess(final Set<Rate> rates) {
				StreamSupport.stream(rates).forEach((r) -> mCurrencyCalculator.addConversion(r.getFrom(), r.getTo(), r.getRate()));
				changeState(State.READY);
			}

			@Override
			public void onFailure(@NonNull final Throwable throwable) {
				Log.e(TAG, "Failed to parse rates");
				changeState(State.READY);
			}
		};

		Futures.addCallback(
				mExecutorService.submit(() -> RateParser.parseRates(s)),
				callback,
				mCallbackExecutor
		);
	}

	private void parseTransactions(final String s) {
		final FutureCallback<Set<Transaction>> callback = new FutureCallback<Set<Transaction>>() {

			@Override
			public void onSuccess(final Set<Transaction> transactions) {
				StreamSupport.stream(transactions).forEach(mProductContainer::addTransaction);

				mBackend.loadRates(new FutureCallback<String>() {

					@Override
					public void onSuccess(final String s) {
						parseRates(s);
					}

					@Override
					public void onFailure(@NonNull final Throwable throwable) {
						Log.e(TAG, "Failed to load rates", throwable);
					}
				});
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
