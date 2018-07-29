package org.kettle.util;

import org.apache.commons.lang.StringUtils;
import org.kettle.DebugLevel;
import org.pentaho.di.core.Condition;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.logging.LogLevel;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Map;

public class DebugLevelUtil {

  public static void storeDebugLevel( Map<String,String> debugGroupAttributesMap, String stepName, DebugLevel debugLevel ) throws KettleValueException, UnsupportedEncodingException {
    debugGroupAttributesMap.put(stepName + " : " +Defaults.TRANSMETA_ATTR_LOGLEVEL, debugLevel.getLogLevel().getCode());
    debugGroupAttributesMap.put(stepName + " : " +Defaults.TRANSMETA_ATTR_START_ROW, Integer.toString(debugLevel.getStartRow()));
    debugGroupAttributesMap.put(stepName + " : " +Defaults.TRANSMETA_ATTR_END_ROW, Integer.toString(debugLevel.getEndRow()));

    String conditionXmlString = Base64.getEncoder().encodeToString( debugLevel.getCondition().getXML().getBytes( "UTF-8" ) );
    debugGroupAttributesMap.put(stepName + " : " +Defaults.TRANSMETA_ATTR_CONDITION, conditionXmlString);
  }

  public static DebugLevel getDebugLevel( Map<String, String> debugGroupAttributesMap, String stepName ) throws UnsupportedEncodingException, KettleXMLException {


    String logLevelCode = debugGroupAttributesMap.get(stepName+ " : " + Defaults.TRANSMETA_ATTR_LOGLEVEL);
    String startRowString = debugGroupAttributesMap.get(stepName+ " : " + Defaults.TRANSMETA_ATTR_START_ROW);
    String endRowString = debugGroupAttributesMap.get(stepName+ " : " + Defaults.TRANSMETA_ATTR_END_ROW);
    String conditionString = debugGroupAttributesMap.get(stepName+ " : " + Defaults.TRANSMETA_ATTR_CONDITION);

    if ( StringUtils.isEmpty(logLevelCode)) {
      // Nothing to load
      //
      return null;
    }

    DebugLevel debugLevel = new DebugLevel();
    debugLevel.setLogLevel( LogLevel.getLogLevelForCode( logLevelCode ) );
    debugLevel.setStartRow( Const.toInt(startRowString, -1) );
    debugLevel.setEndRow( Const.toInt(endRowString, -1) );

    if (StringUtils.isNotEmpty( conditionString )) {
      String conditionXml = new String( Base64.getDecoder().decode( conditionString ), "UTF-8" );
      debugLevel.setCondition( new Condition( conditionXml ) );
    }
    return debugLevel;
  }


}
