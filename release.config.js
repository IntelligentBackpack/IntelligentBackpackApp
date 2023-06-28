var publishCmd = `
git tag -a -f \${nextRelease.version} \${nextRelease.version} -F CHANGELOG.md || exit 1
./gradlew assembleRelease --stacktrace -PstorePassword="$KEY_STORE_PASSWORD" -PkeyPassword="$KEY_PASSWORD" -PkeyAlias="$ALIAS" || exit 2
./gradlew bundleRelease --stacktrace -PstorePassword="$KEY_STORE_PASSWORD" -PkeyPassword="$KEY_PASSWORD" -PkeyAlias="$ALIAS" || exit 3
git push --force origin \${nextRelease.version} || exit 4
`
var config = require('semantic-release-preconfigured-conventional-commits');
config.plugins.push(
    ["@semantic-release/exec", {
        "publishCmd": publishCmd,
    }],
    ["@semantic-release/github", {
        "assets": [
            { "path": "app/build/outputs/apk/release/*.apk" },
            { "path": "app/build/outputs/bundle/release/*.aab" },
        ]
    }],
    "@semantic-release/git",
)
config.branches = [ "main" ]
module.exports = config