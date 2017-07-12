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
package org.springfield.lou.controllers.station.apps.interactivevideo;

import java.util.Iterator;

import org.json.simple.JSONObject;
import org.springfield.fs.FsNode;
import org.springfield.fs.FsPropertySet;
import org.springfield.lou.controllers.Html5Controller;
import org.springfield.lou.controllers.station.apps.generic.ContentSelectEditController;
import org.springfield.lou.controllers.station.apps.generic.SettingScreenEditController;
import org.springfield.lou.controllers.station.apps.generic.WaitScreenEditController;
import org.springfield.lou.controllers.station.apps.photoexplore.PhotoExploreMainAppController;
import org.springfield.lou.model.ModelEvent;
import org.springfield.lou.screen.Screen;

public class InteractiveVideoEditController extends Html5Controller{
	private String tab="waitscreen";
	private String valuelist="appeditor_content_url";
	
	public InteractiveVideoEditController() {
		
	}
	
	public void attach(String sel) {
		selector = sel;
		fillPage();
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
		if (tab.equals("setting")) {
			screen.get("#subeditor").append("div","appeditor_subeditor_setting",new SettingScreenEditController());
		} else if (tab.equals("waitscreen")) {
			screen.get("#subeditor").append("div","appeditor_subeditor_waitscreen",new WaitScreenEditController());
		} else if (tab.equals("contentselect")) {
			screen.get("#subeditor").append("div","appeditor_subeditor_contentselect",new ContentSelectEditController());
		} else if (tab.equals("mainapp")) {
			screen.get("#subeditor").append("div","appeditor_subeditor_mainapp_interactivevideo",new InteractiveVideoMainAppController());
		}
	}
	
	
	public void onAppMenu(Screen s,JSONObject data) {
		tab  = (String)data.get("id");
		fillPage();
	}
	
}
