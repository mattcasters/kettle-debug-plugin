package org.kettle.xp;

import org.eclipse.swt.graphics.Image;
import org.kettle.DebugLevel;
import org.kettle.util.DebugLevelUtil;
import org.kettle.util.Defaults;
import org.kettle.xp.util.SvgLoader;
import org.pentaho.di.core.Condition;
import org.pentaho.di.core.SwtUniversalImage;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.extension.ExtensionPoint;
import org.pentaho.di.core.extension.ExtensionPointInterface;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.svg.SvgImage;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransPainterExtension;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.RowAdapter;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.ui.spoon.Spoon;
import org.pentaho.di.ui.util.ImageUtil;
import org.pentaho.di.ui.util.SwtSvgImageUtil;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

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
    if (!(o instanceof TransPainterExtension )) {
      return;
    }

    Spoon spoon = Spoon.getInstance();
    TransPainterExtension tpe= (TransPainterExtension) o;

    Map<String, String> stepLevelMap = tpe.transMeta.getAttributesMap().get( Defaults.TRANSMETA_DEBUG_GROUP );

    if (stepLevelMap!=null) {

      String stepname = tpe.stepMeta.getName();

      try {

        final DebugLevel debugLevel = DebugLevelUtil.getDebugLevel( stepLevelMap, stepname );
        if ( debugLevel != null ) {

          // Paint the bee...
          //
          // SwtUniversalImage beeImage = SwtSvgImageUtil.getUniversalImage( spoon.getDisplay(), this.getClass().getClassLoader(), "bee.svg" );

          if (beeImage==null) {
            beeImage = SvgLoader.transcodeSVGDocument( this.getClass().getClassLoader(), "bee.svg", 30, 26 );
          }

          tpe.gc.drawImage( beeImage, tpe.x1+tpe.iconsize, tpe.y1-tpe.iconsize/2);

        }

      } catch ( Exception e ) {
        logChannelInterface.logError( "Unable to handle specific debug level for step : " + stepname, e );
      }
    }

  }
}
