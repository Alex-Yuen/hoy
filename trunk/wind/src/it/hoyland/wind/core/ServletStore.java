package it.hoyland.wind.core;

import java.util.HashMap;

import javax.servlet.http.HttpServlet;

public class ServletStore extends HashMap<String, HttpServlet> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1306447171446276828L;

	private static ServletStore STORE;
}
