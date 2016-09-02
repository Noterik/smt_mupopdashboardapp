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
	

	public RoomSelectorController() {
	}
	
	public void attach(String sel) {
		selector = sel;
		String exhibitionpath = "/domain/"+screen.getApplication().getDomain()+"/user/"+model.getProperty("/screen/username")+"/exhibition/"+model.getProperty("/screen/exhibitionid")+"/room";
		FSList list = FSListManager.get(exhibitionpath,false);
		List<FsNode> nodes = list.getNodes();
		JSONObject data = FSList.ArrayToJSONObject(nodes,screen.getLanguageCode(),"name");
		
		// add the current one to the list
		data.put("roomid",model.getProperty("/screen/roomid"));
		data.put("roomname",model.getProperty("/screen/roomname"));
		
		screen.get(selector).render(data);
		screen.get("#roomselector_formarea").draggable();
		screen.get("#roomselector_cancel").on("mouseup","onCancel", this);
 		screen.get("#roomselector_selector").on("change","onSelectChange", this);
	}
	
    public void onCancel(Screen s,JSONObject data) {
    	screen.get(selector).remove();
    }
    
    public void onSelectChange(Screen s,JSONObject data) {
		System.out.println("SET ROOM CHANGE EVENT="+(String)data.get("value"));
    	model.setProperty("/screen/roomid",(String)data.get("value"));
    	screen.get(selector).remove();
    }
	
 	 
}
