# Build a Test Docker Image

1. Run a Maven build from the root directory
2. Run Docker BuildX to build a test image

```
docker buildx build \
  --platform linux/arm64 \
  --file ./Dockerfile.early-access-release \
  --tag schemacrawler/schemacrawler-ai:test \
  --load \
  .
```