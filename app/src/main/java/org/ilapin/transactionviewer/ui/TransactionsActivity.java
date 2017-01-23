package org.ilapin.transactionviewer.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.ilapin.common.Observer;
import org.ilapin.common.android.MainThreadExecutor;
import org.ilapin.transactionviewer.App;
import org.ilapin.transactionviewer.R;
import org.ilapin.transactionviewer.TransactionViewer;
import org.ilapin.transactionviewer.currency.NoConversionException;

import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TransactionsActivity extends AppCompatActivity {

	private static final String SKU_KEY = "SKU";

	@BindView(R.id.view_progress_bar)
	ProgressBar mProgressBar;
	@BindView(R.id.view_list)
	RecyclerView mTransactionListRecyclerView;
	@BindView(R.id.view_total)
	TextView mTotalTextView;

	@Inject
	TransactionViewer mTransactionViewer;
	@Inject
	MainThreadExecutor mMainThreadExecutor;

	private String mSku;

	private final Observer mTransactionViewerObserver = new Observer() {

		@Override
		public void update() {
			switch (mTransactionViewer.getState()) {
				case INITIALIZING:
					mTransactionListRecyclerView.setVisibility(View.GONE);
					mTotalTextView.setVisibility(View.GONE);
					mProgressBar.setVisibility(View.VISIBLE);
					break;

				case READY:
					mTransactionListRecyclerView.setVisibility(View.VISIBLE);
					mTotalTextView.setVisibility(View.VISIBLE);
					mProgressBar.setVisibility(View.GONE);

					String totalString;
					try {
						final float total = mTransactionViewer.getTotal(mSku);
						totalString = getString(R.string.total, String.format(Locale.US, "%.2f", total));
					} catch (final NoConversionException e) {
						totalString = getString(R.string.total, getString(R.string.not_available));
					}
					mTotalTextView.setText(totalString);

					//mProductListAdapter.setData(mProductContainer.getProducts());

					break;
			}
		}
	};

	public static void start(final Context context, final String sku) {
		final Intent intent = new Intent(context, TransactionsActivity.class);
		intent.putExtra(SKU_KEY, sku);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_transactions);

		ButterKnife.bind(this);
		App.getApplicationComponent().inject(this);

		mSku = getIntent().getStringExtra(SKU_KEY);

		setTitle(getString(R.string.transactions_for, mSku));

		mTransactionListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
	}

	@Override
	protected void onResume() {
		super.onResume();

		mTransactionViewer.addObserverAndNotify(mTransactionViewerObserver, mMainThreadExecutor);
	}

	@Override
	protected void onPause() {
		super.onPause();

		mTransactionViewer.removeObserver(mTransactionViewerObserver);
	}
}
