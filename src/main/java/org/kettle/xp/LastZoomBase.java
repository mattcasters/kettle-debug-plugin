package org.kettle.xp;

import org.kettle.DebugLevel;
import org.kettle.util.DebugLevelUtil;
import org.kettle.util.Defaults;
import org.kettle.xp.util.SvgLoader;
import org.kettle.xp.util.ZoomLevel;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.extension.ExtensionPoint;
import org.pentaho.di.core.extension.ExtensionPointInterface;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.job.JobPainter;
import org.pentaho.di.trans.TransPainter;
import org.pentaho.di.trans.TransPainterExtension;
import org.pentaho.di.ui.spoon.Spoon;
import org.pentaho.di.ui.spoon.trans.TransGraph;

import java.awt.image.BufferedImage;
import java.util.Map;


public class LastZoomBase {

  private static int lastZoom;

  public void rememberTransZoomLevel(TransPainter transPainter) {

    ZoomLevel.getInstance().setLastTransMagnification( transPainter.getMagnification() );
  }

  public void rememberJobZoomLevel(JobPainter jobPainter) {

    ZoomLevel.getInstance().setLastJobMagnification( jobPainter.getMagnification() );
  }



}
