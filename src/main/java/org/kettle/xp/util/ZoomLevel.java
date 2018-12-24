package org.kettle.xp.util;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.pentaho.di.core.Const;
import org.pentaho.di.ui.core.PropsUI;
import org.pentaho.di.ui.spoon.AbstractGraph;
import org.pentaho.di.ui.spoon.Spoon;
import org.pentaho.di.ui.spoon.job.JobGraph;
import org.pentaho.di.ui.spoon.trans.TransGraph;

public class ZoomLevel {

  public static final String PROPERTY_LAST_TRANS_MAGNIFICATION = "LastTransMagnification";
  public static final String PROPERTY_LAST_JOB_MAGNIFICATION = "LastJobMagnification";

  private static ZoomLevel zoomLevel;

  private float lastTransMagnification;
  private float lastJobMagnification;

  private ZoomLevel() {
    lastTransMagnification = (float) Const.toDouble( PropsUI.getInstance().getProperty( PROPERTY_LAST_TRANS_MAGNIFICATION ), 1.0f );
    lastJobMagnification = (float) Const.toDouble( PropsUI.getInstance().getProperty( PROPERTY_LAST_JOB_MAGNIFICATION ), 1.0f );
  }

  public static ZoomLevel getInstance() {
    if ( zoomLevel == null ) {
      zoomLevel = new ZoomLevel();
    }

    return zoomLevel;
  }

  public void saveProperties() {
    PropsUI.getInstance().setProperty( PROPERTY_LAST_TRANS_MAGNIFICATION, Float.toString( lastTransMagnification ) );
    PropsUI.getInstance().setProperty( PROPERTY_LAST_JOB_MAGNIFICATION, Float.toString( lastJobMagnification ) );
  }

  private static Listener zoomListener = event -> {
    // get the trans zoom level whenever it's changed actively anywhere by the user...
    //
    Combo combo = (Combo) event.widget;
    String oldZoom = combo.getText();
    String newZoom = oldZoom.substring( 0, event.start ) + event.text + oldZoom.substring( event.end );

    if ( StringUtils.isEmpty( oldZoom ) || StringUtils.isEmpty( newZoom ) ) {
      // Initial set, not worth remembering the incorrect value of 100% (sigh)
      //
      return;
    }

    String zoomText = newZoom.replace( "%", "" );
    double magnification = Const.toDouble( zoomText, 100.0D ) / 100;
    ZoomLevel.getInstance().setLastTransMagnification( (float) magnification );
  };

  public static final void changeTransGraphZoomLevel() {
    Spoon spoon = Spoon.getInstance();
    if ( spoon == null ) {
      // Not running in spoon, don't care about zoom level...
      //
      return;
    }

    ZoomLevel.getInstance().saveProperties();

    float magnification = ZoomLevel.getInstance().getLastTransMagnification();

    correctActiveGraphZoomLevel(spoon.getActiveTransGraph(), magnification);
  }

  private static void correctActiveGraphZoomLevel( AbstractGraph graph, float magnification ) {
    if ( graph == null ) {
      // Nothing to see here, move along!
      //
      return;
    }

    // Find the toolbar, try to set the zoom level based on the last transformation magnification...
    //
    for ( Control control : graph.getChildren() ) {
      if ( control instanceof ToolBar ) {
        ToolBar toolBar = (ToolBar) control;
        // This is the toolbar at the top of the transformation...
        // Find the Combo with the zoom level
        //
        for ( ToolItem toolItem : toolBar.getItems() ) {
          if ( toolItem.getControl() instanceof Combo ) {
            toolItem.setWidth( 150 );

            Combo zoomLevelCombo = (Combo) toolItem.getControl();
            if ( zoomLevelCombo.getItemCount() < 10 ) {
              zoomLevelCombo.add( "  400%", 0 );
              zoomLevelCombo.add( "  500%", 0 );
              zoomLevelCombo.add( "  750%", 0 );
              zoomLevelCombo.add( " 1000%", 0 );
            }

            zoomLevelCombo.setText( Integer.toString( Math.round( magnification * 100.0F ) ) + "%" );

            // Now update the magnification used in TransGraph
            //
            zoomLevelCombo.removeListener( SWT.Verify, zoomListener );
            zoomLevelCombo.notifyListeners( SWT.Selection, new Event() );
            zoomLevelCombo.addListener( SWT.Verify, zoomListener );
            return;
          }
        }
      }
    }
  }


  public static final void changeJobGraphZoomLevel() {
    Spoon spoon = Spoon.getInstance();
    if ( spoon == null ) {
      // Not running in spoon, don't care about zoom level...
      //
      return;
    }

    ZoomLevel.getInstance().saveProperties();

    float magnification = ZoomLevel.getInstance().getLastJobMagnification();

    correctActiveGraphZoomLevel( spoon.getActiveJobGraph(), magnification );
  }


  /**
   * Gets lastTransMagnification
   *
   * @return value of lastTransMagnification
   */
  public float getLastTransMagnification() {
    return lastTransMagnification;
  }

  /**
   * @param lastTransMagnification The lastTransMagnification to set
   */
  public void setLastTransMagnification( float lastTransMagnification ) {
    this.lastTransMagnification = lastTransMagnification;
  }

  /**
   * Gets lastJobMagnification
   *
   * @return value of lastJobMagnification
   */
  public float getLastJobMagnification() {
    return lastJobMagnification;
  }

  /**
   * @param lastJobMagnification The lastJobMagnification to set
   */
  public void setLastJobMagnification( float lastJobMagnification ) {
    this.lastJobMagnification = lastJobMagnification;
  }
}
