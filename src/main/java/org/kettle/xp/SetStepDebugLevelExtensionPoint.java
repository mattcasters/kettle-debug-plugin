package org.kettle.xp;

import org.kettle.StepDebugLevel;
import org.kettle.util.DebugLevelUtil;
import org.kettle.util.Defaults;
import org.pentaho.di.core.Condition;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.extension.ExtensionPoint;
import org.pentaho.di.core.extension.ExtensionPointInterface;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.RowAdapter;
import org.pentaho.di.trans.step.StepInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

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

    Map<String, String> stepLevelMap = trans.getTransMeta().getAttributesMap().get( Defaults.DEBUG_GROUP );

    if (stepLevelMap!=null) {

      logChannelInterface.logDetailed( "Set debug level information on transformation : " + trans.getTransMeta().getName() );

      List<String> stepnames = new ArrayList<>(  );
      for ( String key : stepLevelMap.keySet() ) {

        int index = key.indexOf( " : " );
        if ( index > 0 ) {
          String stepname = key.substring( 0, index );
          if ( !stepnames.contains( stepname ) ) {
            stepnames.add( stepname );
          }
        }
      }

      for ( String stepname : stepnames ) {

        logChannelInterface.logDetailed( "Handling debug level for step : "+stepname );

        try {

          final StepDebugLevel debugLevel = DebugLevelUtil.getStepDebugLevel( stepLevelMap, stepname );
          if ( debugLevel != null ) {

            logChannelInterface.logDetailed("Found debug level info for step "+stepname);

            List<StepInterface> baseSteps = trans.findBaseSteps( stepname );

            if (debugLevel.getStartRow()<0 && debugLevel.getEndRow()<0 && debugLevel.getCondition().isEmpty()) {

              logChannelInterface.logDetailed("Set logging level for step "+stepname+" to "+debugLevel.getLogLevel().getDescription());

              // Just a general log level on the step
              //
              String logLevelCode = stepLevelMap.get( stepname );
              for ( StepInterface stepInterface : baseSteps ) {
                if ( stepInterface instanceof BaseStep ) {
                  BaseStep baseStep = (BaseStep) stepInterface;
                  LogLevel logLevel = debugLevel.getLogLevel();
                  baseStep.setLogLevel( logLevel );
                  logChannelInterface.logDetailed( "SET LOGGING LEVEL " + logLevel.getDescription() + " ON STEP COPY " + baseStep.getStepname() + "." + baseStep.getCopy() );
                }
              }
            } else {

              // We need to look at every row
              //
              for ( StepInterface stepInterface : baseSteps ) {

                final LogLevel baseLogLevel = stepInterface.getLogChannel().getLogLevel();
                final AtomicLong rowCounter = new AtomicLong( 0L );

                stepInterface.addRowListener( new RowAdapter() {

                  @Override public void rowReadEvent( RowMetaInterface rowMeta, Object[] row ) throws KettleStepException {

                    rowCounter.incrementAndGet();
                    boolean enabled = false;

                    Condition condition = debugLevel.getCondition();

                    if (debugLevel.getStartRow()>0 && rowCounter.get()>=debugLevel.getStartRow() && debugLevel.getEndRow()>=0 && debugLevel.getEndRow()>=rowCounter.get()) {
                      // If we have a start and an end, we want to stay between start and end
                      enabled = true;
                    } else if (debugLevel.getStartRow()<=0 && debugLevel.getEndRow()>=0 && rowCounter.get()<=debugLevel.getEndRow()) {
                      // If don't have a start row, just and end...
                      enabled = true;
                    } else if (debugLevel.getEndRow()<=0 && debugLevel.getStartRow()>=0 && rowCounter.get()>=debugLevel.getStartRow()) {
                      enabled = true;
                    }

                    if ( (debugLevel.getStartRow()<=0 && debugLevel.getEndRow()<=0 || enabled) && !condition.isEmpty()) {
                      enabled = condition.evaluate( rowMeta, row );
                    }

                    if (enabled) {
                      stepInterface.getLogChannel().setLogLevel( debugLevel.getLogLevel() );
                    }
                  }

                  @Override public void rowWrittenEvent( RowMetaInterface rowMeta, Object[] row ) throws KettleStepException {

                    // Set the log level back to the original value.
                    //
                    stepInterface.getLogChannel().setLogLevel( baseLogLevel );
                  }
                } );

              }
            }
          }
        } catch ( Exception e ) {
          logChannelInterface.logError( "Unable to handle specific debug level for step : " + stepname, e );
        }
      }

    }

  }
}
