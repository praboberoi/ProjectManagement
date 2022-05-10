fuser -k 9500/tcp || true
source /gitlab-runner/staging-identityprovider/env.sh
java -jar staging-identityprovider/libs/identityprovider-0.0.1-SNAPSHOT.jar \
      --spring.application.name=identity-provider \
      --grpc.server.port=9500 \
      --spring.profiles.active=staging
