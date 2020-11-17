/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_toxicR_ToxicRJNI */

#ifndef _Included_com_toxicR_ToxicRJNI
#define _Included_com_toxicR_ToxicRJNI
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_toxicR_ToxicRJNI
 * Method:    runContinuousSingleJNI
 * Signature: (IZ[D[D[D[D[DIZDDIDIII)V
 */
JNIEXPORT jstring JNICALL Java_com_toxicR_ToxicRJNI_runContinuousSingleJNI
  (JNIEnv *, jobject, jint, jboolean, jdoubleArray, jdoubleArray, jdoubleArray, jdoubleArray, jdoubleArray, jint, jboolean, jdouble, jdouble, jint, jdouble, jint,jint, jint, jint, jint);

string convertSingleContinuousResultToJSON(continuous_model_result* result);

JNIEXPORT jstring JNICALL Java_com_toxicR_ToxicRJNI_runContinuousMAJNI
  (JNIEnv *, jobject, jint, jintArray, jintArray, jintArray,
   jintArray, jintArray, jdoubleArray, jboolean, 
   jdoubleArray, jdoubleArray, jdoubleArray, jdoubleArray, jdoubleArray, 
   jint, jboolean, jdouble, jdouble, 
   jdouble, jint, jint );

string convertMAContinuousResultToJSON(continuousMA_result* result);


JNIEXPORT jstring JNICALL Java_com_toxicR_ToxicRJNI_runContinuousMCMCSingleJNI
  (JNIEnv *env, jobject thisObject, jint model, jboolean suff_stat, 
   jdoubleArray Y, jdoubleArray doses, jdoubleArray sd, jdoubleArray n_group, jdoubleArray prior, 
   jint BMD_type, jboolean isIncreasing, jdouble BMR, jdouble tail_prob, jint disttype, 
   jdouble alpha, jint samples, jint burnin, jint parms, jint prior_cols, jint degree);

string convertMCMCSingleContinuousResultToJSON(continuous_model_result* result, bmd_analysis_MCMC  *output);


JNIEXPORT jstring JNICALL Java_com_toxicR_ToxicRJNI_runContinuousMCMCMAJNI
  (JNIEnv *env, jobject thisObject, jint nmodels, jintArray jmodels, jintArray jnparms, jintArray jactual_parms,
   jintArray jprior_cols, jintArray jdisttypes, jdoubleArray jmodelPriors, jboolean suff_stat, 
   jdoubleArray Y, jdoubleArray doses, jdoubleArray sd, jdoubleArray n_group, jdoubleArray jpriors, 
   jint BMD_type, jboolean isIncreasing, jdouble BMR, jdouble tail_prob, 
   jdouble alpha, jint samples, jint burnin );

string convertMCMCMAContinuousResultToJSON(continuousMA_result* result, ma_MCMCfits* model_mcmc_info);

string convertBMDAnalysisMCMCOutputToJSON(bmd_analysis_MCMC  *output);

#ifdef __cplusplus
}
#endif
#endif
