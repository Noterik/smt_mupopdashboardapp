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
import org.springfield.lou.controllers.roomselector.RoomSelectorController;
import org.springfield.lou.controllers.station.StationController;
import org.springfield.lou.model.ModelEvent;
import org.springfield.lou.screen.Screen;

public class RoomController extends Html5Controller {
	
	public RoomController() {
	}
	
	public void attach(String sel) {
		selector = sel;
		model.setProperty("/screen/roomid","");
		fillPage();
	}
	
	
	private void fillPage() {
		JSONObject data = new JSONObject();
		String username = model.getProperty("/screen/username");
		String exhibitionid = model.getProperty("/screen/exhibitionid");
		String roomid = model.getProperty("/screen/roomid");
		data.put("username",username);
		data.put("exhibitionid",exhibitionid);
		
		FsNode exhibitionnode = model.getNode("/domain/"+screen.getApplication().getDomain()+"/user/"+model.getProperty("/screen/username")+"/exhibition/"+exhibitionid);
		if (exhibitionnode!=null) {	
			data.put("exhibition",exhibitionnode.getProperty("name"));
			data.put("location",exhibitionnode.getProperty("location"));
			data.put("timeframe",exhibitionnode.getProperty("timeframe"));
			String roompath = "/domain/"+screen.getApplication().getDomain()+"/user/"+model.getProperty("/screen/username")+"/exhibition/"+exhibitionid+"/room";
			FsNode roomnode = null;
			if (roomid==null || roomid.equals("")) {
				roomnode = getAndsetFirstRoom(roompath);
				model.setProperty("/screen/roomid",roomnode.getId());
			} else {
				roomnode = model.getNode(roompath+"/"+roomid);
			}
			data.put("room", roomnode.getProperty("name"));
			data.put(roomnode.getProperty("shape"),"true");
			model.setProperty("/screen/roomname",roomnode.getProperty("name"));
			String stationpath = "/domain/"+screen.getApplication().getDomain()+"/user/"+model.getProperty("/screen/username")+"/exhibition/"+exhibitionid+"/station";
			data.put("stations",addStations(stationpath,roomnode.getId()));
			data.put("offline",addOfflineStations(stationpath));
			
		} else {
			// should be some error here, since this can't happen really
		}
		screen.get(selector).render(data);
		screen.get(".room_station").draggable();
		screen.get(".room_station").on("dragstop","onStationMove", this); 
		screen.get(".room_station").on("dblclick","onStationSelect", this); 
		screen.get(".breadcrumbpathsubmit").on("mouseup","onBreadCrumbSubmit", this); 
 		screen.get("#room_roomselectorbutton").on("mouseup","onRoomSelectorButton", this);
 		screen.get("#room_addstationbutton").on("mouseup","onAddStationButton", this);
		model.onPropertyUpdate("/screen/roomid","onRoomChange",this);
	}
	
	private JSONObject addStations(String path,String roomid) {
		FSList stations = FSListManager.get(path,false);
		if (stations!=null && stations.size()>0) {
			List<FsNode> nodes = stations.getNodesFiltered("room",roomid); // kind of tricky since it uses global matching
			return FSList.ArrayToJSONObject(nodes,screen.getLanguageCode(),"app,labelid,name,x,y,room,url");
		} else {
			return new JSONObject();
		}	
	}
	
	private JSONObject addOfflineStations(String path) {
		FSList stations = FSListManager.get(path,false);
		if (stations!=null && stations.size()>0) {
			List<FsNode> nodes = stations.getNodesFiltered("room","offline"); 
			return FSList.ArrayToJSONObject(nodes,screen.getLanguageCode(),"app,labelid,name,x,y,room,url");
		} else {
			return new JSONObject();
		}	
	}
	
	private FsNode getAndsetFirstRoom(String path) {
		FSList rooms = FSListManager.get(path,false);
		if (rooms!=null && rooms.size()>0) {
			return rooms.getNodes().get(0);
		}
		return null;
	}
	
    public void onRoomSelectorButton(Screen s,JSONObject data) {
    	screen.get(selector).append("div","roomselector",new RoomSelectorController());
    }
    
    public void onStationSelect(Screen s,JSONObject data) {
    	model.setProperty("/screen/newstation", "false");
    	screen.get(selector).append("div","station",new StationController()); 	
    }
    
    public void onAddStationButton(Screen s,JSONObject data) {
    	model.setProperty("/screen/newstation", "true");
    	screen.get(selector).append("div","station",new StationController()); 	
    }
    
    public void onStationMove(Screen s,JSONObject data) {
    	String stationid = ((String)data.get("id")).substring(12);
    	double xp = (Double)data.get("screenXp");
    	double yp = (Double)data.get("screenYp");
		String stationpath = "/domain/"+screen.getApplication().getDomain()+"/user/"+model.getProperty("/screen/username")+"/exhibition/"+model.getProperty("/screen/exhibitionid")+"/station/"+stationid;

    	model.setProperty(stationpath+"/x", ""+xp);
    	model.setProperty(stationpath+"/y", ""+yp);
    	if (yp<80) {
    		String roomid=model.getProperty("/screen/roomid");
    		if (roomid!=null) {
    			model.setProperty(stationpath+"/room",roomid);
    		} else {
    			System.out.println("RoomController roomid is not set should not be possible");
    		}
    	} else {
        	model.setProperty(stationpath+"/room","offline");
    	}
    }
    
	public void onRoomChange(ModelEvent e) {
		fillPage();
	}
	
    public void onBreadCrumbSubmit(Screen s,JSONObject data) {
    	screen.get(selector).remove();
		screen.get("#content").append("div","dashboard",new DashboardController());
    }
 	 
}
