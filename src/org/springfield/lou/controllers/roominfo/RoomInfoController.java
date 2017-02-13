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
import org.springfield.lou.controllers.room.RoomController;
import org.springfield.lou.homer.LazyHomer;
import org.springfield.lou.model.ModelEvent;
import org.springfield.lou.screen.Screen;

public class RoomInfoController extends Html5Controller {
	String fields = "roominfo_exhibition,roominfo_location,roominfo_room,roominfo_timeframe,roominfo_jumper,roominfo_building,roominfo_description,roominfo_gpslan,roominfo_gpslat";
	String currentshape = "roomshape_square";
	
	public RoomInfoController() {
	}
	
	public void attach(String sel) {
		selector = sel;
		fillPage();
	}
	
	
	private void fillPage() {
		JSONObject data = new JSONObject();
		data.put("username",model.getProperty("@username"));
		data.put("domain",LazyHomer.getExternalIpNumber());
		String exhibitionid = model.getProperty("@exhibitionid");
		if (exhibitionid.equals("newexhibition")) {
			data.put("newexhibition","true");	
		} else {
			data.put("exhibitionid",exhibitionid);
			FsNode exhibitionnode = model.getNode("@exhibition"); // get the exhibition node
			if (exhibitionnode!=null) {	// do we have a valid one
				data.put("exhibition",exhibitionnode.getProperty("name")); // ifso set name in json
				data.put("location",exhibitionnode.getProperty("location")); // ifso set name in json
				data.put("timeframe",exhibitionnode.getProperty("timeframe")); // ifso set name in json
			}
			FsNode roomnode = model.getNode("@room"); // get the room node
			if (roomnode!=null) {	// do we have a valid one
				data.put("room",roomnode.getProperty("name")); // ifso set name in json
				currentshape = roomnode.getProperty("shape");
				data.put("shape",currentshape); // ifso set name in json
			}
			if (model.getProperty("@roomid").equals("addnewroom")) {
				data.put("addnewroom","true");	
			}
		}
		screen.get(selector).render(data);
 		screen.get("#roominfo_createbutton").on("mouseup",fields,"onCreateButton", this);
 		screen.get("#roominfo_cancelbutton").on("mouseup",fields,"onCancelButton", this);
 		screen.get(".roomshape_button").on("mouseup","onNewRoomShapeButton", this);
 		screen.get("#roominfo_selector_"+currentshape).css("background-color","#c1c097");
	}
	
    public void onNewRoomShapeButton(Screen s,JSONObject data) {
    	currentshape = ((String)data.get("id")).substring(18);
 		model.setProperty("@room/shape",currentshape);
 		fillPage();
    }
	
    public void onCancelButton(Screen s,JSONObject data) {
    	//System.out.println("ROOMUD="+model.getProperty("@oldroomid"));
    	//model.setProperty("@roomid", model.getProperty("@oldroomid"));
		if (model.getProperty("@exhibitionid").equals("newexhibition")) {
			screen.get(selector).remove();
			screen.get("#content").append("div","dashboard",new DashboardController());
		} else {
       		screen.get("#content").append("div","room",new RoomController()); // if user wanted a old exhibition lets open the default room for it
			screen.get(selector).remove();
		}
    }
	
    public void onCreateButton(Screen s,JSONObject data) {
		if (model.getProperty("@exhibitionid").equals("newexhibition")) {
			// check if the exhibition & room are valid if not report back
    		screen.get("#roominfo_feedback").html("");
			boolean result = checkExhibition(data);
			if (result==true) result = checkRoom(data);
			
			if (result==false) return;
			
			// all is valid lets create exhibition and first room
			String newid = ""+new Date().getTime();
    		FsNode exhibitionnode = new FsNode("exhibition",newid);
    		exhibitionnode.setProperty("name",(String)data.get("roominfo_exhibition"));
    		exhibitionnode.setProperty("location",(String)data.get("roominfo_location"));
    		exhibitionnode.setProperty("timeframe",(String)data.get("roominfo_timeframe"));
    		exhibitionnode.setProperty("stationselect","none");
    		exhibitionnode.setProperty("languageselect","none");

    		exhibitionnode.setProperty("availablelanguages","en");
    		exhibitionnode.setProperty("state","off");
    		boolean insertresult = model.putNode("@exhibitions",exhibitionnode);
			if (insertresult) {
				// set the exhibtion id for later use
				model.setProperty("@exhibitionid",newid);
				// lets first make the jumper work
	    		FsNode jumpernode = new FsNode("jumper",(String)data.get("roominfo_jumper"));
	    		jumpernode.setProperty("target","http://"+LazyHomer.getExternalIpNumber()+"/lou/domain/"+screen.getApplication().getDomain()+"/user/"+model.getProperty("@username")+"/html5application/mupopmobile?u="+model.getProperty("@username")+"&e="+newid);
	    		jumpernode.setProperty("domain","mupop");
	    	    		
	    		insertresult = model.putNode("@jumpers", jumpernode);
				if (!insertresult) {
		    		screen.get("#roominfo_feedback").html("** could not insert jumper **"); 
		    		return;
				} else {
					// also set it in the exhibition
					model.setProperty("@exhibition/jumper",(String)data.get("roominfo_jumper"));
				}
	    		
				// lets insert the room node
	    		FsNode roomnode = new FsNode("room",""+new Date().getTime());
	    		roomnode.setProperty("name",(String)data.get("roominfo_room"));
	    		roomnode.setProperty("shape",currentshape);
	    		insertresult = model.putNode("@exhibition", roomnode);
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
			if (model.getProperty("@roomid").equals("addnewroom")) {
				FsNode exhibitionnode = model.getNode("@exhibition");
				if (exhibitionnode!=null) {
					// lets insert the room node
					String newid = ""+new Date().getTime();
	    			FsNode roomnode = new FsNode("room",newid);
	    			roomnode.setProperty("name",(String)data.get("roominfo_room"));
	    			roomnode.setProperty("shape",currentshape);
	    			boolean insertresult = model.putNode("@exhibition", roomnode);
					if (insertresult) {
						model.setProperty("@roomid",newid);
		       			screen.get("#content").append("div","room",new RoomController()); // if user wanted a old exhibition lets open the default room for it
		       			screen.get(selector).remove();
					} else {
						screen.get("#roominfo_feedback").html("** could not insert room **"); 
					}
				}
			} else {
				// just update the roomid fields
				System.out.println("UPDATE ROOM");
				model.setProperty("@room/name",(String)data.get("roominfo_room"));
				model.setProperty("@room/shape",currentshape);
				// lets first make the jumper work
	    		FsNode jumpernode = new FsNode("jumper",(String)data.get("roominfo_jumper"));
	    		jumpernode.setProperty("target","http://"+LazyHomer.getExternalIpNumber()+"/lou/domain/"+screen.getApplication().getDomain()+"/user/"+model.getProperty("@username")+"/html5application/mupopmobile?u="+model.getProperty("@username")+"&e="+model.getProperty("@exhibitionid"));
	    		jumpernode.setProperty("domain","mupop");
	    	    		
	    		Boolean insertresult = model.putNode("@jumpers", jumpernode);
				if (!insertresult) {
		    		screen.get("#roominfo_feedback").html("** could not insert jumper **"); 
		    		return;
				} else {
					// also set it in the exhibition
					model.setProperty("@exhibition/jumper",(String)data.get("roominfo_jumper"));
				}
     			screen.get("#content").append("div","room",new RoomController()); // if user wanted a old exhibition lets open the default room for it
       			screen.get(selector).remove();
			}
			
			// check if new room is valid if not report back
			// all is valid lets add the room
		}
    }
    
    private boolean checkExhibition(JSONObject data) {
    	String exhibition = (String)data.get("roominfo_exhibition");
    	String location = (String)data.get("roominfo_location");
    	String timeframe = (String)data.get("roominfo_timeframe");
    	String jumper = (String)data.get("roominfo_jumper");
    	if (exhibition.equals("")) {
    		screen.get("#roominfo_feedback").html("** exhibition name needed **");
    		return false;
    	} else if (location.equals("")) {
    		screen.get("#roominfo_feedback").html("** location name needed **");
    		return false;
    	} else if (timeframe.equals("")) {
    		screen.get("#roominfo_feedback").html("** timeframe name needed **");
    		return false;
    	} else if (jumper.equals("")) {
    		screen.get("#roominfo_feedback").html("** short url needed **");
    		
    		return false;
    	} else if (jumperInUse(jumper)) {
    		screen.get("#roominfo_feedback").html("** url in use already **");	
    		return false;
    	}
    	return true;
    }
    
    private boolean jumperInUse(String jumper) {
		//FsNode jumpernode = model.getNode("@jumpers/jumper['"+jumper+"']"); // fix in lou needed?
		FsNode jumpernode = model.getNode("/domain['mupop']/config['jumpers']/jumper['"+jumper+"']");
		if (jumpernode==null) return false;
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
