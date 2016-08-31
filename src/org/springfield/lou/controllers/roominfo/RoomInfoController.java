package org.springfield.lou.controllers.roominfo;


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
import org.springfield.lou.model.ModelEvent;
import org.springfield.lou.screen.Screen;

public class RoomInfoController extends Html5Controller {
	
	String username;
	String exhibitionid;
	String fields = "roominfo_exhibition,roominfo_location,roominfo_room,roominfo_timeframe,roominfo_building,roominfo_description,roominfo_gpslan,roominfo_gpslat";

	public RoomInfoController() {
	}
	
	public void attach(String sel) {
		selector = sel;
		fillPage();
	}
	
	
	private void fillPage() {
		JSONObject data = new JSONObject();
		username = model.getProperty("/screen/username");
		data.put("username",username);
		exhibitionid = model.getProperty("/screen/exhibitionid");
		if (exhibitionid.equals("newexhibition")) {
			data.put("newexhibition","true");
		} else {
			data.put("exhibition","exhibition");
		}
		screen.get(selector).render(data);
 		screen.get("#roominfo_createbutton").on("mouseup",fields,"onCreateButton", this);
 		screen.get("#roominfo_cancelbutton").on("mouseup",fields,"onCancelButton", this);
	}
	
    public void onCancelButton(Screen s,JSONObject data) {
		screen.get(selector).remove();
		screen.get("#content").append("div","dashboard",new DashboardController());
    }
	
    public void onCreateButton(Screen s,JSONObject data) {
		if (exhibitionid.equals("newexhibition")) {
			// check if the exhibition & room are valid if not report back
    		screen.get("#roominfo_feedback").html("");
			boolean result = checkExhibition(data);
			if (result==true) result = checkRoom(data);
			// all is valid lets create exhibition and first room
			String newid = ""+new Date().getTime();
    		FsNode exhibitionnode = new FsNode("exhibition",newid);
    		exhibitionnode.setProperty("name",(String)data.get("roominfo_exhibition"));
    		exhibitionnode.setProperty("location",(String)data.get("roominfo_location"));
    		exhibitionnode.setProperty("timeframe",(String)data.get("roominfo_timeframe"));
    		
    		boolean insertresult = Fs.insertNode(exhibitionnode,"/domain/"+screen.getApplication().getDomain()+"/user/"+username);
			if (insertresult) {
				// lets insert the room node
	    		FsNode roomnode = new FsNode("room",""+new Date().getTime());
	    		roomnode.setProperty("name",(String)data.get("roominfo_room"));
	    		roomnode.setProperty("shape","roomshape_l");
	    		insertresult = Fs.insertNode(roomnode,"/domain/"+screen.getApplication().getDomain()+"/user/"+username+"/exhibition/"+newid);
				if (insertresult) {
					screen.get(selector).remove();
					screen.get("#content").append("div","dashboard",new DashboardController());
				} else {
		    		screen.get("#roominfo_feedback").html("** could not insert room **"); 
				}
			} else {
	    		screen.get("#roominfo_feedback").html("** could not insert exhibition **"); 
			}
		} else {
			// check if new room is valid if not report back
			// all is valid lets add the room
		}
    }
    
    private boolean checkExhibition(JSONObject data) {
    	String exhibition = (String)data.get("roominfo_exhibition");
    	String location = (String)data.get("roominfo_location");
    	String timeframe = (String)data.get("roominfo_timeframe");
    	if (exhibition.equals("")) {
    		screen.get("#roominfo_feedback").html("** exhibition name needed **");
    		return false;
    	} else if (location.equals("")) {
    		screen.get("#roominfo_feedback").html("** location name needed **");
    		return false;
    	} else if (timeframe.equals("")) {
    		screen.get("#roominfo_feedback").html("** timeframe name needed **");
    		return false;
    	}
    	return true;
    }
    
    private boolean checkRoom(JSONObject data) {
    	String room = (String)data.get("roominfo_room");
    	if (room.equals("")) {
    		screen.get("#roominfo_feedback").html("** room name needed **");
    		return false;
    	} 
    	return true;
    }
 	 
}
