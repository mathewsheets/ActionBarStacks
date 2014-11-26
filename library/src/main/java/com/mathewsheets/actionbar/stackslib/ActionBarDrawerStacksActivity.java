package com.mathewsheets.actionbar.stackslib;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class ActionBarDrawerStacksActivity extends ActionBarActivity {

	private static final String TAG = ActionBarDrawerStacksActivity.class.getSimpleName();

    protected abstract int getMainLayoutRes();
    protected abstract DrawerLayout getDrawerLayout();
    protected abstract int getOpenDrawerContentDescRes();
    protected abstract int getCloseDrawerContentDescRes();
    protected abstract View getDrawerView();
    protected abstract ListView getDrawerListView();
    protected abstract ListAdapter getDrawerListAdapter();
    protected abstract String getDrawerItemTitle(int position);
    protected abstract int getContentFrameRes();
    protected abstract int getFragmentContainerLayoutRes();
    protected abstract int getFragmentContainerFrameRes();
    protected abstract Fragment onNewDrawerItem(int position);

    private DrawerLayout drawerLayout;
    private View drawerView;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;    
    private String drawerTitle;
    private boolean homeAsUpShowing;
    private FragmentStack currentFragmentStack;
    private Map<String, FragmentStack> containers = new HashMap<String, FragmentStack>();

    private OnDrawerOpenListener onDrawerOpenListener;
    private OnDrawerClosedListener onDrawerClosedListener;
    
	private boolean enableDebugLogging;
	public boolean isEnableDebugLogging() {
		return enableDebugLogging;
	}
	public void setEnableDebugLogging(boolean enableDebugLogging) {
		this.enableDebugLogging = enableDebugLogging;
	}

	public interface OnDrawerOpenListener {

		public void onDrawerOpen();
	}

	public interface OnDrawerClosedListener {

		public void onDrawerClosed();
	}

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(getMainLayoutRes());
        drawerLayout = getDrawerLayout();
        drawerView = getDrawerView();
        drawerList = getDrawerListView();
        drawerList.setAdapter(getDrawerListAdapter());
        drawerList.setOnItemClickListener(new ListView.OnItemClickListener() {

        	@Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        		selectMenuItem(position - drawerList.getHeaderViewsCount());
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        drawerToggle = new ActionBarDrawerToggle(this,
        										drawerLayout,
        										getOpenDrawerContentDescRes(),
        										getCloseDrawerContentDescRes()) {

        	@Override
            public void onDrawerClosed(View view) {

            	if (homeAsUpShowing) {
            		drawerToggle.setDrawerIndicatorEnabled(false);
				} else {
					drawerToggle.setDrawerIndicatorEnabled(true);
				}

            	getSupportActionBar().setTitle(drawerTitle);

            	if(onDrawerClosedListener != null) onDrawerClosedListener.onDrawerClosed(); 
            }

            @Override
            public void onDrawerOpened(View drawerView) {

        		drawerToggle.setDrawerIndicatorEnabled(true);

            	getSupportActionBar().setTitle(drawerTitle);

            	if(onDrawerOpenListener != null) onDrawerOpenListener.onDrawerOpen(); 
            }
        };

        drawerLayout.setDrawerListener(drawerToggle);

        if (savedInstanceState == null) {

            selectMenuItem(0);

        } else {

            if (enableDebugLogging) Log.d(TAG, "restoring drawer title: " + savedInstanceState.getString("title"));

        	setTitle(savedInstanceState.getString("title"));

            FragmentManager f = getSupportFragmentManager();
        	ArrayList<String> titles = savedInstanceState.getStringArrayList("titles");
        	for(String title : titles) {

        		FragmentStack fragmentStack = (FragmentStack) f.getFragment(savedInstanceState, title);

                if (enableDebugLogging) Log.d(TAG, "restoring frag container: " + title);

                containers.put(title, fragmentStack);
        	}
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {

        super.onPostCreate(savedInstanceState);

        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);

        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
	protected void onSaveInstanceState(Bundle savedInstanceState) {

        if (enableDebugLogging) Log.d(TAG, "storing drawer title: " + drawerTitle.toString());

        savedInstanceState.putString("title", drawerTitle.toString());

        ArrayList<String> titles = new ArrayList<String>();

		FragmentManager fragmentManager = getSupportFragmentManager();
	    for (Map.Entry<String, FragmentStack> entry : containers.entrySet()) {

	    	titles.add(entry.getKey());

			fragmentManager.putFragment(savedInstanceState, entry.getKey(), entry.getValue());
	    }

        if (enableDebugLogging) Log.d(TAG, "storing frag containers: " + titles);

        savedInstanceState.putStringArrayList("titles", titles);

		super.onSaveInstanceState(savedInstanceState);
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

    	if(item.getItemId() == android.R.id.home) {

    		if (homeAsUpShowing) {

    			moveUp();

    			return true;
    		}    		
    	}

        if (drawerToggle.onOptionsItemSelected(item)) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

	public void setOnDrawerOpenListener(OnDrawerOpenListener onDrawerOpenListener) {

		this.onDrawerOpenListener = onDrawerOpenListener;
	}

	public void setOnDrawerClosedListener(OnDrawerClosedListener onDrawerClosedListener) {

		this.onDrawerClosedListener = onDrawerClosedListener;
	}
	
	public boolean isDrawerOpen() {

    	return drawerLayout.isDrawerOpen(drawerList);
    }

    private void hideDrawerToggle() {

    	drawerToggle.setDrawerIndicatorEnabled(false);

    	homeAsUpShowing = true;
    }

    private void showDrawerToggle() {

    	drawerToggle.setDrawerIndicatorEnabled(true);

    	homeAsUpShowing = false;
    }

    public void selectMenuItem(int position) {

        setDrawerItem(position);

    	String title = getDrawerItemTitle(position);

    	if(currentFragmentStack != null && title.equals(currentFragmentStack.getInitialFragmentTitle())) {

    		return;
    	}

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction tx = fragmentManager.beginTransaction();

    	FragmentStack fragment = null;

    	if(containers.get(title) == null) {

            // new fragment stack,  it needs to be added to the stack

            fragment = FragmentStack.newInstance(title, onNewDrawerItem(position));

            containers.put(title, fragment);

            setTitle(title);

        } else {

            // we have an existing fragment stack, just show it

    		fragment = containers.get(title);

			setCurrentFragmentStack(fragment);
			currentFragmentStack.setInitialFragmentTitle(title);

            // if the fragment container's stack is > 1, show the up arrow indicating that the user
            // can go up in navigation

			if(fragment.getBackStackEntryCount() > 1) {
				hideDrawerToggle();
			} else {
				showDrawerToggle();
			}
    	}

    	if (enableDebugLogging) Log.d(TAG, "adding to backStack: " + title);

        tx.replace(getContentFrameRes(), fragment, title);
        tx.setBreadCrumbShortTitle(title);
        tx.addToBackStack(title);
        tx.commit();
    }

    @Override
    public void setTitle(CharSequence title) {

        drawerTitle = title.toString();
        getSupportActionBar().setTitle(drawerTitle);
    }

    private void setDrawerItem(int position) {

        drawerList.setItemChecked(position, true);
        drawerLayout.closeDrawer(drawerView);
    }

    private void setCurrentFragmentStack(FragmentStack fragment) {

    	currentFragmentStack = fragment;
    	
    	onSetDrawerItem(fragment.getTag());
    }

    public void onSetDrawerItem(String itemTitle) {

    }

	@Override
	public void onBackPressed() {
		
		if(drawerLayout.isDrawerOpen(drawerView)) {

			drawerLayout.closeDrawer(drawerView);
			return;
		}

		if(currentFragmentStack.getBackStackEntryCount() > 1) {

			currentFragmentStack.popBackStack();
			return;
		}

		int stackSize = getBackStackEntryCount();
		if (stackSize > 0) {

			FragmentManager.BackStackEntry entry = getSupportFragmentManager().getBackStackEntryAt(stackSize - 1);
			containers.remove(entry.getName());
			getSupportFragmentManager().popBackStackImmediate();

			stackSize = getBackStackEntryCount();
			if(stackSize == 0) {

				finish();
			}
		} else {

			finish();
		}
	}
	
	protected int getBackStackEntryCount() {
		
		return getSupportFragmentManager().getBackStackEntryCount();
	}

	protected void moveUp() {

		if(drawerLayout.isDrawerOpen(drawerView)) {

			drawerLayout.closeDrawer(drawerView);

			return;
		}

		if(currentFragmentStack != null) {

			currentFragmentStack.popBackStack();

		} else {

			homeAsUpShowing = false;
		}
	}

	public void addFragmentToCurrentStack(String title, Fragment fragment) {

		currentFragmentStack.addFragment(title, fragment);
	}

	public Fragment getCurrentFragmentStack() {

		return currentFragmentStack;
	}

	public static class FragmentStack extends Fragment {

		private static String ARG_INITIAL_FRAGMENT_TITLE = "initialFragmentTitle";

		private String initialFragmentTitle;
		private Fragment initialFragment;

		private OnBackStackChangedListener backStackChangedListener;

		private static FragmentStack newInstance(String initialFragmentTitle, Fragment initialFragment) {

			FragmentStack fragmentStack = new FragmentStack();

			fragmentStack.initialFragment = initialFragment;

	        Bundle args = new Bundle();
	        args.putString(ARG_INITIAL_FRAGMENT_TITLE, initialFragmentTitle);
	        fragmentStack.setArguments(args);

			return fragmentStack;
		}

		private void addFragment(String title, Fragment fragment) {

	        FragmentManager fragmentManager = getChildFragmentManager();
	        FragmentTransaction tx = fragmentManager.beginTransaction();
	        tx.replace(((ActionBarDrawerStacksActivity) getActivity()).getFragmentContainerFrameRes(), fragment, title);
	        tx.setBreadCrumbShortTitle(title);
	        tx.addToBackStack(title);
	        tx.commit();
		}

		private void setTitle() {

			ActionBarDrawerStacksActivity activity = (ActionBarDrawerStacksActivity) getActivity();
			int stackSize = getBackStackEntryCount();

			if(stackSize > 1) {
				activity.hideDrawerToggle();
			} else {
				activity.showDrawerToggle();
			}

			if (stackSize > 0) {

				FragmentManager.BackStackEntry entry = getChildFragmentManager().getBackStackEntryAt(stackSize - 1);
				String title = entry.getBreadCrumbShortTitle().toString();
				activity.setTitle(title);
			}
		}

		private String getInitialFragmentTitle() {

			return initialFragmentTitle;
		}

		private void setInitialFragmentTitle(String initialFragmentTitle) {

			this.initialFragmentTitle = initialFragmentTitle;
		}

		private void popBackStack() {

			getChildFragmentManager().popBackStack();
		}

		private int getBackStackEntryCount() {

			return getChildFragmentManager().getBackStackEntryCount();
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

			return inflater.inflate(((ActionBarDrawerStacksActivity) getActivity()).getFragmentContainerLayoutRes(), null);
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {

			super.onActivityCreated(savedInstanceState);

			initialFragmentTitle = getArguments().getString(ARG_INITIAL_FRAGMENT_TITLE);

			ActionBarDrawerStacksActivity activity = (ActionBarDrawerStacksActivity) getActivity();
			activity.setCurrentFragmentStack(this);

			setTitle();

            backStackChangedListener = new OnBackStackChangedListener() {

                @Override
                public void onBackStackChanged() {

                    setTitle();
                }
            };

			if (savedInstanceState == null && getBackStackEntryCount() == 0) {

	    		addFragment(initialFragmentTitle, initialFragment);
	    		
	    		// just used it, should be in backstack, null it out
	    		initialFragment = null;
			}
		}

		@Override
		public void onResume() {

			super.onResume();

			getChildFragmentManager().addOnBackStackChangedListener(backStackChangedListener);
		}

		@Override
		public void onPause() {

			getChildFragmentManager().removeOnBackStackChangedListener(backStackChangedListener);

			super.onPause();
		}
	}

}