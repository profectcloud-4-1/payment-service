# Dockerization

## 이미지 빌드

원하는 이미지이름과 태그이름을 붙여 컨테이너 이미지를 빌드합니다.

- application-dev.yml과 application-secret.yml 파일은 PROJECT_ROOT/config 아래로 이동시킵니다.
  기존 경로인 src/resources/...에 두면 이미지 빌드 과정에서 이미지에 포함될 우려가 있기 때문입니다.

- 실제 배포환경인 리눅스에서는 --platform linux/amd64 옵션을 추가할 수 있습니다.

```bash
docker build --no-cache -t <이미지이름>:<태그이름> .
```

## 컨테이너 구동

### 선택 1: 바로 구동

```bash
docker run \
--name <custom-name> \
-p <host-port>:8080 \
-v my-config-path:/app/config \
<이미지이름>:<태그이름>
```

- `custom-name`: 개발자가 컨테이너를 식별하기 용이하게 별명의 개념으로 사용됩니다.
- `host-port`: 컨테이너 내부의 8080포트로 포워딩 시킬 호스트의 포트입니다. 가령 8001이라면, localhost:8001으로 접속 시 컨테이너(spring boot application)의 8080로 트래픽이 전달되어 api 이용이 가능합니다.
- `my-config-path`: application-dev.yml과 application-secret.yml이 위치한 폴더 경로입니다.
  위 설명대로면 PROJECT_ROOT/config가 my-config-path가 될 것이므로, 터미널의 현재 작업폴더가 PROJECT_ROOT라면 `-v config:/app/config` 라고 작성합니다.

### 선택 2: docker compose로 구동

docker-compose.yaml

```yaml
services:
  api:
    image: <이미지이름>:<태그이름>
    ports:
      - 8080:8080
    environment:
      - SPRING_PROFILES_ACTIVE=dev,secret
    volumes:
      - ./config:/app/config
    user: 1000:1000
```

- `user: 1000:1000`: 대게는 컨테이너 내부의 리눅스 사용자는 root로 두지 않습니다(따로 옵션을 넣지 않으면 root가 기본입니다).
  컨테이너 내부 사용자의 UID, GID를 root(0)가 아닌 값으로 지정합니다. 지정 가능한 UID, GID가 이미 정해져있는 경우도 있지만 적어도 현재 우리의 프로젝트에서는 해당 없으므로 1000과 같이 임의의 값으로 지정해도 무방합니다.

- `SPRING_PROFILES_ACTIVE`: 활성화할 프로파일입니다. 프로덕션 환경에서는 "dev,secret"대신 "prod,secret"등으로 지정할 수 있습니다.
