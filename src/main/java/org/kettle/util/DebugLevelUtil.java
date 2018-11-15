package org.kettle.util;

import org.apache.commons.lang.StringUtils;
import org.kettle.JobEntryDebugLevel;
import org.kettle.StepDebugLevel;
import org.pentaho.di.core.Condition;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.logging.LogLevel;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Map;

public class DebugLevelUtil {

  public static void storeStepDebugLevel( Map<String,String> debugGroupAttributesMap, String stepName, StepDebugLevel debugLevel ) throws KettleValueException, UnsupportedEncodingException {
    debugGroupAttributesMap.put(stepName + " : " +Defaults.STEP_ATTR_LOGLEVEL, debugLevel.getLogLevel().getCode());
    debugGroupAttributesMap.put(stepName + " : " +Defaults.STEP_ATTR_START_ROW, Integer.toString(debugLevel.getStartRow()));
    debugGroupAttributesMap.put(stepName + " : " +Defaults.STEP_ATTR_END_ROW, Integer.toString(debugLevel.getEndRow()));

    String conditionXmlString = Base64.getEncoder().encodeToString( debugLevel.getCondition().getXML().getBytes( "UTF-8" ) );
    debugGroupAttributesMap.put(stepName + " : " +Defaults.STEP_ATTR_CONDITION, conditionXmlString);
  }

  public static StepDebugLevel getStepDebugLevel( Map<String, String> debugGroupAttributesMap, String stepName ) throws UnsupportedEncodingException, KettleXMLException {


    String logLevelCode = debugGroupAttributesMap.get(stepName+ " : " + Defaults.STEP_ATTR_LOGLEVEL );
    String startRowString = debugGroupAttributesMap.get(stepName+ " : " + Defaults.STEP_ATTR_START_ROW );
    String endRowString = debugGroupAttributesMap.get(stepName+ " : " + Defaults.STEP_ATTR_END_ROW );
    String conditionString = debugGroupAttributesMap.get(stepName+ " : " + Defaults.STEP_ATTR_CONDITION );

    if ( StringUtils.isEmpty(logLevelCode)) {
      // Nothing to load
      //
      return null;
    }

    StepDebugLevel debugLevel = new StepDebugLevel();
    debugLevel.setLogLevel( LogLevel.getLogLevelForCode( logLevelCode ) );
    debugLevel.setStartRow( Const.toInt(startRowString, -1) );
    debugLevel.setEndRow( Const.toInt(endRowString, -1) );

    if (StringUtils.isNotEmpty( conditionString )) {
      String conditionXml = new String( Base64.getDecoder().decode( conditionString ), "UTF-8" );
      debugLevel.setCondition( new Condition( conditionXml ) );
    }
    return debugLevel;
  }


  public static void clearDebugLevel( Map<String,String> debugGroupAttributesMap, String stepName) {
    debugGroupAttributesMap.remove(stepName+ " : " + Defaults.STEP_ATTR_LOGLEVEL );
    debugGroupAttributesMap.remove(stepName+ " : " + Defaults.STEP_ATTR_START_ROW );
    debugGroupAttributesMap.remove(stepName+ " : " + Defaults.STEP_ATTR_END_ROW );
    debugGroupAttributesMap.remove(stepName+ " : " + Defaults.STEP_ATTR_CONDITION );

    debugGroupAttributesMap.remove(stepName+ " : " + Defaults.JOBENTRY_ATTR_LOGLEVEL);
    debugGroupAttributesMap.remove(stepName+ " : " + Defaults.JOBENTRY_ATTR_LOG_RESULT);
    debugGroupAttributesMap.remove(stepName+ " : " + Defaults.JOBENTRY_ATTR_LOG_VARIABLES);
    debugGroupAttributesMap.remove(stepName+ " : " + Defaults.JOBENTRY_ATTR_LOG_RESULT_ROWS);
    debugGroupAttributesMap.remove(stepName+ " : " + Defaults.JOBENTRY_ATTR_LOG_RESULT_FILES);
  }

  public static void storeJobEntryDebugLevel( Map<String,String> debugGroupAttributesMap, String entryName, JobEntryDebugLevel debugLevel ) throws KettleValueException, UnsupportedEncodingException {
    debugGroupAttributesMap.put(entryName + " : " +Defaults.JOBENTRY_ATTR_LOGLEVEL, debugLevel.getLogLevel().getCode());
    debugGroupAttributesMap.put(entryName + " : " +Defaults.JOBENTRY_ATTR_LOG_RESULT, debugLevel.isLoggingResult() ? "Y" : "N");
    debugGroupAttributesMap.put(entryName + " : " +Defaults.JOBENTRY_ATTR_LOG_VARIABLES, debugLevel.isLoggingVariables() ? "Y" : "N");
    debugGroupAttributesMap.put(entryName + " : " +Defaults.JOBENTRY_ATTR_LOG_RESULT_ROWS, debugLevel.isLoggingResultRows() ? "Y" : "N");
    debugGroupAttributesMap.put(entryName + " : " +Defaults.JOBENTRY_ATTR_LOG_RESULT_FILES, debugLevel.isLoggingResultFiles() ? "Y" : "N");
  }

  public static JobEntryDebugLevel getJobEntryDebugLevel( Map<String, String> debugGroupAttributesMap, String entryName ) throws UnsupportedEncodingException, KettleXMLException {

    String logLevelCode = debugGroupAttributesMap.get(entryName+ " : " + Defaults.JOBENTRY_ATTR_LOGLEVEL);
    boolean loggingResult      = "Y".equalsIgnoreCase( debugGroupAttributesMap.get(entryName+ " : " + Defaults.JOBENTRY_ATTR_LOG_RESULT) );
    boolean loggingVariables   = "Y".equalsIgnoreCase( debugGroupAttributesMap.get(entryName+ " : " + Defaults.JOBENTRY_ATTR_LOG_VARIABLES) );
    boolean loggingResultRows  = "Y".equalsIgnoreCase( debugGroupAttributesMap.get(entryName+ " : " + Defaults.JOBENTRY_ATTR_LOG_RESULT_ROWS) );
    boolean loggingResultFiles = "Y".equalsIgnoreCase( debugGroupAttributesMap.get(entryName+ " : " + Defaults.JOBENTRY_ATTR_LOG_RESULT_FILES) );

    if ( StringUtils.isEmpty(logLevelCode)) {
      // Nothing to load
      //
      return null;
    }

    JobEntryDebugLevel debugLevel = new JobEntryDebugLevel();
    debugLevel.setLogLevel( LogLevel.getLogLevelForCode( logLevelCode ) );
    debugLevel.setLoggingResult( loggingResult );
    debugLevel.setLoggingVariables( loggingVariables );
    debugLevel.setLoggingResultRows( loggingResultRows );
    debugLevel.setLoggingResultFiles( loggingResultFiles );

    return debugLevel;
  }
}
