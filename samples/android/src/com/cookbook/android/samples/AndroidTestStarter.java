/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.cookbook.android.samples;

import java.util.List;

import com.cookbook.samples.GdxSamples;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class AndroidTestStarter extends ListActivity {
	SharedPreferences prefs;

	@Override
	public void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		List<String> testNames = GdxSamples.getNames();
		setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, testNames));

		prefs = getSharedPreferences("libgdx-tests", Context.MODE_PRIVATE);
		getListView().setBackgroundColor(Color.BLACK);
		getListView().setSelectionFromTop(prefs.getInt("index", 0), prefs.getInt("top", 0));
	}

	protected void onListItemClick (ListView listView, View view, int position, long id) {
		super.onListItemClick(listView, view, position, id);

		Editor editor = prefs.edit();
		editor.putInt("index", listView.getFirstVisiblePosition());
		editor.putInt("top", listView.getChildAt(0) == null ? 0 : listView.getChildAt(0).getTop());
		editor.commit();

		Object o = this.getListAdapter().getItem(position);
		String sampleName = o.toString();

		Bundle bundle = new Bundle();
		bundle.putString("sample", sampleName);
		Intent intent = new Intent(this, GdxSampleActivity.class);
		intent.putExtras(bundle);

		startActivity(intent);
	}

}
