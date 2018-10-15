package org.kettle.xp;

import org.kettle.DebugLevel;
import org.kettle.util.DebugLevelUtil;
import org.kettle.util.Defaults;
import org.kettle.xp.util.SvgLoader;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.extension.ExtensionPoint;
import org.pentaho.di.core.extension.ExtensionPointInterface;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.trans.TransPainterExtension;
import org.pentaho.di.ui.spoon.Spoon;

import java.awt.image.BufferedImage;
import java.util.Map;

@ExtensionPoint(
  id = "DrawDebugLevelBeeExtensionPoint",
  description = "Draw a bee over a step which has debug level information stored",
  extensionPointId = "TransPainterStep"
)
/**
 * Paint steps that have a debug level set...
 */
public class DrawDebugLevelBeeExtensionPoint implements ExtensionPointInterface {

  private static BufferedImage beeImage;

  @Override public void callExtensionPoint( LogChannelInterface logChannelInterface, Object o ) throws KettleException {
    if ( !( o instanceof TransPainterExtension ) ) {
      return;
    }

    try {
      // The next statement sometimes causes an exception in WebSpoon
      // Keep it in the try/catch block
      //
      Spoon spoon = Spoon.getInstance();
      TransPainterExtension tpe = (TransPainterExtension) o;

      Map<String, String> stepLevelMap = tpe.transMeta.getAttributesMap().get( Defaults.TRANSMETA_DEBUG_GROUP );

      if ( stepLevelMap != null ) {

        String stepname = tpe.stepMeta.getName();

        final DebugLevel debugLevel = DebugLevelUtil.getDebugLevel( stepLevelMap, stepname );
        if ( debugLevel != null ) {

          // Paint the bee...
          //
          // SwtUniversalImage beeImage = SwtSvgImageUtil.getUniversalImage( spoon.getDisplay(), this.getClass().getClassLoader(), "bee.svg" );

          if ( beeImage == null ) {
            beeImage = SvgLoader.transcodeSVGDocument( this.getClass().getClassLoader(), "bee.svg", 30, 26 );
          }

          tpe.gc.drawImage( beeImage, tpe.x1 + tpe.iconsize, tpe.y1 - tpe.iconsize / 2 );

        }
      }
    } catch ( Exception e ) {
      // Ignore error, not that important
      // logChannelInterface.logError( "Unable to handle specific debug level", e );
    }
  }
}
