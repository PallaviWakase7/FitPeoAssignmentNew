import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.JavascriptExecutor;

public class FitPeoAssignment {

    public static void main(String[] args) {
        // Set the path to the ChromeDriver executable
        System.setProperty("webdriver.chrome.driver", "./drivers/chromedriver.exe");

        // Initialize WebDriver
        WebDriver driver = new ChromeDriver();

        // Corrected WebDriverWait constructor using Duration
        WebDriverWait wait = new WebDriverWait(driver, 10);  // Updated for Selenium 4
        Actions actions = new Actions(driver);

        try {
            // Navigate to the FitPeo Homepage
            driver.get("https://fitpeo.com"); // Replace with the actual URL
            driver.manage().window().maximize();

            // Navigate to the Revenue Calculator Page
            WebElement revenueCalculatorLink = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Revenue Calculator")));
            revenueCalculatorLink.click();

            // Scroll to the slider section using JavaScript
            WebElement sliderSection = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".MuiTypography-root.MuiTypography-h3.crimsonPro.css-k7aeh2")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", sliderSection);
            actions.moveToElement(sliderSection).perform();

            // Adjust the Slider to 820
            WebElement slider = driver.findElement(By.cssSelector("input[type='range']"));
            adjustSlider(driver, slider, 820); // Helper function to move slider
            WebElement sliderValue = driver.findElement(By.cssSelector("input[id=':R57alklff9da:']")); // Replace with actual selector
            assert sliderValue.getAttribute("value").equals("820") : "Slider value is not updated to 820";

            // Update the Text Field to 560
            WebElement textField = driver.findElement(By.cssSelector("input[id=':R57alklff9da:']")); // Replace with actual selector
            textField.clear();
            textField.sendKeys("560");
            textField.sendKeys("\n");

            // Validate the slider reflects 560
            assert slider.getAttribute("value").equals("560") : "Slider value did not update to 560";

            // Scroll to CPT Codes and select checkboxes
            WebElement cptSection = driver.findElement(By.cssSelector(".cpt-codes-section")); // Replace with actual selector
            actions.moveToElement(cptSection).perform();

            String[] cptCodes = {"99091", "99453", "99454", "99474"};
            for (String code : cptCodes) {
                WebElement checkbox = driver.findElement(By.cssSelector("input[value='" + code + "']")); // Replace with actual selector
                if (!checkbox.isSelected()) {
                    checkbox.click();
                }
            }

            // Validate Total Recurring Reimbursement
            WebElement reimbursementHeader = driver.findElement(By.cssSelector("#total-reimbursement")); // Replace with actual selector
            assert reimbursementHeader.getText().equals("$110700") : "Total Recurring Reimbursement did not match expected value";

            System.out.println("All steps executed successfully.");

        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
        } finally {
            // Close the browser
            driver.quit();
        }
    }

    // Helper function to adjust slider in smaller steps
    private static void adjustSlider(WebDriver driver, WebElement slider, int targetValue) {
        Actions actions = new Actions(driver);
        int currentValue = Integer.parseInt(slider.getAttribute("value"));
        int offset = targetValue - currentValue;

        // Smaller step size for incremental moves
        int stepSize = 5;
        
        while (currentValue != targetValue) {
            if (currentValue < targetValue) {
                actions.clickAndHold(slider).moveByOffset(stepSize, 0).release().perform();
                currentValue += stepSize;
            } else if (currentValue > targetValue) {
                actions.clickAndHold(slider).moveByOffset(-stepSize, 0).release().perform();
                currentValue -= stepSize;
            }

            // Brief delay to allow the UI to update
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
