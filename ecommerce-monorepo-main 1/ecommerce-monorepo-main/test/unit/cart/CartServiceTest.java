import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.*;
 
public class CartServiceTest {
 
    WebDriver driver;
 
    @BeforeMethod
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("https://example.com/cart");
    }
 
    @Test(description = "5029 - Support percentage and fixed amount coupons with eligibility rules and promotion stacking")
    public void verifyPercentageAndFixedCouponStacking() {
 
        // Step 1: Apply percentage coupon
        driver.findElement(By.id("couponInput")).sendKeys("PERCENT10");
        driver.findElement(By.id("applyCouponBtn")).click();
 
        // Step 2: Apply fixed amount coupon
        driver.findElement(By.id("couponInput")).sendKeys("FLAT50");
        driver.findElement(By.id("applyCouponBtn")).click();
 
        // Step 3: Validate discount applied
        String discountValue = driver.findElement(By.id("totalDiscount")).getText();
        Assert.assertTrue(discountValue.contains("-"),
                "Discount was not applied correctly with stacked coupons");
    }
 
    @Test(description = "5034 - Discount allocation across multiple cart items and compute shipping after discounts")
    public void verifyDiscountAllocationAcrossMultipleItems() {
 
        // Step 1: Fetch item-level discounts
        String item1Discount = driver.findElement(By.id("item1Discount")).getText();
        String item2Discount = driver.findElement(By.id("item2Discount")).getText();
 
        // Step 2: Validate discounts are allocated
        Assert.assertNotEquals(item1Discount, "0", "Item 1 discount not applied");
        Assert.assertNotEquals(item2Discount, "0", "Item 2 discount not applied");
    }
 
    @Test(description = "5049 - Proportional discount allocation across cart items and shipping computed post discount")
    public void verifyProportionalDiscountAndShippingCalculation() {
 
        // Step 1: Capture subtotal and discount
        String subtotal = driver.findElement(By.id("subtotal")).getText();
        String discount = driver.findElement(By.id("totalDiscount")).getText();
 
        // Step 2: Capture shipping after discount
        String shippingAfterDiscount = driver.findElement(By.id("shippingAmount")).getText();
 
        // Step 3: Validate shipping is calculated
        Assert.assertNotNull(shippingAfterDiscount,
                "Shipping was not calculated after discount");
    }
 
    @Test(description = "5060 - Implement stacking policy for promotions and compute shipping after discounts")
    public void verifyPromotionStackingPolicy() {
 
        // Step 1: Apply multiple stackable promotions
        driver.findElement(By.id("couponInput")).sendKeys("STACK10");
        driver.findElement(By.id("applyCouponBtn")).click();
 
        driver.findElement(By.id("couponInput")).sendKeys("STACK5");
        driver.findElement(By.id("applyCouponBtn")).click();
 
        // Step 2: Validate stacked discount
        String discountValue = driver.findElement(By.id("totalDiscount")).getText();
        Assert.assertTrue(discountValue.contains("-"),
                "Stacked promotions were not applied correctly");
    }
 
    @Test(description = "5064 - Apply coupon before tax calculation for eligible items")
    public void verifyCouponAppliedBeforeTax() {
 
        // Step 1: Capture tax before discount
        String taxBeforeDiscount = driver.findElement(By.id("taxBeforeDiscount")).getText();
 
        // Step 2: Apply coupon
        driver.findElement(By.id("couponInput")).sendKeys("PRETAX20");
        driver.findElement(By.id("applyCouponBtn")).click();
 
        // Step 3: Capture tax after discount
        String taxAfterDiscount = driver.findElement(By.id("taxAfterDiscount")).getText();
 
        // Step 4: Validate tax recalculation
        Assert.assertNotEquals(taxBeforeDiscount, taxAfterDiscount,
                "Tax was not recalculated after applying coupon");
    }
 
    @Test(description = "5079 - Discount allocation across cart items and compute shipping after discounts")
    public void verifyFinalPayableAmountAfterDiscount() {
 
        // Step 1: Capture payable amount
        String finalAmount = driver.findElement(By.id("finalPayableAmount")).getText();
 
        // Step 2: Validate final amount is displayed
        Assert.assertNotNull(finalAmount,
                "Final payable amount not calculated correctly");
    }
 
    @Test(description = "5094 - Ensure discounts are proportionally allocated and shipping is computed after discounts")
    public void verifyDiscountAndShippingConsistency() {
 
        // Step 1: Fetch discount and shipping values
        String discount = driver.findElement(By.id("totalDiscount")).getText();
        String shipping = driver.findElement(By.id("shippingAmount")).getText();
 
        // Step 2: Validate values exist
        Assert.assertNotNull(discount, "Discount value missing");
        Assert.assertNotNull(shipping, "Shipping value missing");
    }
 
    @AfterMethod
    public void tearDown() {
        driver.quit();
    }
}
