# EduTrack-App
EduTrack: a study progress tracker for students. Android app built with Kotlin, Jetpack Compose, Room and MVVM. IBM4203 coursework.

## Progress Status
- **Day 8 & Day 9 Completed**:
  - Add Subject form with color picker (8 tag colors) and weekly goal selector (- / +).
  - Field validation (Name required, <= 30 chars; Goal 1–168 hrs) with inline error messages.
  - Subject creation, editing, and deletion with cascading session deletion and confirmation dialog.
  - Snackbars for subject saved, updated, and deleted notifications.
  - Screen scrollability with IME keyboard padding and light/dark theme support with Compose previews.
- **Day 14 & Day 15 Completed**:
  - Session Detail screen displaying subject tag, duration, formatted date, and notes.
  - Session deletion with confirmation dialog returning safely to History.
  - Reusable Add/Edit Session screen and SessionViewModel supporting edit mode.
  - Updating existing study sessions in Room with "Session updated" Snackbar notification.
