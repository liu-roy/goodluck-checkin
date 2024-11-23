package com.goodluck.checkin.site;


import com.goodluck.common.resp.R;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.Duration;

/**
 * @author liuleyi
 */
@Slf4j
public class Shaoshuren {
    // 配置部分
    private static final String USERNAME = "232448@q.com";  // 替换为您的用户名
    private static final String PASSWORD = "2444545049";  // 替换为您的密码
    private static final String LOGIN_URL = "https://xn--gmqz83awjh.net/auth/login";
    private static final String USER_URL = "https://xn--gmqz83awjh.net/user";

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

    @PostMapping("/test")
    @ApiOperation("test")
    public R<Boolean> test() {
        loginAndCheckin();
        return R.success(Boolean.TRUE);
    }

    public static void loginAndCheckin() {
        WebDriver browser = initBrowser();
        try {
            // 打开登录页面
            browser.get(LOGIN_URL);

            // 等待页面加载
            WebDriverWait wait = new WebDriverWait(browser, Duration.ofSeconds(10).getSeconds());

            // 找到邮箱输入框并输入邮箱
            WebElement emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));
            Thread.sleep(1000);
            emailField.sendKeys(USERNAME); // 替换为你的邮箱

            // 找到密码输入框并输入密码
            WebElement passwordField = browser.findElement(By.id("password"));
            Thread.sleep(1000);
            passwordField.sendKeys(PASSWORD); // 替换为你的密码

            Thread.sleep(1000);
            // 点击登录按钮
            WebElement loginButton = browser.findElement(By.id("login_submit"));
            loginButton.click();

            // 等待登录完成
            wait.until(ExpectedConditions.urlContains("/user"));

            // 定位签到按钮
            WebElement signInButton = new WebDriverWait(browser, 10).until(
                    ExpectedConditions.presenceOfElementLocated(By.id("checkin")) // 按钮的 ID 为 "checkin"
            );
            if (signInButton == null) {
                log.error("签到按钮未找到！");
                return;
            }

            // 检查按钮是否已禁用（通过 "disabled" 属性或类名）
            if (signInButton == null ||signInButton.getAttribute("class").contains("disabled") ||
                    signInButton.getAttribute("disabled") != null) {
                log.info("今日已签到，无需重复操作。");
            } else {
                log.info("未签到，尝试签到...");
                signInButton.click(); // 点击签到按钮

                // 等待签到成功提示
                WebElement successMessage = new WebDriverWait(browser, 10).until(
                        ExpectedConditions.presenceOfElementLocated(By.xpath("//p[contains(text(), '签到成功')]"))
                );
                log.info("签到成功！提示信息: " + successMessage.getText());
            }
        } catch (Exception e) {
            log.error("签到过程中出现问题: " + e.getMessage());
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
