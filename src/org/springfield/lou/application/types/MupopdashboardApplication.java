/* 
*SceneApplication.java
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
import org.springfield.lou.homer.LazyHomer;
import org.springfield.lou.screen.*;
import org.springfield.lou.servlet.LouServlet;

public class MupopdashboardApplication extends Html5Application {
	
	
 	public MupopdashboardApplication(String id) {
		super(id); 
		this.setSessionRecovery(true);
	}
 	
    public void onNewScreen(Screen s) {
    		s.setLanguageCode("en");
			s.get("#screen").attach(new ScreenController());
			s.get("#screen").append("div","dashboard",new DashboardController());
     }
    
    

}
