# XSKIN 文档说明

## 一、简介
xskin是一个简单易用的组件，实现应用动态换肤功能。无侵入不需要继承任何activity和fragment，
名字随便起的。

## 二、集成
1. 添加仓库地址  
```
repositories {
        ...
        maven {url 'https://s01.oss.sonatype.org/content/repositories/snapshots/'}
    }
```

2. 在项目的 build.gradle 文件中添加依赖项：  
```
dependencies {
    implementation 'io.github.langqiang:XSkin:1.0.0-SNAPSHOT'
}
```

## 三、基础使用示例
1. 初始化  
```
class MyApp: Application() {

    override fun onCreate() {
        super.onCreate()
        ...
        SkinManager.init(this)
    }
}

```

2. 加载网络皮肤资源  
```
val url = "https://godq-1307306000.cos.ap-beijing.myqcloud.com/skinresapk-debug_2.apk"
SkinManager.loadSkin(url)

```

3. 加载本地以存在皮肤资源  
```
val localPath = "xxx/xxx/xxx.apk"
SkinManager.loadLocalExistsSkin(localPath)

```

4. 重置    
```
SkinManager.reset()

```

5. 在xml中声明支持换肤的view , 使用自定义属性xskin声明，同时添加命名空间xmlns:xskin="http://schemas.android.com/xskin"
```
<androidx.constraintlayout.widget.ConstraintLayout 
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:xskin="http://schemas.android.com/xskin"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <TextView
        xskin:enable="true"
        android:textSize="30dp"
        android:gravity="center"
        android:text="TwoActivity"
        android:textColor="@color/skin_text_secondary"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>

</androidx.constraintlayout.widget.ConstraintLayout>
```

## 四、进阶使用实例
1. 支持动态添加的控件换肤  
```
val addView = TextView(context)
addView.text = "Dynamic addition View”
rootViewGroup.addView(addView)
SkinManager.setSkinAttrsWhenAddViewByCode(
        SkinViewWrapper.Builder(addView)
           .addAttr(SkinConstants.SupportAttributeName.TEXT_COLOR,R.color.skin_text_primary)
           .build()
        )
```
2. 支持单独获取换肤资源,这种场景需要添加监听，在换肤的时候手动更新资源
```
  //在自定义view中,获取换肤颜色来绘制一个矩形 
  
  private val paint = Paint()

  private val onSkinChangedListener:() -> Unit = {
        SkinManager.getSkinResource()?.getColor(R.color.skin_text_Tertiary)?.also {
            setPaintColor(it)
            invalidate()
        }
    }
    
  init {
        paint.isAntiAlias = true
        SkinManager.getSkinResource()?.getColor(R.color.skin_text_Tertiary)?.apply {
            setPaintColor(this)
        }
    }
    
  override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        //注册监听
        SkinManager.registerSkinChangedListener(onSkinChangedListener)
    }
  override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        //反注册监听
        SkinManager.unregisterSkinChangedListener(onSkinChangedListener)

    }
    
  override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawRect(width.toFloat() / 2, 0f, width.toFloat(), height.toFloat(), paint)
        canvas?.drawRect(0f, 0f, width.toFloat() / 2, height.toFloat(), paint2)
    }
 
  private fun setPaintColor(color: Int) {
        paint.color = color
    }
```

## 五、原理
利用AssetManager反射增加路径资源，创建未安装apk中的resources，通过新创建的resources加载同名资源达到换肤效果。
实时性：记录所有创建完成并且需要支持换肤的view，在加载皮肤之后遍历重新设置换肤支持的资源。

## 六、皮肤包制作
皮肤包实际就是一个只有资源文件的apk项目，并且内部资源和换肤项目参与换肤的资源名字和类型相同。
1. 前置条件：统计主项目中参与换肤的资源。
2. 创建一个专用于换肤功能皮肤包生成的空的Android工程。
3. 在换肤项目中添加主项目中参与换肤的资源，无论是color、drawable、shape等保证资源名字一致。
4. 打包成apk，放到换肤项目中或者上传到云上。  

## 七、注意事项和限制
1. XSkin:1.0.x-SNAPSHOT 目前只支持  textColor｜background｜src｜textColorHint属性，后续会陆续支持
2. 加载网络皮肤包以url作为唯一标识，多次加载同一url不会重复下载即使真正的文件不同。
3. 在应用冷启动后不会记录上一次加载的资源，需要用户自行实现，记录最新加载的url或者本地路径在初始化之后调用XSkin的加载接口。  
4. 不停的加载多个皮肤包理论上会持续的增加内存。

## 八、常见问题

## 九、TODO
1. 皮肤包制作脚手架实现
2. 优化下加载多个皮肤包内存增加问题

项目地址：https://github.com/LangQiang/AndroidProjectEngine/tree/master/component-factory/xskin
