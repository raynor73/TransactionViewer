package org.ilapin.common;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

public class SimpleObservable implements Observable {

	private final Map<Observer, Executor> mObservers = new HashMap<>();

	@Override
	public void addObserverAndNotify(final Observer observer) {
		addObserver(observer);
		observer.update();
	}

	@Override
	public void addObserverAndNotify(final Observer observer, final Executor executor) {
		addObserver(observer, executor);
		executor.execute(observer::update);
	}

	@Override
	public void addObserver(final Observer observer, final Executor executor) {
		if (mObservers.keySet().contains(observer)) {
			throw new RuntimeException("Can not add observer second time");
		}

		mObservers.put(observer, executor);
	}

	@Override
	public void addObserver(final Observer observer) {
		if (mObservers.keySet().contains(observer)) {
			throw new RuntimeException("Can not add observer second time");
		}

		mObservers.put(observer, null);
	}

	@Override
	public void removeObserver(final Observer observer) {
		if (!mObservers.keySet().contains(observer)) {
			throw new RuntimeException("Can not find observer to remove");
		}

		mObservers.remove(observer);
	}

	public void notifyObservers() {
		for (final Observer observer : mObservers.keySet()) {
			final Executor executor = mObservers.get(observer);
			if (executor != null) {
				executor.execute(observer::update);
			} else {
				observer.update();
			}
		}
	}
}
