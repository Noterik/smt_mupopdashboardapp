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

public class ContentSelectEditController extends Html5Controller{
	
	public ContentSelectEditController() {
		
	}
	
	public void attach(String sel) {
		selector = sel;
		System.out.println("CONTENT EDIT SELECTOR="+selector);
		fillPage();
	}
	
	
	private void fillPage() {
		String currentapp = model.getProperty("@station/contentselect");
		String prefixdir = model.getProperty("@station/contentselect_content");
		model.setProperty("@contentrole",prefixdir);
		JSONObject data = getcontentselectAppList(currentapp);  // read the available aps to json
		addcontentselectPrefixList(data,prefixdir);
		addImages(data);

		screen.get(selector).render(data);
		screen.get("#station_contentselect_appname").on("change","onAppNameChange", this);
		screen.get("#station_contentselect_prefix").on("change","onPrefixChange", this);
		
		setUploadSettings("station_contentselect_imageupload");
		screen.get("#station_contentselect_imageuploadbutton").on("mouseup","station_contentselect_imageupload","onFileUpload", this);
		model.onPropertiesUpdate("/screen/upload/station_contentselect_imageupload","onUploadState",this);
		screen.get(".contentselect_deleteimage").on("mouseup","onDeleteImage", this);
		screen.get("#contentselect_wantedselect").on("change","onWantedSelect", this);
		}
	

	
	public void onDeleteImage(Screen s,JSONObject data) {
		String id=((String)data.get("id")).substring(25);
		Boolean result = model.deleteNode("@content/image/"+id);
		if (result) {
			fillPage();
			model.notify("@station","changed"); 
		}
	}
	
	public void onWantedSelect(Screen s,JSONObject data) {
		String id=((String)data.get("id")).substring(26);
		String value=(String)data.get("value");
		model.setProperty("@content/image/"+id+"/wantedselect",value);
	}
	
	public void onPrefixChange(Screen s,JSONObject data) {
		model.setProperty("@station/contentselect_content",(String)data.get("value"));
		fillPage();
		model.notify("@station","changed"); 
	}
	
	public void onAppNameChange(Screen s,JSONObject data) {
		model.setProperty("@station/contentselect",(String)data.get("value"));
		fillPage();
		model.notify("@station","changed"); 
	}
	
	private void addImages(JSONObject data) {
		FSList imagesList = model.getList("@images");
		JSONObject images = imagesList.toJSONObject("en","url,wantedselect");
		data.put("images",images);
	}
	
	   private void addcontentselectPrefixList(JSONObject data,String current) {
		    if (current==null || current.equals("")) {
		    	current = "contentselect";
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


	
    private JSONObject getcontentselectAppList(String currentapp) {
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
		node.setProperty("name","coverflow");
		node.setProperty("labelname","coverflow");
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
    		
    		// check if we already have a contentrole if not set it to contentselect
    		String contentrole = model.getProperty("@contentrole");
    		if (contentrole==null || contentrole.equals("")) {
    			model.setProperty("@station/contentselect_content","contentselect");
    			model.setProperty("@contentrole","contentselect");
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
		model.setProperty("@uploadid",upid);
		model.setProperty("@upload/method","s3amazon");		
		model.setProperty("@upload/storagehost","https://s3-eu-west-1.amazonaws.com/");
		model.setProperty("@upload/bucketname","springfield-storage");
		model.setProperty("@upload/destpath","mupop/images/");
		model.setProperty("@upload/destname_prefix","upload_");
		model.setProperty("@upload/publicpath","https://s3-eu-west-1.amazonaws.com/");
		model.setProperty("@upload/destname_type","epoch");
		model.setProperty("@upload/filetype","image");
		model.setProperty("@upload/fileext","png");
		model.setProperty("@upload/checkupload","true");
	}
	
}
