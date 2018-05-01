package com.sciome.bmdexpress2.util.curvep;

//import com.sciome.bmdexpress2.util.stat.DosesStat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.sciome.bmdexpress2.mvp.model.DoseResponseExperiment;
import com.sciome.bmdexpress2.mvp.model.probe.ProbeResponse;
import com.sciome.bmdexpress2.mvp.model.probe.Treatment;

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
		/* SD-like metric, median absolute difference = MAD;
		 * NB: does not require sorting of m[] since its median mm is supplied;
		 * perturb is usually small number, such as 0.000001f to avoid exact zero
		 */
		
		Float[] v = m.clone();
		for (int i = 0; i < m.length; i++)
			if (v[i] > mm)
				v[i] -= mm;
			else
				v[i] = mm - v[i];
		
		Arrays.sort(v); //needed for proper median estimate right below. 
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
			if ( sDoses[d].floatValue() == sDoses[d - 1].floatValue() )
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
			float Fixer = (float) FirstDoseBaseFix; // e.g., -12, -24 (Avogadro#), etc.

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

	public static float calc_POD(List<Float> allD, List<Float> allR, float Z_thr, boolean UseLog, float AUC)
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

		if (sdr.get(0) == 0.0) return ulD.get(0);
		
		float L1 = avr.get(0) - Z_thr * sdr.get(0);
		float L2 = avr.get(0) + Z_thr * sdr.get(0);

		float P1 = ImputeDose(ulD, avr, L1);
		float P2 = ImputeDose(ulD, avr, L2);

		//picks appropriate POD depending on the overall direction (judged by AUC)
		if (AUC < 0) return P1;
		if (AUC > 0) return P2;
		
		return Math.min(P1, P2); //only for AUC == 0 cases
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
	 * returns AUC normalized by point of departure (POD) and dose test range 
	 * NB: Doses, AUC, and POD have to be on the matching scale of dose units 
	 */
	{
		float UnDose = Doses.get(0), LoDose = Doses.get(1), HiDose = Doses.get(Doses.size() - 1);
		
		if (POD > HiDose)
			return 0.0f;
		
		if (POD < UnDose)
			return 0.0f;

		Float wAUC = AUC; // signed area-under-curve, such as from calc_AUC()
		
		wAUC /= HiDose - UnDose;
		wAUC *= LoDose - UnDose; //norm by POD relative to the LoDose (this + next op act as a scaling coefficient)
		wAUC /= POD - UnDose;
		
		return wAUC;
	}

	public static float parametric_val(float D0, List<Float>P, int type)
	/* calculates  response at D0 dose based on parametric curve model, 
	 * P contains curve coefficients, 
	 * type defines math.equation:  
	 * 		0 - polynomial, 1 - power, 2 - exponential, 3 - log, 4 - Hill 
	 */
	{
		float rBase = 0.0f;
		
		rBase = P.get(0); 
		if (type == 0)	//polynomial (incl. linear and constant cases)
		{//y(x) = P[0] + P[1]*x + ... + P[n]*x^n
			for (int i = 1; i < P.size(); i++) 
				rBase += P.get(i)*Math.pow(D0, i);			
		}
		
		if (type == 1) //power
		{//y(x) = P[0] + P[1]*x^P[2]
			rBase += P.get(1)*Math.pow(D0, P.get(2));
		}
		
		if (type == 2) // exponential
		{//y(x) = P[0] * { P[2] - (P[2]-1)* exp( P[1] * x^P[3] ) }
			
			rBase = (float)Math.exp(  P.get(1)*Math.pow(D0, P.get(3))  );
			rBase *= 1 - P.get(2);
			rBase += P.get(2);
			rBase *= P.get(0);
		}
		
		if (type == 3) // logarithmic
		{// y(x) = P[0] + P[1]*ln(x)
			rBase += P.get(1)*Math.log(D0);
		}
		
		if (type == 4) // Hill
		{// y(x0 = P[0] + P[1] - P[1]P[3]/(x^P[2] + P[3])
			
			rBase += P.get(1);
			rBase -= P.get(1)*P.get(3)/(Math.pow(D0, P.get(2))+P.get(3));			
		}

		return rBase;
	} //end of parametric_val()
	
	public static float intg_log_AUC(List<Float> D, List<Float> P, int type, int log0fix, int npoints)
	/* log10-integrates several curve types to calculate AUC, P contains curve coefficients, 
	 * type defines math.equation:  0 - polynomial, 1 - power, 2 - exponential, 3 - log, 4 - Hill
	 * 
	 * calls logBaseDoses(D, log0fix) to obtain log10 transform for D[]   
	 */
	{
		List<Float> luD;
		
		try {
			luD = logBaseDoses(D, log0fix);
		} catch (Exception e) {			
			return 0.0f;
		}
		
		List<Float> estR = new ArrayList<Float>(); //estimated responses
		List<Float> estD = new ArrayList<Float>(); //sampled log-doses
		
		//since we cannot analytically integrate all the cases, switch to trapezoid estimate instead with arbitrary precision (npoints)
		int nscan = npoints - 3;
		float hiD = luD.get( luD.size() - 1), loD = luD.get(1);
		
		estD.add( luD.get(0) );
		estR.add( parametric_val(0.0f, P, type) );
		
		float stepD = (hiD - loD) / nscan;
				
		for (int p = -1; p <= nscan; p++)
		{
			float startD  = loD + p*stepD;
			
			estD.add(startD);
			estR.add( parametric_val((float)Math.pow(10, startD), P, type) );			
		}
		
		return calc_AUC(estD, estR);
	} //end of intg_log_AUC()
	
	
	public static float intg_AUC(List<Float> D, List<Float> P, int type)
	{
	/* analytically integrates several curve types to calculate AUC, P contains curve coefficients, 
	 * type defines math.equation:  0 - polynomial, 1 - power, 2 - exponential, 3 - log, 4 - Hill 
	 */
		
		int N = P.size();
		float hiD = D.get(N-1), unD = D.get(0);		
		float iAUC = parametric_val(unD, P, type);
		iAUC *= unD - hiD; //now iAUC contains -baseline's AUC;
		
		if (type == 0) // polynomial, including linear and constant cases
		{
			// y(x) = P[0] + P[1]*x + ... + P[n]*x^n
			// F(y)dx = C + P[0]*x + P[1]*x^2/2 + ... P[n]*x^(n+1)/(n+1)
		
			for (int i = 0; i < N; i++)
			{
				float c = P.get(i);
				if (i == 0) 
				{
					iAUC += c*(hiD - unD);
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
			float c = P.get(1)/pp;
			c *= Math.pow(hiD, pp) - Math.pow(unD, pp);
			
			iAUC += P.get(0)*(hiD - unD) + c;
		}

		if (type == 2) // exponential
		{
			// y(x) = P[0] * { P[2] - (P[2]-1)* exp( P[1] * x^P[3] ) }
			// F(y)dx = C + P[0] * { P[2]*x - (P[2]-1)* exp( P[1] * x^P[3] )/P[1]/P[3]/x^(P[3]-1) }
			
			float pp =  P.get(3) - 1;
			float c = 1 - P.get(2);
			c /= P.get(1) * P.get(3) / P.get(0);
			
			float p1 = (float)Math.exp( Math.pow(unD, P.get(3))*P.get(1) );
			p1 /= Math.pow(unD,  pp);
			
			float p2 = (float)Math.exp( Math.pow(hiD, P.get(3))*P.get(1) );
			p2 /= Math.pow(hiD,  pp);

			iAUC += P.get(0)*P.get(2)*(hiD - unD) + c*(p2-p1);
		}

		if (type == 3) // logarithmic
		{//NB: this type will be a issue for cases where x = 0; currently not supported
			
			//y(x) = P[0] + P[1]*ln(x)
			//F(y)dx = C + P[0]*x + x(ln(x) - 1)P[1]
			
			float c = P.get(1);
			c *= hiD*Math.log(hiD) - unD*Math.log(unD);
			
			iAUC += (P.get(0) - P.get(1))*(hiD - unD) + c;
		}

		if (type == 4) // Hill
		{
			// y(x) = P[0] + P[1]*x^P[2] / ( x^P[2] + P[3]) = 
			//P[0] + P[1] - P[1]P[3]/(x^P[2] + P[3])
			
			// F(y)dx = C + x^

		}

		// other types can be added and handled below

		return 0.0f;
	} // end of intg_AUC()

	public static Float curveP(List<Float> allD, List<Float> allR) {
		List<Float> avR = calc_WgtAvResponses(allD, allR);
		List<Float> unqD = CollapseDoses(allD);
		List<Float> luD;
		try
		{
			luD = logBaseDoses(unqD, -24);
			Float myAUC = calc_AUC(luD, avR);
			Float myPOD = calc_POD(allD, allR, 1.34f, true, myAUC);

			// main call:
			Float res = calc_wAUC(myAUC, myPOD, luD);
//			System.out.printf("wAUC = %f%n", res);
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
			Float myAUC = calc_AUC(luD, avR);
			Float myPOD = calc_POD(allD, allR, 1.34f, true, myAUC);

			// main call:
			Float res = calc_wAUC(myAUC, myPOD, luD);
			System.out.printf("wAUC = %f%n", res);

		}
		catch (Exception e)
		{
			System.out.println("problems with calculations");
		}

		System.out.println("Happy Christmas, you dirty animal... And Hapy New Year!");

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
		
		
		
		for(int i = 0; i < responses.size(); i++) {
			if ( responses.get(i).getProbe().getId().equals("1387874_at") )
				wAUCList.add(CurvePProcessor.curveP(doseVector, numericMatrix.get(i)));
			
			if ( responses.get(i).getProbe().getId().equals("1371076_at") ) //i == 3681
			    wAUCList.add(CurvePProcessor.curveP(doseVector, numericMatrix.get(i)));
		}
	}
	
	public static List<Float> logwAUC(List<Float> wauc)
	{
		List<Float> logwAUC = new ArrayList<Float>();
		
		for(int i = 0; i < wauc.size(); i++)
		{
			logwAUC.add(directionallyAdjustedLog(wauc.get(i), 2));
		}
		
		return logwAUC;
	}
	
	public static Float directionallyAdjustedLog(Float val, int base)
	{
		if(val == 0) {
			return new Float(0);
		}
		
		boolean sign = false;
		if(val < 0)
			sign = true;
		
		Float ret = Math.abs(val);
		ret = (float) (Math.log(ret)/Math.log(base));
		if(sign)
			ret = Math.abs(ret) * -1;
		else
			ret = Math.abs(ret);
		
		return ret;
	}
}
