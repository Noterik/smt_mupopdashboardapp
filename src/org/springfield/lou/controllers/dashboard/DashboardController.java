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
	

	public DashboardController() {
	}
	
	public void attach(String sel) {
		selector = sel;
		if (model.getProperty("/screen/username")==null) {
			JSONObject data = new JSONObject();
			screen.get(selector).render(data);
			model.onPropertyUpdate("/screen/username","onLogin",this);
		} else {
			fillPage();
		}
		model.onPropertyUpdate("/app/remotepointer/position","onPosTest",this);
		model.onPathUpdate("/app/remotepointer/","onPos2Test",this);
		model.setProperty("/app/remotepointer/position","false");
	}
	
	public void onPosTest(ModelEvent e) {
		System.out.println("POSTEST E="+e);
	}
	
	public void onPos2Test(ModelEvent e) {
		System.out.println("POSTEST2 E="+e);
	}
	
	public void onLogin(ModelEvent e) {
		FsNode node = e.getTargetFsNode();
		fillPage();
		model.setProperty("/app/remotepointer/position","true");
	}
	
	private void fillPage() {
		FSList list = FSListManager.get("/domain/"+screen.getApplication().getDomain()+"/user/"+model.getProperty("/screen/username")+"/exhibition",false);
		List<FsNode> nodes = list.getNodes();
		JSONObject data = FSList.ArrayToJSONObject(nodes,screen.getLanguageCode(),"name,location,timeframe");
		data.put("username",model.getProperty("/screen/username"));
		screen.get(selector).render(data);
 		screen.get(".selectablerow").on("mouseup","onShow", this);
	}
	
    public void onShow(Screen s,JSONObject data) {
    	String id = (String)data.get("id");
    	model.setProperty("/screen/exhibitionid", id);
    	s.get(selector).remove();
    	if (id.equals("newexhibition")) {
    		s.get("#content").append("div","roominfo",new RoomInfoController());
    	} else {
       		s.get("#content").append("div","room",new RoomController());
    	}
    }
	
 	 
}
