/* 
* 
* Copyright (c) 2017 Noterik B.V.
* 
* This file is part of MuPoP framework
*
* MuPoP framework is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* MuPoP framework is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with MuPoP framework .  If not, see <http://www.gnu.org/licenses/>.
*/
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
import org.springfield.lou.controllers.station.apps.interactivevideo.InteractiveVideoEditController;
import org.springfield.lou.controllers.station.apps.photoexplore.PhotoExploreEditController;
import org.springfield.lou.controllers.station.apps.photoinfospots.PhotoInfoSpotsEditController;
import org.springfield.lou.controllers.station.apps.trivia.TriviaEditController;
import org.springfield.lou.model.ModelEvent;
import org.springfield.lou.screen.Screen;

public class StationController extends Html5Controller {
	
	String fields = "station_labelid,station_name,station_app,station_paired";
	String currentapp = "none"; // we assume a empty at the start
	String oldpaired = "";

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
		fillPage(); // fill the screen
	}
	
	
	/**
	 * fill our space on our screen
	 */
	private void fillPage() {
		if (model.getProperty("/screen['vars']/newstation").equals("true")) { // is it a new or old station
			createNewStation();
		} 
			FsNode stationnode = model.getNode("@station");
    		if (stationnode==null) { // did we find the old station node ?
    			screen.get(selector).remove();
    		} else {
    			currentapp = stationnode.getProperty("app"); // yes 
    			JSONObject data = getAppList(currentapp); // read the available aps to json 
    			data.put("stationname",stationnode.getProperty("name")); // load the name to json
    			data.put("stationlabel",stationnode.getProperty("labelid")); // load the labelid
    			data.put("stationpaired",stationnode.getProperty("paired"));
    			oldpaired = stationnode.getProperty("paired");
    			//System.out.println("CURAPP="+currentapp);
    			data = addHids(data);
    			screen.get(selector).render(data); // send the data to client mustache render
    			if (currentapp.equals("photoexplore")) { // check for the 2 apps en jump if needed
    				screen.get("#station_appspace").append("div","appeditor_photoexplore",new PhotoExploreEditController());
    			} else if (currentapp.equals("photoinfospots")) {
    				screen.get("#station_appspace").append("div","appeditor_photoinfospots",new PhotoInfoSpotsEditController());
    			} else if (currentapp.equals("interactivevideo")) {
    				screen.get("#station_appspace").append("div","appeditor_interactivevideo",new InteractiveVideoEditController());
    			} else if (currentapp.equals("trivia")) {
    				screen.get("#station_appspace").append("div","appeditor_trivia",new TriviaEditController());

    			}
    		}
		screen.get("#station_formarea").draggable(); // make the window draggable for fun (its a overlay)
		screen.get("#station_done").on("mouseup","onDone", this); // watch if user wants to cancel
		screen.get("#station_labelid").on("change","onLabelChange", this); // watch if user wants to save
		screen.get("#station_name").on("change","onNameChange", this); // watch if user wants to save
		screen.get("#station_app").on("change","onAppChange", this); // watch if user wants to save
		screen.get("#station_paired").on("change","onPairedChange", this); // watch if user wants to save

	}
	
	private void createNewStation() {
		FsNode stationnode = new FsNode("station",""+new Date().getTime());
		stationnode.setProperty("labelid",getNewStationName()); // generate a id best we can 
		stationnode.setProperty("name","name"+stationnode.getProperty("labelid"));
		stationnode.setProperty("room", model.getProperty("@roomid"));
		stationnode.setProperty("app", "none");
		stationnode.setProperty("x","50");
		stationnode.setProperty("y","85");
		stationnode.setProperty("paired","*");
		model.putNode("@stations", stationnode);
		model.setProperty("@stationid", stationnode.getId());
		
		// also create some subnodes we need
		FsNode node = new FsNode("content","waitscreen");
		model.putNode("@station",node);
		node = new FsNode("content","contentselect");
		model.putNode("@station",node);
		node = new FsNode("content","mainapp");
		model.putNode("@station",node);
	}
	
    public void onPairedChange(Screen s,JSONObject data) {
    //	System.out.println("PAIRED STATION CHANGE="+data.toJSONString());
    	String oldvalue = model.getProperty("@station/paired");
    	String newvalue = (String)data.get("value");
    	model.setProperty("@station/paired",newvalue);
    	
		model.setProperty("/domain/mupop/config/hids/hid/"+oldvalue+"/username","");
		model.setProperty("/domain/mupop/config/hids/hid/"+oldvalue+"/exhibitionid","");
		model.setProperty("/domain/mupop/config/hids/hid/"+oldvalue+"/stationid","");
		
		if (newvalue.indexOf("*")==-1) {
			model.setProperty("/domain/mupop/config/hids/hid/"+newvalue+"/username",model.getProperty("@username"));
			model.setProperty("/domain/mupop/config/hids/hid/"+newvalue+"/exhibitionid",model.getProperty("@exhibitionid"));
			model.setProperty("/domain/mupop/config/hids/hid/"+newvalue+"/stationid",model.getProperty("@stationid"));
		}

    	
		System.out.println("SENDING SHARED PAIR NOTIFY");
		model.notify("/shared['mupop']/hid['"+oldvalue+"']","unpaired");
		model.notify("/shared['mupop']/hid['"+newvalue+"']","paired");
    	fillPage(); // we need to rewrite this page
    }
	
    public void onAppChange(Screen s,JSONObject data) {
    	//System.out.println("APP STATION CHANGE="+data.toJSONString());
    	model.setProperty("@station/app",(String)data.get("value"));
    	fillPage(); // we need to rewrite this page
		model.notify("@station","changed");
    }
	
    public void onLabelChange(Screen s,JSONObject data) {
    	//System.out.println("LABEL STATION CHANGE="+data.toJSONString());
    	model.setProperty("@station/labelid",(String)data.get("value"));
    }
	
    public void onNameChange(Screen s,JSONObject data) {
    	System.out.println("NAME STATION CHANGE="+data.toJSONString());
    	model.setProperty("@station/name",(String)data.get("value"));
    }
	
	private JSONObject addHids(JSONObject data) {
		FSList list = model.getList("/domain/mupop/config/hids/hid");
		if (list!=null) {
			JSONObject paired = list.toJSONObject("en","stationname");
			data.put("paired",paired);
		}
		return data;
	}
	
	/**
	 * user hit done signal
	 * @param s
     * @param data
	 */
    public void onDone(Screen s,JSONObject data) {
    	screen.get(selector).remove(); // remove us from the screen
		model.notify("@room","changed");
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
		node = new FsNode("apps","5");
		node.setProperty("name","interactivevideo");
		node.setProperty("labelname","InteractiveVideo");
		list.addNode(node);
		node = new FsNode("apps","6");
		node.setProperty("name","trivia");
		node.setProperty("labelname","Trivia");
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
		FSList stations = model.getList("@stations");
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
