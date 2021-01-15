# treemeasurer

#### 介绍
treemeasurer立木测量仪是一款立木测量app，能够实现立木胸径、高度、距离测量，开发模式为MVP。
后端使用FastAPI框架开发，利用detection2深度学习框架训练Mask R-CNN分割模型，能够实现对图像中立木树干的精准分割。
客户端将采集的数据发送给后端，后端处理后将得到的立木识别图像分割结果与立木胸径大小返回给客户端。

#### App主界面
<img src="https://github.com/wuzheng228/treemeasurer/blob/master/images/%E4%B8%BB%E7%95%8C%E9%9D%A2_0.jpg" width = "200" alt="主界面0" style="float:left" />

#### 使用说明
##### 水平仪
测量时需要在平地测量，水平仪用来测量当前地面的平整程度。
<img src="https://github.com/wuzheng228/treemeasurer/blob/master/images/%E4%B8%BB%E7%95%8C%E9%9D%A2_1.jpg" width = "200" alt="主界面1" align=center />


##### 相机标定
相机标定功能用于标定自定义相机的内部参数，第一次测量胸径时自动进入相机标定界面，按步骤提示打印标定板->设置标定板参数->提示如何拍摄标定板图片
<div >
  <img src="https://github.com/wuzheng228/treemeasurer/blob/master/images/%E7%9B%B8%E6%9C%BA%E6%A0%87%E5%AE%9A_0.jpg" width = "200" alt="主界面1" align=center />
  <img src="https://github.com/wuzheng228/treemeasurer/blob/master/images/%E7%9B%B8%E6%9C%BA%E6%A0%87%E5%AE%9A_1.jpg" width = "200" alt="主界面1" align=center />
  <img src="https://github.com/wuzheng228/treemeasurer/blob/master/images/%E7%9B%B8%E6%9C%BA%E6%A0%87%E5%AE%9A_2.jpg" width = "200" alt="主界面1" align=center />
  <img src="https://github.com/wuzheng228/treemeasurer/blob/master/images/%E7%9B%B8%E6%9C%BA%E6%A0%87%E5%AE%9A_3.jpg" width = "200" alt="主界面1" align=center />
  <img src="https://github.com/wuzheng228/treemeasurer/blob/master/images/%E7%9B%B8%E6%9C%BA%E6%A0%87%E5%AE%9A_4.jpg" width = "200" alt="主界面1" align=center />
  <img src="https://github.com/wuzheng228/treemeasurer/blob/master/images/%E7%9B%B8%E6%9C%BA%E6%A0%87%E5%AE%9A_5.jpg" width = "200" alt="主界面1" align=center />
  <img src="https://github.com/wuzheng228/treemeasurer/blob/master/images/%E7%9B%B8%E6%9C%BA%E6%A0%87%E5%AE%9A_7.jpg" width = "200" alt="主界面1" align=center />
  <img src="https://github.com/wuzheng228/treemeasurer/blob/master/images/%E7%9B%B8%E6%9C%BA%E6%A0%87%E5%AE%9A_6.jpg" width = "200" alt="主界面1" align=center />
</div>
<div/>
##### 立木胸径测量

点击主界面的立木胸径测量模块，第一次使用按照提示设置参数，在3米左右拍摄立木图像，然后测量深度后，等待服务器将处理结果返回
<div style="display:inline-block">
  <img src="https://github.com/wuzheng228/treemeasurer/blob/master/images/%E8%83%B8%E5%BE%84%E6%B5%8B%E9%87%8F_0.jpg" width = "200" alt="主界面1" align=center />
  <img src="https://github.com/wuzheng228/treemeasurer/blob/master/images/%E8%83%B8%E5%BE%84%E6%B5%8B%E9%87%8F_1.jpg" width = "200" alt="主界面2" align=center />
  <img src="https://github.com/wuzheng228/treemeasurer/blob/master/images/%E8%83%B8%E5%BE%84%E6%B5%8B%E9%87%8F_2.jpg" width = "200" alt="主界面3" align=center />
  <img src="https://github.com/wuzheng228/treemeasurer/blob/master/images/%E8%83%B8%E5%BE%84%E6%B5%8B%E9%87%8F_3.jpg" width = "200" alt="主界面4" align=center />
  <img src="https://github.com/wuzheng228/treemeasurer/blob/master/images/%E8%83%B8%E5%BE%84%E6%B5%8B%E9%87%8F_4.jpg" width = "200" alt="主界面5" align=center />
  <img src="https://github.com/wuzheng228/treemeasurer/blob/master/images/%E8%83%B8%E5%BE%84%E6%B5%8B%E9%87%8F_5.jpg" width = "200" alt="主界面6" align=center />
  <img src="https://github.com/wuzheng228/treemeasurer/blob/master/images/%E8%83%B8%E5%BE%84%E6%B5%8B%E9%87%8F_7.jpg" width = "200" alt="主界面8" align=center />
</div>

##### 距离测量

点击主界面的距离测量模块，在平地靶心瞄准目标底部，点击拍摄按钮即可实现距离测量
<div style="display:inline-block">
  <img src="https://github.com/wuzheng228/treemeasurer/blob/master/images/%E8%B7%9D%E7%A6%BB%E6%B5%8B%E9%87%8F_0.jpg" width = "200" alt="主界面1" align=center />
  <img src="https://github.com/wuzheng228/treemeasurer/blob/master/images/%E8%B7%9D%E7%A6%BB%E6%B5%8B%E9%87%8F_1.jpg" width = "200" alt="主界面2" align=center />
</div>

##### 高度测量

点击主界面的高度测量模块，第一次使用按照提示设置参数，拍摄目标底部后，再拍摄目标顶部一次即可得到目标高度值
<div style="display:inline-block">
  <img src="https://github.com/wuzheng228/treemeasurer/blob/master/images/%E9%AB%98%E5%BA%A6%E6%B5%8B%E9%87%8F_0.jpg" width = "200" alt="主界面1" align=center />
  <img src="https://github.com/wuzheng228/treemeasurer/blob/master/images/%E9%AB%98%E5%BA%A6%E6%B5%8B%E9%87%8F_1.jpg" width = "200" alt="主界面2" align=center />
  <img src="https://github.com/wuzheng228/treemeasurer/blob/master/images/%E9%AB%98%E5%BA%A6%E6%B5%8B%E9%87%8F_2.jpg" width = "200" alt="主界面2" align=center />
  <img src="https://github.com/wuzheng228/treemeasurer/blob/master/images/%E9%AB%98%E5%BA%A6%E6%B5%8B%E9%87%8F_3.jpg" width = "200" alt="主界面2" align=center />
  <img src="https://github.com/wuzheng228/treemeasurer/blob/master/images/%E9%AB%98%E5%BA%A6%E6%B5%8B%E9%87%8F_4.jpg" width = "200" alt="主界面2" align=center />
</div>
