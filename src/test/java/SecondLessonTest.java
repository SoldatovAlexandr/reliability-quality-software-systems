import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static io.github.bonigarcia.wdm.WebDriverManager.chromedriver;

public class SecondLessonTest {
    private final static String browserVersion = "93.0.4577.82";
    private final static String soundcloudUrl = "https://soundcloud.com/";
    private final static String soundcloudDownloadUrl = "https://sclouddownloader.net/";
    private final static List<String> optionsParams = List.of(
            "start-maximized",
            "enable-automation",
            "--no-sandbox",
            "--disable-infobars",
            "--disable-dev-shm-usage",
            "--disable-browser-side-navigation",
            "--disable-gpu"
    );
    private static final String SOUND_INPUT = "//*[@id=\"content\"]/div/div/div[2]/div/div[1]/span/span/form/input";
    private static final String SOUND_INPUT_BTN = "//*[@id=\"content\"]/div/div/div[2]/div/div[1]/span/span/form/button";
    private static final String ACCEPT_COOKIES = "//*[@id=\"onetrust-accept-btn-handler\"]";
    private static final String FIRST_TRACK = "//*[@id=\"content\"]/div/div/div[3]/div/div/div/ul/li[1]/div/div/div/div[1]/a/div/span";
    private static final String INPUT_TRACK_URL = "/html/body/div[2]/div/center/form/div/input";
    private static final String INPUT_TRACK_BTN = "/html/body/div[2]/div/center/form/div/div/input";
    private static final String DOWNLOAD_BTN = "/html/body/div[2]/div[2]/div[1]/center/a[1]";
    private static boolean cookiesAccepted = false;
    private static final String soundName = "1000-7";

    /**
     * Загрузчик музыки с SoundCloud
     */
    @Test
    public void testSoundcloud() {
        chromedriver().browserVersion(browserVersion).setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments(optionsParams);
        WebDriver driver = new ChromeDriver(options);

        String trackUrl = findTrackUrl(driver);
        downloadTrack(driver, trackUrl);
    }

    private static void acceptCookies(WebDriver driver) {
        if (!cookiesAccepted) {
            driver.findElement(By.xpath(ACCEPT_COOKIES)).click();
            cookiesAccepted = true;
        }
    }

    private static String findTrackUrl(WebDriver driver) {
        driver.get(soundcloudUrl);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        acceptCookies(driver);
        driver.findElement(By.xpath(SOUND_INPUT)).sendKeys(soundName);
        driver.findElement(By.xpath(SOUND_INPUT_BTN)).click();
        driver.findElement(By.xpath(FIRST_TRACK)).click();
        return driver.getCurrentUrl();
    }

    private static void downloadTrack(WebDriver driver, String trackUrl) {
        driver.get(soundcloudDownloadUrl);
        WebElement element = driver.findElement(By.xpath(INPUT_TRACK_URL));
        element.sendKeys(trackUrl);
        driver.findElement(By.xpath(INPUT_TRACK_BTN)).click();
        driver.findElement(By.xpath(DOWNLOAD_BTN)).click();
    }

}
