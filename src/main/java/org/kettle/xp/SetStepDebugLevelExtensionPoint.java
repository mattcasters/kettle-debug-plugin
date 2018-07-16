package org.kettle.xp;

import org.kettle.DebugPluginsHelper;
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

    logChannelInterface.logBasic( "!!!!! SET DEBUG LEVELS ON STEP COPIES !!!!!" );
    Trans trans= (Trans) o;

    Map<TransMeta, Map<StepMeta, LogLevel>> transStepLevelMap = DebugPluginsHelper.getInstance().getTransStepLevelMap();

    Map<StepMeta, LogLevel> stepLevelMap = transStepLevelMap.get( trans.getTransMeta() );

    if (stepLevelMap!=null) {

      logChannelInterface.logBasic( "!!!!! SET DEBUG LEVELS ON TRANSFORMATION "+trans.getTransMeta().getName());

      for (StepMeta stepMeta : stepLevelMap.keySet() ) {
        List<StepInterface> baseSteps = trans.findBaseSteps( stepMeta.getName() );
        LogLevel logLevel = stepLevelMap.get( stepMeta );
        for (StepInterface stepInterface : baseSteps) {
          if (stepInterface instanceof BaseStep) {
            BaseStep baseStep = (BaseStep) stepInterface;
            baseStep.setLogLevel( logLevel );
            logChannelInterface.logBasic( "!!!!! SET DEBUG LEVELS ON STEP COPY "+baseStep.getStepname()+"."+baseStep.getCopy());
          }
        }
      }
    }
  }
}
