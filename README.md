# Aether Focus — Production-Ready Android Focus & Screen-Time Blocker

Aether Focus is a native Android application built in Kotlin and Jetpack Compose featuring a luminous glassmorphism interface, real-time app restriction enforcement, daily habit tracking, and user-adjustable screen-time telemetry.

---

## 1. Executive Summary & Production Transformation

The codebase has been refactored from a prototype into a production-grade application:
- **Demo-Only Code Removed**: The floating demo time machine HUD, manual clock shifting, and simulated test containers have been completely removed. The app now syncs directly with the Android device's hardware clock (`System.currentTimeMillis()`) and real system telemetry.
- **Real App Interception & Launch**: Applications unlocked or unrestricted launch their genuine device counterparts via `PackageManager.getLaunchIntentForPackage()`. When restricted, the full-screen glassmorphic Shield Overlay is displayed.
- **Privacy First Guarantee**: 100% on-device operation with zero remote telemetry or user tracking.

---

## 2. Privacy Policy & Zero-Data Collection

### Explicit Privacy Statement
> *“This app does not collect any personal information from the device. It only requests permission to block other apps and does not store or transmit user data.”*

- **In-App Policy Page**: Accessible from `Settings & Safeguards` -> `Privacy Policy`, featuring high-contrast glassmorphism cards and full transparency disclosures.
- **Offline HTML Asset**: Bundled at `app/src/main/assets/privacy_policy.html` and referenced via `@string/privacy_statement` for Play Policy compliance.
- **Local Storage**: All settings, streaks, habits, and star balances reside strictly in encrypted on-device SharedPreferences.

---

## 3. Required Permissions Architecture

The application implements real Android system permission requests with live verification:

1. **Device Administrator (`android.permission.BIND_DEVICE_ADMIN`)**:
   - Receiver: `FocusDeviceAdminReceiver`
   - Purpose: Prevents hasty uninstallation or circumventing active lock schedules during deep work sessions.
   - Activation: Prompts via `DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN`.

2. **Accessibility Service (`android.permission.BIND_ACCESSIBILITY_SERVICE`)**:
   - Service: `FocusAccessibilityService`
   - Purpose: Monitors `AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED` to detect foreground launches of blocked packages in real-time. If restricted, immediately brings `MainActivity` to the foreground with `ACTION_SHOW_SHIELD`.
   - Activation: Launches `Settings.ACTION_ACCESSIBILITY_SETTINGS`.

3. **Screen-Time Analysis (`android.permission.PACKAGE_USAGE_STATS`)**:
   - Purpose: Reads local foreground application usage durations via `UsageStatsManager` to evaluate daily and weekly usage trends against your target ceiling.
   - Activation: Launches `Settings.ACTION_USAGE_ACCESS_SETTINGS`.

4. **Display Over Other Apps (`android.permission.SYSTEM_ALERT_WINDOW`)**:
   - Purpose: Allows rendering the restriction shield directly over targeted applications.
   - Activation: Launches `Settings.ACTION_MANAGE_OVERLAY_PERMISSION`.

5. **Battery Optimization Exemption (`android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS`)**:
   - Purpose: Protects background interval checks and downtime alarms from being terminated by aggressive device power managers.
   - Activation: Launches `Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS`.

6. **Notifications (`android.permission.POST_NOTIFICATIONS`)**:
   - Purpose: Alerts users prior to scheduled sanctuary downtime windows and announces milestone star awards.

---

## 4. Habit Tracker Feature

Aether Focus includes a daily focus habit tracker integrated with the Star Economy:
- **Interactive Checkoffs**: Check off daily mindfulness disciplines directly on the Home Screen or dedicated `Habit Tracker` tab.
- **Star Rewards & Streaks**: Completing a habit increments its personal streak and deposits stars (+1 to +5★) into the Star Wallet.
- **Custom Habit Creation**: Users can create custom habits with custom titles, categories (*Mindfulness, Health, Focus, Sleep*), and customizable star rewards.

---

## 5. Screen-Time Analysis (User-Adjustable Target)

- **Default Benchmark**: Configured to **40 hours 22 minutes** (2,422 minutes total weekly ceiling, averaging ~5h 46m / day).
- **User-Adjustable**: Tap **Edit Goal** in `Screen Time` or `Settings` to adjust target hours and minutes.
- **Telemetry Comparison**:
  - Compares actual recorded weekly usage vs. the custom target benchmark.
  - Computes percentage of ceiling consumed, remaining allowance, and daily average.
  - Renders a 7-day frosted bar chart with baseline ceiling indicators.

---

## 6. Glassmorphism UI Design System

- **Background Drawables**: Includes `res/drawable/glass_background.xml` (gradient mesh) and `res/drawable/glass_card.xml` (frosted stroke and rounded corners).
- **Jetpack Compose Glass Hierarchy**:
  - `AmbientGlassBackdrop`: Radial glowing ambient lights in Iris Purple, Star Gold, and Emerald.
  - `GlassCard`: Frosted translucent panels (`SurfaceContainer.copy(alpha = 0.75f)` to `0.9f`) with subtle borders (`GlassBorderDefault`).
  - Elevated floating glass navigation pill dock and full-screen frosted Shield Overlay.

---

## 7. How to Build & Test

### Building the Project
```bash
# Compile and build the debug APK
gradle assembleDebug

# Run unit tests
gradle :app:testDebugUnitTest
```

### Testing Key Features

1. **Permissions Flow**:
   - Navigate to `Settings` -> `Manage System Permissions` (or the initial setup flow).
   - Tap **Grant** on any permission card (Usage Access, Accessibility, Device Admin, Overlay, Battery).
   - Observe the system settings page open. Upon granting and returning to the app, the status chip automatically updates to **Granted** / **Active**.

2. **App Blocking & Shield Overlay**:
   - Navigate to the `Blocker` tab and toggle an app (e.g., YouTube or Instagram) to **Blocked**.
   - Navigate to `Settings` -> `Restriction Schedule` and set the schedule to cover the current time.
   - Tap the app icon in the Quick App Dock or open the app on your device (with Accessibility Service enabled).
   - The full-screen **Shield Overlay** appears immediately with *"This app is restricted until..."* and an **Unlock with Stars** option.

3. **Early Unlock Mechanics**:
   - On the Shield Overlay, tap **Unlock with Stars**.
   - Review the confirmation modal showing the cost (5 Stars) and current balance.
   - Confirming deducts 5 stars from your ledger and grants a 15-minute grace period pass.

4. **Screen-Time Benchmark Modification**:
   - Go to `Screen Time` tab and tap **Edit Goal**.
   - Change the hours or minutes from the default 40h 22m and tap **Apply Benchmark**.
   - Observe all ceiling progress bars, remaining allowances, and daily threshold lines recalculate dynamically.

5. **Privacy Policy**:
   - Go to `Settings` -> tap **Privacy Policy**.
   - Verify the exact required statement is displayed in the highlighted emerald card:
     *“This app does not collect any personal information from the device. It only requests permission to block other apps and does not store or transmit user data.”*

6. **Google OAuth & Supabase Cloud Persistence**:
   - Navigate to `Settings` -> view the **Account & Cloud Sync** card.
   - Tap **Sign in with Google (Firebase Auth)** to authenticate via Android Jetpack Credential Manager.
   - Once signed in, your user identity is displayed with a verified status and your data is automatically synchronized to Supabase.
   - Tap **Sync Now** to trigger an on-demand sync of habits, wallet balance, and screen-time targets.
   - When offline or before Supabase credentials are configured, the app operates in offline-first mode, preserving all records in local persistent storage.

---

## 8. Google OAuth & Supabase Backend Architecture

### Overview
The app integrates **Google OAuth** using the **Firebase Auth** dependency and **Android Credential Manager**, linking user sessions to a **Supabase PostgREST** backend for cross-device persistence.

### Supabase Table Schemas (SQL)
Run the following SQL in your Supabase SQL Editor to initialize the tables:

```sql
-- Habits table
create table public.habits (
    id text primary key,
    user_id text not null,
    title text not null,
    category text not null,
    completed_today boolean default false,
    current_streak integer default 0,
    star_reward integer default 2,
    icon_key text default 'spa',
    updated_at timestamp with time zone default timezone('utc'::text, now()) not null
);

-- Screen Time Targets table
create table public.screen_time_targets (
    user_id text primary key,
    target_hours integer not null default 40,
    target_minutes integer not null default 22,
    updated_at timestamp with time zone default timezone('utc'::text, now()) not null
);

-- Wallets table
create table public.wallets (
    user_id text primary key,
    balance integer default 24,
    lifetime_stars integer default 142,
    updated_at timestamp with time zone default timezone('utc'::text, now()) not null
);

-- Enable Row Level Security (RLS)
alter table public.habits enable row level security;
alter table public.screen_time_targets enable row level security;
alter table public.wallets enable row level security;
```

### Environment Configuration (.env)
Configure the following keys in the **Secrets panel in AI Studio** or in `.env`:

```properties
SUPABASE_URL=https://your-project.supabase.co
SUPABASE_ANON_KEY=your-supabase-anon-key
GOOGLE_WEB_CLIENT_ID=your-google-web-client-id.apps.googleusercontent.com
```
