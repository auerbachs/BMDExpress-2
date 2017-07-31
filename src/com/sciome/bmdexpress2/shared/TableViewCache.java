package com.sciome.bmdexpress2.shared;

import java.util.Hashtable;

import javafx.scene.control.TableView;

//store the tableviews in a hash and make them
// available.  This is to keep track of how the 
// table is sorted and ordered when switching between
// views.
public class TableViewCache
{

	private static TableViewCache			instance		= null;

	private Hashtable<String, TableView>	tableViewHash	= new Hashtable<>();

	protected TableViewCache()
	{

	}

	public static TableViewCache getInstance()
	{
		if (instance == null)
		{
			instance = new TableViewCache();
		}
		return instance;
	}

	@SuppressWarnings("rawtypes")
	public TableView getTableView(String key)
	{
		if (tableViewHash.containsKey(key))
			return tableViewHash.get(key);

		TableView tv = new TableView();
		tableViewHash.put(key, tv);
		return tv;

	}

	// This should be called when a project
	// is closed or opened to make sure all
	// tableviews are released and not misused.
	public void clear()
	{
		tableViewHash = new Hashtable<>();
	}
}
