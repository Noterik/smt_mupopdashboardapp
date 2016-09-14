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
import org.springfield.lou.model.ModelEvent;
import org.springfield.lou.screen.Screen;

public class RoomInfoController extends Html5Controller {
	
	String username;
	String usernamepath;
	String roomidpath;
	String oldroomidpath;
	String oldroomid;
	String roomnodepath;
	String roomid;
	String exhibitionid;
	String exhibitionidpath;
	String exhibitionnodepath;
	String fields = "roominfo_exhibition,roominfo_location,roominfo_room,roominfo_timeframe,roominfo_building,roominfo_description,roominfo_gpslan,roominfo_gpslat";
	String currentshape = "roomshape_square";
	
	public RoomInfoController() {
	}
	
	public void attach(String sel) {
		selector = sel;
		getVars();
		fillPage();
	}
	
	/**
	 * Load all the vars we plan to use if we can already
	 */
	private void getVars() {
		usernamepath = "/screen['profile']/username";
		exhibitionidpath = "/screen['vars']/exhibitionid";
		roomidpath="/screen['vars']/roomid"; // path in screen to share between controllers
		oldroomidpath="/screen['vars']/oldroomid"; // path in screen to share between controllers
				
		username = model.getProperty(usernamepath);
		roomid = model.getProperty(roomidpath);
		oldroomid = model.getProperty(oldroomidpath);
		System.out.println("OLD ROOM ID="+oldroomid);
		exhibitionid = model.getProperty(exhibitionidpath);
		exhibitionnodepath="/domain['"+screen.getApplication().getDomain()+"']/user['"+username+"']/exhibition['"+exhibitionid+"']";
		roomnodepath="/domain['"+screen.getApplication().getDomain()+"']/user['"+username+"']/exhibition['"+exhibitionid+"']/room['"+roomid+"']";

	}
	
	private void fillPage() {
		JSONObject data = new JSONObject();
		//username = model.getProperty("/screen['profile']/username");
		data.put("username",username);
		//exhibitionid = model.getProperty("/screen['vars']/exhibitionid");
		if (exhibitionid.equals("newexhibition")) {
			data.put("newexhibition","true");	
		} else {
			data.put("exhibitionid",exhibitionid);
			FsNode exhibitionnode = model.getNode(exhibitionnodepath); // get the exhibition node
			if (exhibitionnode!=null) {	// do we have a valid one
				data.put("exhibition",exhibitionnode.getProperty("name")); // ifso set name in json
				data.put("location",exhibitionnode.getProperty("location")); // ifso set name in json
				data.put("timeframe",exhibitionnode.getProperty("timeframe")); // ifso set name in json
			}
			FsNode roomnode = model.getNode(roomnodepath); // get the room node
			if (roomnode!=null) {	// do we have a valid one
				data.put("room",roomnode.getProperty("name")); // ifso set name in json
				currentshape = roomnode.getProperty("shape");
				System.out.println("CUR="+currentshape);
				data.put("shape",currentshape); // ifso set name in json
			}
			if (roomid.equals("addnewroom")) {
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
    	System.out.println("NEW SHAPE CALLED="+currentshape);
		String roompath = "/domain/"+screen.getApplication().getDomain()+"/user/"+username+"/exhibition/"+exhibitionid+"/room/"+roomid;
		model.setProperty(roompath+"/shape",currentshape);
		fillPage();
    }
	
    public void onCancelButton(Screen s,JSONObject data) {
    	model.setProperty(roomidpath, oldroomid);
		if (exhibitionid.equals("newexhibition")) {
			screen.get(selector).remove();
			screen.get("#content").append("div","dashboard",new DashboardController());
		} else {
       		screen.get("#content").append("div","room",new RoomController()); // if user wanted a old exhibition lets open the default room for it
			screen.get(selector).remove();
		}
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
	    		roomnode.setProperty("shape",currentshape);
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
			System.out.println("CREATE ROOM? ="+roomid);
			if (roomid.equals("addnewroom")) {
				FsNode exhibitionnode = model.getNode(exhibitionnodepath);
				if (exhibitionnode!=null) {
					// lets insert the room node
					String newid = ""+new Date().getTime();
	    			FsNode roomnode = new FsNode("room",newid);
	    			roomnode.setProperty("name",(String)data.get("roominfo_room"));
	    			roomnode.setProperty("shape",currentshape);
	    			boolean insertresult = Fs.insertNode(roomnode,"/domain/"+screen.getApplication().getDomain()+"/user/"+username+"/exhibition/"+exhibitionid);
					if (insertresult) {
						model.setProperty(roomidpath,newid);
		       			screen.get("#content").append("div","room",new RoomController()); // if user wanted a old exhibition lets open the default room for it
		       			screen.get(selector).remove();
					} else {
						screen.get("#roominfo_feedback").html("** could not insert room **"); 
					}
				}
			} else {
				// just update the roomid fields
				String roompath = "/domain/"+screen.getApplication().getDomain()+"/user/"+username+"/exhibition/"+exhibitionid+"/room/"+roomid;
				model.setProperty(roompath+"/name",(String)data.get("roominfo_room"));
				model.setProperty(roompath+"/shape",currentshape);
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
