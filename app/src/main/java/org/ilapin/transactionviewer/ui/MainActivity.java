package org.ilapin.transactionviewer.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.ilapin.transactionviewer.R;
import org.ilapin.transactionviewer.product.Product;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

	@BindView(R.id.view_list)
	RecyclerView mProductListRecyclerView;

	private ProductListAdapter mProductListAdapter;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ButterKnife.bind(this);

		mProductListAdapter = new ProductListAdapter();

		mProductListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
		mProductListRecyclerView.setAdapter(mProductListAdapter);
	}

	class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ViewHolder> {

		private final LayoutInflater mInflater = LayoutInflater.from(MainActivity.this);
		private final List<Product> mProducts = new ArrayList<>();

		public void setData(final Set<Product> products) {
			mProducts.addAll(products);
			Collections.sort(mProducts, (a, b) -> a.getSku().compareTo(b.getSku()));
			notifyDataSetChanged();
		}

		@Override
		public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
			return new ViewHolder(mInflater.inflate(android.R.layout.simple_list_item_2, parent, false));
		}

		@Override
		public void onBindViewHolder(final ViewHolder holder, final int position) {
			final Product product = mProducts.get(position);
			holder.titleTextView.setText(product.getSku());
			holder.subTitleTextView.setText(product.getNumberOfTransactions());
		}

		@Override
		public int getItemCount() {
			return mProducts.size();
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
