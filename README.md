# 0-spring-mission
스프린트 미션 모범 답안 리포지토리입니다.

# local 태그로  빌드하기
docker build -t discodeit:local .

# image 확인하기
docker images

# http://localhost:8081 로 접속 가능하도록 포트 매핑
docker run --name discodeit-app -d -p 8081:80 --env-file .env discodeit:local
--name : container 이름
-d 백그라운드 실행
-p 포트 설정
--env-file .env 파일 사용


# docker compose 
docker compose down : compose 내리기
docker compose up -d --build : 백그라운드에서 실행하기 전에 image를 다시 빌드하고 실행
docker compose logs -f [app || db] : 컨테이너 로그 확인

