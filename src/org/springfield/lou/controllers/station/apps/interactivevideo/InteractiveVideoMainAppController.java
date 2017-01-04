package org.springfield.lou.controllers.station.apps.interactivevideo;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONObject;
import org.springfield.fs.FSList;
import org.springfield.fs.FsNode;
import org.springfield.fs.FsPropertySet;
import org.springfield.lou.controllers.Html5Controller;
import org.springfield.lou.model.ModelEvent;
import org.springfield.lou.screen.Screen;

public class InteractiveVideoMainAppController extends Html5Controller{
	
	public InteractiveVideoMainAppController() {
		
	}
	
	public void attach(String sel) {
		selector = sel;
		fillPage();
	}
	
	
	private void fillPage() {

		JSONObject data = new JSONObject();
		addVideos(data);
		
		screen.get(selector).render(data);
	
		setUploadSettings("station_mainapp_videoupload");
		screen.get("#station_mainapp_videouploadbutton").on("mouseup","station_mainapp_videoupload","onFileUpload", this);
		model.onPropertiesUpdate("/screen/upload/station_mainapp_videoupload","onUploadState",this);
		screen.get(".mainapp_deletevideo").on("mouseup","onDeleteVideo", this);
	}
	
	public void onDeleteVideo(Screen s,JSONObject data) {
		model.setProperty("@contentrole","mainapp");
		String id=(String)data.get("id");
		Boolean result = model.deleteNode("@content/video/"+id);
		if (result) {
			fillPage();
			model.notify("@station","changed"); 
		}
	}
	
	private void addVideos(JSONObject data) {
		model.setProperty("@contentrole","mainapp");
		FSList videosList = model.getList("@videos");
		JSONObject videos = videosList.toJSONObject("en","url");
		data.put("videos",videos);
	}
	

	
	public void onUploadState(ModelEvent e) {
		FsPropertySet ps = (FsPropertySet)e.target;
		String action = ps.getProperty("action");
		String progress = ps.getProperty("progress");
		if (progress!=null && progress.equals("100")) {
    		FsNode videonode = new FsNode("video",""+new Date().getTime());
    		videonode.setProperty("url",ps.getProperty("url"));
    		
    		// check if we already have a contentrole if not set it to waitscreen
    		model.setProperty("@contentrole","mainapp");
    		boolean result = model.putNode("@videos",videonode);
    		if (!result) {
    			System.out.println("COUNT NOT INSERT VIDEO");
    		} else {
    			System.out.println("VIDEO UPLOAD DONE");
    			model.notify("@station","changed"); 
    			fillPage();
    		}
    		
		}
	
	}

	public void onFileUpload(Screen s,JSONObject data) {
		System.out.println("FILE UPLOAD !!"+data.toJSONString());
	}
	
	private void setUploadSettings(String upid) {
		model.setProperty("/screen/upload/"+upid+"/method","s3amazon");		
		model.setProperty("/screen/upload/"+upid+"/storagehost","https://s3-eu-west-1.amazonaws.com/");
		model.setProperty("/screen/upload/"+upid+"/bucketname","springfield-storage");
		model.setProperty("/screen/upload/"+upid+"/destpath","mupop/videos/");
		model.setProperty("/screen/upload/"+upid+"/destname_prefix","upload_");
		model.setProperty("/screen/upload/"+upid+"/publicpath","https://s3-eu-west-1.amazonaws.com/");
		model.setProperty("/screen/upload/"+upid+"/destname_type","epoch");
		model.setProperty("/screen/upload/"+upid+"/filetype","video");
		model.setProperty("/screen/upload/"+upid+"/fileext","mp4");
		model.setProperty("/screen/upload/"+upid+"/checkupload","true");
	}
	

	
}
