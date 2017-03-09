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
package org.springfield.lou.application.types;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springfield.fs.FSList;
import org.springfield.fs.FSListManager;
import org.springfield.fs.FsNode;
import org.springfield.lou.application.*;
import org.springfield.lou.controllers.*;
import org.springfield.lou.controllers.dashboard.DashboardController;
import org.springfield.lou.controllers.dashboard.login.LoginController;
import org.springfield.lou.homer.LazyHomer;
import org.springfield.lou.screen.*;
import org.springfield.lou.servlet.LouServlet;

public class MupopdashboardApplication extends Html5Application {
	
	/**
	 * The mst app is started by a auto trigger of a first screen coming in
	 * @param id
	 */
 	public MupopdashboardApplication(String id) {
		super(id); 
		this.setSessionRecovery(true);
	}
 	
 	/**
 	 * new screen attached to the application
 	 */
    public void onNewScreen(Screen s) {
    		s.setLanguageCode("en"); // set detault langauge to English
			s.get("#screen").attach(new ScreenController()); // add the main ScreenController (mostly for eddie)
			s.get("#content").append("div","dashboard",new DashboardController()); // Start our main Controller
			s.get("#screen").append("div","login",new LoginController()); // Also allow a user to log in
     }
    
    
	public void maintainanceRun() {
		super.maintainanceRun();
		Iterator<Screen> iter = getScreenManager().getScreens().values().iterator();
		if (iter.hasNext()) {
			Screen scr = iter.next();
			scr.getModel().notify("/app['timers']","10");
		}
	
	}
    

}
