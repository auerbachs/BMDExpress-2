#include <iostream>
#include <string>
#include <sstream>  
#include <vector>

#include "bmds_entry.h"


#include  <statmod.h>

#include <log_likelihoods.h>
#include <normal_likelihoods.h>
#include <normalModels.h>
#include <binomModels.h>
#include <IDPrior.h>

#include "bmd_calculate.h"

#include "normal_HILL_NC.h"
#include "normal_POWER_NC.h"
#include "normal_POLYNOMIAL_NC.h"
#include "normal_EXP_NC.h"

#include "lognormal_HILL_NC.h"
#include "lognormal_POWER_NC.h"
#include "lognormal_POLYNOMIAL_NC.h"
#include "lognormal_EXP_NC.h"

#include "continuous_clean_aux.h"
#include "continuous_entry_code.h"
#include "mcmc_analysis.h"

#include "com_toxicR_ToxicRJNI.h"
#define MAX_PARMS 32 // Should never get close to this many!!!


JNIEXPORT jstring JNICALL Java_com_toxicR_ToxicRJNI_calcDeviance
  (JNIEnv *env, jobject thisObject, jint model, jboolean suff_stat, 
   jdoubleArray Y, jdoubleArray doses, jdoubleArray sd, jdoubleArray n_group, jdoubleArray prior, 
   jint BMD_type, jboolean isIncreasing, jdouble BMR, jdouble tail_prob, jint disttype, 
   jdouble alpha, jint samples, jint burnin, jint parms, jint prior_cols, jint degree)
{
    ////////////////////////////////////////////////
    /// Set up the analysis
    ////////////////////////////////////////////////

   jsize len = env->GetArrayLength( doses);
   jdouble *doseBody = env->GetDoubleArrayElements( doses, 0);
   jdouble *yBody = env->GetDoubleArrayElements( Y, 0);
   jdouble *sdBody = env->GetDoubleArrayElements( sd, 0);
   jdouble *nGroupBody = env->GetDoubleArrayElements( n_group, 0);


   jsize priorLen = env->GetArrayLength( prior);
   jdouble *priorBody = env->GetDoubleArrayElements( prior, 0);

    continuous_analysis analysis; 
    analysis.Y       =    new double[len]; 
    analysis.n       =    len; 
    analysis.doses   =    new double[len]; 
    analysis.model   =    (cont_model) model; 
    analysis.disttype     = disttype; 
    analysis.isIncreasing = isIncreasing; 
    analysis.alpha        = alpha; //alpha for analyses; 
    analysis.BMD_type     = BMD_type; 
    analysis.BMR          = BMR; 
    analysis.samples      = samples; 
    analysis.tail_prob    = tail_prob; 
    analysis.suff_stat    = suff_stat;
    analysis.parms        = parms;
    analysis.prior_cols   = prior_cols; 
    analysis.degree   = degree;
    analysis.sd = new double[1];
    analysis.n_group = new double[1];


    analysis.prior   = new double[parms*prior_cols]; 
    for (int i = 0; i < priorLen; i++){
       analysis.prior[i] = priorBody[i];
    }


    for (int i = 0; i < len; i++){
      analysis.Y[i] = yBody[i]; 
      analysis.doses[i] = doseBody[i]; 
      //if (suff_stat){ //sufficient statistics
      //  analysis.n_group[i] = nGroupBody[i];
      //  analysis.sd[i]      = sdBody[i];
      //}
    }
    continuous_deviance aod;
    estimate_normal_aod(&analysis,&aod);

    
   string jsonResults = convertDevianceResultToJSON(&aod);
   del_continuous_analysis(analysis);
  // delete &aod;

   return env->NewStringUTF(jsonResults.c_str());


}

JNIEXPORT jstring JNICALL Java_com_toxicR_ToxicRJNI_runContinuousSingleJNI
  (JNIEnv *env, jobject thisObject, jint model, jboolean suff_stat, 
   jdoubleArray Y, jdoubleArray doses, jdoubleArray sd, jdoubleArray n_group, jdoubleArray prior, 
   jint BMD_type, jboolean isIncreasing, jdouble BMR, jdouble tail_prob, jint disttype, 
   jdouble alpha, jint samples, jint burnin, jint parms, jint prior_cols, jint degree, 
   jboolean isFast)
{
    
    ////////////////////////////////////////////////
    /// Set up the analysis
    ////////////////////////////////////////////////

   jsize len = env->GetArrayLength( doses);
   jdouble *doseBody = env->GetDoubleArrayElements( doses, 0);
   jdouble *yBody = env->GetDoubleArrayElements( Y, 0);
   jdouble *sdBody = env->GetDoubleArrayElements( sd, 0);
   jdouble *nGroupBody = env->GetDoubleArrayElements( n_group, 0);


   jsize priorLen = env->GetArrayLength( prior);
   jdouble *priorBody = env->GetDoubleArrayElements( prior, 0);

    continuous_analysis analysis; 
    analysis.Y       =    new double[len]; 
    analysis.n       =    len; 
    analysis.doses   =    new double[len]; 
    analysis.model   =    (cont_model) model; 
    analysis.disttype     = disttype; 
    analysis.isIncreasing = isIncreasing; 
    analysis.alpha        = alpha; //alpha for analyses; 
    analysis.BMD_type     = BMD_type; 
    analysis.BMR          = BMR; 
    analysis.samples      = samples; 
    analysis.tail_prob    = tail_prob; 
    analysis.suff_stat    = suff_stat;
    analysis.parms        = parms;
    analysis.prior_cols   = prior_cols; 
    analysis.degree   = degree;
    analysis.sd = new double[1];
    analysis.n_group = new double[1];


    analysis.prior   = new double[parms*prior_cols]; 
    for (int i = 0; i < priorLen; i++){
       analysis.prior[i] = priorBody[i];
    }


    for (int i = 0; i < len; i++){
      analysis.Y[i] = yBody[i]; 
      analysis.doses[i] = doseBody[i]; 
      //if (suff_stat){ //sufficient statistics
      //  analysis.n_group[i] = nGroupBody[i];
      //  analysis.sd[i]      = sdBody[i];
      //}
    }
    continuous_model_result *result = new_continuous_model_result( analysis.model, analysis.parms,
                                                                   200); //have 200 equally spaced values
    estimate_sm_laplace(&analysis,result,isFast);

    
   string jsonResults = convertSingleContinuousResultToJSON(result);
   del_continuous_analysis(analysis);
   del_continuous_model_result(result); 

   return env->NewStringUTF(jsonResults.c_str());

  }



JNIEXPORT jstring JNICALL Java_com_toxicR_ToxicRJNI_runContinuousMAJNI
  (JNIEnv *env, jobject thisObject, jint nmodels, jintArray jmodels, jintArray jnparms, jintArray jactual_parms,
   jintArray jprior_cols, jintArray jdisttypes, jdoubleArray jmodelPriors, jboolean suff_stat, 
   jdoubleArray Y, jdoubleArray doses, jdoubleArray sd, jdoubleArray n_group, jdoubleArray jpriors, 
   jint BMD_type, jboolean isIncreasing, jdouble BMR, jdouble tail_prob, 
   jdouble alpha, jint samples, jint burnin, jboolean isFast )
{
    
    ////////////////////////////////////////////////
    /// Set up the analysis
    ////////////////////////////////////////////////

   jsize len = env->GetArrayLength( doses);
   jdouble *doseBody = env->GetDoubleArrayElements( doses, 0);
   jdouble *yBody = env->GetDoubleArrayElements( Y, 0);
   jdouble *sdBody = env->GetDoubleArrayElements( sd, 0);
   jdouble *nGroupBody = env->GetDoubleArrayElements( n_group, 0);
   jdouble *priors = env->GetDoubleArrayElements( jpriors, 0);
   jdouble *modelPriors = env->GetDoubleArrayElements( jmodelPriors, 0);
   jint *models = env->GetIntArrayElements(jmodels,0);
   jint *nparms = env->GetIntArrayElements(jnparms,0);
   jint *priorCols = env->GetIntArrayElements(jprior_cols,0);
   jint *actualParms = env->GetIntArrayElements(jactual_parms,0);
   jint *disttypes = env->GetIntArrayElements(jdisttypes,0);

   continuousMA_analysis ma_analysis;

   ma_analysis.nmodels = nmodels;
   ma_analysis.modelPriors = new double [ma_analysis.nmodels];
   ma_analysis.priors  = new double *[ma_analysis.nmodels];
   ma_analysis.nparms  = new int [ma_analysis.nmodels];
   ma_analysis.actual_parms = new int [ma_analysis.nmodels];
   ma_analysis.prior_cols   = new int[ma_analysis.nmodels];
   ma_analysis.models  = new int [ma_analysis.nmodels];
   ma_analysis.disttype= new int [ma_analysis.nmodels];

   continuousMA_result *ma_result = new continuousMA_result;
   ma_result->nmodels    = ma_analysis.nmodels;
   ma_result->dist_numE  = 300;
   ma_result->bmd_dist   = new double[300*2];
   ma_result->post_probs = new double[ma_analysis.nmodels];
   ma_result->models     = new continuous_model_result*[ma_analysis.nmodels];


   int currPriorIndex=0;
   for (int i = 0; i < ma_analysis.nmodels; i++)
   {
      ma_analysis.modelPriors[i] = 1.0/double(ma_analysis.nmodels);
      //Eigen::MatrixXd temp = model_priors[i];
	int prows = nparms[i];
	int pcols = priorCols[i];
        ma_analysis.priors[i]    = new double[prows*pcols];

        for(int j=0; j<prows*pcols; j++)
           ma_analysis.priors[i][j] = priors[currPriorIndex++];
        ma_analysis.nparms[i]     = prows;
        ma_analysis.prior_cols[i] = pcols;
        ma_analysis.models[i]     = (int) models[i];
        ma_analysis.disttype[i]   = (int) disttypes[i];
        ma_result->models[i] = new_continuous_model_result( ma_analysis.models[i], ma_analysis.nparms[i], 300); //have 300 equally
    }


    // Set up the other info
    continuous_analysis analysis; 
    analysis.Y       =    new double[len]; 
    analysis.n       =    len; 
    analysis.doses   =    new double[len]; 
    analysis.isIncreasing = isIncreasing; 
    analysis.alpha        = alpha; //alpha for analyses; 
    analysis.BMD_type     = BMD_type; 
    analysis.BMR          = BMR; 
    analysis.samples      = samples; 
    analysis.tail_prob    = tail_prob; 
    analysis.suff_stat    = suff_stat;
    analysis.prior   = NULL;
    analysis.sd = new double[1];
    analysis.n_group = new double[1];

    for (int i = 0; i < len; i++){
      analysis.Y[i] = yBody[i]; 
      analysis.doses[i] = doseBody[i]; 
    }



   estimate_ma_laplace(&ma_analysis,&analysis,ma_result);
   string jsonResults = convertMAContinuousResultToJSON(ma_result);

   // free up memory
   for (unsigned int i = 0; i < ma_result->nmodels; i++){
           del_continuous_model_result(ma_result->models[i]);
   }

   delete ma_result->post_probs;
   delete ma_result->bmd_dist;
   delete ma_result;
   del_continuous_analysis(analysis);
   del_continuousMA_analysis(ma_analysis);
    

   return env->NewStringUTF(jsonResults.c_str());

  }


/*
MCMC single
*/
JNIEXPORT jstring JNICALL Java_com_toxicR_ToxicRJNI_runContinuousMCMCSingleJNI
  (JNIEnv *env, jobject thisObject, jint model, jboolean suff_stat, 
   jdoubleArray Y, jdoubleArray doses, jdoubleArray sd, jdoubleArray n_group, jdoubleArray prior, 
   jint BMD_type, jboolean isIncreasing, jdouble BMR, jdouble tail_prob, jint disttype, 
   jdouble alpha, jint samples, jint burnin, jint parms, jint prior_cols,jint degree, jboolean isFast)
{
    
    ////////////////////////////////////////////////
    /// Set up the analysis
    ////////////////////////////////////////////////

   jsize len = env->GetArrayLength( doses);
   jdouble *doseBody = env->GetDoubleArrayElements( doses, 0);
   jdouble *yBody = env->GetDoubleArrayElements( Y, 0);
   jdouble *sdBody = env->GetDoubleArrayElements( sd, 0);
   jdouble *nGroupBody = env->GetDoubleArrayElements( n_group, 0);


   jsize priorLen = env->GetArrayLength( prior);
   jdouble *priorBody = env->GetDoubleArrayElements( prior, 0);

    continuous_analysis analysis; 
    analysis.Y       =    new double[len]; 
    analysis.n       =    len; 
    analysis.doses   =    new double[len]; 
    analysis.model   =    (cont_model) model; 
    analysis.disttype     = disttype; 
    analysis.isIncreasing = isIncreasing; 
    analysis.alpha        = alpha; //alpha for analyses; 
    analysis.BMD_type     = BMD_type; 
    analysis.BMR          = BMR; 
    analysis.samples      = samples; 
    analysis.burnin       = burnin;
    analysis.tail_prob    = tail_prob; 
    analysis.suff_stat    = suff_stat;
    analysis.parms        = parms;
    analysis.prior_cols   = prior_cols; 
    analysis.degree   = degree;
    analysis.sd = new double[1];
    analysis.n_group = new double[1];


    analysis.prior   = new double[parms*prior_cols]; 
    for (int i = 0; i < priorLen; i++){
       analysis.prior[i] = priorBody[i];
    }


  bmd_analysis_MCMC  *output = new bmd_analysis_MCMC;
  output->parms = new double[samples*analysis.parms];
  output->BMDS  = new double[samples];


    for (int i = 0; i < len; i++){
      analysis.Y[i] = yBody[i]; 
      analysis.doses[i] = doseBody[i]; 
      //if (suff_stat){ //sufficient statistics
      //  analysis.n_group[i] = nGroupBody[i];
      //  analysis.sd[i]      = sdBody[i];
      //}
    }
   continuous_model_result *result = new_continuous_model_result( analysis.model, analysis.parms,
                                                                   200); //have 200 equally spaced values
   estimate_sm_mcmc(&analysis, result, output);

    
   string jsonResults = convertMCMCSingleContinuousResultToJSON(result, output);

   del_continuous_model_result(result);
   del_continuous_analysis(analysis);
 
   return env->NewStringUTF(jsonResults.c_str());

  }



JNIEXPORT jstring JNICALL Java_com_toxicR_ToxicRJNI_runContinuousMCMCMAJNI
  (JNIEnv *env, jobject thisObject, jint nmodels, jintArray jmodels, jintArray jnparms, jintArray jactual_parms,
   jintArray jprior_cols, jintArray jdisttypes, jdoubleArray jmodelPriors, jboolean suff_stat, 
   jdoubleArray Y, jdoubleArray doses, jdoubleArray sd, jdoubleArray n_group, jdoubleArray jpriors, 
   jint BMD_type, jboolean isIncreasing, jdouble BMR, jdouble tail_prob, 
   jdouble alpha, jint samples, jint burnin, jboolean isFast )
{
    
    ////////////////////////////////////////////////
    /// Set up the analysis
    ////////////////////////////////////////////////

   jsize len = env->GetArrayLength( doses);
   jdouble *doseBody = env->GetDoubleArrayElements( doses, 0);
   jdouble *yBody = env->GetDoubleArrayElements( Y, 0);
   jdouble *sdBody = env->GetDoubleArrayElements( sd, 0);
   jdouble *nGroupBody = env->GetDoubleArrayElements( n_group, 0);
   jdouble *priors = env->GetDoubleArrayElements( jpriors, 0);
   jdouble *modelPriors = env->GetDoubleArrayElements( jmodelPriors, 0);
   jint *models = env->GetIntArrayElements(jmodels,0);
   jint *nparms = env->GetIntArrayElements(jnparms,0);
   jint *priorCols = env->GetIntArrayElements(jprior_cols,0);
   jint *actualParms = env->GetIntArrayElements(jactual_parms,0);
   jint *disttypes = env->GetIntArrayElements(jdisttypes,0);


   continuousMA_analysis ma_analysis;

   ma_analysis.nmodels = nmodels;
   ma_analysis.modelPriors = new double [ma_analysis.nmodels];
   ma_analysis.priors  = new double *[ma_analysis.nmodels];
   ma_analysis.nparms  = new int [ma_analysis.nmodels];
   ma_analysis.actual_parms = new int [ma_analysis.nmodels];
   ma_analysis.prior_cols   = new int[ma_analysis.nmodels];
   ma_analysis.models  = new int [ma_analysis.nmodels];
   ma_analysis.disttype= new int [ma_analysis.nmodels];

   continuousMA_result *ma_result = new continuousMA_result;

  ma_MCMCfits model_mcmc_info;
  model_mcmc_info.analyses = new bmd_analysis_MCMC*[ma_analysis.nmodels];
  model_mcmc_info.nfits = ma_analysis.nmodels;

   ma_result->nmodels    = ma_analysis.nmodels;
   ma_result->dist_numE  = 300;
   ma_result->bmd_dist   = new double[300*2];
   ma_result->post_probs = new double[ma_analysis.nmodels];
   ma_result->models     = new continuous_model_result*[ma_analysis.nmodels];


   int currPriorIndex=0;
   for (int i = 0; i < ma_analysis.nmodels; i++)
   {
      ma_analysis.modelPriors[i] = 1.0/double(ma_analysis.nmodels);
	int prows = nparms[i];
	int pcols = priorCols[i];
        ma_analysis.priors[i]    = new double[prows*pcols];

        for(int j=0; j<prows*pcols; j++)
           ma_analysis.priors[i][j] = priors[currPriorIndex++];
        ma_analysis.nparms[i]     = prows;
        ma_analysis.prior_cols[i] = pcols;
        ma_analysis.models[i]     = (int) models[i];
        ma_analysis.disttype[i]   = (int) disttypes[i];
        ma_result->models[i] = new_continuous_model_result( ma_analysis.models[i], ma_analysis.nparms[i], 300); //have 300 equally
        model_mcmc_info.analyses[i] = new_mcmc_analysis(ma_analysis.models[i], ma_analysis.nparms[i], samples);
    }


    // Set up the other info
    continuous_analysis analysis; 
    analysis.Y       =    new double[len]; 
    analysis.n       =    len; 
    analysis.doses   =    new double[len]; 
    analysis.isIncreasing = isIncreasing; 
    analysis.alpha        = alpha; //alpha for analyses; 
    analysis.BMD_type     = BMD_type; 
    analysis.BMR          = BMR; 
    analysis.samples      = samples; 
    analysis.burnin       = burnin;
    analysis.tail_prob    = tail_prob; 
    analysis.suff_stat    = suff_stat;
    analysis.sd = new double[1];
    analysis.n_group = new double[1];
    analysis.prior   = NULL;

    for (int i = 0; i < len; i++){
      analysis.Y[i] = yBody[i]; 
      analysis.doses[i] = doseBody[i]; 
    }



   estimate_ma_MCMC(&ma_analysis,&analysis,ma_result,&model_mcmc_info);
   string jsonResults = convertMCMCMAContinuousResultToJSON(ma_result, &model_mcmc_info);
   //string jsonResults = "hello ma world";

   // free up memory
   for (unsigned int i = 0; i < ma_result->nmodels; i++){
           del_continuous_model_result(ma_result->models[i]);
   }

   delete ma_result->post_probs;
   delete ma_result->bmd_dist;
   delete ma_result;
   del_continuous_analysis(analysis);
   del_continuousMA_analysis(ma_analysis);
    

   return env->NewStringUTF(jsonResults.c_str());

  }







string convertDevianceResultToJSON(continuous_deviance* result)
{
  std::stringstream buffer;
  buffer << "{" << std::endl;
  
  buffer << "\"A1\":" << result->A1<<","<< std::endl;
  buffer << "\"N1\":" << result->N1<<","<< std::endl;
  buffer << "\"A2\":" << result->A2<<","<< std::endl;
  buffer << "\"N2\":" << result->N2<<","<< std::endl;
  buffer << "\"A3\":" << result->A3<<","<< std::endl;
  buffer << "\"N3\":" << result->N3<<std::endl;
  buffer << "}" << std::endl;
  return buffer.str();

}








string convertSingleContinuousResultToJSON(continuous_model_result* result)
{
  std::stringstream buffer;
  buffer << "{" << std::endl;
  
  buffer << "\"model\":" << result->model<<","<< std::endl;
  buffer << "\"dist\":" << result->dist<<","<< std::endl;
  buffer << "\"nparms\":" << result->nparms<<","<< std::endl;
  buffer << "\"max\":" << result->max<<","<< std::endl;
  buffer << "\"dist_numE\":" << result->dist_numE<<","<< std::endl;
  buffer << "\"model_df\":" << result->model_df<<","<< std::endl;
  buffer << "\"total_df\":" << result->total_df<<","<< std::endl;
  buffer << "\"parms\":";

  buffer <<"[";
  for(int i=0; i< result->nparms; i++)
  {
    if(i!=0)
       buffer<<",";
    buffer<<result->parms[i];
  }
  buffer <<"],";
  buffer << std::endl;

  /*
  buffer << "\"cov\":";
  buffer <<"[";
  for(int i=0; i< result->dist_numE; i++)
  {
    if(i!=0)
       buffer<<",";
    buffer<<result->cov[i];
  }
  buffer <<"],";
  buffer << std::endl;
  */

  buffer << "\"bmd_dist\":";
  buffer <<"[";
  for(int i=0; i< result->dist_numE; i++)
  {
    if(i!=0)
       buffer<<",";
    buffer<<result->bmd_dist[i];
    buffer<<",";
    buffer<<result->bmd_dist[i+result->dist_numE];

  }
  buffer <<"]";
  buffer << std::endl;


  buffer << "}" << std::endl;

  return buffer.str();

}


string convertMAContinuousResultToJSON(continuousMA_result* result)
{
  std::stringstream buffer;

  buffer << "{" << std::endl;
  
  buffer << "\"nmodels\":" << result->nmodels<<","<< std::endl;

  buffer << "\"models\":" << "["<< std::endl;
  for(int i=0;i<result->nmodels;i++)
  {
     buffer << convertSingleContinuousResultToJSON(result->models[i])<<endl;
     if(i+1 <result->nmodels)
        buffer << ","<<endl;
  }

  buffer << "],"<< std::endl;
  buffer << "\"dist_numE\":" << result->dist_numE<<","<< std::endl;

  buffer << "\"post_probs\":";
  buffer <<"[";
  for(int i=0; i< result->nmodels; i++)
  {
    if(i!=0)
       buffer<<",";
    buffer<<result->post_probs[i];
  }
  buffer <<"],";
  buffer << std::endl;





  buffer << "\"bmd_dist\":";
  buffer <<"[";
  for(int i=0; i< result->dist_numE; i++)
  {
    if(i!=0)
       buffer<<",";
    buffer<<result->bmd_dist[i];
    buffer<<",";
    buffer<<result->bmd_dist[i+result->dist_numE];
  }
  buffer <<"]";
  buffer << std::endl;



  buffer << std::endl;


  buffer << "}" << std::endl;




  return buffer.str();

}


string convertMCMCSingleContinuousResultToJSON(continuous_model_result* result, bmd_analysis_MCMC  *output)
{
  std::stringstream buffer;

  buffer << "{" << std::endl;
  
  buffer << "\"result\":" <<  std::endl;
  buffer << convertSingleContinuousResultToJSON(result)<<endl;
  buffer << ","<< std::endl;

  buffer << std::endl;

  buffer << "\"output\":";
  buffer << convertBMDAnalysisMCMCOutputToJSON(output)<<endl;
  buffer << std::endl;


  buffer << "}" << std::endl;

  return buffer.str();

}

string convertMCMCMAContinuousResultToJSON(continuousMA_result* result, ma_MCMCfits* model_mcmc_info)
{
  std::stringstream buffer;

  buffer << "{" << std::endl;
  buffer << "\"result\":" <<  std::endl;
  buffer << convertMAContinuousResultToJSON(result)<<endl;
  buffer << ","<< std::endl;

  buffer << std::endl;


  buffer << "\"output\":" << "["<< std::endl;
  for(unsigned int i=0;i<model_mcmc_info->nfits;i++)
  {
     buffer << convertBMDAnalysisMCMCOutputToJSON(model_mcmc_info->analyses[i])<< endl;
     if(i+1 < model_mcmc_info->nfits)
        buffer << ","<<endl;
  }

  buffer << "]"<< std::endl;



  buffer << "}" << std::endl;




  return buffer.str();

}

string convertBMDAnalysisMCMCOutputToJSON(bmd_analysis_MCMC* output)
{
  std::stringstream buffer;

  buffer <<"{";
  buffer << std::endl;
  buffer << "\"samples\":" << output->samples<<","<< std::endl;
  buffer << "\"nparms\":" << output->nparms<<","<< std::endl;
  buffer << "\"BMDS\":";
  buffer <<"[";
  for(int i=0; i< output->samples; i++)
  {
    if(i!=0)
       buffer<<",";
    buffer<<output->BMDS[i];
  }
  buffer <<"],";
  buffer << std::endl;

  buffer << "\"parms\":";
  buffer <<"[";
  for(int i=0; i< output->samples*output->nparms; i++)
  {
    if(i!=0)
       buffer<<",";
    buffer<<output->parms[i];
  }
  buffer <<"]";

  buffer <<"}";


  return buffer.str();

}


