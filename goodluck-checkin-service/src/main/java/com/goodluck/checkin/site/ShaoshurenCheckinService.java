package com.goodluck.checkin.site;

import com.goodluck.checkin.AbstractCheckinService;
import com.goodluck.checkin.manager.CheckinRetryScheduler;
import com.goodluck.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static com.goodluck.checkin.utils.SeleniumUtils.safeWaitForElement;

@Slf4j
@Service
public class ShaoshurenCheckinService extends AbstractCheckinService {

    @Value("${bugutv.username}")
    private String username;

    @Value("${bugutv.password}")
    private String password;

    private static final String LOGIN_URL = "https://www.bugutv.org";
    private static final String USER_URL = "https://www.bugutv.org/user";

    @Scheduled(fixedDelay = 11 * 60 * 60 * 1000)
    public void loginAndCheckIn() {
        if (!doCheckIn()) {
            CheckinRetryScheduler.scheduleTask(this::doCheckIn, 1, TimeUnit.HOURS);
        }
    }

    @Override
    protected void login(WebDriver browser) {
        browser.get(LOGIN_URL);
        // 等待页面加载
        WebDriverWait wait = new WebDriverWait(browser, Duration.ofSeconds(10).getSeconds());

        // 找到邮箱输入框并输入邮箱
        WebElement emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));
        emailField.sendKeys(username);

        // 找到密码输入框并输入密码
        WebElement passwordField = browser.findElement(By.id("password"));
        passwordField.sendKeys(password);
        // 点击登录按钮
        WebElement loginButton = browser.findElement(By.id("login_submit"));
        loginButton.click();
        // 等待登录完成
        wait.until(ExpectedConditions.urlContains("/user"));
    }

    @Override
    protected boolean performCheckIn(WebDriver browser) {
        // 等待签到成功提示
        WebElement alreadySignIn = safeWaitForElement(browser, By.xpath("//a[contains(text(), '已签到')]"), 5);
        if (alreadySignIn != null) {
            log.info("今日已签到！提示信息: " + alreadySignIn.getText());
            return true;
        }
        // 定位签到按钮 // 按钮的 ID 为 "checkin"
        WebElement signInButton = safeWaitForElement(browser, By.id("checkin"), 1);
        if (signInButton == null) {
            throw new BusinessException("签到按钮未找到！");
        }
        log.info("少数人未签到，尝试签到...");
        // 点击签到按钮
        signInButton.click();

        alreadySignIn = safeWaitForElement(browser, By.xpath("//a[contains(text(), '已签到')]"), 5);
        // 等待签到成功提示
        if (alreadySignIn == null) {
            throw new BusinessException("少数人签到成功按钮未找到,签到失败！");
        }
        log.info("少数人签到成功！提示信息: " + alreadySignIn.getText());
        return true;
    }



}
