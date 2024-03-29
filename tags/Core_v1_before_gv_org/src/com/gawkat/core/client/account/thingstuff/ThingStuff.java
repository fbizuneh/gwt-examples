package com.gawkat.core.client.account.thingstuff;

import com.gawkat.core.client.ClientPersistence;
import com.gawkat.core.client.Row;
import com.gawkat.core.client.account.thing.ThingData;
import com.gawkat.core.client.account.thingstufftype.ThingStuffTypeData;
import com.gawkat.core.client.account.thingstufftype.ThingStuffTypesData;
import com.gawkat.core.client.global.DeleteDialog;
import com.gawkat.core.client.global.EventManager;
import com.gawkat.core.client.global.Global_ListBox;
import com.gawkat.core.client.rpc.RpcCore;
import com.gawkat.core.client.rpc.RpcCoreServiceAsync;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ThingStuff extends Composite implements ClickHandler, ChangeHandler, MouseOverHandler, MouseOutHandler {
  	
  private ClientPersistence cp = null;
  
  private RpcCoreServiceAsync rpc;
  
  private int changeEvent = 0;
  
  private Row pWidget = new Row();

  private DeleteDialog wdelete = new DeleteDialog();
  
  private FlowPanel pId = new FlowPanel();
  
  private ListBox lbTypes = new ListBox();
  
  // inputs container
  private FlowPanel pInput = new FlowPanel();
  
  private FlowPanel pThingStuffCount = new FlowPanel();
  
  private TextBox tbValue = new TextBox();
  private CheckBox cbValue = new CheckBox();
  private TextArea taValue = new TextArea();

  // owner
  private ThingData thingData = new ThingData();
  
  private ThingStuffData thingStuffData = null;
  
  // delete button
  private PushButton bDelete = new PushButton("X");

  // types choices - what kind of types, questions, attributes to choose from and then give them a value
  private ThingStuffTypesData thingStuffTypesData = null;

  // what is the rank/order/index of this widget in comparison to the to the others in the list
	private int index = 0;
	
	private int widgetType = 0;

	/**
	 * consturctor - init widget
	 * 
	 * @param cp
	 * @param widgetType - what type of widget, thingstuff or thingabout stuff
	 */
  public ThingStuff(ClientPersistence cp, int widgetType) {
    this.cp = cp;
    this.widgetType = widgetType;
    
    // inputs of stuff, and then add another group of stuffs
    VerticalPanel vpInput = new VerticalPanel();
    vpInput.add(pInput);
    
    HorizontalPanel hpButtons = new HorizontalPanel();
    hpButtons.add(bDelete);
    
    pWidget.add(pId);
    pWidget.add(lbTypes);
    pWidget.add(vpInput);
    pWidget.add(hpButtons);
    pWidget.add(pThingStuffCount);
    
    initWidget(pWidget);
    
    pWidget.addMouseOverHandler(this);
    pWidget.addMouseOutHandler(this);
    bDelete.addClickHandler(this);
    lbTypes.addChangeHandler(this);
    
    rpc = RpcCore.initRpc();
    
    //pWidget.addStyleName("test2");
  }
  
  /**
   * set up the stuff data
   * 
   * @param thingData - owner
   * @param thingStuffTypesData - choices
   * @param thingStuffData - fill in saved data
   */
  public void setData(
  		int index,
  		ThingData thingData, 
  		ThingStuffTypesData thingStuffTypesData, 
  		ThingStuffData thingStuffData, int count) {
  	this.index = index;
    this.thingData = thingData;
    this.thingStuffData = thingStuffData;
    this.thingStuffTypesData = thingStuffTypesData;
    
    // use an empty object to store data in - TODO - maybe not do this
    if (thingStuffData == null) {
    	thingStuffData = new ThingStuffData();
    }
    
    drawId();
    
    // draw choices
    drawListBoxTypes();
    
    // draw input type - textbox, checkbox...
    drawInput();
    
    drawStuffCount();
  }
  
  private void drawStuffCount() {
  	pThingStuffCount.clear();
  	String s = "&nbsp;";
    if (widgetType == ThingStuffs.WIDGETTYPE_THINGSTUFF) {
    	if (thingStuffData.getThingStuffsAbout() != null && 	thingStuffData.getThingStuffsAbout().getThingStuffData() != null) {
    		s = Long.toString(thingStuffData.getThingStuffsAbout().getThingStuffData().length);
    	}
    }
    pThingStuffCount.add(new HTML(s));
  }

	private void drawId() {
  	pId.clear();
  	long id = 0;
  	if (thingStuffData.getStuffId() > 0 && widgetType == ThingStuffs.WIDGETTYPE_THINGSTUFF) {
  		id = thingStuffData.getStuffId();
  	} else if (thingStuffData.getStuffAboutId() > 0 && widgetType == ThingStuffs.WIDGETTYPE_THINGSTUFFABOUT) {
  		id = thingStuffData.getStuffAboutId();
  	}
  	
  	String s = "";
  	if (id != 0) {
  		s = Long.toString(id);
  	} else {
  		s = "&nbsp;&nbsp;";
  	}
  	
  	HTML h = new HTML(s);
  	pId.add(h);
  }

	/**
   * draw choices of types
   */
  private void drawListBoxTypes() {
    lbTypes.clear();
    
    if (thingStuffTypesData == null) {
      return;
    }
    
    lbTypes.addItem("Select", "0");
    
    for (int i=0; i < thingStuffTypesData.thingStuffTypeData.length; i++) {
      String item = thingStuffTypesData.thingStuffTypeData[i].getName();
      String value = Long.toString(thingStuffTypesData.thingStuffTypeData[i].getStuffTypeId());
      lbTypes.addItem(item, value);
      
      //debug
      //System.out.println("item: " + item + " value: " + value);
    }
        
    long sel = thingStuffData.getThingStuffTypeId();
    Global_ListBox.setSelected(lbTypes, (int) sel);
  }
  
  private void drawInput() {
    pInput.clear();
      
    long typeId = getDataTypeId();
    if (typeId == ThingStuffTypeData.VT_STRING) {
      drawInput(typeId, thingStuffData.getValue());
      
    } else if (typeId == ThingStuffTypeData.VT_BOOLEAN) {
      drawInput(thingStuffData.getValueBol());
      
    } else if (typeId == ThingStuffTypeData.VT_DOUBLE) {
      drawInput(thingStuffData.getValueDouble());
      
    } else if (typeId == ThingStuffTypeData.VT_INT) {
      drawInput(thingStuffData.getValueLong());
      
    } else if (typeId == ThingStuffTypeData.VT_STRING_LARGE) {
      drawInput(typeId, thingStuffData.getValue());
      
    } else if (typeId == ThingStuffTypeData.VT_STRING_CASE) {
      drawInput(typeId, thingStuffData.getValue());
      
    } else if (typeId == ThingStuffTypeData.VT_STRING_LARGE_CASE) {
      drawInput(typeId, thingStuffData.getValue());
      
    } else if (typeId == ThingStuffTypeData.VT_HTML) {
      drawInput(typeId, thingStuffData.getValue());
      
    } else if (typeId == ThingStuffTypeData.VT_URL) {
      drawInput(typeId, thingStuffData.getValue());
      
    } else if (typeId == ThingStuffTypeData.VT_EMAIL) {
      drawInput(typeId, thingStuffData.getValue());
      
    } else if (typeId == ThingStuffTypeData.VT_PHONE) {
      drawInput(typeId, thingStuffData.getValue());
      
    } else if (typeId == ThingStuffTypeData.VT_LINK) {
    	drawInput(thingStuffData.getValueLong());
      
    } else {
      drawInputBlank();
    }
    
  }
  
  private void drawInputBlank() {
    pInput.add(new HTML("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"));
  }
  
  private void drawInput(long typeId, String value) {
    Widget w = null;
    if (typeId == ThingStuffTypeData.VT_STRING) {
      w = tbValue;
      tbValue.setText(value);
    } else if (typeId == ThingStuffTypeData.VT_STRING_LARGE) {
      w = taValue;
      taValue.setText(value);
    } else if (typeId == ThingStuffTypeData.VT_STRING_CASE) {
      w = tbValue;
      tbValue.setText(value);
    } else if (typeId == ThingStuffTypeData.VT_STRING_LARGE_CASE) {
      w = taValue;
      taValue.setText(value);
    } else if (typeId == ThingStuffTypeData.VT_HTML) {
      w = taValue;
      taValue.setText(value);
    } else if (typeId == ThingStuffTypeData.VT_URL) {
      w = tbValue;
      tbValue.setText(value);
    } else if (typeId == ThingStuffTypeData.VT_EMAIL) {
      w = tbValue;
      tbValue.setText(value);
    } else if (typeId == ThingStuffTypeData.VT_PHONE) {
      w = tbValue;
      tbValue.setText(value);
      
    } else if (typeId == ThingStuffTypeData.VT_LINK) {
      w = tbValue;
      tbValue.setText(value);
    }
    
    if (w != null) {
      pInput.add(w);
    }
    
  }
  
  public ThingStuffData getData() {
    
    String value = null;
    Boolean valueBol = false;
    Double valueDouble = null; 
    Long valueLong = null;
    
    long typeId = getDataTypeId();
    if (typeId == ThingStuffTypeData.VT_STRING) {
      value = getTextBox_String();
      valueBol = false;
      valueDouble = null;
      valueLong = null;
    } else if (typeId == ThingStuffTypeData.VT_BOOLEAN) {
      value = null;
      valueBol = cbValue.getValue();
      valueDouble = null;
      valueLong = null;
    } else if (typeId == ThingStuffTypeData.VT_DOUBLE) {
      value = null;
      valueBol = false;
      valueDouble = getTextBox_Double();
      valueLong = null;
    } else if (typeId == ThingStuffTypeData.VT_INT) {
      value = null;
      valueBol = false;
      valueDouble = null;
      valueLong = getTextBox_Long();
    } else if (typeId == ThingStuffTypeData.VT_STRING_LARGE) {
      value = getTextArea_String();
      valueBol = false;
      valueDouble = null;
      valueLong = null;
    } else if (typeId == ThingStuffTypeData.VT_STRING_CASE) {
      value = getTextBox_String();
      valueBol = false;
      valueDouble = null;
      valueLong = null;
    } else if (typeId == ThingStuffTypeData.VT_STRING_LARGE_CASE) {
      value = getTextArea_String();
      valueBol = false;
      valueDouble = null;
      valueLong = null;
    } else if (typeId == ThingStuffTypeData.VT_HTML) {
      value = getTextArea_String();
      valueBol = false;
      valueDouble = null;
      valueLong = null;
    } else if (typeId == ThingStuffTypeData.VT_URL) {
      value = getTextBox_String();
      valueBol = false;
      valueDouble = null;
      valueLong = null;
    } else if (typeId == ThingStuffTypeData.VT_EMAIL) {
      value = getTextBox_String();
      valueBol = false;
      valueDouble = null;
      valueLong = null;
    } else if (typeId == ThingStuffTypeData.VT_PHONE) {
      value = getTextBox_String();
      valueBol = false;
      valueDouble = null;
      valueLong = null;
    } else if (typeId == ThingStuffTypeData.VT_LINK) {
      value = null;
      valueBol = false;
      valueDouble = null;
      valueLong = getTextBox_Long();
    } else {
      value = getTextBox_String();
      valueBol = false;
      valueDouble = null;
      valueLong = null;
    }
    
    int stuffTypeId = Global_ListBox.getSelectedValue(lbTypes);
    thingStuffData.setThingStuffTypeId(stuffTypeId);
    thingStuffData.setValue(value);
    thingStuffData.setValue(valueBol);
    
    // TODO - doubleVal still comes through
    // not able to use the setters for this b/c it won't carry the variables values through the cast 2/7/2010
    //thingStuffData.setValue(valueDouble);
    //thingStuffData.setValue(valueInt);
    thingStuffData.setValue(getTextBox_Double());
    thingStuffData.setValue(getTextBox_Long());
    
    thingStuffData.setThingId(thingData.getThingId());
    
    // TODO no need, already sets on mousing
    // multi dem format for stuff having stuff
    //thingStuffData.setThingStuffsAbout(thingStuffData.getThingStuffsAbout());
    
    return thingStuffData;
  }

  private String getTextBox_String() {
  	String s = null;
  	if (tbValue.getText().trim().length() > 0) {
  		s = tbValue.getText().trim();
  	}
    return s;
  }
  
  private String getTextArea_String() {
  	String s = null;
  	if (tbValue.getText().trim().length() > 0) {
  		s = tbValue.getText().trim();
  	}
    return s;
  }
  
  private Long getTextBox_Long() {
    String s = tbValue.getText().trim();
    Long l = null;
    try {
      l = Long.parseLong(s);
    } catch (NumberFormatException e) {
      l = null;
    }
    return l;
  }
  
  private Double getTextBox_Double() {
    String s = tbValue.getText().trim();
    Double d = null;
    try {
      d = Double.parseDouble(s);
    } catch (NumberFormatException e) {
      d = null;
    }
    return d;
  }

  private void drawInput(Boolean valueBol) {
  	if (valueBol == null) {
  		valueBol = new Boolean(false);
  	}
    pInput.add(cbValue);
    cbValue.setValue(valueBol);
  }

  private void drawInput(Double valueDouble) {
  	if (valueDouble == null) {
  		valueDouble = new Double(0.0);
  	}
    pInput.add(tbValue);
    tbValue.setText(Double.toString(valueDouble));
  }

  private void drawInput(Long valueLong) {
  	if (valueLong == null) {
  		valueLong = new Long(0);
  	}
    pInput.add(tbValue);
    tbValue.setText(Long.toString(valueLong));
  }

  private int getDataTypeId() {
  	if (lbTypes == null) {
  		return -1;
  	}
    int thingStuffId = Global_ListBox.getSelectedValue(lbTypes);
    if (thingStuffId == -1) {
    	return -1;
    }
    ThingStuffTypeData type = thingStuffTypesData.getStuffTypeData(thingStuffId);
    int typeId = 0;
    if (type == null) {
      typeId = 0;
    } else {
      typeId = type.getValueTypeId();
    }
    return typeId;
  }
  
  private void changeType() {
    int dataTypeId = getDataTypeId();
    drawInput();
    resizeInput(dataTypeId);
    fireChange(EventManager.THINGSTUFF_TYPECHANGE);
  }
  
  public Row getRow() {
    return pWidget;
  }
  
  private void resizeInput(int dataTypeId) {

    int wt = tbValue.getOffsetWidth();
    int wtc = cbValue.getOffsetWidth();
    int wta = taValue.getOffsetWidth();
    int width = 0;
    if (dataTypeId == ThingStuffTypeData.VT_STRING) {
      width = wt;
    } else if (dataTypeId == ThingStuffTypeData.VT_BOOLEAN) {
      width = wtc;
    } else if (dataTypeId == ThingStuffTypeData.VT_DOUBLE) {
      width = wt;
    } else if (dataTypeId == ThingStuffTypeData.VT_INT) {
      width = wt;
    } else if (dataTypeId == ThingStuffTypeData.VT_STRING_LARGE) {
      width = wta;
    } else if (dataTypeId == ThingStuffTypeData.VT_STRING_CASE) {
      width = wt;
    } else if (dataTypeId == ThingStuffTypeData.VT_STRING_LARGE_CASE) {
      width = wta;
    } else if (dataTypeId == ThingStuffTypeData.VT_HTML) {
      width = wta;
    } else if (dataTypeId == ThingStuffTypeData.VT_URL) {
      width = wt;
    } else if (dataTypeId == ThingStuffTypeData.VT_EMAIL) {
      width = wt;
    } else if (dataTypeId == ThingStuffTypeData.VT_PHONE) {
      width = wt;
    } else if (dataTypeId == ThingStuffTypeData.VT_LINK) {
      width = wt;
    } else {
      width = wt;
    } 
    
    // adjust input sizes too
    if (width > 50) {
    	pInput.setWidth(width + "px");
    	tbValue.setWidth(width + "px");
    	taValue.setWidth(width + "px");
    }
    
    //System.out.println("resize: " + width);
  }
  
  private void delete() {
    wdelete.center();
    wdelete.addChangeHandler(new ChangeHandler() {
      public void onChange(ChangeEvent event) {
        
      	DeleteDialog dd = (DeleteDialog) event.getSource();
        
        int changeEvent = dd.getChangeEvent();
        
        if (changeEvent == EventManager.DELETE_YES && 
        		thingStuffData.getStuffId() > 0 && 
        		widgetType == ThingStuffs.WIDGETTYPE_THINGSTUFF) {
        
        	deleteRpcThingStuff(thingStuffData.getStuffId());
        		
        } else if (changeEvent == EventManager.DELETE_YES && 
        		thingStuffData.getStuffAboutId() > 0 && 
        		widgetType == ThingStuffs.WIDGETTYPE_THINGSTUFFABOUT) {
        	
        	deleteRpcThingStuffAbout(thingStuffData.getStuffAboutId());
        	
        } else {
          deleteIt(true);
        }
      }
    });
  }
  
  private void deleteIt(boolean b) {
    if (b == true) {
      this.removeFromParent();
    } else {
      Window.alert("Wasn't able to delete it. Please try again?");
    }
    
    // redraw so the array is correct in list
    fireChange(EventManager.THINGSTUFF_REDRAW);
  }
  
  public int getChangeEvent() {
    return changeEvent;
  }
  
  private void fireChange(int changeEvent) {
    this.changeEvent = changeEvent;
    NativeEvent nativeEvent = Document.get().createChangeEvent();
    ChangeEvent.fireNativeEvent(nativeEvent, this);
  }
  
  public HandlerRegistration addChangeHandler(ChangeHandler handler) {
    return addDomHandler(handler, ChangeEvent.getType());
  }

  public void onClick(ClickEvent event) {
    Widget sender = (Widget) event.getSource();
    
    if (sender == bDelete) {
      delete();
    }
  }

  public void onChange(ChangeEvent event) {
    Widget sender = (Widget) event.getSource();
    if (sender == lbTypes) {
      changeType();
    }
  }

  public void onMouseOver(MouseOverEvent event) {
  	Widget sender = (Widget) event.getSource();
  	
  	if (sender == pWidget) {
  		fireChange(EventManager.THINGSTUFFABOUT_MOUSEOVER);
  	}
  }

  public void onMouseOut(MouseOutEvent event) {
  	Widget sender = (Widget) event.getSource();
	  
  	if (sender == pWidget) {
  		fireChange(EventManager.THINGSTUFFABOUT_HIDE);
  	}
  }
  
	public ThingStuffData getThingStuffData() {
	  return thingStuffData;
  }

	public int getIndex() {
		return index;
	}

	public void setAboutStuff(ThingStuffData[] tsd) {
		
		ThingStuffsData tssd = new ThingStuffsData();
		tssd.thingStuffData = tsd;
		
		thingStuffData.setThingStuffsAbout(tssd);
  }
	
  public void setWidgetType(int widgetType) {
  	this.widgetType = widgetType;
  }

  private void deleteRpcThingStuff(long thingStuffId) {
    
    rpc.deleteThingStuffData(cp.getAccessToken(), thingStuffId, new AsyncCallback<Boolean>() {
      public void onSuccess(Boolean b) {
        deleteIt(b);
      }
      public void onFailure(Throwable caught) {
      	cp.setRpcFailure(caught);
      }
    });
  }
  
  private void deleteRpcThingStuffAbout(long thingStuffAboutId) {
    
  	rpc.deleteThingStuffAboutData(cp.getAccessToken(), thingStuffAboutId, new AsyncCallback<Boolean>() {
			public void onSuccess(Boolean result) {
				deleteIt(result);
			}
			public void onFailure(Throwable caught) {
				cp.setRpcFailure(caught);
			}
		});
  }
  

  
}
