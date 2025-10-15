# Weight EAN Scanner (Android TSD)

- Single-purpose app: reads weighted EAN‑13 barcodes from TSDs.
- Supports Sunmi broadcast intents and keyboard wedge mode.
- Parses prefixes 20–29, layout PP AAAAA BBBBB C. Shows PLU and weight (kg).

## Build
1) Open in Android Studio (Giraffe+).
2) Sync Gradle, build `app` -> `assembleDebug`.
3) Install APK on the device.

## Sunmi
On Sunmi L2s Pro, set scan output to **Broadcast** or **API**, which sends:
- `com.sunmi.scanner.ACTION_DATA_CODE_RECEIVED` or `com.sunmi.intent.action.SCAN_RESULT`
with extras: `data` / `barcode_string` / `scan_data`.

If your TSD works as a keyboard, focus the input and scan; press Enter if needed.
