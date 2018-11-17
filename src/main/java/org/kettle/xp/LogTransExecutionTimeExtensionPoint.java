package org.kettle.xp;

import org.kettle.util.DebugLevelUtil;
import org.kettle.util.Defaults;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.extension.ExtensionPoint;
import org.pentaho.di.core.extension.ExtensionPointInterface;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransAdapter;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepInterface;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@ExtensionPoint(
  id = "LogTransExecutionTimeExtensionPoint",
  description = "Logs execution time of a transformation when it finishes",
  extensionPointId = "TransformationPrepareExecution"
)
/**
 * set the debug level right before the step starts to run
 */
public class LogTransExecutionTimeExtensionPoint implements ExtensionPointInterface {

  @Override public void callExtensionPoint( LogChannelInterface log, Object o ) throws KettleException {
    if (!(o instanceof Trans )) {
      return;
    }

    Trans trans= (Trans) o;

    // If the KETTLE_DEBUG_DURATION variable is set to N or FALSE, we don't log duration
    //
    String durationVariable = trans.getVariable( Defaults.VARIABLE_KETTLE_DEBUG_DURATION, "Y" );
    if ("N".equalsIgnoreCase( durationVariable ) || "FALSE".equalsIgnoreCase( durationVariable)) {
      // Nothing to do here
      return;
    }

    final long startTime = System.currentTimeMillis();

    trans.addTransListener( new TransAdapter() {
      @Override public void transFinished( Trans trans ) throws KettleException {
        long endTime = System.currentTimeMillis();
        double seconds = ((double)endTime - (double)startTime) / 1000;
        log.logBasic("Transformation duration : "+ seconds+" seconds [ "+ DebugLevelUtil.getDurationHMS( seconds ) +" ]");
      }
    } );

  }
}
