import org.openqa.selenium.*;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.*;
import java.util.List;

class AutomateBrowserActions {
    java.awt.Dimension screenDimensions = Toolkit.getDefaultToolkit().getScreenSize();
    //4:3 window - minimal dimensions
    private final int windowWidth = 960;
    private final int windowHeight = 720;
    private final Cursor cursor = new Cursor();
    private Performer performer;
    private final long timeInMs = 60000;

    private void customizeBrowser(){
        System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        performer = new Performer(driver);
        resizeAndRepositionWindow(driver);

        System.out.println("Running: " + windowWidth + " by " + windowHeight);

        initiateDisplayThread(driver);
        denyGoogleCookies(driver);
        //denyYahooCookies(driver);
        //denyCameraAccess(driver);
    }

    private void changeBrowserLanguage(WebDriver driver){
        driver.get("chrome://settings/languages");

        resetCursorPosition();
        movePointerWithinWindowBy((int) (windowWidth*0.5),155);
        clickPointerWithinWindow();
        movePointerWithinWindowBy((int) (windowWidth*0.3),100);
        clickPointerWithinWindow();
        performer.getActionsBuilder().sendKeys(Keys.ARROW_DOWN).perform();
        performer.getActionsBuilder().sendKeys(Keys.RETURN).perform();
        clickPointerWithinWindow();
        clickPointerWithinWindow();
    }
    private void changeDefaultSearchEngineTo(WebDriver driver,int element) {
        driver.get("chrome://settings");
        sleep(1);
        resetCursorAndGainFocus();
        for(int i = 0;i<36;i++){
            performer.getActionsBuilder().sendKeys(Keys.ARROW_DOWN).perform();
        }
        movePointerWithinWindowBy((int) (windowWidth*0.23),25);
        clickPointerWithinWindow();
        for(int r = 0;r<4;r++){
            performer.getActionsBuilder().sendKeys(Keys.ARROW_UP).perform();
        }
        for(int i = 0;i<element;i++){
            performer.getActionsBuilder().sendKeys(Keys.ARROW_DOWN).perform();
        }
        performer.getActionsBuilder().sendKeys(Keys.RETURN).perform();
    }

    private void denyLocationAccess(WebDriver driver){
        driver.get("chrome://settings/content/location");
        sleep(1);
        resetCursorPosition();
        movePointerWithinWindowBy((int) (windowWidth*0.5),155);
        clickPointerWithinWindow();
    }
    private void denyCameraAccess(WebDriver driver){
        driver.get("chrome://settings/content/camera");
        resetCursorPosition();
        movePointerWithinWindowBy((int) (windowWidth*0.5),155);
        clickPointerWithinWindow();
        //irregular render times of camera dropdown box
        movePointerWithinWindowBy(0,45);
        clickPointerWithinWindow();
    }
    private void denyGoogleCookies(WebDriver driver){
        WebDriverWait wait= new WebDriverWait(driver,15);
        driver.get("https://www.google.com");
        // //div[@class='uScs5d']//div[@data-is-touch-wrapper='true']
        WebElement customizePrivacySettingsElement = driver.findElement(By.xpath("/html/body/div[2]/div[2]/div[3]/span/div/div/div[3]/button[1]"));
        wait.until(ExpectedConditions.visibilityOf(customizePrivacySettingsElement));
        customizePrivacySettingsElement.click();

        List<WebElement> listOfWebElements = driver.findElements(By.xpath("//div[@class='uScs5d']//div[@data-is-touch-wrapper='true']"));
        listOfWebElements.get(0).click();
        listOfWebElements.get(2).click();
        listOfWebElements.get(4).click();

        WebElement confirmPrivacySettingsButton = driver.findElement(By.xpath("/html/body/c-wiz/div/div/div/div[2]/form/div/button/span"));
        confirmPrivacySettingsButton.click();
    }
    private void denyYahooCookies(WebDriver driver){
        driver.get("https://www.yahoo.com");
        WebElement scrollDownButton = driver.findElement(By.id("scroll-down-btn"));
        clickOnElement(scrollDownButton);

        WebElement manageSettingsButton = driver.findElement(By.cssSelector("btn.secondary.manage-settings"));
        clickOnElement(manageSettingsButton);

    }
    private void resetCursorAndGainFocus(){
        resetCursorPosition();
        //settings panel is 55 pixels high, gain focus on newly loaded page
        movePointerWithinWindowBy((int) (windowWidth*0.5),60);
        clickPointerWithinWindow();
    }
    private void resetCursorPosition(){
        movePointerWithinWindowBy(-cursor.getCursorXPosition(), -cursor.getCursorYPosition());
    }
    /** mouse location according to window */
    private void displayPositionRelativeToWindow(WebDriver driver){
        int x = (int) MouseInfo.getPointerInfo().getLocation().getX();
        int y = (int) MouseInfo.getPointerInfo().getLocation().getY();
        int xPosition = x-driver.manage().window().getPosition().getX()-8;
        int yPosition = y-128-driver.manage().window().getPosition().getY();
        System.out.println(xPosition + " " + yPosition);
    }
    private void displayCursorPosition(){
        System.out.println(cursor.getCursorXPosition() + " " + cursor.getCursorYPosition());
    }
    private void resizeAndRepositionWindow(WebDriver driver){
        driver.manage().window().setSize(new Dimension(windowWidth, windowHeight));
        driver.manage().window().setPosition(new Point(screenDimensions.width-windowWidth, 0));
    }
    private void movePointerWithinWindowBy(int x,int y){
        //handle target out of bounds exception
        int nextX = cursor.getCursorXPosition()+x;
        int nextY = cursor.getCursorYPosition()+y;
        if(nextX<0 || nextX>windowWidth-16|| nextY<0 || nextY>windowHeight-136) {
            System.out.println("Values exceeding window dimensions");
            return;
        }else{
            cursor.setCursorXPosition(nextX);
            cursor.setCursorYPosition(nextY);
        }
        performer.getActionsBuilder().moveByOffset(x,y).build().perform();
    }
    private void clickPointerWithinWindow(){
        performer.getActionsBuilder().click().perform();
    }
    private void initiateDisplayThread(WebDriver driver) {
        long start = System.currentTimeMillis();
        Thread displayThread = new Thread(new Runnable() {
            public void run() {
                while(start+timeInMs>System.currentTimeMillis()){
                    sleep(500);
                    displayPositionRelativeToWindow(driver);
                }
            }
        });
        displayThread.start();
    }
    private void clickOnElement(WebElement webElement){
        webElement.click();
    }
    private void sleep(long time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new AutomateBrowserActions().customizeBrowser();
    }
}
class Cursor{
    private int cursorXPosition = 0;
    private int cursorYPosition = 0;

    public void setCursorXPosition(int cursorXPosition) {
        this.cursorXPosition = cursorXPosition;
    }
    public void setCursorYPosition(int cursorYPosition) {
        this.cursorYPosition = cursorYPosition;
    }

    public int getCursorXPosition() {
        return cursorXPosition;
    }
    public int getCursorYPosition() {
        return cursorYPosition;
    }
}
class Performer {
    private final Actions actionBuilder;
    protected Performer(WebDriver driver) {
        this.actionBuilder = new Actions(driver);
    }
    public Actions getActionsBuilder(){
        return actionBuilder;
    }
}
