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
package org.springfield.lou.controllers.station.apps.quiz;

import java.awt.event.ItemEvent;
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

public class QuizMainAppController extends Html5Controller{
	
	String selecteditem;
	String selectedmask;
	String selectedslide="1";
	FsNode selectedslidenode;
	
	public QuizMainAppController() {
		
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
			//data.put("imageurl",model.getProperty("@item/imageurl"));
			//System.out.println("IM="+model.getProperty("@item/imageurl"));
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
		
		/*
		String nextlevel = model.getProperty("@item/next");
		if (nextlevel!=null && !nextlevel.equals("")) {
			data.put("nextlevel", nextlevel);
		} else {
			model.setProperty("@item/next","end");
			data.put("next", "end");	
		}
		*/
		
		
		String random = model.getProperty("@item/random");
		if (random!=null && !random.equals("")) {
			data.put("random", random);
		} else {
			model.setProperty("@item/random","0");
			data.put("random", "0");	
		}

		String got = model.getProperty("@item/goto");
		if (got!=null && !got.equals("")) {
			data.put("goto", got);
		} else {
			model.setProperty("@item/goto","");
			data.put("goto", "");	
		}
		
		
		addItemSlides(data);
		// now add all the selected Item options
		
		if (!model.getNode("@item").getId().equals("one")) { // temp hack
			addItemSettings(data);
			addNextOptions(data);
			addGotoOptions(data);
		}
		
		screen.get(selector).render(data);
		screen.get("#station_mainapp_itemsettings_level").on("change","onLevelChange", this);
		screen.get("#station_mainapp_itemsettings_maxsame").on("change","onMaxSameChange", this);
		screen.get("#station_mainapp_itemsettings_timeout").on("change","onTimeOutChange", this);
		screen.get("#station_mainapp_itemsettings_set").on("change","onSetChange", this);
		screen.get("#station_mainapp_itemsettings_nextlevel").on("change","onNextLevelChange", this);
		screen.get("#station_mainapp_itemsettings_nextset").on("change","onNextSetChange", this);
	
		
		screen.get("#station_mainapp_slidetype").on("change","onSlideTypeChange", this);	
		screen.get("#station_mainapp_slidequestion").on("change","onSlideQuestionChange", this);
		screen.get("#station_mainapp_slideanswer1").on("change","onSlideAnswer1Change", this);
		screen.get("#station_mainapp_slideanswer2").on("change","onSlideAnswer2Change", this);		 
		screen.get("#station_mainapp_slideanswer3").on("change","onSlideAnswer3Change", this);
		screen.get("#station_mainapp_slideanswer4").on("change","onSlideAnswer4Change", this);
		screen.get("#station_mainapp_slidecorrectanswer").on("change","onSlideCorrectAnswerChange", this);
		screen.get("#station_mainapp_slidenext").on("change","onSlideNextChange", this);
		screen.get("#station_mainapp_slidetimeout").on("change","onSlideTimeoutChange", this);
		screen.get("#station_mainapp_slideset").on("change","onSlideSetChange", this);
		screen.get("#station_mainapp_random").on("change","onRandomChange", this);	
		screen.get("#station_mainapp_goto").on("change","onGotoChange", this);	
	
		screen.get(".station_mainapp_item").on("mouseup","onEditItem", this);
		screen.get(".station_mainapp_itemselected").on("mouseup","onEditItem", this);
		screen.get("#station_mainapp_newitem").on("mouseup","station_mainapp_newitemname","onNewItem", this);

		screen.get("#station_mainapp_item_newslide").on("mouseup","station_mainapp_item_newslidename","onNewItemSlide", this);

		screen.get(".station_mainapp_slide").on("mouseup","onItemSlideSelected", this);

		
		
		setImageSettings("station_mainapp_slideimageupload");
		screen.get("#station_mainapp_slideimageuploadbutton").on("mouseup","station_mainapp_slideimageupload","onImageFileUpload", this);
		model.onPropertiesUpdate("/screen/upload/station_mainapp_slideimageupload","onImageUploadState",this);
		
		setVideoSettings("station_mainapp_slidevideoupload");
		screen.get("#station_mainapp_slidevideouploadbutton").on("mouseup","station_mainapp_slidevideoupload","onVideoFileUpload", this);
		model.onPropertiesUpdate("/screen/upload/station_mainapp_slidevideoupload","onVideoUploadState",this);

		
		screen.get("#station_mainapp_deleteitem").on("mouseup","station_mainapp_deleteitemconfirm","onDeleteItem", this);
	
	
	}
	
	
	public void onItemSlideSelected(Screen s,JSONObject data) {
		System.out.println("SELECT SlIDE="+data.toJSONString());
		String id = ((String)data.get("id")).substring(6);
		System.out.println("ID="+id);
		selectedslide=id;
		fillPage();
//		screen.get("#screen").append("div","appeditor_quizquestion",new QuizQuestionController());
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
	
	public void onRandomChange(Screen s,JSONObject data) {
		model.setProperty("@item/random",(String)data.get("value"));
	}
	
	public void onSlideNextChange(Screen s,JSONObject data) {
		model.setProperty("@item/slide/"+selectedslide+"/next",(String)data.get("value"));
	}
	
	public void onSlideSetChange(Screen s,JSONObject data) {
		model.setProperty("@item/slide/"+selectedslide+"/set",(String)data.get("value"));
	}
	
	public void onSlideTimeoutChange(Screen s,JSONObject data) {
		model.setProperty("@item/slide/"+selectedslide+"/timeout",(String)data.get("value"));
	}
	
	public void onSlideTypeChange(Screen s,JSONObject data) {
		model.setProperty("@item/slide/"+selectedslide+"/type",(String)data.get("value"));
		fillPage();
	}
	
	public void onSlideQuestionChange(Screen s,JSONObject data) {
		model.setProperty("@item/slide/"+selectedslide+"/question",(String)data.get("value"));
	}
	
	public void onSlideAnswer1Change(Screen s,JSONObject data) {
		model.setProperty("@item/slide/"+selectedslide+"/answer1",(String)data.get("value"));
	}
	
	public void onSlideAnswer2Change(Screen s,JSONObject data) {
		model.setProperty("@item/slide/"+selectedslide+"/answer2",(String)data.get("value"));
	}

	public void onSlideAnswer3Change(Screen s,JSONObject data) {
		model.setProperty("@item/slide/"+selectedslide+"/answer3",(String)data.get("value"));
	}
	
	public void onSlideAnswer4Change(Screen s,JSONObject data) {
		model.setProperty("@item/slide/"+selectedslide+"/answer4",(String)data.get("value"));
	}
	
	public void onSlideCorrectAnswerChange(Screen s,JSONObject data) {
		model.setProperty("@item/slide/"+selectedslide+"/correctanswer",(String)data.get("value"));
	}
	
	
	
	
	
	public void onNewItem(Screen s,JSONObject data) {
		String newid = getNewItemId();
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
	
	
	private void addNextOptions(JSONObject data) {
		FSList itemsList = model.getList("@item/slide");
		FSList resultitems = new FSList();
		List<FsNode> nodes = itemsList.getNodes();
		System.out.println(nodes);
		if (selectedslidenode.getProperty("next")==null || !selectedslidenode.getProperty("next").equals("next")) {
			FsNode resultnode = new FsNode("value","next");
			resultnode.setProperty("value","next");
			resultitems.addNode(resultnode);
		}
		if (selectedslidenode.getProperty("next")==null || !selectedslidenode.getProperty("next").equals("end")) {
			FsNode resultnode = new FsNode("value","end");
			resultnode.setProperty("value","end");
			resultitems.addNode(resultnode);
		}
		if (nodes != null) {
			System.out.println("NODE="+nodes.size());
			for (Iterator<FsNode> iter = nodes.iterator(); iter.hasNext();) {
				FsNode node = (FsNode) iter.next();
				if (!selectedslide.equals(node.getId())) {
					FsNode resultnode = new FsNode("option",node.getId());
					resultnode.setProperty("value", node.getId());
					resultitems.addNode(resultnode);
				}
			}

		}
		data.put("nextoptions",resultitems.toJSONObject("en","value"));
	}
		
	private void addGotoOptions(JSONObject data) {
		FSList itemsList = model.getList("@item/slide");
		FSList resultitems = new FSList();
		List<FsNode> nodes = itemsList.getNodes();
		if (nodes != null) {
			for (Iterator<FsNode> iter = nodes.iterator(); iter.hasNext();) {
				FsNode node = (FsNode) iter.next();
				FsNode resultnode = new FsNode("option",node.getId());
				resultnode.setProperty("value", node.getId());
				resultitems.addNode(resultnode);
			}

		}
		data.put("gotooptions",resultitems.toJSONObject("en","value"));
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
	
	public void onVideoFileUpload(Screen s,JSONObject data) {
		System.out.println("upload of main video wanted");
	}
	
	public void onNewItemSlide(Screen s,JSONObject data) {
		String newid = getNewSlideId();
		System.out.println("New item slide id="+newid);
		FsNode newitemslide = new FsNode("slide",newid);
		newitemslide.setProperty("next","next");
		newitemslide.setProperty("timeout","10");
		model.setProperty("@contentrole","mainapp");
		Boolean result=model.putNode("@item", newitemslide);
		if (result) {
			fillPage();
		}
	}
	
	private void addItemSettings(JSONObject data) {
		if (selectedslidenode!=null) {
			String type = selectedslidenode.getProperty("type");
			data.put(type,"true");
			if (type.equals("imagequestion")) {
				String question = selectedslidenode.getProperty("question");
				String answer1 = selectedslidenode.getProperty("answer1");
				String answer2 = selectedslidenode.getProperty("answer2");
				String answer3 = selectedslidenode.getProperty("answer3");
				String answer4 = selectedslidenode.getProperty("answer4");
				String imageurl = selectedslidenode.getProperty("imageurl");
				String correctanswer = selectedslidenode.getProperty("correctanswer");
				String next = selectedslidenode.getProperty("next");
				String timeout = selectedslidenode.getProperty("timeout");
				String set = selectedslidenode.getProperty("set");
				if (set==null) { data.put("slideset","normal");} else {data.put("slideset",set);}
				if (question==null) { data.put("slidequestion","");} else {data.put("slidequestion",question);}
				if (answer1==null) { data.put("slideanswer1","");} else {data.put("slideanswer1",answer1);}
				if (answer2==null) { data.put("slideanswer2","");} else {data.put("slideanswer2",answer2);}				
				if (answer3==null) { data.put("slideanswer3","");} else {data.put("slideanswer3",answer3);}	
				if (answer4==null) { data.put("slideanswer4","");} else {data.put("slideanswer4",answer4);}	
				if (answer4==null) { data.put("slidecorrectanswer","");} else {data.put("slidecorrectanswer",correctanswer);}
				if (imageurl==null) { data.put("slideimageurl","");} else {data.put("slideimageurl",imageurl);}		
				if (next==null) { data.put("slidenext","");} else {data.put("slidenext",next);}
				if (timeout==null) { data.put("slidetimeout","");} else {data.put("slidetimeout",timeout);}
			} else if (type.equals("image")) {
				String imageurl = selectedslidenode.getProperty("imageurl");
				if (imageurl==null) { data.put("slideimageurl","");} else {data.put("slideimageurl",imageurl);}	
				String next = selectedslidenode.getProperty("next");
				String timeout = selectedslidenode.getProperty("timeout");
				String set = selectedslidenode.getProperty("set");
				if (set==null) { data.put("slideset","normal");} else {data.put("slideset",set);}
				if (next==null) { data.put("slidenext","");} else {data.put("slidenext",next);}
				if (timeout==null) { data.put("slidetimeout","");} else {data.put("slidetimeout",timeout);}
			} else if (type.equals("video")) {
				String videourl = selectedslidenode.getProperty("videourl");
				if (videourl==null) { data.put("slidevideourl","");} else {data.put("slidevideourl",videourl);}	
				String next = selectedslidenode.getProperty("next");
				String timeout = selectedslidenode.getProperty("timeout");
				String set = selectedslidenode.getProperty("set");
				if (set==null) { data.put("slideset","normal");} else {data.put("slideset",set);}
				if (next==null) { data.put("slidenext","");} else {data.put("slidenext",next);}
				if (timeout==null) { data.put("slidetimeout","");} else {data.put("slidetimeout",timeout);}
			} else if (type.equals("videoquestion")) {
				String question = selectedslidenode.getProperty("question");
				String answer1 = selectedslidenode.getProperty("answer1");
				String answer2 = selectedslidenode.getProperty("answer2");
				String answer3 = selectedslidenode.getProperty("answer3");
				String answer4 = selectedslidenode.getProperty("answer4");
				String videourl = selectedslidenode.getProperty("videourl");
				String correctanswer = selectedslidenode.getProperty("correctanswer");
				String next = selectedslidenode.getProperty("next");
				String timeout = selectedslidenode.getProperty("timeout");
				String set = selectedslidenode.getProperty("set");
				if (set==null) { data.put("slideset","normal");} else {data.put("slideset",set);}
				if (question==null) { data.put("slidequestion","");} else {data.put("slidequestion",question);}
				if (answer1==null) { data.put("slideanswer1","");} else {data.put("slideanswer1",answer1);}
				if (answer2==null) { data.put("slideanswer2","");} else {data.put("slideanswer2",answer2);}				
				if (answer3==null) { data.put("slideanswer3","");} else {data.put("slideanswer3",answer3);}	
				if (answer4==null) { data.put("slideanswer4","");} else {data.put("slideanswer4",answer4);}	
				if (answer4==null) { data.put("slidecorrectanswer","");} else {data.put("slidecorrectanswer",correctanswer);}
				if (videourl==null) { data.put("slidevideourl","");} else {data.put("slidevideourl",videourl);}	
				if (next==null) { data.put("slidenext","");} else {data.put("slidenext",next);}	
				if (timeout==null) { data.put("slidetimeout","");} else {data.put("slidetimeout",timeout);}
			}
		}
		
	}
	
	private void addItemSlides(JSONObject data) {
		model.setProperty("@contentrole","mainapp");
		System.out.println("E="+model.getNode("@item"));
		FSList slidesList = model.getList("@item/slide");
		
		FSList resultslides = new FSList();
		List<FsNode> nodes = slidesList.getNodes();
		if (nodes != null) {
			for (Iterator<FsNode> iter = nodes.iterator(); iter.hasNext();) {
				FsNode node = (FsNode) iter.next();
				if (node.getId().equals(selectedslide)) {
					node.setProperty("classname", "station_mainapp_slide_selected");
					selectedslidenode = node;
				} else {
					node.setProperty("classname", "station_mainapp_slide");
				}
				String type=node.getProperty("type");
				if (type==null) node.setProperty("type","image");
				System.out.println("T="+type+" "+node.asXML());
  				resultslides.addNode(node);
				
			}
		}
		JSONObject slides = resultslides.toJSONObject("en","url,classname");
		data.put("slides",slides);
	}

	
	public void onImageUploadState(ModelEvent e) {
		FsPropertySet ps = (FsPropertySet)e.target;
		String action = ps.getProperty("action");
		String progress = ps.getProperty("progress");
		System.out.println("PR="+progress);
		if (progress!=null && progress.equals("100")) {
			System.out.println("URL="+ps.getProperty("url"));
    		model.setProperty("@item/slide/"+selectedslide+"/imageurl",ps.getProperty("url"));
    		model.notify("@station","changed"); 
    		fillPage();
    		
		}
	}
	
	public void onVideoUploadState(ModelEvent e) {
		FsPropertySet ps = (FsPropertySet)e.target;
		String action = ps.getProperty("action");
		String progress = ps.getProperty("progress");
		System.out.println("PR2="+progress);
		if (progress!=null && progress.equals("100")) {
			System.out.println("URL2="+ps.getProperty("url"));
    		model.setProperty("@item/slide/"+selectedslide+"/videourl",ps.getProperty("url"));
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
	
	private void setVideoSettings(String upid) {
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
	
    private String getNewSlideId() {
    	int result = 0;
    	// get the list from domain so see if we are on a number idea we can use.
		FSList slides = model.getList("@item/slide");
		if (slides!=null && slides.size()>0) { // if we have stations already lets find the highest number
			for(Iterator<FsNode> iter = slides.getNodes().iterator() ; iter.hasNext(); ) {
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
    
    private String getNewItemId() {
    	int result = 0;
    	// get the list from domain so see if we are on a number idea we can use.
		FSList questions = model.getList("@items");
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
