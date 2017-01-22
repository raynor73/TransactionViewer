package org.ilapin.transactionviewer.product;

public class Transaction {

	private String mSku;
	private float mAmount;
	private String mCurrency;

	public Transaction(final String sku, final float amount, final String currency) {
		mSku = sku;
		mAmount = amount;
		mCurrency = currency;
	}

	public String getSku() {
		return mSku;
	}

	public void setSku(final String sku) {
		mSku = sku;
	}

	public float getAmount() {
		return mAmount;
	}

	public void setAmount(final float amount) {
		mAmount = amount;
	}

	public String getCurrency() {
		return mCurrency;
	}

	public void setCurrency(final String currency) {
		mCurrency = currency;
	}
}
