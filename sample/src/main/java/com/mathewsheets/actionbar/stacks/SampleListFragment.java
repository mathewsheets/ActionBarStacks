package com.mathewsheets.actionbar.stacks;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.mathewsheets.actionbar.stackslib.ActionBarDrawerStacksActivity;
import com.mathewsheets.actionbar.stackslib.ActionBarFragmentStacksActivity;

public class SampleListFragment extends ListFragment {

	public static final String ARG_TITLE = "title";
    public static final String ARG_COLOR = "color";

	private SampleAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		return inflater.inflate(R.layout.sample_list, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);

        Bundle args = getArguments();

        String title = args.getString(ARG_TITLE);
        int colorRes = args.getInt(ARG_COLOR);

		adapter = new SampleAdapter(getActivity());
		for (int i = 1; i <= 10; i++) {
			adapter.add(new SampleItem(title + ": " + i, colorRes));
		}
		setListAdapter(adapter);
		
		setHasOptionsMenu(true);
	}

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
        case R.id.action_github:

    		Uri uri = Uri.parse(getString(R.string.github_url));
    		Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uri);
    		startActivity(launchBrowser);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

	@Override
	public void onListItemClick (ListView l, View v, int position, long id){

		SampleItem item = (SampleItem) adapter.getItem(position);

		SampleListFragment fragment = new SampleListFragment();

        Bundle args = new Bundle();
        args.putString(SampleListFragment.ARG_TITLE, item.title);
        args.putInt(SampleListFragment.ARG_COLOR, item.colorRes);

        fragment.setArguments(args);

        if(getActivity() instanceof ActionBarDrawerStacksActivity) {
        	((ActionBarDrawerStacksActivity) getActivity()).addFragmentToCurrentStack(item.title, fragment);
        } else {
        	((ActionBarFragmentStacksActivity) getActivity()).addFragment(item.title, fragment);
        }		
	}
	
	private static class SampleAdapter extends ArrayAdapter<SampleItem> {

		public SampleAdapter(Context context) {

			super(context, 0);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

            View v = convertView;

            if (v == null) {

                v = LayoutInflater.from(getContext()).inflate(R.layout.sample_list_item, null);

                SampleItemViewHolder holder = new SampleItemViewHolder();

                holder.titleTxt = (TextView) v.findViewById(R.id.title);

                v.setTag(holder);
            }

            SampleItem item = getItem(position);
            if (item != null) {

                SampleItemViewHolder holder = (SampleItemViewHolder) v.getTag();

                holder.titleTxt.setText(item.title);
                holder.titleTxt.setTextColor(getContext().getResources().getColor(item.colorRes));
            }

			return v;
		}
	}

	private static class SampleItem {

		private String title;
        private int colorRes;

		public SampleItem(String title, int colorRes) {

			this.title = title;
            this.colorRes = colorRes;
		}
	}

    private static class SampleItemViewHolder {

        private TextView titleTxt;
    }
	
}
