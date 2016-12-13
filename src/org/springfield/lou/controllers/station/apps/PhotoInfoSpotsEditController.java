package org.springfield.lou.controllers.station.apps;

import java.util.Iterator;

import org.json.simple.JSONObject;
import org.springfield.fs.FsNode;
import org.springfield.fs.FsPropertySet;
import org.springfield.lou.controllers.Html5Controller;
import org.springfield.lou.model.ModelEvent;
import org.springfield.lou.screen.Screen;

public class PhotoInfoSpotsEditController extends Html5Controller{
	
	private String tab="content";
	private String valuelist="appeditor_content_url,appeditor_waitscreen_mode,appeditor_selection_mode";

	public PhotoInfoSpotsEditController() {
	}
	
	public void attach(String sel) {
		selector = sel;
		getVars();
		fillPage();
		model.onNotify("/screen['appsave']","onSaveWanted",this);
		model.onNotify("/screen/['appcancel']","onCancelWanted",this);
	}
	
	private void fillPage() {
		JSONObject data = new JSONObject();
		data.put(tab+"tab","true"); 
		data.put("username", model.getProperty("@username"));	
		data.put("domain",model.getProperty("mupop"));	
		data.put("exhibitionid",model.getProperty("@exhibitionid"));
		data.put("stationid",model.getProperty("@stationid"));
		data = memoryToData(data);
		
		screen.get(selector).render(data);
 		screen.get(".appmenu").on("mouseup","onAppMenu", this);
 		screen.get(selector).on("mouseleave",valuelist,"onLeaveMenu", this);
 		
 		String upid = "appeditor_content_fileupload";
 		setUploadSettings(upid);
 		screen.get("#appeditor_content_uploadbutton").on("mouseup",upid,"onFileUpload", this);
 		model.onPropertiesUpdate("/screen/upload/"+upid,"onUploadState",this);
	}
	
	public void onUploadState(ModelEvent e) {
		FsPropertySet ps = (FsPropertySet)e.target;
		String action = ps.getProperty("action");
		String progress = ps.getProperty("progress");
		screen.get("#appeditor_content_fileupload_feedback").html(action+" - "+progress);
		if (progress.equals("100")) {
			model.setProperty("/screen['data']/form['1']/appeditor_content_url",ps.getProperty("url"));
			fillPage();
			//screen.get("#appeditor_content_preview").html("<image width=\"100%\" height=\"100%\" src=\""+ps.getProperty("url")+"\" />");
		}
		
	}
	
	public void onFileUpload(Screen s,JSONObject data) {
	//	System.out.println("FILE UPLOAD !!"+data.toJSONString());
	}
	
	private void setUploadSettings(String upid) {
		model.setProperty("/screen/upload/"+upid+"/method","s3amazon");		
		model.setProperty("/screen/upload/"+upid+"/storagehost","https://s3-eu-west-1.amazonaws.com/");
		model.setProperty("/screen/upload/"+upid+"/bucketname","springfield-storage");
		model.setProperty("/screen/upload/"+upid+"/destpath","mupop/images/");
		model.setProperty("/screen/upload/"+upid+"/destname_prefix","upload_");
		model.setProperty("/screen/upload/"+upid+"/publicpath","https://s3-eu-west-1.amazonaws.com/");
		model.setProperty("/screen/upload/"+upid+"/destname_type","epoch");
		model.setProperty("/screen/upload/"+upid+"/filetype","image");
		model.setProperty("/screen/upload/"+upid+"/fileext","png");
		model.setProperty("/screen/upload/"+upid+"/checkupload","true");
		
		/* using scp 
		model.setProperty("/screen/upload/"+upid+"/method","scp");	
		model.setProperty("/screen/upload/"+upid+"/destpath","/usr/local/sites/storage.qandr.eu/images/");
		model.setProperty("/screen/upload/"+upid+"/pemfile","/home/springfield/.ssh/Noterikkey.pem");
		model.setProperty("/screen/upload/"+upid+"/destname_prefix","upload_");
		model.setProperty("/screen/upload/"+upid+"/destname_type","epoch");
		model.setProperty("/screen/upload/"+upid+"/storagehost","storage.qandr.eu");
		model.setProperty("/screen/upload/"+upid+"/storagename","ubuntu");
		model.setProperty("/screen/upload/"+upid+"/filetype","image");
		model.setProperty("/screen/upload/"+upid+"/fileext","png");
		model.setProperty("/screen/upload/"+upid+"/checkupload","true");
		model.setProperty("/screen/upload/"+upid+"/publicpath","http://storage.qandr.eu/images/");
		*/
	}
	
	
	private void getVars() {
		model.setProperty("/screen['data']/form['1']/appeditor_content_url",model.getProperty("@station/image/1/url"));
		model.setProperty("/screen['data']/form['1']/appeditor_waitscreen_mode",model.getProperty("@station/waitscreenmode"));
		model.setProperty("/screen['data']/form['1']/appeditor_selection_mode",model.getProperty("@station/selectionmode"));
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
	
	public void onAppMenu(Screen s,JSONObject data) {
		tab  = (String)data.get("id");
		saveToMemory(data);
		fillPage();
	}
	
	public void onLeaveMenu(Screen s,JSONObject data) {
		saveToMemory(data);
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
					//model.setProperty("@station/url", value);
					model.setProperty("@station/image/1/url", value);
				} else if (key.equals("appeditor_waitscreen_mode")) {
					model.setProperty("@station/waitscreenmode", value);
				} else if (key.equals("appeditor_selection_mode")) {
					model.setProperty("@station/selectionmode", value);
				}
			}
	
		}
		screen.get(selector).remove();
	}


	
}
