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
package org.springfield.lou.controllers.exhibitioninfo;


import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springfield.fs.FSList;
import org.springfield.fs.FSListManager;
import org.springfield.fs.Fs;
import org.springfield.fs.FsNode;
import org.springfield.fs.FsPropertySet;
import org.springfield.lou.controllers.Html5Controller;
import org.springfield.lou.controllers.dashboard.DashboardController;
import org.springfield.lou.controllers.room.RoomController;
import org.springfield.lou.controllers.roominfo.RoomInfoController;
import org.springfield.lou.controllers.roomselector.RoomSelectorController;
import org.springfield.lou.model.ModelEvent;
import org.springfield.lou.screen.Screen;

public class ExhibitionInfoController extends Html5Controller {
	
	String newstationpath;
	FsNode roomnode; 
	String code=null;
	String hid;
	String feedback=null;
	
	
	/**
	 * One of the core controllers shows the layout of a room with access to many
	 * elements of the dashboard.
	 */
	public ExhibitionInfoController() {
	}
	
	/**
	 * moment this controller gets attached to target screen on given id
	 * 
	 * @see org.springfield.lou.controllers.Html5Controller#attach(java.lang.String)
	 */
	public void attach(String sel) {
		selector = sel; // set the id for later use.
		fillPage();
		model.onPropertyUpdate("/shared/mupop/hidrequest","onHidRequest",this);
	}
	
	public void onHidRequest(ModelEvent e) {
		FsNode node = e.getTargetFsNode();
		code = node.getProperty("hidrequest");

		// use code to reflect back to only that screen
		fillPage();
		
		
	}
	
    public void onPairButton(Screen s,JSONObject data) {
    	String givencode = (String)data.get("exhibitioninfo_code");
    	hid = (String)data.get("exhibitioninfo_hid");
    	if (givencode.equals(code)) {
    		if (model.getNode("/domain/mupop/config/hids/hid/"+hid)!=null) {
        		feedback = "Hardware name already in use";
        		fillPage();
        		return;
    		}
    		
    		
    		FsNode hidnode = new FsNode("hid",hid);
    		hidnode.setProperty("username","");
    		hidnode.setProperty("exhibitionid","");
    		hidnode.setProperty("stationid","");
    		boolean result = model.putNode("/domain/mupop/config/hids/",hidnode);
    		if (result) {
    			model.setProperty("/shared['mupop']/hidresponse"+code,hid);
    			code = null;
        		feedback = "Hardware pairing done : "+hid+" now available for mapping";
    		} else {
    			System.out.println("Could not insert node "+hidnode.asXML());
    		}	
    	} else {
    		feedback = "Wrong pairing code given";
    	}
		fillPage();
    }
	
	
	
	/**
	 * fill our space on our screen
	 */
	private void fillPage() {
		
		JSONObject data = new JSONObject(); // create json object for the clients render call
		data.put("username",model.getProperty("@username")); // add username so we can display it
		data.put("exhibitionid",model.getProperty("@exhibitionid")); // add exhibition idea for use in forms
		if (code!=null) data.put("code", code);
		if (feedback!=null) data.put("feedback", feedback);
		FSList list = model.getList("/domain/mupop/config/hids/hid");
		if (list!=null) {
			JSONObject paired = list.toJSONObject("en","stationname");
			data.put("paired",paired);
		}
		
		String currentlanguageselecy = model.getProperty("@exhibition/languageselect");
		addLanguageSelectList(data,currentlanguageselecy);
		String currentstationselect = model.getProperty("@exhibition/stationselect");
		addStationSelectList(data,currentstationselect);
		String currentshowurl = model.getProperty("@exhibition/showurl");	
		addShowUrlList(data,currentshowurl);
		String currentstyle = model.getProperty("@exhibition/style");	
		addStyleList(data,currentstyle);
		String currentaudiocheck = model.getProperty("@exhibition/audiocheck");	
		addAudiocheckList(data,currentaudiocheck);
		String currentlanguage = model.getProperty("@exhibition/language");	
		addLanguageList(data,currentlanguage);
		data.put("availablelanguages",model.getProperty("@exhibition/availablelanguages"));
		
		
		String audiochecksample = model.getProperty("@exhibition/audiochecksample");
		if (audiochecksample!=null && !audiochecksample.equals("")) {
			data.put("audiochecksample",audiochecksample);
		}
		
		screen.get(selector).render(data); // now we have all data give it to client and render using mustache	
		
		setUploadSettings("exhibitioninfo_audioupload");
		screen.get("#exhibitioninfo_audiouploadbutton").on("mouseup","exhibitioninfo_audioupload","onFileUpload", this);
		model.onPropertiesUpdate("/screen/upload/exhibitioninfo_audioupload","onUploadState",this);
		
 		screen.get("#exhibitioninfo_donebutton").on("mouseup","onDoneButton", this);
 		screen.get("#exhibitioninfo_pairbutton").on("mouseup","exhibitioninfo_code,exhibitioninfo_hid","onPairButton", this);
		screen.get("#exhibitioninfo_deletehidbutton").on("mouseup","exhibitioninfo_hids_select","onHidDeleteButton", this);
		screen.get("#exhibitioninfo_jumpersubmit").on("mouseup","exhibitioninfo_jumper","onJumperSubmit", this);
		screen.get("#exhibitioninfo_languageselect").on("change","onLanguageSelectChange", this);
		screen.get("#exhibitioninfo_stationselect").on("change","onStationSelectChange", this);
		screen.get("#exhibitioninfo_delete").on("mouseup","exhibitioninfo_deleteconfirm","onDeleteExhibition", this);
		screen.get("#exhibitioninfo_showurl").on("change","onShowUrlChange", this);
		screen.get("#exhibitioninfo_style").on("change","onStyleChange", this);
		screen.get("#exhibitioninfo_audiocheck").on("change","onAudiocheckChange", this);
		screen.get("#exhibitioninfo_language").on("change","onLanguageChange", this);
		screen.get("#exhibitioninfo_availablelanguages").on("change","onAvailableLanguagesChange", this);


	}
	
	public void onDeleteExhibition(Screen s,JSONObject data) {
		String confirm = (String)data.get("exhibitioninfo_deleteconfirm");
		if (confirm.equals("yes")) {
			model.deleteNode("@exhibition");
			model.setProperty("@exhibitionid","");
	    	screen.get(selector).remove(); // remove us from the screen.
	    	screen.get("#room").remove(); // remove us from the screen.
			screen.get("#content").append("div","dashboard",new DashboardController()); // 
		}
	}
	
	public void onAvailableLanguagesChange(Screen s,JSONObject data) {
	 model.setProperty("@exhibition/availablelanguages",(String)data.get("value"));
	 model.notify("@exhibition","change");
	}
	
	public void onLanguageChange(Screen s,JSONObject data) {
		 model.setProperty("@exhibition/language",(String)data.get("value"));
		 model.notify("@exhibition","change");
	}
	
	public void onShowUrlChange(Screen s,JSONObject data) {
		 model.setProperty("@exhibition/showurl",(String)data.get("value"));
		 model.notify("@exhibition","change");
	}
	
	public void onStyleChange(Screen s,JSONObject data) {
		 model.setProperty("@exhibition/style",(String)data.get("value"));
		 model.notify("@exhibition","change");
	}
	
	public void onAudiocheckChange(Screen s,JSONObject data) {
		 model.setProperty("@exhibition/audiocheck",(String)data.get("value"));
		 model.notify("@exhibition","change");
	}
	
	public void onLanguageSelectChange(Screen s,JSONObject data) {
		 model.setProperty("@exhibition/languageselect",(String)data.get("value"));
	}
	
	public void onStationSelectChange(Screen s,JSONObject data) {
		 model.setProperty("@exhibition/stationselect",(String)data.get("value"));
		 model.notify("@exhibition","change");
	}
	
	
	public void onJumperSubmit(Screen s,JSONObject data) {
		System.out.println("Jumper Submit="+data.toJSONString());
		feedback = "Jumper set";
		fillPage();
	}
	
	
	public void onHidDeleteButton(Screen s,JSONObject data) {
		JSONArray hids = (JSONArray)data.get("exhibitioninfo_hids_select");
		String hid = (String)hids.get(0);
		if (hid!=null && !hid.equals("")) {
    		boolean result = Fs.deleteNode("/domain/mupop/config/hids/hid/"+hid);
    		fillPage();
		}
	}
	
    public void onDoneButton(Screen s,JSONObject data) {
       	screen.get("#content").append("div","room",new RoomController()); // if user wanted a old exhibition lets open the default room for it
		screen.get(selector).remove();
    }
    
    private void addLanguageSelectList(JSONObject data,String currentapp) {
		FSList list =new FSList();
		if (currentapp==null || currentapp.equals("")) currentapp="none";
		FsNode node = new FsNode("option","1");
		node.setProperty("name",currentapp.toLowerCase());
		list.addNode(node);
		node = new FsNode("option","2");
		node.setProperty("name","none");
		list.addNode(node);
		node = new FsNode("option","3");
		node.setProperty("name","default");
		list.addNode(node);
		node = new FsNode("option","4");
		node.setProperty("name","flags");
		list.addNode(node);
		data.put("languageselect",list.toJSONObject("en","name"));
    }
    
    private void addShowUrlList(JSONObject data,String currentapp) {
		FSList list =new FSList();
		if (currentapp==null || currentapp.equals("")) currentapp="false";
		FsNode node = new FsNode("option","1");
		node.setProperty("name",currentapp.toLowerCase());
		list.addNode(node);
		node = new FsNode("option","2");
		node.setProperty("name","false");
		list.addNode(node);
		node = new FsNode("option","3");
		node.setProperty("name","true");
		list.addNode(node);
		data.put("urlchange",list.toJSONObject("en","name"));
    }
    
    private void addAudiocheckList(JSONObject data,String currentapp) {
		FSList list =new FSList();
		if (currentapp==null || currentapp.equals("")) currentapp="false";
		FsNode node = new FsNode("option","1");
		node.setProperty("name",currentapp.toLowerCase());
		list.addNode(node);
		node = new FsNode("option","2");
		node.setProperty("name","false");
		list.addNode(node);
		node = new FsNode("option","3");
		node.setProperty("name","true");
		list.addNode(node);
		data.put("audiocheck",list.toJSONObject("en","name"));
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
    
    private void addLanguageList(JSONObject data,String currentlanguage) {
		FSList list =new FSList();
		if (currentlanguage==null || currentlanguage.equals("")) currentlanguage="en";
		FsNode node = new FsNode("option","1");
		node.setProperty("name",currentlanguage.toLowerCase());
		list.addNode(node);
		node = new FsNode("option","2");
		node.setProperty("name","en");
		list.addNode(node);
		node = new FsNode("option","3");
		node.setProperty("name","nl");
		list.addNode(node);
		data.put("language",list.toJSONObject("en","name"));
    }
    
    private void addStationSelectList(JSONObject data,String currentapp) {
  	FSList list =new FSList();
		if (currentapp==null || currentapp.equals("")) currentapp="none";
		FsNode node = new FsNode("option","1");
		node.setProperty("name",currentapp.toLowerCase());
		list.addNode(node);
		node = new FsNode("option","2");
		node.setProperty("name","none");
		list.addNode(node);
		node = new FsNode("option","3");
		node.setProperty("name","listview");
		list.addNode(node);
		node = new FsNode("option","4");
		node.setProperty("name","codeselect");
		list.addNode(node);
		data.put("stationselect",list.toJSONObject("en","name"));
    }
    
	public void onUploadState(ModelEvent e) {
		System.out.println("UPLOAD ="+e);
		FsPropertySet ps = (FsPropertySet)e.target;
		String action = ps.getProperty("action");
		String progress = ps.getProperty("progress");
		if (progress!=null && progress.equals("100")) {
			model.setProperty("@exhibition/audiochecksample",ps.getProperty("url"));
 		}
	}
    
	public void onFileUpload(Screen s,JSONObject data) {
		System.out.println("FILE UPLOAD !!"+data.toJSONString());
	}
	
	private void setUploadSettings(String upid) {
		model.setProperty("@uploadid",upid);
		model.setProperty("@upload/method","s3amazon");	
		model.setProperty("@upload/storagehost","https://s3-eu-west-1.amazonaws.com/");
		model.setProperty("@upload/bucketname","springfield-storage");
		model.setProperty("@upload/destpath","mupop/audios/");
		model.setProperty("@upload/destname_prefix","upload_");
		model.setProperty("@upload/publicpath","https://s3-eu-west-1.amazonaws.com/");
		model.setProperty("@upload/destname_type","epoch");
		model.setProperty("@upload/filetype","audio");
		model.setProperty("@upload/fileext","mp3");
		model.setProperty("@upload/checkupload","true");
	}
	
 	 
}
