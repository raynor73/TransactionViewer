package org.ilapin.transactionviewer.product;

public class Product {

	private String mSku;
	private int mNumberOfTransactions;

	public Product(final String sku, final int numberOfTransactions) {
		mSku = sku;
		mNumberOfTransactions = numberOfTransactions;
	}

	public String getSku() {
		return mSku;
	}

	public void setSku(final String sku) {
		mSku = sku;
	}

	public int getNumberOfTransactions() {
		return mNumberOfTransactions;
	}

	public void setNumberOfTransactions(final int numberOfTransactions) {
		mNumberOfTransactions = numberOfTransactions;
	}
}
