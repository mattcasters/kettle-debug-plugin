package org.kettle.xp.util;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.pentaho.di.core.Const;
import org.pentaho.di.ui.core.PropsUI;
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
    lastTransMagnification = (float) Const.toDouble(PropsUI.getInstance().getProperty( PROPERTY_LAST_TRANS_MAGNIFICATION ), 1.0f);
    lastJobMagnification = (float) Const.toDouble(PropsUI.getInstance().getProperty( PROPERTY_LAST_JOB_MAGNIFICATION ), 1.0f);
  }

  public static ZoomLevel getInstance() {
    if (zoomLevel==null) {
      zoomLevel = new ZoomLevel();
    }

    return zoomLevel;
  }

  public void saveProperties() {
    PropsUI.getInstance().setProperty( PROPERTY_LAST_TRANS_MAGNIFICATION, Float.toString( lastTransMagnification ) );
    PropsUI.getInstance().setProperty( PROPERTY_LAST_JOB_MAGNIFICATION, Float.toString( lastJobMagnification) );
  }

  private static Listener transZoomListener = event -> {
    // get the trans zoom level whenever it's changed actively anywhere by the user...
    //
    Combo combo = (Combo) event.widget;
    String oldZoom = combo.getText();
    String newZoom = oldZoom.substring(0, event.start) + event.text + oldZoom.substring(event.end);

    if ( StringUtils.isEmpty(oldZoom) || StringUtils.isEmpty( newZoom )) {
      // Initial set, not worth remembering the incorrect value of 100% (sigh)
      //
      return;
    }

    String zoomText = newZoom.replace( "%", "" );
    double magnification =  Const.toDouble( zoomText, 100.0D ) / 100;
    ZoomLevel.getInstance().setLastTransMagnification( (float)magnification );

  };

  public static final void changeTransGraphZoomLevel() {
    Spoon spoon = Spoon.getInstance();
    if (spoon==null) {
      // Not running in spoon, don't care about zoom level...
      //
      return;
    }

    ZoomLevel.getInstance().saveProperties();

    float magnification = ZoomLevel.getInstance().getLastTransMagnification();

    TransGraph transGraph = spoon.getActiveTransGraph();
    if (transGraph==null) {
      // Nothing to see here, move along!
      //
      return;
    }

    // Find the toolbar, try to set the zoom level based on the last transformation magnification...
    //
    for (Control control : transGraph.getChildren()) {
      if (control instanceof ToolBar ) {
        ToolBar toolBar = (ToolBar) control;
        // This is the toolbar at the top of the transformation...
        // Find the Combo with the zoom level
        //
        for(ToolItem toolItem : toolBar.getItems()) {
          if (toolItem.getControl() instanceof Combo ) {
            Combo zoomLevelCombo = (Combo) toolItem.getControl();
            zoomLevelCombo.setText( Integer.toString(Math.round(magnification * 100.0F)) + "%");

            // Now update the magnification used in TransGraph
            //
            zoomLevelCombo.removeListener(SWT.Verify, transZoomListener );
            zoomLevelCombo.notifyListeners( SWT.Selection, new Event() );
            zoomLevelCombo.addListener(SWT.Verify, transZoomListener );
            return;
          }
        }
      }
    }
  }



  private static Listener jobZoomListener = event -> {
    // get the job zoom level whenever it's changed actively anywhere by the user...
    //
    Combo combo = (Combo) event.widget;
    String oldZoom = combo.getText();
    String newZoom = oldZoom.substring(0, event.start) + event.text + oldZoom.substring(event.end);

    if ( StringUtils.isEmpty(oldZoom) || StringUtils.isEmpty( newZoom )) {
      // Initial set, not worth remembering the incorrect value of 100% (sigh)
      //
      return;
    }

    String zoomText = newZoom.replace( "%", "" );
    double magnification =  Const.toDouble( zoomText, 100.0D ) / 100;
    ZoomLevel.getInstance().setLastJobMagnification( (float)magnification );
  };


  public static final void changeJobGraphZoomLevel() {
    Spoon spoon = Spoon.getInstance();
    if (spoon==null) {
      // Not running in spoon, don't care about zoom level...
      //
      return;
    }

    ZoomLevel.getInstance().saveProperties();

    float magnification = ZoomLevel.getInstance().getLastJobMagnification();

    JobGraph jobGraph = spoon.getActiveJobGraph();
    if (jobGraph==null) {
      // Nothing to see here, move along!
      //
      return;
    }

    // Find the toolbar, try to set the zoom level based on the last transformation magnification...
    //
    for (Control control : jobGraph.getChildren()) {
      if (control instanceof ToolBar ) {
        ToolBar toolBar = (ToolBar) control;
        // This is the toolbar at the top of the transformation...
        // Find the Combo with the zoom level
        //
        for(ToolItem toolItem : toolBar.getItems()) {
          if (toolItem.getControl() instanceof Combo) {
            Combo zoomLevelCombo = (Combo) toolItem.getControl();
            zoomLevelCombo.setText( Integer.toString(Math.round(magnification * 100.0F)) + "%");

            // Now update the magnification used in JobGraph
            //
            zoomLevelCombo.removeListener( SWT.Verify, jobZoomListener );
            zoomLevelCombo.notifyListeners( SWT.Selection, new Event() );
            zoomLevelCombo.addListener( SWT.Verify, jobZoomListener );

            return;
          }
        }
      }
    }
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
