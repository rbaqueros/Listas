/*
 * Copyright (c) 2015 Rafael Baquero
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.baqsoft.listas.ui.overview;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.baqsoft.listas.database.ListasDummyData;
import com.baqsoft.listas.ui.click_processor.ChecklistClickData;
import com.baqsoft.listas.R;
import com.baqsoft.listas.contentprovider.ListasContract;
import com.baqsoft.listas.ui.click_processor.RecyclerViewClickProcessor;
import com.baqsoft.listas.ui.navdrawer.NavDrawerListAdapter;

public class ListActivity extends ActionBarActivity implements RecyclerViewClickProcessor.ClickProcessor,
        LoaderManager.LoaderCallbacks<Cursor> {
    ActionBarDrawerToggle mDrawerToggle;
    ListFragment mViewFragment;
    DrawerLayout mDrawerLayout;
    NavDrawerListAdapter mListAdapter;
    Toolbar mToolbar;

    // Nav drawer projection.
    private static final String[] sNavProjection = {ListasContract.NavListContract.COLUMN_ID,
            ListasContract.NavListContract.COLUMN_TYPE,
            ListasContract.NavListContract.COLUMN_TITLE};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        // Toolbar - Actionbar
        mToolbar = (Toolbar) findViewById(R.id.category_list_toolbar);
        mToolbar.setTitle("Categories");
        setSupportActionBar(mToolbar);

        // Fill the navigation drawer.
        mDrawerLayout = (DrawerLayout) findViewById(R.id.activity_list_layout);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.left_drawer);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mViewFragment = new ListFragment();
        mListAdapter = new NavDrawerListAdapter();
        mListAdapter.setClickProcessor(this);
        recyclerView.setAdapter(mListAdapter);

        // Fetch nav drawer data.
        getLoaderManager().initLoader(0, null, this);

        // ActionBarDrawerToggle ties together the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                       /* host Activity */
                mDrawerLayout,              /* DrawerLayout object */
                mToolbar,
                R.string.drawer_open,   /* "open drawer" description for accessibility */
                R.string.drawer_close   /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
//                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
//                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        //
        // enable ActionBar app icon to behave as action to toggle nav drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Set fragment
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.recycleview_fragment, mViewFragment)
                    .commit();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_initialize:
                ListasDummyData.initializeDB(this);
                return true;

            case R.id.action_settings:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void processClick(RecyclerViewClickProcessor.ClickParams clickParams) {
        // Set checklist
        ChecklistClickData clickData = (ChecklistClickData) clickParams.clickData;
        mToolbar.setTitle(clickData.checklistTitle);
        mViewFragment.setChecklist(clickData.checklistId);

        // Close nav drawer
        mDrawerLayout.closeDrawers();
    }

    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * @param id   The ID whose loader is to be created.
     * @param args Any arguments supplied by the caller.
     * @return Return a new Loader instance that is ready to start loading.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, Uri.parse(ListasContract.NavListContract.URI_STRING),
                sNavProjection, null, null, null);
    }

    /**
     * Called when a previously created loader has finished its load.
     *
     * @param loader The Loader that has finished.
     * @param data   The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mListAdapter.changeCursor(data);
    }

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.  The application should at this point
     * remove any references it has to the Loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mListAdapter.changeCursor(null);
    }
}
