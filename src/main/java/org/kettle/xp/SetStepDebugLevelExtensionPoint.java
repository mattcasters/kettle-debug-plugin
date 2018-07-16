package org.kettle.xp;

import org.kettle.DebugPluginsHelper;
import org.kettle.util.Defaults;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.extension.ExtensionPoint;
import org.pentaho.di.core.extension.ExtensionPointInterface;
import org.pentaho.di.core.extension.KettleExtensionPoint;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaDataCombi;

import java.util.List;
import java.util.Map;

@ExtensionPoint(
  id = "SetStepDebugLevelExtensionPoint",
  description = "Set Step Debug Level Extension Point Plugin",
  extensionPointId = "TransformationStartThreads"
)
/**
 * set the debug level right before the step starts to run
 */
public class SetStepDebugLevelExtensionPoint implements ExtensionPointInterface {

  @Override public void callExtensionPoint( LogChannelInterface logChannelInterface, Object o ) throws KettleException {
    if (!(o instanceof Trans )) {
      return;
    }

    Trans trans= (Trans) o;

    Map<String, String> stepLevelMap = trans.getTransMeta().getAttributesMap().get( Defaults.TRANSMETA_DEBUG_GROUP );

    if (stepLevelMap!=null) {

      logChannelInterface.logDetailed("SET DEBUG LEVELS ON TRANSFORMATION "+trans.getTransMeta().getName());

      for (String stepname: stepLevelMap.keySet() ) {
        List<StepInterface> baseSteps = trans.findBaseSteps( stepname );
        String logLevelCode = stepLevelMap.get( stepname );
        for (StepInterface stepInterface : baseSteps) {
          if (stepInterface instanceof BaseStep) {
            BaseStep baseStep = (BaseStep) stepInterface;
            LogLevel logLevel = LogLevel.getLogLevelForCode( logLevelCode );
            baseStep.setLogLevel( logLevel );
            logChannelInterface.logDetailed( "SET LOGGING LEVEL "+logLevel.getDescription()+" ON STEP COPY "+baseStep.getStepname()+"."+baseStep.getCopy());
          }
        }
      }
    }
  }
}
