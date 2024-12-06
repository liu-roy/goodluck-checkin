//package com.goodluck.checkin.site;
//
//
//import com.goodluck.common.exception.BusinessException;
//import com.goodluck.common.resp.R;
//import com.google.common.base.Throwables;
//import io.github.bonigarcia.wdm.WebDriverManager;
//import io.swagger.annotations.ApiOperation;
//import lombok.extern.slf4j.Slf4j;
//import org.openqa.selenium.By;
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.WebElement;
//import org.openqa.selenium.chrome.ChromeDriver;
//import org.openqa.selenium.chrome.ChromeOptions;
//import org.openqa.selenium.support.ui.ExpectedConditions;
//import org.openqa.selenium.support.ui.WebDriverWait;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//import org.springframework.web.bind.annotation.PostMapping;
//
//import java.time.Duration;
//
//import static com.goodluck.checkin.utils.SeleniumUtils.safeWaitForElement;
//
///**
// * @author liuleyi
// */
//@Slf4j
//@Service
//public class Shaoshuren {
//
//    @Value("${shaoshuren.username}")
//    private String username;
//
//    @Value("${shaoshuren.password}")
//    private String password;
//
//    private static final String LOGIN_URL = "https://xn--gmqz83awjh.net/auth/login";
//    private static final String USER_URL = "https://xn--gmqz83awjh.net/user";
//
//    // 初始化浏览器（以 Chrome 为例）
//    private static WebDriver initBrowser() {
//        WebDriverManager.chromedriver().setup();
//        ChromeOptions options = new ChromeOptions();
////        options.addArguments("--start-maximized");
//        // 启用无头模式
//        options.addArguments("--headless");
//        // 在某些 Linux 环境下需要
//        options.addArguments("--no-sandbox");
//        // 解决共享内存问题
//        options.addArguments("--disable-dev-shm-usage");
//        options.addArguments("--window-size=1920,1080");
//        WebDriver browser = new ChromeDriver(options);
//        return browser;
//    }
//
//    @Scheduled(fixedDelay = 11 * 60 * 60 * 1000)
//    public void loginAndCheckIn() {
//        log.info("------------------少数人每日签到程序启动----------------");
//        WebDriver browser = initBrowser();
//        try {
//            // 打开登录页面
//            browser.get(LOGIN_URL);
//
//            // 等待页面加载
//            WebDriverWait wait = new WebDriverWait(browser, Duration.ofSeconds(10).getSeconds());
//
//            // 找到邮箱输入框并输入邮箱
//            WebElement emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));
//            Thread.sleep(1000);
//            emailField.sendKeys(username);
//
//            // 找到密码输入框并输入密码
//            WebElement passwordField = browser.findElement(By.id("password"));
//            Thread.sleep(1000);
//            passwordField.sendKeys(password);
//
//            Thread.sleep(1000);
//            // 点击登录按钮
//            WebElement loginButton = browser.findElement(By.id("login_submit"));
//            loginButton.click();
//            log.info("少数人登录成功");
//            // 等待登录完成
//            wait.until(ExpectedConditions.urlContains("/user"));
//
//            // 等待签到成功提示
//            WebElement alreadySignIn = safeWaitForElement(browser, By.xpath("//a[contains(text(), '已签到')]"), 5);
//            if (alreadySignIn != null) {
//                log.info("今日已签到！提示信息: " + alreadySignIn.getText());
//                return;
//            }
//            // 定位签到按钮 // 按钮的 ID 为 "checkin"
//            WebElement signInButton = safeWaitForElement(browser, By.id("checkin"), 1);
//            if (signInButton == null) {
//                throw new BusinessException("签到按钮未找到！");
//            }
//            log.info("少数人未签到，尝试签到...");
//            // 点击签到按钮
//            signInButton.click();
//
//            alreadySignIn = safeWaitForElement(browser, By.xpath("//a[contains(text(), '已签到')]"), 5);
//            // 等待签到成功提示
//            if (alreadySignIn == null) {
//                throw new BusinessException("少数人签到成功按钮未找到,签到失败！");
//            }
//            log.info("少数人签到成功！提示信息: " + alreadySignIn.getText());
//        } catch (Exception e) {
//            log.error("少数人签到过程中出现问题: [{}]", Throwables.getStackTraceAsString(e));
//        } finally {
//            // 关闭浏览器
//            try {
//                Thread.sleep(3000);
//                browser.quit();
//            } catch (Exception e) {
//                log.error("关闭浏览器失败 {}", Throwables.getStackTraceAsString(e));
//            }
//
//        }
//        log.info("--------------------少数人每日签到程序结束----------------------");
//    }
//
//}
