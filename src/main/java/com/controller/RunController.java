package com.controller;

import com.AppMainAll;
import com.job.SapJobAll;
import com.job.SapJobAllWeb;
import com.merck.utils.DateUtils;
import com.model.Result;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

@Controller
public class RunController {
    @Resource
    private SapJobAllWeb sapJobAllWeb;
    private static Logger log = LoggerFactory.getLogger(AppMainAll.class);
    public static final Properties p = new Properties();

    /**
     * 跳转到简易首页
     *
     * @return
     */
    @RequestMapping("/index")
    public String index() {

        log.info("跳转到网页端");
        return "index";
    }

    /**
     * 运行程序_服务器端
     * @param request
     * @return
     */
   /* @RequestMapping("/run")
    @ResponseBody
    public Result click(HttpServletRequest request) {
        try {
            String path = request.getRealPath("/WEB-INF/classes/config.properties");
            InputStream resourceAsStream = new FileInputStream(new File(path));
            p.load(resourceAsStream);
        } catch (IOException e) {
            log.info("配置文件异常");
            e.printStackTrace();
        }
        if (SapJobAll.record == 0) {
            log.info("页面触发执行任务开始：" + DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
            long startTime = System.currentTimeMillis();
            Boolean flag = SapJobAllWeb.flag;
            try {
                sapJobAllWeb.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
            long endTime = System.currentTimeMillis();
            log.info("页面触发执行任务结束：" + DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
            if (flag) {
                long runTime = endTime - startTime;
                if (runTime <= 5000) {
                    return new Result(202, "程序运行失败，请检查文件是否齐全！");
                }
                return new Result(200, "运行成功！耗时" + (runTime/1000) + "秒");
            }
            return new Result(201, "运行失败！");
        }
        return new Result(203, "定时任务正在进行，请稍后重试！");
    }
*/
    /**
     * 运行程序_Idea工具端
     * @param request
     * @return
     */
    @RequestMapping("/run")
    @ResponseBody
    public Result click(HttpServletRequest request) {
        try {
            String path = AppMainAll.class.getClassLoader().getResource("config.properties").getPath();
            InputStream resourceAsStream = new FileInputStream(new File(path));
            p.load(resourceAsStream);
        } catch (IOException e) {
            log.info("配置文件异常");
            e.printStackTrace();
        }
        if (SapJobAll.record == 0) {
            log.info("页面触发执行任务开始：" + DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
            long startTime = System.currentTimeMillis();
            Boolean flag = SapJobAllWeb.flag;
            try {
                sapJobAllWeb.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
            long endTime = System.currentTimeMillis();
            log.info("页面触发执行任务结束：" + DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
            if (flag) {
                long runTime = endTime - startTime;
                if (runTime <= 5000) {
                    return new Result(202, "程序运行失败，请检查文件是否齐全！");
                }
                return new Result(200, "运行成功！耗时" + (runTime/1000) + "秒");
            }
            return new Result(201, "运行失败！");
        }
        return new Result(203, "定时任务正在进行，请稍后重试！");

    }
}

