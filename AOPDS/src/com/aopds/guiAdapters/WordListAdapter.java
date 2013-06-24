package com.aopds.guiAdapters;

import java.util.List;

import com.aopds.R;

import com.aopds.aopdsData.domain.AbstractWord;
import com.aopds.aopdsData.domain.Dictionary;
import com.aopds.aopdsData.domain.Headword;
import com.aopds.aopdsData.domain.Suggestion;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * Adapter for a Headword List. This class is a bridge between a headword list 
 * and a ListView.
 * 
 * @author Julien Wollscheid | July 2011
 *
 */
public class WordListAdapter extends ArrayAdapterExtension<AbstractWord> {

	public static final int VIEW_TYPE_HEADWORD = 0;
	public static final int VIEW_TYPE_SUGGESTION = 1;
	
	Dictionary concernedDictionary;
	LayoutInflater inflater;
	
	/**
	 * Creates the adapter.
	 * 
	 * @param context The android context.
	 * @param textViewResourceId The id of the headword XML layout which has to be used to display a headword.
	 * @param words The headwords to display.
	 */
	public WordListAdapter(Context context, int textViewResourceId,
			List<AbstractWord> words, Dictionary concernedDictionary) {
		super(context, textViewResourceId, words);
		this.concernedDictionary = concernedDictionary;
		
		inflater = 
			(LayoutInflater) getContext().
				getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
	}

	 @Override
     public int getItemViewType(int position) {
		 
		 AbstractWord word = getItem(position);
		 
		 if ( word instanceof Suggestion ) {
			 return VIEW_TYPE_SUGGESTION;
		 } else {
			 if ( ((Headword) word).getLastModification() != null ) {
				 return VIEW_TYPE_SUGGESTION;
			 } else {
				 return VIEW_TYPE_HEADWORD;
			 }
			 
		 }
     }

     @Override
     public int getViewTypeCount() {
         return 2;
     }
	
    
     
	@Override
	public AbstractWord getItem(int position) {
		
		AbstractWord word = super.getItem(position);
		
		if ( word instanceof Suggestion ) {
			return word;
		} else {
			if ( ((Headword) word).getLastModification() != null ) {
				return ((Headword) word).getLastModification();
			} else {
				return word;
			}
		}
		
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View v = convertView;
		int type = getItemViewType(position);
		
		// inflating view if no inflated
		if ( v == null ) {
			
			switch (type) {
				case VIEW_TYPE_HEADWORD:
					v = inflater.inflate(R.layout.search_word_list_item, null);
				break;
				case VIEW_TYPE_SUGGESTION:
					v = inflater.inflate(R.layout.search_word_suggestion_list_item, null);
				break;
				default:
					break;
			}
			
		}
		
		
		// getting the word to display
		AbstractWord word = getItem(position);
		
		
		// if ok, display the word
		if (word != null) {

			if ( type == VIEW_TYPE_SUGGESTION ) {
				
				Suggestion sugg = (Suggestion) word;
				
				if ( sugg.getSynchroStatus().equals( Suggestion.SYNCHRO_STATUS_UNSYNCHRONIZED ) ) {
					v.setBackgroundColor( Color.BLUE );
				} else if ( sugg.getSynchroStatus().equals( Suggestion.SYNCHRO_STATUS_WAITING ) ) {
					v.setBackgroundColor( Color.YELLOW );
				} else if ( sugg.getSynchroStatus().equals( Suggestion.SYNCHRO_STATUS_REFUSED ) ) {
					v.setBackgroundColor( Color.RED );
				} else if ( sugg.getSynchroStatus().equals( Suggestion.SYNCHRO_STATUS_ACCEPTED ) ) {
					v.setBackgroundColor( Color.GREEN );
				} else {
					v.setBackgroundColor( Color.BLACK);
				}
				
				
				ImageView img = (ImageView) v.findViewById(R.id.searchWordSuggestionListItemImage);
				
				if ( sugg.isAddActionType() ) {
					img.setImageResource(R.drawable.pencil_add);
				} else if ( sugg.isDeleteActionType() ) {
					img.setImageResource(R.drawable.pencil_delete);
				} else {
					img.setImageResource(R.drawable.pencil);
				}
				
				
				TextView displayWord = 
					(TextView) v.findViewById( R.id.suggestionTopText);
			
				TextView displayEntry = 
					(TextView) v.findViewById( R.id.suggestionBottomText );
				
				
				
				if ( displayWord != null ) {
					String text = word.getWord();
					
					if ( sugg.getHeadword() != null ) {
						text += " ( " + sugg.getHeadword().getWord() + " )";
					}
					displayWord.setText( text );
				}
				
				if ( displayEntry != null ) {
					displayEntry.setText( word.getEntry() );
				}
				
			} else {
				
				
				
				TextView displayWord = 
					(TextView) v.findViewById( R.id.headwordTopText );
			
				TextView displayEntry = 
					(TextView) v.findViewById( R.id.headwordBottomText );
				
				if ( displayWord != null ) {
					displayWord.setText( word.getWord() );
				}
				
				if ( displayEntry != null ) {
					displayEntry.setText( word.getEntry() );
				}
				
			}
			
			
			
		}
				
		
		
		return v;
	}

	
	
	
}
