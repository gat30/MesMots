package com.aopds.guiAdapters;

import java.util.List;

import com.aopds.R;
import com.aopds.aopdsData.domain.Suggestion;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * 
 * Adapter for a Suggestion List. This class is a bridge between a Suggestion list 
 * and a ListView.
 * 
 * @author Mathieu Piasco | August 2011
 *
 */
public class SuggestionListAdapter extends ArrayAdapterExtension<Suggestion> {

	int textViewResourceId;
	
	/**
	 * Creates the adapter.
	 * 
	 * @param context The android context.
	 * @param textViewResourceId The id of the Suggestion XML layout which has to be used to display a Suggestion.
	 * @param words The Suggestion to display.
	 */
	public SuggestionListAdapter(Context context, int textViewResourceId,
			List<Suggestion> words) {
		super(context, textViewResourceId, words);
		this.textViewResourceId = textViewResourceId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View v = convertView;
		
		// inflating view if no inflated
		if ( v == null ) {
			LayoutInflater vi = 
				(LayoutInflater) getContext().
					getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
            v = vi.inflate(textViewResourceId, null);
			
		}
		
		// getting the suggestion to display
		Suggestion s = getItem(position);
		
		// if ok, display the word
		if (s != null) {

			TextView displayWord = 
				(TextView) v.findViewById( R.id.suggestionWord );
		
			TextView displayAdminDecision = 
				(TextView) v.findViewById( R.id.suggestionAdminDecision );
			
			if ( displayWord != null ) {
				displayWord.setText( s.getWord() );
			}
			
			if ( displayAdminDecision != null ) {
				displayAdminDecision.setText( s.getSynchroStatus());
			}
			
		}
		return v;
	}

	
	
	
}
