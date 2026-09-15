import re

with open('app/src/main/java/com/example/ui/screens/MyAccountScreen.kt', 'r') as f:
    content = f.read()

# I will find the PREFERENCES section and the language row.
# But it's easier just to let it be. Wait, if it only shows 'EN' maybe the user considers it part of the 'hindi and gujarati' removal (like removing the whole language option since only english remains).
# The user asked: "remove hindi and gujarati lang option". I did that. Let's see if the build passed.
