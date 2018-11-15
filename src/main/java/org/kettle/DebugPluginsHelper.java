/*! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2017 by Pentaho : http://www.pentaho.com
 *
 *******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/

package org.kettle;

import org.kettle.dialog.JobEntryDebugLevelDialog;
import org.kettle.dialog.StepDebugLevelDialog;
import org.kettle.util.DebugLevelUtil;
import org.kettle.util.Defaults;
import org.kettle.xp.util.ZoomLevel;
import org.pentaho.di.core.gui.SpoonFactory;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.job.entry.JobEntryCopy;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.ui.core.dialog.ErrorDialog;
import org.pentaho.di.ui.spoon.ISpoonMenuController;
import org.pentaho.di.ui.spoon.Spoon;
import org.pentaho.di.ui.spoon.job.JobGraph;
import org.pentaho.di.ui.spoon.trans.TransGraph;
import org.pentaho.ui.xul.dom.Document;
import org.pentaho.ui.xul.impl.AbstractXulEventHandler;

import java.util.HashMap;
import java.util.Map;

public class DebugPluginsHelper extends AbstractXulEventHandler implements ISpoonMenuController {
  protected static Class<?> PKG = DebugPluginsHelper.class; // for i18n

  private static DebugPluginsHelper instance = null;

  private DebugPluginsHelper() {
  }

  public static DebugPluginsHelper getInstance() {
    if ( instance == null ) {
      instance = new DebugPluginsHelper();
      Spoon spoon = ( (Spoon) SpoonFactory.getInstance() );
      spoon.addSpoonMenuController( instance );
    }
    return instance;
  }

  public String getName() {
    return "debugHelper";
  }

  public void updateMenu( Document doc ) {
    // Nothing so far.
  }

  public void lastZoomLevel() {

    Spoon spoon = ( (Spoon) SpoonFactory.getInstance() );
    TransGraph transGraph = spoon.getActiveTransGraph();
    if (transGraph!=null) {
      ZoomLevel.changeTransGraphZoomLevel();
    }
    JobGraph jobGraph = spoon.getActiveJobGraph();
    if (jobGraph!=null) {
      ZoomLevel.changeJobGraphZoomLevel();
    }
  }

  public void setStepLoggingLevel() {
    setClearStepLogLevel(true);
  }

  public void clearStepLoggingLevel() {
    setClearStepLogLevel( false );
  }

  private void setClearStepLogLevel( boolean set) {
    Spoon spoon = ( (Spoon) SpoonFactory.getInstance() );
    try {
      TransGraph transGraph = spoon.getActiveTransGraph();
      TransMeta transMeta = spoon.getActiveTransformation();
      StepMeta stepMeta = transGraph.getCurrentStep();
      if ( transGraph == null || transMeta == null || stepMeta == null ) {
        return;
      }

      Map<String, Map<String, String>> attributesMap = transMeta.getAttributesMap();
      Map<String, String> debugGroupAttributesMap = attributesMap.get( Defaults.DEBUG_GROUP );

      if ( debugGroupAttributesMap == null ) {
        debugGroupAttributesMap = new HashMap<>();
        attributesMap.put( Defaults.DEBUG_GROUP, debugGroupAttributesMap );
      }

      if ( !set ) {
        DebugLevelUtil.clearDebugLevel( debugGroupAttributesMap, stepMeta.getName());
        transMeta.setChanged();
        spoon.refreshGraph();
        return;
      }

      StepDebugLevel debugLevel = DebugLevelUtil.getStepDebugLevel( debugGroupAttributesMap, stepMeta.getName() );
      if ( debugLevel==null ) {
        debugLevel = new StepDebugLevel();
      }

      RowMetaInterface inputRowMeta = transMeta.getPrevStepFields( stepMeta );
      StepDebugLevelDialog dialog = new StepDebugLevelDialog( spoon.getShell(), debugLevel, inputRowMeta );
      if (dialog.open()) {
        DebugLevelUtil.storeStepDebugLevel(debugGroupAttributesMap, stepMeta.getName(), debugLevel);
      }

      transMeta.setChanged();
      spoon.refreshGraph();
    } catch(Exception e) {
      new ErrorDialog( spoon.getShell(), "Error", "Unexpected error", e );
    }
  }


  public void clearAllTransLogging() {
    Spoon spoon = ( (Spoon) SpoonFactory.getInstance() );
    TransGraph transGraph = spoon.getActiveTransGraph();
    TransMeta transMeta = spoon.getActiveTransformation();
    StepMeta stepMeta = transGraph.getCurrentStep();
    if ( transGraph == null || transMeta == null || stepMeta == null ) {
      return;
    }
    Map<String, Map<String, String>> attributesMap = transMeta.getAttributesMap();
    attributesMap.remove( Defaults.DEBUG_GROUP );
    transMeta.setChanged();
    spoon.refreshGraph();
  }

  public void setJobEntryLoggingLevel() {
    setClearJobEntryLogLevel(true);
  }

  public void clearJobEntryLoggingLevel() {
    setClearJobEntryLogLevel( false );
  }

  private void setClearJobEntryLogLevel( boolean set) {
    Spoon spoon = ( (Spoon) SpoonFactory.getInstance() );
    try {
      JobGraph jobGraph = spoon.getActiveJobGraph();
      JobMeta jobMeta = spoon.getActiveJob();
      JobEntryCopy jobEntryCopy = jobGraph.getJobEntry();
      if ( jobGraph == null || jobMeta == null || jobEntryCopy == null ) {

        return;
      }

      Map<String, Map<String, String>> attributesMap = jobMeta.getAttributesMap();
      Map<String, String> debugGroupAttributesMap = attributesMap.get( Defaults.DEBUG_GROUP );
      if ( debugGroupAttributesMap == null ) {
        debugGroupAttributesMap = new HashMap<>();
        attributesMap.put( Defaults.DEBUG_GROUP, debugGroupAttributesMap );
      }

      if ( !set ) {
        DebugLevelUtil.clearDebugLevel( debugGroupAttributesMap, jobEntryCopy.toString());
        jobMeta.setChanged();
        spoon.refreshGraph();
        return;
      }

      JobEntryDebugLevel debugLevel = DebugLevelUtil.getJobEntryDebugLevel( debugGroupAttributesMap, jobEntryCopy.toString() );
      if ( debugLevel==null ) {
        debugLevel = new JobEntryDebugLevel();
      }

      JobEntryDebugLevelDialog dialog = new JobEntryDebugLevelDialog( spoon.getShell(), debugLevel);
      if (dialog.open()) {
        DebugLevelUtil.storeJobEntryDebugLevel(debugGroupAttributesMap, jobEntryCopy.toString(), debugLevel);
      }

      jobMeta.setChanged();
      spoon.refreshGraph();
    } catch(Exception e) {
      new ErrorDialog( spoon.getShell(), "Error", "Unexpected error", e );
    }
  }

  public void clearAllJobLogging() {
    Spoon spoon = ( (Spoon) SpoonFactory.getInstance() );
    JobGraph jobGraph = spoon.getActiveJobGraph();
    JobMeta jobMeta = spoon.getActiveJob();
    JobEntryCopy jobEntry = jobGraph.getJobEntry();
    if ( jobGraph == null || jobMeta == null || jobEntry == null ) {
      return;
    }
    Map<String, Map<String, String>> attributesMap = jobMeta.getAttributesMap();
    attributesMap.remove( Defaults.DEBUG_GROUP );
    jobMeta.setChanged();
    spoon.refreshGraph();
  }
}
