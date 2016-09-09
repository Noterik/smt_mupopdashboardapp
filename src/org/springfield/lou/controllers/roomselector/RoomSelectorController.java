package org.springfield.lou.controllers.roomselector;


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

public class RoomSelectorController extends Html5Controller {

	String username;
	String usernamepath;
	String exhibitionidpath;
	String exhibitionid;
	String roomidpath;
	String roomid;
	String roomnamepath;
	String roomname;

	public RoomSelectorController() {
	}
	
	public void attach(String sel) {
		selector = sel;
		getVars();
		
		String exhibitionpath = "/domain['"+screen.getApplication().getDomain()+"']/user['"+username+"']/exhibition['"+exhibitionid+"']/room";
		FSList list = model.getList(exhibitionpath);
		List<FsNode> nodes = list.getNodes();
		JSONObject data = FSList.ArrayToJSONObject(nodes,screen.getLanguageCode(),"name");
		
		// add the current one to the list
		data.put("roomid",roomid);
		data.put("roomname",roomname);
		screen.get(selector).render(data);
		
		screen.get("#roomselector_formarea").draggable();
		screen.get("#roomselector_cancel").on("mouseup","onCancel", this);
 		screen.get("#roomselector_selector").on("change","onSelectChange", this);
	}
	
	/**
	 * Load all the vars we plan to use if we can already
	 */
	private void getVars() {
		usernamepath="/screen['profile']/username"; // path in screen to share between controllers
		exhibitionidpath="/screen['vars']/exhibitionid"; // path in screen to share between controllers
		username = model.getProperty(usernamepath); // get the username from the screen space
		exhibitionid = model.getProperty(exhibitionidpath); // get the username from the screen space
		roomidpath="/screen['vars']/roomid"; // path in screen to share between controllers
		roomnamepath="/screen['vars']/roomname"; // path in screen to share between controllers
		roomid = model.getProperty(roomidpath); // get the username from the screen space
		roomname = model.getProperty(roomnamepath); // get the username from the screen space
	}
	
    public void onCancel(Screen s,JSONObject data) {
    	screen.get(selector).remove();
    }
    
    public void onSelectChange(Screen s,JSONObject data) {
		System.out.println("SET ROOM CHANGE EVENT="+(String)data.get("value")+" ROOMIDPATH="+roomidpath);
    	model.setProperty(roomidpath,(String)data.get("value"));
    	screen.get(selector).remove();
    }
	
 	 
}
