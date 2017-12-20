package com.sciome.bmdexpress2.util.curvep;

//import com.sciome.bmdexpress2.util.stat.DosesStat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CurvePProcessor
{

	/*-- static methods for handling metric calculations
	 * 
	 */

	// --------------------------------------------------------------------------------
	// NB: move the below auxiliary static functions into the sciome-commons.math?
	// --------------------------------------------------------------------------------

	private static Float smedian(Float[] m)
	{
		// calculates median value of the pre-sorted array m
		int mid = m.length >> 1;
		Float mv = m[mid];
		if (m.length == (mid << 1))
		{
			mv += m[mid - 1];
			mv *= 0.5f;
		}
		return mv;
	} // end of smedian()

	private static Float MAD(Float[] m, Float perturb)
	{
		// SD-like metric, median absolute difference = MAD;
		// perturb is usually small number, such as 0.000001f to avoid exact zero
		Float[] v = m.clone();
		Arrays.sort(v);
		Float x = smedian(v);
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

	private static Float[] TukeyBiWs(Float[] m, Float c, Float p)
	{
		/*
		 * returns Tukey's biweight coefficients for the array m c is number of MADs beyond which the weight
		 * will be set to 0 (and corresponding point would thus be considered as an outlier) p is a small
		 * number to add to avoid division by zero MAD (variation-like metric)
		 */
		Float[] f = m.clone();
		Arrays.sort(f);
		Float madv = MAD(f, p), piv = smedian(f);

		for (int i = 0; i < m.length; i++)
		{// fills f[] corresponding to the order in m[]
			Float zx = (m[i] - piv) / madv / c;
			Float x = 1.0f - zx * zx;
			f[i] = 0.0f;
			if (x > 0)
				f[i] = x * x;
		} // for i

		return f;
	} // end of TukeyBiWs()

	private static Float wMean(Float[] vals, Float[] coffs)
	{
		/*
		 * returns weighted average for vals[], using coffs coffs[] must be non-negative
		 */

		Float c_sum = 0.0f, w_sum = 0.0f;
		for (int i = 0; i < vals.length; i++)
		{
			c_sum += coffs[i];
			w_sum += vals[i] * coffs[i];
		} // for i

		return (w_sum / c_sum);
	}

	private static Float wSD(Float[] vals, Float[] coffs)
	{
		/*
		 * returns weighted standard deviation of vals, using coffs coffs[] must be non-negative
		 */
		Float wm = wMean(vals, coffs);
		int n = vals.length;
		Float[] diff = new Float[n];

		Float c_sum = 0.0f, w_sum = 0.0f;
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
		// return t.asscendingSort( (Float[])allDoses.toArray() ).sortedUniDoses();

		Float[] sDoses = (Float[]) allDoses.toArray();
		Arrays.sort(sDoses);
		List<Float> usDoses = new ArrayList<Float>();
		usDoses.add(sDoses[0]);
		for (int d = 1; d < sDoses.length; d++)
		{
			if (sDoses[d] == sDoses[d - 1])
				continue;

			usDoses.add(sDoses[d]);
		}

		return usDoses;
	} // end of CollapseDoses()

	public static List<Float> calc_WgtAvResponses(List<Float> allDoses, List<Float> allResponses)
	{
		/*
		 * Calculates weighted average response for each dose group using Tukey's biweight method
		 */
		List<Float> D = CollapseDoses(allDoses);
		List<Float> R = new ArrayList<Float>();

		for (int d = 0; d < D.size(); d++)
		{
			Float g = allDoses.get(d);
			List<Float> gR = new ArrayList<Float>();

			for (int ad = allDoses.indexOf(g); ad <= allDoses.lastIndexOf(g); ad++)
				if (allDoses.get(ad) == g)
					gR.add(allResponses.get(ad));

			Float[] gResps = (Float[]) gR.toArray();
			Float[] cfs = TukeyBiWs(gResps, 5.0f, 0.00001f);

			R.add(wMean(gResps, cfs));
		} // for d

		return R;
	} // end of calc_WgtAvResponses()

	public static List<Float> calc_WgtSdResponses(List<Float> allDoses, List<Float> allResponses)
	{
		/*
		 * Calculates weighted st.dev of response for each dose group using Tukey's biweight method
		 */
		List<Float> D = CollapseDoses(allDoses);
		List<Float> RS = new ArrayList<Float>();

		for (int d = 0; d < D.size(); d++)
		{
			Float g = allDoses.get(d);
			List<Float> gR = new ArrayList<Float>();

			for (int ad = allDoses.indexOf(g); ad <= allDoses.lastIndexOf(g); ad++)
				if (allDoses.get(ad) == g)
					gR.add(allResponses.get(ad));

			Float[] gResps = (Float[]) gR.toArray();
			Float[] cfs = TukeyBiWs(gResps, 5.0f, 0.00001f);

			RS.add(wSD(gResps, cfs));
		} // for d

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
			Float Fixer = (float) FirstDoseBaseFix; // e.g., -12, -24 (Avogadro#), etc.

			if (FirstDoseBaseFix == 0)
				Fixer = 2 * NewD.get(1) - NewD.get(2);
			NewD.set(0, Fixer);
		}

		return NewD;
	} // end of logBaseDoses()

	public static Float ImputeDose(List<Float> D, List<Float> V, Float L)
	/*
	 * Interpolates the dose at which the L threshold of response is reached D - unique doses (can be
	 * log-transformed) V - responses corresponding to D[] L - threshold response
	 */
	{
		int e = D.size() - 1;
		Float[] sv = (Float[]) V.toArray();
		Arrays.sort(sv);

		Float iD = D.get(e) + 1000.0f; // default invalid value (out of dose range)

		if ((sv[e] < L) || (L < sv[0]))
			return iD;

		for (int s = 0; s < e; s++)
		{
			int z = s + 1;
			if (L == V.get(s))
				return D.get(s); // to handle exact hit

			Float Vsz = Math.abs(V.get(s) - V.get(s));
			if (Vsz < Math.abs(V.get(z) - L))
				continue;
			if (Vsz < Math.abs(V.get(s) - L))
				continue;

			iD = D.get(s) + (D.get(z) - D.get(s)) * (L - V.get(s)) / (V.get(z) - V.get(s));
			break;
		}

		return iD;
	} // end of ImputeDose()

	public static Float calc_POD(List<Float> allD, List<Float> allR, Float Z_thr, boolean UseLog)
	{
		/*
		 * returns lowest of the two POD estimates for decreasing and increasing direction (large number
		 * indicates no POD)
		 * 
		 * allD - all doses, allR - all responses (for all replicates in all dose groups) Z_thr - Z-score
		 * threshold to calculate POD response level (both directions will be checked) UseLog - if on, the
		 * doses will be log-transformed
		 */

		List<Float> sdr = calc_WgtSdResponses(allD, allR);
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

		Float L1 = avr.get(0) - Z_thr * sdr.get(0);
		Float L2 = avr.get(0) + Z_thr * sdr.get(0);

		Float P1 = ImputeDose(ulD, avr, L1);
		Float P2 = ImputeDose(ulD, avr, L2);

		return Math.min(P1, P2);
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

	public static float calc_wAUC(float AUC, float POD, float FirstDose, float LastDose)
	/*
	 * returns AUC normalized by point of departure (POD) and dose test range NB: FirstDose - untreated
	 * control, e.g., either 0 or log-transformed by logBaseDoses() NB: POD - can be calculated by calc_POD()
	 * or supplied externally, has to match Dose scale NB: AUC - can be calculated by calc_AUC() or intg_AUC()
	 * - has to be on the same scale of dose-units as other parameters!
	 */
	{
		if (POD > LastDose)
			return 0.0f;
		if (POD < FirstDose)
			return 0.0f;

		Float wAUC = AUC; // signed area-under-curve, such as from calc_AUC()
		wAUC /= LastDose - FirstDose;
		wAUC /= POD - FirstDose;
		return wAUC;
	}

	// -- TODO!!??!!
	public static float intg_AUC(List<Float> D, List<Float> P, int type)
	{// integrates several curve types to calculate AUC, P contains curve coefficients

		// if z = log10 x, then x = exp(az), where a = ln 10, then

		if (type == 0) // polynomial, including linear and constant cases
		{
			// y(x) = P[0] + P[1]*x + ... + P[n]*x^n
			// F(y)dx = C + P[0]*x + P[1]*x^2/2 + ... P[n]*x^(n+1)/(n+1)

			// y(z) = P[0] + P[1]*exp(az) + ... + P[n]*exp(azn)
			// F(y)dz = C + P[0]*z + (P[1]*exp(az)/1 + ... P[n]*exp(azn)/n )/ a
		}

		if (type == 1) // power
		{
			// y(x) = P[0] + P[1]*x^P[2]
			// F(y)dx = C + P[0]*x + P[1]*x^(P[2]+1)/(P[2]+1)

			// y(z) = P[0] + P[1]*exp(azP[2])
			// F(y)dz = C + P[0]*z + P[1]*exp(azP[2])/(aP[2])
		}

		if (type == 2) // exponential
		{
			// y(x) = P[0] * { P[2] - (P[2]-1)* exp( P[1] * x^P[3] ) }
			// F(y)dx = C + P[0] * { P[2]*x - (P[2]-1)* exp( P[1] * x^P[3] )/P[1]/P[3]/x^(P[3]-1) }

			// y(z) = P[0] * { P[2] - (P[2]-1)* exp( P[1] * exp(azP[3]) ) }
			// F(y)dz = C + P[0] * { P[2]*z - (P[2]-1)* exp( P[1] * exp(azP[3]) )/(aP[1]P[3]exp(azP[3])) }
		}

		if (type == 3) // logarithmic
		{
			// y(x) = P[0] + P[1]*ln(x)
			// F(y)dx = C + P[0]*x + x(ln(x) - 1)P[1]

			// y(z) = P[0] + P[1]*az
			// F(y)dz = C + P[0]*z + P[1]*az^2/2
		}

		if (type == 4) // Hill
		{
			// y(x) = P[0] + P[1]*x^P[2] / ( x^P[2] + P[3]) = P[0] + 1 - P[3]/(x^P[2] + P[3])
			// F(y)dx = C + (1+P[0])*x - ...?

			// y(z) =
		}

		// other types can be added and handled below

		return 0.0f;
	} // end of intg_AUC()

	public static void main(String args[])
	{
		CurvePProcessor cp = new CurvePProcessor();

		System.out.println("hello world");

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
			Float myAUC = calc_AUC(luD, avR);
			Float myPOD = calc_POD(allD, allR, 1.34f, true);

			// main call:
			Float res = calc_wAUC(myAUC, myPOD, -24, luD.get(luD.size() - 1));
			System.out.printf("wAUC = %f%n", res);

		}
		catch (Exception e)
		{
			System.out.println("problems with log-transform");
		}

		System.out.println("good bye world");

	}

}
