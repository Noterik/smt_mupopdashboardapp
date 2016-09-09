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


	public StationController() {
	}
	
	public void attach(String sel) {
		selector = sel;

		fillPage();
	}
	
	private void fillPage() {
		if (model.getProperty("/screen/newstation").equals("true")) {
			currentapp = "none";
			JSONObject data = getAppList(currentapp);
			data.put("newstation","true");
			data.put("stationlabel",getNewStationName()); // generate a id best we can 
			screen.get(selector).render(data);
		} else {
    		FsNode stationnode = model.getNode("/domain/"+screen.getApplication().getDomain()+"/user/"+model.getProperty("/screen['profile']/username")+"/exhibition/"+model.getProperty("/screen['vars']/exhibitionid")+"/station/"+model.getProperty("/screen/stationid"));
    		if (stationnode!=null) {
    			currentapp = stationnode.getProperty("app");
    			JSONObject data = getAppList(currentapp);
    			data.put("stationname",stationnode.getProperty("name")); 
    			data.put("stationlabel",stationnode.getProperty("labelid")); 
    			
    			screen.get(selector).render(data);
    			if (currentapp.equals("photoexplore")) {
    				screen.get("#station_appspace").append("div","appeditor_photoexplore",new PhotoExploreEditController());
    			} else if (currentapp.equals("photoinfospots")) {
    				screen.get("#station_appspace").append("div","appeditor_photoinfospots",new PhotoInfoSpotsEditController());
    			}
    		}
		}
		screen.get("#station_formarea").draggable();
		screen.get("#station_cancel").on("mouseup","onCancel", this);
 		screen.get("#station_save").on("mouseup",fields,"onSave", this);
 		//screen.get("#station_app").on("change",fields,"onAppChange", this);
	}
	
    public void onAppChange(Screen s,JSONObject data) {
    	System.out.println("APP CHANGE !!! = "+data.toJSONString());
    }
	
    public void onCancel(Screen s,JSONObject data) {
		model.notify("/screen/appcancel", new FsNode("data","1"));
    	screen.get(selector).remove();
    }
    
    public void onSave(Screen s,JSONObject data) {
		String username = model.getProperty("/screen['profile']/username");
		String exhibitionid = model.getProperty("/screen['vars']/exhibitionid");
		
    	if (model.getProperty("/screen/newstation").equals("true")) {
			String newid = ""+new Date().getTime();
    		FsNode stationnode = new FsNode("station",newid);
    		stationnode.setProperty("labelid",(String)data.get("station_labelid"));
    		stationnode.setProperty("name",(String)data.get("station_name"));
    		stationnode.setProperty("room","offline");
    		stationnode.setProperty("app",(String)data.get("station_app"));
    		stationnode.setProperty("x","40"); // kinda weird they always popup in same location
    		stationnode.setProperty("y","85");  // kinda weird they always popup in same location
    		
    		boolean insertresult = Fs.insertNode(stationnode,"/domain/"+screen.getApplication().getDomain()+"/user/"+username+"/exhibition/"+exhibitionid);
			if (insertresult) {
				System.out.println("INSERT STATION DONE");
			}
    		model.notify("/screen/appsave", new FsNode("data","1"));
    	} else {
    		System.out.println("SAVING OLD STATION WANTED");
    		FsNode stationnode = model.getNode("/domain/"+screen.getApplication().getDomain()+"/user/"+model.getProperty("/screen['profile']/username")+"/exhibition/"+model.getProperty("/screen['vars']/exhibitionid")+"/station/"+model.getProperty("/screen/stationid"));
    		if (stationnode!=null) {
        		stationnode.setProperty("labelid",(String)data.get("station_labelid"));
        		stationnode.setProperty("name",(String)data.get("station_name"));
        		stationnode.setProperty("app",(String)data.get("station_app"));	
        		Fs.insertNode(stationnode,"/domain/"+screen.getApplication().getDomain()+"/user/"+model.getProperty("/screen['profile']/username")+"/exhibition/"+model.getProperty("/screen['vars']/exhibitionid"));
        		
    		}
    		model.notify("/screen/appsave", new FsNode("data","1"));

    		//model.notify("/screen/appsave", key,value);
    		//model.notify("/screen/appsave", PropertySet);
    	}
    	screen.get(selector).remove();
    	model.setProperty("/screen/roomid",model.getProperty("/screen/roomid")); // dirty trick to get a reload
    }
    
    /*
     * temp list
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
    
    private String getNewStationName() {
    	int result = 0;
		String stationpath = "/domain/"+screen.getApplication().getDomain()+"/user/"+model.getProperty("/screen['profile']/username")+"/exhibition/"+model.getProperty("/screen['vars']/exhibitionid")+"/station";
		FSList stations = FSListManager.get(stationpath,false);
		if (stations!=null && stations.size()>0) {
			for(Iterator<FsNode> iter = stations.getNodes().iterator() ; iter.hasNext(); ) {
				FsNode node = (FsNode)iter.next();	
				try {
					int newvalue = Integer.parseInt(node.getProperty("labelid"));
					if (newvalue>result) {
						result = newvalue;
					}
				} catch(Exception e) {
					
				}
			}
		}
		return ""+(result+1);
    }

 	 
}
