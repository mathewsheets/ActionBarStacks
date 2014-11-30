package com.mathewsheets.actionbar.stacks;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.mathewsheets.actionbar.stackslib.ActionBarDrawerStacksActivity;

public class SampleActionBarDrawerStacksActivity extends ActionBarDrawerStacksActivity {

	private String[] itemTitles;
	private DrawerAdapter adapter;

    @Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
		setEnableDebugLogging(true);

		setOnDrawerOpenListener(new ActionBarDrawerStacksActivity.OnDrawerOpenListener() {

            @Override
            public void onDrawerOpen() {

                getSupportActionBar().setSubtitle(getString(R.string.drawer_open_text));
            }
        });

		setOnDrawerClosedListener(new ActionBarDrawerStacksActivity.OnDrawerClosedListener() {

			@Override
			public void onDrawerClosed() {

				getSupportActionBar().setSubtitle(getString(R.string.drawer_close_text));
			}
		});
	}
	
	@Override
	protected int getMainLayoutRes() {

		return R.layout.drawer_main;
	}

	@Override
	protected DrawerLayout getDrawerLayout() {

		DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

		return drawerLayout;
	}
	
	@Override
	protected View getDrawerView() {

		View drawerView = (View) findViewById(R.id.drawer);

		final String fixedTopTitle = getString(R.string.fixed_top_item);
		final String fixedBottomTitle = getString(R.string.fixed_bottom_item);

		TextView topTxt = (TextView) findViewById(R.id.drawer_top);
		topTxt.setText(fixedTopTitle);
		topTxt.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {

				Intent intent = new Intent(SampleActionBarDrawerStacksActivity.this, SampleActionBarFragmentStackActivity.class);
				intent.putExtra("from", fixedTopTitle);
                intent.putExtra("color", R.color.blue);
                intent.putExtra("color_dark", R.color.blue_dark);
                intent.putExtra("homeAsUp", true);

				startActivity(intent);
			}
		});

		TextView bottomTxt = (TextView) findViewById(R.id.drawer_bottom);
		bottomTxt.setText(fixedBottomTitle);
		bottomTxt.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {

				Intent intent = new Intent(SampleActionBarDrawerStacksActivity.this, SampleActionBarFragmentStackActivity.class);
				intent.putExtra("from", fixedBottomTitle);
                intent.putExtra("color", R.color.purple);
                intent.putExtra("color_dark", R.color.purple_dark);
                intent.putExtra("homeAsUp", false);

				startActivity(intent);
			}
		});

		return drawerView;
	}

	@Override
	protected ListView getDrawerListView() {

		ListView listView = (ListView) findViewById(R.id.drawer_list);

		TextView headerTxt = (TextView) LayoutInflater.from(this).inflate(R.layout.drawer_list_header, listView, false);
		TextView footerTxt = (TextView) LayoutInflater.from(this).inflate(R.layout.drawer_list_footer, listView, false);		

		final String headerTitle = getString(R.string.items_header);
		final String footerTitle = getString(R.string.items_footer);

		headerTxt.setText(headerTitle);
		footerTxt.setText(footerTitle);

		headerTxt.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {

				Intent intent = new Intent(SampleActionBarDrawerStacksActivity.this, SampleActionBarFragmentStackActivity.class);
				intent.putExtra("from", headerTitle);
                intent.putExtra("color", R.color.red);
                intent.putExtra("color_dark", R.color.red_dark);
                intent.putExtra("homeAsUp", true);

				startActivity(intent);
			}
		});

		footerTxt.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {

				Intent intent = new Intent(SampleActionBarDrawerStacksActivity.this, SampleActionBarFragmentStackActivity.class);
				intent.putExtra("from", footerTitle);
                intent.putExtra("color", R.color.orange);
                intent.putExtra("color_dark", R.color.orange_dark);
                intent.putExtra("homeAsUp", false);

				startActivity(intent);
			}
		});

		listView.addHeaderView(headerTxt);
		listView.addFooterView(footerTxt);

		return listView;
	}

	@Override
	protected ListAdapter getDrawerListAdapter() {

		itemTitles = getResources().getStringArray(R.array.items);

		adapter = new DrawerAdapter(this);

		for(int i=0;i<itemTitles.length; i++) {

			DrawerItem drawerItem = new DrawerItem(itemTitles[i]);

			adapter.add(drawerItem);
		}

		return adapter;
	}

	@Override
	protected String getDrawerItemTitle(int position) {

		return itemTitles[position];
	}

    @Override
    protected int getOpenDrawerContentDescRes() {

    	return R.string.drawer_open;
    }
    
    @Override
    protected int getCloseDrawerContentDescRes() {

    	return R.string.drawer_close;
    }
	
    @Override
    protected int getContentFrameRes() {

    	return R.id.content_frame;
    }
    
    @Override
    protected int getFragmentContainerLayoutRes() {

    	return R.layout.fragment_container;
    }
    
    @Override
    protected int getFragmentContainerFrameRes() {

    	return R.id.fragment_container_frame;
    }

	@Override
	protected Fragment onNewDrawerItem(int position) {

        SampleMasterListFragment sampleFragment = new SampleMasterListFragment();

		Bundle args = new Bundle();
		args.putString(SampleListFragment.ARG_TITLE, getDrawerItemTitle(position));
        args.putInt(SampleListFragment.ARG_COLOR, R.color.green);

		sampleFragment.setArguments(args);

		return sampleFragment;
	}

	@Override
    public void onSetDrawerItem(String title) {

        adapter.setDrawItemSelected(title);

        clearDetailFragment();
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();

        clearDetailFragment();
    }

    @Override
    protected void moveUp() {

        super.moveUp();

        clearDetailFragment();
    }

    private void clearDetailFragment() {

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container_frame_detail);
        if (fragment != null) {
            FragmentTransaction tx = fragmentManager.beginTransaction();
            tx.remove(fragment);
            tx.commit();
        }
    }

    public void addFragment(String title, Fragment fragment) {

        if (hasDetailFragment()) {

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction tx = fragmentManager.beginTransaction();
            tx.replace(R.id.fragment_container_frame_detail, fragment);
            tx.commit();

        } else {

            addFragmentToCurrentStack(title, fragment);
        }
    }

    public boolean hasDetailFragment() {

        return getDetailView() != null ? true : false;
    }

    protected View getDetailView() {

        return findViewById(R.id.fragment_container_frame_detail);
    }

	private static class DrawerAdapter extends ArrayAdapter<DrawerItem> {

		public DrawerAdapter(Context context) {

			super(context, 0);
		}

        public void setDrawItemSelected(String title) {

            int count = getCount();
            for(int position=0;position<count;position++) {

                DrawerItem drawerItem = getItem(position);

                if(title.equals(drawerItem.title)) {
                    drawerItem.setSelected(true);
                } else {
                    drawerItem.setSelected(false);
                }
            }

            notifyDataSetChanged();
        }

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView == null) {

				convertView = LayoutInflater.from(getContext()).inflate(R.layout.drawer_list_item, parent, false);
			}

			DrawerItem drawerItem = getItem(position);

			TextView title = (TextView) convertView.findViewById(R.id.item_title);
			title.setText(drawerItem.title);

			if(drawerItem.isSelected()) {
				title.setTextColor(getContext().getResources().getColor(R.color.orange));
			} else {
				title.setTextColor(getContext().getResources().getColor(R.color.green));
			}

			return convertView;
		}
	}

	private static class DrawerItem {

		public String title;
		public boolean selected;

		public DrawerItem(String title) {

			this.title = title;
		}

		public boolean isSelected() {
			return selected;
		}

		public void setSelected(boolean selected) {
			this.selected = selected;
		}
	}

}
