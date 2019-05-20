package org.iot.dsa.dslink.webscrape;

import java.io.IOException;
import org.iot.dsa.dslink.DSMainNode;
import org.iot.dsa.node.DSMap;
import org.iot.dsa.node.DSString;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.SilentCssErrorHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * The main and only node of this link.
 *
 * @author Aaron Hansen
 */
public class MainNode extends DSMainNode implements DocumentFetcher {
    
//    private static MainNode instance;
    
    private WebClient webClient;

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
        //declareDefault("Add Cookie", makeAddCookieAction());
        declareDefault("Fetch Document", DocumentFetcher.makeFetchDocAction());
//        declareDefault("Add Login Form", makeAddLoginFormAction());
        declareDefault("Help",
                       DSString.valueOf("https://github.com/iot-dsa-v2/dslink-java-v2-example"))
                .setTransient(true)
                .setReadOnly(true);
    }

//    @Override
//    protected void onStarted() {
//        super.onStarted();
////        instance = this;
//    }
    
    @Override
    protected void onStable() {
        super.onStable();
//        try {
//            HtmlPage page = getWebClient().getPage("https://www.bedashboard.com/TenantPortal/");
//            String text = page.asText();
//        } catch (FailingHttpStatusCodeException | IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
        
    }
    
//    private DSAction makeAddCookieAction() {
//        DSAction act = new DSAction() {
//            @Override
//            public void prepareParameter(DSInfo target, DSMap parameter) {
//            }
//            
//            @Override
//            public ActionResult invoke(DSInfo target, ActionInvocation request) {
//                ((MainNode) target.get()).addCookie(request.getParameters());
//                return null;
//            }
//        };
//        act.addParameter("Name", DSValueType.STRING, null);
//        act.addParameter("Content", DSValueType.STRING, null);
//        return act;
//    }
//
//    private void addCookie(DSMap parameters) {
//        String name = parameters.getString("Name");
//        String content = parameters.getString("Content");
//        DSMap cookies = getCookies();
//        cookies.put(name, content);
//        put("Cookies", cookies);
//    }
    
    public void fetchDocument(DSMap parameters) {
        String name = parameters.getString("Name");
        String url = parameters.getString("URL");
        put(name, new DocumentNode(this, url));
    }

    @Override
    public WebClient getWebClient() {
        if (webClient == null) {
            webClient = new WebClient();
            webClient.setCssErrorHandler(new SilentCssErrorHandler());
        }
        return webClient;
    }
    
//    private DSIObject makeAddLoginFormAction() {
//        DSAction act = new DSAction() {
//            
//            @Override
//            public void prepareParameter(DSInfo target, DSMap parameter) {
//            }
//            
//            @Override
//            public ActionResult invoke(DSInfo target, ActionInvocation request) {
//                ((MainNode) target.get()).addLoginForm(request.getParameters());
//                return null;
//            }
//        };
//        act.addParameter("Name", DSValueType.STRING, null);
//        act.addParameter("URL", DSValueType.STRING, null);
//        act.addParameter("Inputs", new DSMap(), "e.g. {\"UserName\": \"admin\", \"Password\": \"admin1234\"}");
//        act.addParameter("CSRF Token Name", DSValueType.STRING, null);
//        return act;
//    }
//    
//    private void addLoginForm(DSMap parameters) {
//        
//    }
    
//    public DSMap getCookies() {
//        DSIObject cookiesObj = get("Cookies");
//        if (cookiesObj instanceof DSMap) {
//            return (DSMap) cookiesObj;
//        } else {
//            return new DSMap();
//        }
//    }
    
//    public static DSMap getCookies() {
//        DSIObject cookiesObj = instance.get("Cookies");
//        if (cookiesObj instanceof DSMap) {
//            return ((DSMap) cookiesObj).copy();
//        } else {
//            return new DSMap();
//        }
//    }
}
