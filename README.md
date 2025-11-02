# Account – Supabase Authentication with Kotlin & Jetpack Compose

This project demonstrates implementing user authentication in an Android application using Kotlin, Jetpack Compose, and Supabase. It covers email/password registration, login, GitHub OAuth, storing user profile data in Supabase, and displaying that data in the UI.

This project is a continuation of a previous work where I integrated Supabase as a database in Android. That project focused on database operations, while this one focuses on authentication and account management.

**Previous project (Supabase – Database Demo):**
[https://github.com/PrinceJoseph-software/Supabase](https://github.com/PrinceJoseph-software/Supabase)

---

## Features

* Login screen on startup
* Email and password authentication
* GitHub sign-in
* Sign-up form containing:

  * First name
  * Last name
  * Email
  * Phone number
  * Password
* Stores user profile data in Supabase
* Redirects to dashboard after sign-up or login
* Dashboard displays:

  * User’s name
  * Email
  * Phone number
* Dark mode support
* Logout button available at the top-right of the dashboard

---

## Tech Stack

| Tool / Library  | Purpose                         |
| --------------- | ------------------------------- |
| Kotlin          | Android development language    |
| Jetpack Compose | UI framework                    |
| Supabase        | Backend for auth and database   |
| Supabase Auth   | Email/password and GitHub OAuth |

---

## Supabase Configuration

Supabase credentials are not included in this repository.
`SUPABASE_URL` and `SUPABASE_KEY` are stored in `local.properties` and are ignored using `.gitignore`.

To run the project:

1. Create a project at [https://supabase.com](https://supabase.com)
2. Open the `local.properties` file in the root of the Android project
3. Add your credentials:

```
SUPABASE_URL=your-supabase-url
SUPABASE_KEY=your-anon-public-key
```

4. Sync and run the project

---

## App Flow

1. App launches to the Login screen
2. Users can:

   * Log in with email and password
   * Log in with GitHub
   * Navigate to Sign Up if they don’t have an account
3. Sign Up collects user details and stores them in Supabase
4. Successful login/sign-up redirects to the dashboard
5. Dashboard shows user information fetched from Supabase
6. Dark mode can be toggled
7. Logout is available from the top-right menu

---

## Related Project

This is a continuation of a previous project focused on Supabase as a database backend.

Supabase Database Project:
[https://github.com/PrinceJoseph-software/Supabase](https://github.com/PrinceJoseph-software/Supabase)

---

## Built by Joe

Prince Joseph
GitHub: [https://github.com/PrinceJoseph-software](https://github.com/PrinceJoseph-software)
