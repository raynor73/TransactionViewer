package org.ilapin.transactionviewer.currency;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.util.*;

public class TreeNode<T, V> {

	private TreeNode<T, V> parent;
	private final Set<TreeNode<T, V>> children = new HashSet<>();
	private final Map<TreeNode<T, V>, V> edges = new HashMap<>();

	private final T mData;

	public TreeNode(final T data) {
		mData = data;
	}

	public void addChild(final TreeNode<T, V> child, final V edgeValue) {
		children.add(child);
		child.setParent(this);
		edges.put(child, edgeValue);
	}

	public TreeNode<T, V> getParent() {
		return parent;
	}

	public void setParent(final TreeNode<T, V> parent) {
		this.parent = parent;
	}

	public T getData() {
		return mData;
	}

	public Set<TreeNode<T, V>> getChildren() {
		return ImmutableSet.copyOf(children);
	}

	public Map<TreeNode<T, V>, V> getEdges() {
		return ImmutableMap.copyOf(edges);
	}
}
