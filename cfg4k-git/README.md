# Git config loader
This git config loader will read from a public or private repository and use the FileLoader that you provide to it in 
order to load all the settings in memory.

## Parameters
|Name|Type|Required|Default value|
|---|---|---|---|
|uri|String|Yes| |
|repoDirectory|File|Yes| |
|configFilePath|String|Yes| |
|branch|String|No|master|
|credentials|CredentialsProvider|No|null|
|ssh|CustomConfigSessionFactory|No|null|

## Public auth
Nothing to configure! Just write the uri, folder, the file and optionally, the branch.

## User auth
If you want to use your repository with https and user/password authentication you have to pass the 
`CredentialsProvider (credentials)` as:
```kotlin
val credentials = UsernamePasswordCredentialsProvider("myuser", "mypassword")
```

## Ssh auth
To achieve this you have to instantiate a CustomConfigSessionFactory. 
This config factory will take two parameters (one optional) as the ssh key and the known hosts file:
```kotlin
val ssh = CustomConfigSessionFactory(
    System.getProperty("user.home") + "/.ssh/id_rsa", 
    System.getProperty("user.home") + "/.ssh/known_hosts"
)
```

## Example 
```kotlin
val loader = GitConfigLoader(
        "https://github.com/jdiazcano/cfg4k-git-test.git", // (1) Repo url
        File("cfg4k-git-test"),                            // (2) Folder to be used to clone
        "test.json",                                       // (3) Settings file
        loaderGenerator = ::JsonConfigLoader               // (4) The loader used to load the (3) settings file
)
```