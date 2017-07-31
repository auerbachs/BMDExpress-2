package com.sciome.bmdexpress2.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Vector;

import com.sciome.bmdexpress2.mvp.model.DoseResponseExperiment;
import com.sciome.bmdexpress2.mvp.model.probe.Probe;
import com.sciome.bmdexpress2.mvp.model.probe.ProbeResponse;
import com.sciome.bmdexpress2.mvp.model.probe.Treatment;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;
import com.sciome.bmdexpress2.shared.eventbus.project.ShowErrorEvent;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Window;

public class ExperimentFileUtil
{
	private static ExperimentFileUtil instance = null;

	protected ExperimentFileUtil()
	{
		// Exists only to defeat instantiation.
	}

	public static ExperimentFileUtil getInstance()
	{
		if (instance == null)
		{
			instance = new ExperimentFileUtil();
		}
		return instance;
	}

	/*
	 * read an dose response experiement file and return an instance.
	 */
	public DoseResponseExperiment readFile(File infile, Window owner)
	{
		try
		{
			FileReader fr = new FileReader(infile);
			BufferedReader br = new BufferedReader(fr, 1024 * 2000);
			Vector<String[]> vecData = new Vector<String[]>();
			StringBuffer bfNotes = new StringBuffer();
			String line = "";
			int c = 0;

			DoseResponseExperiment doseResponseExperiement = new DoseResponseExperiment();

			try
			{
				while ((line = br.readLine()) != null)
				{
					if (line.indexOf("\t") >= 0 && !line.replaceAll("\\s*", "").equals(""))
					{
						String[] array = line.split("\t");
						int n = array.length;
						vecData.add(array);

						if (n > c)
						{
							c = n;
						}
					}
					else
					{
						bfNotes.append(line + "\n");
					}
				}

				String[] headers = new String[c];

				for (int j = 0; j < c; j++)
				{
					headers[j] = "Column " + j;
				}

				// let's loop through the vectors and create our experiment object

				if (vecData.size() > 1)
				{
					// get the headers and create treatment list.
					String[] experiementHeaders = vecData.get(0);

					int starti = 0;
					if (isFirstVecHeader(experiementHeaders, owner))
					{
						starti = 1;
					}
					List<Treatment> treatments = new ArrayList<>();
					for (int i = 1; i < experiementHeaders.length; i++)
					{
						Float dose = Float.valueOf(vecData.get(starti)[i]);
						String colheader = experiementHeaders[i];
						if (starti == 0)
						{
							colheader = headers[i - 1];
						}

						Treatment treatment = new Treatment(colheader, dose);
						treatments.add(treatment);

					}

					// sort the treatments and keep track of the new indexes
					// so we can put the responses in corresponding order.
					List<Treatment> orderedTreatments = new ArrayList<>(treatments.size());

					for (Treatment t : treatments)
					{
						orderedTreatments.add(t);
					}

					// sort the treatments
					Collections.sort(orderedTreatments, new Comparator<Treatment>() {

						@Override
						public int compare(Treatment o1, Treatment o2)
						{
							return o1.getDose().compareTo(o2.getDose());
						}
					});

					List<Integer> orderedIndexes = new ArrayList<>(treatments.size());

					for (Treatment t : treatments)
					{
						orderedIndexes.add(orderedTreatments.indexOf(t));
					}

					List<ProbeResponse> probeResponses = new ArrayList<>();
					// load probes and response data.

					for (int i = starti + 1; i < vecData.size(); i++)
					{
						// initialized a byte array which will be stored for speedy serialization
						String probeID = vecData.get(i)[0];
						Probe probe = new Probe();
						probe.setId(probeID);

						ProbeResponse probeResponse = new ProbeResponse();
						probeResponse.setProbe(probe);
						List<Float> responseRow = new ArrayList<>();

						for (int j = 1; j < vecData.get(i).length; j++)
						{
							try
							{
								Float doseResponse = Float.valueOf(vecData.get(i)[j]);
								responseRow.add(doseResponse);
							}
							catch (Exception e)
							{
								BMDExpressEventBus.getInstance()
										.post(new ShowErrorEvent("The value found is not numeric on line: "
												+ (i + 1) + ", column: " + (j + 1) + " of file \""
												+ infile.getName() + "\""));
								return null;
							}

						}
						if (responseRow.size() != treatments.size())
						{
							BMDExpressEventBus.getInstance()
									.post(new ShowErrorEvent(
											"Number of dose reponses does not match number of values for line: "
													+ (i + 1) + " of file \"" + infile.getName() + "\""));
							return null;
						}

						// put the responses in corresponding order to the treatments.
						List<Float> orderedResponses = new ArrayList<>(responseRow.size());
						// initialize the ordered Reponses to null
						for (@SuppressWarnings("unused")
						Float response : responseRow)
						{
							orderedResponses.add(null);
						}
						int j = 0;
						for (Float response : responseRow)
						{
							orderedResponses.set(orderedIndexes.get(j).intValue(), response);
							j++;
						}

						probeResponse.setResponses(orderedResponses);
						probeResponses.add(probeResponse);

					}

					doseResponseExperiement.setTreatments(orderedTreatments);
					doseResponseExperiement.setProbeResponses(probeResponses);
					String fileName = infile.getName();
					if (fileName.indexOf(".") > 0)
						fileName = fileName.substring(0, fileName.lastIndexOf("."));
					doseResponseExperiement.setName(fileName);

					return doseResponseExperiement;
				}
				else
				{}
			}
			catch (IOException e)
			{
				// System.out.println("Read file problem: " + e);
			}
			finally
			{
				br.close();
				fr.close();
			}
		}
		catch (FileNotFoundException e)
		{
			// System.out.println("Input file problem: " + e);
		}
		catch (Exception e)
		{
			BMDExpressEventBus.getInstance().post(new ShowErrorEvent(e.getMessage()));
			e.printStackTrace();
		}

		return null;
	}

	/*
	 * trying to auto detect whether a column is a header. Let's assume that if it is a header, then some of
	 * the names will not be numeric.
	 */
	private boolean isFirstVecHeader(String[] headers, Window owner)
	{
		for (int i = 1; i < headers.length; i++)
		{
			if (!isNumeric(headers[i]))
			{
				return true;
			}
		}

		String headerPreview = "";
		for (int i = 1; i < headers.length; i++)
		{
			headerPreview += headers[i] + "  ";
		}

		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Column headers confirmation.");
		alert.setHeaderText("Is the first line column headers?");
		alert.getDialogPane().setPrefSize(500, 300);
		alert.initOwner(owner);
		alert.initModality(Modality.WINDOW_MODAL);
		WebView webView = new WebView();
		webView.getEngine().loadContent(
				"<html><b>First line looks like this:</b><p><pre> " + headerPreview + "</pre></html>");
		webView.setPrefSize(150, 60);
		alert.getDialogPane().setContent(webView);;
		// alert.setContentText("<b> First line looks like this: </b><p>" + headerPreview);

		ButtonType buttonYes = new ButtonType("Yes");
		ButtonType buttonNo = new ButtonType("No");

		alert.getButtonTypes().setAll(buttonYes, buttonNo);

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == buttonYes)
		{
			return true;
		}

		return false;
	}

	private boolean isNumeric(String str)
	{
		try
		{
			double d = Double.parseDouble(str);
		}
		catch (NumberFormatException nfe)
		{
			return false;
		}
		return true;
	}

}
