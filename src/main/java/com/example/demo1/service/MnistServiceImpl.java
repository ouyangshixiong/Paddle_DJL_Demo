package com.example.demo1.service;

import ai.djl.inference.Predictor;
import ai.djl.modality.cv.Image;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.index.NDIndex;
import ai.djl.ndarray.types.Shape;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelZoo;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.translate.TranslateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.file.Paths;

/**
 * @author alexouyang
 * @Date 2021/5/19
 */
@Component
public class MnistServiceImpl {

    private static Logger log = LoggerFactory.getLogger("MnistServiceImpl");

    private static Predictor<NDList, NDList> predictor;

    public MnistServiceImpl(){
        try {
            // load model
            Criteria<NDList, NDList> criteria = Criteria.builder()
                    .optEngine("PaddlePaddle")
                    .setTypes(NDList.class, NDList.class)
                    .optModelPath(Paths.get("/Users/ouyang/Downloads/model.zip"))
                    .optModelName("inference")
                    .build();

            ZooModel<NDList, NDList> model = ModelZoo.loadModel(criteria);
            // run inference
            predictor = model.newPredictor();

        }catch(Exception e){
            log.error("constructor:", e);
        }
    }

    public void predict(Image image) throws TranslateException {
        NDList list = processImageInput(image);
        NDArray result = predictor.predict(list).get(0).argSort();
        log.info("预测结果是:" + result.get(new NDIndex(0,9)).getLong());
    }


    private NDList processImageInput(Image input) {
        NDManager manager = NDManager.newBaseManager();
        NDArray array = input.toNDArray( manager, Image.Flag.GRAYSCALE );
        array = array.transpose(2, 0, 1).flip(0);
        NDArray allOnes = manager.ones(new Shape(1, 28, 28));
        array = allOnes.sub(array.div(255));
        array = array.expandDims(0);
        return new NDList(array);
    }

}
