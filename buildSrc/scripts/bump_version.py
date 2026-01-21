import re
import os
import argparse

# Sciezka do pliku konfiguracyjnego wzgledem root projektu (uruchamiane z glownego katalogu w CI)
FILE_PATH = 'buildSrc/src/main/kotlin/ProjectConfig.kt'

def bump_version(bump_type):
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

    # 2. Znajdz i podbij VERSION_NAME
    # Szuka: const val VERSION_NAME = "1.0.3"
    name_pattern = r'(const\s+val\s+VERSION_NAME\s*=\s*")(\d+)\.(\d+)\.(\d+)(")'
    
    def replace_name(match):
        major = int(match.group(2))
        minor = int(match.group(3))
        patch = int(match.group(4))
        
        old_version = f"{major}.{minor}.{patch}"

        if bump_type == 'major':
            major += 1
            minor = 0
            patch = 0
        elif bump_type == 'minor':
            minor += 1
            patch = 0
        else: # patch
            patch += 1

        new_version = f"{major}.{minor}.{patch}"
        print(f"Bumping VERSION_NAME ({bump_type}): {old_version} -> {new_version}")
        return f"{match.group(1)}{new_version}{match.group(5)}"

    new_content = re.sub(name_pattern, replace_name, new_content)

    with open(FILE_PATH, 'w') as f:
        f.write(new_content)

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Bump Android version code and name.')
    parser.add_argument('bump_type', nargs='?', default='patch', choices=['major', 'minor', 'patch'],
                        help='Type of version bump: major, minor, or patch (default: patch)')
    args = parser.parse_args()
    
    bump_version(args.bump_type)
