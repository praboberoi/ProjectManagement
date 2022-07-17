fuser -k 9500/tcp || true
source staging-identityprovider/env.sh
java -jar staging-identityprovider/libs/identityprovider-0.0.1-SNAPSHOT.jar \
      --spring.application.name=identity-provider \
      --grpc.server.port=9502 \
      --spring.profiles.active=staging
      --server.port=9500
