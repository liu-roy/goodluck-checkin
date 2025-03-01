package com.goodluck.checkin.site;

import com.goodluck.checkin.manager.CheckinRetryScheduler;
import com.goodluck.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static com.goodluck.checkin.utils.SeleniumUtils.safeWaitForElement;

@Slf4j
@Service
public class BuguTvCheckinService extends AbstractCheckinService {

    @Value("${bugutv.username}")
    private String username;

    @Value("${bugutv.password}")
    private String password;

    @Value("${bugutv.url}")
    private String LOGIN_URL = "https://www.bugutv.vip";

    private final String USER_URI = "/user";


    @Scheduled(fixedDelay = 11 * 60 * 60 * 1000+30000)
    public void loginAndCheckIn() {
        if (!doCheckIn()) {
            CheckinRetryScheduler scheduler = CheckinRetryScheduler.getInstance();
            scheduler.start();
            scheduler.scheduleTask(this::loginAndCheckIn, 1, TimeUnit.HOURS);

        }
    }

    @Override
    protected void login(WebDriver browser) {
        browser.get(LOGIN_URL);
        // 等待登录按钮加载完成
        WebDriverWait wait = new WebDriverWait(browser, 10);
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

        usernameField.sendKeys(username);
        passwordField.sendKeys(password);
        loginButton.click();

        // 等待登录完成
        waitForAjaxLoad(browser);
    }

    @Override
    protected boolean performCheckIn(WebDriver browser) {
        browser.get(LOGIN_URL+USER_URI);

        // 检查是否已经签到
        WebElement alreadySignIn = safeWaitForElement(browser, By.xpath("//button[contains(text(), '今日已签到')]"), 10);
        if (alreadySignIn != null) {
            log.info("布谷TV今日已签到，提示信息: {}", alreadySignIn.getText());
            return true;
        }

        // 尝试签到
        log.info("布谷TV未签到，尝试签到...");
        WebElement signInButton = new WebDriverWait(browser, 10).until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[contains(@class, 'go-user-qiandao')]")));
        if (signInButton == null) {
            throw new BusinessException("签到按钮未找到！");
        }
        signInButton.click();
        alreadySignIn = safeWaitForElement(browser, By.xpath("//button[contains(text(), '今日已签到')]"), 5);
        if (alreadySignIn == null) {
            throw new BusinessException("布谷tv签到失败！未找到签到成功标记！");
        }
        log.info("布谷tv签到成功");
        return true;
    }


    private void waitForAjaxLoad(WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, 15);
        wait.until(webDriver -> ((JavascriptExecutor) webDriver)
                .executeScript("return jQuery.active == 0")
                .equals(true));
    }



}
