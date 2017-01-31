package org.springfield.lou.controllers.station.apps.photoinfospots;

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

public class PhotoInfoSpotsMainAppController extends Html5Controller{
	
	String selecteditem;
	String selectedmask;
	
	public PhotoInfoSpotsMainAppController() {
		
	}
	
	public void attach(String sel) {
		selector = sel;
		fillPage();
	}
	
	
	private void fillPage() {

		JSONObject data = new JSONObject();
		addItems(data);
		System.out.println("SELECTEDITEM="+selecteditem);
		if (selecteditem!=null) {
			model.setProperty("@contentrole","mainapp");
			model.setProperty("@itemid",selecteditem);
			FsNode item=model.getNode("@item");
			data.put("selecteditem", item.getId());
			String voiceover = model.getProperty("@item/voiceover");
			if (voiceover!=null && !voiceover.equals("")) {
				data.put("voiceover",voiceover);
			}
			data.put("url",model.getProperty("@item/url"));
		}
		//addImageRenderOptions(data,model.getProperty("@item/renderoption"));
		//addImages(data);
		addItemMasks(data);
		
		if (selectedmask!=null) {
			model.setProperty("@itemid",selecteditem);
			model.setProperty("@itemmaskid",selectedmask);
			System.out.println("SELECTEDITEM="+selecteditem+" "+selectedmask);
			FsNode itemmask=model.getNode("@itemmask");
			System.out.println("MASKNODE="+itemmask.asXML());
			data.put("maskurl", itemmask.getProperty("maskurl"));
			data.put("audiourl", itemmask.getProperty("audiourl"));
			data.put("selecteditemmask", itemmask.getId());
		}
		
		screen.get(selector).render(data);
		screen.get(".station_mainapp_item").on("mouseup","onEditItem", this);
		screen.get(".station_mainapp_itemselected").on("mouseup","onEditItem", this);
		screen.get("#station_mainapp_newitem").on("mouseup","station_mainapp_newitemname","onNewItem", this);

		setUploadAudioSettings("station_mainapp_item_editmaskaudioupload");
		screen.get("#station_mainapp_item_editmaskaudiouploadbutton").on("mouseup","station_mainapp_item_editmaskaudioupload","onAudioFileUpload", this);
		model.onPropertiesUpdate("/screen/upload/station_mainapp_item_editmaskaudioupload","onAudioUploadState",this);
		
		
		setUploadMaskSettings("station_mainapp_item_editmaskurlupload");
		screen.get("#station_mainapp_item_editmaskurluploadbutton").on("mouseup","station_mainapp_item_editmaskurlupload","onFileMaskUpload", this);
		model.onPropertiesUpdate("/screen/upload/station_mainapp_item_editmaskurlupload","onUploadMaskState",this);
		

		setUploadMainSettings("station_mainapp_item_editurlupload");
		screen.get("#station_mainapp_item_editurluploadbutton").on("mouseup","station_mainapp_item_editurlupload","onFileMainImageUpload", this);
		model.onPropertiesUpdate("/screen/upload/station_mainapp_item_editurlupload","onUploadMainImageState",this);
		screen.get("#station_mainapp_deleteitem").on("mouseup","station_mainapp_deleteitemconfirm","onDeleteItem", this);
	
		screen.get("#station_mainapp_edititem_renderoptions").on("change","onRenderOptionChange", this);
		screen.get("#station_mainapp_item_newmask").on("mouseup","station_mainapp_item_newmaskname","onNewItemMask", this);
		screen.get(".station_mainapp_maskselected").on("mouseup","onEditItemMask", this);
		screen.get(".station_mainapp_mask").on("mouseup","onEditItemMask", this);
	
	}
	
	public void onRenderOptionChange(Screen s,JSONObject data) {
		model.setProperty("@item/renderoption",(String)data.get("value"));
		fillPage();
		model.notify("@station","changed"); 
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
	
	public void onEditItemMask(Screen s,JSONObject data) {
		String itemmaskid = (String)data.get("id");
		itemmaskid = itemmaskid.substring(25);
		System.out.println("WHOOOO="+itemmaskid);
		selectedmask = itemmaskid;
		fillPage();
	}
	
	public void onDeleteItem(Screen s,JSONObject data) {
		String confirm = (String)data.get("station_mainapp_deleteitemconfirm");
		if (confirm.equals("yes")) {
			model.deleteNode("@item");
			selecteditem = null;
			fillPage();
		}
	}
		
	
	public void onNewItemMask(Screen s,JSONObject data) {
		String newid=(String)data.get("station_mainapp_item_newmaskname");
		FsNode newitemmask = new FsNode("mask",newid);
		model.setProperty("@contentrole","mainapp");
		Boolean result=model.putNode("@item", newitemmask);
		if (result) {
			fillPage();
		}
	}
	
	public void onEditItem(Screen s,JSONObject data) {
		System.out.println("ITEM SELECT="+data.toJSONString());
		String itemid = (String)data.get("id");
		itemid = itemid.substring(20);
		selecteditem = itemid;
		selectedmask = null;
		fillPage();
	}
		
	private void addItemMasks(JSONObject data) {
		model.setProperty("@contentrole","mainapp");
		FSList masksList = model.getList("@item/mask");
		FSList resultitems = new FSList();
		List<FsNode> nodes =masksList.getNodes();
		if (nodes != null) {
			for (Iterator<FsNode> iter = nodes.iterator(); iter.hasNext();) {
				FsNode node = (FsNode) iter.next();
				if (selectedmask==null) selectedmask = node.getId();
				if (selectedmask.equals(node.getId())) {
					node.setProperty("classname","station_mainapp_maskselected");
				} else {
					node.setProperty("classname","station_mainapp_mask");	
				}
				resultitems.addNode(node);
			}
		}
		data.put("masks",resultitems.toJSONObject("en","classname,maskurl,audiourl"));
	}
	
	
	   private void addImageRenderOptions(JSONObject data,String current) {
		    if (current==null || current.equals("")) {
		    	current = "none";
		    }
			FSList list =new FSList();
			FsNode node = new FsNode("option","1");
			node.setProperty("name",current.toLowerCase());
			list.addNode(node);
			node = new FsNode("option","2");
			node.setProperty("name","small");
			list.addNode(node);
			node = new FsNode("option","3");
			node.setProperty("name","medium");
			list.addNode(node);
			node = new FsNode("option","4");
			node.setProperty("name","large");
			list.addNode(node);
			node = new FsNode("option","5");
			node.setProperty("name","super");
			list.addNode(node);
			node = new FsNode("option","6");
			node.setProperty("name","nolimit");
			list.addNode(node);
			node = new FsNode("option","7");
			node.setProperty("name","thumbnail");
			list.addNode(node);
			node = new FsNode("option","8");
			node.setProperty("name","none");
			list.addNode(node);
			data.put("renderoptions",list.toJSONObject("en","name"));
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
	
	public void onFileMainImageUpload(Screen s,JSONObject data) {
		System.out.println("upload of main image wanted");
	}
	
	
	public void onFileMaskUpload(Screen s,JSONObject data) {
		System.out.println("upload of mask wanted");
	}
	
	public void onUploadMaskState(ModelEvent e) {
		FsPropertySet ps = (FsPropertySet)e.target;
		String action = ps.getProperty("action");
		String progress = ps.getProperty("progress");
		if (progress!=null && progress.equals("100")) {
    		model.setProperty("@itemmask/maskurl",ps.getProperty("url"));
    		model.notify("@station","changed"); 
    		fillPage();
    		
		}
	
	}
	
	public void onUploadMainImageState(ModelEvent e) {
		System.out.println("MAIN IMAGE UPLOADED!!");
		FsPropertySet ps = (FsPropertySet)e.target;
		String action = ps.getProperty("action");
		String progress = ps.getProperty("progress");
		if (progress!=null && progress.equals("100")) {
    		model.setProperty("@item/url",ps.getProperty("url"));
    		model.notify("@station","changed"); 
    		fillPage();
		}
	}

	public void onFileUpload(Screen s,JSONObject data) {
		System.out.println("FILE UPLOAD !!"+data.toJSONString());
	}
	
	public void onAudioUploadState(ModelEvent e) {
		FsPropertySet ps = (FsPropertySet)e.target;
		String action = ps.getProperty("action");
		String progress = ps.getProperty("progress");
		if (progress!=null && progress.equals("100")) {
    		model.setProperty("@itemmask/audiourl",ps.getProperty("url"));
    		model.notify("@station","changed"); 
    		fillPage();
    		
		}
	}

	public void onAudioFileUpload(Screen s,JSONObject data) {
		System.out.println("AUDIO FILE UPLOAD !!"+data.toJSONString());
	}
	
	private void setUploadMainSettings(String upid) {
		model.setProperty("@uploadid",upid);
		model.setProperty("@upload/method","s3amazon");		
		model.setProperty("@upload/storagehost","https://s3-eu-west-1.amazonaws.com/");
		model.setProperty("@upload/bucketname","springfield-storage");
		model.setProperty("@upload/destpath","mupop/images/");
		model.setProperty("@upload/destname_prefix","upload_");
		model.setProperty("@upload/publicpath","https://s3-eu-west-1.amazonaws.com/");
		model.setProperty("@upload/destname_type","epoch");
		model.setProperty("@upload/filetype","image");
		model.setProperty("@upload/fileext","png,jpeg,jpg");
		model.setProperty("@upload/checkupload","true");
	}
	
	
	private void setUploadMaskSettings(String upid) {
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
	
	private void setUploadAudioSettings(String upid) {
		model.setProperty("@uploadid",upid);
		model.setProperty("@upload/method","s3amazon");		
		model.setProperty("@upload/storagehost","https://s3-eu-west-1.amazonaws.com/");
		model.setProperty("@upload/bucketname","springfield-storage");
		model.setProperty("@upload/destpath","mupop/audios/");
		model.setProperty("@upload/destname_prefix","upload_");
		model.setProperty("@upload/publicpath","https://s3-eu-west-1.amazonaws.com/");
		model.setProperty("@upload/destname_type","epoch");
		model.setProperty("@upload/filetype","audio");
		model.setProperty("@upload/fileext","mp3,m4a");
		model.setProperty("@upload/checkupload","true");
	}
	

	
}
