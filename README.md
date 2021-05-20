# 运行方法
### 1.启动springboot项目
### 2.打开[网址](http://localhost:8080/predict?url=http://www.abilitygame.cn/wp-content/uploads/2021/05/number_7.png)
### 3.查看Intellij的Console输出

# 代码说明
### 编译MNIST模型
使用model.save('model/inference', False)导出成推理模型格式。
内含三个文件：
* inference.pdiparams
* inference.pdiparams.info
* inference.pdmodel
打包成一个zip，取名model.zip

### 加载模型
```java
            // load model
            Criteria<NDList, NDList> criteria = Criteria.builder()
                    .optEngine("PaddlePaddle")
                    .setTypes(NDList.class, NDList.class)
                    .optModelPath(Paths.get("/Users/ouyang/Downloads/model.zip"))
                    .optModelName("inference")
                    .build();
```

### 预处理图片
```java
    private NDList processImageInput(Image input) {
        NDManager manager = NDManager.newBaseManager();
        // 灰度图
        NDArray array = input.toNDArray( manager, Image.Flag.GRAYSCALE );
        // 格式从(28, 28, 1)调整为(1, 28, 28)
        array = array.transpose(2, 0, 1).flip(0);
        // 初始化一个全1的NDArray
        NDArray allOnes = manager.ones(new Shape(1, 28, 28));
        // 归一化
        array = allOnes.sub(array.div(255));
        // 格式从(1, 28, 28) 调整为(1, 1, 28, 28)
        array = array.expandDims(0);
        // 转换成NDList，因为DJL不支持setTypes使用NDArray。其实NDList= ArrayList<NDArray>
        return new NDList(array);
    }
```

### 预测
```java
    public void predict(Image image) throws TranslateException {
        NDList list = processImageInput(image);
        // 对应python的result = np.argsort(result.numpy())
        NDArray result = predictor.predict(list).get(0).argSort();
        // DJL的get方法实现看源码，跟普通Java不一样。这里输出1维向量，有10个值，取最后一个
        log.info("预测结果是:" + result.get(new NDIndex(0,9)).getLong());
    }
```
