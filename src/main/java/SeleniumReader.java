import org.openqa.selenium.*;
import org.openqa.selenium.Point;
import org.openqa.selenium.chrome.ChromeDriver;
import java.io.*;
import java.util.*;

class SeleniumReader {
    private final Runtime run = Runtime.getRuntime();
    private final String homeDir = System.getProperty("user.home");
    BufferedReader reader;

    public static void main(String[] args) throws IOException {
        SeleniumReader selenium = new SeleniumReader();
        selenium.runScript();
    }

    private void runScript() throws IOException{
        //actual:445
        //available:344
        final int ELEMENTS = 445;
        try {
            reader = new BufferedReader(new FileReader(homeDir +"\\Downloads\\zalamo.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String [] connectionData = new String[3];
        for(int i = 0;i<3;i++){
            connectionData[i]=reader.readLine();
        }

        System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        JavascriptExecutor js = (JavascriptExecutor) driver;
        //driver.manage().window().setPosition(new Point(950,5));
        driver.manage().window().maximize();

        driver.get(connectionData[0]);
        sleep(10);

        WebElement passElement = driver.findElement(By.name("password"));
        sleep(10);

        passElement.sendKeys(connectionData[1]);
        passElement.submit();
        sleep(10);

        WebElement sessionDropDown = driver.findElement(By.className("dropdown-toggle"));
        sessionDropDown.click();
        sleep(100);

        String outputDirectory = homeDir+"\\Desktop\\pictures";
        //String outputDirectory = new Scanner(System.in).nextLine() + "/";

        for(int scroll = 0;scroll<5;scroll++){
            js.executeScript("window.scrollBy(0,1)");
            sleep(1);
        }
        WebElement element1 = driver.findElement(By.cssSelector(".image.loaded"));
        assert element1 != null;
        element1.click();
        sleep(1000);

        String urlPath = element1.getAttribute("style");
        int indexOfImage = urlPath.indexOf("/image");
        urlPath = connectionData[2]+urlPath.substring(indexOfImage,urlPath.indexOf("\"",indexOfImage));

        js.executeScript("window.open()");
        ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());
        driver.switchTo().window(tabs.get(1));
        int numIndexStart = urlPath.indexOf('=')+1;
        int numIndexEnd = urlPath.indexOf('&');
        StringBuilder pathBuilder = new StringBuilder(urlPath);

        int currentNumber = Integer.parseInt(pathBuilder.substring(numIndexStart,numIndexEnd))-2;
        //int currentNumber=18899452;
        for(int i = 0;i<ELEMENTS;i++) {
            pathBuilder.replace(numIndexStart,numIndexEnd, String.valueOf(currentNumber));
            driver.get(pathBuilder.toString());
            //req limit
            sleep(getRandom(1000,1500));

            //move from temp dir to desired dir
            TakesScreenshot ss = (TakesScreenshot) driver;
            File saveToFile = ss.getScreenshotAs(OutputType.FILE);
            run.exec("cmd /C move " + saveToFile.getPath() + " " + outputDirectory);
            int fileNameIndex = lastIndexOf('\\',saveToFile.getPath());
            String command = "cmd /C rename " + outputDirectory + saveToFile.getPath().substring(fileNameIndex) + " " + currentNumber+".png";
            run.exec(command);

            currentNumber++;
            sleep(500);
        }
        driver.close();
    }

    private static void sleep(long time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    private static int lastIndexOf(char c, String str){
        for(int index = str.length()-1;index>-1;index--){
            if(str.charAt(index)==c){
                return index;
            }
        }
        return 0;
    }
    public static int getRandom(int min, int max){
        return (int)(Math.random()*(max-min+1))+min;
    }

}
