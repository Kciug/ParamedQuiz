import re
import os

# Sciezka do pliku konfiguracyjnego wzgledem root projektu (uruchamiane z glownego katalogu w CI)
FILE_PATH = 'buildSrc/src/main/kotlin/ProjectConfig.kt'

def bump_version():
    if not os.path.exists(FILE_PATH):
        print(f"Error: File not found at {FILE_PATH}")
        exit(1)

    with open(FILE_PATH, 'r') as f:
        content = f.read()

    # 1. Znajdz i podbij VERSION_CODE
    # Szuka: const val VERSION_CODE = 10003
    code_pattern = r'(const\s+val\s+VERSION_CODE\s*=\s*)(\d+)'
    
    def replace_code(match):
        current_code = int(match.group(2))
        new_code = current_code + 1
        print(f"Bumping VERSION_CODE: {current_code} -> {new_code}")
        return f"{match.group(1)}{new_code}"
    
    new_content = re.sub(code_pattern, replace_code, content)

    # 2. Znajdz i podbij VERSION_NAME (tylko Patch: 1.0.3 -> 1.0.4)
    # Szuka: const val VERSION_NAME = "1.0.3"
    name_pattern = r'(const\s+val\s+VERSION_NAME\s*=\s*")(\d+\.\d+\.)(\d+)(")'
    
    def replace_name(match):
        prefix = match.group(2) # np. "1.0."
        patch = int(match.group(3)) # np. 3
        new_patch = patch + 1
        print(f"Bumping VERSION_NAME: {prefix}{patch} -> {prefix}{new_patch}")
        return f"{match.group(1)}{prefix}{new_patch}{match.group(4)}"

    new_content = re.sub(name_pattern, replace_name, new_content)

    with open(FILE_PATH, 'w') as f:
        f.write(new_content)

if __name__ == "__main__":
    bump_version()
