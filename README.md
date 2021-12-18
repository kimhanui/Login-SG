## User Authentication

아키텍쳐 설계
- erd, uml(또는 프로덕트백로그), 아키텍처 설계

방어하고자했던 것 (보안. 주제가 인증이니까)
- csrf, cors

얻은 것
- `SecurityConfig.java`의 security chain filter 의 구성을 다 이해했는가?
- 에러핸들링(dispatcherServlet in, out), 에러 응답 format 통일

### To Try

application-auth.yml 생성후, 아래 property를 채워주세요.
```yaml
jwt:
  secret-key: 
  access-valid-time:
  refresh-valid-time:
mail:
  sender:
    username:
    password:
```