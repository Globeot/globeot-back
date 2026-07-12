#!/usr/bin/env bash
# EC2 서버에서 SSM을 통해 실행되는 배포 스크립트 (ec2-user 권한으로 실행됨)
set -e

APP_DIR=/home/ec2-user/globeot-back
BUCKET=globeot-deploy-080403790659

cd "$APP_DIR"

echo "[1/4] S3에서 새 jar 다운로드"
aws s3 cp "s3://${BUCKET}/app.jar" app.jar

echo "[2/4] 기존 앱 종료"
if [ -f app.pid ]; then
  kill -15 "$(cat app.pid)" 2>/dev/null || true
  rm -f app.pid
  sleep 5
fi
pkill -f 'app.jar' || true
sleep 5

echo "[3/4] 새 앱 기동"
setsid nohup java -Xms256m -Xmx512m -jar app.jar \
  --spring.profiles.active=prod \
  --spring.config.additional-location=file:.env.properties \
  > app.log 2>&1 < /dev/null &
echo $! > app.pid
sleep 5

echo "[4/4] 헬스체크 (기동 완료까지 최대 2분 폴링)"
ps -p "$(cat app.pid)" -o pid,cmd || true
for i in $(seq 1 40); do
  if curl -sf http://localhost:8080/health >/dev/null 2>&1; then
    echo "DEPLOY_OK"
    exit 0
  fi
  sleep 3
done
echo "HEALTH_FAILED"
tail -n 60 app.log
exit 1
