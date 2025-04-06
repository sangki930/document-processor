# Document Processor with Spring AI[CLine TEST]

Document Processor는 Spring Boot와 Spring AI를 활용한 문서 처리 시스템입니다. 이 애플리케이션은 다양한 형식의 문서를 업로드하고 AI를 활용하여 분석할 수 있는 기능을 제공합니다.

## 주요 기능

- **문서 업로드 및 관리**: PDF, DOCX, DOC, TXT 등 다양한 형식의 문서를 업로드하고 관리할 수 있습니다.
- **텍스트 추출**: 업로드된 문서에서 텍스트를 자동으로 추출합니다.
- **AI 분석**: Spring AI와 OpenAI 통합을 통해 다음과 같은 분석을 제공합니다:
  - 문서 요약 생성
  - 주요 키워드 추출
  - 감정 분석
  - 카테고리 분류
- **검색 기능**: 파일명 또는 문서 내용으로 검색할 수 있습니다.
- **비동기 처리**: 대용량 문서 처리와 AI 분석을 비동기적으로 처리합니다.

## 기술 스택

- **백엔드**: Spring Boot 3.x, Spring AI, Spring Data JPA
- **프론트엔드**: Thymeleaf, Bootstrap, JavaScript
- **데이터베이스**: H2 (개발용)
- **문서 처리**: Apache PDFBox (PDF), Apache POI (Office 문서)
- **AI 제공업체**: OpenAI

## 시작하기

### 사전 요구사항

- JDK 17 이상
- Maven
- OpenAI API 키

### 설치 및 실행

1. 저장소 클론
   ```bash
   git clone https://github.com/yourusername/document-processor.git
   cd document-processor
   ```

2. OpenAI API 키 설정
   `src/main/resources/application.properties` 파일에서 다음 속성을 설정합니다:
   ```properties
   spring.ai.openai.api-key=your-openai-api-key
   ```

3. 애플리케이션 빌드 및 실행
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

4. 웹 브라우저에서 애플리케이션 접속
   ```
   http://localhost:8080
   ```

## 프로젝트 구조

```
document-processor/
├── src/
│   ├── main/
│   │   ├── java/com/example/documentprocessor/
│   │   │   ├── config/
│   │   │   ├── controller/
│   │   │   ├── model/
│   │   │   ├── repository/
│   │   │   ├── service/
│   │   │   │   ├── document/
│   │   │   │   ├── ai/
│   │   │   │   └── storage/
│   │   │   └── DocumentProcessorApplication.java
│   │   └── resources/
│   │       ├── static/
│   │       ├── templates/
│   │       └── application.properties
│   └── test/
└── pom.xml
```

## 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다. 자세한 내용은 [LICENSE](LICENSE) 파일을 참조하세요.
