package com.tactics.error;

/**
 *	Custom errors for loading tiles, used for when needed properties don't exist on a map object
 */

@SuppressWarnings("serial")
public class TileLoadingException extends Exception{

	public TileLoadingException(String msg){
	  super(msg);
	}
	
}
