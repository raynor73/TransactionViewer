package org.ilapin.transactionviewer.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.*;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import org.ilapin.common.Observer;
import org.ilapin.common.android.MainThreadExecutor;
import org.ilapin.transactionviewer.*;
import org.ilapin.transactionviewer.currency.CurrencyCalculator;
import org.ilapin.transactionviewer.currency.NoConversionException;
import org.ilapin.transactionviewer.product.ProductContainer;
import org.ilapin.transactionviewer.product.Transaction;

import javax.inject.Inject;
import java.util.*;

public class TransactionsActivity extends AppCompatActivity {

	private static final String DEFAULT_CURRENCY = "GBP";
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
	@Inject
	CurrencyCalculator mCurrencyCalculator;
	@Inject
	ProductContainer mProductContainer;

	private String mSku;

	private TransactionListAdapter mTransactionListAdapter;

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
						totalString = getString(R.string.total, String.format(Locale.US, "%.2f " + DEFAULT_CURRENCY, total));
					} catch (final NoConversionException e) {
						totalString = getString(R.string.total, getString(R.string.not_available));
					}
					mTotalTextView.setText(totalString);

					mTransactionListAdapter.setData(mProductContainer.getTransactions(mSku));

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

		mTransactionListAdapter = new TransactionListAdapter();
		mTransactionListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
		mTransactionListRecyclerView.setAdapter(mTransactionListAdapter);
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

	class TransactionListAdapter extends RecyclerView.Adapter<TransactionListAdapter.ViewHolder> {

		private final LayoutInflater mInflater = LayoutInflater.from(TransactionsActivity.this);
		private final List<Transaction> mTransactions = new ArrayList<>();

		public void setData(final Set<Transaction> transactions) {
			mTransactions.addAll(transactions);
			notifyDataSetChanged();
		}

		@Override
		public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
			return new ViewHolder(mInflater.inflate(android.R.layout.simple_list_item_2, parent, false));
		}

		@Override
		public void onBindViewHolder(final ViewHolder holder, final int position) {
			final Transaction transaction = mTransactions.get(position);

			final String price = String.format(
					Locale.US,
					"%.2f %s",
					transaction.getAmount(),
					transaction.getCurrency()
			);
			String convertedPrice;
			try {
				convertedPrice = String.format(
						Locale.US,
						"%.2f %s",
						mCurrencyCalculator.calculate(transaction.getCurrency(), DEFAULT_CURRENCY, transaction.getAmount()),
						DEFAULT_CURRENCY
				);
			} catch (final NoConversionException e) {
				convertedPrice = getString(R.string.not_available);
			}

			holder.titleTextView.setText(price);
			holder.subTitleTextView.setText(convertedPrice);
		}

		@Override
		public int getItemCount() {
			return mTransactions.size();
		}

		class ViewHolder extends RecyclerView.ViewHolder {

			@BindView(android.R.id.text1)
			public TextView titleTextView;
			@BindView(android.R.id.text2)
			public TextView subTitleTextView;

			public ViewHolder(final View itemView) {
				super(itemView);
				ButterKnife.bind(this, itemView);
			}
		}
	}
}
