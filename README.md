# User Authentication

## ERD 
![image](https://user-images.githubusercontent.com/30483337/146680737-85747173-5ccb-42f9-b9e1-dd782c27f4e8.png)

## 아키텍처 설계
![image](https://user-images.githubusercontent.com/30483337/146684647-9e1df7f6-9e66-4e7b-a160-e96d0d419183.png)


## 개인 프로젝트 목표 & 수정된 것
https://github.com/kimhanui/Login-SG/issues/8

## 프로젝트 실행하기
application-auth.yml 생성후, 아래 property를 채워주세요.
```yaml
jwt:
  secret-key:         {here}
  access-valid-time:  {here}
  refresh-valid-time: {here}
my:
  mail:
    sender:
      username: {here}
      password: {here}
```