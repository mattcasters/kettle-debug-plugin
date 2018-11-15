package org.kettle.xp;

import org.kettle.xp.util.ZoomLevel;
import org.pentaho.di.job.JobPainter;
import org.pentaho.di.trans.TransPainter;


public class LastZoomBase {

  private static int lastZoom;

  public void rememberTransZoomLevel(TransPainter transPainter) {

    ZoomLevel.getInstance().setLastTransMagnification( transPainter.getMagnification() );
  }

  public void rememberJobZoomLevel(JobPainter jobPainter) {

    ZoomLevel.getInstance().setLastJobMagnification( jobPainter.getMagnification() );
  }



}
