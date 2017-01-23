package org.ilapin.transactionviewer;

import org.ilapin.transactionviewer.ui.MainActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {SystemModule.class, TransactionViewerModule.class})
public interface ApplicationComponent {

	void inject(MainActivity activity);
}
