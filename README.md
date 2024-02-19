### DataX插件

SM4国密加密插件Transformer

基于DataX 3.0 Release datax_v202309 https://github.com/alibaba/DataX/releases/tag/datax_v202309

#### 打包
```shell
mvn clean package -DskipTests assmebly:assembly
```

#### 使用
复制打包好的文件夹encrypttransformer
![plugin-package.png](plugin-package.png)

在datax文件夹下创建文件夹local_storage/transformer
![datax-dir.png](datax-dir.png)

粘贴文件夹到datax文件夹下
![plugin-install.png](plugin-install.png)
