# 🤖 중고마켓 챗봇 프로젝트

Spring Boot 기반의 중고마켓 웹 애플리케이션으로, AI 챗봇을 통한 상품 검색 및 거래 기능을 제공합니다.

📋 **노션 페이지**: https://www.notion.so/2268e4dd67248015bb47f0e50570c75c

🌐 **배포 링크**: http://13.125.70.176:8080/

## 🛠️ 기술 스택

- **Backend**: Spring Boot 3.5.3, Java 17, Spring Security, JPA, WebSocket
- **Database**: PostgreSQL + PostGIS
- **Cloud**: AWS S3, AWS EC2, AWS RDS
- **Frontend**: Thymeleaf, JavaScript, CSS

## 🗄️ 데이터베이스 설계 (ERD)

<img width="1296" height="531" alt="스크린샷 2025-07-22 154512" src="https://github.com/user-attachments/assets/730ccf7a-980a-403e-9a08-28bfb6e8aa79" />

## 📁 프로젝트 구조

```
chatbot-used-market/
├── src/main/java/com/example/chatbot_used_market/
│   ├── controller/      # REST API 컨트롤러
│   ├── service/         # 비즈니스 로직
│   ├── entity/          # JPA 엔티티
│   ├── dto/             # 데이터 전송 객체
│   └── config/          # 설정 클래스
├── src/main/resources/
│   ├── templates/       # Thymeleaf 템플릿
│   └── static/          # 정적 리소스
└── deploy.sh            # 배포 스크립트
```

## 🎬 시연영상

📹 **프로젝트 시연영상**: https://youtu.be/9_8_Jx5Gik4

## 🚀 제공 기능

### 🔐 사용자 인증

- Google OAuth2 소셜 로그인
- 사용자 프로필 관리
- 동네인증 기능

### 🛒 중고거래

- 상품 등록 및 관리
- 카테고리별 상품 검색
- 무한 스크롤 기반 상품 목록

### 💬 AI 챗봇

- **Google Gemini API** 기반
- 자유로운 질문과 대화형 상품 검색 ("노트북 추천해줘", "가격대별 전자기기 찾아줘")
- 상품 정보에 대한 자유로운 질의응답

### 💬 실시간 채팅

- WebSocket 기반 실시간 메시지
- 판매자와 구매자 간 1:1 채팅
- 거래 확정 기능

### 📍 위치 기반 서비스

- PostGIS를 활용한 지역별 상품 검색
- 5km 반경 내 상품 필터링
- 사용자 위치 기반 추천

### 📸 이미지 관리

- AWS S3를 통한 이미지 업로드
- 다중 이미지 지원 (최대 5장)
- 이미지 미리보기 기능

### ⭐ 리뷰 시스템

- 거래 후기 작성 및 관리
- 판매자/구매자 평가
- 별점 시스템
