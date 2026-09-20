# Technician Portal (Jetpack Compose)

Six screens from the mockups: **Home, Cases, Live map, Alerts, Profile, Case details**.

## Setup

1. Create a new Android Studio project ("Empty Activity", Compose) with package `com.awash.technician`.
2. Copy `app/src/main/java/...` over the generated sources, and merge `AndroidManifest.xml` and the dependencies from `app/build.gradle.kts`.
3. (Optional) Add the **Outfit** font to `res/font/` and update `AppFont` in `ui/theme/Theme.kt`. It uses the system sans-serif until you do.

## Structure

```
data/        Models.kt (Case, CaseStatus, StatusEvent...), SampleData.kt
location/    LocationTracker.kt   FusedLocationProvider wrapper
ui/          TechnicianViewModel.kt, TechnicianApp.kt (nav + scaffold)
ui/theme/    Theme.kt             colors (light + dark), typography
ui/components/ Components.kt, CaseCard.kt, RouteMapCard.kt
ui/screens/  Home, Cases, Map, Alerts, Profile, CaseDetails
util/        Format.kt, Intents.kt
```

## How location is recorded on every status change

`TechnicianViewModel.advanceCase()` is the only place a case changes status:

1. Requests a fresh high-accuracy fix (`LocationTracker.currentFix()`).
2. For `ARRIVED` and `COMPLETED` it refuses to continue without a fix, and shows a snackbar.
   Change `LOCATION_REQUIRED` in the ViewModel to require it for other steps.
3. Appends a `StatusEvent(status, timestamp, latitude, longitude, accuracy)` to the case history.
4. The case details timeline shows the time and coordinates of every step.

Closing a case shows a confirmation dialog first ("your location will be saved").

## What you still need to wire up

- **Backend**: `SampleData` is placeholder. Replace it with a repository (Retrofit + Room). Look for the `TODO` comments in `TechnicianViewModel` where events and location fixes should be sent. Commit locally only after the server accepts the event, or queue with WorkManager for offline use.
- **Continuous tracking**: `LocationTracker.updates()` only runs while the app is open. To let supervisors see technicians all day, add a foreground service (`foregroundServiceType="location"`) and `ACCESS_BACKGROUND_LOCATION`. The commented permissions in the manifest are for that.
- **Real map**: `RouteMapCard` is a stylised drawing (works offline, no API key). To use real tiles, add `maps-compose` and put a `GoogleMap { }` in place of the `Canvas`, keeping the text overlays.
- **Distance / ETA**: straight-line distance and a fixed 18 km/h estimate. Use Google Directions or Routes API for real numbers.
- **Push notifications**: the Alerts screen reads a local list. Hook it to FCM.
