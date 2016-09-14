package org.springfield.lou.controllers.station;


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
import org.springfield.lou.controllers.station.apps.PhotoExploreEditController;
import org.springfield.lou.controllers.station.apps.PhotoInfoSpotsEditController;
import org.springfield.lou.model.ModelEvent;
import org.springfield.lou.screen.Screen;

public class StationController extends Html5Controller {
	
	String fields = "station_labelid,station_name,station_app";
	String currentapp = "none"; // we assume a empty at the start
	String username;
	String usernamepath;
	String exhibitionidpath;
	String exhibitionid;
	String exhibitionnodepath;
	String roomidpath;
	String roomnamepath;
	String roomid;
	String roompath;
	String stationidpath;
	String stationid;

	/**
	 * Station controller where the station and its app get edited
	 */
	public StationController() {
	}
	
	/**
	 * fill our space on our screen
	 */
	public void attach(String sel) {
		selector = sel; // save for later use
		getVars(); // read all the needed vars
		fillPage(); // fill the screen
	}
	
	/**
	 * Load all the vars we plan to use if we can already
	 */
	private void getVars() {
		usernamepath = "/screen['profile']/username"; // path in screen to share between controllers
		exhibitionidpath="/screen['vars']/exhibitionid"; // path in screen to share between controllers
		roomidpath="/screen['vars']/roomid"; // path in screen to share between controllers
		stationidpath="/screen['vars']/stationid"; // path in screen to share between controllers
		
		username = model.getProperty(usernamepath); // get the username from the screen space
		exhibitionid = model.getProperty(exhibitionidpath); // get the exhibitionid from the screen space
		roomid = model.getProperty(roomidpath); // get the roomid from the screen space
		stationid = model.getProperty(stationidpath); // get the stationid from the screen space
		
		model.setProperty("/screen['vars']/stationfullpath","/domain['"+screen.getApplication().getDomain()+"']/user['"+username+"']/exhibition['"+exhibitionid+"']/station['"+stationid+"']");
	}
	
	/**
	 * fill our space on our screen
	 */
	private void fillPage() {
		if (model.getProperty("/screen['vars']/newstation").equals("true")) { // is it a new or old station
			currentapp = "none"; // its new so no app is selected
			JSONObject data = getAppList(currentapp);  // read the available aps to json
			data.put("newstation","true"); // set a variable to
			data.put("stationlabel",getNewStationName()); // generate a id best we can 
			screen.get(selector).render(data); // send data to client mustache render
		} else {
    		FsNode stationnode = model.getNode("/domain['"+screen.getApplication().getDomain()+"']/user['"+username+"']/exhibition['"+exhibitionid+"']/station['"+stationid+"']");
    		if (stationnode!=null) { // did we find the old station node ?
    			currentapp = stationnode.getProperty("app"); // yes 
    			JSONObject data = getAppList(currentapp); // read the available aps to json 
    			data.put("stationname",stationnode.getProperty("name")); // load the name to json
    			data.put("stationlabel",stationnode.getProperty("labelid")); // load the labelid
    			
    			screen.get(selector).render(data); // send the data to client mustache render
    			if (currentapp.equals("photoexplore")) { // check for the 2 apps en jump if needed
    				screen.get("#station_appspace").append("div","appeditor_photoexplore",new PhotoExploreEditController());
    			} else if (currentapp.equals("photoinfospots")) {
    				screen.get("#station_appspace").append("div","appeditor_photoinfospots",new PhotoInfoSpotsEditController());
    			}
    		}
		}
		screen.get("#station_formarea").draggable(); // make the window draggable for fun (its a overlay)
		screen.get("#station_cancel").on("mouseup","onCancel", this); // watch if user wants to cancel
 		screen.get("#station_save").on("mouseup",fields,"onSave", this); // watch if user wants to save
	}
	
	/**
	 * user hit cancel signal
	 * @param s
     * @param data
	 */
    public void onCancel(Screen s,JSONObject data) {
		model.notify("/screen['appcancel']", new FsNode("data","1")); // signal the app controller if active
    	screen.get(selector).remove(); // remove us from the screen
    }
    
    /**
     * user hit save
     * @param s
     * @param data
     */
    public void onSave(Screen s,JSONObject data) {
    	if (model.getProperty("/screen['vars']/newstation").equals("true")) { // check if its a new or old station
			String newid = ""+new Date().getTime(); // its new lets create it 
    		FsNode stationnode = new FsNode("station",newid); // so new node with new id
    		stationnode.setProperty("labelid",(String)data.get("station_labelid")); // set the station label
    		stationnode.setProperty("name",(String)data.get("station_name")); // set the station name
    		stationnode.setProperty("room","offline"); // since its new its starts in offline mode
    		stationnode.setProperty("app",(String)data.get("station_app")); // set the app defined
    		stationnode.setProperty("x","40"); // kinda weird they always popup in same location
    		stationnode.setProperty("y","85");  // kinda weird they always popup in same location
    		
    		// save the node (will still change)
    		model.putNode("/domain['"+screen.getApplication().getDomain()+"']/user['"+username+"']/exhibition['"+exhibitionid+"']",stationnode);
    		model.notify("/screen['appsave']", new FsNode("data","1")); // notify the app controller to it can also save
    	} else {
    		// get the node to be updated
    		FsNode stationnode = model.getNode("/domain['"+screen.getApplication().getDomain()+"']/user['"+username+"']/exhibition['"+exhibitionid+"']/station['"+stationid+"']");
    		if (stationnode!=null) { 
        		stationnode.setProperty("labelid",(String)data.get("station_labelid")); // set the station label
        		stationnode.setProperty("name",(String)data.get("station_name"));  // set the station name
        		stationnode.setProperty("app",(String)data.get("station_app"));	// set the app defined
        		model.putNode("/domain['"+screen.getApplication().getDomain()+"']/user['"+username+"']/exhibition['"+exhibitionid+"']",stationnode);
        		
    		}
    		model.notify("/screen['appsave']", new FsNode("data","1")); // notify the app controller to it can also save
    	}
    	screen.get(selector).remove(); // remove from screen
    	model.setProperty(roomidpath,roomid); // dirty trick to get a reload
    }
    
    /*
     * temp list might be programmable in the future for example per client
     */
    private JSONObject getAppList(String currentapp) {
		FSList list =new FSList();
		FsNode node = new FsNode("apps","1");
		node.setProperty("name",currentapp.toLowerCase());
		node.setProperty("labelname",currentapp);
		list.addNode(node);
		node = new FsNode("apps","2");
		node.setProperty("name","none");
		node.setProperty("labelname","None");
		list.addNode(node);
		node = new FsNode("apps","3");
		node.setProperty("name","photoexplore");
		node.setProperty("labelname","PhotoExplore");
		list.addNode(node);
		node = new FsNode("apps","4");
		node.setProperty("name","photoinfospots");
		node.setProperty("labelname","PhotoInfoSpots");
		list.addNode(node);
		return list.toJSONObject("en","name,labelname");
    }
    
    /**
     * get a new station name based on the idea if we can claim a number we will. In the future we might
     * add multiple generators for different clients.
     * 
     * @return
     */
    private String getNewStationName() {
    	int result = 0;
    	// get the list from domain so see if we are on a number idea we can use.
		String stationpath = "/domain['"+screen.getApplication().getDomain()+"']/user['"+username+"']/exhibition['"+exhibitionid+"']/station";
		FSList stations = model.getList(stationpath);
		if (stations!=null && stations.size()>0) { // if we have stations already lets find the highest number
			for(Iterator<FsNode> iter = stations.getNodes().iterator() ; iter.hasNext(); ) {
				FsNode node = (FsNode)iter.next();	
				try {
					int newvalue = Integer.parseInt(node.getProperty("labelid")); // parse the number and store if valid
					if (newvalue>result) {
						result = newvalue; // valid number remember if its higher than the last one
					}
				} catch(Exception e) { // forget exceptions we assume many are not numbers
				}
			}
		}
		return ""+(result+1); // take the highest number add one so its new and return it 
    }

 	 
}
