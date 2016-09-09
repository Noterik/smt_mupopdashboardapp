package org.springfield.lou.controllers.dashboard;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONObject;
import org.springfield.fs.FSList;
import org.springfield.fs.FSListManager;
import org.springfield.fs.Fs;
import org.springfield.fs.FsNode;
import org.springfield.lou.controllers.Html5Controller;
import org.springfield.lou.controllers.room.RoomController;
import org.springfield.lou.controllers.roominfo.RoomInfoController;
import org.springfield.lou.model.ModelEvent;
import org.springfield.lou.screen.Screen;

public class DashboardController extends Html5Controller {
	
	String username; // the real user name (daniel,pieter,rutger)
	String usernamepath; // fs path to the name
	String exhibitionlistpath; // fs path to the exhibitionlist
	String exhibitionidpath; // fs path to the exhibitionid 
	
	/**
	 * Dashboard of MuPoP, starting point for all the controllers in the backend
	 */
	public DashboardController() {
	}
	
	/**
	 * moment this controller gets attached to target screen on given id
	 * 
	 * @see org.springfield.lou.controllers.Html5Controller#attach(java.lang.String)
	 */
	public void attach(String sel) {
		selector = sel; // set the selector for later reuse
		getVars(); // load all the vars we need
		if (username==null) { // is the user logged in? 
			screen.get(selector).render(); // nope lets render a empty screen
			model.onPropertyUpdate(usernamepath,"onLogin",this); // wait for a login
		} else {
			fillPage(); // we have a user lets show his/hers exhibition list
		}
	}
	
	/**
	 * Load all the vars we plan to use if we can already
	 */
	private void getVars() {
		usernamepath = "/screen['profile']/username"; // path in screen to share between controllers
		exhibitionidpath="/screen['vars']/exhibitionid"; // path in screen to share between controllers
		
		username = model.getProperty(usernamepath); // get the username from the screen space
		exhibitionlistpath= "/domain/"+screen.getApplication().getDomain()+"/user/"+username+"/exhibition"; // define the path to the list based on username/domain
	}

	/**
	 * Called if user logs in on this screen
	 * 
	 * @param e 
	 */
	public void onLogin(ModelEvent e) {
		getVars(); // they might have changed
		fillPage(); // lets fill the screen again now we are logged in 
	}
	
	/**
	 * fill our space on our screen
	 */
	private void fillPage() {
		FSList list = FSListManager.get(exhibitionlistpath,false); // get list of users exhibitions (OLD FORMAT)
		List<FsNode> nodes = list.getNodes(); // gets its nodes in order of creation
		
		JSONObject data = FSList.ArrayToJSONObject(nodes,screen.getLanguageCode(),"name,location,timeframe"); // convert it to json list with wanted fields
		data.put("username",username); // also add the user name so we can display it
		screen.get(selector).render(data); // tell frontend to render it using mustache
 		screen.get(".selectablerow").on("mouseup","onShow", this); // wait for user to select one of the exhibitions from the list
	}
	
	/**
	 * onShow will be called when user selects exhibtion from the list
	 * @param s
	 * @param data
	 */
    public void onShow(Screen s,JSONObject data) {
    	String id = (String)data.get("id"); // lets get the id of the wanted exhibition
    	model.setProperty(exhibitionidpath, id); // store it in the screen space so other controllers can use it
    	s.get(selector).remove(); // clearly we don't need this controller anymore so lets remove ourselves from the screen
    	if (id.equals("newexhibition")) {
    		s.get("#content").append("div","roominfo",new RoomInfoController()); // if user wanted a new exhibition we open new room info screen for it
    	} else {
       		s.get("#content").append("div","room",new RoomController()); // if user wanted a old exhibition lets open the default room for it
    	}
    }
	
 	 
}
