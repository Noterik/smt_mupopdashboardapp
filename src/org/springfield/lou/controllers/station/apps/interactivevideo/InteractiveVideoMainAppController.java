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
	
	String selecteditem;
	String selectedquestion;
	
	public InteractiveVideoMainAppController() {
		
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
			String videourl = item.getProperty("videourl");
			if (videourl!=null && !videourl.equals("")) {
				data.put("videourl", videourl);
			}
			String audiourl = item.getProperty("audiourl");
			if (audiourl!=null && !audiourl.equals("")) {
				data.put("audiourl", audiourl);
			}
					
			data.put("selecteditem", item.getId());
			addItemQuestions(data);
			
			if (selectedquestion!=null) {
				model.setProperty("@itemquestionid",selectedquestion);
				FsNode itemquestion=model.getNode("@itemquestion");
				data.put("starttime", itemquestion.getProperty("starttime"));
				data.put("duration", itemquestion.getProperty("duration"));
				data.put("questiontitle", itemquestion.getProperty("question"));
				data.put("answer1", itemquestion.getProperty("answer1"));
				data.put("answer2", itemquestion.getProperty("answer2"));
				data.put("answer3", itemquestion.getProperty("answer3"));
				data.put("answer4", itemquestion.getProperty("answer4"));
				data.put("correctanswer", itemquestion.getProperty("correctanswer"));
				data.put("selecteditemquestion", itemquestion.getId());
			}
		}
		addVideos(data);
		
		screen.get(selector).render(data);
	
		setUploadVideoSettings("station_mainapp_videoupload");
		screen.get("#station_mainapp_videouploadbutton").on("mouseup","station_mainapp_videoupload","onFileVideoUpload", this);
		model.onPropertiesUpdate("/screen/upload/station_mainapp_videoupload","onVideoUploadState",this);

		setUploadAudioSettings("station_mainapp_audioupload");
		screen.get("#station_mainapp_audiouploadbutton").on("mouseup","station_mainapp_audioupload","onFileAudioUpload", this);
		model.onPropertiesUpdate("/screen/upload/station_mainapp_audioupload","onAudioUploadState",this);

		screen.get("#station_mainapp_deleteitem").on("mouseup","station_mainapp_deleteitemconfirm","onDeleteItem", this);

		
		screen.get(".mainapp_deletevideo").on("mouseup","onDeleteVideo", this);
		screen.get(".station_mainapp_item").on("mouseup","onEditItem", this);
		screen.get(".station_mainapp_itemselected").on("mouseup","onEditItem", this);
		
		screen.get(".station_mainapp_question").on("mouseup","onEditItemQuestion", this);
		screen.get(".station_mainapp_questionselected").on("mouseup","onEditItemQuestion", this);
	
		screen.get("#station_mainapp_newitem").on("mouseup","station_mainapp_newitemname","onNewItem", this);
		
		screen.get("#station_mainapp_item_newquestion").on("mouseup","station_mainapp_item_newquestionname","onNewItemQuestion", this);

		screen.get("#station_mainapp_item_editquestionstarttime").on("change","onQuestionStarttimeChange", this);
		screen.get("#station_mainapp_item_editquestionduration").on("change","onQuestionDurationChange", this);
		screen.get("#station_mainapp_item_editquestiontitle").on("change","onQuestionChange", this);
		screen.get("#station_mainapp_item_editanswer1").on("change","onAnswer1Change", this);
		screen.get("#station_mainapp_item_editanswer2").on("change","onAnswer2Change", this);
		screen.get("#station_mainapp_item_editanswer3").on("change","onAnswer3Change", this);
		screen.get("#station_mainapp_item_editanswer4").on("change","onAnswer4Change", this);	
		screen.get("#station_mainapp_item_editcorrectanswer").on("change","onCorrectAnswerChange", this);	
	}
	
	public void onDeleteItem(Screen s,JSONObject data) {
		String confirm = (String)data.get("station_mainapp_deleteitemconfirm");
		if (confirm.equals("yes")) {
			model.deleteNode("@item");
			selecteditem = null;
			selectedquestion = null;
			fillPage();
		}
	}
	
	public void onQuestionStarttimeChange(Screen s, JSONObject data) {
		model.setProperty("@itemquestion/starttime",(String)data.get("value"));
	}
	
	public void onQuestionDurationChange(Screen s, JSONObject data) {
		model.setProperty("@itemquestion/duration",(String)data.get("value"));
	}
	
	public void onQuestionChange(Screen s, JSONObject data) {
		model.setProperty("@itemquestion/question",(String)data.get("value"));
	}
	
	public void onAnswer1Change(Screen s, JSONObject data) {
		model.setProperty("@itemquestion/answer1",(String)data.get("value"));
	}
	
	public void onAnswer2Change(Screen s, JSONObject data) {
		model.setProperty("@itemquestion/answer2",(String)data.get("value"));
	}
	
	public void onAnswer3Change(Screen s, JSONObject data) {
		model.setProperty("@itemquestion/answer3",(String)data.get("value"));
	}
	
	public void onAnswer4Change(Screen s, JSONObject data) {
		model.setProperty("@itemquestion/answer4",(String)data.get("value"));
	}
	
	public void onCorrectAnswerChange(Screen s, JSONObject data) {
		model.setProperty("@itemquestion/correctanswer",(String)data.get("value"));
	}
	
	public void onNewItemQuestion(Screen s,JSONObject data) {
		String newid=(String)data.get("station_mainapp_item_newquestionname");
		FsNode newitemquestion = new FsNode("question",newid);
		model.setProperty("@contentrole","mainapp");
		Boolean result=model.putNode("@item", newitemquestion);
		if (result) {
			fillPage();
		}
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
		selectedquestion = null;
		fillPage();
	}
	
	public void onEditItemQuestion(Screen s,JSONObject data) {
		String itemquestionid = (String)data.get("id");
		itemquestionid = itemquestionid.substring(29);
		System.out.println("WHOOOO="+itemquestionid);
		selectedquestion = itemquestionid;
		fillPage();
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
	
	private void addItemQuestions(JSONObject data) {
		model.setProperty("@contentrole","mainapp");
		System.out.println("E="+model.getNode("@item"));
		FSList questionsList = model.getList("@item/question");
		
		FSList resultquestions = new FSList();
		List<FsNode> nodes = questionsList.getNodes();
		if (nodes != null) {
			for (Iterator<FsNode> iter = nodes.iterator(); iter.hasNext();) {
				FsNode node = (FsNode) iter.next();
				if (selectedquestion==null) selectedquestion = node.getId();
				if (selectedquestion.equals(node.getId())) {
					node.setProperty("classname","station_mainapp_questionselected");
				} else {
					node.setProperty("classname","station_mainapp_question");	
				}
				resultquestions.addNode(node);
			}
		}
		JSONObject questions = resultquestions.toJSONObject("en","url,classname");
		data.put("questions",questions);
	}
	
	private void addVideos(JSONObject data) {
		model.setProperty("@contentrole","mainapp");
		FSList videosList = model.getList("@videos");
		JSONObject videos = videosList.toJSONObject("en","url");
		data.put("videos",videos);
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
	
	public void onAudioUploadState(ModelEvent e) {
		FsPropertySet ps = (FsPropertySet)e.target;
		String action = ps.getProperty("action");
		String progress = ps.getProperty("progress");
		if (progress!=null && progress.equals("100")) {
    		model.setProperty("@item/audiourl",ps.getProperty("url"));
    		model.notify("@station","changed"); 
    		fillPage();
		}
	}

	
	public void onVideoUploadState(ModelEvent e) {
		FsPropertySet ps = (FsPropertySet)e.target;
		String action = ps.getProperty("action");
		String progress = ps.getProperty("progress");
		if (progress!=null && progress.equals("100")) {
    		model.setProperty("@item/videourl",ps.getProperty("url"));
    		model.notify("@station","changed"); 
    		fillPage();
		}
	}

	public void onFileVideoUpload(Screen s,JSONObject data) {
		System.out.println("VIDEO FILE UPLOAD !!"+data.toJSONString());
	}
	
	public void onFileAudioUpload(Screen s,JSONObject data) {
		System.out.println("AUDIO FILE UPLOAD !!"+data.toJSONString());
	}
	
	private void setUploadVideoSettings(String upid) {
		model.setProperty("@uploadid",upid);
		model.setProperty("@upload/method","s3amazon");	
		model.setProperty("@upload/storagehost","https://s3-eu-west-1.amazonaws.com/");
		model.setProperty("@upload/bucketname","springfield-storage");
		model.setProperty("@upload/destpath","mupop/videos/");
		model.setProperty("@upload/destname_prefix","upload_");
		model.setProperty("@upload/publicpath","https://s3-eu-west-1.amazonaws.com/");
		model.setProperty("@upload/destname_type","epoch");
		model.setProperty("@upload/filetype","video");
		model.setProperty("@upload/fileext","mp4");
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
		model.setProperty("@upload//fileext","mp3");
		model.setProperty("@upload/checkupload","true");
	}
	
	
	

	
}
