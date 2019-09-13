package com.sciome.bmdexpress2.util;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;

public class TableViewUtils
{

	/**
	 * Install the keyboard handler: + CTRL + C = copy to clipboard
	 * 
	 * @param table
	 */
	public static void installCopyPasteHandler(TableView<?> table)
	{

		MenuItem copyMenu = new MenuItem("Copy");
		copyMenu.setOnAction((ActionEvent event) ->
		{
			copySelectionToClipboard(table);
		});

		ContextMenu menu = new ContextMenu();
		menu.getItems().addAll(copyMenu);
		table.setContextMenu(menu);

		// install copy/paste keyboard handler
		table.setOnKeyPressed(new TableKeyEventHandler());

	}

	/**
	 * Copy/Paste keyboard event handler. The handler uses the keyEvent's source for the clipboard data. The
	 * source must be of type TableView.
	 */
	public static class TableKeyEventHandler implements EventHandler<KeyEvent>
	{

		KeyCodeCombination copyKeyCodeCompination = new KeyCodeCombination(KeyCode.C,
				KeyCombination.CONTROL_ANY);

		public void handle(final KeyEvent keyEvent)
		{

			if (copyKeyCodeCompination.match(keyEvent))
			{

				if (keyEvent.getSource() instanceof TableView)
				{

					// copy to clipboard
					copySelectionToClipboard((TableView<?>) keyEvent.getSource());

					System.out.println("Selection copied to clipboard");

					// event is handled, consume it
					keyEvent.consume();

				}

			}

		}

	}

	/**
	 * Get table selection and copy it to the clipboard.
	 * 
	 * @param table
	 */
	public static void copySelectionToClipboard(TableView<?> table)
	{

		StringBuilder clipboardString = new StringBuilder();

		ObservableList<TablePosition> positionList = table.getSelectionModel().getSelectedCells();

		int prevRow = -1;

		for (TablePosition position : positionList)
		{

			int row = position.getRow();
			for (TableColumn tc : table.getColumns())
			{

				Object cell = tc.getCellData(row);

				// null-check: provide empty string for nulls
				if (cell == null)
				{
					cell = "";
				}

				// determine whether we advance in a row (tab) or a column
				// (newline).
				if (prevRow == row)
				{

					clipboardString.append('\t');

				}
				else if (prevRow != -1)
				{

					clipboardString.append('\n');

				}

				// create string from cell
				String text = cell.toString();

				// add new item to clipboard
				clipboardString.append(text);

				// remember previous
				prevRow = row;
			}
		}

		// create clipboard content
		final ClipboardContent clipboardContent = new ClipboardContent();
		clipboardContent.putString(clipboardString.toString());

		// set clipboard content
		Clipboard.getSystemClipboard().setContent(clipboardContent);
	}
}
