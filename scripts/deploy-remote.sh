#!/usr/bin/env bash
# EC2 서버에서 SSM을 통해 실행되는 배포 스크립트 (ec2-user 권한으로 실행됨)
# 앱은 systemd 서비스(globeot)로 실행된다 → 죽거나 재부팅돼도 자동으로 다시 켜짐
set -e

APP_DIR=/home/ec2-user/globeot-back
BUCKET=globeot-deploy-080403790659
SERVICE=globeot
UNIT_FILE=/etc/systemd/system/${SERVICE}.service

cd "$APP_DIR"

echo "[1/5] S3에서 새 jar 다운로드"
aws s3 cp "s3://${BUCKET}/app.jar" app.jar

echo "[2/5] systemd 서비스 파일 확인"
UNIT_CONTENT="[Unit]
Description=Globeot backend (Spring Boot)
After=network-online.target
Wants=network-online.target

[Service]
User=ec2-user
WorkingDirectory=${APP_DIR}
ExecStart=/usr/bin/java -Xms256m -Xmx512m -jar app.jar --spring.profiles.active=prod --spring.config.additional-location=file:.env.properties
Restart=always
RestartSec=5
SuccessExitStatus=143
TimeoutStopSec=30
StandardOutput=truncate:${APP_DIR}/app.log
StandardError=inherit

[Install]
WantedBy=multi-user.target"
if [ ! -f "$UNIT_FILE" ] || [ "$(sudo cat "$UNIT_FILE")" != "$UNIT_CONTENT" ]; then
  echo "$UNIT_CONTENT" | sudo tee "$UNIT_FILE" >/dev/null
  sudo systemctl daemon-reload
  sudo systemctl enable "$SERVICE"
  echo "서비스 파일 갱신"
fi

echo "[3/5] 예전 방식(nohup)으로 떠 있는 앱 정리"
if ! sudo systemctl is-active --quiet "$SERVICE"; then
  if [ -f app.pid ]; then
    kill -15 "$(cat app.pid)" 2>/dev/null || true
    sleep 5
  fi
  pkill -f 'java .*app.jar' || true
  sleep 3
fi
rm -f app.pid

echo "[4/5] 앱 재시작 (systemd)"
sudo systemctl restart "$SERVICE"

echo "[5/5] 헬스체크 (기동 완료까지 최대 2분 폴링)"
for i in $(seq 1 40); do
  if curl -sf http://localhost:8080/health >/dev/null 2>&1; then
    sudo systemctl status "$SERVICE" --no-pager | head -5 || true
    echo "DEPLOY_OK"
    exit 0
  fi
  sleep 3
done
echo "HEALTH_FAILED"
sudo systemctl status "$SERVICE" --no-pager | head -15 || true
tail -n 60 app.log
exit 1
