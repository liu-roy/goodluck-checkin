package com.goodluck.checkin.controller;


import com.goodluck.checkin.site.BuguTv;
import com.goodluck.checkin.site.Shaoshuren;
import com.goodluck.common.resp.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author liuleyi
 */
@RestController
@RequestMapping("checkin")
@Api(tags = "签到")
@RequiredArgsConstructor
@Slf4j
public class CheckinController {


    @PostMapping("/buguTV")
    @ApiOperation("buguTV")
    public R<Boolean> buguTV() {
        BuguTv.loginAndCheckin();
        return R.success(Boolean.TRUE);
    }

    @PostMapping("/shaoshuren")
    @ApiOperation("shaoshuren")
    public R<Boolean> shaoshuren() {
        Shaoshuren.loginAndCheckin();
        return R.success(Boolean.TRUE);
    }

}