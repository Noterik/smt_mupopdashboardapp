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

public class WaitScreenEditController extends Html5Controller{
	
	public WaitScreenEditController() {
		
	}
	
	public void attach(String sel) {
		selector = sel;
		System.out.println("WAISCREEN SELECTOR="+selector);
		fillPage();
	}
	
	
	private void fillPage() {
		String currentapp = model.getProperty("@station/waitscreen");
		String prefixdir = model.getProperty("@station/waitscreen_content");
		String languagecode = model.getProperty("@languagecode");
		if (languagecode==null) {
			languagecode = "en";
			model.setProperty("@languagecode","en");
		}
		model.setProperty("@contentrole",prefixdir);
		System.out.println("WAITSCREEN APP="+currentapp+" "+prefixdir);
		JSONObject data = getWaitScreenAppList(currentapp);  // read the available aps to json
		addWaitScreenPrefixList(data,prefixdir);
		addWaitScreenLanguagesList(data,languagecode);
		addImages(data);
		data.put("title", model.getProperty("@station/"+languagecode+"_title"));

		screen.get(selector).render(data);
		screen.get("#station_waitscreen_appname").on("change","onAppNameChange", this);
		screen.get("#station_waitscreen_prefix").on("change","onPrefixChange", this);
		screen.get("#station_waitscreen_language").on("change","onLanguageChange", this);
		screen.get("#station_waitscreen_title").on("change","onTitleChange", this);
		
		setUploadSettings("station_waitscreen_imageupload");
		screen.get("#station_waitscreen_imageuploadbutton").on("mouseup","station_waitscreen_imageupload","onFileUpload", this);
		model.onPropertiesUpdate("/screen/upload/station_waitscreen_imageupload","onUploadState",this);
		screen.get(".waitscreen_deleteimage").on("mouseup","onDeleteImage", this);
		screen.get("#station_waitscreen_delete").on("mouseup","station_waitscreen_deleteconfirm","onDeleteStation", this);
		}
	
	public void onDeleteStation(Screen s,JSONObject data) {
		String confirm = (String)data.get("station_waitscreen_deleteconfirm");
		if (confirm.equals("yes")) {
			model.deleteNode("@station");
			model.setProperty("@stationid","");
			model.notify("@room","change");
		}
	}

	public void onTitleChange(Screen s,JSONObject data) {
		String title = (String)data.get("value");
		String code = model.getProperty("@languagecode");
		System.out.println("title="+title+" code="+code);
		model.setProperty("@station/"+code+"_title",title);
		fillPage();
		model.notify("@station","changed"); 
	}
	public void onLanguageChange(Screen s,JSONObject data) {
		model.setProperty("@languagecode",(String)data.get("value"));
		fillPage();
	}
	
	public void onDeleteImage(Screen s,JSONObject data) {
		String id=(String)data.get("id");
		Boolean result = model.deleteNode("@content/image/"+id);
		if (result) {
			fillPage();
			model.notify("@station","changed"); 
		}
	}
	
	public void onPrefixChange(Screen s,JSONObject data) {
		model.setProperty("@station/waitscreen_content",(String)data.get("value"));
		fillPage();
		model.notify("@station","changed"); 
	}
	
	public void onAppNameChange(Screen s,JSONObject data) {
		model.setProperty("@station/waitscreen",(String)data.get("value"));
		fillPage();
		model.notify("@station","changed"); 
	}
	
	private void addImages(JSONObject data) {
		FSList imagesList = model.getList("@images");
		JSONObject images = imagesList.toJSONObject("en","url");
		data.put("images",images);
	}
	
	   private void addWaitScreenPrefixList(JSONObject data,String current) {
		    if (current==null || current.equals("")) {
		    	current = "waitscreen";
		    }
			FSList list =new FSList();
			FsNode node = new FsNode("prefix","1");
			node.setProperty("name",current.toLowerCase());
			list.addNode(node);
			node = new FsNode("prefix","2");
			node.setProperty("name","waitscreen");
			list.addNode(node);
			node = new FsNode("prefix","3");
			node.setProperty("name","contentselect");
			list.addNode(node);
			node = new FsNode("prefix","4");
			node.setProperty("name","mainapp");
			list.addNode(node);
			data.put("prefix",list.toJSONObject("en","name"));
	    }
	   
	   private void addWaitScreenLanguagesList(JSONObject data,String currentcode) {
		    String current="";
		    if (currentcode==null || currentcode.equals("") || currentcode.equals("en")) {
		    	current = "english";
		    	currentcode = "en";
		    } else if (currentcode.equals("nl")) {
		    	current = "dutch";
		    	currentcode = "nl";
		    } else if (currentcode.equals("de")) {
		    	current = "german";
		    	currentcode = "de";  
		    }
			FSList list =new FSList();
			FsNode node = new FsNode("language","1");
			node.setProperty("name",current);
			node.setProperty("code",currentcode);
			list.addNode(node);
			node = new FsNode("language","2");
			node.setProperty("name","english");
			node.setProperty("code","en");
			list.addNode(node);
			node = new FsNode("language","3");
			node.setProperty("name","dutch");
			node.setProperty("code","nl");
			list.addNode(node);
			node = new FsNode("language","4");
			node.setProperty("name","german");
			node.setProperty("code","de");
			list.addNode(node);
			data.put("languages",list.toJSONObject("en","name,code"));
	    }

	
    private JSONObject getWaitScreenAppList(String currentapp) {
    	if (currentapp==null) currentapp="none";
		FSList list =new FSList();
		FsNode node = new FsNode("apps","1");
		node.setProperty("name",currentapp.toLowerCase());
		node.setProperty("labelname",currentapp);
		list.addNode(node);
		node = new FsNode("apps","2");
		node.setProperty("name","none");
		node.setProperty("labelname","None");
		list.addNode(node);
		node = new FsNode("apps","3");
		node.setProperty("name","static");
		node.setProperty("labelname","static");
		list.addNode(node);
		node = new FsNode("apps","4");
		node.setProperty("name","kenburn");
		node.setProperty("labelname","kenburn");
		list.addNode(node);
		return list.toJSONObject("en","name,labelname");
    }
    


	public void onUploadState(ModelEvent e) {
		FsPropertySet ps = (FsPropertySet)e.target;
		String action = ps.getProperty("action");
		String progress = ps.getProperty("progress");
		if (progress!=null && progress.equals("100")) {
			//screen.get("#appeditor_content_preview").html("<image width=\"100%\" height=\"100%\" src=\""+ps.getProperty("url")+"\" />");
			//System.out.println("UPLOAD DONE SHOULD CREATE NODE !");
    		FsNode imagenode = new FsNode("image",""+new Date().getTime());
    		imagenode.setProperty("url",ps.getProperty("url"));
    		
    		// check if we already have a contentrole if not set it to waitscreen
    		String contentrole = model.getProperty("@contentrole");
    		if (contentrole==null || contentrole.equals("")) {
    			model.setProperty("@station/waitscreen_content","waitscreen");
    			model.setProperty("@contentrole","waitscreen");
    		}
    		boolean result = model.putNode("@content",imagenode);
    		if (!result) {
    			System.out.println("COUNT NOT INSERT IMAGE");
    		} else {
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
		model.setProperty("/screen/upload/"+upid+"/destpath","mupop/images/");
		model.setProperty("/screen/upload/"+upid+"/destname_prefix","upload_");
		model.setProperty("/screen/upload/"+upid+"/publicpath","https://s3-eu-west-1.amazonaws.com/");
		model.setProperty("/screen/upload/"+upid+"/destname_type","epoch");
		model.setProperty("/screen/upload/"+upid+"/filetype","image");
		model.setProperty("/screen/upload/"+upid+"/fileext","png");
		model.setProperty("/screen/upload/"+upid+"/checkupload","true");
	}
	
}
