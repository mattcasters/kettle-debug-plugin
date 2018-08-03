package org.kettle.xp;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.kettle.xp.util.ZoomLevel;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.extension.ExtensionPoint;
import org.pentaho.di.core.extension.ExtensionPointInterface;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.ui.spoon.Spoon;
import org.pentaho.di.ui.spoon.job.JobGraph;
import org.pentaho.di.ui.spoon.trans.TransGraph;

@ExtensionPoint(
  id = "SetDefaultJobZoomLevelExtensionPoint",
  description = "After a file is opened, set the appropriate zoom level",
  extensionPointId = "JobAfterOpen"
)
/**
 * Every time we load a transformation in Spoon, set the zoom level to what we used last time...
 */
public class SetDefaultJobZoomLevelExtensionPoint implements ExtensionPointInterface {

  @Override public void callExtensionPoint( LogChannelInterface log, Object o ) throws KettleException {

    ZoomLevel.changeJobGraphZoomLevel();

  }
}
