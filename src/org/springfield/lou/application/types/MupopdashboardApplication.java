/* 
* MuPoP dashboard where users can create quick popup museum exhibitions. Work in combination with
* the MuPoP mobile app and the MuPoP station app.
* 
* Copyright (c) 2016 Noterik B.V.

*/
package org.springfield.lou.application.types;

import java.util.ArrayList;
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
    
    

}
