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
	private boolean uploaddone = false;
	
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
			model.setProperty("@contentrole","mainapp");
			model.setProperty("@itemid",selecteditem);
			FsNode item=model.getNode("@item");
			data.put("selecteditem", item.getId());
			String voiceover = model.getProperty("@item/voiceover");
			if (voiceover!=null && !voiceover.equals("")) {
				data.put("voiceover",voiceover);
			}
		}
		addImageRenderOptions(data,model.getProperty("@item/renderoption"));
		addImages(data);
		
		screen.get(selector).render(data);
		screen.get(".station_mainapp_item").on("mouseup","onEditItem", this);
		screen.get("#station_mainapp_newitem").on("mouseup","station_mainapp_newitemname","onNewItem", this);

		setUploadAudioSettings("station_mainapp_audio_upload");
		screen.get("#station_mainapp_audio_submithidden").on("mouseup","station_mainapp_audio_upload","onAudioFileUpload", this);
		model.onPropertiesUpdate("/screen/upload/station_mainapp_audio_upload","onAudioUploadState",this);

		screen.get("#station_mainapp_audio_deletebutton").on("mouseup","onDeleteAudio", this);
		
		
		setUploadSettings("station_mainapp_image_upload");
		screen.get("#station_mainapp_image_submithidden").on("mouseup","station_mainapp_image_upload","onFileUpload", this);
		model.onPropertiesUpdate("/screen/upload/station_mainapp_image_upload","onUploadState",this);

		
		screen.get("#station_mainapp_edititem_renderoptions").on("change","onRenderOptionChange", this);
		
		screen.get("#station_mainapp_deleteitem").on("mouseup","station_mainapp_deleteitemconfirm","onDeleteItem", this);
	
		screen.get(".station_mainapp_deleteimage").on("mouseup","onDeleteImage", this);
		
	}
	
	public void onDeleteAudio(Screen s,JSONObject data) {
		model.setProperty("@item/voiceover","");
		fillPage();
	}

	
	public void onDeleteImage(Screen s,JSONObject data) {
		String id=((String)data.get("id")).substring(19);
		Boolean result = model.deleteNode("@item/image/"+id);
		if (result) {
			fillPage();
			model.notify("@station","changed"); 
		}
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
			fillPage();
		}
	}
	
	public void onEditItem(Screen s,JSONObject data) {
		String itemid = (String)data.get("id");
		itemid = itemid.substring(20);
		selecteditem = itemid;
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
	
	private void addImages(JSONObject data) {
		FSList imagesList = model.getList("@itemimages");
		JSONObject images = imagesList.toJSONObject("en","url");
		data.put("images",images);
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
	
	public void onUploadState(ModelEvent e) {
		FsPropertySet ps = (FsPropertySet)e.target;
		String action = ps.getProperty("action");
		String progress = ps.getProperty("progress");
		//if (progress!=null && progress.equals("100")) {
			
		if (progress!=null && progress.equals("100") && !uploaddone) {
			uploaddone=true;
			//screen.get("#appeditor_content_preview").html("<image width=\"100%\" height=\"100%\" src=\""+ps.getProperty("url")+"\" />");
			//System.out.println("UPLOAD DONE SHOULD CREATE NODE !");
    		FsNode imagenode = new FsNode("image",""+new Date().getTime());
    		imagenode.setProperty("url",ps.getProperty("url"));
    		
    		// check if we already have a contentrole if not set it to waitscreen
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
		uploaddone = false;
	}
	
	public void onAudioUploadState(ModelEvent e) {
		FsPropertySet ps = (FsPropertySet)e.target;
		String action = ps.getProperty("action");
		String progress = ps.getProperty("progress");
		if (progress!=null && progress.equals("100")) {
			model.setProperty("@item/voiceover",ps.getProperty("url"));
			fillPage();
		}
	
	}

	public void onAudioFileUpload(Screen s,JSONObject data) {
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
		model.setProperty("@upload/fileext","png,jpg,jpeg,gif,JPG,PNG,GIF,JPEG");
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
		model.setProperty("@upload/fileext","m4a,mp3");
		model.setProperty("@upload/checkupload","true");
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
