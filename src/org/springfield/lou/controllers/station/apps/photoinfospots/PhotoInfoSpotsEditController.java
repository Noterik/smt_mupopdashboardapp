package org.springfield.lou.controllers.station.apps.photoinfospots;

import java.util.Iterator;

import org.json.simple.JSONObject;
import org.springfield.fs.FsNode;
import org.springfield.fs.FsPropertySet;
import org.springfield.lou.controllers.Html5Controller;
import org.springfield.lou.controllers.roominfo.RoomInfoController;
import org.springfield.lou.controllers.station.apps.generic.ContentSelectEditController;
import org.springfield.lou.controllers.station.apps.generic.WaitScreenEditController;
import org.springfield.lou.model.ModelEvent;
import org.springfield.lou.screen.Screen;

public class PhotoInfoSpotsEditController extends Html5Controller{
	
	private String tab="waitscreen";
	private String valuelist="appeditor_content_url";
	
	public PhotoInfoSpotsEditController() {
		
	}
	
	public void attach(String sel) {
		selector = sel;
		getVars();
		fillPage();
	}
	
	private void getVars() {
		model.setProperty("/screen['data']/form['1']/appeditor_content_url",model.getProperty("@station/url"));
	}
	
	private void fillPage() {
		JSONObject data = new JSONObject();
		data.put(tab+"tab","true"); 
		data.put("username", model.getProperty("@username"));	
		data.put("domain",model.getProperty("mupop"));	
		data.put("exhibitionid",model.getProperty("@exhibitionid"));
		data.put("stationid",model.getProperty("@stationid"));
		screen.get(selector).render(data);
 		screen.get(".appmenu").on("mouseup",valuelist,"onAppMenu", this);
 		//screen.get(selector).on("mouseleave",valuelist,"onLeaveMenu", this);
 		fillSubPage(tab);
	}
	
	private void fillSubPage(String tab) {
		if (tab.equals("waitscreen")) {
			screen.get("#subeditor").append("div","appeditor_subeditor_waitscreen",new WaitScreenEditController());
		} else if (tab.equals("contentselect")) {
			screen.get("#subeditor").append("div","appeditor_subeditor_contentselect",new ContentSelectEditController());
		} else if (tab.equals("mainapp")) {
			screen.get("#subeditor").append("div","appeditor_subeditor_mainapp_photoinfospots",new PhotoInfoSpotsMainAppController());
		}
	}
	
	
	public void onAppMenu(Screen s,JSONObject data) {
		tab  = (String)data.get("id");
		fillPage();
	}
	
}
