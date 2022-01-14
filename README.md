## springboot-sureness-example

 

fork 官方的集成例子**sample-tom**进行了拆分集成示例

### 集成示例 

* JWT方式认证鉴权
> 切换分支jwt分支查看代码
> 
> 通过官方提供的组件来演示认证鉴权 
> 
> 注解+数据库方式加载 权限数据
 


* 自定义token（redis）认证 + 鉴权（注解和数据库配置方式）
> 切换分支custom-token 分支查看代码
> 
> 通过Redis来演示认证鉴权、以及一定时间内刷新Token的示例
>  
> 注解+数据库方式加载 权限数据

* 自定义token（redis）认证 + 鉴权（配置文件`sureness.yml`和注解方式）
> 切换分支custom-token-file 分支查看代码
>
> 通过Redis来演示认证鉴权、以及一定时间内刷新Token的示例
> 
>  配置文件sureness.yml + 注解 加载权限数据


### 测试示例
> 测试登录认证：`AccountController`
>
> 测试鉴权例子： `AnnotationController`
 