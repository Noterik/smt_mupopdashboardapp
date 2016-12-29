package org.springfield.lou.controllers.exhibitioninfo;


import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
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
import org.springfield.lou.model.ModelEvent;
import org.springfield.lou.screen.Screen;

public class ExhibitionInfoController extends Html5Controller {
	
	String newstationpath;
	FsNode roomnode; 
	String code=null;
	String hid;
	String feedback=null;
	
	
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
		code = node.getProperty("hidrequest");

		// use code to reflect back to only that screen
		fillPage();
		
		
	}
	
    public void onPairButton(Screen s,JSONObject data) {
    	System.out.println("PAIR BUTTON="+data.toJSONString());
    	
    	String givencode = (String)data.get("exhibitioninfo_code");
    	hid = (String)data.get("exhibitioninfo_hid");
    	if (givencode.equals(code)) {
    		if (model.getNode("/domain/mupop/config/hids/hid/"+hid)!=null) {
        		feedback = "Hardware name already in use";
        		fillPage();
        		return;
    		}
    		
    		
    		FsNode hidnode = new FsNode("hid",hid);
    		hidnode.setProperty("username","");
    		hidnode.setProperty("exhibitionid","");
    		hidnode.setProperty("stationid","");
    		boolean result = model.putNode("/domain/mupop/config/hids/",hidnode);
    		if (result) {
    			model.setProperty("/shared['mupop']/hidresponse"+code,hid);
    			code = null;
        		feedback = "Hardware pairing done : "+hid+" now available for mapping";
    		} else {
    			System.out.println("Could not insert node "+hidnode.asXML());
    		}	
    	} else {
    		feedback = "Wrong pairing code given";
    	}
		fillPage();
    }
	
	
	
	/**
	 * fill our space on our screen
	 */
	private void fillPage() {
		
		JSONObject data = new JSONObject(); // create json object for the clients render call
		data.put("username",model.getProperty("@username")); // add username so we can display it
		data.put("exhibitionid",model.getProperty("@exhibitionid")); // add exhibition idea for use in forms
		if (code!=null) data.put("code", code);
		if (feedback!=null) data.put("feedback", feedback);
		FSList list = model.getList("/domain/mupop/config/hids/hid");
		if (list!=null) {
			JSONObject paired = list.toJSONObject("en","stationname");
			data.put("paired",paired);
		}
		screen.get(selector).render(data); // now we have all data give it to client and render using mustache	
 		screen.get("#exhibitioninfo_donebutton").on("mouseup","onDoneButton", this);
 		screen.get("#exhibitioninfo_pairbutton").on("mouseup","exhibitioninfo_code,exhibitioninfo_hid","onPairButton", this);
		screen.get("#exhibitioninfo_deletehidbutton").on("mouseup","exhibitioninfo_hids_select","onHidDeleteButton", this);
		screen.get("#exhibitioninfo_jumpersubmit").on("mouseup","exhibitioninfo_jumper","onJumperSubmit", this);
	}
	
	public void onJumperSubmit(Screen s,JSONObject data) {
		System.out.println("Jumper Submit="+data.toJSONString());
		feedback = "Jumper set";
		fillPage();
	}
	
	
	public void onHidDeleteButton(Screen s,JSONObject data) {
		JSONArray hids = (JSONArray)data.get("exhibitioninfo_hids_select");
		String hid = (String)hids.get(0);
		if (hid!=null && !hid.equals("")) {
    		boolean result = Fs.deleteNode("/domain/mupop/config/hids/hid/"+hid);
    		fillPage();
		}
	}
	
    public void onDoneButton(Screen s,JSONObject data) {
       	screen.get("#content").append("div","room",new RoomController()); // if user wanted a old exhibition lets open the default room for it
		screen.get(selector).remove();
    }
    
	
 	 
}
