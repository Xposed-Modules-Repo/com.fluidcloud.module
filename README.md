# XposedFluidCloud

ColorOS 16 流体云 UI 调整 Xposed 模块

## 功能

- 调整流体云胶囊高度、圆角、边距等尺寸
- 自定义胶囊背景色、边框色及其光晕效果
- 自定义媒体卡片背景色、边框色
- 调整卡片高度、间距、圆角
- 设置热点/音乐胶囊持续时长
- 封面圆角切换

## 使用

1. 安装模块并激活（推荐 LSPosed）
2. 勾选目标应用（系统界面）
3. 重启系统界面或重启手机
4. 打开模块进行个性化设置

## 构建

```bash
# Debug 包
./gradlew assembleDebug

# Release 包（需要签名密钥）
$env:KEYSTORE_PASSWORD="你的密码"
$env:KEY_PASSWORD="你的密码"
./gradlew assembleRelease
```

APK 输出在 `app/build/outputs/apk/`

## 依赖

- [Miuix KMP](https://github.com/Yukonga/Miuix-KMP) - MIUI 风格 Compose 组件库
- [LibXposed](https://github.com/libxposed/libxposed) - Xposed 框架 API

## License

GNU General Public License v3.0

## 致谢

- Coolapk@那泛滥的思绪 - 原作者
