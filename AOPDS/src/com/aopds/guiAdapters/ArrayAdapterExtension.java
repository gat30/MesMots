package com.aopds.guiAdapters;

import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.widget.ArrayAdapter;

/**
 * 
 * Extension of Android ArrayAdaper adding the addAll() functionality not already 
 * available in API 2.2. If moving the project to a more recent API just remove this class.
 * 
 * @author Julien Wollscheid | July
 *
 * @param <T> The data type to adapt.
 */
public abstract class ArrayAdapterExtension<T> extends ArrayAdapter<T> {

	/*
	 * Redeclaring framework constructors.
	 */
	
	public ArrayAdapterExtension(Context context, int resource,
			int textViewResourceId, List<T> objects) {
		super(context, resource, textViewResourceId, objects);
	}

	public ArrayAdapterExtension(Context context, int resource,
			int textViewResourceId, T[] objects) {
		super(context, resource, textViewResourceId, objects);
	}

	public ArrayAdapterExtension(Context context, int resource,
			int textViewResourceId) {
		super(context, resource, textViewResourceId);
	}

	public ArrayAdapterExtension(Context context, int textViewResourceId,
			List<T> objects) {
		super(context, textViewResourceId, objects);
	}

	public ArrayAdapterExtension(Context context, int textViewResourceId,
			T[] objects) {
		super(context, textViewResourceId, objects);
	}

	public ArrayAdapterExtension(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
	}

	/**
	 * Fill the adapter with the whole list in argument. Data will be appended 
	 * at the end of the data set.
	 * 
	 * @param newList The list to fill the adapter data with.
	 */
	public void addAll(List<T> newList) {
		
		Iterator<T> it = newList.iterator();
		
		while ( it.hasNext() ) {
			add( it.next() );
		}
		
	}
	
	
}
