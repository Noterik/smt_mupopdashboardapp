package org.springfield.lou.controllers.station.apps;

import java.util.Iterator;

import org.json.simple.JSONObject;
import org.springfield.fs.FsNode;
import org.springfield.fs.FsPropertySet;
import org.springfield.lou.controllers.Html5Controller;
import org.springfield.lou.model.ModelEvent;
import org.springfield.lou.screen.Screen;

public class InteractiveVideoEditController extends Html5Controller{
	
	private String tab="content";
	private String valuelist="appeditor_content_url";
	
	public InteractiveVideoEditController() {
		
	}
	
	public void attach(String sel) {
		selector = sel;
		System.out.println("SELECTOR="+selector);
		getVars();
		fillPage();
		model.onNotify("/screen['appsave']","onSaveWanted",this);
		model.onNotify("/screen/['appcancel']","onCancelWanted",this);
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
		System.out.println("STATIONID="+model.getProperty("@stationid"));
		data = memoryToData(data);
			
		screen.get(selector).render(data);
 		screen.get(".appmenu").on("mouseup",valuelist,"onAppMenu", this);
 		screen.get(selector).on("mouseleave",valuelist,"onLeaveMenu", this);
 		
 		String upid = "appeditor_content_fileupload";
 		setUploadSettings(upid);
 		screen.get("#appeditor_content_uploadbutton").on("mouseup",upid,"onFileUpload", this);
 		model.onPropertiesUpdate("/screen/upload/"+upid,"onUploadState",this);
 		
	}
	
	private void setUploadSettings(String upid) {
		model.setProperty("/screen/upload/"+upid+"/destpath","/usr/local/sites/storage.qandr.eu/images/");
		model.setProperty("/screen/upload/"+upid+"/pemfile","/home/springfield/.ssh/Noterikkey.pem");
		model.setProperty("/screen/upload/"+upid+"/destname_prefix","upload_");
		model.setProperty("/screen/upload/"+upid+"/destname_type","epoch");
		model.setProperty("/screen/upload/"+upid+"/storagehost","storage.qandr.eu");
		model.setProperty("/screen/upload/"+upid+"/storagename","ubuntu");
		model.setProperty("/screen/upload/"+upid+"/filetype","image");
		model.setProperty("/screen/upload/"+upid+"/fileext","png");
		model.setProperty("/screen/upload/"+upid+"/checkupload","true");
	}
	
	
	public void onUploadState(ModelEvent e) {
		FsPropertySet ps = (FsPropertySet)e.target;
		String action = ps.getProperty("action");
		System.out.println("UPLOAD ACTION="+action);
		
	}
	
	public void onFileUpload(Screen s,JSONObject data) {
	//	System.out.println("FILE UPLOAD !!"+data.toJSONString());
	}
	
	public void onAppMenu(Screen s,JSONObject data) {
		tab  = (String)data.get("id");
		saveToMemory(data);
		fillPage();
	}
	
	public void onLeaveMenu(Screen s,JSONObject data) {
		saveToMemory(data);
	}
	
	private JSONObject memoryToData(JSONObject data) {
		FsNode node = model.getNode("/screen['data']/form['1']");
		if (node!=null) {
			String[] checklist = valuelist.split(",");
			for (int i=0;i<checklist.length;i++) {
				String check = checklist[i];
				String value = node.getProperty(check);
				if (value!=null) data.put(check, value);
			}
		}
		return data;
	}
	
	private void saveToMemory(JSONObject data) {
		FsNode node = model.getNode("/screen['data']/form['1']");
		if (node==null) {
			node = new FsNode("form","1");
			model.putNode("/screen['data']", node);
		}
		String[] checklist = valuelist.split(",");
		for (int i=0;i<checklist.length;i++) {
			String check = checklist[i];
			Object o = data.get(check);
			if (o!=null) {
				String value = (String)o;
				String oldvalue = node.getProperty(check);
				if (oldvalue==null || !value.equals(oldvalue)) {
					node.setProperty(check, value);
				}
			}
		}
	}
	
	public void onCancelWanted(ModelEvent e) {
		screen.get(selector).remove();
	}
	
	public void onSaveWanted(ModelEvent e) {
		FsNode node = model.getNode("/screen['data']/form['1']");
		if (node!=null) {
			for(Iterator<String> iter = node.getKeys(); iter.hasNext();) {
				String key = iter.next();
				String value = node.getProperty(key);
				if (key.equals("appeditor_content_url")) {
					model.setProperty("@station/url", value);
				}
			}
	
		}
		screen.get(selector).remove();
	}
	
	
}
