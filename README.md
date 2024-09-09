# GachMap

## 가천대학교 교내 AR 내비게이션 서비스 'Gach.가자'
----

> 가천인의 시간을 가치있게, Gach.가자
> 
> 👉 'Gach.가자' 앱스토어 : https://apps.apple.com/kr/app/gach-%EA%B0%80%EC%9E%90/id6504272608


> 
> AI 모델 학습용 데이터 수집 애플리케이션 'GachData'
> 
> 👉 'GachData' 레포지토리 : https://github.com/LeeeeSuHyeon/time-required-data-collection-app

![로그인](https://github.com/user-attachments/assets/8e29ad31-00d2-4aa8-9928-f03856f51ebe)
![회원가입](https://github.com/user-attachments/assets/def6a847-1c51-49c4-b7b4-6dee88a2fc64)
![카테고리](https://github.com/user-attachments/assets/671a91a0-a601-4337-810e-ad28c784710a)
![대시보드](https://github.com/user-attachments/assets/faf8efa3-0e52-4be8-9628-a50ebbb6b090)
![캠퍼스맵](https://github.com/user-attachments/assets/aa0fc9e0-09e1-445d-9b55-bdc22bb087fc)
![행사탭](https://github.com/user-attachments/assets/2181b84c-7fb2-4f73-ae42-e566cae6a878)
![마이페이지](https://github.com/user-attachments/assets/0517d189-7872-4f4c-a6d9-44cbcca6497f)
![경로](https://github.com/user-attachments/assets/b50d2cca-d792-4f82-a698-62f3f532a412)
![AR](https://github.com/user-attachments/assets/e5f0b9a0-9ca1-478b-9488-021069812ea8)
![내비게이션](https://github.com/user-attachments/assets/39e45b06-46da-4d1f-a3ea-65f9a214dc1e)

## 목차
- [1. 프로젝트 소개](#프로젝트-소개)
  - [1.1. 프로젝트 배경](#프로젝트-배경)
  - [1.2. 프로젝트 설명](#프로젝트-설명)
  - [1.3. 프로젝트 기능(사용자)](#프로젝트-기능(사용자))
  - [1.4. 프로젝트 기능(관리자)](#프로젝트-기능(관리자))
- [2. 팀원 소개](#팀원-구성)
- [3. 개발 환경](#개발-환경)
- [4. 개발 기간 및 산출물](#개발-기간-및-산출물 )
- [5. 시연 영상](#시연-영상)
  - [5.1. 카테고리 별 건물 위치 확인](#카테고리-별-건물-위치-확인)
  - [5.2. 건물 별 상세 정보 확인](#건물-별-상세-정보-확인)
  - [5.3. 건물 별 상세 정보 확인](#건물-별-상세-정보-확인)
  - [5.4. 검색 기능](#검색-기능)
  - [5.5. 행사 정보 및 위치 확인](#행사-정보-및-위치-확인)
  - [5.6. 경로 조회 및 경로 별 AI 소요시간 확인 기능](#경로-조회-및-경로-별-AI-소요시간-확인-기능)
  - [5.7. AR 내비게이션](#AR-내비게이션)
- [6. AI 예측 소요시간 비교](#AI-예측-소요시간-비교)






## 프로젝트 소개


<img width="984" alt="Gach가자" src="https://github.com/user-attachments/assets/ae35f453-2a60-4e32-8461-ced1bc44a661">

### 프로젝트 배경
- 가천대학교는 잘 관리된 교내 지도를 보유하고 있지만, 넓은 캠퍼스로 인해 신입생 및 처음 방문하는 사람들이 길을 찾기 어려워합니다. 
- 교내 지도에 표시되지 않은 보건실이나 학과 사무실 등의 시설들은 재학생조차 위치를 모르는 경우가 많습니다.
- 기존 지도 앱들은 도보가 아닌 차도를 기준으로 경로를 안내하며, 사용자의 개별 속도나 날씨를 고려한 정확한 소요 시간을 제공하지 못합니다. 
- 이러한 문제를 해결하기 위해 교내 지도를 직접 매핑하고, AR 기술을 활용한 길 안내 서비스를 제공합니다. 
- 성별, 나이, 키, 몸무게, 속도, 날씨 데이터를 고려하여 사용자 맞춤형 소요 시간을 제공함으로써 더 나은 서비스를 제공합니다.



### 프로젝트 설명 
- 가천대학교 교내 이동 시간 데이터를 활용한 AI 예측 소요시간 제공, 최적 경로 추천 및 AR 내비게이션 서비스입니다.
- 이 서비스는 교내 도보 경로와 개인화된 예측 소요 시간을 제공하며, 지도 맵과 AR을 통해 길 안내 서비스를 제공합니다. 이를 통해 사용자의 편의성과 만족도를 극대화하는 것이 목표입니다.
- 주 사용자는 가천대학교 학생과 교직원이며, 모바일(스마트폰)을 통해 쉽게 접속하고 사용하도록 설계되었습니다.




### 프로젝트 기능 (사용자)
- 회원가입 / 로그인 / 로그아웃 / 개인정보 수정 / 회원탈퇴
- 대시보드
- AR 캠퍼스 투어
- 날씨 정보 조회
- 교내 행사 및 위치 조회
- 교내 건물 조회 (캠퍼스 맵) / 건물 별 상세 정보 조회 
- 카테고리 별 위치 및 정보 조회
- 키워드 검색 기능
- 사용자 위치(GPS) 조회
- AR 내비게이션 / 지도 내비게이션 기능 
- 이용내역 조회
- 문의내역 조회 / 문의하기



### 프로젝트 기능 (관리자)
- 회원 관리
- 지점 관리
- 경로 관리
- 건물 정보 관리
- 행사 정보 관리
- 문의 관리
- AI 모델 관리
- 게스트 관리
- 로그


## 팀원 구성

| **Design / Mobile** | **Mobile / AR** | **Frontend / AI** | **Backend** |
| :------: |  :------: | :------: | :------: |
| **원웅주** | **이수현** | **노명준** | **김민서** |
| [<img src="https://avatars.githubusercontent.com/u/157135564?v=4" height=150 width=150> <br/> @wwj0313](https://github.com/wwj0313) | [<img src="https://avatars.githubusercontent.com/u/105594739?v=4" height=150 width=150> <br/> @LeeeeSuHyeon](https://github.com/LeeeeSuHyeon) | [<img src="https://avatars.githubusercontent.com/u/126738583?v=4" height=150 width=150> <br/> @mxxxjun](https://github.com/mxxxjun) | [<img src="https://avatars.githubusercontent.com/u/44363238?v=4" height=150 width=150> <br/> @hurrayPersimmon](https://github.com/hurrayPersimmon) |




## 개발 환경

- Front : HTML/CSS/JS, Thymeleaf
- Back-end : Spring4, Spring Boot
- Mobile : Swift 
- DB : MariaDB
- IDE : Xcode, Git, Intellij IDEA, VSC, PyCharm
- API : Google Map API, 기상청 단기예보 API
- 버전 관리 : Github
- 디자인 : Figma
- 협업 툴 : Notion, Discord 



## 개발 기간 및 산출물 

### 2024.03 ~ 2024.06.07
* 요구 분석 (제안서) : 03/05 ~ 03/19
* 설계 단계 (설계서) : 03/19 ~ 04/09
* 구현 단계 (구현 진도표) : 04/09 ~ 05/21
* 완료 단계 (완료 보고서) : 05/21 ~ 06/07


## 시연 영상

### 카테고리 별 건물 위치 확인 
![카테고리 별 위치 확인](https://github.com/user-attachments/assets/44f0e33e-5fa4-45c9-859c-3d88796915be) 

----

### 건물 별 상세 정보 확인 
![캠퍼스맵](https://github.com/user-attachments/assets/0f6be562-8b83-48d7-a35c-fc1724c2861c)

----

### 검색 기능
![검색](https://github.com/user-attachments/assets/c5a9c96a-ebc9-4b9b-8238-e0bb6b4cc09c)

----

### 행사 정보 및 위치 확인
![행사 정보](https://github.com/user-attachments/assets/196625e0-5f40-41c8-b2d6-e7e8bc0bc139)

----

### 경로 조회 및 경로 별 AI 소요시간 확인 기능 
![경로조회](https://github.com/user-attachments/assets/b213e5cf-05f4-4d46-a215-d14fccb80ced)

----


### AR 내비게이션 
![AR 내비게이션](https://github.com/user-attachments/assets/aa9b8fbc-faf2-4a7d-afcf-463fc220e4d2)

----

## AI 예측 소요시간 비교
<img width="864" alt="비교" src="https://github.com/user-attachments/assets/bfec24a6-12c7-47bd-a991-579442da0994">

