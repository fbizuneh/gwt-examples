package com.gawkat.flashcard.client;

import com.google.gwt.gadgets.client.Gadget;
import com.google.gwt.gadgets.client.Gadget.ModulePrefs;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

@ModulePrefs(//
    title = "Math Flash Cards", //
    directory_title = "Math Flash Cards", //
    author = "Brandon Donnelson", //
    author_email = "branflake2267@gmail.com", //
    author_affiliation = "GoneVerical.org", //
    description = "Practice your times tables. See more on gwt-examples.googlecode.com", //
    thumbnail = "/images/FlashCard_sm.png", //
    title_url = "http://gwt-examples.googlecode.com", //
    author_location = "Arlington, WA, 98223", //
    screenshot = "/images/FlashCard_lg.png")
public class FlashCard extends Gadget<FlashCardPreferences> {

  /**
   * This is the entry point method.
   */
  public void init(FlashCardPreferences preferences) {
  	
  	//RootPanel.get().add(new HTML("debug: gadget start draw"));
  	
    VerticalPanel vp = new VerticalPanel();
    vp.setWidth("100%");
    RootPanel.get().add(vp);
    
    Layout layout = new Layout();
    vp.add(layout);
    layout.start();
    
    vp.setCellHorizontalAlignment(layout, HorizontalPanel.ALIGN_CENTER);
    
    //RootPanel.get().add(new HTML("debug: gadget draw end"));
  }
  
}
