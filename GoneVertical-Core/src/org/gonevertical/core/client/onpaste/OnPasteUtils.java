package org.gonevertical.core.client.onpaste;

import com.google.gwt.user.client.Event;

public class OnPasteUtils {

  /**
   * 
   * https://groups.google.com/d/topic/google-web-toolkit/CaNSdwfSK-A/discussion
   * 
   * @param event
   * @return
   */
  public static native String getPastedText(Event event)
  /*-{

      var text = "";

      if (event.clipboardData) // WebKit/Chrome/Safari
      {
          try
          {
              text = event.clipboardData.getData("Text");
              return text;
          }
          catch (e)
          {
              // Hmm, that didn't work.
          }
      }

      if ($wnd.clipboardData) // IE
      {
          try
          {
              text = $wnd.clipboardData.getData("Text");
              return text;
          }
          catch (e)
          {
              // Hmm, that didn't work.
          }
      }

      // Ok, last chance.  Firefox refuses to make this easy:
      // https://bugzilla.mozilla.org/show_bug.cgi?id=407983
      // And this ridiculous approach is unlikely to work.
      // But try it anyway; we have nothing to lose.
      // We can ignore Opera; it doesn't even fire ONPASTE.

      try
      {
          if (netscape.security.PrivilegeManager.enablePrivilege)
          {

netscape.security.PrivilegeManager.enablePrivilege("UniversalXPConnect");
          }
          else
          {
              return "";
          }
      }
      catch (ex)
      {
          return "";
      }

      var clip = Components.classes["@mozilla.org/widget/clipboard;
1"].getService(Components.interfaces.nsIClipboard);
      if (!clip) return "";

      var trans = Components.classes["@mozilla.org/widget/
transferable;
1"].createInstance(Components.interfaces.nsITransferable);
      if (!trans) return "";

      trans.addDataFlavor("text/unicode");
      clip.getData(trans, clip.kGlobalClipboard);

      var str = new Object();
      var strLength = new Object();

      trans.getTransferData("text/unicode", str, strLength);
      if (str) str =
str.value.QueryInterface(Components.interfaces.nsISupportsString);
      if (str) text = str.data.substring(0, strLength.value / 2);

      return text;

  }-*/;

}
