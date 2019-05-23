package org.iot.dsa.dslink.webscrape;

import org.iot.dsa.node.DSInfo;
import org.iot.dsa.node.DSMap;
import org.iot.dsa.node.DSValueType;
import org.iot.dsa.node.action.ActionInvocation;
import org.iot.dsa.node.action.ActionResult;
import org.iot.dsa.node.action.DSAction;
import org.openqa.selenium.firefox.FirefoxDriver;

public interface DocumentFetcher {
    
    public static DSAction makeFetchDocAction() {
        DSAction act = new DSAction() {
            @Override
            public void prepareParameter(DSInfo target, DSMap parameter) {
            }
            
            @Override
            public ActionResult invoke(DSInfo target, ActionInvocation request) {
                ((DocumentFetcher) target.get()).fetchDocument(request.getParameters());
                return null;
            }
        };
        act.addParameter("Name", DSValueType.STRING, null);
        act.addParameter("URL", DSValueType.STRING, null);
        return act;
    }
    
    public void fetchDocument(DSMap parameters);
    
    public FirefoxDriver getWebClient();

}
