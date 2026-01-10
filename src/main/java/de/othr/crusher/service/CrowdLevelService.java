package de.othr.crusher.service;

import org.htmlunit.BrowserVersion;
import org.htmlunit.WebClient;
import org.htmlunit.html.HtmlImage;
import org.htmlunit.html.HtmlPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service for scraping crowd level information from gym websites.
 * Uses HtmlUnit to execute JavaScript and parse dynamic HTML.
 */
@Service
public class CrowdLevelService {

    private static final Logger logger = LoggerFactory.getLogger(CrowdLevelService.class);
    private static final Pattern MARGIN_PATTERN = Pattern.compile("margin-left\\s*:\\s*(\\d+(?:\\.\\d+)?)%");

    @Value("${crowdlevel.timeout:5000}")
    private int timeout;

    @Value("${crowdlevel.js-wait:2000}")
    private int jsWaitTime;

    /**
     * Fetches crowd level from a gym's website.
     * Uses HtmlUnit with JavaScript execution to handle dynamically loaded content.
     *
     * @param url the URL of the gym's website
     * @return CrowdLevel with percentage and status, or null if unavailable
     */
    public CrowdLevel getCrowdLevel(String url) {
        if (url == null || url.isBlank()) {
            logger.debug("Crowd level URL is null or empty");
            return null;
        }

        WebClient webClient = null;
        try {
            // Configure HtmlUnit WebClient
            webClient = new WebClient(BrowserVersion.CHROME);
            webClient.getOptions().setJavaScriptEnabled(true);
            webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setThrowExceptionOnScriptError(false);
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
            webClient.getOptions().setTimeout(timeout);

            // Suppress HtmlUnit warnings
            java.util.logging.Logger.getLogger("org.htmlunit").setLevel(java.util.logging.Level.OFF);

            logger.debug("Fetching crowd level from: {}", url);
            HtmlPage page = webClient.getPage(url);

            // Wait for JavaScript to execute
            webClient.waitForBackgroundJavaScript(jsWaitTime);

            // Find the crowd level pointer image
            HtmlImage img = page.querySelector(".crowd-level-pointer img");
            if (img == null) {
                logger.warn("Crowd level indicator image not found for URL: {}", url);
                return null;
            }

            // Extract margin-left percentage from style attribute
            String style = img.getAttribute("style");
            Matcher matcher = MARGIN_PATTERN.matcher(style);

            if (!matcher.find()) {
                logger.warn("Could not parse margin-left from style: {}", style);
                logger.debug("Available style attribute: {}", style);
                return null;
            }

            double percentage = Double.parseDouble(matcher.group(1));
            CrowdLevel crowdLevel = new CrowdLevel(percentage, getCrowdStatus(percentage));

            logger.info("Crowd level for {}: {}% ({})", url, percentage, crowdLevel.status());
            return crowdLevel;

        } catch (Exception e) {
            logger.error("Error fetching crowd level from {}: {}", url, e.getMessage());
            return null;
        } finally {
            if (webClient != null) {
                webClient.close();
            }
        }
    }

    /**
     * Maps crowd percentage to a human-readable status.
     */
    private String getCrowdStatus(double percentage) {
        if (percentage < 25) {
            return "Leer";
        } else if (percentage < 50) {
            return "Wenig los";
        } else if (percentage < 75) {
            return "Mittel";
        } else if (percentage < 90) {
            return "Viel los";
        } else {
            return "Sehr voll";
        }
    }

    /**
     * Crowd level information containing percentage and status text.
     */
    public record CrowdLevel(double percentage, String status) {}
}
