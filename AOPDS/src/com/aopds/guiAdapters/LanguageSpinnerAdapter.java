package com.aopds.guiAdapters;

import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aopds.R;
import com.aopds.aopdsData.domain.Language;

/**
 * 
 * Adapter for a list of language which has to be displayed on a spinner.
 * 
 * @author Julien Wollscheid | July 2011
 *
 */
public class LanguageSpinnerAdapter extends ArrayAdapterExtension<Language> {
	
	/**
	 * The data list kept to be able to retrieve languages after.
	 */
	List<Language> data;
	
	/**
	 * Creates the adapter.
	 * 
	 * @param context The android context.
	 * @param spinnerResourceId The id of the XML layout which has to be used to display one language.
	 * @param languages The languages to display.
	 */
	public LanguageSpinnerAdapter(Context context, int spinnerResourceId,
			List<Language> languages) {
		super(context, spinnerResourceId, languages);
		data = languages;
	}
	
	/**
	 * Get a language using the abbreviation.
	 * @param abreviation The ISO-639 abbreviation of the searched language.
	 * @return The langauge whose the abbreviation is or null if not in the adapter data set.
	 */
	public Language getLanguageByAbreviation(String abreviation) {
		
		Iterator<Language> it = data.iterator();
		Language current = null;
		Boolean found = false;
		
		while ( it.hasNext() && !found ) {
			
			current = it.next();
			if ( current.getAbreviation().equals( abreviation ) ) {
				found = true;
			}
		}
		
		if ( found ) {
			return current;
		} else return null;
		
	}
	
	@Override
	public View getDropDownView (int position, View convertView, ViewGroup parent)
	{
		View v = convertView;
		
		// inflating view
		if ( v == null ) {
			
			LayoutInflater vi = 
				(LayoutInflater) getContext().
					getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
            v = vi.inflate(R.layout.language_list_item, null);
			
		}
		
		// getting language to display
		Language lang = getItem(position);
		
		if(lang != null)
		{
			// displaying language
			TextView langView = (TextView) v.findViewById(R.id.languageItemName);
			
			if ( langView != null ) {
				langView.setText( lang.getName() );
			}
			
		}
		
		return v;
		
	}
	
	@Override
	public View getView (int position, View convertView, ViewGroup parent)
	{
		View v = convertView;
		
		// inflating view
		if ( v == null ) {
			
			LayoutInflater vi = 
				(LayoutInflater) getContext().
					getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
            v = vi.inflate(R.layout.language_list_item, null);
			
		}
		
		// getting language
		Language lang = getItem(position);
		
		if(lang != null)
		{
			// displaying language
			TextView langView = (TextView) v.findViewById(R.id.languageItemName);
			
			if ( langView != null ) {
				langView.setText( lang.getName() );
			}
			
		}
		
		return v;
		
	}
}
