package org.iot.dsa.dslink.webscrape;

import org.iot.dsa.dslink.DSMainNode;
import org.iot.dsa.node.DSIObject;
import org.iot.dsa.node.DSInfo;
import org.iot.dsa.node.DSMap;
import org.iot.dsa.node.DSString;
import org.iot.dsa.node.DSValueType;
import org.iot.dsa.node.action.ActionInvocation;
import org.iot.dsa.node.action.ActionResult;
import org.iot.dsa.node.action.DSAction;

/**
 * The main and only node of this link.
 *
 * @author Aaron Hansen
 */
public class MainNode extends DSMainNode {
    
    private static MainNode instance;

    // Nodes must support the public no-arg constructor.  Technically this isn't required
    // since there are no other constructors...
    public MainNode() {
    }

    /**
     * Defines the permanent children of this node type, their existence is guaranteed in all
     * instances.  This is only ever called once per, type per process.
     */
    @Override
    protected void declareDefaults() {
        super.declareDefaults();
        declareDefault("Add Cookie", makeAddCookieAction());
        declareDefault("Fetch Document", makeFetchDocAction());
        declareDefault("Help",
                       DSString.valueOf("https://github.com/iot-dsa-v2/dslink-java-v2-example"))
                .setTransient(true)
                .setReadOnly(true);
    }
    
    @Override
    protected void onStarted() {
        super.onStarted();
        instance = this;
    }
    
    
    private DSAction makeAddCookieAction() {
        DSAction act = new DSAction() {
            @Override
            public void prepareParameter(DSInfo target, DSMap parameter) {
            }
            
            @Override
            public ActionResult invoke(DSInfo target, ActionInvocation request) {
                ((MainNode) target.get()).addCookie(request.getParameters());
                return null;
            }
        };
        act.addParameter("Name", DSValueType.STRING, null);
        act.addParameter("Content", DSValueType.STRING, null);
        return act;
    }

    private void addCookie(DSMap parameters) {
        String name = parameters.getString("Name");
        String content = parameters.getString("Content");
        DSMap cookies = getCookies();
        cookies.put(name, content);
        put("Cookies", cookies);
    }

    private DSAction makeFetchDocAction() {
        DSAction act = new DSAction() {
            @Override
            public void prepareParameter(DSInfo target, DSMap parameter) {
            }
            
            @Override
            public ActionResult invoke(DSInfo target, ActionInvocation request) {
                ((MainNode) target.get()).fetchDocument(request.getParameters());
                return null;
            }
        };
        act.addParameter("Name", DSValueType.STRING, null);
        act.addParameter("URL", DSValueType.STRING, null);
        return act;
    }
    
    private void fetchDocument(DSMap parameters) {
        String name = parameters.getString("Name");
        String url = parameters.getString("URL");
        put(name, new DocumentNode(url));
    }
    
//    public DSMap getCookies() {
//        DSIObject cookiesObj = get("Cookies");
//        if (cookiesObj instanceof DSMap) {
//            return (DSMap) cookiesObj;
//        } else {
//            return new DSMap();
//        }
//    }
    
    public static DSMap getCookies() {
        DSIObject cookiesObj = instance.get("Cookies");
        if (cookiesObj instanceof DSMap) {
            return ((DSMap) cookiesObj).copy();
        } else {
            return new DSMap();
        }
    }
}
