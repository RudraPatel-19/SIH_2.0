import os
import re

def process_file(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    # If it already imports KisanPrimaryButton, skip (or don't, just to be safe)
    if 'import androidx.compose.material3.Button' in content and 'KisanPrimaryButton' not in content:
        content = content.replace(
            'import androidx.compose.material3.Button\n',
            'import androidx.compose.material3.Button\nimport com.example.presentation.components.KisanPrimaryButton\n'
        )
    
    if 'import androidx.compose.material3.Card' in content and 'KisanCard' not in content:
        content = content.replace(
            'import androidx.compose.material3.Card\n',
            'import androidx.compose.material3.Card\nimport com.example.presentation.components.KisanCard\n'
        )

    # We only want to replace Button( when it's not IconButton, TextButton, OutlinedButton
    # But wait, Button( is just Button(
    # re.sub with word boundary
    content = re.sub(r'\bButton\(', 'KisanPrimaryButton(', content)
    
    # We only want to replace Card(
    content = re.sub(r'\bCard\(', 'KisanCard(', content)

    # Some OutlinedButton use colors = ButtonDefaults.outlinedButtonColors(). That's fine.
    
    with open(filepath, 'w') as f:
        f.write(content)

for root, dirs, files in os.walk('app/src/main/java/com/example/ui'):
    for file in files:
        if file.endswith('.kt'):
            process_file(os.path.join(root, file))

for root, dirs, files in os.walk('app/src/main/java/com/example/'):
    for file in files:
        if file == 'MainActivity.kt':
            process_file(os.path.join(root, file))

