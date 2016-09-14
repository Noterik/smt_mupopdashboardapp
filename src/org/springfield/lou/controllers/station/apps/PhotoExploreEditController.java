package org.springfield.lou.controllers.station.apps;

import java.util.Iterator;

import org.json.simple.JSONObject;
import org.springfield.fs.FsNode;
import org.springfield.fs.FsPropertySet;
import org.springfield.lou.controllers.Html5Controller;
import org.springfield.lou.model.ModelEvent;
import org.springfield.lou.screen.Screen;

public class PhotoExploreEditController extends Html5Controller{
	
	private String tab="content";
	private String valuelist="appeditor_content_url";
	private String stationfullpath;
	
	public PhotoExploreEditController() {
		
	}
	
	public void attach(String sel) {
		selector = sel;
		System.out.println("SELECTOR="+selector);
		getVars();
		fillPage();
		model.onNotify("/screen['appsave']","onSaveWanted",this);
		model.onNotify("/screen/['appcancel']","onCancelWanted",this);
	}
	
	private void getVars() {
		stationfullpath=model.getProperty("/screen['vars']/stationfullpath");
		System.out.println("PATH="+stationfullpath);
		System.out.println("OLD VALUE="+model.getProperty(stationfullpath+"/url"));
		model.setProperty("/screen['data']/form['1']/appeditor_content_url",model.getProperty(stationfullpath+"/url"));
	}
	
	private void fillPage() {
		JSONObject data = new JSONObject();
		data.put(tab+"tab","true"); 
		
		data = memoryToData(data);
			
		screen.get(selector).render(data);
 		screen.get(".appmenu").on("mouseup",valuelist,"onAppMenu", this);
 		screen.get(selector).on("mouseleave",valuelist,"onLeaveMenu", this);
	}
	
	
	public void onAppMenu(Screen s,JSONObject data) {
		tab  = (String)data.get("id");
		saveToMemory(data);
		fillPage();
	}
	
	public void onLeaveMenu(Screen s,JSONObject data) {
		saveToMemory(data);
	}
	
	private JSONObject memoryToData(JSONObject data) {
		FsNode node = model.getNode("/screen['data']/form['1']");
		if (node!=null) {
			System.out.println("NODE="+node.asXML());
			String[] checklist = valuelist.split(",");
			for (int i=0;i<checklist.length;i++) {
				String check = checklist[i];
				String value = node.getProperty(check);
				if (value!=null) data.put(check, value);
			}
		}
		return data;
	}
	
	private void saveToMemory(JSONObject data) {
		FsNode node = model.getNode("/screen['data']/form['1']");
		if (node==null) {
			node = new FsNode("form","1");
			System.out.println("SAVE NODE TO MEM");
			model.putNode("/screen['data']", node);
		}
		String[] checklist = valuelist.split(",");
		for (int i=0;i<checklist.length;i++) {
			String check = checklist[i];
			Object o = data.get(check);
			if (o!=null) {
				String value = (String)o;
				String oldvalue = node.getProperty(check);
				if (oldvalue==null || !value.equals(oldvalue)) {
					node.setProperty(check, value);
				}
			}
		}
	}
	
	public void onCancelWanted(ModelEvent e) {
		System.out.println("ON CANCEL CALLED!!"+ selector);
		screen.get(selector).remove();
	}
	
	public void onSaveWanted(ModelEvent e) {
		FsNode node = model.getNode("/screen['data']/form['1']");
		if (node!=null) {
			System.out.println("SAVE WANTED ON FOLLOWING DATA="+node.asXML());
			for(Iterator<String> iter = node.getKeys(); iter.hasNext();) {
				String key = iter.next();
				String value = node.getProperty(key);
				if (key.equals("appeditor_content_url")) {
					model.setProperty(stationfullpath+"/url", value);
				}
			}
	
		}
		screen.get(selector).remove();
	}
	
	
}
