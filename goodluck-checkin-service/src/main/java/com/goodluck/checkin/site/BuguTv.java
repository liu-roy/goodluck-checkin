package com.goodluck.checkin.site;


import com.goodluck.common.exception.BusinessException;
import com.google.common.base.Throwables;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static com.goodluck.checkin.utils.SeleniumUtils.safeWaitForElement;

/**
 * @author liuleyi
 */
@Slf4j
@Service
public class BuguTv {

    @Value("${bugutv.username}")
    private String username;

    @Value("${bugutv.password}")
    private String password; // 替换为您的密码

    private static final String LOGIN_URL = "https://www.bugutv.org";
    private static final String USER_URL = "https://www.bugutv.org/user";

    // 初始化浏览器（以 Chrome 为例）
    private static WebDriver initBrowser() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--start-maximized");
        options.addArguments("--headless");  // 启用无头模式
        options.addArguments("--no-sandbox");  // 在某些 Linux 环境下需要
        options.addArguments("--disable-dev-shm-usage");  // 解决共享内存问题
        WebDriver browser = new ChromeDriver(options);
        return browser;
    }


    @Scheduled(fixedDelay = 11 * 60 * 60 * 1000+30000)
    public void loginAndCheckIn() {
        log.info("--------------------布谷tv每日签到程序启动----------------------");
        WebDriver browser = initBrowser();
        try {
            WebDriverWait wait = new WebDriverWait(browser, 20); //
            browser.get(LOGIN_URL);

            // 点击登录按钮，触发登录弹窗
            WebElement loginTriggerButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("a.login-btn.navbar-button")));
            loginTriggerButton.click();

            // 等待表单内容加载完成
            WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("input[name='username']")));
            WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("input[name='password']")));
            WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button.go-login")));

            // 输入用户名和密码
            usernameField.sendKeys(username); // 替换为实际的用户名或邮箱
            passwordField.sendKeys(password);         // 替换为实际的密码

            // 点击登录按钮
            loginButton.click();

            // 等待登录完成后的页面标志（比如跳转或特定元素出现）
            waitForAjaxLoad(browser);

            // 跳转到用户页面
            browser.get(USER_URL);

            // 检查是否签到
            WebElement alreadySignIn = safeWaitForElement(browser, By.xpath("//button[contains(text(), '今日已签到')]"), 10);
            if (alreadySignIn != null) {
                log.info("布谷tv今日已签到！提示信息: " + alreadySignIn.getText());
                return;
            }
            log.info("布谷tv未签到，尝试签到...");
            WebElement signInButton = new WebDriverWait(browser, 20).until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[contains(@class, 'go-user-qiandao')]")));
            if (signInButton == null) {
                throw new BusinessException("签到按钮未找到！");
            }
            signInButton.click();
            alreadySignIn = safeWaitForElement(browser, By.xpath("//button[contains(text(), '今日已签到')]"), 10);
            if (alreadySignIn == null) {
                throw new BusinessException("布谷tv签到失败！未找到签到成功标记！");
            }
            log.info("布谷tv签到成功");
        } catch (Exception e) {
            log.error("签到过程中出现问题: [{}] ", Throwables.getStackTraceAsString(e));
        } finally {
            // 关闭浏览器
            try {
                Thread.sleep(3000);
                browser.quit();
            } catch (Exception e) {
                log.error("关闭浏览器失败 {}", Throwables.getStackTraceAsString(e));
            }

        }
        log.info("--------------------布谷tv每日签到程序结束----------------------");
    }
    private static void waitForAjaxLoad(WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, 15);
        wait.until(webDriver -> ((JavascriptExecutor) webDriver)
                .executeScript("return jQuery.active == 0")
                .equals(true));
    }
}
