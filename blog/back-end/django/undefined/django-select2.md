# django‑select2

### django‑select2란 무엇인가

django‑select2는 Django 프로젝트에서 [Select2](https://select2.org/) jQuery 플러그인을 손쉽게 활용할 수 있도록 도와주는 라이브러이다.\
Select2는 일반적인 드롭다운 선택창에 검색 기능과 자동완성 기능을 추가하여 사용자 경험을 개선한다.\
데이터가 많거나 복잡한 선택 옵션이 있을 때 특히 유용하며, Django Admin이나 커스텀 폼에서도 효과적으로 활용할 수 있다.

***

### 프로젝트에 django‑select2 적용하기

이번 프로젝트에서는 django‑select2를 활용하여 경기 정보를 관리하는 모델(TbGame)의 폼 필드에 자동완성 기능을 추가하였다.\
홈 팀과 어웨이 팀을 선택하는 필드에 대해 ModelChoiceField와 ModelSelect2Widget을 사용하였다.\
이를 통해 사용자는 팀 이름의 일부만 입력해도 자동완성된 옵션을 확인할 수 있다.

#### 모델 폼 필드 설정

다음 코드는 `home_team_name`과 `away_team_name` 필드에 django‑select2 위젯을 적용한 예이다.\
각 필드는 `TbTeam` 모델을 대상으로 하며, `team_name` 필드를 검색 기준으로 설정하였다.

```python
home_team_name = forms.ModelChoiceField(
    queryset=TbTeam.objects,
    widget=ModelSelect2Widget(
        model=TbTeam,
        search_fields=['team_name__icontains'],  # 검색을 위한 필드 설정이다.
        attrs={'class': 'custom-select2'},       # 추가적인 클래스 설정이다.
        url='team-autocomplete-ws',              # 자동완성 URL이다.
    ),
    required=True,
    label="홈 팀",
)

away_team_name = forms.ModelChoiceField(
    queryset=TbTeam.objects,
    widget=ModelSelect2Widget(
        model=TbTeam,
        search_fields=['team_name__icontains'],  # 동일한 검색 기준이다.
        attrs={'class': 'custom-select2'},
        url='team-autocomplete-ws',
    ),
    required=True,
    label="어웨이 팀",  
)

```

자동완성 기능은 ajax 요청을 통해 서버의 데이터를 받아오며, django‑select2가 내부적으로 이를 캐싱할 수 있도록 설정할 수도 있다. 이 프로젝트에서는 settings.py에서 locmem 캐시 백엔드를 설정하여 SELECT2\_CACHE\_BACKEND에 할당하였다.



```python
# settings.py
CACHES = {
    'default': {
        'BACKEND': 'django.core.cache.backends.locmem.LocMemCache',
        'LOCATION': 'unique-snowflake',
        'TIMEOUT': 60 * 60 * 24,  # 24시간 캐시
    }
}
SELECT2_CACHE_BACKEND = 'default'

```

{% hint style="info" %}
```python
SELECT2_CACHE_BACKEND = 'default'

이걸 추가해주지 않으면 검색이 캐시가 적용되지 않아서 일정시간이 지나면 검색이 동작하지 않는다.
```
{% endhint %}



### Django Admin 커스터마이징

Django Admin에서 TbGame 모델을 관리할 때 검색 필드에 홈 팀과 어웨이 팀 이름을 포함시켜 보다 쉽게 데이터를 필터링할 수 있도록 설정하였다.

```python
@admin.register(models.TbGame)
class TbGameAdmin(admin.ModelAdmin, GameAdmin):
    search_fields = ['home_team_name', 'away_team_name']  # 검색 가능하게 설정

   class Media:
    css = {
        'all': ('css/custom_select2.css',)  # CSS 파일을 추가
    }
    js = ('js/admin_theme_toggle.js',)  # 다크 모드 감지 및 스타일 변경 JS 추가

```

또한, Admin의 스타일을 더욱 세련되게 커스터마이징하기 위해 추가 CSS와 JavaScript를 함께 로드하였다.

***

### 다크 모드 지원을 위한 CSS와 JavaScript 커스터마이징

최근 다크 모드가 각광받으면서 Admin 인터페이스도 다크 모드에 최적화할 필요가 있었다.\
Select2 위젯 역시 다크 모드에 맞춰 스타일을 변경할 수 있도록 custom\_select2.css와 admin\_theme\_toggle.js 파일을 작성하였다.

#### custom\_select2.css

다음 CSS는 Select2 위젯의 배경색과 글자색에 부드러운 전환 효과를 주기 위해 transition 속성을 추가하였다.

```css
cssCopy/* custom_select2.css */
.select2-container--default .select2-selection--single {
    transition: background-color 0.3s ease, color 0.3s ease;
}

.select2-container--default .select2-results__option--highlighted {
    transition: background-color 0.3s ease;
}
```

#### admin\_theme\_toggle.js

JavaScript 코드는 Admin의 다크 모드 상태를 감지하여 Select2 위젯에 실시간으로 스타일을 적용한다.\
MutationObserver를 사용해 테마 변경 및 동적으로 생성되는 드롭다운에도 스타일을 업데이트한다.

```javascript
javascriptCopy// admin_theme_toggle.js
$(document).ajaxSend(function(event, xhr, settings) {
    function getCookie(name) {
        var cookieValue = null;
        if (document.cookie && document.cookie !== '') {
            var cookies = document.cookie.split(';');
            for (var i = 0; i < cookies.length; i++) {
                var cookie = $.trim(cookies[i]);
                if (cookie.substring(0, name.length + 1) === (name + '=')) {
                    cookieValue = decodeURIComponent(cookie.substring(name.length + 1));
                    break;
                }
            }
        }
        return cookieValue;
    }

    if (!(/^http:.*/.test(settings.url) || /^https:.*/.test(settings.url))) {
        xhr.setRequestHeader("X-CSRFToken", getCookie('csrftoken'));
    }
});

document.addEventListener("DOMContentLoaded", function () {
    function getCurrentTheme() {
        const htmlElement = document.documentElement;
        const theme = htmlElement.getAttribute("data-theme");

        if (theme === "dark") {
            return "dark";
        } else if (theme === "light") {
            return "light";
        } else if (theme === "auto") {
            return window.matchMedia("(prefers-color-scheme: dark)").matches ? "dark" : "light";
        }
        return "light"; // 기본값이다.
    }

    function updateSelect2Theme() {
        const isDarkMode = getCurrentTheme() === "dark";
        console.log(isDarkMode);
        document.querySelectorAll('.select2-container--default .select2-selection--single').forEach(element => {
            element.style.backgroundColor = isDarkMode ? "#000000" : "#ffffff";
            element.style.borderColor = "#417690";
            element.style.color = isDarkMode ? "#ffffff" : "#000000";
        });

        document.querySelectorAll('.select2-container--default .select2-selection--single .select2-selection__rendered').forEach(element => {
            element.style.backgroundColor = isDarkMode ? "#000000" : "#ffffff";
            element.style.borderColor = "#417690";
            element.style.color = isDarkMode ? "#ffffff" : "#000000";
        });

        document.querySelectorAll('.select2-container--default .select2-results__option--highlighted').forEach(element => {
            element.style.backgroundColor = "#417690";
            element.style.color = isDarkMode ? "#000000" : "#ffffff";
        });

        document.querySelectorAll('.select2-dropdown').forEach(element => {
            element.style.backgroundColor = isDarkMode ? "#222222" : "#ffffff";
            element.style.borderColor = "#417690";
        });

        document.querySelectorAll('.select2-results__option').forEach(element => {
            element.style.backgroundColor = isDarkMode ? "black" : "#ffffff";
            element.style.color = isDarkMode ? "#ffffff" : "#000000";
        });

        document.querySelectorAll('.select2-results__option--highlighted').forEach(element => {
            element.style.backgroundColor = "#417690";
            element.style.color = "#ffffff";
        });
    }

    // Django Admin의 현재 모드를 감지하여 스타일을 적용한다.
    function detectAndApplyTheme() {
        updateSelect2Theme();
    }

    // 초기 로딩 시 테마 적용
    detectAndApplyTheme();

    // Django Admin 테마 변경 감지 (MutationObserver 사용)
    const observer = new MutationObserver(() => {
        updateSelect2Theme();
    });
    observer.observe(document.documentElement, { attributes: true, attributeFilter: ["data-theme"] });

    // 동적으로 생성되는 Select2 드롭다운에도 스타일 적용
    const dropdownObserver = new MutationObserver(() => {
        updateSelect2Theme();
    });
    dropdownObserver.observe(document.body, { childList: true, subtree: true });
});
```

이러한 스크립트와 스타일 시트를 통해 Admin 인터페이스에서 다크 모드와 라이트 모드에 따라 Select2 위젯의 디자인이 자동으로 전환되도록 구현하였다.

***

### 마치며

django‑select2는 Django 애플리케이션에 자동완성 기능을 손쉽게 도입할 수 있는 강력한 도구이다.\
특히 데이터 양이 많거나 사용자가 옵션을 빠르게 찾아야 하는 경우에 큰 도움이 된다.\
또한 이번 프로젝트에서는 django‑select2와 함께 Admin 커스터마이징, 캐싱, 다크 모드 지원까지 구현하며 Django에서 제공하는 유연한 확장성을 체감할 수 있었다.
