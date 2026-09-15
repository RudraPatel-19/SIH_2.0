import re

with open('app/src/main/java/com/example/data/model/Language.kt', 'r') as f:
    content = f.read()

# Replace the enum
content = content.replace('  ENGLISH("en", "English", "English"),\n  HINDI("hi", "Hindi", "हिन्दी"),\n  GUJARATI("gu", "Gujarati", "ગુજરાતી")', '  ENGLISH("en", "English", "English")')
content = content.replace('  HINDI("hi", "Hindi", "हिन्दी"),\n  GUJARATI("gu", "Gujarati", "ગુજરાતી")', '')

# Replace LocalizedStrings.get method block
# We can just extract the ENGLISH part or use regex.
# Since we only support English now, we can simplify `get(language: AppLanguage)` to just return English unconditionally, or simply remove the `when(language)` block.

# Wait, let's just make it return English regardless.
lines = content.split('\n')
