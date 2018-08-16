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

import java.util.Iterator;

import org.json.simple.JSONObject;
import org.springfield.fs.FsNode;
import org.springfield.fs.FsPropertySet;
import org.springfield.lou.controllers.Html5Controller;
import org.springfield.lou.controllers.roominfo.RoomInfoController;
import org.springfield.lou.controllers.station.apps.generic.ContentSelectEditController;
import org.springfield.lou.controllers.station.apps.generic.WaitScreenEditController;
import org.springfield.lou.model.ModelEvent;
import org.springfield.lou.screen.Screen;

public class QuizQuestionController extends Html5Controller {
	
	public QuizQuestionController() {
		
	}
	
	public void attach(String sel) {
		selector = sel;
		fillPage();
	}
	
	
	private void fillPage() {
		JSONObject data = new JSONObject();
		data.put("domain",model.getProperty("mupop"));	
		data.put("exhibitionid",model.getProperty("@exhibitionid"));
		data.put("stationid",model.getProperty("@stationid"));
		
		String qtype = model.getProperty("@itemquestion/type");
		if (qtype==null || qtype.equals("")) {
			model.setProperty("@itemquestion/type","plain");
		}
		data.put("questiontype",qtype);
		data.put("questiontitle", model.getProperty("@itemquestion/question"));
		data.put("answer1", model.getProperty("@itemquestion/answer1"));
		data.put("answer2", model.getProperty("@itemquestion/answer2"));
		data.put("answer3", model.getProperty("@itemquestion/answer3"));
		data.put("answer4", model.getProperty("@itemquestion/answer4"));
		data.put("correctanswer", model.getProperty("@itemquestion/correctanswer"));
		screen.get(selector).render(data);

		screen.get("#station_mainapp_questiontype").on("change","onQuestionType", this);
		screen.get("#station_mainapp_done").on("mouseup","onDone", this);
		screen.get("#station_mainapp_deletequestion").on("mouseup","station_mainapp_deletequestionconfirm","onDeleteQuestion", this);
		screen.get("#station_mainapp_item_editquestiontitle").on("change","onQuestionChange", this);
		screen.get("#station_mainapp_item_editanswer1").on("change","onAnswer1Change", this);
		screen.get("#station_mainapp_item_editanswer2").on("change","onAnswer2Change", this);
		screen.get("#station_mainapp_item_editanswer3").on("change","onAnswer3Change", this);
		screen.get("#station_mainapp_item_editanswer4").on("change","onAnswer4Change", this);	
		screen.get("#station_mainapp_item_editcorrectanswer").on("change","onCorrectAnswerChange", this);
	}
	
	public void onQuestionType(Screen s,JSONObject data) {
		String value = (String)data.get("value");
		System.out.println("VALUE="+value);
		model.setProperty("@itemquestion/type",value);
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

	
	public void onDone(Screen s,JSONObject data) {
		screen.get(selector).remove();
	}
	
	public void onDeleteQuestion(Screen s,JSONObject data) {
		String confirm = (String)data.get("station_mainapp_deletequestionconfirm");
		if (confirm.equals("yes")) {
			System.out.println("DEL="+model.deleteNode("@itemquestion"));
			screen.get(selector).remove();
		}
	}
	
	
}
