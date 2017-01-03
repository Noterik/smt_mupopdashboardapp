package org.springfield.lou.controllers.station.apps.photoexplore;

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

public class PhotoExploreMainAppController extends Html5Controller{
	
	String selecteditem;
	
	public PhotoExploreMainAppController() {
		
	}
	
	public void attach(String sel) {
		selector = sel;
		fillPage();
	}
	
	
	private void fillPage() {

		JSONObject data = new JSONObject();
		addItems(data);

		if (selecteditem!=null) {
			model.setProperty("@itemid",selecteditem);
			FsNode item=model.getNode("@item");
			System.out.println("ITEMNODE="+item.asXML());
			data.put("id", item.getId());
		}
		addImages(data);
		
		screen.get(selector).render(data);
		screen.get(".station_mainapp_item").on("mouseup","onEditItem", this);
		screen.get("#station_mainapp_newitem").on("mouseup","station_mainapp_newitemname","onNewItem", this);

		setUploadSettings("station_mainapp_newitem_imageupload");
		screen.get("#station_mainapp_newitem_imageuploadbutton").on("mouseup","station_mainapp_newitem_imageupload","onFileUpload", this);
		model.onPropertiesUpdate("/screen/upload/station_mainapp_newitem_imageupload","onUploadState",this);

	}
	
	public void onNewItem(Screen s,JSONObject data) {
		String newid=(String)data.get("station_mainapp_newitemname");
		FsNode newitem = new FsNode("item",newid);
		model.setProperty("@contentrole","mainapp");
		Boolean result=model.putNode("@items", newitem);
		if (result) {
			fillPage();
		}
	}
	
	public void onEditItem(Screen s,JSONObject data) {
		String itemid = (String)data.get("id");
		itemid = itemid.substring(20);
		selecteditem = itemid;
		fillPage();
	}
		
	
	private void addImages(JSONObject data) {
		FSList imagesList = model.getList("@itemimages");
		JSONObject images = imagesList.toJSONObject("en","url");
		data.put("images",images);
	}
	
	private void addItems(JSONObject data) {
		model.setProperty("@contentrole","mainapp");
		FSList itemsList = model.getList("@items");
		JSONObject items = itemsList.toJSONObject("en","url");
		FSList resultitems = new FSList();
		List<FsNode> nodes = itemsList.getNodes();
		if (nodes != null) {
			for (Iterator<FsNode> iter = nodes.iterator(); iter.hasNext();) {
				FsNode node = (FsNode) iter.next();
				if (selecteditem==null) selecteditem = node.getId();
				if (selecteditem.equals(node.getId())) {
					node.setProperty("classname","station_mainapp_itemselected");
				} else {
					node.setProperty("classname","station_mainapp_item");	
				}
				resultitems.addNode(node);
			}
		}
		data.put("items",resultitems.toJSONObject("en","classname,url"));
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
    		model.setDebug(true);
    		model.setProperty("@contentrole","mainapp");
    		boolean result = model.putNode("@item",imagenode);
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
