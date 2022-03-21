
 import groovy.grape.Grape

 // @Grab(group="org.seleniumhq.selenium", module="selenium-java", version="99.0.4844.51")
 /*
  @Grab(group="org.seleniumhq.selenium", module="selenium-java", version="4.1.2")
 @Grab(group="org.seleniumhq.selenium", module="selenium-chrome-driver", version="99.0.4844.51")
 @Grab(group="org.seleniumhq.selenium", module="selenium-support", version="4.1.2")
  @Grab('org.seleniumhq.selenium:selenium-java:4.1.2')
  */
@GrabResolver(name='selenium', root='http://maven.org/')

@Grab(group="org.seleniumhq.selenium", module="selenium-java", version="4.1.2")
 
 
@Grab(group="org.seleniumhq.selenium", module="selenium-chrome-driver", version="4.1.2")
@Grab(group="org.seleniumhq.selenium", module="selenium-firefox-driver", version="4.1.2")
@Grab(group="org.seleniumhq.selenium", module="selenium-support", version="4.1.2")

/*
    **  Below was the attempt to get ExplicitWait going, throws compiler errors for documented usage...
*/
// @Grab(group="com.google.guava", module="guava", version="31.1-jre")
// import com.google.common.base.Preconditions;
// import com.google.common.collections;
// import com.google.common.collect.*;

import org.openqa.selenium.*
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebDriver.*
import org.openqa.selenium.By
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeDriver.*
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxDriver.*
import org.openqa.selenium.firefox.FirefoxOptions
import org.openqa.selenium.Keys
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait 
import groovy.transform.InheritConstructors
import java.util.*

class MainClass {
   static void main(String[] args) {
      def x = 5
      println('Starting Testing');  
      
      TestClass test = new TestClass();
      println('created')
      test.NavigateSearchProducts('stainless work table')
      test.TestSearchResult("Table", test.getInitalPage().getDriver() );
 
      
   }
}

class TestClass {
    public TestClass()
    {
        this.initalPage = new InitialPageObject("https://www.webstaurantstore.com/");

        this.initalPage.InitalizeForChrome()
        print 'PageObjectCreated'  

    }

    public void NavigateSearchProducts(String SearchString )
    {
        this.initalPage.NavigateToPage();

        this.initalPage.FillFieldById('searchval', SearchString )
        this.initalPage.PressEnterForId('searchval');
        
        // First attempt at By.XPath...  Complex, didn't return anythihng...
        // this.initalPage.ClickByXPath('../..//form[@id="searchForm"]/div/button[@type="submit"]');


        // Attempts to use xpath to find submit button, all do not find it, so we press enter instead....
        // //*[@id="searchForm"]
        // //*[@id="searchForm"]/div/button
        // //*[@id="searchForm"]/div/button
        // //*[@id="searchForm"]/div/button
        // /html/body/div[3]/div[1]/div[2]/div[2]/div/div[2]/div/form/div/button
        // initalPage.ClickByXPath('../..//form[@id="searchForm"]/div/button[@type="submit"]')
    }

    public void TestSearchResult(String ResultMatchString, driver){
        searchResultPage = new SearchResultPageObject("default", driver)
        searchResultPage.WaitForResultPage();
        println ("Searching for ${ResultMatchString} in search result.")
        def lastProduct = searchResultPage.CheckResultList(ResultMatchString)
        searchResultPage.TestCartAdd(lastProduct)

        cartPage = new CartPageObject("default", driver)
        cartPage.TestCartClear()

        // For production we'd do this, but I ommit it for traceability
        // cartPage.Stop()
    }

    public getInitalPage() {
        return initalPage;
    }

    InitialPageObject initalPage;
    SearchResultPageObject searchResultPage;
    CartPageObject cartPage;
}

class PageObject {
    public PageObject(String url) {
       this.strURL = url; 
       debugLevel=4
    }

    def debugLevel

    def debug4(action) {
        if (debugLevel >= 4) {
            action();
        }
    }

    def debug5(action) {
        if (debugLevel >= 5) {
            action();
        }
    }
    public void InitalizeForChrome()
    {
        System.setProperty('webdriver.chrome.bin','C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe');
        this.driver = new ChromeDriver();
    }

    public Boolean NavigateToPage() {
        println ("NavigateToPage:: to ${strURL}");
        driver.get("${strURL}");
    }

    public FillFieldById(String elementId, String strValue ){
        println ("FillFieldById:: ${elementId} // ${strValue}");
        def element = driver.findElement(By.id(elementId));
        assert element != null : "Could not find element(${elementId}";

        element.sendKeys(strValue);

        return element;
    }

    public PressEnterForId(String elementId) {

        def element = driver.findElement(By.id(elementId));
        assert element != null : "Could not find element(${elementId}";

        element.sendKeys(Keys.RETURN)
     }

    public void waitElementPresentById(String elementId) {

        println ("Waiting on ${elementId}");
        try {
            driver.wait(2);
            
            // If it would work, below is cleaner
            // WebDriverWait _wait = new WebDriverWait(driver,Duration.ofSeconds(30));
            // def elem ;
            //  elem = _wait.until(  expected_conditions.presence_of_element_located((By.ID, elementId))
        }
        catch(any){

        }

        return
    }

    public Boolean ClickByXPath (String path){

        this.checkPageIsReady();

        println ("${path}");
        def element
        try
        {
            element = driver.FindElement(By.xpath(path));
        } catch (any){};

        assert element != null : "Could not find element(${path}";
        element.Click();
    }

    public Boolean ClickById(String elementId){
        println ("${elementId}");
        def element
        try
        {
            element = driver.findElement(By.id(elementId));
        } catch (any){};

        assert element != null : "Could not find element(${elementId}";
        element.Click();
    }

 
    public void checkPageIsReady() {

        JavascriptExecutor js = (JavascriptExecutor) this.driver;

        // Initially bellow given if condition will check ready state of page.
        if (js.executeScript("return document.readyState").toString().equals("complete")) {
            System.out.println("Page Is loaded.");
            return;
        }

        // This loop will iterate for 25 times to check If page Is ready after
        // every 1 second.
        // If the page loaded successfully, it will terminate the for loop
        for (int i = 0; i < 25; i++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }

            // To check page ready state.
            if (js.executeScript("return document.readyState").toString().equals("complete")) {
                break;
            }
        }

        return
    }

    public Stop() {
        println('Closing down our browser.')
        driver.close();
        driver.quit();
    }
    
    public ArrayList GetMatchingElementsById(String idString){
        ArrayList out = driver.findElements(By.id(idString));

        this.debug5(Closure: {println "Output: ${out}"})
        
        return out;
    }

    public ArrayList getMatchingElementsByAttribute(ArrayList Elements, String attribute, String value)
    {
        def ArrayList out = []

        Elements.each({ Element -> 
                if ( Element.getAttribute(attribute) == value ) {
                    debug4({println "found match of ${value} : ${Element.getText()}"})
                    out.add(Element);
                }
        }) 
        return out  
    }
    
    public getDriver() {
        return driver;
    }

    WebDriver driver;
    def strURL;
}

class SearchResultPageObject extends PageObject {
    public SearchResultPageObject(String url, driver) {
        super(url)
        this.driver = driver
    }    

    public WaitForResultPage(){
        this.waitElementPresentById('tpage');
        println "Found ID(tpage) on result page."
    }

    public CheckResultList(String matchString){
        def elementList;
        println "Getting our result list."
        elementList = this.GetMatchingElementsById('ProductBoxContainer')

        def last = null

            // elementList = this.driver.findElements(By.xpath('/*//a[@data-testid=\'item-description\']'))
            // def descriptionLink = element.findElement(By.xpath('/*//a[@data-testid=\'item-description\']'))
            
            // Since Above By.XPath doesn't seem to work, and there's no good class or id,
            //    we go through all links in the block to find the correct data-testid then test
            //    the description(link) for "Table"
        elementList.each({ element -> 
            def descriptionLinks = null
            try {
                descriptionLinks = element.findElements(By.tagName("A"))
            } catch (any) { println "failed for ${element.getText()}"}
           
            descriptionLinks.each({ descriptionLink -> 
                if ( descriptionLink.getAttribute('data-testid') == 'itemDescription' ) {
                    if ( descriptionLink.getText().contains('Table') ) {
                        last = element;
                        debug5({ println "Found: ${descriptionLink.getText()}"})
                    } else {
                        throw(new Exception("Description does not contain 'Table' ${descriptionLink.getText()}"))
                    }
                }
            })   
        })
        return last
    }

    public TestCartAdd(last) {
        // Add the last Product in search to the cart by data-testid
        def buttons = last.findElements(By.tagName("input"))
        debug4({println "AddtoCart: ${buttons}"})
        def matches = this.getMatchingElementsByAttribute(buttons, "data-testid", "itemAddCart")
        matches[0].click()
        // Things seem to be out of sync slightly by the time we get to the cart...  a bug?
        Thread.sleep(500)

        // Go to the cart by the popup cart nav link
        def button = driver.findElement(By.id('watnotif-wrapper'))
        button.findElement(By.className('btn-primary')).click()
    }
}

class CartPageObject extends PageObject {
    public CartPageObject(url, driver) {
        super(url)
        this.driver = driver
    } 

    public TestCartClear() {
        driver.findElement(By.className("emptyCartButton")).click()

        // Find and dismiss the confirmation Modal
        def modals = driver.findElements(By.className('modal-dialog'))
        def clearModal = null

        // Find the modal with the title "Empty Cart"
        modals.each {modal -> 
            try {
                def candidate = modal.findElement(By.className("modal-title"))
                if (candidate.getText().contains("Empty Cart")){
                    clearModal = modal
                }
            } catch (any) { 
                // No real action to take, just skip the modal if there's an error
            }
        }
        
        // Click it's primary button
        clearModal.findElement(By.className('btn-primary')).click()

        Thread.sleep(500)        
    }   
}
class InitialPageObject extends PageObject
{
    public InitialPageObject(String url) {
        super(url)
    }


}