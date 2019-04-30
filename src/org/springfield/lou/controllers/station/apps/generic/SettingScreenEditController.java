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
		
		String waitscreenlogo = model.getProperty("@station/content['waitscreen']/waitscreenlogo");
		if (waitscreenlogo!=null && !waitscreenlogo.equals("")) {
			data.put("waitscreenlogo",waitscreenlogo);
		}
		
		String applogoleft = model.getProperty("@station/content['contentselect']/applogoleft");
		if (applogoleft!=null && !applogoleft.equals("")) {
			data.put("applogoleft",applogoleft);
		}
		
		String applogoright = model.getProperty("@station/content['contentselect']/applogoright");
		if (applogoright!=null && !applogoright.equals("")) {
			data.put("applogoright",applogoright);
		}
		
		String themecolor1 = model.getProperty("@station/content['contentselect']/themecolor1");
		if (themecolor1!=null && !themecolor1.equals("")) {
			data.put("themecolor1",themecolor1);
		}
		
		
		screen.get(selector).render(data);
		//screen.get("#station_waitscreen_appname").on("change","onAppNameChange", this);
		screen.get("#station_setting_style").on("change","onStyleChange", this);
		screen.get("#station_setting_delete").on("mouseup","station_setting_deleteconfirm","onDeleteStation", this);
		screen.get("#station_setting_copy").on("mouseup","onCopyStation", this);
		screen.get("#station_setting_themecolor1").on("change","onThemeColor1", this);

		setUploadSettings("station_setting_waitscreenlogoupload");
		screen.get("#station_setting_waitscreenlogouploadbutton").on("mouseup","station_setting_waitscreenlogoupload","onFile1Upload", this);
		model.onPropertiesUpdate("/screen/upload/station_setting_waitscreenlogoupload","onUpload1State",this);

		setUploadSettings("station_setting_applogoleftupload");
		screen.get("#station_setting_applogoleftuploadbutton").on("mouseup","station_setting_applogoleftupload","onFile2Upload", this);
		model.onPropertiesUpdate("/screen/upload/station_setting_applogoleftupload","onUpload2State",this);

		setUploadSettings("station_setting_applogorightupload");
		screen.get("#station_setting_applogorightuploadbutton").on("mouseup","station_setting_applogorightupload","onFile3Upload", this);
		model.onPropertiesUpdate("/screen/upload/station_setting_applogorightupload","onUpload3State",this);

		
		
	}
	
	public void onThemeColor1(Screen s,JSONObject data) {
		String themecolor1 = (String)data.get("value");
		model.setProperty("@station/content['contentselect']/themecolor1",themecolor1);
		fillPage();

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
    
	public void onUpload1State(ModelEvent e) {
		FsPropertySet ps = (FsPropertySet)e.target;
		String action = ps.getProperty("action");
		String progress = ps.getProperty("progress");
		if (progress!=null && progress.equals("100")) {
			model.setProperty("@station/content['waitscreen']/waitscreenlogo",ps.getProperty("url"));
			fillPage();
		}
	}
	
	public void onUpload2State(ModelEvent e) {
		System.out.println("UPLOAD2");
		FsPropertySet ps = (FsPropertySet)e.target;
		String action = ps.getProperty("action");
		String progress = ps.getProperty("progress");
		if (progress!=null && progress.equals("100")) {
			model.setProperty("@station/content['contentselect']/applogoleft",ps.getProperty("url"));
			fillPage();
		}
	}
	
	public void onUpload3State(ModelEvent e) {
		FsPropertySet ps = (FsPropertySet)e.target;
		String action = ps.getProperty("action");
		String progress = ps.getProperty("progress");
		if (progress!=null && progress.equals("100")) {
			model.setProperty("@station/content['contentselect']/applogoright",ps.getProperty("url"));
			fillPage();
		}
	}
	
	
    
	public void onFile1Upload(Screen s,JSONObject data) {
		System.out.println("FILE UPLOAD 1 !!"+data.toJSONString());
	}
	
	public void onFile2Upload(Screen s,JSONObject data) {
		System.out.println("FILE UPLOAD 2 !!"+data.toJSONString());
	}
	
	public void onFile3Upload(Screen s,JSONObject data) {
		System.out.println("FILE UPLOAD 3 !!"+data.toJSONString());
	}
	
	private void setUploadSettings(String upid) {
		model.setProperty("@uploadid",upid);
		model.setProperty("@upload/method","s3amazon");		
		model.setProperty("@upload/storagehost","https://s3-eu-west-1.amazonaws.com/");
		model.setProperty("@upload/bucketname","springfield-storage");
		model.setProperty("@upload/destpath","mupop/images/");
		model.setProperty("@upload/destname_prefix","upload_");
		model.setProperty("@upload/publicpath","https://s3-eu-west-1.amazonaws.com/");
		model.setProperty("@upload/destname_type","epoch");
		model.setProperty("@upload/filetype","image");
		model.setProperty("@upload/fileext","png,jpg,jpeg,PNG,JPG,JPEG");
		model.setProperty("@upload/checkupload","true");
	}
	

	
}
