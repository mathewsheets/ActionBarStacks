package com.mathewsheets.actionbar.stacks;

import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;

import com.mathewsheets.actionbar.stackslib.ActionBarFragmentStackActivity;

public class SampleActionBarFragmentStackActivity extends ActionBarFragmentStackActivity {

    @SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

        setEnableDebugLogging(true);

        Bundle extras = getIntent().getExtras();

        String from = extras.getString("from");
        int colorRes = extras.getInt("color");
        int colorDarkRes = extras.getInt("color_dark");
        boolean homeAsUp = extras.getBoolean("homeAsUp");

        if (homeAsUp) {

            keepHomeAsUp();

            getSupportActionBar().setSubtitle(getString(R.string.home_as_up));
        }

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(colorRes)));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(colorDarkRes));
        }

        // Need this check here because on orientation change, savedInstanceState != null and we don't
        // want to create another fragment to put on the stack

        if(savedInstanceState == null) {

            SampleListFragment sampleFragment = new SampleListFragment();

            Bundle args = new Bundle();
            args.putString(SampleListFragment.ARG_TITLE, from);
            args.putInt(SampleListFragment.ARG_COLOR, colorRes);

            sampleFragment.setArguments(args);

            addFragment(from, sampleFragment);
        }
	}

    @Override
    protected int getFragmentContainerLayoutRes() {

    	return R.layout.fragment_container_full;
    }
    
    @Override
    protected int getFragmentContainerFrameRes() {

    	return R.id.fragment_container_frame;
    }

}
