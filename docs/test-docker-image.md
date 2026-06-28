# Build a Test Docker Image

1. Run a Maven build from the root directory
2. Run Docker BuildX to build a test image
```sh
docker buildx build \
  --platform linux/arm64 \
  --file ./Dockerfile.early-access-release \
  --tag schemacrawler/schemacrawler-ai:local-test \
  --load \
  .
```
3. Run local image
```sh
docker run \
  --name schemacrawler-ai \
  -it --rm \
  --mount type=bind,source="$(pwd)",target=/home/schcrwlr/share \
  schemacrawler/schemacrawler-ai:local-test
```
4. Verify local image
```sh
mvn \
  --no-transfer-progress \
  --batch-mode \
  clean install

mvn \
  --no-transfer-progress \
  --batch-mode \
  -Dverify \
  -Ddocker_image_tag=local-test \
  -pl schemacrawler-ai-verify \
  test
```
5. Clean Docker Local Build
```sh
docker buildx prune --all
```
