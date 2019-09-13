package com.sciome.bmdexpress2.shared;

import java.util.Hashtable;

import org.controlsfx.control.tableview2.TableView2;

import com.sciome.bmdexpress2.util.TableViewUtils;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;

//store the tableviews in a hash and make them
// available.  This is to keep track of how the 
// table is sorted and ordered when switching between
// views.
public class TableViewCache
{

	private static TableViewCache			instance		= null;

	private Hashtable<String, TableView2>	tableViewHash	= new Hashtable<>();

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
	public TableView2 getTableView(String key)
	{
		if (tableViewHash.containsKey(key))
			return tableViewHash.get(key);

		TableView2 tv = new TableView2();
		tv.getSelectionModel().setCellSelectionEnabled(false);
		tv.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		tv.setRowFixingEnabled(true);
		tv.setRowHeaderVisible(true);

		ContextMenu m = new ContextMenu();
		m.getItems().add(new MenuItem("fix"));
		// enable copy/paste
		TableViewUtils.installCopyPasteHandler(tv);
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
