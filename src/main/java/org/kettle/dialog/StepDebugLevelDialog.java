package org.kettle.dialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.kettle.StepDebugLevel;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.ui.core.PropsUI;
import org.pentaho.di.ui.core.gui.GUIResource;
import org.pentaho.di.ui.core.gui.WindowProperty;
import org.pentaho.di.ui.core.widget.ConditionEditor;
import org.pentaho.di.ui.trans.step.BaseStepDialog;

public class StepDebugLevelDialog extends Dialog {
  private static Class<?> PKG = StepDebugLevelDialog.class; // for i18n purposes, needed by Translator2!!

  private StepDebugLevel input;
  private StepDebugLevel debugLevel;
  private RowMetaInterface inputRowMeta;

  private Shell shell;

  // Connection properties
  //
  private CCombo wLogLevel;
  private Text wStartRow;
  private Text wEndRow;
  private ConditionEditor wCondition;
  
  private Control lastControl;

  private PropsUI props;

  private int middle;
  private int margin;

  private boolean ok;

  public StepDebugLevelDialog( Shell par, StepDebugLevel debugLevel, RowMetaInterface inputRowMeta) {
    super( par, SWT.NONE );
    this.input = debugLevel;
    this.inputRowMeta = inputRowMeta;
    props = PropsUI.getInstance();
    ok = false;

    this.debugLevel = input.clone();

  }

  public boolean open() {
    Shell parent = getParent();
    shell = new Shell( parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MAX | SWT.MIN );
    props.setLook( shell );
    shell.setImage( GUIResource.getInstance().getImageSlave() );

    middle = props.getMiddlePct();
    margin = Const.MARGIN + 2;

    FormLayout formLayout = new FormLayout();
    formLayout.marginWidth = Const.FORM_MARGIN;
    formLayout.marginHeight = Const.FORM_MARGIN;

    shell.setText( "Step debug Level" );
    shell.setLayout( formLayout );

    // The name
    Label wlName = new Label( shell, SWT.RIGHT );
    props.setLook( wlName );
    wlName.setText( "Log level to set " );
    FormData fdlName = new FormData();
    fdlName.top = new FormAttachment( 0, margin );
    fdlName.left = new FormAttachment( 0, 0 ); // First one in the left top corner
    fdlName.right = new FormAttachment( middle, -margin );
    wlName.setLayoutData( fdlName );
    wLogLevel = new CCombo( shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    wLogLevel.setItems( LogLevel.getLogLevelDescriptions());
    props.setLook( wLogLevel );
    FormData fdName = new FormData();
    fdName.top = new FormAttachment( wlName, 0, SWT.CENTER );
    fdName.left = new FormAttachment( middle, 0 ); // To the right of the label
    fdName.right = new FormAttachment( 100, 0 );
    wLogLevel.setLayoutData( fdName );
    lastControl = wLogLevel;

    // Start row option
    Label wlStartRow = new Label( shell, SWT.RIGHT );
    props.setLook( wlStartRow );
    wlStartRow.setText( "Start row ");
    FormData fdlStartRow = new FormData();
    fdlStartRow.top = new FormAttachment( lastControl, margin );
    fdlStartRow.left = new FormAttachment( 0, 0 ); // First one in the left top corner
    fdlStartRow.right = new FormAttachment( middle, -margin );
    wlStartRow.setLayoutData( fdlStartRow );
    wStartRow = new Text( shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    props.setLook( wStartRow );
    FormData fdStartRow = new FormData();
    fdStartRow.top = new FormAttachment( wlStartRow, 0, SWT.CENTER );
    fdStartRow.left = new FormAttachment( middle, 0 ); // To the right of the label
    fdStartRow.right = new FormAttachment( 100, 0 );
    wStartRow.setLayoutData( fdStartRow );
    lastControl = wStartRow;

    // End row
    Label wlEndRow = new Label( shell, SWT.RIGHT );
    props.setLook( wlEndRow );
    wlEndRow.setText( "End row " );
    FormData fdlEndRow = new FormData();
    fdlEndRow.top = new FormAttachment( lastControl, margin );
    fdlEndRow.left = new FormAttachment( 0, 0 ); // First one in the left top corner
    fdlEndRow.right = new FormAttachment( middle, -margin );
    wlEndRow.setLayoutData( fdlEndRow );
    wEndRow = new Text( shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    props.setLook( wEndRow );
    FormData fdEndRow = new FormData();
    fdEndRow.top = new FormAttachment( wlEndRow, 0, SWT.CENTER );
    fdEndRow.left = new FormAttachment( middle, 0 ); // To the right of the label
    fdEndRow.right = new FormAttachment( 100, 0 );
    wEndRow.setLayoutData( fdEndRow );
    lastControl = wEndRow;

    // Buttons
    Button wOK = new Button( shell, SWT.PUSH );
    wOK.setText( BaseMessages.getString( PKG, "System.Button.OK" ) );
    wOK.addListener( SWT.Selection, e -> ok() );

    Button wCancel = new Button( shell, SWT.PUSH );
    wCancel.setText( BaseMessages.getString( PKG, "System.Button.Cancel" ) );
    wCancel.addListener( SWT.Selection, e -> cancel() );

    Button[] buttons = new Button[] { wOK, wCancel };
    BaseStepDialog.positionBottomButtons( shell, buttons, margin, null );

    SelectionAdapter selAdapter = new SelectionAdapter() {
      public void widgetDefaultSelected( SelectionEvent e ) {
        ok();
      }
    };

    wLogLevel.addSelectionListener( selAdapter );
    wStartRow.addSelectionListener( selAdapter );
    wEndRow.addSelectionListener( selAdapter );

    // Condition
    Label wlCondition = new Label( shell, SWT.RIGHT);
    wlCondition.setText( "Condition : " );
    props.setLook( wlCondition );
    FormData fdlCondition = new FormData();
    fdlCondition.top = new FormAttachment( lastControl, margin );
    fdlCondition.left = new FormAttachment( 0, 0 );
    fdlCondition.right = new FormAttachment( middle, -margin );
    wlCondition.setLayoutData( fdlCondition );
    wCondition = new ConditionEditor( shell, SWT.NONE, debugLevel.getCondition(), inputRowMeta);
    props.setLook( wCondition );
    FormData fdCondition = new FormData();
    fdCondition.top = new FormAttachment( lastControl, margin );
    fdCondition.left = new FormAttachment( middle, 0 );
    fdCondition.right = new FormAttachment( 100, 0 );
    fdCondition.bottom= new FormAttachment( wOK, 0 );
    wCondition.setLayoutData( fdCondition );
    lastControl = wCondition;

    // Detect X or ALT-F4 or something that kills this window...
    shell.addShellListener( new ShellAdapter() {
      public void shellClosed( ShellEvent e ) {
        cancel();
      }
    } );

    getData();

    BaseStepDialog.setSize( shell );

    shell.open();
    Display display = parent.getDisplay();
    while ( !shell.isDisposed() ) {
      if ( !display.readAndDispatch() ) {
        display.sleep();
      }
    }
    return ok;
  }

  public void dispose() {
    props.setScreen( new WindowProperty( shell ) );
    shell.dispose();
  }

  public void getData() {
    wLogLevel.setText( debugLevel.getLogLevel().getDescription() );
    wStartRow.setText( debugLevel.getStartRow() < 0 ? "" : Integer.toString( debugLevel.getStartRow() ) );
    wEndRow.setText( debugLevel.getEndRow() < 0 ? "" : Integer.toString( debugLevel.getEndRow() ) );

    wLogLevel.setFocus();
  }

  private void cancel() {
    ok = false;
    dispose();
  }

  public void ok() {
    getInfo( input );
    ok = true;
    dispose();
  }

  // Get dialog info in securityService
  private void getInfo( StepDebugLevel level ) {
    int index = Const.indexOfString( wLogLevel.getText(), LogLevel.getLogLevelDescriptions() );
    level.setLogLevel( LogLevel.values()[index] );
    level.setStartRow( Const.toInt( wStartRow.getText(), -1) );
    level.setEndRow( Const.toInt( wEndRow.getText(), -1) );
    level.setCondition( debugLevel.getCondition() );
  }

}
