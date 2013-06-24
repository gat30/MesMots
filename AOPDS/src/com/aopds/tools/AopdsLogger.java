package com.aopds.tools;

import java.util.Collection;
import java.util.Iterator;

import android.util.Log;

public class AopdsLogger {

	private static Boolean activated = true;
	
	public static void info(String tag, String value) {
		if ( activated ) {
			int splitLines = 70;
			if ( value.length() > splitLines ) {
				
				int nbLines = value.length() / splitLines;
				int i = 1;
				
				for (i = 1; i<=nbLines; i++) {
					Log.i(
						tag + "(" + i + ")", 
						value.substring( ((i-1)*splitLines), (i*splitLines) ) 
					);
				}
			 
				if ( (value.length() % splitLines) != 0 ) {
					Log.i(
						tag + "(" + i + ")", 
						value.substring( (i-1)*splitLines ) 
					);
				}
				
			} else {
				Log.i(tag, value);
			}
			
			
		}
	}

	public static void info(String tag, Object value) {
		if ( activated ) {
			if ( value == null ) {
				Log.i(tag, "null" );
			} else Log.i(tag, value.toString() );
		}
	}
	
	public static void info(String tag, Collection<?> value) {
		if ( activated ) {
			
			if ( value == null ) {
				Log.i(tag, "null" );
			} else {
				Log.i(tag, "Collection : " + value.getClass() + " " + value.size() + " elems\n" );
				Iterator<?> it = value.iterator();
				int i = 1;
				while ( it.hasNext() ) {
					info(tag + " elem n°" + i, it.next().toString() );
					i++;
				}
			}
			
			
		}
	}
	
	public static void error(String tag, String errorMsg, Throwable e) {
		if ( activated ) {
			Log.e(tag, errorMsg, e);
		}
	}
	
}
