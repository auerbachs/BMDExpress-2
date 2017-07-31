package com.sciome.bmdexpress2.mvp.presenter.annotation;

import java.util.Hashtable;
import java.util.List;

import com.sciome.bmdexpress2.mvp.model.chip.ChipInfo;
import com.sciome.bmdexpress2.mvp.presenter.PresenterBase;
import com.sciome.bmdexpress2.mvp.viewinterface.annotation.IAnnotationUpdateView;
import com.sciome.bmdexpress2.shared.BMDExpressProperties;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;
import com.sciome.bmdexpress2.util.annotation.HttpUpdateWork;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;

public class AnnotationUpdatePresenter extends PresenterBase<IAnnotationUpdateView>
{

	private HttpUpdateWork httpUpdateWork;

	private Hashtable<String, ChipInfo> chipHash;

	/*
	 * Constructors
	 */

	public AnnotationUpdatePresenter(IAnnotationUpdateView view, BMDExpressEventBus eventBus)
	{
		super(view, eventBus);
	}

	public void initTableView()
	{
		// go yonder to the server and see if there are updates.

		httpUpdateWork = new HttpUpdateWork(BMDExpressProperties.getInstance().getUpdateURL());
		Task<Integer> task = new Task<Integer>() {
			@Override
			protected Integer call() throws Exception
			{

				httpUpdateWork.doInBackground();

				Platform.runLater(() ->
				{
					chipHash = httpUpdateWork.getChipsHash();
					getView().updateTableView(httpUpdateWork.getTableData());
				});

				return 0;
			}
		};

		new Thread(task).start();

	}

	public void processUpdate(List<Object[]> tableData)
	{
		int updateCount = 0;
		for (int i = 0; i < tableData.size(); i++)
		{
			if (((SimpleBooleanProperty) tableData.get(i)[0]).getValue())
			{
				updateCount++;
			}
		}
		if (updateCount == 0)
			return;

		final int finalUpdateCount = updateCount;

		Task<Integer> task = new Task<Integer>() {
			@Override
			protected Integer call() throws Exception
			{
				Platform.runLater(() ->
				{
					getView().beginUpdateProgress();
				});

				int count = 1;
				for (int i = 0; i < tableData.size(); i++)
				{
					if (((SimpleBooleanProperty) tableData.get(i)[0]).getValue())
					{
						final int currentRow = i;
						final int currentCount = count;

						Platform.runLater(() ->
						{
							getView().updateProgress("Downloading: " + (String) tableData.get(currentRow)[1],
									(double) currentCount / (double) finalUpdateCount);
						});
						httpUpdateWork.processUpdate((String) tableData.get(i)[1],
								(String) tableData.get(i)[3]);
						count++;
					}

				}

				Platform.runLater(() ->
				{
					getView().finishUpdate();
				});
				return 0;
			}
		};

		new Thread(task).start();

	}

}