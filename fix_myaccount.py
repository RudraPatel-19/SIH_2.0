import re

with open('app/src/main/java/com/example/ui/screens/MyAccountScreen.kt', 'r') as f:
    content = f.read()

# Using regex to remove the App Language block
pattern = r"\s*// App Language\s*Row\(\s*modifier = Modifier\s*\.fillMaxWidth\(\)\s*\.padding\(16\.dp\),\s*horizontalArrangement = Arrangement\.SpaceBetween,\s*verticalAlignment = Alignment\.CenterVertically\s*\)\s*\{.*?\}(?=\s*HorizontalDivider\(color = MaterialTheme\.colorScheme\.outline\.copy\(alpha = 0\.5f\)\))"
new_content = re.sub(pattern, "", content, flags=re.DOTALL)

with open('app/src/main/java/com/example/ui/screens/MyAccountScreen.kt', 'w') as f:
    f.write(new_content)
