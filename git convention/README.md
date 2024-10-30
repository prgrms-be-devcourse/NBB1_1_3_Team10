# 🚣🏻‍♂️ Git - flow 전략

- **master** : 배포가 가능한 가장 메인이 되는 브랜치
- **hotfix** : 빠르게 버그를 수정해야 할 때 사용하는 브랜치
- **release** : 프로젝트 배포를 준비하기 위해 사용하는 브랜치
- **develop** : 개발 과정에서 사용하는 중심 브랜치
    - **feat** : 각 기능을 구현할 때 사용하는 브랜치
    
<p align="center"> <img src="https://github.com/user-attachments/assets/956518a4-c18f-428e-8f4a-284a35b85a85" width="600"/> </p>
    

---
<br>

# ☄️ Commit 컨벤션

- pull request merge 시 모든 팀원의 코드 리뷰와 승인 시 가능
- merge 시 rebase를 진행함
<br>

#### 코드 리뷰 시 참고 자료

https://tech.kakao.com/posts/498

---
<br>

# 🎋 Branch 컨벤션

#### 기본 네이밍 규칙

소문자와 하이픈 사용: 브랜치 이름은 소문자를 사용하고, 단어 사이는 하이픈('-')으로 구분

영숫자만 사용: 영숫자(a-z, 0-9)와 하이픈만 사용하되, 구두점이나 공백, 밑줄은 피함

명확하고 간결하게: 브랜치가 수행하는 작업을 명확하게 반영하는 이름을 선택


#### 브랜치 유형별 네이밍 예시

- 기능 개발: feature/login-system
- 버그 수정: bugfix/header-styling
- 긴급 수정: hotfix/critical-security-issue
- 릴리스 준비: release/v1.0.1
- 문서 작업: docs/api-endpoints1

---
<br>

# 💬 Commit Message 컨벤션
```
커밋 메시지 = 제목 + 본문 + 꼬리말
    각 파트는 2번의 개행을 통해 구분 (= 하나의 빈 줄)

제목 = 커밋 태그 + 개조식 구문
본문 = 상세히 무엇을 왜 변경했는지 작성 (어떻게는 포함 X)
꼬리말 = issue 트래커 번호
```

<br>

### 타입
| 태그 이름        | 설명                                                    |
|:----------------:|:-------------------------------------------------------:|
| Feat             | 새로운 기능을 추가할 경우                               |
| Fix              | 버그를 고친 경우                                        |
| Design           | CSS 등 사용자 UI 디자인 변경                           |
| !BREAKING CHANGE | 커다란 API 변경의 경우                                  |
| !HOTFIX          | 급하게 치명적인 버그를 고쳐야 하는 경우                |
| Style            | 코드 포맷 변경, 세미 콜론 누락, 코드 수정이 없는 경우  |
| Refactor         | 프로덕션 코드 리팩토링                                  |
| Comment          | 필요한 주석 추가 및 변경                                |
| Docs             | 문서를 수정한 경우                                      |
| Test             | 테스트 추가, 테스트 리팩토링(프로덕션 코드 변경 X)      |
| Chore            | 빌드 태스크 업데이트, 패키지 매니저 설정(프로덕션 코드 변경 X) |
| Rename           | 파일 혹은 폴더명을 수정하거나 옮기는 작업만인 경우     |
| Remove           | 파일을 삭제하는 작업만 수행한 경우                      |


<br>

## 제목 작성법
제목 = 코드 변경 사항에 대한 짧은 요약

```
1. 처음은 동사 원형으로 시작
2. 총 글자 수는 50자 이내로 작성
3. 마지막에 특수문자는 삽입 x
4. 개조식 구문으로 작성
```

<br>

예시)
Feat: "추가 OAuth"<br>
Feat: "Add OAuth"

<br>

## 본문 작성법
```
1. 한 줄 당 72자 내로 작성
2. 최대한 상세히 작성
3. 어떻게 변경했는지 보다 무엇을 변경했는지 또는 왜 변경했는지 작성
```

## 꼬리말 작성법
```
1. optional로 이슈 트래커 ID를 작성
2. "유형: #이슈 번호" 형식으로 사용
3. 여러 개의 이슈 번호를 적을 때는 쉼표로 구분
4. 이슈 트래커 유형은 다음 중 하나를 사용
    - Fixes: bugFix 이슈 번호
    - Ref: 참고하면 좋은 이슈 번호
    - Related to: feat(enhancement) 이슈 번호
ex) Fixes: #45 Ref: #34, #23
```

## Commit Message 예시
```
Feat: "추가 로그인 함수"

로그인 API 개발

Resolves: #123
Ref: #456
Related to: #48, #45
```

<br>

#### 출처

https://overcome-the-limits.tistory.com/entry/협업-협업을-위한-기본적인-git-커밋컨벤션-설정하기#본문은-어떻게-작성하는가

<br>

#### 참고하면 좋을만한 자료

https://guuumi.tistory.com/128

https://velog.io/@code-bebop/Github-Issue-Tracker와-Project-Board
