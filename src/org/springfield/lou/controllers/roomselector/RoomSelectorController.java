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
		
		FSList list = model.getList("@rooms");
		List<FsNode> nodes = list.getNodes();
		JSONObject data = FSList.ArrayToJSONObject(nodes,screen.getLanguageCode(),"name");
		
		// add the current one to the list
		data.put("roomid",model.getProperty("@roomid"));
		data.put("roomname",model.getProperty("@roomname"));
		screen.get(selector).render(data);
		
		screen.get("#roomselector_formarea").draggable();
		screen.get("#roomselector_cancel").on("mouseup","onCancel", this);
 		screen.get("#roomselector_selector").on("change","onSelectChange", this);
	}
	

	
	
    public void onCancel(Screen s,JSONObject data) {
    	screen.get(selector).remove();
    }
    
    public void onSelectChange(Screen s,JSONObject data) {
    	screen.get(selector).remove();
    	model.setProperty("@oldroomid",model.getProperty("@roomid"));
    	model.setProperty("@roomid",(String)data.get("value"));
    }
	
 	 
}
