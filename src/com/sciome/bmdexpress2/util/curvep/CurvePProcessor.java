package com.sciome.bmdexpress2.util.curvep;

//import com.sciome.bmdexpress2.util.stat.DosesStat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.sciome.bmdexpress2.mvp.model.DoseResponseExperiment;
import com.sciome.bmdexpress2.mvp.model.probe.ProbeResponse;
import com.sciome.bmdexpress2.mvp.model.probe.Treatment;
import com.sciome.bmdexpress2.mvp.model.stat.StatResult;

public class CurvePProcessor
{

	/*-- static methods for handling metric calculations
	 * 
	 */

	// --------------------------------------------------------------------------------
	// NB: move the below auxiliary static functions into the sciome-commons.math?
	// --------------------------------------------------------------------------------

	private static float smedian(Float[] m)
	{
		// calculates median value of the pre-sorted array m
		int mid = m.length >> 1;
		float mv = m[mid];
		if (m.length == (mid << 1))
		{
			mv += m[mid - 1];
			mv *= 0.5f;
		}
		return mv;
	} // end of smedian()

	private static float MAD(Float[] m, float mm, float perturb)
	{
		/*
		 * SD-like metric, median absolute difference = MAD; NB: does not require sorting of m[] since its
		 * median mm is supplied; perturb is usually small number, such as 0.000001f to avoid exact zero
		 */

		Float[] v = m.clone();
		for (int i = 0; i < m.length; i++)
			if (v[i] > mm)
				v[i] -= mm;
			else
				v[i] = mm - v[i];

		Arrays.sort(v); // needed for proper median estimate right below.
		float x = smedian(v);
		if (x < perturb)
			x = perturb;
		return x;
	} // end of MAD()

	private static float MAD(Float[] m, float perturb)
	{
		// SD-like metric, median absolute difference = MAD;
		// perturb should be a small number, such as 0.000001f to avoid exact zero
		Float[] v = m.clone();
		Arrays.sort(v);
		float x = smedian(v);
		for (int i = 0; i < v.length; i++)
			if (v[i] > x)
				v[i] -= x;
			else
				v[i] = x - v[i];
		x = smedian(v);
		if (x < perturb)
			x = perturb;
		return x;
	} // end of MAD()

	private static float MAD(Float[] m)
	{
		return MAD(m, 0.000001f);
	}

	private static Float[] TukeyBiWs(Float[] m, float c, float p)
	{
		/*
		 * returns Tukey's biweight coefficients for the array m c is number of MADs beyond which the weight
		 * will be set to 0 (and corresponding point would thus be considered as an outlier) p is a small
		 * number to add to avoid division by zero MAD (variation-like metric)
		 */
		Float[] f = m.clone();
		Arrays.sort(f);
		float piv = smedian(f);
		float madv = MAD(f, piv, p);

		for (int i = 0; i < m.length; i++)
		{// fills f[] corresponding to the order in m[]
			float zx = (m[i] - piv) / madv / c;
			float x = 1.0f - zx * zx;
			f[i] = 0.0f;
			if (x > 0)
				f[i] = x * x;
		} // for i

		return f;
	} // end of TukeyBiWs()

	private static Float[] TukeyBiWs(Float[] m, float c)
	{
		return TukeyBiWs(m, c, 0.000001f);
	}

	private static Float[] TukeyBiWs(Float[] m)
	{
		return TukeyBiWs(m, 5.0f);
	}

	private static float wMean(Float[] vals, Float[] coffs)
	{
		/*
		 * returns weighted average for vals[], using coffs coffs[] must be non-negative
		 */

		float c_sum = 0.0f, w_sum = 0.0f;
		for (int i = 0; i < vals.length; i++)
		{
			c_sum += coffs[i];
			w_sum += vals[i] * coffs[i];
		} // for i

		return (w_sum / c_sum);
	}

	private static float wSD(Float[] vals, Float[] coffs)
	{
		/*
		 * returns weighted standard deviation of vals, using coffs coffs[] must be non-negative
		 */
		float wm = wMean(vals, coffs);
		int n = vals.length;
		Float[] diff = new Float[n];

		float c_sum = 0.0f, w_sum = 0.0f;
		for (int i = 0; i < n; i++)
		{
			diff[i] = vals[i] - wm;
			c_sum += coffs[i];
			w_sum += diff[i] * diff[i] * coffs[i];
		} // for i

		w_sum *= n;
		c_sum *= n - 1;

		return (float) Math.sqrt(w_sum / c_sum);
	}

	// --------------------------------------------------------------------------------
	// NB: move the above auxiliary static functions into the sciome-commons.math?
	// --------------------------------------------------------------------------------

	public static List<Float> CollapseDoses(List<Float> allDoses)
	/*
	 * returns unique values for dose groups sorted in ascending order
	 */
	{
		// NB: can use DosesStat.java methods or duplicates removal in RealVector of sciome-commons

		// DosesStat t = new DosesStat();
		// return t.asscendingSort( allDoses.toArray(new Float[0]) ).sortedUniDoses();

		Float[] sDoses = allDoses.toArray(new Float[0]);
		Arrays.sort(sDoses);
		List<Float> usDoses = new ArrayList<Float>();
		usDoses.add(sDoses[0]);
		for (int d = 1; d < sDoses.length; d++)
		{
			if (sDoses[d].floatValue() == sDoses[d - 1].floatValue())
				continue;

			usDoses.add(sDoses[d]);
		}

		return usDoses;
	} // end of CollapseDoses()

	public static int[] DoseGroups(List<Float> allDoses, List<Float> UniqueDoses)
	{// returns number of replicates in each dose group
		int groups[] = new int[UniqueDoses.size()];

		for (int i = 0; i < groups.length; i++)
		{
			Float g = UniqueDoses.get(i);
			for (int ad = allDoses.indexOf(g); ad <= allDoses.lastIndexOf(g); ad++)
				if (allDoses.get(ad).floatValue() == g)
					groups[i]++;
		}

		return groups;
	}

	public static List<Float> calc_WgtAvResponses(List<Float> allDoses, List<Float> allResponses)
	{
		/*
		 * Calculates weighted average response for each dose group using Tukey's biweight method
		 */
		List<Float> D = CollapseDoses(allDoses);
		List<Float> R = new ArrayList<Float>();

		for (int d = 0; d < D.size(); d++)
		{
			float g = D.get(d);
			List<Float> gR = new ArrayList<Float>();

			for (int ad = allDoses.indexOf(g); ad <= allDoses.lastIndexOf(g); ad++)
				if (allDoses.get(ad).floatValue() == g)
					gR.add(allResponses.get(ad));

			Float[] gResps = gR.toArray(new Float[0]);
			Float[] cfs = TukeyBiWs(gResps, 5.0f, 0.00001f);

			R.add(wMean(gResps, cfs));
		} // for d

		return R;
	} // end of calc_WgtAvResponses()

	public static float calc_PulledSD(List<Float> allDoses, List<Float> allResponses)
	{
		/*
		 * Calculates pulled st.dev from entire dose-response ignoring dose groups with 0-variance
		 */

		List<Float> D = CollapseDoses(allDoses);

		float pullv = 0.0f;
		int npullv = 0;

		for (int d = 0; d < D.size(); d++)
		{
			float g = D.get(d), x = 0.0f;
			List<Float> gR = new ArrayList<Float>();

			for (int ad = allDoses.indexOf(g); ad <= allDoses.lastIndexOf(g); ad++)
				if (allDoses.get(ad).floatValue() == g)
				{
					gR.add(allResponses.get(ad));
					x += allResponses.get(ad);
				}

			x /= gR.size();
			for (int bd = 0; bd < gR.size(); bd++)
			{
				float dx = gR.get(bd) - x;
				if (dx == 0.0f)
					continue; // skips 0-variance points - assume those are missing
				pullv += dx * dx;
				npullv++;
			}
		} // for d

		return (float) Math.sqrt((double) pullv / (npullv - 1));
	}

	public static float calc_PulledMAD(List<Float> allDoses, List<Float> allResponses, boolean doseMeans)
	{
		/*
		 * Calculates pulled median absolute difference from entire dose-response ignoring dose groups with
		 * 0-variance Prior to pooling, if doseMeans is true, simple average is used for each dose group, when
		 * calculating absolute differences for that dose group, otherwise - dose group median is used. After
		 * pooling, median value is returned
		 */

		List<Float> D = CollapseDoses(allDoses);
		List<Float> RS = new ArrayList<Float>();

		for (int d = 0; d < D.size(); d++)
		{
			float g = D.get(d), x = 0.0f;
			List<Float> gR = new ArrayList<Float>();

			for (int ad = allDoses.indexOf(g); ad <= allDoses.lastIndexOf(g); ad++)
				if (allDoses.get(ad).floatValue() == g)
				{
					gR.add(allResponses.get(ad));
					x += allResponses.get(ad);
				}

			x /= gR.size();

			if (!doseMeans)
			{
				Float[] v = gR.toArray(new Float[0]);
				Arrays.sort(v);
				x = smedian(v);
			}

			for (int bd = 0; bd < gR.size(); bd++)
			{
				float dx = Math.abs(gR.get(bd) - x);
				if (dx < 0.000001f)
					continue; // skip zero differences, likely from degenerate replicate points that are not
								// true measurements but "fill-ins" for missing data
				RS.add(dx);
			}
		} // for d

		if (RS.size() == 0)
			return 0.0f;
		if (RS.size() == 1)
			return RS.get(0);

		Float[] ads = RS.toArray(new Float[0]);
		Arrays.sort(ads);

		return (smedian(ads));
	} // end of calc_PulledMAD()

	public static List<Float> calc_WgtSdResponses(List<Float> allDoses, List<Float> allResponses)
	{
		/*
		 * Calculates weighted st.dev of response for each dose group using Tukey's biweight method
		 */
		List<Float> D = CollapseDoses(allDoses);
		List<Float> RS = new ArrayList<Float>();

		for (int d = 0; d < D.size(); d++)
		{
			float g = D.get(d);
			List<Float> gR = new ArrayList<Float>();

			for (int ad = allDoses.indexOf(g); ad <= allDoses.lastIndexOf(g); ad++)
				if (allDoses.get(ad).floatValue() == g)
					gR.add(allResponses.get(ad));

			Float[] gResps = gR.toArray(new Float[0]);
			Float[] cfs = TukeyBiWs(gResps, 5.0f, 0.00001f);

			float csd = wSD(gResps, cfs);
			RS.add(csd);
		} // for d

		float x = calc_PulledMAD(allDoses, allResponses, false);

		for (int d = 0; d < RS.size(); d++)
		{// checks and replaces those SDs that are below pulled SD
			if (x > RS.get(d))
				RS.set(d, x);
		}
		return RS;
	} // end of calc_WgtSdResponses()

	public static List<Float> logBaseDoses(List<Float> D, int FirstDoseBaseFix) throws Exception
	/*
	 * logBaseDoses() converts Doses to log10 scale and handles first dose, if 0 FirstDoseBaseFix - default
	 * value to use for the "untreated" (zero) dose, - if it is = 0 then same spacing is used as between two
	 * following doses
	 */
	{
		List<Float> NewD = new ArrayList<Float>();
		int N = D.size();
		boolean fDoseRedo = false;

		for (int i = 0; i < N; i++)
		{
			Float currd = D.get(i);

			if (currd > 0)
				NewD.add((float) Math.log10(currd));
			else
			{
				if ((i == 0) && (N > 2))
				{
					fDoseRedo = true;
					NewD.add(0.0f);
				}
				else
					throw new Exception("Cannot log-transform supplied doses");
			}
		} // for i

		if (fDoseRedo)
		{
			float Fixer = FirstDoseBaseFix; // e.g., -12, -24 (Avogadro#), etc.

			if (FirstDoseBaseFix == 0)
				Fixer = 2 * NewD.get(1) - NewD.get(2);
			NewD.set(0, Fixer);
		}

		return NewD;
	} // end of logBaseDoses()

	public static float ImputeDose(List<Float> D, List<Float> V, float L)
	/*
	 * Interpolates the dose at which the L threshold of response is reached D - unique doses (can be
	 * log-transformed) V - responses corresponding to D[] L - threshold response
	 */
	{
		int e = D.size() - 1;
		Float[] sv = V.toArray(new Float[0]);
		Arrays.sort(sv);

		float iD = D.get(e) + 1000.0f; // default invalid value (out of dose range)

		if ((sv[e] < L) || (L < sv[0]))
			return iD;

		for (int s = 0; s < e; s++)
		{
			int z = s + 1;
			if (L == V.get(s).floatValue())
				return D.get(s); // to handle exact hit

			float Vsz = Math.abs(V.get(z) - V.get(s));
			if (Vsz < Math.abs(V.get(z) - L))
				continue;

			if (Vsz < Math.abs(V.get(s) - L))
				continue;

			iD = D.get(s) + (D.get(z) - D.get(s)) * (L - V.get(s)) / (V.get(z) - V.get(s));

			break;
		}

		return iD;
	} // end of ImputeDose()

	public static float SafeImputeDose(List<Float> D, List<Float> V, float L)
	/*
	 * Interpolates the dose at which the L threshold of response is reached D - unique doses (can be
	 * log-transformed) V - responses corresponding to D[] L - threshold response NB: puts additional
	 * restrictions on imputation near 0th (untreated) dose, where degenerate cases can happen (i.e.
	 * __-------)
	 */
	{
		int e = D.size() - 1;
		Float[] sv = V.toArray(new Float[0]);
		Arrays.sort(sv);

		float iD = D.get(e) + 1000.0f; // default invalid value (out of dose range)

		if ((sv[e] < L) || (L < sv[0]))
			return iD;

		for (int s = 0; s < e; s++)
		{
			int z = s + 1;
			if (L == V.get(s).floatValue())
				return D.get(s); // to handle exact hit

			float Vsz = Math.abs(V.get(z) - V.get(s));
			if (Vsz < Math.abs(V.get(z) - L))
				continue;

			if (Vsz < Math.abs(V.get(s) - L))
				continue;

			iD = D.get(s) + (D.get(z) - D.get(s)) * (L - V.get(s)) / (V.get(z) - V.get(s));

			if (s == 0)
			{// safe impute for cases when log-transformed doses have untreated Dose as arbitrary small number
				int s2 = z + 1;

				// impute from the next spline (s+1; s+2) backwards
				float iD2 = D.get(s2) + (D.get(z) - D.get(s2)) * (L - V.get(s2)) / (V.get(z) - V.get(s2));

				// if this spline has a good slope, then apply its extrapolation
				if (Float.isFinite(iD2))
					iD2 = Math.min(iD2, D.get(z));
				else
					iD2 = D.get(z);

				// pick most conservative imputation, considering this could be a degenerate dose-response
				iD = Math.max(iD, iD2);

				// 2019.07 additional limit for a below-first-dose imputation
				// (e.g., for serial dilutions will stop at a dose smaller by one dilution factor than the
				// first dose)
				iD = Math.max(iD, D.get(1) * 2 - D.get(2));
			}
			break;
		}

		return iD;
	} // end of SafeImputeDose()

	public static float get_baseline_response(List<Float> allD, List<Float> allR)
	{// calculates and returns the response (signal) value for the control group

		List<Float> avr = calc_WgtAvResponses(allD, allR);
		return avr.get(0);
	}

	public static float get_baseline_SD(List<Float> allD, List<Float> allR)
	{
		/*
		 * calculates and returns the standard deviation for the control group, which is based on weighted
		 * average of control group replicates or on the pulled variance (whichever is larger)
		 */

		List<Float> sdr = calc_WgtSdResponses(allD, allR);
		return sdr.get(0);
	}

	public static float calc_PODR_bySD(List<Float> allD, List<Float> allR, float Z_thr)
	{
		/*
		 * calculates point of departure (POD) response, based on supplied dose-response data the signal
		 * direction should be given as the sign of Z_thr (<0 for downward trend, >0 for upward)
		 */

		float b = get_baseline_response(allD, allR), bs = get_baseline_SD(allD, allR);
		return (b + Z_thr * bs);
	}

	public static float calc_PODR_bySD(float base, float base_sd, float Z_thr)
	{
		/*
		 * calculates point of departure (POD) response, based on supplied baseline and its st.dev the signal
		 * direction should be given as the sign of Z_thr (<0 for downward trend, >0 for upward)
		 */
		return (base + Z_thr * base_sd);
	}

	public static float calc_PODR_byFoldChange(List<Float> allD, List<Float> allR, float fold_thr)
	{
		/*
		 * calculates point of departure (POD) response, based on supplied dose-response data and fold change
		 * curve direction should be supplied by fold_thr (0..1 for downward trend, >1 for upward trend)
		 */

		float b = get_baseline_response(allD, allR);
		if (fold_thr < 0.0f)
			return b;
		return (b * fold_thr);
	}

	public static float calc_PODR_byFoldChange(float b, float fold_thr)
	{
		/*
		 * calculates point of departure (POD) response, based on supplied baseline signal and fold change
		 * threshold (fold_thr) curve direction should be supplied by fold_thr (0..1 for downward trend, >1
		 * for upward trend)
		 */

		if (fold_thr < 0.0f)
			return b;
		return (b * fold_thr);
	}

	public static float calc_POD(List<Float> allD, List<Float> allR, float BMR, boolean UseLog)
	{
		/*
		 * autonomous version that does all needed auxiliary calculations (good for external use) returns a
		 * POD estimate based on supplied response level L
		 * 
		 * allD - all doses, allR - all responses (for all replicates in all dose groups) UseLog - if on, the
		 * doses will be log-transformed
		 */

		List<Float> avr = calc_WgtAvResponses(allD, allR);
		List<Float> uD = CollapseDoses(allD), ulD;

		ulD = uD;
		if (UseLog)
			try
			{
				ulD = logBaseDoses(uD, -24); // use lowest theoretical limit for the 0-dose
			}
			catch (Exception e)
			{

				System.out.println("dose log-transform failed.");
				return (uD.get(uD.size() - 1) + 1000.0f);
				// e.printStackTrace();
			}

		return SafeImputeDose(ulD, avr, BMR);
	}

	public static float calc_POD(List<Float> ud, List<Float> avr, float BMR)
	{
		/*
		 * shortcut version, skips some auxiliary calculations; returns a POD estimate for supplied response
		 * level, if number larger than highest dose is returned, it indicates no POD can be estimated
		 */

		return SafeImputeDose(ud, avr, BMR);
	}

	public static float calc_AUC(List<Float> D, List<Float> R)
	/*
	 * returns AUC - area-under-curve relative to baseline, calculated by trapezoids method on: unique doses
	 * in ascending order (D) and respective responses (R) First value in D[] is assumed to be untreated
	 * control and used as baseline
	 */
	{
		float fAUC = 0.0f; // reset AUC

		int N = D.size();
		if (R.size() == N)
		{
			for (int i = 1; i < N; i++)
			{
				fAUC += (R.get(i) + R.get(i - 1)) * (D.get(i) - D.get(i - 1)) / 2;
			}

			fAUC -= (D.get(N - 1) - D.get(0)) * R.get(0);
		}
		return fAUC;
	} // end of calc_AUC()

	public static float calc_wAUC(float AUC, float POD, List<Float> Doses)
	/*
	 * returns AUC normalized by point of departure (POD) and dose test range NB: Doses, AUC, and POD have to
	 * be on the matching scale of dose units
	 */
	{
		float UnDose = Doses.get(0), LoDose = Doses.get(1), HiDose = Doses.get(Doses.size() - 1);

		if (POD > HiDose)
			return 0.0f;

		if (POD < UnDose)
			return 0.0f;

		Float wAUC = AUC; // signed area-under-curve, such as from calc_AUC()

		wAUC /= HiDose - UnDose;
		wAUC *= LoDose - UnDose; // norm by POD relative to the LoDose (this + next op act as a scaling
									// coefficient)
		wAUC /= POD - UnDose;

		return wAUC;
	}

	public static float parametric_val(float D0, List<Float> P, int type)
	/*
	 * deprecated, there is a StatgetResponseAt() in StatResult class which stores parametric calculates
	 * response at D0 dose based on parametric curve model, P contains curve coefficients, type defines
	 * math.equation: 0 - polynomial, 1 - power, 2 - exponential, 3 - log, 4 - Hill
	 */
	{
		float rBase = 0.0f;

		rBase = P.get(0);
		if (type == 0) // polynomial (incl. linear and constant cases)
		{// y(x) = P[0] + P[1]*x + ... + P[n]*x^n
			for (int i = 1; i < P.size(); i++)
				rBase += P.get(i) * Math.pow(D0, i);
		}

		if (type == 1) // power
		{// y(x) = P[0] + P[1]*x^P[2]
			rBase += P.get(1) * Math.pow(D0, P.get(2));
		}

		if (type == 2) // exponential
		{// y(x) = P[0] * { P[2] - (P[2]-1)* exp( P[1] * x^P[3] ) }

			rBase = (float) Math.exp(P.get(1) * Math.pow(D0, P.get(3)));
			rBase *= 1 - P.get(2);
			rBase += P.get(2);
			rBase *= P.get(0);
		}

		if (type == 3) // logarithmic
		{// y(x) = P[0] + P[1]*ln(x)
			rBase += P.get(1) * Math.log(D0);
		}

		if (type == 4) // Hill
		{// y(x0 = P[0] + P[1] - P[1]P[3]/(x^P[2] + P[3])

			rBase += P.get(1);
			rBase -= P.get(1) * P.get(3) / (Math.pow(D0, P.get(2)) + P.get(3));
		}

		return rBase;
	} // end of parametric_val()

	public static float intg_log_AUC(List<Float> D, StatResult statResult, int type, int log0fix, int npoints)
	/*
	 * log10-integrates several curve types to calculate AUC, P contains curve coefficients, type defines
	 * math.equation: 0 - polynomial, 1 - power, 2 - exponential, 3 - log, 4 - Hill
	 * 
	 * calls logBaseDoses(D, log0fix) to obtain log10 transform for D[]
	 */
	{
		List<Float> luD;

		try
		{
			luD = logBaseDoses(D, log0fix);
		}
		catch (Exception e)
		{
			return 0.0f;
		}

		List<Float> estR = new ArrayList<Float>(); // estimated responses
		List<Float> estD = new ArrayList<Float>(); // sampled log-doses

		// since we cannot analytically integrate all the cases, switch to trapezoid estimate instead with
		// arbitrary precision (npoints)
		int nscan = npoints - 3;
		float hiD = luD.get(luD.size() - 1), loD = luD.get(1);

		estD.add(luD.get(0));
		estR.add((float) statResult.getResponseAt(0.0));

		float stepD = (hiD - loD) / nscan;

		for (int p = -1; p <= nscan; p++)
		{
			float startD = loD + p * stepD;

			estD.add(startD);
			estR.add((float) statResult.getResponseAt(Math.pow(10, startD)));
		}

		return calc_AUC(estD, estR);
	} // end of intg_log_AUC()

	public static float intg_AUC(List<Float> D, List<Float> P, int type)
	{
		/*
		 * analytically integrates several curve types to calculate AUC, P contains curve coefficients, type
		 * defines math.equation: 0 - polynomial, 1 - power, 2 - exponential, 3 - log, 4 - Hill
		 */

		int N = P.size();
		float hiD = D.get(N - 1), unD = D.get(0);
		float iAUC = parametric_val(unD, P, type);
		iAUC *= unD - hiD; // now iAUC contains -baseline's AUC;

		if (type == 0) // polynomial, including linear and constant cases
		{
			// y(x) = P[0] + P[1]*x + ... + P[n]*x^n
			// F(y)dx = C + P[0]*x + P[1]*x^2/2 + ... P[n]*x^(n+1)/(n+1)

			for (int i = 0; i < N; i++)
			{
				float c = P.get(i);
				if (i == 0)
				{
					iAUC += c * (hiD - unD);
					continue;
				}
				int i1 = i + 1;
				c *= Math.pow(hiD, i1) - Math.pow(unD, i1);
				c /= i1;
				iAUC += c;
			}
		}

		if (type == 1) // power
		{
			// y(x) = P[0] + P[1]*x^P[2]
			// F(y)dx = C + P[0]*x + P[1]*x^(P[2]+1)/(P[2]+1)

			float pp = P.get(2) + 1;
			float c = P.get(1) / pp;
			c *= Math.pow(hiD, pp) - Math.pow(unD, pp);

			iAUC += P.get(0) * (hiD - unD) + c;
		}

		if (type == 2) // exponential
		{
			// y(x) = P[0] * { P[2] - (P[2]-1)* exp( P[1] * x^P[3] ) }
			// F(y)dx = C + P[0] * { P[2]*x - (P[2]-1)* exp( P[1] * x^P[3] )/P[1]/P[3]/x^(P[3]-1) }

			float pp = P.get(3) - 1;
			float c = 1 - P.get(2);
			c /= P.get(1) * P.get(3) / P.get(0);

			float p1 = (float) Math.exp(Math.pow(unD, P.get(3)) * P.get(1));
			p1 /= Math.pow(unD, pp);

			float p2 = (float) Math.exp(Math.pow(hiD, P.get(3)) * P.get(1));
			p2 /= Math.pow(hiD, pp);

			iAUC += P.get(0) * P.get(2) * (hiD - unD) + c * (p2 - p1);
		}

		if (type == 3) // logarithmic
		{// NB: this type will be a issue for cases where x = 0; currently not supported

			// y(x) = P[0] + P[1]*ln(x)
			// F(y)dx = C + P[0]*x + x(ln(x) - 1)P[1]

			float c = P.get(1);
			c *= hiD * Math.log(hiD) - unD * Math.log(unD);

			iAUC += (P.get(0) - P.get(1)) * (hiD - unD) + c;
		}

		if (type == 4) // Hill
		{
			// y(x) = P[0] + P[1]*x^P[2] / ( x^P[2] + P[3]) =
			// P[0] + P[1] - P[1]P[3]/(x^P[2] + P[3])

			// F(y)dx = C + x^

		}

		// other types can be added and handled below

		return 0.0f;
	} // end of intg_AUC()

	public static void shift_dr_group(List<Float> allD, List<Float> allR, Float TargetDose, Float RShift)
	{
		for (int td = allD.indexOf(TargetDose); td <= allD.lastIndexOf(TargetDose); td++)
			if (allD.get(td).floatValue() == TargetDose)
			{
				Float xx = allR.get(td) + RShift;
				allR.set(td, xx);
			}
	}

	public static boolean dr_OOR(Float base_av, Float base_sd, Float x_av, Float x_sd)
	{// out-of-range check for two intervals, ci and f, defined by average and sd
		Float davx = Math.abs(base_av - x_av);
		if (davx > Math.max(base_sd, x_sd))
			return true;
		return false;
	}

	public static float[] scan_dr_4mono(List<Float> avR, List<Float> sdR, int mode)
	// Calculates monotonicity signs (from -1 to 1) measured for each interval in Dose-Response data (uses
	// AVERAGE and SD of responses as input)
	// mode: 0 - from left-to-right, 1 - from right-to-left, 2 - harmonic mean of both, 3 - strict (min of
	// both), 4 - lax (max of both)
	{

		int m = avR.size() - 1;
		float Zs[] = new float[m];

		for (int ci = 0; ci < m; ci++)
		{
			// estimate sigma from standard deviations of (ci) and (ci+1) replicates
			Float esd = 0.0f, esd1 = sdR.get(ci), esd2 = sdR.get(ci + 1);
			if (mode == 0)
				esd = esd1;
			if (mode == 1)
				esd = esd2;
			if (mode == 2)
				esd = 2.0f * esd1 * esd2 / (esd1 + esd2);
			if (mode == 3)
				esd = Math.max(esd1, esd2);
			if (mode == 4)
				esd = Math.min(esd1, esd2);

			if (!Float.isFinite(esd) || (esd < 0.0001))
				esd = 0.0001f; // a crutch to fix fake input data with duplicate "replicates" causing 0 st.dev

			// difference of means is divided by estimated sigma
			Zs[ci] = avR.get(ci + 1) - avR.get(ci); // esd;
		}
		return (Zs);
	}

	public static int monotonize(List<Float> allD, List<Float> allR, List<Float> corrR, int Direction)
	{
		// returns number of corrected points, which themselves are written into corrR
		corrR.clear();
		corrR.addAll(allR);

		List<Float> sdr = calc_WgtSdResponses(allD, allR);
		List<Float> avr = calc_WgtAvResponses(allD, allR);
		List<Float> unqD = CollapseDoses(allD);

		// -- luD will be needed for extrapolation
		List<Float> luD;
		try
		{
			luD = logBaseDoses(unqD, -24);

		}
		catch (Exception e)
		{
			System.out.println("problems with calculations");
			return 0;
		}
		// -----------------

		int n = unqD.size();
		// mask of corrections
		byte[] Baddies = new byte[n];
		for (int v = 0; v < Baddies.length; v++)
			Baddies[v] = 0;

		Float BBA = avr.get(0), BBS = sdr.get(0);
		Float BL = BBA - BBS, BU = BBA + BBS;

		if (Direction == 0)
		{// constant curves
			int ncorr = 0;
			for (int v = 1; v < n; v++)
			{
				Float vR = avr.get(v);
				if ((vR > BU) || (vR < BL))
				{
					ncorr++;
					Baddies[v] = 1;
					Float diff = BBA - vR, cd = unqD.get(v);
					shift_dr_group(allD, corrR, cd, diff);
				}
			}

			return ncorr;
		}

		// below are supposed-to-be-monotonic cases

		// get extreme response values
		Float mna = BBA, mxa = BBA;
		for (int v = 1; v < n; v++)
		{
			Float ca = avr.get(v);
			if (ca > mxa)
				mxa = ca;
			if (ca < mna)
				mna = ca;
		}

		Float extr = mxa;
		if (Direction < 0)
			extr = mna;

		// invalidate non-conforming tail, when obvious
		for (int u = n - 1; u > 0; u--)
		{
			Float cr = avr.get(u), csd = sdr.get(u);
			Float crl = cr - csd, cru = cr + csd;
			if ((extr < crl) || (extr > cru))
				Baddies[u] = 1;
			else
				break;
		}

		// Detect a minimum set of violators
		byte[] TrialBest = Baddies.clone();
		int tbSize = Baddies.length; // #corrections to do, will be updated
		int bdSize = 0;
		for (int v = 0; v < Baddies.length; v++)
			bdSize += Baddies[v];

		// scan_dr_4mono(avr, sdr, 0); //addl invalidation of glitches, but can be tricky

		for (int v = 0; v < n; v++)
		{
			if (Baddies[v] == 1)
				continue;

			// v is the initial seed for the "trusted" point
			byte[] Trial = Baddies.clone();
			int f = v, ci = v;

			// first, check forward from v
			while (++ci < n)
			{
				if (Baddies[ci] == 1)
					continue;
				Float ci_a = avr.get(ci), f_a = avr.get(f), ci_s = sdr.get(ci), f_s = sdr.get(f);
				if (dr_OOR(f_a, f_s, ci_a, ci_s))
				{
					if (Direction * (ci_a - f_a) < 0)
					{
						Trial[ci] = 1;
						continue;
					}
				}
				f = ci;
			}

			// then check backward from v
			f = v;
			ci = v;
			while (ci > 1)
			{// avoid changing untreated (control) sample point
				ci--;
				if (Baddies[ci] == 1)
					continue;
				Float ci_a = avr.get(ci), f_a = avr.get(f), ci_s = sdr.get(ci), f_s = sdr.get(f);
				if (dr_OOR(f_a, f_s, ci_a, ci_s))
				{
					if (Direction * (f_a - ci_a) < 0)
					{
						Trial[ci] = 1;
						continue;
					}
				}
				f = ci;
			}

			f = 0;
			for (int z = 0; z < Trial.length; z++)
				f += Trial[z];
			if (tbSize < f)
				continue;
			if (tbSize > f)
			{
				TrialBest = Trial;
				tbSize = f;
			}

			if (tbSize == bdSize)
				break; // optimum reached
		} // v

		Baddies = TrialBest;
		for (int ci = 1; ci < n; ci++)
		{
			if (Baddies[ci] == 0)
				continue;
			int f = ci, v = ci; // find valid points around ci
			while (v > 0)
				if (Baddies[v] == 1)
					v--;
				else
					break;
			while (f < n)
				if (Baddies[f] == 1)
					f++;
				else
					break;

			Float new_ci = avr.get(v);
			if (f < n)
			{// interpolate
				new_ci = luD.get(ci) - luD.get(v);
				new_ci /= luD.get(f) - luD.get(v);
				new_ci *= avr.get(f) - avr.get(v);
				new_ci += avr.get(v);
			}

			shift_dr_group(allD, corrR, unqD.get(ci), new_ci - avr.get(ci)); // apply corrections
		}
		return tbSize;
	}

	public static List<Float> r_sample(int n, Float avr, Float sdr)
	{// samples n responses from normal distribution with the mean = avr and st.dev = sdr
		List<Float> bootr = new ArrayList<Float>();
		java.util.Random xx = new java.util.Random();
		for (int nn = 0; nn < n; nn++)
		{
			double v = avr + sdr * xx.nextGaussian();
			bootr.add((float) v);
		}
		return (bootr);
	}

	public static float get_dr_signal(List<Float> r)
	{// r is array of single-value responses (one per dose, such as averaged curve, etc.)
		int n = r.size() - 1;
		float base = r.get(0), sig = 0.0f;
		for (int i = 1; i <= n; i++)
			sig += r.get(i) - base;

		return sig / n; // returns averaged signal relative to control (0-element)
	}

	public static List<Float> get_combi_dr(int[] ndoses, List<Float> r)
	{// generates one random combination of responses picked from each dose group of replicates (ndoses)
		List<Float> c_dr = new ArrayList<Float>();
		java.util.Random cc = new java.util.Random();

		for (int i = 0, shift = 0; i < ndoses.length; i++)
		{
			int picked = cc.nextInt(ndoses[i]) + shift;
			c_dr.add(r.get(picked));
			shift += ndoses[i];
		}

		return c_dr;
	}

	public static List<Float> get_dr_sample(int[] ndoses, List<Float> avr, List<Float> sdr)
	{// gets one bootstrap sample of a curve, based on mean and sd responses provided (ndoses specify
		// #replicates for each dose)
		List<Float> rand_dr = new ArrayList<Float>();
		for (int i = 0; i < ndoses.length; i++)
		{
			List<Float> cgroup = r_sample(ndoses[i], avr.get(i), sdr.get(i));
			rand_dr.addAll(cgroup);
		}

		return rand_dr;
	}

	public static List<Float> curvePcorr(List<Float> allD, List<Float> allR, List<Float> dr0, float BMR,
			int mono, int nboot, float p)
	{
		/*
		 * corrects curves monotonically based on supplied direction in mono (0 - flat, 1 - rising, -1 -
		 * falling) bootstraps to estimate POD and AUC (only if nboot*p > 1), p is pvalue for confidence
		 * interval boundaries on returned metrics returns list of 10 values: 1st = #fit-score, 0..1, (the
		 * higher the fewer are the corrections) 2nd - 4th POD triplet (lower confidence, POD, upper
		 * confidence) 5th - 7th AUC triplet 8th - 10th wAUC triplet
		 */

		List<Float> avR = calc_WgtAvResponses(allD, allR);
		List<Float> unqD = CollapseDoses(allD);
		List<Float> sdR = calc_WgtSdResponses(allD, allR); // SDs will not change during corrections

		List<Float> luD;
		try
		{
			luD = logBaseDoses(unqD, -24);
		}

		catch (Exception e)
		{
			System.out.println("curvep failure, check input data");
			return null;
		}

		// List<Float> dr0 = new ArrayList<Float>(), dr1 = new ArrayList<Float>();
		List<Float> dr1 = new ArrayList<Float>();
		int nfixed = monotonize(allD, allR, dr0, mono);

		List<Float> xx_avR = calc_WgtAvResponses(allD, dr0);
		Float myAUC = calc_AUC(luD, xx_avR);
		Float myPOD = calc_POD(luD, xx_avR, BMR);
		Float mywAUC = calc_wAUC(myAUC, myPOD, luD);

		// normally, this very function is called only when significant response is detected,
		// so the below signal-estimates should not be near-0
		float asis_sgnl = Math.abs(get_dr_signal(avR)), corr_sgnl = Math.abs(get_dr_signal(xx_avR));
		float fit_score = Math.min(asis_sgnl, corr_sgnl) / Math.max(asis_sgnl, corr_sgnl);
		if (!Float.isFinite(fit_score))
			fit_score = -1.0f;
		if (nfixed == 0)
			fit_score = 1.0f;

		if (myPOD > luD.get(luD.size() - 1))
			myPOD = Float.NaN; // luD.get(luD.size()-1); //fixes NA PODs

		List<Float> metrics = new ArrayList<Float>(); // results
		if (p * nboot < 1.0f) // skip bootstrap
		{
			metrics.add(fit_score);

			metrics.add(myAUC);
			metrics.add(myAUC);
			metrics.add(myAUC);

			metrics.add(myPOD);
			metrics.add(myPOD);
			metrics.add(myPOD);

			metrics.add(mywAUC);
			metrics.add(mywAUC);
			metrics.add(mywAUC);
			return metrics;
		}

		int[] ngroups = DoseGroups(allD, unqD);

		List<Float> bAUC = new ArrayList<Float>();
		List<Float> bPOD = new ArrayList<Float>();
		List<Float> bwAUC = new ArrayList<Float>();

		List<Double> bFit = new ArrayList<Double>();
		for (int s = 0; s < nboot; s++)
		{
			List<Float> curr_cdr = get_combi_dr(ngroups, allR);
			bFit.add((double) get_dr_signal(curr_cdr));

			List<Float> curr_dr = get_dr_sample(ngroups, avR, sdR);
			monotonize(allD, curr_dr, dr1, mono);
			xx_avR = calc_WgtAvResponses(allD, dr1);

			float cbAUC = calc_AUC(luD, xx_avR);
			float cbPOD = calc_POD(luD, xx_avR, BMR);
			float cbwAUC = calc_wAUC(cbAUC, cbPOD, luD);

			bAUC.add(cbAUC);
			bPOD.add(cbPOD);
			bwAUC.add(cbwAUC);
		}

		// NB: t-test is not a good choice here, oversensitive, maybe due to too many combi-samples
		/*--------------------------
		   //t-test based calculations based on unique combinatorial samples of the asis-curve
		org.apache.commons.math3.stat.inference.TTest ff = new org.apache.commons.math3.stat.inference.TTest();		
		double [] fits = org.apache.commons.lang3.ArrayUtils.toPrimitive(bFit.toArray(new Double[bFit.size()]));
		
		   //gets two-sided p value from t-test (if corr_sgnl is different from the mean of asis-samples)
		   //the higher the p, the fewer corrections were applied
		fit_score = (float)ff.tTest((double)corr_sgnl, DoubleStream.of(fits).distinct().toArray());
		//-------------------------- */

		// update fitness based on non-parametric calculations
		double corr_median = Math.abs(bFit.get((bFit.size() >> 1)));
		float fit_score2 = Math.min(asis_sgnl, (float) corr_median)
				/ Math.max(asis_sgnl, (float) corr_median);
		if (!Float.isFinite(fit_score2))
			fit_score2 = -1.0f;
		fit_score = Math.max(fit_score2, fit_score);
		if (nfixed == 0)
			fit_score = 1.0f; // if nothing was fixed, set to perfect fit
		// --------------------------

		// ascending sort
		Collections.sort(bAUC);
		Collections.sort(bPOD);
		Collections.sort(bwAUC);

		int rank = Math.round(p * nboot);
		int lrank = nboot - rank;
		rank--;

		metrics.add(fit_score);

		metrics.add(bAUC.get(rank));
		metrics.add(myAUC);
		metrics.add(bAUC.get(lrank));

		float PODL = bPOD.get(rank), PODU = bPOD.get(lrank);
		if (PODL > luD.get(luD.size() - 1))
			PODL = Float.NaN;
		if (PODU > luD.get(luD.size() - 1))
			PODU = Float.NaN;

		metrics.add(PODL);
		metrics.add(myPOD); // myPOD was checked and fixed earlier, if invalid
		metrics.add(PODU);

		metrics.add(bwAUC.get(rank));
		metrics.add(mywAUC);
		metrics.add(bwAUC.get(lrank));
		return metrics;
	}

	public static Float curveP(List<Float> allD, List<Float> allR, float BMR)
	{
		// basic calculator of wAUC, does not do corrections or bootstrap
		List<Float> avR = calc_WgtAvResponses(allD, allR);
		List<Float> sdR = calc_WgtSdResponses(allD, allR);
		List<Float> unqD = CollapseDoses(allD);

		List<Float> luD;
		try
		{
			luD = logBaseDoses(unqD, -24);
			Float myAUC = calc_AUC(luD, avR);
			Float myPOD = calc_POD(luD, avR, BMR);

			// main call:
			Float res = calc_wAUC(myAUC, myPOD, luD);
			// System.out.printf("wAUC = %f%n", res);
			return res;
		}
		catch (Exception e)
		{
			System.out.println("problems with calculations");
			return null;
		}
	}

	public static void main(String args[])
	{
		System.out.println("Hi Dudes");

		// below is example run of calculations using fake dose-response data
		List<Float> allD = new ArrayList<>();
		List<Float> allR = new ArrayList<>();

		allD.add(0f);
		allD.add(0f);

		allD.add(11f);
		allD.add(11f);

		allD.add(50f);
		allD.add(50f);

		allD.add(200f);
		allD.add(200f);
		// ------------------------------
		allR.add(9.1f);
		allR.add(10.2f);

		allR.add(8.9f);
		allR.add(9.4f);

		allR.add(8.0f);
		allR.add(7.4f);

		allR.add(6.0f);
		allR.add(6.4f);

		// actual calculations:

		List<Float> avR = calc_WgtAvResponses(allD, allR);
		List<Float> unqD = CollapseDoses(allD);
		List<Float> luD;
		try
		{
			luD = logBaseDoses(unqD, -24);
			Float myAUC = calc_AUC(luD, avR), v;
			if (myAUC < 0)
				v = calc_PODR_bySD(allD, allR, -1.34f);
			else
				v = calc_PODR_bySD(allD, allR, 1.34f);
			Float myPOD = calc_POD(allD, allR, v, true);

			// main call:
			Float res = calc_wAUC(myAUC, myPOD, luD);
			System.out.printf("wAUC = %f%n", res);

		}
		catch (Exception e)
		{
			System.out.println("problems with calculations");
		}

		System.out.println("Mery Christmas, you dirty animal... And Hapy New Year!");

	}

	public static void debug_curvep(DoseResponseExperiment doseResponseExperiment)
	{
		List<ProbeResponse> responses = doseResponseExperiment.getProbeResponses();
		List<Treatment> treatments = doseResponseExperiment.getTreatments();
		List<ArrayList<Float>> numericMatrix = new ArrayList<ArrayList<Float>>();
		List<Float> doseVector = new ArrayList<Float>();
		// Fill numeric matrix
		for (int i = 0; i < responses.size(); i++)
		{
			numericMatrix.add((ArrayList<Float>) responses.get(i).getResponses());
		}

		// Fill doseVector
		for (int i = 0; i < treatments.size(); i++)
		{
			doseVector.add(treatments.get(i).getDose());
		}
		List<Float> wAUCList = new ArrayList<Float>();

		for (int i = 0; i < responses.size(); i++)
		{

			if (responses.get(i).getProbe().getId().equals("1367733_at"))
				wAUCList.add(CurvePProcessor.curveP(doseVector, numericMatrix.get(i), 1.34f));

			if (responses.get(i).getProbe().getId().equals("FIS1_2429"))
			{
				List<Float> corr_r = new ArrayList<Float>();
				List<Float> rr = curvePcorr(doseVector, numericMatrix.get(i), corr_r, 1.34f, -1, 100, 0.05f);
				System.out.printf("wAUC = %f[%f - %f]%n", rr.get(8), rr.get(7), rr.get(9));
				// wAUCList.add(CurvePProcessor.curveP(doseVector, numericMatrix.get(i), 1.34f));
			}

		}
	}

	public static List<Float> logwAUC(List<Float> wauc)
	{
		List<Float> logwAUC = new ArrayList<Float>();

		for (int i = 0; i < wauc.size(); i++)
		{
			logwAUC.add(directionallyAdjustedLog(wauc.get(i), 2, 1.0f));
		}

		return logwAUC;
	}

	public static Float directionallyAdjustedLog(Float val, int base, Float K)
	{// K is a positive scaling coefficient, values in [-K; K] become 0, others get log-compressed (base -> 1)

		if (Math.abs(val) < K)
		{
			return new Float(0);
		}

		boolean sign = false;
		if (val < 0)
			sign = true;

		Float ret = Math.abs(val / K);
		ret = (float) (Math.log(ret) / Math.log(base));

		if (sign)
			return -ret;

		return ret;
	}

}
