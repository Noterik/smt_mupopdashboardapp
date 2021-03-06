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
		if (selecteditem!=null) {
			model.setProperty("@contentrole","mainapp");
			model.setProperty("@itemid",selecteditem);
			FsNode item=model.getNode("@item");
			data.put("selecteditem", item.getId());
			String voiceover = model.getProperty("@item/en_voiceover");
			if (voiceover!=null && !voiceover.equals("")) {
				data.put("voiceover",voiceover);
			}
			data.put("url",model.getProperty("@item/url"));
			data.put("scale",model.getProperty("@item/scale"));
			data.put("origin",model.getProperty("@item/origin"));
			data.put("voiceover",model.getProperty("@item/en_voiceover"));
		}
		addItemMasks(data);
		
		if (selectedmask!=null) {
			model.setProperty("@itemid",selecteditem);
			model.setProperty("@itemmaskid",selectedmask);
			FsNode itemmask=model.getNode("@itemmask");
			if (itemmask!=null) {
				data.put("maskurl",itemmask.getProperty("maskurl"));
				data.put("audiourl", itemmask.getProperty("en_audiourl"));
				data.put("selecteditemmask", itemmask.getId());
			} else {
				data.put("maskurl","");
				data.put("audiourl","");
			}

		}
		
		screen.get(selector).render(data);
		screen.get(".station_mainapp_item").on("mouseup","onEditItem", this);
		screen.get(".station_mainapp_itemselected").on("mouseup","onEditItem", this);
		screen.get("#station_mainapp_newitem").on("mouseup","station_mainapp_newitemname","onNewItem", this);

		setUploadAudioSettings("station_mainapp_item_audio_upload");
		screen.get("#station_mainapp_item_audio_submithidden").on("mouseup","station_mainapp_item_audio_upload","onAudioFileUpload", this);
		model.onPropertiesUpdate("/screen/upload/station_mainapp_item_audio_upload","onAudioUploadState",this);
		screen.get("#station_mainapp_item_audio_deletebutton").on("mouseup","onDeleteMaskAudio", this);



		setUploadMaskSettings("station_mainapp_item_image_upload");
		screen.get("#station_mainapp_item_image_submithidden").on("mouseup","station_mainapp_item_image_upload","onFileMaskUpload", this);
		model.onPropertiesUpdate("/screen/upload/station_mainapp_item_image_upload","onUploadMaskState",this);


		setUploadMainSettings("station_mainapp_image_upload");
		screen.get("#station_mainapp_image_submithidden").on("mouseup","station_mainapp_image_upload","onFileMainImageUpload", this);
		model.onPropertiesUpdate("/screen/upload/station_mainapp_image_upload","onUploadMainImageState",this);
		
		
		screen.get("#station_mainapp_image_deletebutton").on("mouseup","onDeleteMainImage",this);
		screen.get("#station_mainapp_item_image_deletebutton").on("mouseup","onDeleteMaskImage",this);
		
		setUploadMainAudioSettings("station_mainapp_audio_upload");
		screen.get("#station_mainapp_audio_submithidden").on("mouseup","station_mainapp_audio_upload","onFileMainAudioUpload", this);
		model.onPropertiesUpdate("/screen/upload/station_mainapp_audio_upload","onUploadMainAudioState",this);
		screen.get("#station_mainapp_audio_deletebutton").on("mouseup","onDeleteMainAudio", this);

		
		screen.get("#station_mainapp_deleteitem").on("mouseup","station_mainapp_deleteitemconfirm","onDeleteItem", this);
		screen.get("#station_mainapp_deletemask").on("mouseup","station_mainapp_deletemaskconfirm","onDeleteMask", this);

		screen.get("#station_mainapp_item_editscale").on("change","onScaleChange", this);
		screen.get("#station_mainapp_item_editorigin").on("change","onOriginChange", this);
		
		screen.get("#station_mainapp_edititem_renderoptions").on("change","onRenderOptionChange", this);
		screen.get("#station_mainapp_item_newmask").on("mouseup","station_mainapp_item_newmaskname","onNewItemMask", this);
		screen.get(".station_mainapp_maskselected").on("mouseup","onEditItemMask", this);
		screen.get(".station_mainapp_mask").on("mouseup","onEditItemMask", this);
	
	}
	
	public void onScaleChange(Screen s,JSONObject data) {
		model.setProperty("@item/scale",(String)data.get("value"));
		fillPage();
		model.notify("@station","changed"); 
	}
	
	public void onOriginChange(Screen s,JSONObject data) {
		model.setProperty("@item/origin",(String)data.get("value"));
		fillPage();
		model.notify("@station","changed"); 
	}
	
	public void onRenderOptionChange(Screen s,JSONObject data) {
		model.setProperty("@item/renderoption",(String)data.get("value"));
		fillPage();
		model.notify("@station","changed"); 
	}
	
	public void onNewItem(Screen s,JSONObject data) {
		String newid=(String)data.get("station_mainapp_newitemname");
		
		if (newid==null || newid.equals("")) {
			newid = getAutoName();
		}
		
		FsNode newitem = new FsNode("item",newid);
		model.setProperty("@contentrole","mainapp");
		Boolean result=model.putNode("@items", newitem);
		if (result) {
			selecteditem = newid;
			fillPage();
		}
	}
	
	public void onEditItemMask(Screen s,JSONObject data) {
		String itemmaskid = (String)data.get("id");
		itemmaskid = itemmaskid.substring(25);
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
	
	public void onDeleteMask(Screen s,JSONObject data) {
		String confirm = (String)data.get("station_mainapp_deletemaskconfirm");
		if (confirm.equals("yes")) {
			model.deleteNode("@itemmask");
			selectedmask = null;
			fillPage();
		}
	}
	
	public void onDeleteMainAudio(Screen s,JSONObject data) {
		model.setProperty("@item/en_voiceover","");
		fillPage();
	}
	
	public void onDeleteMaskAudio(Screen s,JSONObject data) {
		model.setProperty("@itemmask/en_audiourl","");
		fillPage();
	}
		
	
	public void onNewItemMask(Screen s,JSONObject data) {
		String newid=(String)data.get("station_mainapp_item_newmaskname");
		
		if (newid==null || newid.equals("")) {
			newid = getAutoMaskName();
		}
		if (newid.equals("")) return;
		
		
		FsNode newitemmask = new FsNode("mask",newid);
		model.setProperty("@contentrole","mainapp");
		Boolean result=model.putNode("@item", newitemmask);
		if (result) {
			selectedmask = newid;
			fillPage();
		}
	}
	
	public void onEditItem(Screen s,JSONObject data) {
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
	}
	
	public void onFileMainAudioUpload(Screen s,JSONObject data) {
	}
	
	
	public void onFileMaskUpload(Screen s,JSONObject data) {
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
	
	public void onDeleteMainImage(Screen s,JSONObject data) {
		model.setProperty("@item/url","");
		model.notify("@station","changed"); 
		fillPage();
	}
	
	
	
	public void onDeleteMaskImage(Screen s,JSONObject data) {
		model.setProperty("@itemmask/maskurl","");
		model.notify("@station","changed"); 
		fillPage();
	}
	
	
	public void onUploadMainImageState(ModelEvent e) {
		FsPropertySet ps = (FsPropertySet)e.target;
		String action = ps.getProperty("action");
		String progress = ps.getProperty("progress");
		if (progress!=null && progress.equals("100")) {
    		model.setProperty("@item/url",ps.getProperty("url"));
    		model.notify("@station","changed"); 
    		fillPage();
		}
	}
	
	public void onUploadMainAudioState(ModelEvent e) {
		FsPropertySet ps = (FsPropertySet)e.target;
		String action = ps.getProperty("action");
		String progress = ps.getProperty("progress");
		if (progress!=null && progress.equals("100")) {
    		model.setProperty("@item/en_voiceover",ps.getProperty("url"));
    		model.notify("@station","changed"); 
    		fillPage();
		}
	}

	public void onFileUpload(Screen s,JSONObject data) {
	}
	
	public void onAudioUploadState(ModelEvent e) {
		FsPropertySet ps = (FsPropertySet)e.target;
		String action = ps.getProperty("action");
		String progress = ps.getProperty("progress");
		if (progress!=null && progress.equals("100")) {
    		model.setProperty("@itemmask/en_audiourl",ps.getProperty("url"));
    		model.notify("@station","changed"); 
    		fillPage();
		}
	}

	public void onAudioFileUpload(Screen s,JSONObject data) {
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
		model.setProperty("@upload/fileext","png,jpg,jpeg,gif,JPG,PNG,GIF,JPEG");
		model.setProperty("@upload/checkupload","true");
	}
	
	private void setUploadMainAudioSettings(String upid) {
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
	
	private String getAutoMaskName() {
		int curi = 0;
		FSList list = model.getList("@item/mask");
		if (list!=null) {
			List<FsNode> nodes = list.getNodes();
			for (Iterator<FsNode> iter = nodes.iterator(); iter.hasNext();) {
				FsNode node = iter.next();
				String nw = node.getId();
				try {
					int nwi = Integer.parseInt(nw);
					if (nwi>curi) curi = nwi;
				} catch(Exception e) {
					return "";
				}
			}
		}
		return ""+(curi+1);
	}
	
	private String getAutoName() {
		int curi = 0;
		FSList list = model.getList("@items");
		if (list!=null) {
			List<FsNode> nodes = list.getNodes();
			for (Iterator<FsNode> iter = nodes.iterator(); iter.hasNext();) {
				FsNode node = iter.next();
				String nw = node.getId();
				if (nw!=null) {
					int nwi = wordToNumber(nw);
					if (nwi>curi) curi = nwi;
				}
			}
		}
		return numberToWord(curi+1);
	}
	
	private String numberToWord(int nmber) {
		if (nmber==1) return "one";
		if (nmber==2) return "two";
		if (nmber==3) return "three";
		if (nmber==4) return "four";
		if (nmber==5) return "five";
		if (nmber==6) return "six";
		if (nmber==7) return "seven";
		if (nmber==8) return "eight";
		if (nmber==9) return "nine";
		if (nmber==10) return "ten";
		return "unknown";
	}
	
	private int wordToNumber(String word) {
		if (word.equals("one")) return 1;
		if (word.equals("two")) return 2;
		if (word.equals("three")) return 3;
		if (word.equals("four")) return 4;
		if (word.equals("five")) return 5;
		if (word.equals("six")) return 6;
		if (word.equals("seven")) return 7;
		if (word.equals("eight")) return 8;
		if (word.equals("nine")) return 9;
		return -1;
	}
	

	
}
