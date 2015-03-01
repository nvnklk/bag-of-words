/*
 * Copyright (C) 2015 Naveen Kulkarni.
 *
 * This file is part of Bag of Words program.
 *
 * Bag of Words is free software: you can redistribute it and/or modify it 
 * under the terms of the GNU General Public License as published by the 
 * Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version.
 *
 * Bag of Words is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License 
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along 
 * with Bag of Words program. If not, see <http://www.gnu.org/licenses/>.
 */

package ctrus.pa.util;

import org.apache.commons.lang3.StringUtils;

import ctrus.pa.party3.hash.MurmurHash3;

/**
 * A utility Class
 * @author Naveen Kulkarni
 */
public class CtrusHelper {

	/**
	 * A command line progress bar.
	 * Author: http://nakkaya.com/2009/11/08/command-line-progress-bar/
	 * @param message	Any message to be printed along side the progress bar
	 * @param current	Current count
	 * @param max		Maximum count
	 */
	public final static void progressMonitor(String message, int current, int max) {

	    StringBuilder bar = new StringBuilder("\r");
	    bar.append(message).append(" [");

	    int percent = (int) (current * 100 / max);

		for(int i = 0; i <= 50; i++) {
			if( i < (percent/2)){
				bar.append("=");
		    } else if( i == (percent/2)) {
		    	bar.append(">");
		    } else {
		    	bar.append(".");
		    }
		}

	    bar.append("]  " + percent + "%  #(" + current + "/" + max + ")");

	    // Add a new line if the processing is finished...
	    if(percent == 100)
	    	bar.append("\n");

	    System.out.print(bar.toString());
	}

	public static final void printToConsole(String message) {
		System.out.println(message);
	}

	// create a unique id using base62 encoding of a static counter
	private static final String $baseDigits = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

	public static final CharSequence uniqueId(String s) {
		
		if(s == null || StringUtils.isEmpty(s))
			throw new IllegalArgumentException("Input cannot be null or empty");

		//long idNum = MurmurHash2.hash64(sClean);
		long idNum = MurmurHash3.hashUnencodedChars(s);
		idNum = idNum < 0 ? -idNum : idNum;

		String uid = "";
		long mod = 0;
		int  division = 17;

        while( idNum != 0 ) {
            mod = idNum % division;
            uid = $baseDigits.substring( (int)mod, (int)mod + 1 ) + uid;
            idNum = idNum / division;
        }

        return uid;
	}

}
