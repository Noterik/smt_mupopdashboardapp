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
package org.springfield.lou.controllers.station.apps.trivia;

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

public class TriviaMainAppController extends Html5Controller{
	
	String selecteditem;
	String selectedmask;
	
	public TriviaMainAppController() {
		
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
			data.put("imageurl",model.getProperty("@item/imageurl"));
			System.out.println("IM="+model.getProperty("@item/imageurl"));
			data.put("selecteditem", item.getId());
		}
		String level = model.getProperty("@item/level");
		if (level!=null && !level.equals("")) {
			data.put("level", level);
		} else {
			model.setProperty("@item/level","1");
			data.put("level", "1");	
		}
		
		String maxsame = model.getProperty("@item/maxsame");
		if (maxsame!=null && !maxsame.equals("")) {
			data.put("maxsame", maxsame);
		} else {
			model.setProperty("@item/maxsame","1");
			data.put("maxsame", "1");	
		}
		
		String timeout = model.getProperty("@item/timeout");
		if (timeout!=null && !timeout.equals("")) {
			data.put("timeout", timeout);
		} else {
			model.setProperty("@item/timeout","8");
			data.put("timeout", "8");	
		}
		
		String set = model.getProperty("@item/set");
		if (set!=null && !set.equals("")) {
			data.put("set", set);
		} else {
			model.setProperty("@item/set","things");
			data.put("set", "things");	
		}
		
		String nextlevel = model.getProperty("@item/nextlevel");
		if (nextlevel!=null && !nextlevel.equals("")) {
			data.put("nextlevel", nextlevel);
		} else {
			model.setProperty("@item/nextlevel","1");
			data.put("nextlevel", "1");	
		}
		
		String nextset = model.getProperty("@item/nextset");
		if (nextset!=null && !nextset.equals("")) {
			data.put("nextset", nextset);
		} else {
			model.setProperty("@item/nextset","things");
			data.put("nextset", "things");	
		}

		String got = model.getProperty("@item/goto");
		if (got!=null && !got.equals("")) {
			data.put("goto", got);
		} else {
			model.setProperty("@item/goto","");
			data.put("goto", "");	
		}
		
		addItemQuestions(data);
		
		screen.get(selector).render(data);
		screen.get("#station_mainapp_itemsettings_level").on("change","onLevelChange", this);
		screen.get("#station_mainapp_itemsettings_maxsame").on("change","onMaxSameChange", this);
		screen.get("#station_mainapp_itemsettings_timeout").on("change","onTimeOutChange", this);
		screen.get("#station_mainapp_itemsettings_set").on("change","onSetChange", this);
		screen.get("#station_mainapp_itemsettings_nextlevel").on("change","onNextLevelChange", this);
		screen.get("#station_mainapp_itemsettings_nextset").on("change","onNextSetChange", this);
		screen.get("#station_mainapp_itemsettings_goto").on("change","onGotoChange", this);		
	
		screen.get(".station_mainapp_item").on("mouseup","onEditItem", this);
		screen.get(".station_mainapp_itemselected").on("mouseup","onEditItem", this);
		screen.get("#station_mainapp_newitem").on("mouseup","station_mainapp_newitemname","onNewItem", this);

		screen.get("#station_mainapp_item_newquestion").on("mouseup","station_mainapp_item_newquestionname","onNewItemQuestion", this);

		screen.get(".station_mainapp_question").on("mouseup","onItemQuestionSelected", this);

		
		
		setImageSettings("station_mainapp_imageupload");
		screen.get("#station_mainapp_imageuploadbutton").on("mouseup","station_mainapp_imageupload","onImageFileUpload", this);
		model.onPropertiesUpdate("/screen/upload/station_mainapp_imageupload","onImageUploadState",this);
		
		
		screen.get("#station_mainapp_deleteitem").on("mouseup","station_mainapp_deleteitemconfirm","onDeleteItem", this);
	
	
	}
	
	public void onItemQuestionSelected(Screen s,JSONObject data) {
			System.out.println("SELECT QUESTION="+data.toJSONString());
	}
	
	public void onLevelChange(Screen s,JSONObject data) {
		model.setProperty("@item/level",(String)data.get("value"));
	}
	
	public void onMaxSameChange(Screen s,JSONObject data) {
		model.setProperty("@item/maxsame",(String)data.get("value"));
	}
	
	public void onTimeOutChange(Screen s,JSONObject data) {
		model.setProperty("@item/timeout",(String)data.get("value"));
	}

	public void onSetChange(Screen s,JSONObject data) {
		model.setProperty("@item/set",(String)data.get("value"));
	}
	
	public void onNextLevelChange(Screen s,JSONObject data) {
		model.setProperty("@item/nextlevel",(String)data.get("value"));
	}
	
	public void onNextSetChange(Screen s,JSONObject data) {
		model.setProperty("@item/nextset",(String)data.get("value"));
	}
	
	public void onGotoChange(Screen s,JSONObject data) {
		model.setProperty("@item/goto",(String)data.get("value"));
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
	
	public void onDeleteItem(Screen s,JSONObject data) {
		String confirm = (String)data.get("station_mainapp_deleteitemconfirm");
		if (confirm.equals("yes")) {
			model.deleteNode("@item");
			selecteditem = null;
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
	
	public void onImageFileUpload(Screen s,JSONObject data) {
		System.out.println("upload of main image wanted");
	}
	
	public void onNewItemQuestion(Screen s,JSONObject data) {
		String newid = getNewQuestionId();
		System.out.println("New item question id="+newid);
		FsNode newitemquestion = new FsNode("question",newid);
		model.setProperty("@contentrole","mainapp");
		Boolean result=model.putNode("@item", newitemquestion);
		if (result) {
			fillPage();
		}
	}
	
	private void addItemQuestions(JSONObject data) {
		model.setProperty("@contentrole","mainapp");
		System.out.println("E="+model.getNode("@item"));
		FSList questionsList = model.getList("@item/question");
		
		FSList resultquestions = new FSList();
		List<FsNode> nodes = questionsList.getNodes();
		if (nodes != null) {
			for (Iterator<FsNode> iter = nodes.iterator(); iter.hasNext();) {
				FsNode node = (FsNode) iter.next();
				resultquestions.addNode(node);
			}
		}
		JSONObject questions = resultquestions.toJSONObject("en","url,classname");
		data.put("questions",questions);
	}

	
	public void onImageUploadState(ModelEvent e) {
		FsPropertySet ps = (FsPropertySet)e.target;
		String action = ps.getProperty("action");
		String progress = ps.getProperty("progress");
		System.out.println("PR="+progress);
		if (progress!=null && progress.equals("100")) {
    		model.setProperty("@item/imageurl",ps.getProperty("url"));
    		model.notify("@station","changed"); 
    		fillPage();
    		
		}
	
	}
	
	private void setImageSettings(String upid) {
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
	
    private String getNewQuestionId() {
    	int result = 0;
    	// get the list from domain so see if we are on a number idea we can use.
		FSList questions = model.getList("@item/question");
		if (questions!=null && questions.size()>0) { // if we have stations already lets find the highest number
			for(Iterator<FsNode> iter = questions.getNodes().iterator() ; iter.hasNext(); ) {
				FsNode node = (FsNode)iter.next();	
				try {
					int newvalue = Integer.parseInt(node.getId()); // parse the number and store if valid
					if (newvalue>result) {
						result = newvalue; // valid number remember if its higher than the last one
					}
				} catch(Exception e) { // forget exceptions we assume many are not numbers
				}
			}
		}
		return ""+(result+1); // take the highest number add one so its new and return it 
    }

	
	
	

	
}
