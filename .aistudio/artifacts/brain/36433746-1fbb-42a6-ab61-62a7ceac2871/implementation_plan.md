# Aether Focus — Luminous Screen-Time Blocker & Star Economy

Aether Focus is a native Android focus companion built in Kotlin and Jetpack Compose. It transforms digital discipline from a punitive barrier into a rewarding celestial sanctuary. Users lock distracting apps during recurring schedules and can only unlock them early by spending stars earned through mindful, low phone usage.

---

## User Review & Critical Decisions

> [!IMPORTANT]
> The implementation incorporates the user-confirmed design decisions:
> - **Native Android Kotlin & Jetpack Compose:** Built natively for Android following modern Material 3 and custom glassmorphism design specifications.
> - **Interactive App Launcher Dock:** A dedicated testing affordance allows testing how opening any installed or blocked app behaves (triggering the full-screen Shield or opening freely).
> - **Floating Demo Time Machine:** A top-level collapsible demo HUD allows shifting simulated time (+15m, +1h, +8h, +1 day, jump to 11 PM [Restricted], jump to 11 AM [Free], trigger weekly payout) to verify interval and unlock logic immediately.

- **Confirmed Decision 1**: Native Kotlin + Jetpack Compose with custom glassmorphism shaders, ambient backdrops, and translucent panels.
- **Confirmed Decision 2**: Local state persistence using Android SharedPreferences with Moshi JSON serialization for reliable zero-backend state saving.
- **Confirmed Decision 3**: Complete implementation of all 9 screens, including all required empty, loading, and error states.

---

## 1. Overview & Core Concept

- **What It Does**: Enforces strict app-blocking windows (e.g., 10:00 PM – 8:00 AM) where opening restricted apps presents an unskippable full-screen Shield overlay. The only escape is paying an "Early Unlock Fee" of 5 stars, which are only awarded if phone usage stayed below strict limits.
- **Target Audience**: Individuals seeking deep focus, restorative evening downtime, and digital mindfulness through gamified friction and positive reinforcement.
- **Key Value**: Mindful friction mechanics—spending stars makes unlocking an intentional choice rather than an impulsive reflex.

---

## 2. User Experience & Visual Design

### Visual Identity & Theme
- **Movement**: **Refined Luminous Glassmorphism** with translucent frosted slabs, prismatic hairline specular borders (`#FFFFFF` with low alpha), and layered ambient backdrops (Iris Purple `#8B5CF6`, Amber Gold `#F59E0B`, Vibrant Emerald `#10B981`, Deep Obsidian Plum `#14121F`).
- **Color Palette**:
  - `Surface`: Deep void plum `#14121F`, container variants `#1C1A27`, `#201E2C`, `#2B2836`.
  - `Primary / Star Gold`: `#FFC174` / `#F59E0B` for celestial star balances, celebratory payouts, and unlock badges.
  - `Secondary / Iris Purple`: `#D0BCFF` / `#571BC1` for ambient halos, active tabs, and navigation highlights.
  - `Tertiary / Emerald`: `#56E5A9` / `#10B981` for active protection, verified permissions, and healthy progress.
  - `Error / Restrictive Red`: `#FFB4AB` / `#93000A` for blocked states and cost deductions.
- **Typography Pairing**:
  - Clean display font hierarchy with tabular timer readouts and uppercase letter-spaced eyebrow headers.
- **Bottom Navigation**:
  - Elevated frosted glass pill dock: Home, Screen Time, Shield (Blocked Apps), Wallet, Settings.

### 9 Core Screens & Flows
1. **Onboarding / System Permissions**:
   - Stacked glass cards for Usage Access, Notifications, Display Over Apps, Device Admin, Battery Exemption.
   - Interactive "Grant" triggers updating state to "Active" with an animated progress bar and milestone counter.
2. **Home Screen**:
   - Top bar: Stardust balance pill (`24 Stars`) with haptic glow.
   - Status Card: "RESTRICTED (Active)" vs "FREE (Inactive)", current interval `10:00 PM – 8:00 AM`, live countdown `7h 14m remaining`.
   - Screen Rhythm Card: 3 progress bars (Usage since last check, This week daily avg, Last week comparison).
   - Weekly Objective (5-Star Bounty Vault): Met/unmet conditions checklist (`< 1h since last check`, `< 10h weekly`, `lower than previous week`).
   - Quick Action: Emergency Snooze (spend stars for 15m grace).
3. **Screen Time Analysis**:
   - Segmented period selector (`Today`, `This week`, `Last week`).
   - Total screen time header, daily average, trend badge (`-28% vs last wk`).
   - 7-day frosted bar chart with goal benchmark line (`2h 30m`).
   - Per-app usage list with limits, progress tracks, and filter tabs (`All Apps`, `Blocked`).
   - Interactive preview switches for Live, Empty, and Loading states.
4. **Blocked Apps (Sanctuary Guard)**:
   - Search bar with clear button.
   - System Protected Core section: "Phone" and "Contacts" permanently locked as "Always allowed".
   - Configured Sanctuaries: Searchable list of installed apps (Instagram, TikTok, Reddit, Netflix, Amazon, etc.) with custom toggle switches.
   - Empty search and sync error state cards.
5. **Restriction Schedule**:
   - Master schedule toggle switch ("Schedule Active").
   - Interval window pickers: Downtime Starts and Ends with AM/PM toggles, +/- 15 min step buttons, and computed duration badge.
   - Active Days selector: M/T/W/T/F/S/S circular toggles with Quick Presets (Weekdays, Everyday, Weekends).
   - Guardrails: Repeat Weekly toggle, Strict Lock (Admin) toggle, Early Unlock Penalty toggle.
   - Save Schedule button with feedback animation.
6. **Blocked-App Shield (Full-Screen Overlay)**:
   - Full-screen blurred backdrop obscuring the simulated app view.
   - Cosmic shield emblem with pulsing aura and app identity badge ("Instagram is Shielded").
   - Primary headline: "This app is restricted until 8:00 AM".
   - Remaining interval countdown.
   - Primary button: "Unlock with 5 Stars".
   - Wallet balance indicator and "Close and stay focused" dismiss button.
7. **Unlock Confirmation Modal**:
   - Modal over dimmed blurred background.
   - App title and duration ("This will lift restrictions for 15 minutes").
   - Cost breakdown: Unlock Cost (-5 Stars), Current Balance (24 Stars), Remaining (19 Stars).
   - Primary action: "Confirm Unlock (Spend 5 Stars)".
   - Disabled state handling: When balance < 5, button becomes disabled showing "Not enough stars" with a prompt to earn more.
8. **Star Wallet & Ledger**:
   - Hero balance card ("24 Stars Available", "Equivalent to 4 early unlock passes").
   - 7-day star harvest visualizer bar graph.
   - Activity filter chips: All Activity, Earned (+), Spent (-).
   - Ledger list showing transaction source, star delta (`+5 ★`, `-5 ★`), and timestamp.
   - Toggleable empty ledger state.
9. **Settings & Safeguards**:
   - User profile & tier card ("Focus Guardian Level 4", lifetime stars).
   - System permissions status summary with deep-link triggers.
   - Default schedule summary entry.
   - Early unlock fee rule card (read-only 5 stars, streak locked).
   - Emergency unlock pass replenishment countdown.
   - Critical Actions: Reset Star Balance (with confirmation dialog), Clear Restriction Rules, and Export Focus Data (CSV simulation).

---

## 3. Key Product Decisions & Trade-Offs

- **Client-Side Persistence via SharedPreferences & Moshi**:
  - *Chosen*: Encrypted / standard SharedPreferences with Moshi JSON adapter storing app configurations, wallet balances, transaction logs, and mock telemetry.
  - *Why*: Instant startup, zero network dependencies, resilient offline performance, and matches the user's specification of "no backend, local state persistence".
- **Interactive App Simulation & Dock**:
  - *Chosen*: A floating "App Launcher" drawer/dock allowing the user to tap any installed app (Instagram, TikTok, Phone, etc.).
  - *Why*: In a sandbox Android emulator, users cannot easily switch to real installed Instagram/TikTok; having an embedded App Simulator provides an immediate way to test the Shield overlay and early unlock flow.
- **Time Machine (Demo Clock HUD)**:
  - *Chosen*: A sleek, collapsible HUD pill at the top of the screen that shifts simulated time forward or back, toggles into the 10:00 PM – 8:00 AM window, and triggers the weekly payout engine.

---

## 4. Technical Architecture & Data Strategy

```
┌────────────────────────────────────────────────────────┐
│                   Aether Focus UI                      │
│                                                        │
│  ┌─────────────────┐  ┌─────────────────────────────┐  │
│  │ Floating Clock  │  │   Interactive App Launcher  │  │
│  │ Demo HUD        │  │   Simulator Dock            │  │
│  └────────┬────────┘  └──────────────┬──────────────┘  │
│           │                          │                 │
│  ┌────────▼──────────────────────────▼──────────────┐  │
│  │                  Active Screen                   │  │
│  │ (Home | Screen Time | Blocked | Wallet | Settings)│ │
│  └────────────────────────┬─────────────────────────┘  │
│                           │                            │
│  ┌────────────────────────▼─────────────────────────┐  │
│  │          Blocked-App Shield & Unlock Modal       │  │
│  └────────────────────────┬─────────────────────────┘  │
└───────────────────────────┼────────────────────────────┘
                            │
┌───────────────────────────▼────────────────────────────┐
│                    FocusViewModel                      │
│  - isWithinInterval(now, schedule)                     │
│  - evaluateWeeklyPayout(usage, wallet)                 │
│  - unlockAppWithStars(appId)                           │
│  - toggleBlockApp(appId)                               │
│  - grantPermission(permKey)                            │
└───────────────────────────┬────────────────────────────┘
                            │
┌───────────────────────────▼────────────────────────────┐
│               FocusRepository & DataStore              │
│  - SharedPreferences (Moshi JSON serialized state)     │
│  - AppList, ScheduleState, WalletState, LedgerList     │
└────────────────────────────────────────────────────────┘
```

### Core Entities & Data Model
- `AppItem`: `id`, `name`, `category`, `avgDailyUsage`, `blocked`, `isSystemProtected` ("Phone", "Contacts"), `unlockedUntilMillis`.
- `ScheduleConfig`: `enabled`, `startHour`, `startMinute`, `startPeriod`, `endHour`, `endMinute`, `endPeriod`, `activeDays`, `repeatWeekly`, `strictLock`, `penaltyEnabled`.
- `WalletState`: `balance`, `lifetimeStars`, `ledger: List<LedgerItem>`.
- `LedgerItem`: `id`, `title`, `source`, `stars` (positive or negative), `timestamp`, `note`.
- `ScreenTimeData`: `usageSinceLastCheckMinutes`, `todayMinutes`, `thisWeekDailyAvgMinutes`, `lastWeekAvgMinutes`, `dailyUsageHistory`.
- `PayoutRules`: `maxUsageSinceLastCheck = 60m`, `maxWeeklyUsage = 600m`, `requireLowerThanPreviousWeek = true`.

---

## Verification Plan

### Automated & Build Verification
- Execute `compile_applet` to ensure Kotlin and Compose compilation succeeds with zero errors.
- Validate that all dependencies in `app/build.gradle.kts` match without missing symbols.

### Manual & Interactive User Verification
1. **Schedule & Interval Evaluation**:
   - Use the Demo Clock to move the time to 11:00 PM (inside 10 PM – 8 AM interval).
   - Verify Home screen displays "RESTRICTED (Active)".
2. **Shield Overlay & Early Unlock**:
   - Open the simulated Instagram app via the App Launcher.
   - Confirm full-screen Shield appears: "This app is restricted until 8:00 AM".
   - Tap "Unlock with 5 Stars". Verify confirmation modal shows cost breakdown.
   - Confirm unlock: 5 stars deducted, ledger updated in Wallet, Instagram unlocked for 15 minutes.
3. **Star Balance & Insufficient Stars**:
   - Repeatedly unlock or reset stars to < 5 in Settings.
   - Trigger shield again: Verify button changes to disabled "Not enough stars" and prevents unlock.
4. **Permissions Onboarding**:
   - Tap "Grant" on pending permissions; verify progress bar increases and status updates to "Active".
5. **Screen Time & State Switchers**:
   - Switch between Today / This week / Last week.
   - Test "Live", "Empty", and "Loading" view toggles on Screen Time and Blocked Apps screens.
