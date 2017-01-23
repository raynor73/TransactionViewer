package org.ilapin.transactionviewer.currency;

import java.util.*;

public class CurrencyCalculator {

	private static final String ROOT_NODE_NAME = "_root_";
	private static final float ROOT_NODE_CONVERSION_RATE = 1;

	private final TreeNode<String, Float> mCurrenciesGraph = new TreeNode<>(ROOT_NODE_NAME);

	public void addConversion(final String from, final String to, final float rate) {
		TreeNode<String, Float> fromNode = findNode(mCurrenciesGraph, from);
		if (fromNode == null) {
			fromNode = new TreeNode<>(from);
			mCurrenciesGraph.addChild(fromNode, ROOT_NODE_CONVERSION_RATE);
		}
		fromNode.addChild(new TreeNode<>(to), rate);
	}

	public float calculate(final String from, final String to, final float amount) throws NoConversionException {
		final TreeNode<String, Float> fromNode = findNode(mCurrenciesGraph, from);
		final TreeNode<String, Float> toNode = findNode(mCurrenciesGraph, to);
		if (fromNode == null || toNode == null) {
			throw new NoConversionException();
		}

		final List<Float> rates = new ArrayList<>();
		final List<TreeNode<String, Float>> parentsAndMe = new ArrayList<>();
		parentsAndMe.add(fromNode);
		parentsAndMe.addAll(getAllParents(fromNode));
		final List<Float> route = new ArrayList<>();
		for (final TreeNode<String, Float> node : parentsAndMe) {
			buildRoute(node, toNode, route);
			if (route.size() > 0) {
				rates.addAll(route);
				break;
			}

			final TreeNode<String, Float> parent = node.getParent();
			if (parent != null) {
				rates.add(1 / parent.getEdges().get(node));
			}
		}

		float result = amount;
		for (final Float rate : rates) {
			result *= rate;
		}

		return result;
	}

	private TreeNode<String, Float> findNode(final TreeNode<String, Float> node, final String data) {
		if (node.getData().equals(data)) {
			return node;
		}

		for (final TreeNode<String, Float> child : node.getChildren()) {
			final TreeNode<String, Float> matchedChild = findNode(child, data);
			if (matchedChild != null) {
				return matchedChild;
			}
		}

		return null;
	}

	private boolean buildRoute(final TreeNode<String, Float> from,
			final TreeNode<String, Float> to, final List<Float> route) {
		final Map<TreeNode<String, Float>, Float> edges = from.getEdges();

		for (final TreeNode<String, Float> node : from.getChildren()) {
			route.add(edges.get(node));
			if (node.getData().equals(to.getData())) {
				return true;
			} else {
				if (!buildRoute(node, to, route)) {
					if (route.size() > 0) {
						route.remove(route.size() - 1);
					}
				} else {
					return true;
				}
			}
		}

		return false;
	}

	private List<TreeNode<String, Float>> getAllParents(final TreeNode<String, Float> node) {
		final List<TreeNode<String, Float>> parents = new ArrayList<>();

		TreeNode<String, Float> currentNode = node;
		do {
			final TreeNode<String, Float> parent = currentNode.getParent();
			if (parent != null) {
				parents.add(parent);
			}
			currentNode = parent;
		} while (currentNode != null);

		return parents;
	}

	/*private boolean isTreeContains(final TreeNode<String, Float> node, final String data) {
		final Stream<TreeNode<String, Float>> stream = StreamSupport.stream(node.getChildren());
		return node.getData().equals(data) || stream.filter((n) -> isTreeContains(n, data)).count() > 0;
	}*/
}
