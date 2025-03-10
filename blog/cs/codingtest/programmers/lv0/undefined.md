# 세 개의 구분자

```java
import java.util.*;

class Solution {
    public String[] solution(String myStr) {
        ArrayList<String> answer = new ArrayList();
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<myStr.length(); i++){
            if(myStr.charAt(i) == 'a' || myStr.charAt(i) == 'b' || myStr.charAt(i) == 'c'){
                if(sb.length() != 0){
                    answer.add(sb.toString());
                    sb = new StringBuilder();
                }
            }
            else{
                sb.append(myStr.charAt(i));
            }
            
        }
        
        if(sb.length() != 0){
            answer.add(sb.toString());
        }
        
        if(answer.size() == 0){
            answer.add("EMPTY");
            
        }
        
        return answer.toArray(new String[0]);
    }
}
```

