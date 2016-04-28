#iutiaosdk-android
-------------
still in heavy development

### 下载安装
1. 将克隆下的代码以模块方式导入到 android studio
 项目中
> File -> Import Module -> 克隆的 iutiaosdk 目录

2. 项目依赖项里请添加 iutiaosdk 模块依赖
> File -> Project structure ... -> 选中左侧 app 模块 -> 点击右侧 Dependencies -> + Module Dependency -> 选中克隆下来的 iutiaosdk 模块

###使用方法
在Application中调用 IUTiaoSdk.sdkInitialize(getApplicationContext())
 进行初始化：
```
// 开启沙盒模式， 用于调试，正式部署时去掉该模式
IUTiaoSdk.setSandBoxMode();
IUTiaoSdk.sdkInitialize(getApplicationContext());
```
### 注意事项
* SDK初始化暂时必须在Application中进行，否则应用有几率崩溃。
* 不要忘记在AndroidManifest.xml的Application节点指定使用自己的Application。
```
<application
...    
  android:name="YourApplicationName"
...
> 
  ```
