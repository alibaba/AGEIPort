# API参考文档

## Processor

### ImportProcessor

#### 泛型参数QUERY

QueryObject，映射到查询参数的类，比如查询参数{"name":"tom"}，则QueryObject应包含name字段，前端传递{"name":"tom"}参数时，会构造出一个 name=tom的QueryObject。


#### 泛型参数VIEW

ViewObject，映射到文件中某行数据的类，比如Excel中的每行数据会被构造为这里的一个对象。

#### 泛型参数DATA

DataObject，映射到实际写入数据源或接口的类型，一般是ViewObject执行转换后得到的类型，从文件中读取的数据，很多情况下不能直接插入数据源，需要补齐一些数据，或者添加一些额外的字段，这时候就需要有个补齐或转换操作。如果没有这种需求，V和D可以定义为相同的类型。

#### 0.resolver(可选实现)

* 接口功能：返回Processor的解析器名称，解析器用来注册Processor实现，注册之后的Processor实现才可以被使用

* 是否必须实现：否

* 入参：无

* 返回值：String

#### 1.getConcreteAdapter(可选实现)

* 接口功能：返回Processor的适配器，用于修改任务执行流程

* 是否必须实现：否

* 入参：无

* 返回值：Adapter

#### 2.resetQuery(可选实现)

* 接口功能：修改当前Query对象

* 是否必须实现：否

* 入参：
  * BizUser bizUser，当前创建任务的用户
  * QUERY query，当前任务的查询参数

* 返回值：QUERY

#### 3.taskRuntimeConfig(可选实现)

* 接口功能：修改当前任务运行时配置

* 是否必须实现：否

* 入参：
  * BizUser bizUser，当前创建任务的用户
  * QUERY query，当前任务的查询参数

* 返回值：BizImportTaskRuntimeConfig

#### 4.getHeaders(可选实现)

* 接口功能：获取导入任务的表头，与ViewField效果相同，此方法优先级更高

* 是否必须实现：否

* 入参：
  * BizUser bizUser，当前创建任务的用户
  * QUERY query，当前任务的查询参数

* 返回值：BizColumnHeaders

#### 5.getDynamicHeaders(可选实现)

* 接口功能：获取导入任务的动态表头

* 是否必须实现：否

* 入参：
  * BizUser bizUser，当前创建任务的用户
  * QUERY query，当前任务的查询参数

* 返回值：BizDynamicColumnHeaders



#### 6.getBizDataGroup(可选实现)

* 接口功能：将子任务需处理的原始数据转为VIEW对象分组

* 是否必须实现：否

* 入参：
  * BizUser bizUser，表示当前创建任务的用户
  * QUERY query，当前任务的查询参数
  * DataGroup group，子任务需处理的原始数据

* 返回值：BizDataGroup\<VIEW\>



#### 7.flat(可选实现)

* 接口功能：将VIEW对象分组展平为VIEW对象列表

* 是否必须实现：否

* 入参：
  * BizUser bizUser，表示当前创建任务的用户
  * QUERY query，当前任务的查询参数
  * BizDataGroup\<VIEW\> views，VIEW对象分组

* 返回值：List\<VIEW\>



#### 8.convertAndCheck(必须实现)

* 接口功能：校验并转换VIEW对象列表数据，返回DATA列表和VIEW列表

* 是否必须实现：是

* 入参：
  * BizUser bizUser，表示当前创建任务的用户
  * QUERY query，当前任务的查询参数
  * List\<VIEW\> views，VIEW对象列表

* 返回值：BizImportResult<VIEW, DATA>，VIEW数据会被输出到文件允许用户下载，DATA数据会交由write方法执行写入逻辑



#### 9.write(必须实现)

* 接口功能：执行用户写入逻辑

* 是否必须实现：是

* 入参：
  * BizUser bizUser，表示当前创建任务的用户
  * QUERY query，当前任务的查询参数
  * List\<DATA\> data，DATA对象列表

* 返回值：BizImportResult<VIEW, DATA>，VIEW数据会被输出到文件允许用户下载



#### 10.group(可选实现)

* 接口功能：将write和covertAndCheck返回的数据分组，输出到不同文件或Excel的Sheet

* 是否必须实现：否

* 入参：
  * BizUser bizUser，表示当前创建任务的用户
  * QUERY query，当前任务的查询参数
  * List\<VIEW\> views，VIEW对象列表

* 返回值：BizDataGroup\<VIEW\>



#### 11.getDataGroup(可选实现)

* 接口功能：将BizDataGroup\<VIEW\>转换为子任务要数据的原始数据格式，将VIEW对象转为List、Map等原始数据类型

* 是否必须实现：否

* 入参：
  * BizUser bizUser，表示当前创建任务的用户
  * QUERY query，当前任务的查询参数
  * BizDataGroup\<VIEW\> bizDataGroup VIEW对象分组

* 返回值：DataGroup



### ExportProcessor

#### 泛型参数QUERY

QueryObject，映射到查询参数的类，比如查询参数{"name":"tom"}，则QueryObject应包含name字段，前端传递{"name":"tom"}参数时，会构造出一个 name=tom的QueryObject。


#### 泛型参数VIEW

ViewObject，映射到文件中某行数据的类，比如Excel中的每行数据会被构造为这里的一个对象。

#### 泛型参数DATA

DataObject，映射到实际写入数据源或接口的类型，一般是ViewObject执行转换后得到的类型，从文件中读取的数据，很多情况下不能直接插入数据源，需要补齐一些数据，或者添加一些额外的字段，这时候就需要有个补齐或转换操作。如果没有这种需求，V和D可以定义为相同的类型。

#### 0.resolver(可选实现)

* 接口功能：返回Processor的解析器名称，解析器用来注册Processor实现，注册之后的Processor实现才可以被使用

* 是否必须实现：否

* 入参：无

* 返回值：String

#### 1.getConcreteAdapter(可选实现)

* 接口功能：返回Processor的适配器，用于修改任务执行流程

* 是否必须实现：否

* 入参：无

* 返回值：Adapter

#### 2.resetQuery(可选实现)

* 接口功能：修改当前Query对象

* 是否必须实现：否

* 入参：
  * BizUser bizUser，当前创建任务的用户
  * QUERY query，当前任务的查询参数

* 返回值：QUERY

#### 3.taskRuntimeConfig(可选实现)

* 接口功能：修改当前任务运行时配置

* 是否必须实现：否

* 入参：
  * BizUser bizUser，当前创建任务的用户
  * QUERY query，当前任务的查询参数

* 返回值：BizExportTaskRuntimeConfig

#### 4.totalCount(必选实现)

* 接口功能：获取导出任务数据总量

* 是否必须实现：是

* 入参：
  * BizUser bizUser，当前创建任务的用户
  * QUERY query，当前任务的查询参数

* 返回值：Integer

#### 5.getHeaders(可选实现)

* 接口功能：获取导入任务的表头，与ViewField效果相同，此方法优先级更高

* 是否必须实现：否

* 入参：
  * BizUser bizUser，当前创建任务的用户
  * QUERY query，当前任务的查询参数

* 返回值：BizColumnHeaders

#### 6.getDynamicHeaders(可选实现)

* 接口功能：获取导入任务的动态表头

* 是否必须实现：否

* 入参：
  * BizUser bizUser，当前创建任务的用户
  * QUERY query，当前任务的查询参数

* 返回值：BizDynamicColumnHeaders

#### 7.queryData(必选实现)

* 接口功能：分页获取要导出的数据

* 是否必须实现：是

* 入参：
  * BizUser bizUser，当前创建任务的用户
  * QUERY query，当前任务的查询参数
  * BizExportPage bizExportPage，当前子任务的分页信息

* 返回值：List\<DATA\>当前子任务分页数据

#### 8.convert(必选实现)

* 接口功能：转换子任务分页数据，List\<DATA\>->List\<VIEW\>

* 是否必须实现：是

* 入参：
  * BizUser bizUser，当前创建任务的用户
  * QUERY query，当前任务的查询参数

* 返回值：BizDynamicColumnHeaders

#### 10.group(可选实现)

* 接口功能：将write和covertAndCheck返回的数据分组，输出到不同文件或Excel的Sheet

* 是否必须实现：否

* 入参：
  * BizUser bizUser，表示当前创建任务的用户
  * QUERY query，当前任务的查询参数
  * List\<VIEW\> views，VIEW对象列表

* 返回值：BizDataGroup\<VIEW\>

#### 11.getDataGroup(可选实现)

* 接口功能：将BizDataGroup\<VIEW\>转换为子任务要数据的原始数据格式，将VIEW对象转为List、Map等原始数据类型

* 是否必须实现：否

* 入参：
  * BizUser bizUser，表示当前创建任务的用户
  * QUERY query，当前任务的查询参数
  * BizDataGroup\<VIEW\> bizDataGroup VIEW对象分组

* 返回值：DataGroup

## @Annotation

### @ViewField

注解在VIEW类型的字段上

| 字段            | 含义                             | 可选值 |
| --------------- |--------------------------------| ------ |
| headerName      | 表头名称，必填                        |        |
| type            | 表头类型，非必填                       |        |
| index           | 表头下标，用于排序，非必填                  |        |
| isDynamicColumn | 表头是否动态列，若为true则当前字段需为Map类型，非必填 |        |
| groupIndex      | 表头分组下标，用于处理多sheet/多文件，非必填      |        |
| groupName       | 表头分组名称，用于处理多sheet/多文件，非必填      |        |
| isErrorHeader   | 表头是否为错误列，错误列不会出现在导入文件的模板中，非必填  |        |
| isRequired      | 表头对应的值是否必须，非必填                 |        |
| columnWidth      | 宽度，对Excel生效,非必填                |        |
| values      | 可选值，在Excel中会转为下拉框，非必填          |        |

### @ImportSpecification

注解在ImportProcessor的实现类上

| 字段           | 含义                         | 可选值                                     |
| -------------- | ---------------------------- | ------------------------------------------ |
| code           | 任务编码，必填               |                                            |
| name           | 任务名称，必填               |                                            |
| desc           | 任务描述，非必填             |                                            |
| type           | 任务类型，非必填             | 导入：IMPORT，导出：EXPORT，用户可扩展实现 |
| executeType    | 任务执行类型，非必填         | 多机：CLUSTER，单机：STANDALONE            |
| timeoutMs      | 任务超时时间，非必填         |                                            |
| totalThreshold | 任务处理数据上限，非必填     |                                            |
| fileType       | 任务输出输出文件类型，非必填 | xlsx、csv，用户可扩展实现                  |
| pageSize       | 子任务分页大小，非必填       |                                            |
| sliceStrategy  | 任务分片策略，非必填         | 平均分片策略：AvgImportSliceStrategy       |

### @ExportSpecification

注解在ExportProcessor的实现类上

| 字段           | 含义                         | 可选值                                     |
| -------------- | ---------------------------- | ------------------------------------------ |
| code           | 任务编码，必填               |                                            |
| name           | 任务名称，必填               |                                            |
| desc           | 任务描述，非必填             |                                            |
| type           | 任务类型，非必填             | 导入：IMPORT，导出：EXPORT，用户可扩展实现 |
| executeType    | 任务执行类型，非必填         | 多机：CLUSTER，单机：STANDALONE            |
| timeoutMs      | 任务超时时间，非必填         |                                            |
| totalThreshold | 任务处理数据上限，非必填     |                                            |
| fileType       | 任务输出输出文件类型，非必填 | xlsx、csv，用户可扩展实现                  |
| pageSize       | 子任务分页大小，非必填       |                                            |
| sliceStrategy  | 任务分片策略，非必填         | 平均分片策略：AvgExportSliceStrategy       |

