package org.springfield.lou.controllers.station.apps;

import org.json.simple.JSONObject;
import org.springfield.lou.controllers.Html5Controller;
import org.springfield.lou.screen.Screen;

public class PhotoInfoSpotsEditController extends Html5Controller{
	
	private String tab="content";

	public PhotoInfoSpotsEditController() {
	}
	
	public void attach(String sel) {
		selector = sel;
		fillPage();
	}
	
	private void fillPage() {
		JSONObject data = new JSONObject();
		data.put(tab+"tab","true"); 
			
		screen.get(selector).render(data);
 		screen.get(".appmenu").on("mouseup","onAppMenu", this);
	}
	
	public void onAppMenu(Screen s,JSONObject data) {
		tab  = (String)data.get("id");
		autoSave();
		fillPage();
	}
	
	private void autoSave() {
		
	}
	
	
}
