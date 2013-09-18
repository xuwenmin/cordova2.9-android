cordova2.9-android
==================

cordova 的源码项目，以此基础上增加业务需求


1.增加了访问assets里的网站资源文件
 
    用法例子: window.resolveLocalFileSystemURI("qm_a.txt",onResolveSuccess, fail);
    
    第一个参数里带qm的就会走assets里获取资源文件，否则默认是从sd卡0里获取的.
