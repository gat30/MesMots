package com.aopds.guiAdapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aopds.R;
import com.aopds.aopdsData.domain.Dictionary;

/**
 * 
 * Adapter for a dictionary list. This class is a bridge between a dictionary
 * List and a ListView.
 * 
 * @author Julien Wollscheid | July 2011
 * 
 */
public class DictionaryListAdapter extends ArrayAdapterExtension<Dictionary> {

	int textViewResourceId;

	/**
	 * 
	 * Creates the dictionary Adapter.
	 * 
	 * {@inheritDoc}
	 */
	public DictionaryListAdapter(Context context, int textViewResourceId,
			List<Dictionary> dictionaries) {
		super(context, textViewResourceId, dictionaries);
		this.textViewResourceId = textViewResourceId;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View v = convertView;

		// if the view is not inflated yet, we inflate it
		if (v == null) {

			LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);

			v = vi.inflate(textViewResourceId, null);

		}

		// getting the dictionary to display
		Dictionary dictionary = getItem(position);

		if (textViewResourceId == R.layout.dictionary_update_list_item) {

			TextView displayDic = (TextView) v
					.findViewById(R.id.dictionaryUpdateItem);

			if (dictionary != null) {
				displayDic.setText(dictionary.getCode() + " - "
						+ dictionary.getVersion());
			}

		} else {

			// if ok, displaying !
			if (dictionary != null) {

				TextView displayName = (TextView) v
						.findViewById(R.id.dictionaryItemName);

				TextView displayLanguageFrom = (TextView) v
						.findViewById(R.id.dictionaryItemLanguageFrom);

				TextView displayLanguageTo = (TextView) v
						.findViewById(R.id.dictionaryItemLanguageTo);

				if (displayName != null) {
					displayName.setText(dictionary.getName());
				}

				if (displayLanguageFrom != null) {
					displayLanguageFrom.setText(dictionary.getLanguageFrom()
							.getName());
				}

				if (displayLanguageTo != null) {
					displayLanguageTo.setText(dictionary.getLanguageTo()
							.getName());
				}

			}

		}

		return v;
	}

}
