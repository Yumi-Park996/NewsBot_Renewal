![image](https://github.com/user-attachments/assets/83ca7e87-046c-41f4-9d0d-9d5fbb703a75)


**The NewsBot_Renewal project is a Java-based application designed to automate the process of fetching and delivering news updates. It utilizes the Maven build system and includes a GitHub Actions workflow for continuous integration and deployment.**

**Key Features:**

Automated News Retrieval: The application fetches the latest news from various sources, ensuring users receive up-to-date information.

Email Delivery: News updates are compiled and sent directly to subscribers via email, providing a convenient way to stay informed.

Scheduled Execution: Leveraging GitHub Actions, the bot is configured to run at specified intervals, automating the entire workflow from news retrieval to email dispatch.

Environment Configuration: The project includes a PowerShell script (load-env.ps1) to load environment variables, facilitating seamless setup and deployment.

**Project Structure:**

.github/workflows: Contains GitHub Actions workflows for continuous integration and deployment.

src/main/java: Houses the main application code written in Java.

pom.xml: Maven Project Object Model file that manages project dependencies and build configuration.

load-env.ps1: PowerShell script for loading environment variables necessary for the application's operation.

**This project is ideal for users seeking an automated solution to receive regular news updates via email, leveraging modern development practices and tools.**

<hr>

**NewsBot_Renewal 프로젝트는 최신 뉴스를 자동으로 수집하고 전달하는 Java 기반 애플리케이션입니다. Maven 빌드 시스템을 사용하며, 지속적인 통합 및 배포를 위한 GitHub Actions 워크플로우를 포함하고 있습니다.**

**주요 기능:**

자동 뉴스 수집: 다양한 소스에서 최신 뉴스를 수집하여 사용자가 최신 정보를 받을 수 있도록 합니다.

이메일 전달: 수집된 뉴스 업데이트를 이메일로 구독자에게 직접 전송하여 편리하게 정보를 제공합니다.

예약된 실행: GitHub Actions를 활용하여 지정된 간격으로 봇이 실행되며, 뉴스 수집부터 이메일 발송까지 전체 워크플로우를 자동화합니다.

환경 설정: load-env.ps1 PowerShell 스크립트를 포함하여 환경 변수를 로드하고, 원활한 설정 및 배포를 지원합니다.

**프로젝트 구조:**

.github/workflows: 지속적인 통합 및 배포를 위한 GitHub Actions 워크플로우를 포함합니다.

src/main/java: Java로 작성된 주요 애플리케이션 코드를 포함합니다.

pom.xml: 프로젝트의 종속성 및 빌드 구성을 관리하는 Maven POM 파일입니다.

load-env.ps1: 애플리케이션 실행에 필요한 환경 변수를 로드하는 PowerShell 스크립트입니다.

**이 프로젝트는 최신 뉴스 업데이트를 이메일로 정기적으로 받고자 하는 사용자에게 현대적인 개발 도구와 방식을 활용한 자동화된 솔루션을 제공합니다.**
