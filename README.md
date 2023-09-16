# kopring


#### 코틀린 + Spring 

[1. 컨트롤러 활용](#컨트롤러-활용)

# 컨트롤러 활용
``` kotlin
@Controller
class MainController {

    @GetMapping("")
    @ResponseBody
    fun mainGetMap(model : Model): String {
        return "test"
    }
}
```
