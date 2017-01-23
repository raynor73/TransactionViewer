package org.ilapin.transactionviewer.currency;

public class Rate {

	private final String mFrom;
	private final String mTo;
	private final float mRate;

	public Rate(final String from, final String to, final float rate) {
		mFrom = from;
		mTo = to;
		mRate = rate;
	}

	public String getFrom() {
		return mFrom;
	}

	public String getTo() {
		return mTo;
	}

	public float getRate() {
		return mRate;
	}
}
