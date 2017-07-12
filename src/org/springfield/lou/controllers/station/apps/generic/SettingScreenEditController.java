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
package org.springfield.lou.controllers.station.apps.generic;

import java.util.Date;
import java.util.Iterator;

import org.json.simple.JSONObject;
import org.springfield.fs.FSList;
import org.springfield.fs.FsNode;
import org.springfield.fs.FsPropertySet;
import org.springfield.lou.controllers.Html5Controller;
import org.springfield.lou.model.ModelEvent;
import org.springfield.lou.screen.Screen;

public class SettingScreenEditController extends Html5Controller{
	
	public SettingScreenEditController() {
	}
	
	public void attach(String sel) {
		selector = sel;
		fillPage();
	}
	
	
	private void fillPage() {
		JSONObject data = new JSONObject();
		String currentstyle = model.getProperty("@station/style");	
		addStyleList(data,currentstyle);
		screen.get(selector).render(data);
		//screen.get("#station_waitscreen_appname").on("change","onAppNameChange", this);
		screen.get("#station_setting_style").on("change","onStyleChange", this);
		screen.get("#station_setting_delete").on("mouseup","station_setting_deleteconfirm","onDeleteStation", this);
		screen.get("#station_setting_copy").on("mouseup","onCopyStation", this);

	}
	
	public void onCopyStation(Screen s,JSONObject data) {
		System.out.println("onCopy to clipboard called");
		String copyurl = model.getNode("@station").getPath();
		model.setProperty("/browser['clipboard']/copystationname",model.getNode("@station").getProperty("name"));
		model.setProperty("/browser['clipboard']/copystationurl",copyurl);
	}
	
	public void onDeleteStation(Screen s,JSONObject data) {
		String confirm = (String)data.get("station_setting_deleteconfirm");
		if (confirm.equals("yes")) {
			model.deleteNode("@station");
			model.setProperty("@stationid","");
			model.notify("@room","change");
		}
	}
	
	public void onStyleChange(Screen s,JSONObject data) {
		 model.setProperty("@station/style",(String)data.get("value"));
		 model.notify("@exhibition","change");
	}
	
    private void addStyleList(JSONObject data,String currentstyle) {
		FSList list =new FSList();
		if (currentstyle==null || currentstyle.equals("")) currentstyle="neutral";
		FsNode node = new FsNode("option","1");
		node.setProperty("name",currentstyle.toLowerCase());
		list.addNode(node);
		node = new FsNode("option","2");
		node.setProperty("name","neutral");
		list.addNode(node);
		node = new FsNode("option","3");
		node.setProperty("name","soundandvision");
		list.addNode(node);
		node = new FsNode("option","4");
		node.setProperty("name","leuven");
		list.addNode(node);
		data.put("style",list.toJSONObject("en","name"));
    }
	
	
}
