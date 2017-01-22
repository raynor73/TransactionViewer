package org.ilapin.common.android;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

public class MainThreadExecutor implements Executor {

	private final Handler mHandler = new Handler(Looper.getMainLooper());

	@Override
	public void execute(@NonNull final Runnable runnable) {
		if (Thread.currentThread() == mHandler.getLooper().getThread()) {
			runnable.run();
		} else {
			mHandler.post(runnable);
		}
	}
}
