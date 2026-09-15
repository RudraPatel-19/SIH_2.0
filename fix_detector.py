import re

with open('app/src/main/java/com/example/ai/CropDiseaseDetector.kt', 'r') as f:
    lines = f.readlines()

new_lines = []
for line in lines:
    if "adviceHindi =" in line or "adviceGujarati =" in line:
        continue
    new_lines.append(line)

with open('app/src/main/java/com/example/ai/CropDiseaseDetector.kt', 'w') as f:
    f.writelines(new_lines)
