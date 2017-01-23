package org.ilapin.transactionviewer.product;

import com.google.common.collect.ImmutableSet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ProductContainer {
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
	}
}
