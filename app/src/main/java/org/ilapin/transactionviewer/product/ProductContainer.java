package org.ilapin.transactionviewer.product;

import com.google.common.collect.ImmutableSet;

import org.ilapin.common.Observable;
import org.ilapin.common.Observer;
import org.ilapin.common.SimpleObservable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;

public class ProductContainer implements Observable {

	private final SimpleObservable mObservable = new SimpleObservable();

	private final Map<String, Product> mProducts = new HashMap<>();
	private final Set<Transaction> mTransactions = new HashSet<>();

	public Set<Product> getProducts() {
		return ImmutableSet.copyOf(mProducts.values());
	}

	public Set<Transaction> getTransactions() {
		return ImmutableSet.copyOf(mTransactions);
	}

	public void addTransaction(final Transaction transaction) {
		final String sku = transaction.getSku();
		if (mProducts.containsKey(sku)) {
			final Product product = mProducts.get(sku);
			product.setNumberOfTransactions(product.getNumberOfTransactions() + 1);
		} else {
			mProducts.put(sku, new Product(sku, 0));
		}
		mTransactions.add(transaction);

		mObservable.notifyObservers();
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
	public void addObserver(final Observer observer) {
		mObservable.addObserver(observer);
	}

	@Override
	public void removeObserver(final Observer observer) {
		mObservable.removeObserver(observer);
	}
}
