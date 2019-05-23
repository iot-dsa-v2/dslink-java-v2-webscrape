package org.iot.dsa.dslink.webscrape;

import java.io.File;
import org.apache.commons.lang3.ArchUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.arch.Processor.Arch;
import org.iot.dsa.dslink.DSMainNode;
import org.iot.dsa.node.DSMap;
import org.iot.dsa.node.DSString;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

/**
 * The main and only node of this link.
 *
 * @author Aaron Hansen
 */
public class MainNode extends DSMainNode implements DocumentFetcher {
    
//    private static MainNode instance;
    
    private FirefoxDriver webClient;

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
        declareDefault("Fetch Document", DocumentFetcher.makeFetchDocAction());
        declareDefault("Help",
                       DSString.valueOf("https://github.com/iot-dsa-v2/dslink-java-v2-example"))
                .setTransient(true)
                .setReadOnly(true);
    }


    public void fetchDocument(DSMap parameters) {
        String name = parameters.getString("Name");
        String url = parameters.getString("URL");
        put(name, new DocumentNode(this, url));
    }

    @Override
    public FirefoxDriver getWebClient() {
        if (webClient == null) {
            FirefoxBinary firefoxBinary = new FirefoxBinary();
            firefoxBinary.addCommandLineOptions("--headless");
            System.setProperty("webdriver.gecko.driver", getGeckoDriverPath());
            FirefoxOptions firefoxOptions = new FirefoxOptions();
            firefoxOptions.setBinary(firefoxBinary);
            webClient = new FirefoxDriver(firefoxOptions);
        }
        return webClient;
    }
    
    private String getGeckoDriverPath() {
        String extension = "";
        String folder;
        if (SystemUtils.IS_OS_WINDOWS) {
            if (ArchUtils.getProcessor().getArch().equals(Arch.BIT_64)) {
                folder = "win64";
            } else {
                folder = "win32";
            }
            extension = ".exe";
        } else if (SystemUtils.IS_OS_LINUX) {
            if (ArchUtils.getProcessor().getArch().equals(Arch.BIT_64)) {
                folder = "linux64";
            } else {
                folder = "linux32";
            }
        } else if (SystemUtils.IS_OS_MAC) {
            folder = "macos";
        } else {
            return null;
        }
        return "geckodriver" + File.separator + folder + File.separator + "geckodriver" + extension;
    }
    
}
