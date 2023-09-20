# kopring

<img src="https://github.com/van1164/kopring/assets/52437971/15373147-90f6-4d08-8416-9132272188a0" width="20%" height="20%" />

### [1. 컨트롤러 활용](#컨트롤러-활용)

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

# JUnit을 사용한 테스트
``` kotlin
    @Test
    fun mainGetMap() {
        println(">> Assert getMapping()")
        val entity = restTemplate.getForEntity<String>("/")
        assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(entity.body).contains("test")
    }
```
