import org.openqa.selenium.*;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.*;

import java.awt.*;

class AutomateBrowserActions {
    java.awt.Dimension screenDimensions = Toolkit.getDefaultToolkit().getScreenSize();
    private final int windowWidth = (int) (screenDimensions.getWidth()*0.5);
    private final int windowHeight = (int) (screenDimensions.getHeight()*0.8);
    private final Cursor cursor = new Cursor();
    private Performer performer;

    private void customizeBrowser(){
        System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        performer = new Performer(driver);
        resizeAndRepositionWindow(driver);

        System.out.println("Running: " + windowWidth + " by " + windowHeight);
        System.out.println("Cookies: " + driver.manage().getCookies());

        /*JavascriptExecutor js = (JavascriptExecutor) driver;
        WebDriverWait waiter = new WebDriverWait(driver,5);*/
        denyLocationAccess(driver);
        denyGoogleCookies(driver);
        sleep(1000);
        denyCameraAccess(driver);
        /*while(true){
            sleep(2000);
            displayPositionRelativeToWindow(driver.manage().window().getPosition().getX(),driver.manage().window().getPosition().getY());
            System.out.println(driver.getPageSource().length());
        }*/


    }

    private void denyLocationAccess(WebDriver driver){
        driver.get("chrome://settings/content/location");
        sleep(1);
        resetCursorPosition();
        movePointerWithinWindowBy((int) (windowWidth*0.5),150);
        clickPointerWithinWindow();
    }
    private void denyCameraAccess(WebDriver driver){
        resetCursorPosition();
        driver.get("chrome://settings/content/camera");
        movePointerWithinWindowBy((int) (windowWidth*0.5),150);
        clickPointerWithinWindow();
        //irregular render times of camera dropdown box
        movePointerWithinWindowBy(0,50);
        clickPointerWithinWindow();

    }
    private void denyGoogleCookies(WebDriver driver){
        driver.get("https://www.google.com");
        sleep(3);
        WebElement customizePrivacySettingsElement = driver.findElement(By.xpath("/html/body/div[2]/div[2]/div[3]/span/div/div/div[3]/button[1]"));
        clickOnElement(customizePrivacySettingsElement);

        WebElement searchPersonalization = driver.findElement(By.xpath("/html/body/c-wiz/div/div/div/div[2]/div[3]/div[2]/div/div[2]/div[1]/div/button/span"));
        clickOnElement(searchPersonalization);

        WebElement ytHistory = driver.findElement(By.xpath("/html/body/c-wiz/div/div/div/div[2]/div[4]/div[2]/div/div[2]/div[1]/div/button/span"));
        clickOnElement(ytHistory);

        WebElement adPersonalization = driver.findElement(By.xpath("/html/body/c-wiz/div/div/div/div[2]/div[5]/div[2]/div[2]/div/div[2]/div[1]/div/button/span"));
        clickOnElement(adPersonalization);

        WebElement confirmPrivacySettingsButton = driver.findElement(By.xpath("/html/body/c-wiz/div/div/div/div[2]/form/div/button/span"));
        clickOnElement(confirmPrivacySettingsButton);
    }

    private void resetCursorPosition(){
        movePointerWithinWindowBy(-cursor.getCursorXPosition(), -cursor.getCursorYPosition());
    }
    /** mouse location according to window */
    private void displayPositionRelativeToWindow(int windowXLocation, int windowYLocation){
        int x = (int) MouseInfo.getPointerInfo().getLocation().getX();
        int y = (int) MouseInfo.getPointerInfo().getLocation().getY();
        int xPosition = x-windowXLocation;
        int yPosition = y-128-windowYLocation;
        System.out.println(xPosition + " " + yPosition);
    }
    private void displayCursorPosition(){
        System.out.println(cursor.getCursorXPosition() + " " + cursor.getCursorYPosition());
    }
    private void resizeAndRepositionWindow(WebDriver driver){
        driver.manage().window().setSize(new Dimension(windowWidth, windowHeight));
        driver.manage().window().setPosition(new Point(windowWidth, 0));
    }
    private void movePointerWithinWindowBy(int x,int y){
        //handle target out of bounds exception
        int nextX = cursor.getCursorXPosition()+x;
        int nextY = cursor.getCursorYPosition()+y;
        if(nextX<0 || nextX>windowWidth || nextY<0 || nextY>windowHeight) {
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
    private void clickOnElement(WebElement webElement){
        webElement.click();
        sleep(1);
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
