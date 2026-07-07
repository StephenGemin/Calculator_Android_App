#!/bin/bash
set -e

# Setup Android emulator for Calculator app
ANDROID_HOME=${ANDROID_HOME:-/opt/homebrew/share/android-commandlinetools}
API_LEVEL=35

# Help function
show_help() {
    cat << EOF
Setup Android emulator for Calculator app

Usage: ./scripts/setup-emulator.sh [OPTION] [DEVICE]

Options:
  -h, --help       Show this help message
  -l, --list       List available device models
  DEVICE           Device model name or ID number (default: pixel_2)

Examples:
  ./scripts/setup-emulator.sh              # Boot emulator with pixel_2 (default)
  ./scripts/setup-emulator.sh pixel_5      # Boot emulator with pixel_5 (by name)
  ./scripts/setup-emulator.sh 27           # Boot emulator with device ID 27 (pixel_2)
  ./scripts/setup-emulator.sh --list       # Show available devices with IDs
  ./scripts/setup-emulator.sh -h           # Show this help

Available devices:
  Run with --list to see all available device models for your SDK.

Environment:
  ANDROID_HOME  Android SDK root (default: /opt/homebrew/share/android-commandlinetools)

EOF
}

# Parse arguments
case "${1:-}" in
    -h|--help)
        show_help
        exit 0
        ;;
    -l|--list)
        # List available devices
        if [ ! -d "$ANDROID_HOME" ]; then
            echo "Error: ANDROID_HOME not found at $ANDROID_HOME"
            exit 1
        fi
        export PATH="$ANDROID_HOME/platform-tools:$PATH"
        "$ANDROID_HOME/cmdline-tools/latest/bin/avdmanager" list device
        exit 0
        ;;
    "")
        DEVICE="pixel_2"
        ;;
    *)
        # Check if argument is a number (device ID) or string (device name)
        if [[ "$1" =~ ^[0-9]+$ ]]; then
            # It's a device ID, look up the actual device name
            if [ ! -d "$ANDROID_HOME" ]; then
                echo "Error: ANDROID_HOME not found at $ANDROID_HOME"
                exit 1
            fi
            export PATH="$ANDROID_HOME/platform-tools:$PATH"
            DEVICE_LIST=$("$ANDROID_HOME/cmdline-tools/latest/bin/avdmanager" list device 2>/dev/null)
            DEVICE=$(echo "$DEVICE_LIST" | grep "^id: $1 or" | sed 's/.*"\([^"]*\)".*/\1/')
            if [ -z "$DEVICE" ]; then
                echo "Error: Device ID $1 not found"
                echo "Run with --list to see available devices"
                exit 1
            fi
        else
            # It's a device name, use as-is
            DEVICE="$1"
        fi
        ;;
esac

AVD_NAME="calc_${DEVICE}"

echo "Setting up Android emulator..."
echo "  Device: $DEVICE"
echo "  API Level: $API_LEVEL"
echo "  AVD Name: $AVD_NAME"
echo "  ANDROID_HOME: $ANDROID_HOME"
echo ""

# Check if ANDROID_HOME is valid
if [ ! -d "$ANDROID_HOME" ]; then
    echo "Error: ANDROID_HOME not found at $ANDROID_HOME"
    echo "Please install Android SDK tools or set ANDROID_HOME correctly."
    exit 1
fi

# Add tools to PATH for this script
export PATH="$ANDROID_HOME/emulator:$ANDROID_HOME/platform-tools:$PATH"

# Create .android directory if needed
mkdir -p ~/.android/avd

# Check if AVD already exists
if [ -d "$HOME/.android/avd/$AVD_NAME" ]; then
    echo "✓ AVD '$AVD_NAME' already exists. Skipping creation."
else
    echo "Creating AVD '$AVD_NAME'..."
    "$ANDROID_HOME/cmdline-tools/latest/bin/avdmanager" create avd \
        -n "$AVD_NAME" \
        -k "system-images;android-${API_LEVEL};default;arm64-v8a" \
        -d "$DEVICE" \
        --force \
        -p "$HOME/.android/avd/$AVD_NAME" > /dev/null 2>&1

    # Ensure ini file exists
    if [ ! -f "$HOME/.android/avd/${AVD_NAME}.ini" ]; then
        cat > "$HOME/.android/avd/${AVD_NAME}.ini" << EOF
path=$HOME/.android/avd/$AVD_NAME
path.rel=avd/$AVD_NAME
target=android-$API_LEVEL
EOF
    fi
    echo "✓ AVD '$AVD_NAME' created."
fi

# Kill any existing emulator processes
pkill -f "qemu-system.*headless" 2>/dev/null || true
sleep 1

# Boot the emulator
echo ""
echo "Booting emulator '$AVD_NAME'..."
emulator -avd "$AVD_NAME" -no-audio -no-boot-anim -no-snapshot > /tmp/emulator_${AVD_NAME}.log 2>&1 &
EMULATOR_PID=$!

# Wait for adb to recognize the device
echo "Waiting for emulator to boot..."
for i in {1..60}; do
    if adb devices | grep -q "emulator.*device"; then
        echo "✓ Emulator ready!"
        adb devices
        exit 0
    fi
    sleep 1
done

echo "✗ Emulator failed to boot within 60 seconds."
echo "Check the log: tail -f /tmp/emulator_${AVD_NAME}.log"
exit 1
