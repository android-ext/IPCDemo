自定义的Parcelable对象和AIDL对象必须要显示import进来

用到了自定义的Parcelable对象必须新建一个和它同名的AIDL文件


1. 首先，在实现Parcelable接口的模型类所在的包上右键  New -> AIDL

2. AS会自动生成aidl文件夹以及对应的包名。接着声明所需要类。
   提示interface name must be unique时，可以随意命名，新建完成后再重命名。

3. Build -> Make Project后可生成Java文件。