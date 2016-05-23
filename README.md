# AndroidLogSaver

## 引用

    compile 'cc.liyongzhi.androidlogsaver:androidlogsaver:1.0.4'

## 用法

  LogShower.custom(String 你的TAG， String 类名， String 函数名， String 信息， String.. 多个调试种类)
  
  
  运行时可以在Android monitor里输入当前的调试种类、类名、函数名，则只显示当前调试种类的log，这样可以避免同一个位置打多个log
