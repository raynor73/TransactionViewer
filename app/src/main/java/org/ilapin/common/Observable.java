package org.ilapin.common;

import java.util.concurrent.Executor;

public interface Observable {

	void addObserverAndNotify(final Observer observer);

	void addObserverAndNotify(final Observer observer, final Executor executor);

	void addObserver(final Observer observer, final Executor executor);

	void addObserver(final Observer observer);

	void removeObserver(final Observer observer);
}
