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
package org.springfield.lou.controllers.screens;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONObject;
import org.springfield.fs.FSList;
import org.springfield.fs.FsNode;
import org.springfield.lou.controllers.Html5Controller;
import org.springfield.lou.controllers.room.RoomController;
import org.springfield.lou.model.ModelEvent;
import org.springfield.lou.screen.Screen;

public class ScreensController extends Html5Controller {
	
	/**
	 * Station controller where the station and its app get edited
	 */
	public ScreensController() {
	}
	
	/**
	 * fill our space on our screen
	 */
	public void attach(String sel) {
		selector = sel; // save for later use
		fillPage(); // fill the screen
		model.onNotify("/shared[timers]/5second","on5SecondTimer",this);	
		model.onNotify("/shared['mupop']/hids[alive]", "onAliveMessage", this);
	}
	
	public void on5SecondTimer(ModelEvent e) {
		fillPage();
	}
	
	public void onAliveMessage(ModelEvent e) {
		fillPage();
	}
	
	/**
	 * fill our space on our screen
	 */
	private void fillPage() {
		JSONObject data = getScreensList();
		screen.get(selector).render(data); 
 		screen.get("#screens_donebutton").on("mouseup","onDoneButton", this);
	}
	
    public void onDoneButton(Screen s,JSONObject data) {
       	screen.get("#content").append("div","room",new RoomController()); // if user wanted a old exhibition lets open the default room for it
		screen.get(selector).remove();
    }
    
    private JSONObject getScreensList() {
    	long nowdate = new Date().getTime();
    	FSList results = new FSList();
		FSList list = model.getList("/domain/mupop/config/hids/hid");
		//JSONObject data = list.toJSONObject("en","stationid,exhibitionid");
    
		List<FsNode> nodes = list.getNodes();
		for (Iterator<FsNode> iter = nodes.iterator(); iter.hasNext();) {
			FsNode node = iter.next();
			String username = node.getProperty("username");
			String stationid = node.getProperty("stationid");
			String exhibtionid = node.getProperty("exhibitionid");
			FsNode newnode = new FsNode("screen",node.getId());
			newnode.setProperty("stationid", stationid);
			newnode.setProperty("exhibitionid", exhibtionid);
			if (username!=null && !username.equals("")) {
				newnode.setProperty("username",username);
			} else {
				if (stationid!=null && !stationid.equals("")) {
					username = "noterik";
					newnode.setProperty("username","noterik"); // this doesn't make me happy
				}
			}
			
	    	FsNode hidalive = model.getNode("@hidsalive/hid/"+node.getId()); // auto create if not there !
	       	try {
		    		long lastseen = Long.parseLong(hidalive.getProperty("lastseen"));
		    		if ((nowdate-lastseen)<20*1000) {
		    			newnode.setProperty("state", "up");
		    			newnode.setProperty("state-color", "green");
		    		} else {
		    			newnode.setProperty("state", "down");
		    			newnode.setProperty("state-color", "red");	
		    		}
		    } catch(Exception e) {
		    	//e.printStackTrace();
    			newnode.setProperty("state", "down");
    			newnode.setProperty("state-color", "red");	
		    }

			
			
			if (stationid!=null && !stationid.equals("")) {
				FsNode enode = model.getNode("/domain['mupop']/user['"+username+"']/exhibition['"+exhibtionid+"']");
				if (enode!=null) {
					String exname  = enode.getProperty("name")+"-"+enode.getProperty("location")+"-"+enode.getProperty("timeframe");
					newnode.setProperty("exhibitionname",exname);
					FsNode snode = model.getNode("/domain['mupop']/user['"+username+"']/exhibition['"+exhibtionid+"']/station['"+stationid+"']");
					if (snode!=null) {
						String stname =  snode.getProperty("labelid")+"-"+snode.getProperty("name");
						newnode.setProperty("stationname",stname);
						newnode.setProperty("appname",snode.getProperty("app"));
						String codeselect = snode.getProperty("codeselect");
						if (codeselect!=null && !codeselect.equals("")) {
							newnode.setProperty("codeselect",codeselect);
						}
					}	
				}
			}
			results.addNode(newnode);
			
		}
		return results.toJSONObject("en","stationid,exhibitionid,username,exhibitionname,stationname,appname,codeselect,state,state-color");
    }
    
	

 	 
}
