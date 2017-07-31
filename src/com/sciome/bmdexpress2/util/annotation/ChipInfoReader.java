
package com.sciome.bmdexpress2.util.annotation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.zip.GZIPInputStream;

import com.sciome.bmdexpress2.mvp.model.chip.ChipInfo;
import com.sciome.bmdexpress2.shared.BMDExpressConstants;

/**
 * The class of ChipInfoReader
 *
 * Read information of supported chips/arrays
 *
 * @version 1.0 4/14/2008
 * @author Longlong Yang
 */
public class ChipInfoReader
{

	public ChipInfoReader()
	{
	}

	/**
	 * Fields: arrayId, arrayName, provider, species
	 */
	public static Hashtable<String, ChipInfo> readChipsInfo(String httpURL)
	{
		String http = httpURL + BMDExpressConstants.getInstance().ARRAYDIR + "/"
				+ BMDExpressConstants.getInstance().MICROARRAYGZ;
		// System.out.println("URL: " + http);
		Hashtable<String, ChipInfo> chipsHash = new Hashtable<String, ChipInfo>();

		try
		{
			String filePath = BMDExpressConstants.getInstance().ANNOTATION_BASE_PATH + File.separator
					+ BMDExpressConstants.getInstance().ARRAYDIR;
			File inFile = new File(filePath, BMDExpressConstants.getInstance().MICROARRAYGZ);

			// Always check available update in case new supported arrays added
			if (URLUtils.updateAvailable(http, inFile))
			{// !inFile.exists() ||
				System.out.println("Download " + BMDExpressConstants.getInstance().MICROARRAYGZ);
				URLUtils.download(http, inFile);
			}

			BufferedReader reader = new BufferedReader(
					new InputStreamReader(new GZIPInputStream(new FileInputStream(inFile))));
			String line = reader.readLine();

			if (line != null && !line.isEmpty())
			{
				try
				{
					int n = Integer.parseInt(line);

					while ((line = reader.readLine()) != null)
					{
						if (!line.isEmpty())
						{
							String[] values = line.split(BMDExpressConstants.getInstance().TAB);

							if (values.length > 3)
							{ // expected = 4
								ChipInfo arrayInfo = new ChipInfo(values);
								chipsHash.put(values[4], arrayInfo);
							}

						}
					}
				}
				catch (Exception e)
				{
					// e.printStackTrace();
					// ExceptionDialog.showException(parent, "Read From File - " + MICROARRAYGZ, e);
				}
			}

			reader.close();
		}
		catch (Exception e)
		{
			// e.printStackTrace();
			// ExceptionDialog.showException(parent, "Read From File - " + MICROARRAYGZ, e);
		}

		return chipsHash;
	}
}