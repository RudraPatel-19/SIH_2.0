with open('app/src/main/java/com/example/ui/components/KisanTopBar.kt', 'r') as f:
    lines = f.readlines()

new_lines = []
skip = False
for i, line in enumerate(lines):
    if "color = MaterialTheme.colorScheme.onPrimaryContainer" in line and "}" in lines[i+1] and "}" in lines[i+2]:
        # Keep these lines but skip the dangling brackets below
        pass
    if "              }" in line and "            )" in lines[i+1]:
        skip = True
    if skip and "// Profile Button" in line:
        skip = False
    
    if not skip:
        new_lines.append(line)

# Let's write a safer regex script
