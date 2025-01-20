---
icon: cloudflare
---

# CloudFlare + ACM + EC2 로 Https 적용기

## 배경

기존 서비슨는 EC2 t2.medium에 CloudFlare가 직접연결되어있는 상태였다.&#x20;



<figure><img src="../.gitbook/assets/image (5).png" alt=""><figcaption></figcaption></figure>

대표님께서는 https적용을 하며 SSL인증서가 자동으로 갱신되도록 구현을 요청하셨다.



## 과정

### SSL 인증서 자동갱신을 위한 2가지 방안

1. Cloudflare Universal SSL
2. AWS Certificate Manager(ACM)

Cloudflare의 Universal SSL을 사용하면 도메인에 대한 인증서를 자동으로 갱신해주지만, EC2로 정보가 전달될 때 HTTPS로 요청이 전송된다. 하지만 EC2의 Nginx에서는 443 포트에 대한 처리가 되어있지 않기 때문에 처리해야할 작업들이 많았다.



따라서 ACM을 사용해서 LoadBalancer에 SSL인증서를 등록 및 자동 갱신하고 이를 통해 443포트를 리스닝해서 EC2로 넘겨주도록 구현하였다.



## 결론

<figure><img src="https://lh7-rt.googleusercontent.com/docsz/AD_4nXcZuvFhTrTacqeZeD2R__xmKzLjx4q0PgEB3PD9F4AQtbjB82uIJkrbyggFgHMusXYyMKtdd5RJLhFXiAAnshIW1Thz_JlLn52gh5TsUpIyqgeT7SbQKsdrA5uVMuJ3B3emSRFetg?key=oEpSzewSotJnvniOrkQQmqAB" alt=""><figcaption></figcaption></figure>

