package com.goodluck.checkin;

import com.goodluck.common.exception.BusinessException;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
public abstract class AbstractCheckinService {

    private static final String SCREENSHOT_BASE_PATH = "/opt/images/";


    // 初始化浏览器
    protected WebDriver initBrowser() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--start-maximized");
        // 启用无头模式
        options.addArguments("--headless");
        // 在某些 Linux 环境下需要
        options.addArguments("--no-sandbox");
        // 设置为简体中文
        options.addArguments("--lang=zh-CN");
        // 解决共享内存问题
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");
         return new ChromeDriver(options);
    }

    // 抽象方法：登录实现由子类定义
    protected abstract void login(WebDriver browser);

    // 抽象方法：签到实现由子类定义
    protected abstract boolean performCheckIn(WebDriver browser);

    // 公共的签到执行逻辑
    public boolean doCheckIn() {
        log.info("--------- {} 签到程序启动 ---------", this.getClass().getSimpleName());
        WebDriver browser = initBrowser();
        boolean success = false;
        try {
            // 登录
            login(browser);
            log.info("{} 登录成功！", this.getClass().getSimpleName());
            // 执行签到
            success = performCheckIn(browser);
        } catch (BusinessException e) {
            takeScreenshot(browser, "error");
            log.error("业务异常: {}", e.getMessage());
        } catch (Exception e) {
            takeScreenshot(browser, "error");
            log.error("签到过程中出现问题: [{}]", e.getMessage(), e);
        } finally {
            try {
                Thread.sleep(3000);
                browser.quit();
            } catch (Exception e) {
                log.error("关闭浏览器失败: [{}]", e.getMessage(), e);
            }
        }
        log.info("--------- {} 签到程序结束 ---------", this.getClass().getSimpleName());
        return success;
    }


    protected void takeScreenshot(WebDriver browser, String elementDescription) {
        try {
            // 获取当前时间，生成文件名
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            String timestamp = now.format(formatter);

            // 构造文件路径
            String filename = SCREENSHOT_BASE_PATH + timestamp + "-" + elementDescription.replaceAll("[^a-zA-Z0-9]", "_") + ".png";
            File screenshot = ((TakesScreenshot) browser).getScreenshotAs(OutputType.FILE);

            // 确保目录存在
            File outputFile = new File(filename);
            if (!outputFile.getParentFile().exists()) {
                outputFile.getParentFile().mkdirs();
            }

            // 保存截图
            Files.copy(screenshot.toPath(), outputFile.toPath());
            log.info("截图已保存: {}", filename);
        } catch (IOException e) {
            log.error("保存截图时发生错误: {}", e.getMessage(), e);
        }
    }
}
