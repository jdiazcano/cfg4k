---
name: Bug report
about: Create a report to help us improve
title: "[BUG]"
labels: ''
assignees: ''

---

**Describe the bug**
A clear and concise description of what the bug is.

**To Reproduce**
In order for me to reproduce this behavior I need:

1. Version of Cfg4k
1. ConfigProvider
1. ConfigLoader
1. ConfigSource
1. Configuration files unless the source is a String
1. Any interfaces/dataclasses used for binding/getting

As example, it is easy to get an easy prototype with all the 5 points in the list:
```kotlin
val json = """
        {
            "a": "b"
        }
        """.trimIndent()
val loader = JsonConfigLoader(StringConfigSource(json))
val provider = DefaultConfigProvider(loader)

data class MyClass(val a: String)
```

**Expected behavior**
A clear and concise description of what you expected to happen.

**Additional context**
Add any other context about the problem here.
