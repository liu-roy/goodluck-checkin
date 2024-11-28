package com.goodluck.checkin.utils;

import com.google.common.base.Throwables;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

@Slf4j
public class SeleniumUtils {

    /**
     * 等待元素出现，不抛出异常
     * @param driver WebDriver 实例
     * @param by 查找的条件
     * @param timeout 超时时间（秒）
     * @return 找到的元素；如果未找到，返回 null
     */
    public static WebElement safeWaitForElement(WebDriver driver, By by, int timeout) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, timeout);
            return wait.until(driver1 -> {
                List<WebElement> elements = driver1.findElements(by);
                return elements.isEmpty() ? null : elements.get(0);
            });
        } catch (Exception e) {
            log.info("safeWaitForElement byInfo {}, exception：{}", by.toString(), Throwables.getStackTraceAsString(e));
            return null; // 未找到，返回 null
        }
    }
}
