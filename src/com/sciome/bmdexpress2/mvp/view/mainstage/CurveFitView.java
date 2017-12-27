package com.sciome.bmdexpress2.mvp.view.mainstage;

import java.net.URL;
import java.util.ResourceBundle;

import javax.swing.SwingUtilities;

import com.sciome.bmdexpress2.mvp.model.stat.BMDResult;
import com.sciome.bmdexpress2.mvp.model.stat.ProbeStatResult;
import com.sciome.bmdexpress2.mvp.presenter.mainstage.CurveFitPresenter;
import com.sciome.bmdexpress2.mvp.view.BMDExpressViewBase;
import com.sciome.bmdexpress2.mvp.viewinterface.mainstage.ICurveFitView;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;
import com.sciome.bmdexpress2.util.visualizations.curvefit.ModelGraphics;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;

public class CurveFitView extends BMDExpressViewBase implements ICurveFitView, Initializable
{
	@FXML
	SwingNode swingNode;

	CurveFitPresenter presenter;

	public CurveFitView()
	{
		this(BMDExpressEventBus.getInstance());
	}

	/*
	 * Event bus is passed as an argument so the unit tests can pass their own custom eventbus
	 */
	public CurveFitView(BMDExpressEventBus eventBus)
	{
		super();
		presenter = new CurveFitPresenter(this, eventBus);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources)
	{

	}

	@Override
	public void closeWindow()
	{
		handle_Done();
	}

	public void handle_Done()
	{
		Stage stage = (Stage) swingNode.getScene().getWindow();
		stage.close();
	}

	@SuppressWarnings("restriction")
	public void initData(BMDResult bmdResult, ProbeStatResult probeStatResult, ModelGraphics modelGraphics)
	{
		((Stage) swingNode.getScene().getWindow()).setResizable(false);

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run()
			{
				swingNode.setContent(modelGraphics);
				if (probeStatResult.getBestStatResult() != null)
					modelGraphics.setSelectedModel(probeStatResult.getBestStatResult().toString());
				else
					modelGraphics.setSelectedModel(probeStatResult.getStatResults().get(0).toString());
				modelGraphics.setSelectedProbe(probeStatResult.getProbeResponse().getProbe());
			}
		});

		this.swingNode.getScene().widthProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth,
					Number newSceneWidth)
			{
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run()
					{
						swingNode.getContent().repaint();
					}
				});

			}
		});
		this.swingNode.getScene().heightProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2)
			{

				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run()
					{
						swingNode.getContent().repaint();
					}
				});
			}

		});

	}

	@Override
	public void close()
	{
		if (presenter != null)
			presenter.destroy();

	}

}
