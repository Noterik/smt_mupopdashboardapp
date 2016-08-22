package org.springfield.lou.controllers.dashboard;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONObject;
import org.springfield.fs.FSList;
import org.springfield.fs.FSListManager;
import org.springfield.fs.Fs;
import org.springfield.fs.FsNode;
import org.springfield.lou.controllers.Html5Controller;
import org.springfield.lou.screen.Screen;

public class DashboardController extends Html5Controller {
	

	public DashboardController() {
	}
	
	public void attach(String sel) {
		System.out.println("dashboard controller attached called");
		selector = sel;
		screen.loadStyleSheet("dashboard/dashboard.css");
		JSONObject data = new JSONObject();
		data.put("location",model.getProperty("/screen/location"));
 		screen.get(selector).parsehtml(data);
		screen.get("#dashboard_button1").on("mouseup","setButton1",this);
		screen.get("#dashboard_button2").on("mouseup","setButton2",this);
		screen.get("#dashboard_button3").on("mouseup","setButton3",this);
	}
	
	public void setButton1(Screen s,JSONObject data) {
		model.setProperty("/domain/mecanex/app/sceneplayer/location/"+model.getProperty("/screen/location")+"/scene","blue");

	}
	
	public void setButton2(Screen s,JSONObject data) {
		model.setProperty("/domain/mecanex/app/sceneplayer/location/"+model.getProperty("/screen/location")+"/scene","fiat500");
	}
	
	public void setButton3(Screen s,JSONObject data) {
		model.setProperty("/domain/mecanex/app/sceneplayer/location/"+model.getProperty("/screen/location")+"/scene","bosch");
	}

 	 
}
