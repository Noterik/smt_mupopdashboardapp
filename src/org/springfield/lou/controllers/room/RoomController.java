package org.springfield.lou.controllers.room;


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
import org.springfield.lou.controllers.roominfo.RoomInfoController;
import org.springfield.lou.controllers.roomselector.RoomSelectorController;
import org.springfield.lou.controllers.station.StationController;
import org.springfield.lou.model.ModelEvent;
import org.springfield.lou.screen.Screen;

public class RoomController extends Html5Controller {
	
	String newstationpath;
	FsNode roomnode; 
	
	
	/**
	 * One of the core controllers shows the layout of a room with access to many
	 * elements of the dashboard.
	 */
	public RoomController() {
	}
	
	/**
	 * moment this controller gets attached to target screen on given id
	 * 
	 * @see org.springfield.lou.controllers.Html5Controller#attach(java.lang.String)
	 */
	public void attach(String sel) {
		selector = sel; // set the id for later use.
		getVars();
		fillPage();
	}
	
	/**
	 * Load all the vars we plan to use if we can already
	 */
	private void getVars() {
		newstationpath = "/screen['vars']/newstation";
		String roomid = model.getProperty("@roomid");
		if (roomid==null || roomid.equals("")) { 
			roomnode = getAndsetFirstRoom(); // if no room set already get the opening room
			model.setProperty("@roomid",roomnode.getId()); // set it in screen space for other controllers
		} else {
			roomnode = model.getNode("@room"); // if we have one get the node for it
		}
		String roomname = roomnode.getProperty("name"); // read and set the room name
		model.setProperty("@roomname",roomname); // set it in screen space for other controllers
	}
	
	/**
	 * fill our space on our screen
	 */
	private void fillPage() {
		JSONObject data = new JSONObject(); // create json object for the clients render call
		data.put("username",model.getProperty("@username")); // add username so we can display it
		data.put("exhibitionid",model.getProperty("@exhibitionid")); // add exhibition idea for use in forms
		
		FsNode exhibitionnode = model.getNode("@exhibition"); // get the exhibition node
		if (exhibitionnode!=null) {	// do we have a valid one
			data.put("exhibition",exhibitionnode.getProperty("name")); // ifso set name in json
			data.put("location",exhibitionnode.getProperty("location")); // ifso set location in json
			data.put("timeframe",exhibitionnode.getProperty("timeframe")); // ifso set timeframe in json
			data.put("room", roomnode.getProperty("name")); // now we have the roomnode set its name in json
			data.put(roomnode.getProperty("shape"),"true"); // and tricky use the shape name to signal json selector

			data.put("stations",addStations(model.getProperty("@roomid"))); // add all the active in room stations to json
			data.put("offline",addOfflineStations()); // add all the offline stations to json
		} else {
			// should be some error here, since this can't happen really
		}
		screen.get(selector).render(data); // now we have all data give it to client and render using mustache
		screen.get(".room_station").draggable(); // set the window draggable for fun
		screen.get(".room_station").on("dragstop","onStationMove", this);  // if user dragged a station tell us
		screen.get(".room_station").on("dblclick","onStationSelect", this);  // if user want to edit station tell us 
		screen.get(".breadcrumbpathsubmit").on("mouseup","onBreadCrumbSubmit", this);  // is user wants to go back tell us
 		screen.get("#room_roomselectorbutton").on("mouseup","onRoomSelectorButton", this); // if user wants to change room tell us
 		screen.get("#room_addstationbutton").on("mouseup","onAddStationButton", this); // if user wants to add a new station tell us
 		screen.get("#room_roomsettingsbutton").on("mouseup","onRoomSettingsButton", this); // if user wants to edit the room tell us
		model.onPropertyUpdate("/screen['vars']/roomid","onRoomChange",this); // watch for room change events probably done my RoomSelectorController
	}
	
	/**
	 * add the active stations data to the json object for display
	 */
	private JSONObject addStations(String roomid) {
		FSList stations = model.getList("@stations"); // get stations from domain space
		if (stations!=null && stations.size()>0) {
			List<FsNode> nodes = stations.getNodesFiltered("room",roomid); // filter out the ones based on room property
			return FSList.ArrayToJSONObject(nodes,screen.getLanguageCode(),"app,labelid,name,x,y,room,url"); // convert it to json format
		} else {
			return new JSONObject(); // return empty json object if no stations found
		}	
	}
	
	/**
	 * add the offline stations data to the json object for display
	 */
	private JSONObject addOfflineStations() {
		FSList stations = model.getList("@stations");
		if (stations!=null && stations.size()>0) {
			List<FsNode> nodes = stations.getNodesFiltered("room","offline"); 
			return FSList.ArrayToJSONObject(nodes,screen.getLanguageCode(),"app,labelid,name,x,y,room,url");
		} else {
			return new JSONObject();
		}	
	}
	
	/**
	 * tmp implemetation of showing the default first room (should recover the old from some personal memory)
	 */
	private FsNode getAndsetFirstRoom() {
		FSList rooms = model.getList("@rooms");
		if (rooms!=null && rooms.size()>0) {
			return rooms.getNodes().get(0);
		}
		return null;
	}
	
	/**
	 * User wants to edit a room
	 * @param s
	 * @param data
	 */
    public void onRoomSettingsButton(Screen s,JSONObject data) {
    	System.out.println("ROOM EDIT WANTED");
    	s.get(selector).remove();
		s.get("#content").append("div","roominfo",new RoomInfoController()); // if user wanted a new exhibition we open new room info screen for it
    }
	
	/**
	 * User wants to select a different room
	 * @param s
	 * @param data
	 */
    public void onRoomSelectorButton(Screen s,JSONObject data) {
    	screen.get(selector).append("div","roomselector",new RoomSelectorController()); // add the room selector as a overlay
    }
    
    /**
     * user double clicked on station
     * @param s
     * @param data
     */
    public void onStationSelect(Screen s,JSONObject data) {
    	String stationid = ((String)data.get("id")).substring(12); // get the id by removing prefix
    	model.setProperty("@stationid",stationid); // set id screen space 
    	model.setProperty(newstationpath, "false");
    	screen.get(selector).append("div","station",new StationController()); 	
    }
    
    /**
     * User wants to create a new station
     * @param s
     * @param data
     */
    public void onAddStationButton(Screen s,JSONObject data) {
    	model.setProperty(newstationpath, "true"); // tell other controllers we are talking new station !
    	screen.get(selector).append("div","station",new StationController()); // create the station controller as overlay
    }
    
    /**
     * User has moved a station so we need to save it with correct settings
     * @param s
     * @param data
     */
    public void onStationMove(Screen s,JSONObject data) {
    	String stationid = ((String)data.get("id")).substring(12); // remove the prefix to get the id
    	model.setProperty("@stationid", stationid);
    	
    	double xp = (Double)data.get("screenXp"); // get the percentage x from the station
    	double yp = (Double)data.get("screenYp"); // get the percentage y from the station

    	model.setProperty("@station/x",""+xp); // set the x property
    	model.setProperty("@station/y",""+yp); // set the y property
    	if (yp<80) { // kinda hacked based on that the 'offline area' starts at 80% of the screen
    		model.setProperty("@station/room",model.getProperty("@roomid")); // set the room id to this room
    	} else {
        	model.setProperty("@station/room","offline"); // set room id to offline to signal not in use
    	}
    }
    
    /**
     * room change happened probably by RoomSelectorController
     * @param e
     */
	public void onRoomChange(ModelEvent e) {
		String check = model.getProperty("@roomid");
		System.out.println("ROOM ID="+check);
		if (check!=null && check.equals("addnewroom")) {
	    	System.out.println("ROOM ADD WANTED");
	    	screen.get(selector).remove();
			screen.get("#content").append("div","roominfo",new RoomInfoController()); // if user wanted a new exhibition we open new room info screen for it
		} else {
			getVars();
			fillPage(); // fill the page again
		}
	}
	
	/**
	 * User wants to return to the index page
	 */
    public void onBreadCrumbSubmit(Screen s,JSONObject data) {
    	screen.get(selector).remove(); // remove us from the screen.
		screen.get("#content").append("div","dashboard",new DashboardController()); // 
    }
 	 
}
