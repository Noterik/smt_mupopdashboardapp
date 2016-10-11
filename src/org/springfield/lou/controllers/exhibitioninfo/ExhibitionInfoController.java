package org.springfield.lou.controllers.exhibitioninfo;


import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONObject;
import org.springfield.fs.FSList;
import org.springfield.fs.FSListManager;
import org.springfield.fs.Fs;
import org.springfield.fs.FsNode;
import org.springfield.lou.controllers.Html5Controller;
import org.springfield.lou.controllers.dashboard.DashboardController;
import org.springfield.lou.controllers.room.RoomController;
import org.springfield.lou.controllers.roominfo.RoomInfoController;
import org.springfield.lou.controllers.roomselector.RoomSelectorController;
import org.springfield.lou.controllers.station.StationController;
import org.springfield.lou.model.ModelEvent;
import org.springfield.lou.screen.Screen;

public class ExhibitionInfoController extends Html5Controller {
	
	String newstationpath;
	FsNode roomnode; 
	
	
	/**
	 * One of the core controllers shows the layout of a room with access to many
	 * elements of the dashboard.
	 */
	public ExhibitionInfoController() {
	}
	
	/**
	 * moment this controller gets attached to target screen on given id
	 * 
	 * @see org.springfield.lou.controllers.Html5Controller#attach(java.lang.String)
	 */
	public void attach(String sel) {
		selector = sel; // set the id for later use.
		fillPage();
		model.onPropertyUpdate("/shared/mupop/hidrequest","onHidRequest",this);
	}
	
	public void onHidRequest(ModelEvent e) {
		System.out.println("HID REQUEST");
		FsNode node = e.getTargetFsNode();
		System.out.println("CODE="+node.asXML());
		String code = node.getProperty("hidrequest");
		String hid = "danielchrome";
		// use code to reflect back to only that screen
		System.out.println("SET HID RESPONSE="+code+" "+hid);
		model.setProperty("/shared/mupop/hidresponse"+code,hid);
	}
	
	/**
	 * fill our space on our screen
	 */
	private void fillPage() {
		JSONObject data = new JSONObject(); // create json object for the clients render call
		data.put("username",model.getProperty("@username")); // add username so we can display it
		data.put("exhibitionid",model.getProperty("@exhibitionid")); // add exhibition idea for use in forms
		screen.get(selector).render(data); // now we have all data give it to client and render using mustache
 		screen.get("#exhibitioninfo_cancelbutton").on("mouseup","onCancelButton", this);
 		screen.get("#exhibitioninfo_updatebutton").on("mouseup","onUpdateButton", this);
 		
	}
	
    public void onCancelButton(Screen s,JSONObject data) {
       	screen.get("#content").append("div","room",new RoomController()); // if user wanted a old exhibition lets open the default room for it
		screen.get(selector).remove();
    }
    
    public void onUpdateButton(Screen s,JSONObject data) {
       	screen.get("#content").append("div","room",new RoomController()); // if user wanted a old exhibition lets open the default room for it
		screen.get(selector).remove();
    }
	
 	 
}