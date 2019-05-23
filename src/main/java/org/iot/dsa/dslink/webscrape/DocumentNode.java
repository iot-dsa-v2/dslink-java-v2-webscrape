package org.iot.dsa.dslink.webscrape;

import java.util.concurrent.TimeUnit;
import org.iot.dsa.node.DSIObject;
import org.iot.dsa.node.DSMap;
import org.iot.dsa.node.DSString;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WindowType;
import org.openqa.selenium.firefox.FirefoxDriver;


public class DocumentNode extends ElementNode implements DocumentFetcher {
    
    protected String url;
    protected String windowHandle;
    protected DocumentFetcher parent;
    
    public DocumentNode(DocumentFetcher parent, String url) {
        this.url = url;
        this.parent = parent;
    }
    
    public DocumentNode() {
        
    }
    
    @Override
    protected void declareDefaults() {
        super.declareDefaults();
        declareDefault("Fetch Document", DocumentFetcher.makeFetchDocAction());
    }
    
    @Override
    protected void onStarted() {
        super.onStarted();
        DSIObject urlobj = get("URL");
        if (urlobj instanceof DSString) {
            this.url = ((DSString) urlobj).toString();
        }
        parent = (DocumentFetcher) getAncestor(DocumentFetcher.class);
    }
    
    @Override
    protected void onStable() {
        super.onStable();
        init();
    }

    
    protected void init() {
        if (url != null) {
            put("URL", url);
            try {
                FirefoxDriver driver = getWebClient();
                synchronized (driver) {
                    driver.switchTo().newWindow(WindowType.TAB);
                    driver.get(url);
                    driver.manage().timeouts().implicitlyWait(4,
                        TimeUnit.SECONDS);
                    windowHandle = driver.getWindowHandle();
                }

            } catch (Exception e) {
                warn("", e);
            }
        }
    }


    @Override
    public void fetchDocument(DSMap parameters) {
        String name = parameters.getString("Name");
        String url = parameters.getString("URL");
        put(name, new DocumentNode(this, url));
    }

    @Override
    public FirefoxDriver getWebClient() {
        return parent.getWebClient();
    }
    
    @Override
    public String getWindowHandle() {
        return windowHandle;
    }

    @Override
    public WebElement getElement() {
        if (windowHandle != null) {
            FirefoxDriver driver = getWebClient();
            synchronized (driver) {
                driver.switchTo().window(windowHandle);
                return driver.findElementByTagName("html");
            }
        } else {
            return null;
        }
    }
}
