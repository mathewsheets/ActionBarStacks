package com.mathewsheets.actionbar.stackslib;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;

public abstract class ActionBarFragmentStackActivity extends ActionBarActivity {

	private static final String TAG = ActionBarFragmentStackActivity.class.getSimpleName();
	
	protected abstract int getFragmentContainerLayoutRes();
	protected abstract int getFragmentContainerFrameRes();

	private OnBackStackChangedListener backStackListener;
	private String title;

    private boolean homeAsUpShowing;
    private boolean keepHomeAsUp;

    private boolean enableDebugLogging;
	public boolean isEnableDebugLogging() {
		return enableDebugLogging;
	}
	public void setEnableDebugLogging(boolean enableDebugLogging) {
		this.enableDebugLogging = enableDebugLogging;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(getFragmentContainerLayoutRes());

		backStackListener = new OnBackStackChangedListener() {
			@Override
			public void onBackStackChanged() {

				FragmentManager f = getSupportFragmentManager();

//                if (enableDebugLogging) f.dump("", null, new PrintWriter(System.out, true), null);

				int stackSize = f.getBackStackEntryCount();
				if (stackSize > 0) {

					FragmentManager.BackStackEntry entry = f.getBackStackEntryAt(stackSize - 1);
					String title = entry.getBreadCrumbShortTitle().toString();
					setTitle(title);

					if (enableDebugLogging) Log.d(TAG, "backstack = " + stackSize +", title: " + title);

                    if (!keepHomeAsUp) {
                        if (stackSize > 1) {
                            setHomeAsUp(true);
                        } else {
                            setHomeAsUp(false);
                        }
                    }
				} else {

					if (enableDebugLogging) Log.d(TAG, "backstack = 0, finishing: " + title);

					finish();
				}
			}
		};

		if (savedInstanceState != null) {

            // reset the title for the action bar to the current title in the stack

			setTitle(savedInstanceState.getString("title"));
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState) {

        // save the current title so later on it can be use when the activity is being restored

		savedInstanceState.putString("title", title);

		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public void onResume() {

		super.onResume();

//        FragmentManager.enableDebugLogging(enableDebugLogging);

        // important to keep adding the backstack change listener on the onresume because
        // we don't want to handle change if child class adds a fragment in the oncreate

        getSupportFragmentManager().addOnBackStackChangedListener(backStackListener);

        if (!keepHomeAsUp) {
            if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
                setHomeAsUp(true);
            } else {
                setHomeAsUp(false);
            }
        }
	}

	@Override
	public void onPause() {

		getSupportFragmentManager().removeOnBackStackChangedListener(backStackListener);

		super.onPause();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:

			if (homeAsUpShowing) {

				getSupportFragmentManager().popBackStack();
			}

			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void setTitle(CharSequence title) {

        this.title = title.toString();
        getSupportActionBar().setTitle(this.title);
	}

    /*
        This will add the Fragment to the backstack
     */

	public void addFragment(String title, Fragment fragment) {

		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction tx = fragmentManager.beginTransaction();

		tx.replace(getFragmentContainerFrameRes(), fragment);
		tx.setBreadCrumbShortTitle(title); // used as the title when popping off the stack
		tx.addToBackStack(title);

		tx.commit();

        setTitle(title); // make we set the title
	}

	private void setHomeAsUp(boolean show) {

		homeAsUpShowing = show;

		getSupportActionBar().setDisplayHomeAsUpEnabled(show);
	}

    /*
        Call this to always show the up arrow regardless of stack
     */

    public void keepHomeAsUp() {

        keepHomeAsUp = true;

        setHomeAsUp(true);
    }

}
