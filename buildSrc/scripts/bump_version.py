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

    # 1. Znajdz aktualna wersje
    name_pattern = r'const\s+val\s+VERSION_NAME\s*=\s*"(\d+)\.(\d+)\.(\d+)"'
    match = re.search(name_pattern, content)
    if not match:
        print("Error: Could not find VERSION_NAME in ProjectConfig.kt")
        exit(1)

    major = int(match.group(1))
    minor = int(match.group(2))
    patch = int(match.group(3))
    old_version = f"{major}.{minor}.{patch}"

    # 2. Oblicz nowa wersje
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
    new_code = major * 10000 + minor * 100 + patch
    
    print(f"Bumping version ({bump_type}):")
    print(f"  Name: {old_version} -> {new_version}")
    print(f"  Code: {new_code}")

    # 3. Aktualizuj plik
    new_content = re.sub(r'(const\s+val\s+VERSION_CODE\s*=\s*)\d+', r'\g<1>' + str(new_code), content)
    new_content = re.sub(r'(const\s+val\s+VERSION_NAME\s*=\s*")\d+\.\d+\.\d+(")', r'\g<1>' + new_version + r'\g<2>', new_content)

    with open(FILE_PATH, 'w') as f:
        f.write(new_content)

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Bump Android version code and name.')
    parser.add_argument('bump_type', nargs='?', default='patch', choices=['major', 'minor', 'patch'],
                        help='Type of version bump: major, minor, or patch (default: patch)')
    args = parser.parse_args()
    
    bump_version(args.bump_type)
