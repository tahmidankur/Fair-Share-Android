# Fair Share

An Android app for roommates to keep track of chores and shared expenses.

## What it does

- Add roommates
- Create chores with a due date and a frequency (daily, weekly, bi-weekly, monthly or one-time)
- Mark a chore done and it rotates to the next roommate, with a history of who did what
- Log shared purchases and split them equally between whoever was involved
- Balance screen showing each individual debt plus a shorter list of payments that settles everyone up

Everything is stored on the device with Room (SQLite), so there's no account or internet needed.

## Built with

Java, XML layouts, Room, Material Components. Min SDK 24.

## Running it

Open the project in Android Studio and run the `app` configuration, or build from the command line:

```bash
./gradlew assembleDebug
```

## Ideas for later

- Reminders when a chore is due
- Marking individual debts as paid
- Sync between roommates' phones
