package org.ilapin.transactionviewer.currency;

import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.ValueGraphBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import java8.util.stream.StreamSupport;

public class CurrencyCalculator {

	private static final String ROOT_NODE_NAME = "_root_";
	private static final float ROOT_NODE_CONVERSION_RATE = 1;

	private final MutableValueGraph<String, Float> mCurrenciesGraph = ValueGraphBuilder.directed().build();

	public CurrencyCalculator() {
		mCurrenciesGraph.addNode(ROOT_NODE_NAME);
	}

	public void addConversion(final String from, final String to, final float rate) {
		if (!mCurrenciesGraph.nodes().contains(from)) {
			mCurrenciesGraph.addNode(from);
			mCurrenciesGraph.putEdgeValue(ROOT_NODE_NAME, from, ROOT_NODE_CONVERSION_RATE);
		}
		mCurrenciesGraph.addNode(to);
		mCurrenciesGraph.putEdgeValue(from, to, rate);
	}

	public float calculate(final String from, final String to, final float amount) throws NoConversionException {
		final Set<String> nodes = mCurrenciesGraph.nodes();
		if (!nodes.contains(from) || !nodes.contains(to)) {
			throw new NoConversionException();
		}

		final List<Float> rates = new ArrayList<>();

		return amount;
	}
}
