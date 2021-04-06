---
sidebar: auto
---

# Deploy

## local deployment

```bash
# rm -rf ~/.m2/repository/com/github/daggerok
./mvnw -P local-deploy
```

## local release

```bash
# rm -rf ~/.m2/repository/com/github/daggerok
./mvnw  -Dresume=false release:prepare release:perform \
                        -Darguments="-Dmaven.deploy.skip=true" \
                        -Plocal-release,local-deploy \
                        -DgenerateBackupPoms=false \
                        -DskipTests -B -s .mvn/settings.xml
```
