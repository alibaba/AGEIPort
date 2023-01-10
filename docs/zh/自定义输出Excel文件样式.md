## 自定义输出Excel文件样式

框架使用EasyExcel操作Excel，EasyExcel是POI的封装，通过暴露EasyExcel和POI的接口实现对Excel样式的控制

### 基本过程

#### 定义ExcelProcessor

ExportProcessor参考代码：```com.alibaba.ageiport.test.processor.core.exporter.ExcelStyleExportProcessor```

#### 定义WriteHandler

WriteHandler是EasyExcel定义的Excel拦截器，在WriteHandler的实现中开发者可使用POI的API控制每个Sheet、Row和Cell的样式

参考文档实现：https://easyexcel.opensource.alibaba.com/docs/current/quickstart/write#%E8%87%AA%E5%AE%9A%E4%B9%89%E6%8B%A6%E6%88%AA%E5%99%A8%E4%B8%8A%E9%9D%A2%E5%87%A0%E7%82%B9%E9%83%BD%E4%B8%8D%E7%AC%A6%E5%90%88%E4%BD%86%E6%98%AF%E8%A6%81%E5%AF%B9%E5%8D%95%E5%85%83%E6%A0%BC%E8%BF%9B%E8%A1%8C%E6%93%8D%E4%BD%9C%E7%9A%84%E5%8F%82%E7%85%A7%E8%BF%99%E4%B8%AA

WriteHandler参考代码：```com.alibaba.ageiport.test.processor.core.exporter.ExcelStyleExportProcessor```

#### 定义并注册WriteHandlerProvider

WriteHandlerProvider是WriteHandler的提供者，用户根据WriteHandlerProvider接口的入参，构建并返回所需的WriteHandler

WriteHandlerProvider参考代码：```com.alibaba.ageiport.test.processor.core.exporter.ExcelStyleExportProcessor```

### 固定样式

#### 表头合并

当两个表头单元格相邻且值相同时会自动合并单元格

#### 下拉框

* 可通过注解设置Excel中的下拉框（一般用于表头和模型固定的场景）：```com.alibaba.ageiport.processor.core.annotation.ViewField.values```
* 可通过接口Processor返回（一般用于表头和模型不固定的场景）：getDynamicHeaders或getDynamicHeaders接口返回 ```com.alibaba.ageiport.processor.core.model.api.impl.BizColumnHeaderImpl.values```

#### 单元格宽度

* 可通过注解设置Excel的宽度：```com.alibaba.ageiport.processor.core.annotation.ViewField#columnWidth```
* 可通过接口Processor返回（一般用于表头和模型不固定的场景）：getDynamicHeaders或getDynamicHeaders接口返回 ```com.alibaba.ageiport.processor.core.model.api.impl.BizColumnHeaderImpl.columnWidth```