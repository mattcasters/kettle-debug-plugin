package org.kettle.xp;

import org.kettle.JobEntryDebugLevel;
import org.kettle.util.DebugLevelUtil;
import org.kettle.util.Defaults;
import org.kettle.xp.util.SvgLoader;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.extension.ExtensionPoint;
import org.pentaho.di.core.extension.ExtensionPointInterface;
import org.pentaho.di.core.gui.GCInterface;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.job.JobPainterExtension;
import org.pentaho.di.job.entry.JobEntryCopy;
import org.pentaho.di.ui.core.PropsUI;
import org.pentaho.di.ui.spoon.Spoon;

import java.awt.image.BufferedImage;
import java.io.UnsupportedEncodingException;
import java.util.Map;

@ExtensionPoint(
  id = "DrawJobEntryDebugLevelBeeExtensionPoint",
  description = "Draw a bee over a job entry which has debug level information stored",
  extensionPointId = "JobPainterArrow"
)
/**
 * We need to use the hop drawing logic because the job entry XP is not available
 */
public class DrawJobEntryDebugLevelBeeExtensionPoint implements ExtensionPointInterface {

  private static BufferedImage beeImage;

  @Override public void callExtensionPoint( LogChannelInterface log, Object o ) throws KettleException {
    if ( !( o instanceof JobPainterExtension ) ) {
      return;
    }

    try {
      // The next statement sometimes causes an exception in WebSpoon
      // Keep it in the try/catch block
      //
      Spoon spoon = Spoon.getInstance();
      JobPainterExtension jpe = (JobPainterExtension) o;
      int iconsize = PropsUI.getInstance().getIconSize();

      Map<String, String> entryLevelMap = jpe.jobMeta.getAttributesMap().get( Defaults.DEBUG_GROUP );

      if ( entryLevelMap != null ) {

        drawBee( jpe.gc, entryLevelMap, jpe.jobHop.getFromEntry(), jpe.x1, jpe.y1, iconsize );
        drawBee( jpe.gc, entryLevelMap, jpe.jobHop.getToEntry(), jpe.x2, jpe.y2, iconsize );
      }
    } catch ( Exception e ) {
      // Ignore error, not that important
      // logChannelInterface.logError( "Unable to handle specific debug level", e );
    }
  }

  private void drawBee( GCInterface gc, Map<String, String> entryLevelMap, JobEntryCopy copy, int x, int y, int iconsize ) throws Exception {

    final JobEntryDebugLevel debugLevel = DebugLevelUtil.getJobEntryDebugLevel( entryLevelMap, copy.toString() );
    if ( debugLevel == null ) {
      return;
    }

    if ( beeImage == null ) {
      beeImage = SvgLoader.transcodeSVGDocument( this.getClass().getClassLoader(), "bee.svg", 30, 26 );
    }

    gc.drawImage( beeImage, x + iconsize / 2, y - iconsize );
  }
}
