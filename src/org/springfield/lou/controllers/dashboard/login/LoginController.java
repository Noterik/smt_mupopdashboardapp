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
package org.springfield.lou.controllers.dashboard.login;

import java.util.Date;

import org.json.simple.JSONObject;
import org.springfield.fs.Fs;
import org.springfield.fs.FsNode;
import org.springfield.lou.controllers.Html5Controller;
import org.springfield.lou.screen.Screen;
import org.springfield.mojo.interfaces.ServiceInterface;
import org.springfield.mojo.interfaces.ServiceManager;

public class LoginController extends Html5Controller {

	String usernamepath;
	
	/**
	 * Controls the login part
	 */
	public LoginController() {
		
	}
	
	/**
	 * moment this controller gets attached to target screen on given id
	 * 
	 * @see org.springfield.lou.controllers.Html5Controller#attach(java.lang.String)
	 */
	public void attach(String s) {
		selector = s; // set our id for later use
    	JSONObject data = new JSONObject(); 	
    	data.put("feedback", ""); // set feedback to empty there is no problem yet :)
    	screen.get(selector).render(data); // render the data using mustache
    	screen.get("#loginsubmitbutton").on("mouseup","loginname,loginpassword","checkName",this); // wait until user logs in
  	}
	
	/**
	 * check if the user has logged in correctly if not provide feedback and try again
	 * @param s
	 * @param data
	 */
    public void checkName(Screen s,JSONObject data) {
 		String name = (String)data.get("loginname"); // get the login name
		String password = (String)data.get("loginpassword"); // get the entered password
		
		ServiceInterface barney = ServiceManager.getService("barney"); // get barney to check if they are valid
		if (barney!=null) { 
			String ticket = barney.get("login("+s.getApplication().getDomain()+","+name+","+password+")", null, null);

			if (!ticket.equals("-1")) { // barney gave a valid ticket so lets login in the app
					model.setProperty("@username", name); // set user name in screen space for other controllers
					screen.onNewUser(name); // signal new user to app (still needed?)
					screen.get(selector).html("Logged in as : "+name); // set login name in frontend not very nice way
					screen.get(selector).css("width","170px"); // should not be hardcoded will make it a class
					screen.get("#mupoplogo").show(); // make the logo visible also not nice way
				
			} else {
				screen.get("#feedback").html("wrong account or password"); //set feedback user did something wrong
			}
		}

    }
}
