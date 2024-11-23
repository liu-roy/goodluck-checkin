package com.goodluck.checkin.site;


import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * @author liuleyi
 */
@Slf4j
public class BuguTv {
    // 配置部分
    private static final String USERNAME = "u3ck03223";  // 替换为您的用户名
    private static final String PASSWORD = "12745224049";  // 替换为您的密码
    private static final String LOGIN_URL = "https://www.bugutv.org";
    private static final String USER_URL = "https://www.bugutv.org/user";

    // 初始化浏览器（以 Chrome 为例）
    private static WebDriver initBrowser() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
//        options.addArguments("--headless");  // 启用无头模式
//        options.addArguments("--no-sandbox");  // 在某些 Linux 环境下需要
//        options.addArguments("--disable-dev-shm-usage");  // 解决共享内存问题
        WebDriver browser = new ChromeDriver(options);
        return browser;
    }


    public static void loginAndCheckin() {
        WebDriver browser = initBrowser();
        try {
            // 打开登录页面
            browser.get(LOGIN_URL);

            // 点击登录按钮
            WebDriverWait wait = new WebDriverWait(browser, 10);
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(),'登录')]"))).click();

            // 输入用户名和密码并提交登录
            WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[name='username']")));
            WebElement passwordField = browser.findElement(By.cssSelector("input[type='password']"));
            WebElement loginButton = browser.findElement(By.xpath("//button[contains(@class,'go-login')]"));

            // 登录成功后延迟3秒跳转到用户页面
            Thread.sleep(1000);
            usernameField.sendKeys(USERNAME);
            Thread.sleep(1000);
            passwordField.sendKeys(PASSWORD);
            Thread.sleep(1000);
            loginButton.click();

            // 登录成功后延迟3秒跳转到用户页面
            Thread.sleep(3000);
            browser.get(USER_URL);

            try {
                // 检查是否已签到
                WebElement button = new WebDriverWait(browser, 10).until(
                        ExpectedConditions.presenceOfElementLocated(By.xpath("//button[contains(@class, 'go-user-qiandao')]"))
                );

                if (button.getAttribute("outerHTML").contains("disabled")) {
                    log.info("今日已签到，无需重复操作。");
                } else {
                    log.info("未签到，尝试签到...");
                    button.click();

                    // 检查签到成功提示
                    WebElement successMessage = new WebDriverWait(browser, 10).until(
                            ExpectedConditions.presenceOfElementLocated(By.xpath("//p[contains(text(), ' 今日签到成功')]"))
                    );
                    log.info("签到成功！");
                }

            } catch (Exception e) {
                log.info("未找到签到按钮，可能页面加载不完全或元素缺失。");
            }

        } catch (Exception e) {
            log.info("出现错误: " + e.getMessage());
        } finally {
            // 关闭浏览器
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            browser.quit();
        }
    }


    public static void main(String[] args) {
        loginAndCheckin();
    }

}
