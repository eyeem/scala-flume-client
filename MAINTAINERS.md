## Publish a new version of this library to Bintray

1. make your changes, merge to master branch, push to origin
2. make sure to have a `~/.bintray/credentials` file containing

```
realm = Bintray
host = api.bintray.com
user = <username>
password = <API key>
```
3. make sure you are a member of the eyeem organization on bintray ( see https://bintray.com/<username> )
4. run `sbt release`
5. You may wish to update the README.
