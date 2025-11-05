1. 项目导入有问题时，重新导入过程
## 完全清理
rm -rf .idea/  .gradle 
rm  src/main/*.iml 
rm src/test/*.iml
###删除生成的代码
./gradlew clean
## file|New|Project from Existing Sources...
create project from existing sources| 启动后选择 gradle
### 在选项中配置目录

2. lombok 问题
3. 项目中使用了 lombok 注解，但是 IDE 中没有提示
