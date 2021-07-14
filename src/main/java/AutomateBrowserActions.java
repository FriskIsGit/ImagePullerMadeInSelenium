import org.openqa.selenium.*;
import org.openqa.selenium.Point;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.Toolkit;
import java.awt.MouseInfo;
import java.util.List;

class AutomateBrowserActions {
    private static final java.awt.Dimension screenDimensions = Toolkit.getDefaultToolkit().getScreenSize();
    //4:3 window - minimal dimensions
    private static final int WINDOW_WIDTH = 960;
    private static final int WINDOW_HEIGHT = 720;
    private final WebDriver driver;
    private final Actions actions;
    private final CursorPosition cursor;
    private AutomateBrowserActions(){
        cursor = new CursorPosition();
        driver = new ChromeDriver();
        actions = new Actions(driver);
    }

    private void changeBrowserLanguage(){
        resizeAndRepositionWindow();
        driver.get("chrome://settings/languages");
        resetCursorPosition();
        moveBy((int) (WINDOW_WIDTH *0.5),155);
        click();
        moveBy((int) (WINDOW_WIDTH *0.3),100);
        click();
        actions.sendKeys(Keys.ARROW_DOWN).perform();
        actions.sendKeys(Keys.RETURN).perform();
        click();
        click();
    }
    private void changeDefaultSearchEngineTo(int element) {
        final int NUMBER_OF_SEARCH_ENGINES = 4;
        resizeAndRepositionWindow();
        driver.get("chrome://settings");
        resetCursorAndGainFocus();
        for(int i = 0;i<36;i++){
            actions.sendKeys(Keys.ARROW_DOWN).perform();
        }
        moveBy((int) (WINDOW_WIDTH *0.23),25);
        click();
        for(int r = 0;r<NUMBER_OF_SEARCH_ENGINES;r++){
            actions.sendKeys(Keys.ARROW_UP).perform();
        }
        for(int i = 0;i<element;i++){
            actions.sendKeys(Keys.ARROW_DOWN).perform();
        }
        actions.sendKeys(Keys.RETURN).perform();
    }

    private void denyLocationAccess(){
        resizeAndRepositionWindow();
        driver.get("chrome://settings/content/location");
        resetCursorPosition();
        moveBy((int) (WINDOW_WIDTH *0.5),155);
        click();
    }
    private void denyCameraAccess(){
        resizeAndRepositionWindow();
        driver.get("chrome://settings/content/camera");
        resetCursorPosition();
        moveBy((int) (WINDOW_WIDTH *0.5),155);
        click();
        //irregular render times of camera dropdown box
        moveBy(0,45);
        click();
    }
    private void denyGoogleCookies(){
        driver.get("https://www.google.com");
        WebDriverWait waiter = new WebDriverWait(driver,15);
        WebElement customizePrivacySettingsElement = driver.findElement(By.xpath("/html/body/div[2]/div[2]/div[3]/span/div/div/div[3]/button[1]"));
        waiter.until(ExpectedConditions.visibilityOf(customizePrivacySettingsElement));
        customizePrivacySettingsElement.click();

        List<WebElement> toggleButtonsList = driver.findElements(By.xpath("//div[@class='uScs5d']//div[@data-is-touch-wrapper='true']"));
        //every second button is an off button
        toggleButtonsList.get(0).click();
        toggleButtonsList.get(2).click();
        toggleButtonsList.get(4).click();

        WebElement confirmPrivacySettingsButton = driver.findElement(By.xpath("/html/body/c-wiz/div/div/div/div[2]/form/div/button/span"));
        confirmPrivacySettingsButton.click();
    }
    private void denyYahooCookies(){
        driver.get("https://www.yahoo.com");
        WebDriverWait waiter = new WebDriverWait(driver,15);

        WebElement scrollDownButton = driver.findElement(By.id("scroll-down-btn"));
        if(scrollDownButton.isDisplayed()) scrollDownButton.click();

        List<WebElement> listOfClickables = driver.findElements(By.tagName("a"));
        listOfClickables.get(listOfClickables.size()-2).click();

        WebElement legitimateInterest = driver.findElement(By.id("select-legit-all-purpose"));
        waiter.until(ExpectedConditions.visibilityOf(legitimateInterest));
        legitimateInterest.click();

        WebElement saveAndContinue = driver.findElement(By.name("agree"));
        waiter.until(ExpectedConditions.visibilityOf(saveAndContinue));
        saveAndContinue.click();

    }
    private void resetCursorAndGainFocus(){
        resetCursorPosition();
        //settings panel is 55 pixels high, gain focus on newly loaded page
        moveBy((int) (WINDOW_WIDTH *0.5),60);
        click();
    }
    private void resetCursorPosition(){
        moveBy(-cursor.getX(), -cursor.getY());
    }
    /** mouse location according to window */
    private void displayPositionRelativeToWindow(){
        int x = (int) MouseInfo.getPointerInfo().getLocation().getX();
        int y = (int) MouseInfo.getPointerInfo().getLocation().getY();
        int xPosition = x-driver.manage().window().getPosition().getX()-8;
        int yPosition = y-128-driver.manage().window().getPosition().getY();
        System.out.println(xPosition + " " + yPosition);
    }
    private void displayCursorPosition(){
        System.out.println(cursor.getX() + " " + cursor.getY());
    }
    private void resizeAndRepositionWindow(){
        driver.manage().window().setSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        driver.manage().window().setPosition(new Point(screenDimensions.width- WINDOW_WIDTH, 0));
    }
    private void moveBy(int x, int y){
        int nextX = cursor.getX()+x;
        int nextY = cursor.getY()+y;
        //handle target out of bounds exception
        if(nextX<0 || nextX> WINDOW_WIDTH -16|| nextY<0 || nextY> WINDOW_HEIGHT -136) {
            System.out.println("Values exceeding window dimensions");
            return;
        }else{
            cursor.setX(nextX);
            cursor.setY(nextY);
        }
        actions.moveByOffset(x,y).build().perform();
    }
    private void click(){
        actions.click().perform();
    }
    private void logCursorPositionFor(long seconds){
        long time = System.currentTimeMillis()+seconds*1000;
        Thread displayThread = new Thread(new Runnable() {
            public void run()  {
                while(time>System.currentTimeMillis()){
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    displayPositionRelativeToWindow();
                }
            }
        });
        displayThread.start();
    }

    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver.exe");
        AutomateBrowserActions browserActions = new AutomateBrowserActions();

        browserActions.logCursorPositionFor(120);
        browserActions.changeBrowserLanguage();
        browserActions.denyLocationAccess();
        browserActions.denyCameraAccess();
        browserActions.denyGoogleCookies();
        browserActions.changeDefaultSearchEngineTo(3);
        browserActions.denyYahooCookies();

    }
}
class CursorPosition {
    private int x = 0;
    private int y = 0;

    public void setX(int x) {
        this.x = x;
    }
    public void setY(int y) {
        this.y = y;
    }
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
}

