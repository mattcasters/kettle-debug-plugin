package org.kettle.xp;

import org.kettle.JobEntryDebugLevel;
import org.kettle.util.DebugLevelUtil;
import org.kettle.util.Defaults;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.ResultFile;
import org.pentaho.di.core.RowMetaAndData;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.extension.ExtensionPoint;
import org.pentaho.di.core.extension.ExtensionPointInterface;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.core.logging.LoggingObjectInterface;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.variables.Variables;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobEntryListener;
import org.pentaho.di.job.JobExecutionExtension;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.job.entry.JobEntryCopy;
import org.pentaho.di.job.entry.JobEntryInterface;
import org.pentaho.di.trans.Trans;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@ExtensionPoint(
  id = "ModifyJobEntryLogLevelExtensionPoint",
  extensionPointId = "JobStart",
  description = "Modify the logging level of an individual job entry if needed" )
public class ModifyJobEntryLogLevelExtensionPoint implements ExtensionPointInterface {

  public static final String STRING_REFERENCE_VARIABLE_SPACE = "REFERENCE_VARIABLE_SPACE";


  @Override public void callExtensionPoint( LogChannelInterface jobLog, Object object ) throws KettleException {

    if ( !( object instanceof Job ) ) {
      return;
    }

    Job job = (Job) object;

    Job rootJob = job;
    Trans rootTrans = null;
    while (rootJob!=null || rootTrans!=null) {

      if (rootJob!=null) {
        if (rootJob.getParentJob()==null && rootJob.getParentTrans()==null) {
          break;
        }
        rootJob = rootJob.getParentJob();
        rootTrans = rootJob.getParentTrans();
      } else {
        if (rootTrans.getParentJob()==null && rootTrans.getParentTrans()==null) {
          break;
        }
        rootJob = rootTrans.getParentJob();
        rootTrans = rootTrans.getParentTrans();
      }
    }
    Map<String, Object> rootDataMap;
    if (rootJob!=null) {
      rootDataMap = rootJob.getExtensionDataMap();
    } else {
      rootDataMap = rootTrans.getExtensionDataMap();
    }

    // Look for a reference variable space in the root job.
    // If non exists, add it.  Only do this at the start of the root job, afterwards, never again.
    //
    final VariableSpace referenceSpace;
    synchronized ( rootDataMap ) {
      VariableSpace space = (VariableSpace) rootDataMap.get( STRING_REFERENCE_VARIABLE_SPACE );
      if (space==null) {
        space = new Variables();
        space.initializeVariablesFrom( job.getJobMeta() );
        rootDataMap.put(STRING_REFERENCE_VARIABLE_SPACE, space);
      }
      referenceSpace = space;
    }

    // Find the debug info in the job metadata
    //
    JobMeta jobMeta = job.getJobMeta();

    Map<String, String> entryLevelMap = jobMeta.getAttributesMap().get( Defaults.DEBUG_GROUP );
    if ( entryLevelMap == null ) {
      return;
    }

    jobLog.logDetailed( "Set debug level information on job : " + jobMeta.getName() );

    final Set<String> entries = new HashSet<>();
    for ( String key : entryLevelMap.keySet() ) {

      int index = key.indexOf( " : " );
      if ( index > 0 ) {
        String entryName = key.substring( 0, index );
        if ( !entries.contains( entryName ) ) {
          entries.add( entryName );
        }
      }
    }

    if ( entries.isEmpty() ) {
      return;
    }

    try {

      final LogLevel jobLogLevel = job.getLogLevel();
      final Set<String> variablesToIgnore = Defaults.VARIABLES_TO_IGNORE;

      jobLog.logDetailed( "Found debug level info for job entries : " + entries.toString() );

      job.addJobEntryListener( new JobEntryListener() {
        @Override public void beforeExecution( Job job, JobEntryCopy jobEntryCopy, JobEntryInterface jobEntryInterface ) {

          LogChannelInterface log = jobEntryInterface.getLogChannel();

          try {
            // Is this a job entry with debugging set on it?
            //
            if ( entries.contains( jobEntryCopy.toString() ) ) {
              final JobEntryDebugLevel debugLevel = DebugLevelUtil.getJobEntryDebugLevel( entryLevelMap, jobEntryCopy.toString() );
              if ( debugLevel != null ) {
                // Set the debug level for this one...
                //
                log.setLogLevel( debugLevel.getLogLevel() );
                job.setLogLevel( debugLevel.getLogLevel() );
              }
            }
          } catch ( Exception e ) {
            log.logError( "Error setting logging level on entry" );
          }
        }

        @Override public void afterExecution( Job job, JobEntryCopy jobEntryCopy, JobEntryInterface jobEntryInterface, Result result ) {
          LogChannelInterface log = jobEntryInterface.getLogChannel();

          try {
            // Is this a job entry with debugging set on it?
            //
            if ( entries.contains( jobEntryCopy.toString() ) ) {
              final JobEntryDebugLevel debugLevel = DebugLevelUtil.getJobEntryDebugLevel( entryLevelMap, jobEntryCopy.toString() );
              if ( debugLevel != null ) {
                // Set the debug level for this one...
                //
                log.setLogLevel( jobLogLevel );
                job.setLogLevel( jobLogLevel );

                // Set the debug level back to normal...
                //

                if ( debugLevel.isLoggingResult() ) {
                  log.logMinimal( "JOB ENTRY RESULT: " );
                  log.logMinimal( "  - result=" + result.getResult() );
                  log.logMinimal( "  - stopped=" + result.isStopped() );
                  log.logMinimal( "  - linesRead=" + result.getNrLinesRead() );
                  log.logMinimal( "  - linesWritten=" + result.getNrLinesWritten() );
                  log.logMinimal( "  - linesInput=" + result.getNrLinesInput() );
                  log.logMinimal( "  - linesOutput=" + result.getNrLinesOutput() );
                  log.logMinimal( "  - linesRejected=" + result.getNrLinesRejected() );
                  log.logMinimal( "  - result row count=" + result.getRows().size() );
                  log.logMinimal( "  - result files count=" + result.getResultFilesList().size() );
                }
                if ( debugLevel.isLoggingResultRows() ) {
                  log.logMinimal( "JOB ENTRY RESULT ROWS: " );
                  for ( RowMetaAndData rmad : result.getRows() ) {
                    log.logMinimal( " - " + rmad.toString() );
                  }
                }
                if ( debugLevel.isLoggingResultFiles() ) {
                  log.logMinimal( "JOB ENTRY RESULT FILES: " );
                  for ( ResultFile resultFile : result.getResultFilesList() ) {
                    log.logMinimal( " - " + resultFile.getFile().toString() + " from " + resultFile.getOrigin() + " : " + resultFile.getComment() + " / " + resultFile.getTypeCode() );
                  }
                }
                if ( debugLevel.isLoggingVariables() ) {
                  if ( jobEntryInterface instanceof VariableSpace ) {
                    log.logMinimal( "JOB ENTRY NOTEABLE VARIABLES: " );

                    VariableSpace space = (VariableSpace) jobEntryInterface;
                    // See the variables set differently from the parent job
                    for ( String var : space.listVariables() ) {
                      if ( !variablesToIgnore.contains( var ) ) {
                        String value = space.getVariable( var );
                        String refValue = referenceSpace.getVariable( var );

                        if ( refValue == null || !refValue.equals( value ) ) {
                          // Something different!
                          //
                          log.logMinimal( " - " + var + "=" + value );
                        }
                      }
                    }
                  }
                }
              }
            }
          } catch ( Exception e ) {
            log.logError( "Error re-setting logging level on entry" );
          }
        }
      } );
    } catch ( Exception e ) {
      jobLog.logError( "Unable to handle specific debug level for job", e );
    }
  }
}

