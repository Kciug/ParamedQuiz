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

    # 1. Znajdz aktualna wersje i kod
    name_pattern = r'const\s+val\s+VERSION_NAME\s*=\s*"(\d+)\.(\d+)\.(\d+)"'
    code_pattern = r'const\s+val\s+VERSION_CODE\s*=\s*(\d+)'
    staging_pattern = r'const\s+val\s+STAGING_NUMBER\s*=\s*(\d+)'

    name_match = re.search(name_pattern, content)
    code_match = re.search(code_pattern, content)
    staging_match = re.search(staging_pattern, content)

    if not name_match or not code_match or not staging_match:
        print("Error: Could not find VERSION_NAME, VERSION_CODE or STAGING_NUMBER in ProjectConfig.kt")
        exit(1)

    major = int(name_match.group(1))
    minor = int(name_match.group(2))
    patch = int(name_match.group(3))
    old_version = f"{major}.{minor}.{patch}"

    old_code = int(code_match.group(1))
    old_staging = int(staging_match.group(1))
    new_staging = old_staging

    # 2. Oblicz nowa wersje
    if bump_type == 'major':
        major += 1
        minor = 0
        patch = 0
        new_staging = 0  # reset licznika stagingu po realnym release
    elif bump_type == 'minor':
        minor += 1
        patch = 0
        new_staging = 0
    elif bump_type == 'patch':
        patch += 1
        new_staging = 0
    elif bump_type == 'staging':
        # VERSION_NAME bez zmian; podbijamy tylko numer stagingu (sufiks -stagingXXX)
        new_staging = old_staging + 1
    else: # none
        pass

    new_version = f"{major}.{minor}.{patch}"
    # VERSION_CODE zawsze rosnie o 1 (wspoldzielony, globalnie unikalny licznik dla Play)
    new_code = old_code + 1

    print(f"Bumping version ({bump_type}):")
    print(f"  Name: {old_version} -> {new_version}")
    print(f"  Code: {old_code} -> {new_code}")
    print(f"  Staging: {old_staging} -> {new_staging}")

    # 3. Aktualizuj plik
    new_content = re.sub(r'(const\s+val\s+VERSION_CODE\s*=\s*)\d+', r'\g<1>' + str(new_code), content)
    new_content = re.sub(r'(const\s+val\s+VERSION_NAME\s*=\s*")\d+\.\d+\.\d+(")', r'\g<1>' + new_version + r'\g<2>', new_content)
    new_content = re.sub(r'(const\s+val\s+STAGING_NUMBER\s*=\s*)\d+', r'\g<1>' + str(new_staging), new_content)

    with open(FILE_PATH, 'w') as f:
        f.write(new_content)

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Bump Android version code and name.')
    parser.add_argument('bump_type', nargs='?', default='patch', choices=['major', 'minor', 'patch', 'staging', 'none'],
                        help='Type of version bump: major, minor, patch, staging, or none (default: patch)')
    args = parser.parse_args()

    bump_version(args.bump_type)
