package org.ilapin.transactionviewer;

import android.content.Context;

import org.ilapin.transactionviewer.backend.Backend;
import org.ilapin.transactionviewer.currency.CurrencyCalculator;
import org.ilapin.transactionviewer.product.ProductContainer;

import java.util.concurrent.Executor;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class TransactionViewerModule {

	@Provides
	@Singleton
	public TransactionViewer provideTransactionViewer(final Backend backend, final ProductContainer productContainer,
													  final CurrencyCalculator currencyCalculator,
													  final @CallbackExecutor Executor callbackExecutor) {
		return new TransactionViewer(backend, productContainer, currencyCalculator, callbackExecutor);
	}

	@Provides
	@Singleton
	public Backend provideBackend(final Context context, final @CallbackExecutor Executor callbackExecutor) {
		return new Backend(context, callbackExecutor);
	}

	@Provides
	@Singleton
	public ProductContainer provideProductContainer() {
		return new ProductContainer();
	}

	@Provides
	@Singleton
	public CurrencyCalculator provideCurrencyCalculator() {
		return new CurrencyCalculator();
	}
}
