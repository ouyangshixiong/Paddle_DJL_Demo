package com.example.demo1.controller;

import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import com.example.demo1.service.MnistServiceImpl;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author alexouyang
 * @Date 2021/5/19
 */
@RestController
public class MnistController {

    private static Logger log = LoggerFactory.getLogger("MinistController");

    @Autowired
    MnistServiceImpl mnistService;

    @ApiOperation(value="hello value",notes = "hello notes")
    @GetMapping("hello")
    public String hello(){
        return "hello!";
    }
    @GetMapping("predict")
    public String predict(@RequestParam String url){
        try {
            Image image = ImageFactory.getInstance().fromUrl(url);
            image.getWrappedImage();
            mnistService.predict(image);
        }catch(Exception e){
            log.error("predict throws Exception: ", e);
        }
        return "";
    }

}
