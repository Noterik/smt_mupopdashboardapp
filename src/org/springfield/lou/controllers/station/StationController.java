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
import org.springfield.lou.model.ModelEvent;
import org.springfield.lou.screen.Screen;

public class StationController extends Html5Controller {
	
	String fields = "station_labelid,station_name,station_app";


	public StationController() {
	}
	
	public void attach(String sel) {
		selector = sel;

		// add the current one to the list
//		data.put("roomid",model.getProperty("/screen/roomid"));
//		data.put("roomname",model.getProperty("/screen/roomname"));
		
		fillPage();
		screen.get("#station_formarea").draggable();
		screen.get("#station_cancel").on("mouseup","onCancel", this);
 		screen.get("#station_save").on("mouseup",fields,"onSave", this);
	}
	
	private void fillPage() {
		if (model.getProperty("/screen/newstation").equals("true")) {
			String currentapp = "none";
			JSONObject data = getAppList(currentapp);
			data.put("newstation","true");
			data.put("stationlabel",getNewStationName()); // generate a id best we can 
			screen.get(selector).render(data);
		} else {
			
		}
		
	}
	
    public void onCancel(Screen s,JSONObject data) {
    	screen.get(selector).remove();
    }
    
    public void onSave(Screen s,JSONObject data) {
		String username = model.getProperty("/screen/username");
		String exhibitionid = model.getProperty("/screen/exhibitionid");
		
    	if (model.getProperty("/screen/newstation").equals("true")) {
			String newid = ""+new Date().getTime();
    		FsNode stationnode = new FsNode("station",newid);
    		stationnode.setProperty("labelid",(String)data.get("station_labelid"));
    		stationnode.setProperty("name",(String)data.get("station_name"));
    		stationnode.setProperty("room","offline");
    		stationnode.setProperty("app",(String)data.get("station_app"));
    		stationnode.setProperty("x","40");
    		stationnode.setProperty("y","85");
    		
    		boolean insertresult = Fs.insertNode(stationnode,"/domain/"+screen.getApplication().getDomain()+"/user/"+username+"/exhibition/"+exhibitionid);
			if (insertresult) {
				System.out.println("INSERT STATION DONE");
			}
    	} else {
    		System.out.println("SAVING OLD STATION WANTED");
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
		String stationpath = "/domain/"+screen.getApplication().getDomain()+"/user/"+model.getProperty("/screen/username")+"/exhibition/"+model.getProperty("/screen/exhibitionid")+"/station";
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
