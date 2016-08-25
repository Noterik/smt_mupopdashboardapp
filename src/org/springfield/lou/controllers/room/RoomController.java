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
import org.springfield.lou.model.ModelEvent;
import org.springfield.lou.screen.Screen;

public class RoomController extends Html5Controller {
	
	public RoomController() {
	}
	
	public void attach(String sel) {
		selector = sel;
		screen.loadStyleSheet("room/room.css");
		fillPage();
	}
	
	
	private void fillPage() {
		JSONObject data = new JSONObject();
		String username = model.getProperty("/screen/username");
		String exhibitionid = model.getProperty("/screen/exhibitionid");
		
		data.put("username",username);
		data.put("exhibitionid",exhibitionid);
		
		FsNode exhibitionnode = model.getNode("/domain/"+screen.getApplication().getDomain()+"/user/"+model.getProperty("/screen/username")+"/exhibition/"+exhibitionid);
		if (exhibitionnode!=null) {
			data.put("exhibition",exhibitionnode.getProperty("name"));
			data.put("location",exhibitionnode.getProperty("location"));
			data.put("timeframe",exhibitionnode.getProperty("timeframe"));
			data.put("room", "zolder");
		} else {
			// should be some error here, since this can't happen really
		}
		data.put(getRoomShape(),"true");
		screen.get(selector).parsehtml(data);
		screen.get("#room_station1").draggable();
	}
	
	
	private String getRoomShape() {
		return("roomshape_l");
	}
	
	
 	 
}
