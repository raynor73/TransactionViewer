package org.ilapin.transactionviewer;

import android.content.Context;

import org.ilapin.common.android.MainThreadExecutor;

import java.util.concurrent.Executor;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class SystemModule {

	private final Context mContext;

	public SystemModule(final Context context) {
		mContext = context;
	}

	@Provides
	@Singleton
	public MainThreadExecutor provideMainThreadExecutor() {
		return new MainThreadExecutor();
	}

	@Provides
	@Singleton
	@CallbackExecutor
	public Executor provideCallbackExecutor(final MainThreadExecutor executor) {
		return executor;
	}

	@Provides
	@Singleton
	public Context provideContext() {
		return mContext;
	}
}
