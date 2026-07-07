#!/bin/bash
set -e

# Install and launch Calculator app on emulator
ANDROID_HOME=${ANDROID_HOME:-/opt/homebrew/share/android-commandlinetools}
PACKAGE_NAME="com.example.calculator_v01"
APP_ACTIVITY="com.example.calculator_v01.MainActivity"

show_help() {
    cat << EOF
Build, install, and launch Calculator app on emulator

Usage: ./scripts/install-and-launch.sh [OPTION]

Options:
  -h, --help        Show this help message
  -d, --device ID   Device serial number (uses adb -s flag)
  --debug           Build debug APK (default)
  --release         Build release APK

Examples:
  ./scripts/install-and-launch.sh              # Build debug and install on connected device
  ./scripts/install-and-launch.sh --device emulator-5554  # Target specific device
  ./scripts/install-and-launch.sh --release    # Build release version

EOF
}

# Parse arguments
BUILD_TYPE="debug"
ADB_DEVICE=""

while [[ $# -gt 0 ]]; do
    case "$1" in
        -h|--help)
            show_help
            exit 0
            ;;
        -d|--device)
            ADB_DEVICE="-s $2"
            shift 2
            ;;
        --debug)
            BUILD_TYPE="debug"
            shift
            ;;
        --release)
            BUILD_TYPE="release"
            shift
            ;;
        *)
            echo "Unknown option: $1"
            show_help
            exit 1
            ;;
    esac
done

echo "Building and installing Calculator app..."
echo "  Build type: $BUILD_TYPE"
echo "  Package: $PACKAGE_NAME"
echo ""

# Check if adb is available
if ! command -v adb &> /dev/null; then
    export PATH="$ANDROID_HOME/platform-tools:$PATH"
fi

# Check if device is connected
echo "Checking for connected devices..."
if ! adb $ADB_DEVICE devices | grep -q "device$"; then
    echo "✗ No device found!"
    echo "Please ensure the emulator is running or a device is connected."
    echo "To start the emulator, run: ./scripts/setup-emulator.sh"
    exit 1
fi

if [ -n "$ADB_DEVICE" ]; then
    echo "✓ Device $ADB_DEVICE found"
else
    echo "✓ Device found"
fi
echo ""

# Build APK
echo "Building APK (${BUILD_TYPE})..."
cd "$(dirname "$0")/.."
BUILD_TYPE_CAP="$(tr '[:lower:]' '[:upper:]' <<< "${BUILD_TYPE:0:1}")${BUILD_TYPE:1}"
./gradlew "assemble${BUILD_TYPE_CAP}" > /dev/null 2>&1
echo "✓ Build complete"

# Find the APK
APK_PATH="app/build/outputs/apk/${BUILD_TYPE}/app-${BUILD_TYPE}.apk"
if [ ! -f "$APK_PATH" ]; then
    echo "✗ APK not found at $APK_PATH"
    exit 1
fi
echo "✓ APK located: $APK_PATH"
echo ""

# Uninstall existing app (ignore errors if not installed)
echo "Uninstalling existing app..."
adb $ADB_DEVICE uninstall "$PACKAGE_NAME" > /dev/null 2>&1 || true

# Install APK
echo "Installing APK..."
adb $ADB_DEVICE install "$APK_PATH" > /dev/null 2>&1
echo "✓ Installation complete"
echo ""

# Launch app
echo "Launching Calculator app..."
adb $ADB_DEVICE shell am start -n "$PACKAGE_NAME/$APP_ACTIVITY"
echo "✓ App launched!"
